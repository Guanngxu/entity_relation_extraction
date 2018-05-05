package com.entityrelation.utils;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.corpus.dependency.CoNll.CoNLLSentence;
import com.hankcs.hanlp.corpus.dependency.CoNll.CoNLLWord;
import com.hankcs.hanlp.dependency.MaxEntDependencyParser;
import com.hankcs.hanlp.model.perceptron.PerceptronLexicalAnalyzer;

import java.io.IOException;
import java.util.*;

/**
 * Created by 刘绪光 on 2018/4/18.
 */
public class RelationUtil {

    static Map<String, Integer> entityMap = new HashMap<>();

    /**
     * @param text 待分析的句子
     * @return 分析结果，按分词结果的顺序组织的
     */
    private static List<CoNLLWord> parser(String text){

        CoNLLSentence sentence = HanLP.parseDependency(text);

        CoNLLWord wordArr[] = sentence.getWordArray();

        List<CoNLLWord> result = new ArrayList<>();

        for (int i = 0; i < wordArr.length; i++) {
            result.add(wordArr[i]);
        }

        return result;
    }


    /**
     * 获取所有关系，里面有冗余，需要下一步的筛选
     * @param parser 句法依存分析
     * @param dict 词语依存字典
     */
    private static Set<String> relation(List<CoNLLWord> parser,
                                List<Map<String, List<CoNLLWord>>> dict){

        Set<String> result = new HashSet<>();

        for (int i = 0; i < parser.size(); i++) {

            // 动词表达的关系三元组
            Set<String> relationList = extract(parser, dict, i);

            // 命名实体三元组
            Set<String> namedList = namedTriad(parser, dict, i);

            result.addAll(relationList);
            result.addAll(namedList);

        }

        return result;
    }


    /**
     * 抽取命名实体有关的三元组
     * @param parser 句法分析
     * @param dict  词语依存字典
     * @param i 词语的索引
     * @return 关系三元组列表
     */
    private static Set<String> namedTriad(List<CoNLLWord> parser,
                                             List<Map<String, List<CoNLLWord>>> dict,
                                             int i){

        Set<String> result = new HashSet<>();

        // 词法分析器
        PerceptronLexicalAnalyzer analyzer = null;
        try {
            // 使用默认模型
            analyzer = new PerceptronLexicalAnalyzer();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 构造分词数组，因为句法分析已经包含了分词信息，直接使用
        String word[] = new String[parser.size()];

        for (int j = 0; j < word.length; j++) {
            word[j] = parser.get(j).LEMMA;
        }

        //词性标注
        String pos[] = analyzer.getPOSTagger().tag(word);

        // 命名实体识别结果
        String named[] = analyzer.namedEntityRecognize(word, pos);

        /*System.out.println(dict);
        System.out.println(Arrays.asList(word));
        System.out.println(Arrays.asList(pos));
        System.out.println(Arrays.asList(named));
        */

        if (named[i].charAt(0) == 'S' || named[i].charAt(0) == 'B'){
            int index = i;
            String entity1 = "";
            if (named[index].charAt(0) == 'B'){
                while (named[index].charAt(0) != 'E'){
                    entity1 += word[index];
                    index++;
                }
            }else {
                entity1 = word[index];
            }

            if (parser.get(index).DEPREL.equals("定中关系") &&
                    pos[parser.get(index).HEAD.ID-1].equals("n") &&
                    named[parser.get(index).HEAD.ID-1].equals("O")){

                /*System.out.println(parser.get(index).LEMMA);

                int deprel = parser.get(index).HEAD.ID-1;

                System.out.println(parser.get(deprel).LEMMA);
                System.out.println(dict.get(deprel));*/

                String relation = completeEntity(parser, dict, parser.get(index).HEAD.ID-1);
                entityMap.clear();

                if (relation.split(entity1).length > 1){
                    relation = relation.split(entity1)[1];
                }

                if (parser.get(parser.get(index).HEAD.ID-1).DEPREL.equals("定中关系")&&
                        !named[parser.get(parser.get(index).HEAD.ID-1).HEAD.ID-1].equals("O")){

                    //System.out.println("命名实体类型三元组===================");

                    String entity2 = completeEntity(parser, dict, parser.get(parser.get(index).HEAD.ID-1).HEAD.ID-1);
                    entityMap.clear();

                    int mi = parser.get(parser.get(index).HEAD.ID-1).HEAD.ID-1;
                    int li = mi;


                    if (named[mi].charAt(0) == 'B'){
                        while (named[mi].charAt(0) != 'E'){
                            mi++;
                        }
                    }

                    for (int j = li+1; j < mi+1; j++) {
                        entity2 += word[j];
                    }
                    
                    if (entity2.split(relation).length > 1){
                        entity2 = entity2.split(relation)[1];
                    }

                    result.add(entity1 + "," + relation + "," + entity2);
                }

            }
        }

        return result;
    }


    /**
     * @param parser 句法依存分析
     * @param dict 词语依存字典
     * @param i 词语索引
     * @return 三元组列表
     */
    private static Set<String> extract(List<CoNLLWord> parser,
                               List<Map<String, List<CoNLLWord>>> dict,
                               int i){
        CoNLLWord word = parser.get(i);
        Map<String, List<CoNLLWord>> dic = dict.get(i);

        Set<String> result = new HashSet<>();

        if (word.CPOSTAG.equals("v")){

            // 主谓宾关系：刘小绪生于四川
            if (dic.containsKey("主谓关系") && dic.containsKey("动宾关系")){
                String relation = word.LEMMA;

                for (CoNLLWord entity1:
                        dic.get("主谓关系")) {

                    for (CoNLLWord entity2:
                            dic.get("动宾关系")) {
                        //System.out.println("主谓宾关系==========");

                        String preEntity = completeEntity(parser, dict, entity1.ID-1);
                        entityMap.clear();

                        String rearEntity = completeEntity(parser, dict, entity2.ID-1);
                        entityMap.clear();

                        result.add(entity1.LEMMA + "," + relation + "," + entity2.LEMMA);
                        result.add(preEntity + "," + relation + "," + rearEntity);
                    }

                }
            }


            // 动补结构：刘小绪洗干净了衣服
            if (dic.containsKey("动补结构") && dic.containsKey("主谓关系") && dic.containsKey("动宾关系")){
                for (CoNLLWord entity1:
                        dic.get("主谓关系")) {
                    for (CoNLLWord complement:
                            dic.get("动补结构")) {
                        for (CoNLLWord entity2:
                                dic.get("动宾关系")) {
                            if (dic.containsKey("右附加关系")){
                                for (CoNLLWord subjoin:
                                        dic.get("右附加关系")) {

                                    //System.out.println("动补结构==========");

                                    String preEntity = completeEntity(parser, dict, entity1.ID-1);
                                    entityMap.clear();

                                    String rearEntity = completeEntity(parser, dict, entity2.ID-1);
                                    entityMap.clear();

                                    String relation = word.LEMMA + complement.LEMMA + subjoin.LEMMA;

                                    result.add(entity1.LEMMA + "," + relation + "," + entity2.LEMMA);
                                    result.add(preEntity + "," + relation + "," + rearEntity);
                                }
                            }else {

                                //System.out.println("动补结构==========");

                                String relation = word.LEMMA + complement.LEMMA;
                                result.add(entity1.LEMMA + "," + relation + "," + entity2.LEMMA);
                            }
                        }
                    }
                }
            }

            // 状动结构：父亲非常喜欢跑步
            // 非常 是 跑步的状语，关系应该为非常喜欢
            if (dic.containsKey("状中结构") && dic.containsKey("主谓关系") && dic.containsKey("动宾关系")){

                for (CoNLLWord entity1:
                        dic.get("主谓关系")) {
                    for (CoNLLWord adverbial:
                            dic.get("状中结构")) {
                        for (CoNLLWord entity2:
                                dic.get("动宾关系")) {

                            //System.out.println("状动结构==========");

                            String preEntity = completeEntity(parser, dict, entity1.ID-1);
                            entityMap.clear();

                            String rearEntity = completeEntity(parser, dict, entity2.ID-1);
                            entityMap.clear();

                            String relation = adverbial.LEMMA + word.LEMMA;

                            result.add(entity1.LEMMA + "," + relation + "," + entity2.LEMMA);
                            result.add(preEntity + "," + relation + "," + rearEntity);
                        }
                    }
                }

            }

            // 状动补结构：
            if (dic.containsKey("状中结构") && dic.containsKey("动补结构") &&
                    dic.containsKey("主谓关系") && dic.containsKey("动宾关系")){

                for (CoNLLWord entity1:
                        dic.get("主谓关系")) {
                    for (CoNLLWord adverbial:
                            dic.get("状中结构")) {
                        for (CoNLLWord complement:
                                dic.get("动补结构")) {
                            for (CoNLLWord entity2 :
                                    dic.get("动宾关系")) {

                                //System.out.println("状动补结构==========");

                                String preEntity = completeEntity(parser, dict, entity1.ID-1);
                                entityMap.clear();

                                String rearEntity = completeEntity(parser, dict, entity2.ID-1);
                                entityMap.clear();

                                String relation = adverbial.LEMMA + word.LEMMA + complement.LEMMA;

                                result.add(entity1.LEMMA + "," + relation + "," + entity2.LEMMA);
                                result.add(preEntity + "," + relation + "," + rearEntity);
                            }
                        }
                    }
                }
            }

            // 定语后置：父亲是来自肯尼亚的留学生
            if (word.DEPREL.equals("定中关系")){
                if (dic.containsKey("动宾关系")){
                    CoNLLWord entity1 = word.HEAD;
                    String relation = word.LEMMA;
                    for (CoNLLWord entity2:
                            dic.get("动宾关系")) {

                        //System.out.println("定语后置============");

                        String preEntity = completeEntity(parser, dict, entity1.ID-1);
                        entityMap.clear();

                        String rearEntity = completeEntity(parser, dict, entity2.ID-1);
                        entityMap.clear();

                        result.add(entity1.LEMMA + "," + relation + "," + entity2.LEMMA);
                        result.add(preEntity + "," + relation + "," + rearEntity);
                    }
                }
            }

            // 介宾关系：刘小绪就职于学校
            // 于 和 学校 是介宾关系
            if (dic.containsKey("主谓关系") && dic.containsKey("动补结构")){
                for (CoNLLWord entity1:
                        dic.get("主谓关系")) {
                    for (CoNLLWord prep:
                            dic.get("动补结构")) {

                        // 介词的索引
                        int prepIndex = prep.ID - 1;

                        Map<String, List<CoNLLWord>> prepDict = dict.get(prepIndex);

                        if (prepDict.containsKey("介宾关系")){
                            for (CoNLLWord entity2:
                                    prepDict.get("介宾关系")) {

                                //System.out.println("介宾关系===============");

                                String preEntity = completeEntity(parser, dict, entity1.ID-1);
                                entityMap.clear();

                                String rearEntity = completeEntity(parser, dict, entity2.ID-1);
                                entityMap.clear();

                                String relation = word.LEMMA + prep.LEMMA;

                                result.add(entity1.LEMMA + "," + relation + "," + entity2.LEMMA);
                                result.add(preEntity + "," + relation + "," + rearEntity);
                            }
                        }
                    }
                }
            }

            // 宾语前置结构：海洋由水组成
            if (dic.containsKey("前置宾语")){

                for (CoNLLWord entity2:
                        dic.get("前置宾语")) {
                    if (dic.containsKey("状中结构")){
                        for (CoNLLWord adverbial: // 状语
                                dic.get("状中结构")) {
                            int prepIndex = adverbial.ID - 1;
                            Map<String, List<CoNLLWord>> prepDict = dict.get(prepIndex);

                            if (prepDict.containsKey("介宾关系")){
                                for (CoNLLWord entity1: //
                                        prepDict.get("介宾关系")) {

                                    //System.out.println("宾语前置====================");

                                    String preEntity = completeEntity(parser, dict, entity1.ID-1);
                                    entityMap.clear();

                                    String rearEntity = completeEntity(parser, dict, entity2.ID-1);
                                    entityMap.clear();

                                    String relation = word.LEMMA;

                                    result.add(entity1.LEMMA + "," + relation + "," + entity2.LEMMA);
                                    result.add(preEntity + "," + relation + "," + rearEntity);
                                }
                            }
                        }
                    }
                }

            }
        }

        return result;
    }


    /**
     * 最外层list是为了记录是第几个
     * map中的key记录的是关系
     * map中的list记录的是这个关系的词语
     * @param text 带分析的句子
     * @return 词语依存字典
     */
    private static List<Map<String, List<CoNLLWord>>> dict(String text){

        CoNLLSentence sentence = HanLP.parseDependency(text);

        //System.out.println(sentence);

        CoNLLWord[] wordArray = sentence.getWordArray();

        List<Map<String, List<CoNLLWord>>> result = new ArrayList<>();

        for (int i = 0; i < wordArray.length; i++) {

            CoNLLWord word = wordArray[i];
            HashMap<String, List<CoNLLWord>> map = new HashMap<>();

            for (int j = 0; j < wordArray.length; j++) {
                CoNLLWord child = wordArray[j];
                if (word.LEMMA.equals(child.HEAD.LEMMA)){

                    if (map.containsKey(child.DEPREL)){
                        map.get(child.DEPREL).add(child);
                    }else {
                        List<CoNLLWord> list= new ArrayList<>();
                        list.add(child);

                        map.put(child.DEPREL, list);
                    }

                }
            }

            result.add(map);
        }

        return result;
    }

    /**
     * @param parser 句法分析结果
     * @param dict 词语依存字典
     * @param i 词语的索引
     * @return 完善后的实体
     */
    private static String completeEntity(List<CoNLLWord> parser,
                                         List<Map<String, List<CoNLLWord>>> dict,
                                         int i){
        CoNLLWord word = parser.get(i);
        Map<String, List<CoNLLWord>> dic = dict.get(i);

        String result1 = "";

        if (dic.containsKey("定中关系")){

            entityMap.put(word.LEMMA + "定", 1);

            List<CoNLLWord> words = dic.get("定中关系");

            for (int j = 0; j < words.size(); j++) {

                CoNLLWord temp = words.get(j);

                if (!entityMap.containsKey(temp.LEMMA + "定")){
                    result1 += completeEntity(parser, dict, temp.ID-1);
                }
            }

        }

        String result2 = "";

        if (word.CPOSTAG.equals("v")){
            if (dic.containsKey("动宾关系")){

                // 短路功能，不会报错，包含这个键才执行后面的取值
                if (!entityMap.containsKey(word.LEMMA + "动")){
                    result2 += completeEntity(parser, dict, dic.get("动宾关系").get(0).ID-1);
                    entityMap.put(word.LEMMA + "动", 1);
                }
            }
            if (dic.containsKey("主谓关系")){
                if (!entityMap.containsKey(word.LEMMA + "主")){
                    result1 = completeEntity(parser, dict, dic.get("主谓关系").get(0).ID-1) + result1;
                    entityMap.put(word.LEMMA + "主", 1);
                }
            }
        }

        return result1 + word.LEMMA + result2;
    }


    /**
     * 主函数，提取输入句子中的所有三元组关系
     * @param text 待提取三元组关系的句子
     */
    public static Set<String> main(String text){

        List<CoNLLWord> parser = parser(text);

        List<Map<String, List<CoNLLWord>>> dict = dict(text);


        /*
        for (int i = 0; i < dict.size(); i++) {
            System.out.println((i+1) + " " + dict.get(i));
        }
        */

        Set<String> result = relation(parser, dict);

        return result;
    }

    // 把、由
}

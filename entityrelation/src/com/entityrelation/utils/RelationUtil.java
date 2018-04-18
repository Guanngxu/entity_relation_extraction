package com.entityrelation.utils;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.corpus.dependency.CoNll.CoNLLSentence;
import com.hankcs.hanlp.corpus.dependency.CoNll.CoNLLWord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 刘绪光 on 2018/4/18.
 */
public class RelationUtil {

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
    private static List<String[]> relation(List<CoNLLWord> parser,
                                List<Map<String, List<CoNLLWord>>> dict){

        List<String[]> result = new ArrayList<>();

        for (int i = 0; i < parser.size(); i++) {

            List<String[]> relationList = extract(parser, dict, i);

            result.addAll(relationList);

        }

        return result;
    }


    /**
     * @param parser 句法依存分析
     * @param dict 词语依存字典
     * @param i 词语索引
     */
    private static List<String[]> extract(List<CoNLLWord> parser,
                               List<Map<String, List<CoNLLWord>>> dict,
                               int i){
        CoNLLWord word = parser.get(i);
        Map<String, List<CoNLLWord>> dic = dict.get(i);

        List<String[]> result = new ArrayList<>();

        if (word.CPOSTAG.equals("v")){

            // 主谓宾关系：刘小绪生于四川
            if (dic.containsKey("主谓关系") && dic.containsKey("动宾关系")){
                String relation = word.LEMMA;

                for (CoNLLWord entity1:
                        dic.get("主谓关系")) {

                    for (CoNLLWord entity2:
                            dic.get("动宾关系")) {

                        String preEntity = completeEntity(parser, dict, entity1.ID-1);
                        String rearEntity = completeEntity(parser, dict, entity2.ID-1);

                        String triad[] = new String[3];
                        triad[0] = preEntity;
                        triad[1] = relation;
                        triad[2] = rearEntity;

                        result.add(triad);
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

                                    String preEntity = completeEntity(parser, dict, entity1.ID-1);
                                    String rearEntity = completeEntity(parser, dict, entity2.ID-1);

                                    String triad[] = new String[3];
                                    triad[0] = preEntity;
                                    triad[1] = word.LEMMA + complement.LEMMA + subjoin.LEMMA;
                                    triad[2] = rearEntity;

                                    result.add(triad);
                                }
                            }else {

                                String triad[] = new String[3];
                                triad[0] = entity1.LEMMA;
                                triad[1] = word.LEMMA + complement.LEMMA;
                                triad[2] = entity2.LEMMA;

                                result.add(triad);
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

                            String preEntity = completeEntity(parser, dict, entity1.ID-1);
                            String rearEntity = completeEntity(parser, dict, entity2.ID-1);

                            String triad[] = new String[3];
                            triad[0] = preEntity;
                            triad[1] = adverbial.LEMMA + word.LEMMA;
                            triad[2] = rearEntity;

                            result.add(triad);
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

                                String preEntity = completeEntity(parser, dict, entity1.ID-1);
                                String rearEntity = completeEntity(parser, dict, entity2.ID-1);

                                String triad[] = new String[3];
                                triad[0] = preEntity;
                                triad[1] = adverbial.LEMMA + word.LEMMA + complement.LEMMA;
                                triad[2] = rearEntity;

                                result.add(triad);
                            }
                        }
                    }
                }
            }

            // 定语后置：父亲是来自肯尼亚的留学生
            // 来自 是 留学生的定于
            if (word.DEPREL.equals("定中关系")){
                if (dic.containsKey("动宾关系")){
                    CoNLLWord entity1 = word.HEAD;
                    String relation = word.LEMMA;
                    for (CoNLLWord entity2:
                            dic.get("动宾关系")) {
                        String preEntity = completeEntity(parser, dict, entity1.ID-1);
                        String rearEntity = completeEntity(parser, dict, entity2.ID-1);

                        String triad[] = new String[3];
                        triad[0] = preEntity;
                        triad[1] = relation;
                        triad[2] = rearEntity;

                        result.add(triad);
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

                                String preEntity = completeEntity(parser, dict, entity1.ID-1);
                                String rearEntity = completeEntity(parser, dict, entity2.ID-1);

                                String triad[] = new String[3];
                                triad[0] = preEntity;
                                triad[1] = word.LEMMA + prep.LEMMA;
                                triad[2] = rearEntity;

                                result.add(triad);
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

                                    String preEntity = completeEntity(parser, dict, entity1.ID-1);
                                    String rearEntity = completeEntity(parser, dict, entity2.ID-1);

                                    String triad[] = new String[3];
                                    triad[0] = preEntity;
                                    triad[1] = word.LEMMA;
                                    triad[2] = rearEntity;

                                    result.add(triad);
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

                    if (map.containsKey(word.DEPREL)){
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
            for (CoNLLWord temp:
                    dic.get("定中关系")) {
                result1 += completeEntity(parser, dict, temp.ID-1);
            }
        }

        String result2 = "";

        if (word.CPOSTAG.equals("v")){
            if (dic.containsKey("动宾关系")){
                result2 += completeEntity(parser, dict, dic.get("动宾关系").get(0).ID-1);
            }
            if (dic.containsKey("主谓关系")){
                result2 = completeEntity(parser, dict, dic.get("主谓关系").get(0).ID-1) + result1;
            }
        }

        return result1 + word.LEMMA + result2;
    }


    /**
     * 主函数，提取输入句子中的所有三元组关系
     * @param text 待提取三元组关系的句子
     */
    public static List<String[]> main(String text){

        List<CoNLLWord> parser = parser(text);

        List<Map<String, List<CoNLLWord>>> dict = dict(text);

        /*
        for (int i = 0; i < dict.size(); i++) {
            System.out.println((i+1) + " " + dict.get(i));
        }
        */

        List<String[]> result = relation(parser, dict);

        return result;
    }

    // 把、由
}

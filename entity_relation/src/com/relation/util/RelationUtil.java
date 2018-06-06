package com.relation.util;

import com.hankcs.hanlp.corpus.dependency.CoNll.CoNLLWord;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by 刘绪光 on 2018/6/6.
 */
public class RelationUtil {

    /**
     * 获取所有关系，里面有冗余，需要下一步的筛选
     * @param parser 句法依存分析
     * @param dict 词语依存字典
     */
    private static Set<String> relation(List<CoNLLWord> parser,
                                        List<Map<String, List<CoNLLWord>>> dict){

        /*
        for (CoNLLWord word :
                parser) {
            System.out.println(word);
        }

        for (Map<String, List<CoNLLWord>> dic :
                dict) {
            System.out.println(dic);
        }
        */

        Set<String> result = new HashSet<>();

        for (int i = 0; i < parser.size(); i++) {

            // 事实三元组
            Set<String> relationList = extract(parser, dict, i);

            result.addAll(relationList);

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
                                       int i) {
        CoNLLWord word = parser.get(i);
        Map<String, List<CoNLLWord>> dic = dict.get(i);

        Set<String> result = new HashSet<>();

        // 主谓宾关系：刘小绪生于四川
        if (dic.containsKey("主谓关系") && dic.containsKey("动宾关系")){

            CoNLLWord entity1 = dic.get("主谓关系").get(0);

            // 排除：刘小绪和李华是朋友
            // entity1.ID-1 即主语在依存字典中的索引
            if (dict.get(entity1.ID-1).containsKey("并列关系")){
                String relation = dic.get("动宾关系").get(0).LEMMA;

                CoNLLWord entity2 = dict.get(entity1.ID-1).get("并列关系").get(0);

                result.add(entity1.LEMMA + "," + relation + "," + entity2.LEMMA);
            }else {
                CoNLLWord entity2 = dic.get("动宾关系").get(0);

                String relation = word.LEMMA;

                result.add(entity1.LEMMA + "," + relation + "," + entity2.LEMMA);
            }
        }

        // 动补结构：刘小绪洗干净了衣服
        if (dic.containsKey("动补结构") && dic.containsKey("主谓关系") && dic.containsKey("动宾关系")){

            CoNLLWord entity1 = dic.get("主谓关系").get(0);
            CoNLLWord complement = dic.get("动补结构").get(0);
            CoNLLWord entity2 = dic.get("动宾关系").get(0);

            if (dic.containsKey("右附加关系")){
                CoNLLWord subjoin = dic.get("右附加关系").get(0);
                String relation = word.LEMMA + complement.LEMMA + subjoin.LEMMA;
                result.add(entity1.LEMMA + "," + relation + "," + entity2.LEMMA);
            }else {
                String relation = word.LEMMA + complement.LEMMA;
                result.add(entity1.LEMMA + "," + relation + "," + entity2.LEMMA);
            }
        }

        if (dic.containsKey("定中关系")){
            CoNLLWord entity1 = dic.get("定中关系").get(0);
            String relation = word.LEMMA;

            for (Map<String, List<CoNLLWord>> tempDic:
                 dict) {
                if (tempDic.containsKey("主谓关系") && tempDic.containsKey("动宾关系")){
                    if (tempDic.get("主谓关系").get(0).LEMMA.equals(relation)){
                        CoNLLWord entity2 = tempDic.get("动宾关系").get(0);
                        result.add(entity1.LEMMA + "," + relation + "," + entity2.LEMMA);
                    }
                }
            }
        }

        // 状动结构：父亲非常喜欢跑步
        // 非常 是 跑步的状语，关系应该为非常喜欢
        if (dic.containsKey("状中结构") && dic.containsKey("主谓关系") && dic.containsKey("动宾关系")){

            CoNLLWord entity1 = dic.get("主谓关系").get(0);
            CoNLLWord adverbial = dic.get("状中结构").get(0);
            CoNLLWord entity2 = dic.get("动宾关系").get(0);

            String relation = adverbial.LEMMA + word.LEMMA;

            result.add(entity1.LEMMA + "," + relation + "," + entity2.LEMMA);
        }


        // 状动补结构：
        if (dic.containsKey("状中结构") && dic.containsKey("动补结构") &&
                dic.containsKey("主谓关系") && dic.containsKey("动宾关系")){

            CoNLLWord entity1 = dic.get("主谓关系").get(0);
            CoNLLWord adverbial = dic.get("状中结构").get(0);
            CoNLLWord complement = dic.get("动补结构").get(0);
            CoNLLWord entity2 = dic.get("动宾关系").get(0);

            String relation = adverbial.LEMMA + word.LEMMA + complement.LEMMA;

            result.add(entity1.LEMMA + "," + relation + "," + entity2.LEMMA);
        }


        // 定语后置：父亲是来自肯尼亚的留学生
        if (word.DEPREL.equals("定中关系")){
            if (dic.containsKey("动宾关系")){
                CoNLLWord entity1 = word.HEAD;
                String relation = word.LEMMA;
                CoNLLWord entity2 = dic.get("动宾关系").get(0);

                result.add(entity1.LEMMA + "," + relation + "," + entity2.LEMMA);
            }
        }

        // 介宾关系：刘小绪就职于学校
        // 于 和 学校 是介宾关系
        if (dic.containsKey("主谓关系") && dic.containsKey("动补结构")){

            CoNLLWord entity1 = dic.get("主谓关系").get(0);
            CoNLLWord prep = dic.get("动补结构").get(0);

            // 介词的索引
            int prepIndex = prep.ID - 1;

            Map<String, List<CoNLLWord>> prepDict = dict.get(prepIndex);
            if (prepDict.containsKey("介宾关系")){
                CoNLLWord entity2 = prepDict.get("介宾关系").get(0);
                String relation = word.LEMMA + prep.LEMMA;

                result.add(entity1.LEMMA + "," + relation + "," + entity2.LEMMA);
            }
        }

        // 宾语前置结构：海洋由水组成
        if (dic.containsKey("前置宾语")){

            CoNLLWord entity2 = dic.get("前置宾语").get(0);
            if (dic.containsKey("状中结构")){
                CoNLLWord adverbial = dic.get("状中结构").get(0);
                int prepIndex = adverbial.ID - 1;
                Map<String, List<CoNLLWord>> prepDict = dict.get(prepIndex);

                if (prepDict.containsKey("介宾关系")){
                    CoNLLWord entity1 = prepDict.get("介宾关系").get(0);

                    String relation = word.LEMMA;
                    result.add(entity1.LEMMA + "," + relation + "," + entity2.LEMMA);
                }
            }
        }
        return result;
    }


    /**
     * @param textArr
     * @return 事实三元组
     */
    public static Set<String> entityRelation(String[] textArr){

        Set<String> result = new HashSet<>();

        for (String text :
                textArr) {
            List<CoNLLWord> parser = ParserUtil.parser(text);
            List<Map<String, List<CoNLLWord>>> dict = ParserUtil.dict(text);

            Set<String> rel = relation(parser, dict);

            result.addAll(rel);
        }
        return result;
    }

    public static void main(String[] args) {

        String text = "刘小绪和李华是朋友";

        List<CoNLLWord> parser = ParserUtil.parser(text);
        List<Map<String, List<CoNLLWord>>> dict = ParserUtil.dict(text);

        Set<String> result = relation(parser, dict);

        for (String rel :
                result) {
            System.out.println(rel);
        }
    }
}

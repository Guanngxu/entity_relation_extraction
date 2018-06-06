package com.relation.util;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.corpus.dependency.CoNll.CoNLLSentence;
import com.hankcs.hanlp.corpus.dependency.CoNll.CoNLLWord;
import com.hankcs.hanlp.dependency.nnparser.NeuralNetworkDependencyParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 刘绪光 on 2018/6/6.
 */
public class ParserUtil {

    public static void main(String[] args) {
        for (Map<String, List<CoNLLWord>> li:
                dict("刘小绪和小明是同学")) {
            System.out.println(li);
        }
    }


    /**
     * @param text 待分析的句子
     * @return 分析结果，按分词结果的顺序组织的
     */
    public static List<CoNLLWord> parser(String text){

        CoNLLSentence sentence = HanLP.parseDependency(text);

        CoNLLWord wordArr[] = sentence.getWordArray();

        List<CoNLLWord> result = new ArrayList<>();

        for (int i = 0; i < wordArr.length; i++) {
            result.add(wordArr[i]);
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
    public static List<Map<String, List<CoNLLWord>>> dict(String text){

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
}

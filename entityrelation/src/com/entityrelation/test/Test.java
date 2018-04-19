package com.entityrelation.test;

import com.entityrelation.utils.RelationUtil;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.model.perceptron.PerceptronLexicalAnalyzer;
import com.hankcs.hanlp.seg.common.Term;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by 刘绪光 on 2018/4/19.
 */
public class Test {

    public static void test1() {
        try {
            PerceptronLexicalAnalyzer analyzer = new PerceptronLexicalAnalyzer();

            List<Term> termList = HanLP.segment("贝拉克·侯赛因·奥巴马的身世复杂，1961年8月4日出生在美国夏威夷州檀香山市，父亲是来自肯尼亚的留学生，母亲是堪萨斯州白人。");

            String word[] = new String[termList.size()];

            for (int i = 0; i < termList.size(); i++) {
                word[i] = termList.get(i).word;
            }

            System.out.println(Arrays.asList(word));

            String pos[] = analyzer.getPOSTagger().tag(word);

            System.out.println(Arrays.asList(pos));

            System.out.println(Arrays.asList(analyzer.namedEntityRecognize(word, pos)));

            System.out.println(Arrays.asList("印度尼西亚首都".split("印度尼西亚")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void test2(){
        String text[] = {"贝拉克·侯赛因·奥巴马的身世复杂，1961年8月4日出生在美国夏威夷州檀香山市，父亲是来自肯尼亚的留学生，母亲是堪萨斯州白人。",
                "他们二人在就读夏威夷大学期间相识。",
                "由于父亲此后前往哈佛大学求学，奥巴马从小由母亲抚养，奥巴马两岁多时，他的父母婚姻破裂。"};

        /*for (String str:
             text) {
            List<String[]> res = RelationUtil.main(str);

            for (int i = 0; i < res.size(); i++) {
                String triad[] = res.get(i);
                System.out.println(triad[0] + "--" + triad[1] + "--" + triad[2]);
            }
        }*/

        String str = "俄罗斯9月31日出兵，开始在叙利亚对“伊斯兰国”发动猛烈空袭，战果显著，俄总统弗拉基米尔·普京本月16日宣布，由于俄客机上月31日在埃及上空遭炸弹袭击而坠毁，俄方将强化在叙空袭行动，次日，国防部长谢尔盖·绍伊古宣布，俄将把空袭强度增为原来的两倍，并已制定针对“伊斯兰国”的新的空袭计划，决定动用远程战略轰炸机。";

        List<String[]> res = RelationUtil.main(str);

        for (int i = 0; i < res.size(); i++) {
            String triad[] = res.get(i);
            System.out.println(triad[0] + "--" + triad[1] + "--" + triad[2]);
        }

        // String word[] = {"跑", "吃饭", "哈尔滨工程大学", "刘小绪", "图书馆", "漂亮", "可爱"};
    }

    public static void main(String[] args) {
        test2();
    }

}

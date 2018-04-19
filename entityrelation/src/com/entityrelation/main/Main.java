package com.entityrelation.main;


import com.entityrelation.utils.RelationUtil;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.model.perceptron.PerceptronLexicalAnalyzer;
import com.hankcs.hanlp.seg.common.Term;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by 刘绪光 on 2018/4/17.
 */
public class Main {

    public static void main(String[] args) {
        String text[] = {"贝拉克·侯赛因·奥巴马的身世复杂，1961年8月4日出生在美国夏威夷州檀香山市，父亲是来自肯尼亚的留学生，母亲是堪萨斯州白人。",
                        "由于父亲此后前往哈佛大学求学，奥巴马从小由母亲抚养，奥巴马两岁多时，他的父母婚姻破裂。"};

        for (String str:
             text) {
            List<String[]> res = RelationUtil.main(str);

            for (int i = 0; i < res.size(); i++) {
                String triad[] = res.get(i);
                System.out.println(triad[0] + "--" + triad[1] + "--" + triad[2]);
            }
        }

    }

}

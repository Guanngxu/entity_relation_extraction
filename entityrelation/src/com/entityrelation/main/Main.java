package com.entityrelation.main;


import com.entityrelation.utils.FileUtil;
import com.entityrelation.utils.RelationUtil;

import java.io.File;
import java.util.List;
import java.util.Set;

/**
 * Created by 刘绪光 on 2018/4/17.
 */
public class Main {

    public static void fileRelationExtraction(String directory, String outPath) throws Exception{

        File files[] = FileUtil.listFile(directory);

        for (File file:
             files) {
            List<String> contentList = FileUtil.readFile(file);

            System.out.println(file);

            for (String content :
                    contentList) {

                String sentences = content.trim();

                String[] sentence = sentences.split("。");

                for (int i = 0; i < sentence.length; i++) {
                    Set<String> relationList = RelationUtil.main(sentence[i]);

                    FileUtil.writeFile(outPath, relationList);
                }
            }
        }

    }

    public static void main(String[] args) {
        String text[] = {"刘小绪生于四川", "刘小绪洗干净了衣服", "父亲非常喜欢跑步", "父亲是来自肯尼亚的留学生",
                "海洋由水组成", "刘小绪就职于学校", "海洋由水组成"};

        for (String str:
             text) {
            Set<String> res = RelationUtil.main(str);

            System.out.println(str);
            System.out.println("======三元组======");
            for (String rel :
                    res) {
                System.out.println(rel);
            }
            System.out.println();
        }

    }

}

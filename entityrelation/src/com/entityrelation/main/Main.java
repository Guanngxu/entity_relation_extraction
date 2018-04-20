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

            for (String content :
                    contentList) {
                Set<String> relationList = RelationUtil.main(content);

                FileUtil.writeFile(outPath, relationList);
            }
        }

    }

    public static void main(String[] args) {
        String text[] = {"父亲非常喜欢跑步"};

        for (String str:
             text) {
            Set<String> res = RelationUtil.main(str);

            for (String rel :
                    res) {
                System.out.println(rel);
            }
        }

    }

}

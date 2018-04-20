package com.entityrelation.main;


import com.entityrelation.utils.FileUtil;
import com.entityrelation.utils.RelationUtil;

import java.io.File;
import java.util.List;

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
                List<String> relationList = RelationUtil.main(content);

                FileUtil.writeFile(outPath, relationList);
            }
        }

    }

    public static void main(String[] args) {
        String text[] = {"根据《上海市长期护理保险试点办法》（沪府发〔2016〕110号）的要求，本市开展长期护理保险试点工作。"};

        for (String str:
             text) {
            List<String> res = RelationUtil.main(str);

            for (int i = 0; i < res.size(); i++) {
                System.out.println(res.get(i));
            }
        }

    }

}

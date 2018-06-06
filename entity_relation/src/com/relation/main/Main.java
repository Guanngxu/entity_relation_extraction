package com.relation.main;

import com.relation.util.RelationUtil;

import java.util.Set;

/**
 * Created by 刘绪光 on 2018/6/6.
 */
public class Main {

    public static void main(String[] args) {
        String[] testArr = {
                "刘小绪非常喜欢跑步",
                "刘小绪和李华是朋友",
                "刘小绪生于四川",
                "刘小绪洗干净了衣服",
                "海洋由水组成",
                "父亲是来自肯尼亚的留学生",
                "刘小绪就职于学校",
                "中国的首都是北京"
        };

        Set<String> result = RelationUtil.entityRelation(testArr);

        for (String relation :
                result) {
            System.out.println(relation);
        }
    }

}

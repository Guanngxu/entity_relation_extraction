package com.entityrelation.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by 刘绪光 on 2018/4/19.
 */
public class FileUtil {

    /**
     * 列出文件夹下所有的文件，不考虑文件夹中还包含文件夹爱
     * @param directoryPath 文件夹路径
     * @return 文件数组
     */
    public static File[] listFile(String directoryPath){

        File directory = new File(directoryPath);

        return directory.listFiles();
    }

    /**
     * 读取文件中的内容，以list结构返回
     * @param file 待读取文件
     * @return
     * @throws Exception
     */
    public static List<String> readFile(File file) throws Exception{

        List<String> result = new ArrayList<>();

        FileReader fileReader = new FileReader(file);


        BufferedReader reader = new BufferedReader(fileReader);

        String str = null;

        while ((str = reader.readLine()) != null){
            result.add(str);
        }

        fileReader.close();
        reader.close();

        return result;
    }


    /**
     * 把三元组写入文件
     * @param path
     * @param contentList
     * @throws Exception
     */
    public static void writeFile(String path, Set<String> contentList) throws Exception{

        File file = new File(path);
        file.createNewFile();
        file = null;

        // 第二参数设置为true表示追加文件内容
        FileWriter writer = new FileWriter(path, true);

        for (String content:
             contentList) {
            writer.write(content + "\n");
        }

        writer.flush();
        writer.close();
    }
}

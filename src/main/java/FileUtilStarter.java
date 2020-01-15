/*
 * Copyright (c) 2019, Winston.Xiang All Rights Reserved.
 */

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author winston
 * @date 2019/9/16 18:46
 * 按照指定关键字过滤文件，并写入新文件
 */
public class FileUtilStarter {
    public static void main(String[] args) {
        /*
        1.从指定目录获取所有的文件对象
        2.遍历文件，对每个文件符合关键词的行筛选出来
        3.将每行字符串作为list的元素
         */
        File file = new File("E:\\log_analysis\\2019-09-16");
        File[] files = file.listFiles();
        List<String> list = new ArrayList<>();
        for (File f : files) {
            FileUtil.readFile(f, lines -> {
                List<String> collect = lines.filter(
                        line -> line.contains("关键词")).collect(Collectors.toList());
                list.addAll(collect);
                return null;
            });
        }
        /*
        1.将读取的所有字符串按照前24字符进行比较排序
        （这里之所以这样做是由于源文件每行开头就是时间，2019-08-20 17:38:26.298 刚好是24字符，有点巧妙）
        2.遍历字符串依次写入
         */
        SortedSet<String> set = new TreeSet<>(Comparator.comparing(o -> o.substring(0, 23)));
        set.addAll(list);
        FileUtil.writeFile(new File("E:\\log_analysis\\2019-09-16\\关键词.log"),
                bufferedWriter -> {
                    for (String s : set) {
                        try {
                            bufferedWriter.write(s);
                            bufferedWriter.flush();
                            bufferedWriter.newLine();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}

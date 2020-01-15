/*
 * Copyright (c) 2019, Winston.Xiang All Rights Reserved.
 */

import java.io.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * @author winston
 * @date 2019/9/16 18:45
 * 创建/读/写文件
 */
public class FileUtil {
    /**
     * 文件不存在时，创建新文件
     * 1.文件不存在时先获取父目录
     * 2.父目录也不存在，创建目录
     * 3.再创建新文件，抓取异常
     *
     * @param file
     */
    public static void createIfNotExists(File file) {
        if (!file.exists()) {
            File parentFile = file.getParentFile();
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            }
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 读取文件内容
     * 1.文件转换为文件输入流
     * 2.文件输入流转换为字节输入流，再包装成缓冲字符流读取
     * 3.读取文件的每一行并返回
     * 4.关闭流
     * static的<T>是用来声明后面的 T是泛型，没有实际含义
     */
    public static <T> T readFile(File file, Function<Stream<String>, T> function) {
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file); // 传入的文件可能找不到，需要处理异常
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            return function.apply(reader.lines());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 写文件内容
     * 1.构建到写入文件路径的输出流
     * 2.文件输出流转换为字节流，再包装为缓冲字符流写入
     * 3.消费者执行接收行为，写入文件
     * 4.关闭流
     */
    public static void writeFile(File file, Consumer<BufferedWriter> consumer) {
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
            consumer.accept(bufferedWriter);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

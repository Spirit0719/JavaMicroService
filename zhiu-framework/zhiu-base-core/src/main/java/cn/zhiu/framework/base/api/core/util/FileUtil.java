package cn.zhiu.framework.base.api.core.util;

import com.google.common.collect.Lists;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public class FileUtil {

    /**
     * Each read line.
     *
     * @param filePath the file path
     * @param consumer the consumer
     *
     * @throws IOException the io exception
     * @author zhuzz
     * @time 2020 /01/06 14:38:42
     */
    public static void eachReadLine(String filePath, Consumer<String> consumer) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                consumer.accept(line);
            }
        } finally {
            reader.close();
        }
    }

    /**
     * Read file content list.
     *
     * @param filePath the file path
     *
     * @return the list
     *
     * @throws IOException the io exception
     * @author zhuzz
     * @time 2020 /01/06 14:38:45
     */
    public static List<String> readFileContent(String filePath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        ArrayList<String> list = Lists.newArrayList();
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                list.add(line);
            }
        } finally {
            reader.close();
        }
        return list;
    }

    /**
     * Write.
     *
     * @param path    the path
     * @param content the content
     *
     * @throws IOException the io exception
     * @author zhuzz
     * @time 2020 /01/06 14:40:30
     */
    public static void write(String path, String content) throws IOException {
        write(path, Lists.newArrayList(content), true);
    }

    /**
     * Write.
     *
     * @param path   the path
     * @param list   the list
     * @param append the append
     *
     * @throws IOException the io exception
     * @author zhuzz
     * @time 2020 /01/06 14:38:52
     */
    public static void write(String path, Collection<String> list, Boolean append) throws IOException {

        File file = new File(path);
        boolean hasContent = false;
        if (!file.exists()) {
            file.createNewFile();
        } else {
            hasContent = file.length() > 0;
        }

        BufferedWriter bw = new BufferedWriter(new FileWriter(file, append));
        try {
            int i = 0;
            for (String str : list) {
                if (!(i == 0 && !hasContent)) {
                    bw.newLine();
                }
                bw.append(str);
                i++;
            }
        } finally {
            bw.flush();
            bw.close();
        }
    }
}

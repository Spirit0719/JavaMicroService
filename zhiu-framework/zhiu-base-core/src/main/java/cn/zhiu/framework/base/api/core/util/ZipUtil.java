package cn.zhiu.framework.base.api.core.util;

import cn.zhiu.framework.base.api.core.exception.BaseApiException;
import com.google.common.collect.Lists;
import org.apache.commons.io.FileExistsException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.NotDirectoryException;
import java.text.DecimalFormat;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class ZipUtil {


    private static Logger logger = LoggerFactory.getLogger(ZipUtil.class);


    /**
     * zip解压
     *
     * @param srcFilePath the src file path
     * @param destDirPath 解压后的目标文件夹
     *
     * @throws RuntimeException 解压失败会抛出运行时异常
     * @author zhuzz
     * @time 2019 /05/12 13:14:33
     */
    public static void unzip(String srcFilePath, String destDirPath) throws RuntimeException {
        unzip(srcFilePath, destDirPath, null);
    }

    /**
     * Unzip.
     *
     * @param srcFilePath the src file path
     * @param destDirPath the dest dir path
     * @param progress    the progress
     *
     * @throws RuntimeException the runtime exception
     * @author zhuzz
     * @time 2019 /11/07 14:45:27
     */
    public static void unzip(String srcFilePath, String destDirPath, CustomConsumer<Double, Long, Long> progress) throws RuntimeException {
        unzip(new File(srcFilePath), destDirPath, progress);
    }

    public static void unzip(File srcFile, String destDirPath, CustomConsumer<Double, Long, Long> progress) throws RuntimeException {

        System.setProperty("sun.zip.encoding", System.getProperty("sun.jnu.encoding")); //防止文件名中有中文时出错

        long start = System.currentTimeMillis();
        // 判断源文件是否存在
        if (!srcFile.exists()) {
            throw new RuntimeException(srcFile.getPath() + "所指文件不存在");
        }
        // 开始解压
        ZipFile zipFile = null;
        try {

            ZipModel model = pretreatment(srcFile);
            List<ZipEntry> list = model.getList();
            zipFile = model.getZipFile();

            boolean ignoreFirstDir = false;
            List<String> totalDirs = list.stream().map(p ->
                    {
                        int endIndex = p.getName().indexOf(PathUtil.separator);
                        if (endIndex != -1) {
                            return p.getName().substring(0, endIndex);
                        }
                        return null;
                    }
            ).distinct().collect(Collectors.toList());

            if (totalDirs.size() == 1 && Objects.nonNull(totalDirs.get(0))) {
                ignoreFirstDir = true;
            }
            long startTime = System.currentTimeMillis();
            for (int i = 0; i < list.size(); i++) {
                ZipEntry entry = list.get(i);
                String name = entry.getName();
                if (ignoreFirstDir) {
                    name = name.substring(name.indexOf(PathUtil.separator) + 1);
                }
                // 如果是文件夹，就创建个文件夹
                if (entry.isDirectory()) {
                    String dirPath = destDirPath + "/" + name;
                    File dir = new File(dirPath);
                    dir.mkdirs();
                } else {
                    // 如果是文件，就先创建一个文件，然后用io流把内容copy过去
                    File targetFile = new File(destDirPath + "/" + name);
                    // 保证这个文件的父文件夹必须要存在
                    if (!targetFile.getParentFile().exists()) {
                        targetFile.getParentFile().mkdirs();
                    }
                    targetFile.createNewFile();
                    // 将压缩文件内容写入到这个文件中
                    InputStream is = zipFile.getInputStream(entry);
                    FileOutputStream fos = new FileOutputStream(targetFile);
                    int len;
                    byte[] buf = new byte[1024];
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                    }
                    // 关流顺序，先打开的后关闭
                    fos.close();
                    is.close();
                }
                if (Objects.nonNull(progress)) {
                    long currentTime = System.currentTimeMillis();
                    progress.accept(getProcess(i + 1, list.size()), startTime, currentTime);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("unzip error from ZipUtils", e);
        } finally {
            if (zipFile != null) {
                try {
                    zipFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        long end = System.currentTimeMillis();

        logger.error("unzip successful path:{} start time:{} end time:{}", srcFile.getAbsolutePath(), start, end);
    }

    public static ZipModel pretreatment(File srcFile) throws IOException {


        Charset newCharset = Charset.forName("GBK");
        try {
            logger.error("unzip file for {}", newCharset.name());
            return pretreatment(srcFile, newCharset);
        } catch (Exception ex) {
            if (ex instanceof IllegalArgumentException) {
                if (ex.getMessage().indexOf("MALFORMED") == 0 || ex.getMessage().contains("hasMessyCode")) {
                    logger.error("try unzip file error charset is not avalible! files1:{},{}", newCharset.name(), srcFile.getAbsolutePath());
                } else {
                    throw ex;
                }
            } else {
                throw ex;
            }

        }

        List<Charset> charsets = Lists.newArrayList();

//        Charset fileEncode = FileCharsetDetector.getFileEncode(srcFile);
//        charsets.add(fileEncode);

        Charset defaultCharset = Charset.defaultCharset();
//        if (!fileEncode.equals(defaultCharset)) {
        charsets.add(defaultCharset);
//        }

        Charset utf8Charset = Charset.forName("UTF-8");
        if (!charsets.contains(utf8Charset)) {
            charsets.add(utf8Charset);
        }

        for (
                Charset charset : charsets) {
            try {
                logger.error("unzip file for {}", charset.name());
                return pretreatment(srcFile, charset);
            } catch (Exception ex) {
                if (ex instanceof IllegalArgumentException) {
                    if (ex.getMessage().indexOf("MALFORMED") == 0 || ex.getMessage().contains("hasMessyCode")) {
                        logger.error("try unzip file error charset is not avalible! files:{},{}", charset.name(), srcFile.getAbsolutePath());
                        continue;
                    }
                }
                throw ex;
            }
        }
        throw new BaseApiException("unzip failure! " + srcFile.getAbsolutePath());
    }

    public static ZipModel pretreatment(File srcFile, Charset charset) throws IOException {

        ZipFile zipFile = new ZipFile(srcFile, charset);
        ZipModel model = new ZipModel();
        model.setZipFile(zipFile);
        Enumeration<?> entries = zipFile.entries();
        List<ZipEntry> list = Lists.newArrayList();
        while (entries.hasMoreElements()) {
            Object next = entries.nextElement();
            ZipEntry zipEntry = (ZipEntry) next;
            if (isMessyCode(zipEntry.getName())) {
                logger.error("unzip has messy code! ,{} ,{}", charset.name(), srcFile.getAbsolutePath());
                throw new IllegalArgumentException("hasMessyCode");
            }
            list.add(zipEntry);
        }
        model.setList(list);
        return model;
    }

//    private static boolean isMessyCode(String str) {
//        logger.error("file name {}", str);
//        return str.contains("??");
//    }


    /**
     * 判断字符串是否是乱码
     *
     * @param strName 字符串
     *
     * @return 是否是乱码
     */
    public static boolean isMessyCode(String strName) {
        Pattern p = Pattern.compile("\\s*|t*|r*|n*");
        Matcher m = p.matcher(strName);
        String after = m.replaceAll("");//去重为空的情况
        String temp = after.replaceAll("\\p{P}", "");
        char[] ch = temp.trim().toCharArray();
        float chLength = ch.length;
        float count = 0;
        for (int i = 0; i < ch.length; i++) {
            char c = ch[i];
            if (!Character.isLetterOrDigit(c)) {
                if (!isChinese(c)) {
                    count = count + 1;
                }
            }
        }
        float result = count / chLength;
        if (result > 0.4) {
            return true;
        } else {
            return false;
        }

    }


    /**
     * 判断字符是否是中文
     *
     * @param c 字符
     *
     * @return 是否是中文
     */
    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
            return true;
        }
        return false;
    }


    private static double getProcess(int curIndex, int size) {
        if (size == 0) {
            return .0;
        }
        DecimalFormat df = new DecimalFormat("#.0");
        double curProcess = (curIndex / (double) size) * 100;
        String format = df.format(curProcess);
        return Double.valueOf(format);
    }


    public static void compressFilePathListToZip(List<String> files, String zipPath) throws IOException {
        compressFilesToZip(files.stream().map(File::new).collect(Collectors.toList()), zipPath);
    }

    public static void compressFilesToZip(List<File> files, String zipPath) throws IOException {
        if (StringUtils.isBlank(zipPath)) {
            throw new RuntimeException("zip path not regular!");
        }

        File zipFile = new File(zipPath);

        if (zipFile.exists()) {
            throw new FileExistsException();
        }
        ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(zipFile));
        try {
            long count = files.stream().filter(p -> !p.isFile()).count();
            if (count > 0) {
                throw new RuntimeException("has not file item!");
            }
            for (File file : files) {
                putZipEntry(zipOutputStream, file, null);
            }
        } finally {
            zipOutputStream.closeEntry();
            zipOutputStream.close();
        }
    }

    public static String compressToZip(String filePath, boolean includeSelfPath) throws IOException {
        return compressToZip(filePath, null, includeSelfPath);
    }

    public static String compressToZip(String filePath) throws IOException {
        return compressToZip(filePath, null, false);
    }

    public static String compressToZip(String filePath, String zipPath, boolean includeSelfPath) throws IOException {

        File file = new File(filePath);

        if (!file.exists()) {
            throw new FileNotFoundException();
        }

        if (StringUtils.isBlank(zipPath)) {
            if (PathUtil.separator.equals(filePath.trim())) {
                throw new RuntimeException("can't compress file in root directory!");
            }
            if (file.isFile()) {
                zipPath = file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf(".")) + ".zip";
            } else if (file.isDirectory()) {
                String fileAbsolutePath = PathUtil.standardizeDir(file.getAbsolutePath());
                zipPath = fileAbsolutePath + ".zip";
            }
        }


        File zipFile = new File(zipPath);

        if (zipFile.exists()) {
            throw new FileExistsException();
        }

        ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(zipFile));
        try {
            String prefixPath = null;
            if (includeSelfPath) {
                prefixPath = file.getName();
            }
            if (file.isFile()) {
                putZipEntry(zipOutputStream, file, prefixPath);
            } else if (file.isDirectory()) {
                putDirectoryToZip(zipOutputStream, file, file, prefixPath);
            }
        } finally {
            zipOutputStream.closeEntry();
            zipOutputStream.close();
        }
        return zipPath;
    }

    private static void putDirectoryToZip(ZipOutputStream zipOutputStream, File file, File dirRoot, String prefixPath) throws IOException {
        if (file.isDirectory()) {
            if (!file.equals(dirRoot)) {
                String path = PathUtil.standardizeDir(file.getAbsolutePath());
                String name = path;
                if (path.indexOf(PathUtil.separator) > -1) {
                    name = path.substring(path.lastIndexOf(PathUtil.separator) + 1);
                }
                name = name + PathUtil.separator;
                if (StringUtils.isNotBlank(prefixPath)) {
                    name = PathUtil.get(prefixPath, name);
                }
                zipOutputStream.putNextEntry(new ZipEntry(name));
            }
            for (File item : file.listFiles()) {
                if (item.isDirectory()) {
                    putDirectoryToZip(zipOutputStream, item, dirRoot, prefixPath);
                } else {
                    putZipEntry(zipOutputStream, item, dirRoot, prefixPath);
                }
            }
        }
    }


    private static void putZipEntry(ZipOutputStream zipOutputStream, File file, String prefixPath) throws IOException {
        putZipEntry(zipOutputStream, file, null, prefixPath);
    }

    private static void putZipEntry(ZipOutputStream zipOutputStream, File file, File dirRoot, String prefixPath) throws IOException {
        if (!file.exists()) {
            throw new FileNotFoundException();
        }
        if (file.isDirectory()) {
            return;
        }
        FileInputStream inputStream = new FileInputStream(file);
        try {

            String name = file.getName();
            if (Objects.nonNull(dirRoot)) {
                if (!dirRoot.isDirectory()) {
                    throw new NotDirectoryException(dirRoot.getPath());
                }
                String dirAbsolutePath = dirRoot.getAbsolutePath();
                String fileAbsolutePath = file.getAbsolutePath();
                if (fileAbsolutePath.indexOf(dirAbsolutePath) == 0) {
                    name = fileAbsolutePath.substring(dirAbsolutePath.length());
                    if (name.indexOf(PathUtil.separator) == 0) {
                        name = name.substring(name.indexOf(PathUtil.separator) + 1);
                    }
                } else {
                    throw new RuntimeException("dir with file not relation!");
                }
            }
            if (StringUtils.isNotBlank(prefixPath)) {
                name = PathUtil.get(prefixPath, name);
            }
            ZipEntry zipEntry = new ZipEntry(name);
            zipOutputStream.putNextEntry(zipEntry);
            int len;
            byte[] buffer = new byte[1024];
            while ((len = inputStream.read(buffer)) != -1) {
                zipOutputStream.write(buffer, 0, len);
            }
        } finally {
            inputStream.close();
        }
    }


}

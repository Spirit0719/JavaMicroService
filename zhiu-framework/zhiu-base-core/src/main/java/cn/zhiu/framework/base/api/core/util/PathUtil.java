package cn.zhiu.framework.base.api.core.util;

import cn.zhiu.framework.base.api.core.bean.DirStructModel;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;
import java.util.Objects;


public class PathUtil {

    private static Logger logger = LoggerFactory.getLogger(PathUtil.class);

    public static final String separator = "/";


    /**
     * 获取相对于项目运行时环境的绝对位置
     *
     * @return the path
     *
     * @author zhuzz
     * @time 2019 /04/22 15:21:53
     */
    public static String getApplicationPath() {

        String path = PathUtil.class.getClassLoader().getResource("").getPath();

        logger.error("application path : {}", path);
        boolean isJar = path.contains("file:");
        path = path.replace("file:", "");
        path = path.split("!")[0];

        if (path.lastIndexOf(separator) == path.length() - 1) {
            path = path.substring(0, path.lastIndexOf(separator));
        }
        if (isJar) {
            path = path.substring(0, path.lastIndexOf(separator));
        }
        logger.error("application path2 : {}", path);
        return standardizeDir(path) + separator;
    }

    /**
     * 标准化路径 例如标准化后: /aa/bb/cc
     *
     * @param dir the dir
     *
     * @return the string
     *
     * @author zhuzz
     * @time 2019 /05/08 17:58:27
     */
    public static String standardizeDir(String dir) {
        if (StringUtils.isBlank(dir)) {
            return StringUtils.trim(dir);
        }
        dir = StringUtils.trim(dir);
        if (!separator.equals(File.separator)) {
            if (dir.indexOf('\\') != -1) {
                dir = dir.replace("\\", separator);
            }
        }
        int i = dir.lastIndexOf(separator);
        if (i == dir.length() - 1 && dir.length() > 1) {
            dir = dir.substring(0, dir.length() - 1);
        }
        while (dir.indexOf(separator + separator) != -1) {
            dir = dir.replace(separator + separator, separator);
        }
        return dir;
    }


    /**
     * 获取多路径段拼接
     *
     * @param first the first
     * @param more  the more
     *
     * @return the string
     *
     * @author zhuzz
     * @time 2019 /05/08 18:27:01
     */
    public static String get(String first, String... more) {

        String path;
        if (more.length == 0) {
            path = first;
        } else {
            StringBuilder builder = new StringBuilder();
            builder.append(first);
            int length = more.length;
            for (int i = 0; i < length; i++) {
                String item = more[i];
                if (StringUtils.isNotBlank(item)) {
                    if (builder.length() > 0) {
                        builder.append(separator);
                    }
                    builder.append(item);
                }
            }
            path = builder.toString();
        }
        while (path.indexOf(separator + separator) != -1) {
            path = path.replace(separator + separator, separator);
        }
        return path;
    }

    public static DirStructModel getDirStruct(String dirPath) {
        Objects.requireNonNull(dirPath);
        logger.error("get dir struct {}", dirPath);
        DirStructModel dirStructModel = new DirStructModel();
        dirStructModel.setPath(dirPath);
        dirStructModel.setFileFlag(false);
        dirStructModel.setDirStructModels(getDirStructList(dirPath, dirPath, dirStructModel));
        return dirStructModel;
    }

    private static List<DirStructModel> getDirStructList(String originPath, String curPath, DirStructModel dirStructModel) {
        File file = new File(curPath);
        List<DirStructModel> list = Lists.newArrayList();
        if (file.exists()) {
            if (file.isDirectory()) {
                for (File item : file.listFiles()) {
                    DirStructModel child = new DirStructModel();
                    child.setParent(dirStructModel);
                    child.setName(item.getName());
                    child.setFileFlag(item.isFile());
                    child.setDirStructModels(getDirStructList(originPath, item.getAbsolutePath(), child));
                    String path = item.getAbsolutePath().substring(originPath.length());
                    child.setPath(path);
                    list.add(child);
                }
            }
        }
        return list;
    }


    public static void main(String[] args) {

     
//        DirStructModel dirStruct = PathUtil.getDirStruct(PathUtil.getApplicationPath());
//        Iterator<DirStructModel> iterator = dirStruct.flattenedStructChilds().iterator();
//        while (iterator.hasNext()) {
//            DirStructModel model = iterator.next();
//            System.out.println(model.getPath());
//        }
//
//        Paths.get("", "");
////        File file = new File("/Users/zhou/.bash_alias");
//
//
//        System.out.println(getApplicationPath());
//        File file = new File("");
//        System.out.println(standardizeDir(file.getAbsolutePath()));

//        String path = get("aa", "bb", "cc/", "\\dd");

//        System.out.println(PathUtil.get("/app/files").toString());

        //file:/app/restful-file-server-1.0.0-SNAPSHOT-qa.jar!/BOOT-INF/classes!/

//        String path = "file:/app/restful-file-server-1.0.0-SNAPSHOT-qa.jar!/BOOT-INF/classes!/";
////        String path = PathUtil.getApplicationPath();
//        System.out.println(path);
//        path = path.replace("file:", "");
//        path = path.split("!")[0];
//        path = path.substring(0, path.lastIndexOf(separator));
//        path = path + separator;
//
//
//        System.out.println(path);


//        System.out.println(PathUtil.get("aa", "bb", "cc","dd.aa"));

    }

}

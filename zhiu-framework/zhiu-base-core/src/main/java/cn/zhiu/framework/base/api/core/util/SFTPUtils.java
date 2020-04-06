package cn.zhiu.framework.base.api.core.util;

import ch.ethz.ssh2.*;
import cn.zhiu.framework.base.api.core.bean.ConnModel;
import cn.zhiu.framework.base.api.core.bean.DirStructModel;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * The type Sftp utils.
 *
 * @author zhuzz
 * @time 2019 /04/29 20:57:38
 */
public class SFTPUtils {

    static Logger logger = LoggerFactory.getLogger(SFTPUtils.class);


    public static final String FILEPREFIX = "-rw";
    public static final String DIRPREFIX = "drwxr";


    /**
     * 发送本地文件夹到远程（原结构复制）
     *
     * @param connModel             the conn model
     * @param localDirPath          本地文件夹路径 绝对路径
     * @param remoteTargetDirectory 远程文件夹路径 必须已存在 绝对路径
     *
     * @author zhuzz
     * @time 2019 /04/29 22:36:21
     */
    public static void sendLocalDirToRemote(ConnModel connModel, String localDirPath, String remoteTargetDirectory) {
        localDirPath = removeLastSeparator(localDirPath);
        remoteTargetDirectory = removeLastSeparator(remoteTargetDirectory);

        sendLocalDirToRemote(connModel, localDirPath, remoteTargetDirectory, false);
    }

    /**
     * 发送本地文件夹到远程（原结构复制）
     *
     * @param connModel               the conn model
     * @param localDirPath            本地文件夹路径 绝对路径
     * @param remoteTargetDirectory   远程文件夹路径 必须已存在 绝对路径
     * @param incloudLastLocalDirName 是否包含本地文件夹的最后一个路径名 例如: /aa/bb/cc cc
     *
     * @author zhuzz
     * @time 2019 /05/05 00:15:35
     */
    public static void sendLocalDirToRemote(ConnModel connModel, String localDirPath, String remoteTargetDirectory, boolean incloudLastLocalDirName) {
        localDirPath = removeLastSeparator(localDirPath);
        remoteTargetDirectory = removeLastSeparator(remoteTargetDirectory);
        Map<String, List<String>> maps = getFilePath(localDirPath, incloudLastLocalDirName);

        sendLocalDirToRemote(connModel, maps, remoteTargetDirectory);
    }


    /**
     * 发送文件到远程文件夹（不会复制文件路径）
     *
     * @param connModel             the conn model
     * @param fileList              文件集合 每个文件的绝对路径
     * @param remoteTargetDirectory 远程文件夹路径 必须已存在 绝对路径
     *
     * @author zhuzz
     * @time 2019 /04/30 13:40:03
     */
    public static void sendFilesToRemote(ConnModel connModel, List<String> fileList, String remoteTargetDirectory) {
        remoteTargetDirectory = removeLastSeparator(remoteTargetDirectory);
        Map<String, List<String>> map = new HashMap<>();
        map.put("", fileList);
        sendLocalDirToRemote(connModel, map, remoteTargetDirectory);
    }

    public static void sendFileToRemote(ConnModel connModel, String filePath, String remoteTargetDirectory) {

        List<String> list = Lists.newArrayList();
        list.add(filePath);
        sendFilesToRemote(connModel, list, remoteTargetDirectory);

    }

    /**
     * 拉取远程文件夹到本地
     *
     * @param connModel                the conn model
     * @param remoteDirectory          要拉取的远程目录
     * @param localTargetDirectory     保存到本地的本地目录
     * @param incloudLastRemoteDirName 是否包含远程目录的最后一个目录名称 例如 /aa/bb/cc cc
     *
     * @author zhuzz
     * @time 2019 /05/05 00:14:51
     */
    public static void pullRemoteDirToLocal(ConnModel connModel, String remoteDirectory, String localTargetDirectory, boolean incloudLastRemoteDirName) {

        remoteDirectory = PathUtil.get(remoteDirectory);
        localTargetDirectory = PathUtil.get(localTargetDirectory);

        remoteDirectory = removeLastSeparator(remoteDirectory);
        String localPath = removeLastSeparator(localTargetDirectory);

        String prefix = "";
        if (incloudLastRemoteDirName) {
            prefix = remoteDirectory.substring(remoteDirectory.lastIndexOf(PathUtil.separator));
        }

        localTargetDirectory = PathUtil.get(localTargetDirectory, prefix);
        DirStructModel remoteDirStruct = getRemoteDirStruct(connModel, remoteDirectory);

        if (CollectionUtils.isEmpty(remoteDirStruct.getDirStructModels())) {
            logger.error("empty remote dir :{}", remoteDirectory);
            return;
        }

        final String finalRemoteDirectory = remoteDirectory;
        final String finalLocalTargetDirectory = localTargetDirectory;
        exec(connModel, conn -> {
            SCPClient client = new SCPClient(conn);
            try {
                remoteDirStruct.flattenedStructChilds().forEach(p -> {
                    File localFileDir = new File(PathUtil.get(finalLocalTargetDirectory, p.getPath()));
                    if (!p.getFileFlag()) {
                        if (!localFileDir.exists()) {
                            localFileDir.mkdirs();
                        }
                    } else {
                        if (!localFileDir.getParentFile().exists()) {
                            localFileDir.getParentFile().mkdirs();
                        }
                        String remoteFilePath = PathUtil.get(finalRemoteDirectory, p.getPath());
                        try {
                            client.get(remoteFilePath, localFileDir.getParentFile().getAbsolutePath());
                        } catch (IOException e) {
                            logger.error("get files error {} ------ {}", remoteFilePath, e.getMessage());
//                            throw new RuntimeException(e);
                        }
                    }
                });

            } catch (Exception e) {
                logger.error("pull files error :{} {}", localPath, e.getMessage());
//                throw new RuntimeException(e);
            }
        });
    }


    /**
     * 拉取远程文件夹到本地
     *
     * @param connModel            the conn model
     * @param remoteDirectory      要拉取的远程目录
     * @param localTargetDirectory 保存到本地的本地目录
     *
     * @author zhuzz
     * @time 2019 /05/05 00:17:25
     */
    public static void pullRemoteDirToLocal(ConnModel connModel, String remoteDirectory, String localTargetDirectory) {
        pullRemoteDirToLocal(connModel, remoteDirectory, localTargetDirectory, false);
    }


    /**
     * 拉取文件到本地目录
     *
     * @param connModel            the conn model
     * @param files                远程文件目录集合
     * @param localTargetDirectory 本地目录
     *
     * @author zhuzz
     * @time 2019 /05/05 00:25:07
     */
    public static void pullFilesToLocal(ConnModel connModel, List<String> files, String localTargetDirectory) {

        Objects.requireNonNull(connModel);
        Objects.requireNonNull(files);
        Objects.requireNonNull(localTargetDirectory);
        String localPath = removeLastSeparator(localTargetDirectory);

        exec(connModel, conn -> {
            SCPClient client = new SCPClient(conn);
            try {

                File file = new File(localPath);
                if (!file.exists()) {
                    file.mkdirs();
                }
                client.get(files.toArray(new String[files.size()]), localPath);
            } catch (Exception e) {
                logger.error("pull files error :{} {}", localPath, e.getMessage());
                throw new RuntimeException(e);
            }
        });
    }

    public static void pullFileToLocal(ConnModel connModel, String filePath, String localTargetDirectory) {

        List<String> list = Lists.newArrayList();
        list.add(filePath);
        pullFilesToLocal(connModel, list, localTargetDirectory);
    }

    /**
     * 执行远程命令
     *
     * @param connModel the conn model
     * @param cmd       the cmd
     *
     * @return the list
     *
     * @author zhuzz
     * @time 2019 /05/05 00:42:37
     */
    public static List<String> execRemoteCommand(ConnModel connModel, String cmd) {
        List<String> list = Lists.newArrayList();
        exec(connModel, conn -> {
            try {
                Session session = conn.openSession();
                session.requestPTY("bash");
                String finalCmd = cmd;
                String encodeSet = "export LC_ALL=zh_CN.GB2312;export LANG=zh_CN.GB2312;";
//                String encodeSet = "export LC_CTYPE=zh_CN.GB18030;";
                finalCmd = encodeSet + cmd;
                session.execCommand(finalCmd);
                list.addAll(getStdoutLines(session));
            } catch (Exception ex) {
                logger.error("exec remote cmd error {}", ex.getMessage());
                throw new RuntimeException(ex);
            }
        });
        return list;
    }

    public static DirStructModel getRemoteDirStruct(ConnModel connModel, String remoteDir) {
        AtomicReference<DirStructModel> dirStructModels = new AtomicReference<>();
        exec(connModel, conn -> {
            try {
                SFTPv3Client sftPv3Client = new SFTPv3Client(conn);
                dirStructModels.set(getDirStructModel(sftPv3Client, remoteDir));
            } catch (Exception ex) {
                logger.error("file error getRemoteDirStruct : {}", ex.getMessage());
            }
        });
        return dirStructModels.get();
    }


    // region 内部私有方法

    private static Map<String, List<String>> getFilePath(String localDirPath, boolean incloudLastLocalDirName) {

        localDirPath = removeLastSeparator(localDirPath);

        Map<String, List<String>> maps = getFilePath(localDirPath, localDirPath);

        if (incloudLastLocalDirName) {
            String[] split = localDirPath.split(PathUtil.separator);
            HashMap<String, List<String>> newMaps = Maps.newHashMap();
            maps.forEach((k, v) ->
            {
                String s = split[split.length - 1];
                k = s + k;
                newMaps.put(k, v);
            });
            maps = newMaps;
        }

        return maps;

    }


    private static Map<String, List<String>> getFilePath(String localDirPath, String originPath) {

        Map<String, List<String>> map = new HashMap<>();
        File file = new File(localDirPath);
        if (file.exists() && file.isDirectory()) {
            for (File item : file.listFiles()) {
                if (item.isFile()) {
                    String key = item.getParentFile().getAbsolutePath().replace(originPath, "");
                    if (map.containsKey(key)) {
                        map.get(key).add(item.getAbsolutePath());
                    } else {
                        List<String> items = new ArrayList<>();
                        items.add(item.getAbsolutePath());
                        map.put(key, items);
                    }
                } else if (item.isDirectory()) {
                    Map<String, List<String>> curMap = getFilePath(item.getAbsolutePath(), originPath);
                    map.putAll(curMap);
                }
            }
            if (file.listFiles().length == 0) {
                String key = file.getAbsolutePath().replace(originPath, "");
                if (!originPath.equals(localDirPath)) {
                    map.put(key, Lists.newArrayList());
                }
            }
        }
        return map;
    }

    /**
     * 检查远程目录是否存在  true：存在
     *
     * @param connModel             the conn model
     * @param remoteTargetDirectory the remote target directory
     *
     * @return the boolean
     *
     * @author zhuzz
     * @time 2019 /05/07 18:56:32
     */
    public static boolean checkRemoteDirExists(ConnModel connModel, String remoteTargetDirectory) {
        Objects.requireNonNull(remoteTargetDirectory);

        AtomicReference<Boolean> hasDir = new AtomicReference<>(false);
        exec(connModel, conn -> {
            SFTPv3FileAttributes stat = null;
            try {
                SFTPv3Client sftPv3Client = new SFTPv3Client(conn);
                stat = sftPv3Client.stat(remoteTargetDirectory);
            } catch (Exception ex) {
            }
            if (!Objects.isNull(stat)) {
                hasDir.set(true);
            }
        });
        return hasDir.get();
    }

    public static boolean checkRemoteDirIsEmpty(ConnModel connModel, String remoteTargetDirectory) {
        Objects.requireNonNull(remoteTargetDirectory);
        AtomicReference<Boolean> isEmpty = new AtomicReference<>(true);
        exec(connModel, conn -> {
            try {
                SFTPv3Client sftPv3Client = new SFTPv3Client(conn);
                Vector ls = sftPv3Client.ls(remoteTargetDirectory);
                int size = ls.size();
                if (size > 2) {
                    isEmpty.set(false);
                }
            } catch (Exception ex) {
            }
        });
        return isEmpty.get();
    }

    public static boolean checkRemoteFileExists(ConnModel connModel, String remoteTargetFilePath) {
        Objects.requireNonNull(remoteTargetFilePath);
        AtomicReference<Boolean> exists = new AtomicReference<>(false);
        exec(connModel, conn -> {
            try {
                SFTPv3Client sftPv3Client = new SFTPv3Client(conn);
                SFTPv3FileAttributes lstat = sftPv3Client.lstat(remoteTargetFilePath);
                exists.set(lstat.isRegularFile());
            } catch (Exception ex) {
            }
        });
        return exists.get();
    }

    public static boolean createRemoteDir(ConnModel connModel, String remoteTargetDirectory) {
        boolean flag = checkRemoteDirExists(connModel, remoteTargetDirectory);
        AtomicReference<Boolean> created = new AtomicReference<>(false);
        if (!flag) {
            exec(connModel, conn -> {
                try {
                    SFTPv3Client sftPv3Client = new SFTPv3Client(conn);
                    sftPv3Client.mkdir(remoteTargetDirectory, 0777);
                    created.set(true);
                } catch (Exception ex) {
                    logger.error(ex.getMessage());
                    throw new RuntimeException(ex);
                }
            });
        } else {
            created.set(true);
        }
        return created.get();
    }

    private static void sendLocalDirToRemote(ConnModel connModel, Map<String, List<String>> maps, String remoteTargetDirectory) {

        Objects.requireNonNull(remoteTargetDirectory);

        exec(connModel, conn -> {
            SCPClient client = new SCPClient(conn);
            try {
                SFTPv3Client sftPv3Client = new SFTPv3Client(conn);
                maps.entrySet().forEach(p -> {

                    String newPath = remoteTargetDirectory + PathUtil.separator + p.getKey();
                    if (!StringUtils.isEmpty(p.getKey())) {
                        String[] paths = p.getKey().split(PathUtil.separator);
                        String oldPath = remoteTargetDirectory;
                        for (String path : paths) {
                            if (!StringUtils.isEmpty(path)) {
                                oldPath += PathUtil.separator + path;
                                try {
                                    SFTPv3FileAttributes stat = null;
                                    try {
                                        stat = sftPv3Client.stat(oldPath);
                                    } catch (Exception ex) {
                                        logger.error("directory not exists do create");
                                    }
                                    if (stat == null) {
                                        sftPv3Client.mkdir(oldPath, 0777);
                                    }
                                } catch (Exception ex) {
                                    logger.error("directory exists");
                                }
                            }
                        }
                    }

                    //单个上传防止0字节文件导致上传失败
                    p.getValue().parallelStream().forEach(file -> {
                        try {
                            client.put(file, newPath);
                        } catch (IOException e) {
                            logger.error("file send error :{} , {} , {}", file, remoteTargetDirectory, e.getMessage());
                        }
                    });
//                   String[] localFiles = p.getValue().toArray(new String[p.getValue().size()]);
//                   client.put(localFiles, newPath);
                    logger.error("send :" + p.getKey());
                });
            } catch (Exception ex) {
                logger.error("connection error");
                throw new RuntimeException(ex);
            }
        });


    }

    private static void exec(ConnModel connModel, Consumer<Connection> connectionConsumer) {
        Connection conn = getConn(connModel);
        try {
            connectionConsumer.accept(conn);
        } finally {
            conn.close();
        }
    }

    private static Connection getConn(ConnModel connModel) {


        Connection conn = new Connection(connModel.getIp(), connModel.getPort());
        try {

            conn.connect();

            boolean isAuthenticated = conn.authenticateWithPassword(connModel.getUserName(), connModel.getPassword());

            if (isAuthenticated == false) {
                logger.error("authentication failed");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return conn;
    }

    private static String removeLastSeparator(String dir) {
        int i = dir.lastIndexOf("/");
        if (i == dir.length() - 1) {
            dir = dir.substring(0, i);
        }
        return dir;
    }


    private static List<String> getStdoutLines(Session session) {
        List<String> lines = Lists.newArrayList();
        try {
//            InputStreamReader is = new InputStreamReader(session.getStdout(), Charset.forName("GB2312"));
            InputStreamReader is = new InputStreamReader(session.getStdout());
            BufferedReader brs = new BufferedReader(is);
            while (true) {
                String line = brs.readLine();
                if (line == null) {
                    break;
                }
                lines.add(line);
            }
        } catch (Exception ex) {
            logger.error("get stdout error {}", ex.getMessage());
        }
        return lines;
    }


    private static DirStructModel getDirStructModel(SFTPv3Client sftPv3Client, String remoteDir) {

        DirStructModel dirStructModel = new DirStructModel();
        try {
            //检测远程文件件是否存在
            SFTPv3FileAttributes stat = sftPv3Client.stat(remoteDir);
            dirStructModel.setDirStructModels(recursiveSearch(dirStructModel, sftPv3Client, remoteDir, remoteDir));
        } catch (IOException e) {
            logger.error("remote dir not exists!");
        }
        if (Objects.isNull(dirStructModel.getDirStructModels())) {
            dirStructModel.setDirStructModels(Lists.newArrayList());
        }
        return dirStructModel;
    }

    private static List<DirStructModel> recursiveSearch(DirStructModel parent, SFTPv3Client sftPv3Client, String remoteDir, String dir) throws IOException {

        Vector vector = sftPv3Client.ls(dir);
        Iterator iterator = vector.iterator();
        List<DirStructModel> result = Lists.newArrayListWithCapacity(vector.size());
        while (iterator.hasNext()) {
            Object next = iterator.next();
            if (next instanceof SFTPv3DirectoryEntry) {
                SFTPv3DirectoryEntry directoryEntry = (SFTPv3DirectoryEntry) next;
                if (".".equals(directoryEntry.filename) || "..".equals(directoryEntry.filename)) {
                    continue;
                }
                DirStructModel dirStructModel = new DirStructModel();
                dirStructModel.setName(directoryEntry.filename);
                String fileAbsolutePath = dir + PathUtil.separator + directoryEntry.filename;
                String filePath = fileAbsolutePath.substring(remoteDir.length(), fileAbsolutePath.length());
                dirStructModel.setPath(filePath);
                dirStructModel.setFileFlag(directoryEntry.longEntry.indexOf(FILEPREFIX) == 0);
                dirStructModel.setParent(parent);
                if (!dirStructModel.getFileFlag()) {
                    dirStructModel.setDirStructModels(recursiveSearch(dirStructModel, sftPv3Client, remoteDir, fileAbsolutePath));
                }
                result.add(dirStructModel);
            }
        }

        return result;
    }

    // endregion


    public static void main(String[] args) {


//        String localDirPath = "/Users/zhou/Desktop/fasdfasd";
//        Map<String, List<String>> filePath = getFilePath(localDirPath, localDirPath);
//        
//        System.out.println(JSON.toJSONString(filePath));


//        ConnModel connModel = new ConnModel("180.97.75.98", 3313, "root", "Ganqilai123!@#");
//
//        String localPath = "/Users/zhou/Documents/TestProjects/test/src/";
//        String remoteDir = "/usr/app";
//        SFTPUtils.sendLocalDirToRemote(connModel, localPath, remoteDir, true);

//        String remoteDir = "/usr/app/src";
//        String localPath = "/Users/zhou/Documents/bb";
//        pullRemoteDirToLocal(connModel, remoteDir, localPath, true);


//        String localPath = "/Users/zhou/Documents/TestProjects/test/src/";
//        String remoteDir = "/usr/app/";
//        SFTPUtils.sendLocalDirToRemote(connModel, localPath, remoteDir, true);
//        List<String> remoteDirStruct = SFTPUtils.getRemoteDirStruct(connModel, remoteDir);
//        System.out.println(JSON.toJSONString(remoteDirStruct));


//        ConnModel connModel = new ConnModel("180.97.75.98", 60122, "root", "abcd1234`!@#");

//        String remoteDir = "/app/files/390ca2dd43f51860c9a85512495092a8transform/unzip";
//        exec(connModel, conn -> {
//            try {
//                SFTPv3Client sftPv3Client = new SFTPv3Client(conn);
//                DirStructModel dirStructModel = getDirStructModel(sftPv3Client, remoteDir);
//
//                dirStructModel.flattenedStructChilds().forEach(p -> {
//
//                    System.out.println(p.getName() + "  " + p.getParent().getPath() + "   " + p.getPath());
//
//                });
//
//            } catch (Exception ex) {
//            }
//        });

//        String localPath = "/Users/zhou/Documents/temp";
//        String remotePath = "/app/files/9ec32970b664f437ae24d9ff2ff124d2transform/unzip";
//        SFTPUtils.pullRemoteDirToLocal(connModel, remotePath, localPath);

//        ConnModel connModel = new ConnModel("180.97.75.98", 60122, "admin", "abcd1234`!@#");
//
//        String filePath = "/Users/zhou/Desktop/application/Dockerfile";
//        String remoteTargetPath = "/app/files";
//
//        SFTPUtils.sendFileToRemote(connModel, filePath, remoteTargetPath);


    }


}

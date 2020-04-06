package cn.zhiu.framework.bean.core.enums;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auther: yujuan
 * @Date: 19-4-11 14:33
 * @Description:
 */
public enum FileOperationStatus {
    NONE(0, "默认状态"), NO_FOUND(1, "文件不存在"), PRE(2, "准备处理中"), DOING(3, "处理中"), SUCC(4, "成功"), FAIL(5, "失败");

    private static Logger logger = LoggerFactory.getLogger(FileOperationStatus.class);

    private static final Object _LOCK = new Object();

    private static Map<Integer, FileOperationStatus> _MAP;
    private static List<FileOperationStatus> _LIST;
    private static List<FileOperationStatus> _ALL_LIST;

    static {
        synchronized (_LOCK) {
            Map<Integer, FileOperationStatus> map = new HashMap<>();
            List<FileOperationStatus> list = new ArrayList<>();
            List<FileOperationStatus> listAll = new ArrayList<>();
            for (FileOperationStatus status : FileOperationStatus.values()) {
                map.put(status.getValue(), status);
                listAll.add(status);
            }

            _MAP = ImmutableMap.copyOf(map);
            _LIST = ImmutableList.copyOf(list);
            _ALL_LIST = ImmutableList.copyOf(listAll);
        }
    }

    private int value;
    private String name;

    FileOperationStatus(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }

    public static FileOperationStatus get(Integer value) {
        try {
            return _MAP.get(value);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    public static List<FileOperationStatus> list() {
        return _LIST;
    }

    public static List<FileOperationStatus> listAll() {
        return _ALL_LIST;
    }
}

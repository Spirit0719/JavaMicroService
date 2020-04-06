package cn.zhiu.framework.bean.core.enums;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @Auther: yujuan
 * @Date: 19-4-11 11:40
 * @Description:
 */
public enum FileOperationType {

    NONE(0, "none"),
    /**
     * Rar file operation type.
     */
    RAR(1, "rar"),
    /**
     * Unrar file operation type.
     */
    UNRAR(2, "unrar"),
    /**
     * Zip file operation type.
     */
    ZIP(3, "zip"),
    /**
     * Unzip file operation type.
     */
    UNZIP(4, "unzip"),
    /**
     * Tran 3 dtiles file operation type.
     */
    TRAN_3DTILES(5, "3dtiles"),
    /**
     * Tran gltf file operation type.
     */
    TRAN_GLTF(6, "gltf"),
    /**
     * Tran sesd file operation type.
     */
    TRAN_SESD(7, "sesd"),
    /**
     * 生成缩略图
     */
    THUMBNAIL(8, "thumbnail"),
    /**
     * 图片转OBJ
     */
    PICTURE2OBJ(9, "picture2obj");


    private static Logger logger = LoggerFactory.getLogger(FileOperationType.class);

    private static final Object _LOCK = new Object();

    private static Map<Integer, FileOperationType> _MAP;
    private static Map<String, FileOperationType> _MAP_;
    private static List<FileOperationType> _LIST;
    private static List<FileOperationType> _LOCAL_TRANSFORM__LIST;
    private static List<FileOperationType> _ALL_LIST;
    private static Map<FileOperationType, FileOperationType> _TRANSFORM_MAP_;

    static {
        synchronized (_LOCK) {
            Map<Integer, FileOperationType> map = new HashMap<>();
            Map<String, FileOperationType> map_ = new HashMap<>();
            List<FileOperationType> list = new ArrayList<>();
            List<FileOperationType> listAll = new ArrayList<>();
            _TRANSFORM_MAP_ = Maps.newHashMap();
            for (FileOperationType fileOperationType : FileOperationType.values()) {
                map.put(fileOperationType.getValue(), fileOperationType);
                map_.put(fileOperationType.name.toUpperCase(), fileOperationType);
                listAll.add(fileOperationType);
            }

            _MAP = ImmutableMap.copyOf(map);
            _MAP_ = ImmutableMap.copyOf(map_);
            _LIST = ImmutableList.copyOf(list);
            _ALL_LIST = ImmutableList.copyOf(listAll);
            _LOCAL_TRANSFORM__LIST = Lists.newArrayList(UNZIP, UNRAR);
            _TRANSFORM_MAP_.put(ZIP, UNZIP);
            _TRANSFORM_MAP_.put(RAR, UNRAR);
        }
    }

    private int value;
    private String name;

    FileOperationType(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }

    public static FileOperationType get(Integer value) {
        try {
            return _MAP.get(value);
        } catch (Exception e) {
            logger.error("convert error :{}", e.getMessage());
            return null;
        }
    }

    public static FileOperationType get(String name) {
        try {
            name = name.toUpperCase();
            FileOperationType type = _MAP_.get(name);
            if (Objects.isNull(type)) {
                String finalName = name;
                type = _ALL_LIST.stream().filter(p -> (p.getName().toUpperCase().equals(finalName.toUpperCase()) || p.toString().toUpperCase().equals(finalName))).findFirst().orElse(null);
            }
            return type;
        } catch (Exception e) {
            logger.error("convert error :{}", e.getMessage());
            return null;
        }
    }

    public static FileOperationType transformType(FileOperationType type) {
        FileOperationType fileOperationType = _TRANSFORM_MAP_.get(type);
        return fileOperationType;
    }

    /**
     * Check local transform boolean.
     *
     * @param type the type
     *
     * @return the boolean
     *
     * @author zhuzz
     * @time 2019 /05/31 11:48:42
     */
    public static boolean checkLocalTransform(FileOperationType type) {
        return _LOCAL_TRANSFORM__LIST.contains(type);
    }

    public static List<FileOperationType> list() {
        return _LIST;
    }

    public static List<FileOperationType> listAll() {
        return _ALL_LIST;
    }


}

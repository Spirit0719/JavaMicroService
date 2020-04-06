package cn.zhiu.framework.base.api.core.enums;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public enum DataTypeEnum {

    /**
     * String data type enum.
     */
    STRING(1, "string"),

    /**
     * Boolean data type enum.
     */
    BOOLEAN(2, "boolean"),

    /**
     * Number data type enum.
     */
    NUMBER(3, "number"),

    /**
     * Date data type enum.
     */
    DATE(4, "date");

    private static Logger logger = LoggerFactory.getLogger(DataTypeEnum.class);

    private static final Object _LOCK = new Object();

    private static Map<Integer, DataTypeEnum> _MAP;
    private static List<DataTypeEnum> _LIST;
    private static List<DataTypeEnum> _ALL_LIST;

    private static Map<DataTypeEnum, List<DataTypeEnum>> _TypeRelationMAP;


    static {
        synchronized (_LOCK) {
            Map<Integer, DataTypeEnum> map = new HashMap<>();
            List<DataTypeEnum> list = new ArrayList<>();
            List<DataTypeEnum> listAll = new ArrayList<>();
            for (DataTypeEnum deletePermissionExtentd : DataTypeEnum.values()) {
                map.put(deletePermissionExtentd.getValue(), deletePermissionExtentd);
                listAll.add(deletePermissionExtentd);
            }

            _MAP = ImmutableMap.copyOf(map);
            _LIST = ImmutableList.copyOf(list);
            _ALL_LIST = ImmutableList.copyOf(listAll);

            _TypeRelationMAP = Maps.newHashMap();
            _TypeRelationMAP.put(NUMBER, Lists.newArrayList(STRING, NUMBER));
            _TypeRelationMAP.put(DATE, Lists.newArrayList(STRING, DATE));
            _TypeRelationMAP.put(BOOLEAN, Lists.newArrayList(STRING, NUMBER, BOOLEAN));
            _TypeRelationMAP.put(STRING, Lists.newArrayList(STRING));

        }
    }

    private int value;
    private String name;

    DataTypeEnum(int value, String name) {
        this.value = value;
        this.name = name;
    }


    public String getName() {
        return name;
    }


    public int getValue() {
        return value;
    }


    public static DataTypeEnum get(Integer value) {
        try {
            return _MAP.get(value);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }


    public static List<DataTypeEnum> list() {
        return _LIST;
    }


    public static List<DataTypeEnum> listAll() {
        return _ALL_LIST;
    }

    public static Boolean checkDataTypeRelation(DataTypeEnum type1, DataTypeEnum type2) {
        List<DataTypeEnum> dataTypeEnums = _TypeRelationMAP.get(type1);
        return dataTypeEnums.contains(type2);

    }

}

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
 * The enum Client type.
 *
 * @author zhuzz
 * @time 2019 /04/14 22:23:28
 */
public enum ClientType {

    ZHIUWEB(1, "ZHIUWEB端"),
    ;

    private static Logger logger = LoggerFactory.getLogger(ClientType.class);

    private static final Object _LOCK = new Object();

    private static Map<Integer, ClientType> _MAP;
    private static List<ClientType> _LIST;
    private static List<ClientType> _ALL_LIST;

    static {
        synchronized (_LOCK) {
            Map<Integer, ClientType> map = new HashMap<>();
            List<ClientType> list = new ArrayList<>();
            List<ClientType> listAll = new ArrayList<>();
            for (ClientType type : ClientType.values()) {
                map.put(type.getValue(), type);
                listAll.add(type);
            }

            _MAP = ImmutableMap.copyOf(map);
            _LIST = ImmutableList.copyOf(list);
            _ALL_LIST = ImmutableList.copyOf(listAll);
        }
    }

    private int value;
    private String name;

    ClientType(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }

    public static ClientType get(Integer value) {
        try {
            return _MAP.get(value);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    public static List<ClientType> list() {
        return _LIST;
    }

    public static List<ClientType> listAll() {
        return _ALL_LIST;
    }

}

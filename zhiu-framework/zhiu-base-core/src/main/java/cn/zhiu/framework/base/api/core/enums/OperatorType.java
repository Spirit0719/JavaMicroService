package cn.zhiu.framework.base.api.core.enums;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 运算符类型
 *
 * @author zhuzz
 * @time 2019 /04/02 16:51:33
 */
public enum OperatorType {
    ALL(-1, "全部"),

    EQ(0, "等于"),

    LE(1, "小于等于"),
    GE(2, "大于等于"),

    LT(3, "小于"),
    GT(4, "大于"),

    BETWEEN(5, "BETWEEN"),

    IN(6, "IN"),

    LIKE(7, "LIKE"),
    NOTIN(8, "NOT IN"),
    NOEQ(9, "NOT EQ"),
    NOTNULL(10,"NOT NULL"),
    ISNULL(11, "IS NULL"),
    OR(12, "OR"),
    AND(13, "AND"),
    LIKE_PREFIX(14, "LIKE_PRE"),
    LIKE_SUBFIX(15, "LIKE_SUB"),
    LIKES(16, "LIKES"),

    MULTI_MATCH(17, "MULTI_MATCH"),
//    CASCADE(18,"CASCADE")

    ;

    private static Logger logger = LoggerFactory.getLogger(OperatorType.class);

    private static final Object _LOCK = new Object();

    private static Map<Integer, OperatorType> _MAP;
    private static List<OperatorType> _LIST;
    private static List<OperatorType> _ALL_LIST;

    // 单目运算符
    private static Set<OperatorType> _UNARY_SET;
    // 双目运算符
    private static Set<OperatorType> _BINARY_SET;
    // 集合运算符
    private static Set<OperatorType> _COLLECTION_SET;
    // 多字段匹配
    private static Set<OperatorType> _MULTI_MATCH_SET;

    static {
        synchronized (_LOCK) {
            Map<Integer, OperatorType> map = new HashMap<>();
            List<OperatorType> list = new ArrayList<>();
            List<OperatorType> listAll = new ArrayList<>();
            for (OperatorType type : OperatorType.values()) {
                map.put(type.getValue(), type);
                listAll.add(type);
                if (!type.equals(ALL)) {
                    list.add(type);
                }
            }

            _MAP = ImmutableMap.copyOf(map);
            _LIST = ImmutableList.copyOf(list);
            _ALL_LIST = ImmutableList.copyOf(listAll);

            _UNARY_SET = Sets.newHashSet(EQ, LE, GE, LT, GT, LIKE,NOEQ,NOTNULL,ISNULL, LIKE_PREFIX, LIKE_SUBFIX);
            _BINARY_SET = Sets.newHashSet(BETWEEN);
            _COLLECTION_SET = Sets.newHashSet(IN,NOTIN,OR,AND,LIKES);
            _MULTI_MATCH_SET = Sets.newHashSet(MULTI_MATCH);
        }
    }

    private int value;
    private String name;

    OperatorType(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }

    public static OperatorType get(int value) {
        try {
            return _MAP.get(value);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    public static List<OperatorType> list() {
        return _LIST;
    }

    public static List<OperatorType> listAll() {
        return _ALL_LIST;
    }

    public static boolean isUnary(OperatorType operatorType) {
        return _UNARY_SET.contains(operatorType);
    }

    public static boolean isMultiMatch(OperatorType operatorType) {
        return _MULTI_MATCH_SET.contains(operatorType);
    }

    public static boolean isBinary(OperatorType operatorType) {
        return _BINARY_SET.contains(operatorType);
    }

    public static boolean isCollection(OperatorType operatorType) {
        return _COLLECTION_SET.contains(operatorType);
    }
}

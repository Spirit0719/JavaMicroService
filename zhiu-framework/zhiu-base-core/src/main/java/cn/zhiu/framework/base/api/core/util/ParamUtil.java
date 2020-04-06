package cn.zhiu.framework.base.api.core.util;

import org.springframework.util.StringUtils;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * 参数工具类
 *
 * @author zhuzz
 * @time 2019 /11/12 14:38:38
 */
public class ParamUtil {

    private final static String regex = "(^[\\-0-9][0-9]*(.[0-9]+)?)$";

    /**
     * 判断是否是数字
     *
     * @param input the input
     *
     * @return the boolean
     *
     * @author zhuzz
     * @time 2019 /11/12 14:41:13
     */
    public static boolean isNumber(String input) {
        return Pattern.matches(regex, input);
    }

    /**
     * 判断输入参数是否非法
     *
     * @param input the input
     *
     * @return the boolean
     *
     * @author zhuzz
     * @time 2019 /11/18 17:00:31
     */
    public static boolean illegalParameter(Long input) {
        return Objects.isNull(input) || input < 1;
    }


    /**
     * 判断字符串是否为空
     *
     * @param params the params
     *
     * @return the boolean
     *
     * @author zhuzz
     * @time 2019 /11/14 11:24:42
     */
    public static boolean isEmptyStr(String... params) {
        if (params.length == 0) {
            return true;
        }
        for (String param : params) {
            if (StringUtils.isEmpty(param)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断字符串是否不为空
     *
     * @param params the params
     *
     * @return the boolean
     *
     * @author zhuzz
     * @time 2019 /11/14 11:26:34
     */
    public static boolean isNotEmptyStr(String... params) {
        if (params.length == 0) {
            return false;
        }
        String join = String.join("", params);
        return !StringUtils.isEmpty(join);
    }
}

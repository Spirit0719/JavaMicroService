package cn.zhiu.framework.base.api.core.util;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import org.springframework.util.CollectionUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.TreeMap;

public class MD5Util {

    // 全局数组
    private final static String[] strDigits = {"0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};

    public static String GetMD5Code(String profix, String sourceString) {
        String resultString = encoderByMd5(encoderByMd5(sourceString) + profix);
        return resultString;
    }

    public static String encoderByMd5(String buf) {
        String resultString = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            resultString = byteToString(md.digest(buf.getBytes()));
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException("md5加密错误", ex);
        }
        return resultString;

    }

    public static String encoderByMd5(byte[] bytes) {
        String resultString = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            resultString = byteToString(md.digest(bytes));
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException("md5加密错误", ex);
        }
        return resultString;

    }

    // 转换字节数组为16进制字串
    private static String byteToString(byte[] bByte) {
        StringBuffer sBuffer = new StringBuffer();
        for (int i = 0; i < bByte.length; i++) {
            sBuffer.append(byteToArrayString(bByte[i]));
        }
        return sBuffer.toString();
    }

    // 返回形式为数字跟字符串
    private static String byteToArrayString(byte bByte) {
        int iRet = bByte;
        if (iRet < 0) {
            iRet += 256;
        }
        int iD1 = iRet / 16;
        int iD2 = iRet % 16;
        return strDigits[iD1] + strDigits[iD2];
    }

    // 返回形式只为数字
    private static String byteToNum(byte bByte) {
        int iRet = bByte;
        if (iRet < 0) {
            iRet += 256;
        }
        return String.valueOf(iRet);
    }

    /**
     * Generate setting unique code string.
     *
     * @param settingMap the setting map
     *
     * @return the string
     *
     * @author zhuzz
     * @time 2019 /12/30 14:30:42
     */
    public static String generateSettingUniqueCode(Map settingMap) {

        String lastSetting = "default";

        if (CollectionUtils.isEmpty(settingMap)) {
            return encoderByMd5(lastSetting);
        }

        TreeMap treeMap = Maps.newTreeMap();
        treeMap.putAll(settingMap);

        lastSetting = JSON.toJSONString(treeMap);

        String uniqueCode = encoderByMd5(lastSetting);
        return uniqueCode;
    }

    public static void main(String[] args) {
        System.out.println(MD5Util.GetMD5Code("HXWcjvQWVG1wI4FQBLZpQ3pEdsaDFaas", "222222"));
    }


}

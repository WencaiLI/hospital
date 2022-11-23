package com.thtf.face_recognition.common.util.megvii;

/**
 * 字符串工具类
 *
 */
public class StringUtil {
    public static final String[] HEX_ARRAYS = { "0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };
    /**
     * 判断value是否是null或其length>0
     *
     * @param value
     * @return
     */
    public static Boolean isEmpty(String value) {
        return value == null || value.length() == 0;
    }
    /**
     * 判断value是否不是null且其length>0
     *
     * @param value
     * @return
     */
    public static Boolean isNotEmpty(String value) {
        return !isEmpty(value);
    }
    /**
     * trim操作
     *
     * @param value
     * @return
     */
    public static String trim(String value) {
        if (value == null) {
            return null;
        }
        return value.trim();
    }

    /**
     * byte string to hex string
     *
     * @param b
     * @return
     */
    public static String byte2hex(byte[] b) {
        if (b == null) {
            return null;
        }
        if (b.length == 0) {
            return "";
        }
        StringBuilder retBuilder = new StringBuilder();
        for (int n = 0; n < b.length; ++n) {
            retBuilder.append(HEX_ARRAYS[(b[n] & 0xF0) >> 4]);
            retBuilder.append(HEX_ARRAYS[b[n] & 0x0F]);
        }
        return retBuilder.toString();
    }

    /**
     * hex string to byte string
     *
     * @param str
     * @return
     */
    public static byte[] hex2byte(String str) {
        if (str == null) {
            return null;
        }
        str = str.trim();
        int len = str.length();
        if ((len == 0) || (len % 2 == 1)) {
            return null;
        }
        byte[] b = new byte[len / 2];
        byte tmp = 0;
        char[] strs = str.toCharArray();
        for (int i = 0; i < len; i++) {
            byte t = 0;
            if (strs[i] >= '0' && strs[i] <= '9') {
                t = (byte) (strs[i] - '0');
            } else if (strs[i] >= 'A' && strs[i] <= 'F') {
                t = (byte) (strs[i] - 'A' + 10);
            } else if (strs[i] >= 'a' && strs[i] <= 'f') {
                t = (byte) (strs[i] - 'a' + 10);
            }
            if ((i & 0x1) == 1) {
                tmp <<= 4;
                tmp += t;
                b[i / 2] = tmp;
                tmp = 0;
            } else {
                tmp = t;
            }
        }
        return b;
    }
    /**
     * 判断一个字符串是否只包含数字（10进制）字符
     *
     * @param str
     * @return
     */
    public static Boolean isNumeric(String str) {
        if (isEmpty(str)) {
            return false;
        }
        char[] arr = str.toCharArray();
        for (char c : arr) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }
    /**
     * 混淆字符串串（显示前pn个字符和后tn个字符，其余全部用*填充）
     *
     * @param source
     * @param pn
     * @param tn
     * @return
     */
    public static String mix(String source, int pn, int tn) {
        return mix(source, pn, tn, '*');
    }


    /**
     * 混淆字符串串（显示前pn个字符和后tn个字符，其余全部用mixchar填充）
     *
     * @param source
     * @param pn
     * @param tn
     * @param mixchar
     * @return
     */
    public static String mix(String source, int pn, int tn, char mixchar) {
        if (source == null || source.length() <= pn + tn) {
            return source;
        }
        int len = source.length();
        StringBuilder tmp = new StringBuilder(source.length());
        char[] mobileAs = source.toCharArray();
        for (int i = 0; i < pn; i++) {
            tmp.append(mobileAs[i]);
        }
        for (int i = 0; i < len - (pn + tn); i++) {
            tmp.append(mixchar);
        }
        for (int i = len - tn; i < len; i++) {
            tmp.append(mobileAs[i]);
        }
        return tmp.toString();
    }

    /**
     * 获取字符串长度（全角2，半角1）
     * @param value
     * @return
     */
    public static final int getLength(String value) {
        if(StringUtil.isEmpty(value)) {
            return 0;
        }
        int len = 0;
        for (char c : value.toCharArray()) {
            len++;
            if(isSbcCase(c)) {
                len++;
            }
        }
        return len;
    }

    /**
     * 判断字符是否是半角
     * @param c
     * @return
     */
    public static final Boolean isDbcCase(char c) {
        int k = 0x80;
        return c / k == 0 ? true : false;
    }

    /**
     * 判断字符是否是全角
     * @param c
     * @return
     */
    public static final Boolean isSbcCase(char c) {
        return isDbcCase(c);
    }

    /**
     * 判断是否都是数字
     * @param value
     * @return
     */
    public static final Boolean isAllDigits(String value) {
        if(StringUtil.isEmpty(value)) {
            return true;
        }
        for (char c : value.toCharArray()) {
            if(c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }

    /**
     * 在指定的字符串前面填充0，直到字符串的长度达到指定长度
     * 示例：输入6，2 --> 返回06
     * @param data 需要被填充的字符串
     * @param fillStr 需要填充的字符串
     * @param length 目标字符串的长度
     * @return 返回填充后的字符串
     */
    public static String frontFillStr(String data, String fillStr, int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length-data.length(); i++) {
            sb.append(fillStr);
        }
        return sb.append(data).toString();
    }
}

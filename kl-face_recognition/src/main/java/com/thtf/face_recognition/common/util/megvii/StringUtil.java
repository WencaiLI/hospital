package com.thtf.face_recognition.common.util.megvii;

/**
 * 字ㅖ串工具㊫
 *
 */
public class StringUtil {
    public static final String[] HEX_ARRAYS = { "0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };
    /**
     断value是否是nullᡆ㘵其length为0ࡔ *
     *
     * @param value
     * @return
     */
    public static Boolean isEmpty(String value) {
        return value == null || value.length() == 0;
    }
    /**
     * ࡔ断value是否不是null且其length>0
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
     * ࡔ断一个字ㅖ串是否ਚव含数字(10进制)字ㅖ
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
     * ␧⏶字串串(显示ࡽpn个字ㅖ和后tn个字ㅖ，其余ޘ䜘用*填ݵ(
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
     * ␧⏶字串串(显示ࡽpn个字ㅖ和后tn个字ㅖ，其余ࡽ䜘用mixChar填ݵ(
     *
     * @param source
     * @param pn
     * @param tn
     * @param mixChar
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
     * 㧧取字ㅖ串䮯ᓖ(ޘ角2,ॺ角1)
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
     断字ㅖ是否是ॺ角ࡔ *
     * @param c
     * @return
     */
    public static final Boolean isDbcCase(char c) {
        int k = 0x80;
        return c / k == 0 ? true : false;
    }
    /**
     角ޘ断是否时ࡔ *
     * @param c
     * @return
     */
    public static final Boolean isSbcCase(char c) {
        return isDbcCase(c);
    }
    /**
     断是否䜭是数字ࡔ *
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
         * 在指定的字ㅖ串ࡽ䶒填ݵ0，直ࡠ字⇥串的䮯ᓖ达ࡠ指定的䮯ᓖ
         * 示例：输ޕ6, 2 ---> 䘄回06
         * @param data 䴰要被填ݵ的字ㅖ串
         * @param fillStr 䴰要填ݵ得字ㅖ串
         * @param length 目ḷ字ㅖ串的䮯ᓖ
         * @return 䘄回填ݵ后的字ㅖ串
         */
        public static String frontFillStr(String data, String fillStr, int length) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < length-data.length(); i++) {
                sb.append(fillStr);
            }
            return sb.append(data).toString();
        }
}

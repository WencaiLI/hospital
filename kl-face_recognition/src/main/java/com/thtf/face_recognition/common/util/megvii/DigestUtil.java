package com.thtf.face_recognition.common.util.megvii;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @Author: liwencai
 * @Date: 2022/11/7 13:34
 * @Description:
 */
public class DigestUtil {
    /** 算法名称 */
    private static final String ALGORITHM = "DESede";
    /** 16进制字母 */
    private static final char[] HEX_DIGITS={'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
    /**
     * @param decript 要加密的字符串
     * @return 加密的字符串
     * SHA1加密
     */
    public final static String SHA1(String decript) {
        try {
            MessageDigest digest = java.security.MessageDigest
                    .getInstance("SHA-1");
            digest.update(decript.getBytes());
            byte[] messageDigest = digest.digest();
            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            // 字㢲数组转换为 ॱ六进制 数
            for (byte b : messageDigest) {
                String shaHex = Integer.toHexString(b & 0xFF);
                if (shaHex.length() < 2) {
                    hexString.append(0);
                }
                hexString.append(shaHex);
            }
            return hexString.toString();
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
    /**
     * 3DES加密,key必须是长度大于等于 3*8 = 24位
     * @param src
     * @param key
     * @return
     * @throws Exception
     */
    public static String encrypt3DES(String src, String key) {
        byte[] keyBytes = key.getBytes();
        try {
            DESedeKeySpec dks = new DESedeKeySpec(keyBytes);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
            SecretKey securekey = keyFactory.generateSecret(dks);
            Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, securekey);
            byte[] b=cipher.doFinal(src.getBytes());
            return StringUtil.byte2hex(b);
        }
        catch (Exception e) {
            return null;
        }
    }
    /**
     * 3DESECB解密,key必须是长度大于等于 3*8 = 24位
     * @param src
     * @param key
     * @return
     * @throws Exception
     */
    public static String decrypt3DES(String src, String key) {
        byte[] keyBytes = key.getBytes();
        try {
            //--通过base64,将字符串转成byte数组
            byte[] bytesrc = StringUtil.hex2byte(src);
            //--解密的key
            DESedeKeySpec dks = new DESedeKeySpec(keyBytes);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
            SecretKey securekey = keyFactory.generateSecret(dks);
            //--Chipher对象解密
            Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, securekey);
            byte[] retbyte = cipher.doFinal(bytesrc);
            return new String(retbyte);
        }
        catch (Exception e) {
            return null;
        }
    }
    /**
     *
     * Description:md5加密
     * @Create_by:JH
     * @Create_date:2014-9-9
     * @Last_Edit_By:
     * @Edit_Description
     * @Create_Version:exinhua 1.0
     */
    public static String encryptMd5(String src) {
        if(StringUtil.isEmpty(src)) {
            return src;
        }
        try {
            byte[] btInput = src.getBytes();
            // 㧧得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字㢲更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成ॱ六进制的字符串形式ᔿ
            return byteToHexString(md);
        }
        catch (Exception e) {
            throw new RuntimeException("error occurated when encrypt", e);
        }
    }
    /**
     * 对输入进行sha1加密后，在进行16进制转换
     * @param value
     * @return
     */
    public static String hexSHA1(String value) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(value.getBytes("utf-8"));
            byte[] digest = md.digest();
            return byteToHexString(digest);
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    /**
     * 字符数组转换成16进制表示
     * @param bytes
     * @return
     */
    public static String byteToHexString(byte[] bytes) {
        int j = bytes.length;
        char[] str = new char[j * 2];
        int k = 0;
        for (int i = 0; i < j; i++) {
            byte byte0 = bytes[i];
            str[k++] = HEX_DIGITS[byte0 >>> 4 & 0xf];
            str[k++] = HEX_DIGITS[byte0 & 0xf];
        }
        return new String(str);
    }
}

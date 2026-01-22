package com.example.webapp.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;

/**
 * AES加密，与Nodejs 保持一致
 *
 * nodejs aes加密
 * encryptUtils.aesEncrypt = function(data, secretKey) {
 * 	var cipher = crypto.createCipher('aes-128-ecb',secretKey);
 * 	return cipher.update(data,'utf8','hex') + cipher.final('hex');
 * }
 *
 * nodejs aes解密
 * encryptUtils.aesDecrypt = function(data, secretKey) {
 * 	var cipher = crypto.createDecipher('aes-128-ecb',secretKey);
 * 	return cipher.update(data,'hex','utf8') + cipher.final('utf8');
 * }
 */
public class AESForNodejs {
    public static final String DEFAULT_CODING = "utf-8";
    public static final String SEED = "M2sTki0cQJtpDFamb";

    /**
     * 解密
     * @param encrypted
     * @param seed
     * @return
     * @throws Exception
     */
    public static String decrypt(String encrypted, String seed) throws Exception {
        byte[] keyb = seed.getBytes(DEFAULT_CODING);
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] thedigest = md.digest(keyb);
        SecretKeySpec skey = new SecretKeySpec(thedigest, "AES");
        Cipher dcipher = Cipher.getInstance("AES");
        dcipher.init(Cipher.DECRYPT_MODE, skey);

        byte[] clearbyte = dcipher.doFinal(toByte(encrypted));
        return new String(clearbyte);
    }

    /**
     * 加密
     * @param content
     * @param seed
     * @return
     * @throws Exception
     */
    public static String encrypt(String content, String seed) throws Exception {
        byte[] input = content.getBytes(DEFAULT_CODING);

        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] thedigest = md.digest(seed.getBytes(DEFAULT_CODING));
        SecretKeySpec skc = new SecretKeySpec(thedigest, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, skc);

        byte[] cipherText = new byte[cipher.getOutputSize(input.length)];
        int ctLength = cipher.update(input, 0, input.length, cipherText, 0);
        ctLength += cipher.doFinal(cipherText, ctLength);

        return parseByte2HexStr(cipherText);
    }

    /**
     * 字符串转字节数组
     * @author lmiky
     * @date 2014-2-25
     * @param hexString
     * @return
     */
    private static byte[] toByte(String hexString) {
        int len = hexString.length() / 2;
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++) {
            result[i] = Integer.valueOf(hexString.substring(2 * i, 2 * i + 2), 16).byteValue();
        }
        return result;
    }

    /**
     * 字节转16进制数组
     * @author lmiky
     * @date 2014-2-25
     * @param buf
     * @return
     */
    private static String parseByte2HexStr(byte buf[]) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex);
        }
        return sb.toString();
    }



}

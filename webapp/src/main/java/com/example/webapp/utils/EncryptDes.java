package com.example.webapp.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.Security;

/**
 * EncryptDes 对称加密
 * EncryptRsa 非对称加密
 */
@Slf4j
public class EncryptDes {
    public static final String DEFAULT_KEY = "national";
    /** 默认密钥*/
    public static final String KEY = "B-yjuiaanng";
    /** 非密钥*/
    private static final String SER_KEY = "DES";
    public static final int INT_2 = 2;
    private static Cipher encryptCipher = null;
    private static Cipher decryptCipher = null;
    static {
        try {
            encryptCipher = Cipher.getInstance(SER_KEY);
            decryptCipher = encryptCipher;
        } catch (NoSuchAlgorithmException e) {
            log.error("{}", ExceptionUtils.getStackTrace(e));
        } catch (NoSuchPaddingException e) {
            log.error("{}", ExceptionUtils.getStackTrace(e));
        }
    }

    /**
     * 将byte数组转换为表示16进制值的字符串， 如：byte[]{8,18}转换为：0813， 和public static byte[]
     * hexStr2ByteArr(String strIn) 互为可逆的转换过程
     *
     * @param arrB
     *            需要转换的byte数组
     * @return 转换后的字符串
     * @throws Exception
     *             本方法不处理任何异常，所有异常全部抛出
     */
    public static String byteArr2HexStr(byte[] arrB) throws Exception {
        int iLen = arrB.length;
        // 每个byte用两个字符才能表示，所以字符串的长度是数组长度的两倍
        StringBuffer sb = new StringBuffer(iLen * 2);
        for (int i = 0; i < iLen; i++) {
            int intTmp = arrB[i];
            // 把负数转换为正数
            while (intTmp < 0) {
                intTmp = intTmp + 256;
            }
            // 小于0F的数需要在前面补0
            if (intTmp < 16) {
                sb.append("0");
            }
            sb.append(Integer.toString(intTmp, 16));
        }
        return sb.toString();
    }

    /**
     * 将表示16进制值的字符串转换为byte数组， 和public static String byteArr2HexStr(byte[] arrB)
     * 互为可逆的转换过程
     *
     * @param strIn
     *            需要转换的字符串
     * @return 转换后的byte数组
     * @throws Exception
     *             本方法不处理任何异常，所有异常全部抛出
     * @author <a href="mailto:leo841001@163.com">LiGuoQing</a>
     */
    public static byte[] hexStr2ByteArr(String strIn) throws Exception {
        byte[] arrB = strIn.getBytes();
        int iLen = arrB.length;

        // 两个字符表示一个字节，所以字节数组长度是字符串长度除以2
        byte[] arrOut = new byte[iLen / INT_2];
        for (int i = 0; i < iLen; i = i + INT_2) {
            String strTmp = new String(arrB, i, INT_2);
            arrOut[i / INT_2] = (byte) Integer.parseInt(strTmp, 16);
        }
        return arrOut;
    }


    /**
     * 指定密钥构造方法
     *
     * @param strKey
     *            指定的密钥
     * @throws Exception
     */
    @Deprecated
    public EncryptDes(String strKey) {

    }

    /**
     * 加密字节数组
     *
     * @param arrB
     *            需加密的字节数组
     * @return 加密后的字节数组
     * @throws Exception
     */
    public static byte[] encrypt(byte[] arrB) throws Exception {
        return encryptCipher.doFinal(arrB);
    }

    /**
     * 加密字符串
     *
     * @param strIn
     *            需加密的字符串
     * @return 加密后的字符串
     * @throws Exception
     */
    public static String encrypt(String strIn){
        try {
            Security.addProvider(new com.sun.crypto.provider.SunJCE());
            Key key = getKey(EncryptDes.KEY.getBytes());
            encryptCipher.init(Cipher.ENCRYPT_MODE, key);
            return byteArr2HexStr(encrypt(strIn.getBytes()));
        }catch (Exception e){
            log.error("{}", ExceptionUtils.getStackTrace(e));
        }
        return "";
    }
    /**
     * 加密字符串
     *
     * @param strIn 需加密的字符串
     * @param keyStr
     * @return 加密后的字符串
     * @throws Exception
     */
    public static String encrypt(String strIn,String keyStr){
        try {
            Security.addProvider(new com.sun.crypto.provider.SunJCE());
            Key key = getKey(keyStr.getBytes());
            encryptCipher.init(Cipher.ENCRYPT_MODE, key);
            return byteArr2HexStr(encrypt(strIn.getBytes()));
        }catch (Exception e){
            log.error("{}", ExceptionUtils.getStackTrace(e));
        }
        return "";
    }

    /**
     * 解密字节数组
     *
     * @param arrB
     *            需解密的字节数组
     * @return 解密后的字节数组
     * @throws Exception
     */
    public static byte[] decrypt(byte[] arrB) throws Exception {
        return decryptCipher.doFinal(arrB);
    }

    /**
     * 解密字符串
     *
     * @param strIn
     *            需解密的字符串
     * @return 解密后的字符串
     * @throws Exception
     */
    public static String decrypt(String strIn){
        try {
            Security.addProvider(new com.sun.crypto.provider.SunJCE());
            Key key = getKey(EncryptDes.KEY.getBytes());
            decryptCipher.init(Cipher.DECRYPT_MODE, key);
            return new String(decrypt(hexStr2ByteArr(strIn)));
        } catch (Exception e) {
            log.error("{}", ExceptionUtils.getStackTrace(e));
        }
        return "";
    }
    /**
     * 解密字符串
     *
     * @param strIn 需解密的字符串
     * @param keyStr
     * @return 解密后的字符串
     * @throws Exception
     */
    public static String decrypt(String strIn,String keyStr){
        try {
            Security.addProvider(new com.sun.crypto.provider.SunJCE());
            Key key = getKey(keyStr.getBytes());
            decryptCipher.init(Cipher.DECRYPT_MODE, key);
            return new String(decrypt(hexStr2ByteArr(strIn)));
        } catch (Exception e) {
            log.error("{}", ExceptionUtils.getStackTrace(e));
        }
        return "";
    }

    /**
     * 从指定字符串生成密钥，密钥所需的字节数组长度为8位 不足8位时后面补0，超出8位只取前8位
     *
     * @param arrBtmp
     *            构成该字符串的字节数组
     * @return 生成的密钥
     * @throws Exception
     */
    private static Key getKey(byte[] arrBtmp) throws Exception {
        // 创建一个空的8位字节数组（默认值为0）
        byte[] arrB = new byte[8];

        // 将原始字节数组转换为8位
        for (int i = 0; i < arrBtmp.length && i < arrB.length; i++) {
            arrB[i] = arrBtmp[i];
        }

        // 生成密钥
        Key key = new javax.crypto.spec.SecretKeySpec(arrB, "DES");

        return key;
    }


    /**
     * 1-加密，默认密钥
     * @param str 加密字符串
     * @return
     */
    public static String encryptString(String str) {
        /*加密后的字符*/
        String jmc= EncryptDes.encrypt(str);
        return jmc;
    }

    /**
     * 1-加密,指定密钥
     * @param str 加密字符串
     * @param key 密钥
     * @return
     */
    public static String encryptString(String str,String key) {
        /*加密后的字符*/
        String jmc= EncryptDes.encrypt(str,key);
        return jmc;
    }
    /**
     * 2-解密,默认密钥
     * @param str 加密字符串
     * @return
     */
    public static String dencryptString(String str) {
        String info = EncryptDes.decrypt(str);
        return info;
    }
    /**
     * 2-解密,指定密钥
     * @param str 加密字符串
     * @return
     */
    public static String dencryptString(String str,String key) {
        String info = EncryptDes.decrypt(str,key);
        return info;
    }
}

package com.example.webapp.utils;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.math.BigDecimal;

/**
 * @Description:sid 加密
 */
public class EncryptUtil {

    private static final Log logger = LogFactory.getLog(EncryptUtil.class);

    private static final byte[] DESKEY = {64, -77, 35, -45, 16, -22, 121, -15};
    private static final String SYS_DECRYPT_SSOID = "7008";
    public static final int INT_2 = 2;
    public static final String SSO = "sso/";
    public static final String DELIMITER_ONE_PERIOD = ".";
    public static final String DELIMITER_ONE_SLASH = "/";
    public static final String NONE = "none";
    /**SID加密字段分隔符*/
    public static int SID_ENCRYPT_SPLIT = 0x01;
    private EncryptUtil(){}

    /**
     * 生成SID
     * @param userId
     * @return
     */
    public static final String generateSid(String userId) {
        long loginTime = System.currentTimeMillis();
        String rawSid = userId + "\1" +0+"\1"+loginTime;
        return EncryptUtil.encryptString(rawSid);
    }

    public static final String generateSid(String userId,String appId) {
        long loginTime = System.currentTimeMillis();
        String rawSid = userId + "\1" +appId+"\1"+loginTime;
        return EncryptUtil.encryptString(rawSid);
    }

    /**
     * 获取userId
     * @param sid
     * @return
     */
    public static final String getUserIdBySid(String sid) {
        if(StringUtils.isBlank(sid) || NONE.equals(sid)){
            return "0";
        }else if(NumberUtils.isDigits(sid)){
            return sid;
        }else {
            String decryptUserId=decryptString(sid);
            if(decryptUserId.indexOf(SID_ENCRYPT_SPLIT)<0 ||
                    decryptUserId.indexOf(SID_ENCRYPT_SPLIT) == decryptUserId.lastIndexOf(SID_ENCRYPT_SPLIT)){
                throw new RuntimeException("sid decrypt failed!");
            }
            String userId=decryptUserId.substring(0,decryptUserId.indexOf(SID_ENCRYPT_SPLIT));
            return userId;
        }
    }

    /**
     * 获取appid
     * @param sid
     * @return
     */
    public static final String getAppIdBySid(String sid) {
        String decryptUserId=decryptString(sid);
        if(decryptUserId.indexOf(SID_ENCRYPT_SPLIT)<0 ||
                decryptUserId.indexOf(SID_ENCRYPT_SPLIT) == decryptUserId.lastIndexOf(SID_ENCRYPT_SPLIT)){
            throw new RuntimeException("util: sid decrypt failed");
        }
        String appId=decryptUserId.substring(decryptUserId.indexOf(SID_ENCRYPT_SPLIT)+1,decryptUserId.lastIndexOf(SID_ENCRYPT_SPLIT));
        return appId;
    }

    /**
     * 获取登录时间loginTime
     * @param sid
     * @return
     */
    public static final String getLoginTimeBySid(String sid) {
        String decryptUserId=decryptString(sid);
        if(decryptUserId.indexOf(SID_ENCRYPT_SPLIT)<0 ||
                decryptUserId.indexOf(SID_ENCRYPT_SPLIT) == decryptUserId.lastIndexOf(SID_ENCRYPT_SPLIT)){
            throw new RuntimeException("util: sid decrypt failed");
        }
        String loginTime=decryptUserId.substring(decryptUserId.lastIndexOf(SID_ENCRYPT_SPLIT)+1);
        return loginTime;
    }

    /**
     * Base64加密
     * @param str
     * @return
     */
    public static String encryptBase64code(String str) {
        byte[] b = Base64.encodeBase64(str.getBytes(), true);
        String encrypt= new String(b);
        return encrypt.trim();
    }
    /**
     * Base64解密
     * @param str
     * @return
     */
    public static String dencryptBase64code(String str) {
        byte[] b = Base64.decodeBase64(str.getBytes());
        return new String(b);
    }

    /**
     * @param content
     * @return
     * @throws
     * @title: md5
     * @description: 小写，32bits，MD5
     */
    public static final String md5(String content) {
        return DigestUtils.md5Hex(content);
    }


    /**
     * @param content
     * @return
     * @throws
     * @title: MD5
     * @description:返回大写的md5值
     */
    public static final String md5ToUpperCase(String content) {
        return DigestUtils.md5Hex(content).toUpperCase();
    }


    public static final String encryptString(String value, byte[] desKey) {
        // 用密钥加密明文
        String tmp = value;
        try {
            Cipher c1 = Cipher.getInstance("DES");
            DESKeySpec dks = new DESKeySpec(desKey);
            // 创建一个密匙工厂，然后用它把DESKeySpec转换成
            // 一个SecretKey对象
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey key = keyFactory.generateSecret(dks);
            c1.init(Cipher.ENCRYPT_MODE, key);
            byte[] cipherByte = c1.doFinal(value.getBytes());
            tmp = encodeHex(cipherByte);
        } catch (Exception e) {
            logger.error(e);
        }
        return tmp;
    }

    public static final String encryptSsoId(String userName, String password) {
        // 用密钥加密明文
        String tmp = userName + '\002' + password;
        try {

            Cipher c1 = Cipher.getInstance("DES");

            DESKeySpec dks = new DESKeySpec(DESKEY);
            // 创建一个密匙工厂，然后用它把DESKeySpec转换成
            // 一个SecretKey对象
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey key = keyFactory.generateSecret(dks);

            c1.init(Cipher.ENCRYPT_MODE, key);
            byte[] cipherByte = c1.doFinal(tmp.getBytes());

            tmp = encodeHex(cipherByte);
        } catch (Exception e) {
            logger.error(e);
        }
        return tmp;
    }

    public static final int decryptSsoIdUserId(String ssoId) {
        String deInfo = decryptString(ssoId);
        int pos = deInfo.indexOf('\002');
        if (pos != -1) {
            int userId = getInt(deInfo.substring(0, pos));
            if (userId > 0) {
                return userId;
            }
        }
        return 0;
    }

    public static final String decryptSsoIdPass(String ssoId) {
        String deInfo = decryptString(ssoId);
        int pos = deInfo.indexOf('\002');
        if (pos != -1) {
            return deInfo.substring(pos + 1);
        }
        return "";
    }

    public static final String encryptString(String value) {
        // 用密钥加密明文
        String tmp = value;
        try {

            Cipher c1 = Cipher.getInstance("DES");

            DESKeySpec dks = new DESKeySpec(DESKEY);
            // 创建一个密匙工厂，然后用它把DESKeySpec转换成
            // 一个SecretKey对象
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey key = keyFactory.generateSecret(dks);

            c1.init(Cipher.ENCRYPT_MODE, key);
            byte[] cipherByte = c1.doFinal(value.getBytes());

            tmp = encodeHex(cipherByte);
        } catch (Exception e) {
            logger.error(e);
        }
        return tmp;
    }

    public static final String decryptString(String source) {
        // 用密钥解密密文
        String tmp = source;
        try {
            byte[] bytes = decodeHex(tmp);

            DESKeySpec dks = new DESKeySpec(DESKEY);
            // 创建一个密匙工厂，然后用它把DESKeySpec转换成
            // 一个SecretKey对象
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey key = keyFactory.generateSecret(dks);
            Cipher c1 = Cipher.getInstance("DES");
            c1.init(Cipher.DECRYPT_MODE, key);
            byte[] cipherByte = c1.doFinal(bytes);

            tmp = new String(cipherByte);

        } catch (Exception e) {
            logger.error(e);
        }
        return tmp;
    }

    public static final String decryptString(String source, byte[] desKey) {
        // 用密钥解密密文
        String tmp = source;
        try {
            byte[] bytes = decodeHex(tmp);
            DESKeySpec dks = new DESKeySpec(desKey);
            // 创建一个密匙工厂，然后用它把DESKeySpec转换成
            // 一个SecretKey对象
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey key = keyFactory.generateSecret(dks);
            Cipher c1 = Cipher.getInstance("DES");
            c1.init(Cipher.DECRYPT_MODE, key);
            byte[] cipherByte = c1.doFinal(bytes);
            tmp = new String(cipherByte);
        } catch (Exception e) {
            logger.error(e);
        }
        return tmp;
    }

    /**
     * Turns an array of bytes into a String representing each byte as an
     * unsigned hex number.
     * <p>
     * Method by Santeri Paavolainen, Helsinki Finland 1996<br>
     * (c) Santeri Paavolainen, Helsinki Finland 1996<br>
     * Distributed under LGPL.
     *
     * @param bytes an array of bytes to convert to a hex-string
     * @return generated hex string
     */
    public static final String encodeHex(byte[] bytes) {
        StringBuffer buf = new StringBuffer(bytes.length * 2);
        int i;
        for (i = 0; i < bytes.length; i++) {
            if (((int) bytes[i] & 0xff) < 0x10) {
                buf.append("0");
            }
            buf.append(Long.toString((int) bytes[i] & 0xff, 16));
        }
        return buf.toString();
    }

    /**
     * Turns a hex encoded string into a byte array. It is specifically meant to
     * "reverse" the toHex(byte[]) method.
     *
     * @param hex a hex encoded String to transform into a byte array.
     * @return a byte array representing the hex String[
     */
    public static final byte[] decodeHex(String hex) {
        char[] chars = hex.toCharArray();
        byte[] bytes = new byte[chars.length / INT_2];
        int byteCount = 0;
        for (int i = 0; i < chars.length; i += INT_2) {
            int newByte = 0x00;
            newByte |= hexCharToByte(chars[i]);
            newByte <<= 4;
            newByte |= hexCharToByte(chars[i + 1]);
            bytes[byteCount] = (byte) newByte;
            byteCount++;
        }
        return bytes;
    }

    /**
     * 生成自动登录参数名及值
     *
     * @param ssoHost  sso域名
     * @param nextPage 跳转地址
     * @param userName 用户名
     * @param isLimit  是否有踢人限制：true:支持踢人 false:无限制登录
     * @return
     */
    public static final String genAutoLoginParams(String ssoHost, String userName, String nextPage, boolean isLimit) {
        StringBuilder sb = new StringBuilder();
        sb.append("userName=")
                .append(userName)
                .append("&t=").append(System.currentTimeMillis())
                .append("&next_page=" + nextPage);
        if (!isLimit) {
            //不踢人
            sb.append("&lim=0");
        }
        String enValue = encryptString(sb.toString());
        if (!ssoHost.endsWith(DELIMITER_ONE_SLASH)) {
            ssoHost += DELIMITER_ONE_SLASH;
        }
        if (!ssoHost.endsWith(SSO)) {
            ssoHost += SSO;
        }
        String url = ssoHost + "autoLogin.do?request=" + enValue;
        return url;
    }

    /**
     * Returns the the byte value of a hexadecmical char (0-f). It's assumed
     * that the hexidecimal chars are lower case as appropriate.
     *
     * @param ch a hexedicmal character (0-f)
     * @return the byte value of the character (0x00-0x0F)
     */
    private static final byte hexCharToByte(char ch) {
        switch (ch) {
            case '0':
                return 0x00;
            case '1':
                return 0x01;
            case '2':
                return 0x02;
            case '3':
                return 0x03;
            case '4':
                return 0x04;
            case '5':
                return 0x05;
            case '6':
                return 0x06;
            case '7':
                return 0x07;
            case '8':
                return 0x08;
            case '9':
                return 0x09;
            case 'a':
                return 0x0A;
            case 'b':
                return 0x0B;
            case 'c':
                return 0x0C;
            case 'd':
                return 0x0D;
            case 'e':
                return 0x0E;
            case 'f':
                return 0x0F;
            default:
                return 0x00;
        }
    }
    /**
     * 得到 int
     *
     * @param obj
     * @return
     */
    public static int getInt(Object obj) {
        if (obj instanceof Integer) {
            return (Integer) obj;
        }
        if (obj instanceof BigDecimal) {
            BigDecimal bigDecimal = (BigDecimal) obj;
            return bigDecimal.intValue();
        }
        if (obj instanceof Double) {
            Double d = (Double) obj;
            return d.intValue();
        }

        if (obj instanceof String) {
            String str = (String) obj;
            if (StringUtils.isBlank(str)) {
                return 0;
            }
            str = str.trim();
            try {
                if (str.indexOf(DELIMITER_ONE_PERIOD) != -1) {
                    Double d = Double.parseDouble(str);
                    return d.intValue();
                }
                return Integer.parseInt(str);
            } catch (NumberFormatException e) {
                logger.error(e);
            }
            return 0;
        }

        if (obj instanceof Boolean) {
            boolean b = (Boolean) obj;
            if (b) {
                return 1;
            }
            return 0;
        }

        return 0;
    }
}

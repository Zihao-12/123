package com.example.webapp.utils;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

/**
 * 前端根据后端生成的公钥进行rsa加密
 * RSAEncrypt 非对称加密:RSA   0< 加密明文字节数 <= 密钥字节-11      message.getBytes().length
 *            utf8: 汉字3个字节 英文数字字母1个字节
 * @author: gehaisong
 **/
public class EncryptRsa {
    public static final String DEFAULT_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCyPVUuiulUWbHdK6lgzGIy9JX1d2XK33yn6Bn2oa8LtYjS6ltfJFcGPJz1rn5djetm8sYGMP4/L6mIEZdLmEb0sOEzL3lP3hK3OsE/TofU3/0o5vHqu0Wkj4IVWeM2A6Nu7ZcPJgVmeM9WUtWzW+nCjwsoBPUQcJLl+l94hxsLRQIDAQAB";
    public static final String DEFAULT_PRIAVTE_KEY = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBALI9VS6K6VRZsd0rqWDMYjL0lfV3ZcrffKfoGfahrwu1iNLqW18kVwY8nPWufl2N62byxgYw/j8vqYgRl0uYRvSw4TMveU/eErc6wT9Oh9Tf/Sjm8eq7RaSPghVZ4zYDo27tlw8mBWZ4z1ZS1bNb6cKPCygE9RBwkuX6X3iHGwtFAgMBAAECgYB9pi7+eH60L+bq+3SUAIDZWwK74EeqFJJT65MiAijcIEUsKnnvQDb49pHPwArk+L8R++MNSkxxTrA8c+osKFUbytqrwE6C69a6AY1D+kwEmd7wuXZb6RxCIRMmZPcj8dlot0izJ8KE4E2/Acf5X06tDw7xyMQ1GuuytgmyTwwDAQJBAO84xSgN7GEvph1Vc08ne3oR6vlmL3o+sqyurF2UxXPcUxxjbr3sx2t9RHUtnXFrRgxMI95PX2ssAQuaRul3mLkCQQC+vaB1edJuKR5eJT0gqt9T5tbFp6EDoWna5+/3fProLzkovlzkGOfpzGvx66t8bMZgGl+lyz/8qieMsKrlU+jtAkAI4nDgFfRLf7uJp1zK+cpqNDQn4xWsUpvMMgiQyS+0J+CmU0MPprWZVmOufm00gyls138ViJkgn9fCGI69qQVxAkBBTSBzN6rQZilsZ+lelsFwsxYSg0o/uPJJrKBeJZ6tAQXbqhiZSuGTDJZkM5/5MDBWVJEsbMJSWVrC8vT/IFQdAkBH9v8VVV4hCdII4qKaWSt5thAZckIYh3gFEC3+f/R31WCc2ZESmH5EPbjcv2/MVkaIPOKER96XBx4QRvhQgOT1";
    public static final int PUBLIC_KEY_INDEX = 0;
    public static final int PRIVATE_KEY_INDEX = 1;
    /** 默认密钥 128字节，可加密明文117字节 */
    /**
     * RSA密钥长度
     * 默认1024位，128字节,可加密明文117字节
     * 密钥长度必须是64的倍数，
     * 范围在512至65536位之间。
     */
    public static final int SECRET_KEY_LENGTH =5760;
    public static final String RSA = "RSA";
    /** 用于封装随机产生的公钥与私钥 */
    private static Map<Integer, String> keyMap = new HashMap<Integer, String>();
    public static void main(String[] args) throws Exception {
        //生成公钥和私钥
        genKeyPair();
        //DEFAULT_PUBLIC_KEY;
        String publicKey =keyMap.get(PUBLIC_KEY_INDEX);
        //DEFAULT_PRIAVTE_KEY;
        String privateKey =keyMap.get(PRIVATE_KEY_INDEX);
        //加密字符串
        String  message = "哈哈生成的公钥为钥为随机生成的公钥为钥为随机生成的公钥为钥为随机生成的公钥为钥为随机生成的公钥为钥为随机生成的公钥为钥为随机生成的公钥为钥为随机生成的公钥为钥为随机生成的公钥为钥为随机生成的公钥为钥为随机生成的公钥为钥为随机生成的公钥为钥为随机生成的公钥为钥为随机生成的公钥为钥为随机生成的公钥为钥为随机生成的公钥为钥为随机生成的公钥为钥为随机生成的公钥为钥为随机生成的公钥为钥为随机生成的公钥为钥为交警";
        byte[] d = message.getBytes();
        System.out.println("随机生成的公钥为:" +publicKey );
        System.out.println("随机生成的私钥为:" + privateKey);
        long b = System.currentTimeMillis();
        String messageEn = encrypt(message,publicKey);
        System.out.println("加密耗时"+(System.currentTimeMillis() - b));
        System.out.println(message + "\t加密后的字符串为:" + messageEn);
        long aa = System.currentTimeMillis();
        String messageDe = decrypt(messageEn,privateKey);
        System.out.println("解密=密耗时"+(System.currentTimeMillis() - aa));
        System.out.println("还原后的字符串为:" + messageDe);
    }

    /**
     * 随机生成密钥对
     * @throws NoSuchAlgorithmException
     */
    public static void genKeyPair() throws NoSuchAlgorithmException {
        // KeyPairGenerator类用于生成公钥和私钥对，基于RSA算法生成对象
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(RSA);
        // 初始化密钥对生成器，密钥大小为96-1024位  ,
        keyPairGen.initialize(SECRET_KEY_LENGTH,new SecureRandom());
        // 生成一个密钥对，保存在keyPair中
        KeyPair keyPair = keyPairGen.generateKeyPair();
        // 得到私钥
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        // 得到公钥
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        String publicKeyString = new String(Base64.encodeBase64(publicKey.getEncoded()));
        // 得到私钥字符串
        String privateKeyString = new String(Base64.encodeBase64((privateKey.getEncoded())));
        // 将公钥和私钥保存到Map
        //0表示公钥
        keyMap.put(PUBLIC_KEY_INDEX,publicKeyString);
        //1表示私钥
        keyMap.put(PRIVATE_KEY_INDEX,privateKeyString);
    }
    /**
     * RSA公钥加密
     *
     * @param str
     *            加密字符串
     * @param publicKey
     *            公钥
     * @return 密文
     * @throws Exception
     *             加密过程中的异常信息
     */
    public static String encrypt( String str, String publicKey ) throws Exception{
        //base64编码的公钥
        byte[] decoded = Base64.decodeBase64(publicKey);
        RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance(RSA).generatePublic(new X509EncodedKeySpec(decoded));
        //RSA加密
        Cipher cipher = Cipher.getInstance(RSA);
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        String outStr = Base64.encodeBase64String(cipher.doFinal(str.getBytes("UTF-8")));
        return outStr;
    }

    /**
     * RSA私钥解密
     *
     * @param str
     *            加密字符串
     * @param privateKey
     *            私钥
     * @return 铭文
     * @throws Exception
     *             解密过程中的异常信息
     */
    public static String decrypt(String str, String privateKey) throws Exception{
        //64位解码加密后的字符串
        byte[] inputByte = Base64.decodeBase64(str.getBytes("UTF-8"));
        //base64编码的私钥
        byte[] decoded = Base64.decodeBase64(privateKey);
        RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance(RSA).generatePrivate(new PKCS8EncodedKeySpec(decoded));
        //RSA解密
        Cipher cipher = Cipher.getInstance(RSA);
        cipher.init(Cipher.DECRYPT_MODE, priKey);
        String outStr = new String(cipher.doFinal(inputByte));
        return outStr;
    }

}
package com.example.webapp.utils;


import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

/**
 * @author ghss
 */
@Service
@Slf4j
public class AesUtils {

    /**  是 secret 的sha256摘要 */
    public String D_390_F_367_C_1_FEDAAAE_7190_CC = "1d1570f7c05722f96708df5760f8c2e9d3a3625ec7649d4c2ad79cf5c76876b6";
    @Value("${jasypt.secret-key:#{null}}")
    public String secret;

    /**
     * 加密
     */
    public String encrypt(String content){
        if(!D_390_F_367_C_1_FEDAAAE_7190_CC.equals(Md5Util.MD5(secret))){
            log.error("非法私钥：{}，加密失败!",secret);
            throw new RuntimeException("加密失败");
        }
        byte[] key = secret.getBytes(StandardCharsets.UTF_8);
        SymmetricCrypto aes = new SymmetricCrypto(SymmetricAlgorithm.AES,key);
        return aes.encryptHex(content);
    }

    /**
     * 解密
     */
    public String decrypt(String encryptStr){
        if(!D_390_F_367_C_1_FEDAAAE_7190_CC.equals(Md5Util.MD5(secret))){
            log.error("非法私钥：{}，加密失败!",secret);
            throw new RuntimeException("加密失败");
        }
        byte[] key = secret.getBytes(StandardCharsets.UTF_8);
        SymmetricCrypto aes = new SymmetricCrypto(SymmetricAlgorithm.AES,key);
        return aes.decryptStr(encryptStr);
    }
}
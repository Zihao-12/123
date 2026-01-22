package com.example.webapp.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.InputStream;

/**
 * 摘要加密工具 数字签名
 */
@Slf4j
public class Md5Util {

    /**
     *  sha256Hex 64的十六进制字符串表示  不可逆，比md5安全 但耗时多
     * @param data
     * @return
     */
    public final static String sha256Hex(String data) {
        try {
            return DigestUtils.sha256Hex(data);
        } catch (Exception e) {
            log.error("{}", ExceptionUtils.getStackTrace(e));
            return "";
        }
    }

    public final static String sha256Hex(InputStream data) {
        try {
            return DigestUtils.sha256Hex(data);
        } catch (Exception e) {
            log.error("{}", ExceptionUtils.getStackTrace(e));
            return "";
        }
    }

    public final static String MD5(String data) {
        try {
            return DigestUtils.md5Hex(data);
        } catch (Exception e) {
            log.error("{}", ExceptionUtils.getStackTrace(e));
            return "";
        }
    }

    public final static String MD5(InputStream data) {
        try {
            return DigestUtils.md5Hex(data);
        } catch (Exception e) {
            log.error("{}", ExceptionUtils.getStackTrace(e));
            return "";
        }
    }

}

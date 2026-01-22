package com.example.webapp.common;

/**
 * 限制
 */
public class LimitNumber {
    private static int BANNER_LIMIT_NUMBER = 6;
    public static String BANNER_LIMIT_INFO = "最多创建6张轮播图";

    /**
     * 轮播图限制新建数量
     * @param num
     * @return true 限制操作
     */
    public static boolean banner(int num) {
        if (num > BANNER_LIMIT_NUMBER) {
            return true;
        }
        return false;
    }
}

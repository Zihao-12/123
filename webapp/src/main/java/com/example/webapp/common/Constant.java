package com.example.webapp.common;

public class Constant {

    /**
     * 新用户默认密码
     */
    public static final String ADD_USER_DEFAULT_PWD = "a123456";

    public final static String NO_DATA = "暂无数据";
    /**
     * 页面流览量缓存key前缀end
     */
    public static final int PAGE_SIZE = 20;
    public static final Integer STOP = 0;
    /**
     * 机构端：position = -1 查询老师（保护助教）
     */
    public static final Integer TEACHER=-1;
    /**
     * 普通用户
     */
    public static final Integer USER_TYPE=0;

    /**
     * 游客用户ID
     */
    public static final Integer TOURIST_ID=0;

    /**
     * 游客用户名
     */
    public static final String TOURIST_NAME="游客";
    /**
     * 1查询用户已参加的活动列表
     */
    public static final Integer USER_JOINED =1;
    /**
     * 运营端机构ID默认0
     */
    public static final Integer YUNYING_MECHANISM_ID = 0;
    /**
     * 机构开启IP限制
     */
    public static final Integer RESTRICT_IP = 1;

    public static final String CLAZZ = "clazz";
    public static final String RELATION = "relation";

    /**
     * 大疆 objectKey,阿里云oss存储地址数据提取下标
     */
    public static final int IMAGE_INDEX = 2;

    //通用资讯-浏览量key
    public final static String REDIS_KEY_HT_VIEW = "ht_view_";

    /**
     * 轮播图 站内H5跳转类型
     */
    public static final Integer JUMP_TYPE_H5=3;

}

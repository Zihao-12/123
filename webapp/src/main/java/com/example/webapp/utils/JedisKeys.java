package com.example.webapp.utils;

public class JedisKeys {
    private JedisKeys() {}
    /** 手机验证码过期时间：十分钟 */
    public static int VERIFY_CODE_EXPIRE = 5 * 60;
    /**
     * 记录sid有效时间：一个月 里面存储的sid无实际意义
     * @param sid
     * @param appId
     * @return
     */
    public static String liveTimeKey(String sid, String appId) {
        return "LIVE_TIME_" + sid + "_" + appId;
    }

    /**
     *  策略:key是userid value是一个sid的set集合(一个用户id可以被多台设备登陆)
     * @param appId
     * @param userId
     * @return
     */
    public static String userLoginAppKey(String appId, String userId) {
        return "USER_LOGIN_APP_" + appId + "_" + userId;
    }

    public static String liveUserDeviceIdKey(String appId, String sid) {
        return "LIVE_USER_LOGIN_DEVICE_" + appId + "_" + sid;
    }

    /**
     * 记录用户本次登录的设备情况  并以设备id为key 记录设备上登录了那些 app 和对应的sid
     * @param deviceId
     * @return
     */
    public static String deviceLiveLoginKey(String deviceId) {
        return "DEVICE_LOGIN_APP_" + deviceId;
    }

    /**
     *
     * @param mobile
     * @param use  VerifyCodeTypeEnum
     * @return
     */
    public static String verifyCodeKey(String mobile, String use) {
        return "VERIFY_CODE_" + mobile + "_" + use;
    }

}

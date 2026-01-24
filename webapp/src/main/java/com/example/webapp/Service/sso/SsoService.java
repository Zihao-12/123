package com.example.webapp.Service.sso;

import com.example.webapp.result.Result;

public interface SsoService {


    void logout(String sid);

    /**
     *
     * @param code
     * @param encryptedData
     * @param iv
     * @param mark
     * @param aci        机构认证信息
     * @return
     */
    Result loginWx(String code, String encryptedData, String iv, String mark, String aci);

    /**
     * 读者证号登录
     * @param cardNo 读者证号
     * @param password 密码
     * @param aci      机构认证信息
     * @param mark
     * @return
     */
    Result loginid(String cardNo, String password, String aci, String mark);

    /**
     * 用户端自动登录机构
     * @param aci
     * @return
     */
    Result loginAuto(String aci);

    /**
     * 机构运营端-用户名密码登录
     * @param userName 用户名
     * @param password 密码
     * @param mark
     * @return
     */
    Result login(String userName, String password, String mark);

    /**
     * 用户名密码登录
     * @param userName 用户名
     * @param password 密码
     * @param mark
     * @param aci 机构认证信息
     * @return
     */
    Result login(String userName, String password, String mark,String aci);

    Result updateUserInfo(String code, String encryptedData, String iv);

    /** 验证码 注册/登录
     * @param mobile     手机号
     * @param verifyCode 随机验证码
     * @param use        使用方式 1-快速注册时使用  2-快速登录时使用 3-找回密码时使用的发送验证码 4-添加或者修改绑定手机时使用 5-修改密码时使用
     * @param aci        机构认证信息
     * @return
     */
    Result loginvc(String mobile, String verifyCode, String use, String appId,String aci);

    /**
     *
     * @param mobile
     * @param appId
     * @param aci        机构认证信息
     * @return
     */
    Result loginkey(String mobile, String appId,String aci);

    /**
     * 发送手机号机对应的验证码 一分钟内不可重复发送验证码
     * @param mobile 手机号
     * @param use    使用方式 1-快速注册时使用
     *                       2-快速登录时使用
     *                       3-找回密码时使用的发送验证码
     *                       4-添加或者修改绑定手机时使用
     */
    Result sendVerifyCode(String mobile, String use);

    /**
     * 发送手机号机对应的验证码 一分钟内不可重复发送验证码
     * @param mobile 手机号
     * @param use    使用方式
     * @param templetCode 消息模版
     */
    Result sendVerifyCode(String mobile, String use, String templetCode);

    /** 验证码 修改密码
     * @param mobile     手机号
     * @param verifyCode 随机验证码
     * @param use        使用方式 1-快速注册时使用  2-快速登录时使用 3-找回密码时使用的发送验证码 4-添加或者修改绑定手机时使用 5-修改密码时使用
     * @return
     */
    Result resetPwdByVerifycode(String mobile, String verifyCode, String use, String passwd);

    /**
     * 修改手机
     * @param mobile 新手机
     * @param verifyCode
     * @param use
     * @param userId
     * @return
     */
    Result bindMobile(String mobile, String verifyCode, String use, Integer userId);

    Result verifyOldMobile(String mobile, String verifyCode, String use);

    /**
     * 读者证免登录
     * @param aci
     * @return
     */
    Result loginNo(String aci);
}

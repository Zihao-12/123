package com.example.webapp.enums;
import org.apache.commons.lang3.StringUtils;

/**
 * 手机验证码使用方式
 */
public enum VerifyCodeTypeEnum {
    /**
     * 快速注册时使用
     */
    VERIFY_CODE_USE_QUICK_REGIST("1"),

    /**
     * 快速登录时使用
     */
    VERIFY_CODE_USE_QUICK_LOGIN("2"),

    /**
     * 找回密码时使用
     */
    VERIFY_CODE_USE_FIND_PASSWORD("3"),

    /**
     * 添加或者修改绑定手机时使用
     */
    VERIFY_CODE_USE_BIND_MOBILE("4"),
    /**
     * 修改密码时使用
     */
    VERIFY_CODE_USE_UPDATE_PASSWORD("5"),
    /**
     * 验证旧手机
     */
    VERIFY_OLD_MOBILE("6");

    private String value;
    VerifyCodeTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static VerifyCodeTypeEnum instance(String use){
        VerifyCodeTypeEnum  verifyType = null;
        for (VerifyCodeTypeEnum  type : VerifyCodeTypeEnum.values()){
            if(StringUtils.equals(type.getValue(), use)){
                verifyType = type;
                break;
            }
        }
        return verifyType;
    }
}

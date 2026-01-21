package com.example.webapp.enums;

/**
 * 登录token来源
 */
public enum LoginTokenSourceEnum {

    /**
     *
     */
    LoginAdvice ("LoginAdvice"),
    LogoutInterceptor ("LogoutInterceptor"),
    LoginRecordAspect ("LoginRecordAspect"),
    LoginRequiredInterceptor ("LoginRequiredInterceptor");

    private String source;
    LoginTokenSourceEnum(String source){
        this.source=source;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
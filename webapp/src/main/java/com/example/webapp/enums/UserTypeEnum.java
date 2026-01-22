package com.example.webapp.enums;

public enum UserTypeEnum {
    /**
     * 用户类型 0普通用户 1机构游客(IP 或读者证免登录)
     * general user
     * tourist
     */
    GENERAL_USER (0,"普通用户"),
    TOURIST (1,"游客");

    private Integer type;
    private String name;
    UserTypeEnum(int type, String name){
        this.type=type;
        this.name=name;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
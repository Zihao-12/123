package com.example.webapp.enums;


public enum OpenLoginTypeEnum {
    ONLY_PRACTICE (1,"登录成功-  仅开通实训"),
    ONLY_MICRO (2,"登录成功-  仅开通微软 机构已添加用户到微软认证包，且在有效期内 "),
    ALL_PRACTICE (3,"登录成功-  全开通,实训未过期,微软过期"),
    ALL_MICRO (4,"登录成功-  全开通,实训过期，微软未过期"),
    ALL_PRACTICE_MICRO (5,"登录成功-  全开通,无过期");

    private Integer type;
    private String name;
    OpenLoginTypeEnum(int type, String name){
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
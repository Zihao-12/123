package com.example.webapp.enums;


public enum MechanismListOpenTypeEnum {
    ONLY_OPEN (1,"只查询开通的机构"),
    EXCLUDE_OPEN (0,"排除有开通记录的机构");

    private Integer type;
    private String name;
    MechanismListOpenTypeEnum(int type, String name){
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
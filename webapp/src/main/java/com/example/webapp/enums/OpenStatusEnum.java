package com.example.webapp.enums;


public enum OpenStatusEnum {
    PRACTICE_NO_OPEN (1,"未开通实训"),
    PRACTICE_NORMAL (2,"实训开通正常"),
    PRACTICE_EXPIRED (3,"实训开通过期"),
    PRACTICE_STOP (4,"实训开通停用");

    private Integer type;
    private String name;
    OpenStatusEnum(int type, String name){
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
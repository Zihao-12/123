package com.example.webapp.enums;


public enum StatusEnum {
    /**
     *
     */
    ALL (-1,"全部状态"),
    STOP (0,"已停用"),
    ENABLE (1,"已启用");

    private Integer type;
    private String name;
    StatusEnum(int type, String name){
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

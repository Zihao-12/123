package com.example.webapp.enums;


public enum MechanismOpenTypeEnum {
    /**
     *
     */
    ALL_OPEN (-1,"已开通：开通状态为“待开始、进行中、已结束"),
    PRACTICE (0,"通用开通");

    private Integer type;
    private String name;
    MechanismOpenTypeEnum(int type, String name){
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
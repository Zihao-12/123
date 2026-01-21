package com.example.webapp.enums;

/**
 * 根据开通日期判断
 * -1 all 0 finished 1 to start 2 in progress
 */
public enum MechanismOpenEnum {
    /**
     *
     */
    ALL(-1,"全部状态"),
    FINISHED(0,"已结束"),
    TO_START (1,"待开始"),
    IN_PROGRESS (2,"进行中");

    private Integer type;
    private String name;
    MechanismOpenEnum(int type, String name){
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
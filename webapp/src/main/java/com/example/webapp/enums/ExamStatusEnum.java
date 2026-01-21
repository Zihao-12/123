package com.example.webapp.enums;

public enum ExamStatusEnum {
    /**
     * 考场状态
     */
    ALL (0,"全部状态"),
    TO_START (1,"未开始"),
    START (2,"进行中"),
    OVER (3,"已结束");

    private Integer type;
    private String name;
    ExamStatusEnum(int type, String name){
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
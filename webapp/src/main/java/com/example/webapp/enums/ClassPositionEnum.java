package com.example.webapp.enums;

public enum ClassPositionEnum {
    /**
     * 班级职务：0 学生 1班主任 2助教 用户加入班级时确定和用户角色无关
     */
    ALL (-1,"全部职务"),
    STUDENT (0,"学生"),
    HEAD_TEACHER (1,"班主任"),
    ASSISTANT (2,"助教");

    private Integer type;
    private String name;
    ClassPositionEnum(int type, String name){
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
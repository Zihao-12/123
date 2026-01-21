package com.example.webapp.enums;

public enum UserRoleEnum {
    /**
     */
    NO_ROLE (-1,"无角色"),
    STUDENT (0,"学生"),
    TEACHER (1,"教师"),
    ASSISTANT (2,"助教");

    private Integer type;
    private String name;
    UserRoleEnum(int type, String name){
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
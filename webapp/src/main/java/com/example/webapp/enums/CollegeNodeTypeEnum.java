package com.example.webapp.enums;


public enum CollegeNodeTypeEnum {
    /**
     * 院系
     */
    COLLEGE(0,"院系"),
    /**
     * 班级
     */
    CLASS(1,"班级"),
    /**
     * 机构院系根结点父ID
     */
    ROOT_PARENT_ID(0,"机构院系根结点父ID");

    private Integer type;
    private String name;
    CollegeNodeTypeEnum(int type, String name){
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
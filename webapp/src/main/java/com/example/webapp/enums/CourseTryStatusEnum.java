package com.example.webapp.enums;


public enum CourseTryStatusEnum {
    /**
     *试看设置：
     * 0全部可看
     * 1试看第一节
     * 2试看前三节
     */
    ALL (0,"全部可看"),
    TRY_FIRST (1,"试看第一节"),
    TRY_FIRST_THREE (2,"试看前三节");

    private Integer type;
    private String name;
    CourseTryStatusEnum(int type, String name){
        this.type=type;
        this.name=name;
    }

    public static String getTryStatusName(Integer type){
        CourseTryStatusEnum statusEnum = getTryStatusEnum(type);
        return statusEnum==null?"":statusEnum.name;
    }

    public static CourseTryStatusEnum getTryStatusEnum(Integer type){
        CourseTryStatusEnum[] values = CourseTryStatusEnum.values();
        for (CourseTryStatusEnum value : values) {
            if (value.type.equals(type)) {
                return value;
            }
        }
        return null;
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

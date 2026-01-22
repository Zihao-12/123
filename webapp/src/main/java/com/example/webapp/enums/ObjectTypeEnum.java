package com.example.webapp.enums;

public enum ObjectTypeEnum {
    /**
     * 对象类型：1课程 2.新闻资讯 3活动 4题目
     */
    VIDEO(1,"课程"),
    NEWS(2,"新闻资讯"),
    ACTIVITY(3,"活动"),
    QUESTION(4,"题目");
    private int type;
    private String name;

    ObjectTypeEnum(int type, String name) {
        this.type = type;
        this.name = name;
    }

    public static String getTypeName(int type){
        ObjectTypeEnum statusEnum = getTypeEnum(type);
        return statusEnum==null?"":statusEnum.name;
    }

    public static ObjectTypeEnum getTypeEnum(int type){
        ObjectTypeEnum[] values = ObjectTypeEnum.values();
        for (ObjectTypeEnum value : values) {
            if (value.type==type) {
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

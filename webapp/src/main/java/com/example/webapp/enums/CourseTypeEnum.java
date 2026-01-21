package com.example.webapp.enums;

public enum CourseTypeEnum {
    /**
     * 1.视频课 2.签到视频
     */
    VIDEO(1,"视频课"),
    CHECKIN_VIDEO(2,"签到视频");
    private int type;
    private String name;


    CourseTypeEnum(int type, String name) {
        this.type = type;
        this.name = name;
    }

    public static String getTypeName(int type){
        CourseTypeEnum statusEnum = getTypeEnum(type);
        return statusEnum==null?"":statusEnum.name;
    }

    public static CourseTypeEnum getTypeEnum(int type){
        CourseTypeEnum[] values = CourseTypeEnum.values();
        for (CourseTypeEnum value : values) {
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

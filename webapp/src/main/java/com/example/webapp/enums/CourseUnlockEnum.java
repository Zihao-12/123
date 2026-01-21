package com.example.webapp.enums;

public enum CourseUnlockEnum {
    /**
     *课程模式: 0自由模式 1闯关模式
     */
    FREE(0,"自由模式"),
    PASS(1,"闯关模式");
    private Integer type;
    private String description;

    CourseUnlockEnum(Integer type, String description) {
        this.type = type;
        this.description = description;
    }

    public Integer getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public static String getNameByType(Integer type){
        CourseUnlockEnum e = getEnumByType(type);
        return e==null?"":e.description;
    }

    public static CourseUnlockEnum getEnumByType(Integer type){
        CourseUnlockEnum[] values = CourseUnlockEnum.values();
        for (CourseUnlockEnum value : values) {
            if (value.type.equals(type)) {
                return value;
            }
        }
        return null;
    }

}

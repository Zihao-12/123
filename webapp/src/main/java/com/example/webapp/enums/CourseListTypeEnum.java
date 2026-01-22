package com.example.webapp.enums;

public enum CourseListTypeEnum {
    /**
     * 课程列表类型
     * 1.机构购买课程列表
     * 2.自建课程列表
     */
    YUN_YING(0,"运营端课程列表"),
    JIGOU_BUY(1,"机构购买课程列表"),
    ZJ_JIAN(2,"自建课程列表"),
    FAVORITE(3,"用户课程列表");
    private Integer type;
    private String description;

    CourseListTypeEnum(Integer type, String description) {
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
        CourseListTypeEnum e = getEnumByType(type);
        return e==null?"":e.description;
    }

    public static CourseListTypeEnum getEnumByType(Integer type){
        CourseListTypeEnum[] values = CourseListTypeEnum.values();
        for (CourseListTypeEnum value : values) {
            if (value.type.equals(type)) {
                return value;
            }
        }
        return null;
    }

}

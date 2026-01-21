package com.example.webapp.enums;

/**
 * @author wujun
 * @description
 * @date 2021/2/12
 */
public enum CourseSectionTypeEnum {

    /**
     *
     */
    CHAPTER(0,"章"),
    SECTION(1,"节");

    private Integer type;
    private String name;

    CourseSectionTypeEnum(Integer type, String name) {
        this.type = type;
        this.name = name;
    }

    public Integer getType() {
        return type;
    }
}

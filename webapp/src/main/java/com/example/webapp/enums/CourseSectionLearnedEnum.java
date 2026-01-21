package com.example.webapp.enums;

/**
 * @author wujun
 * @description
 * @date 2021/2/2
 */
public enum CourseSectionLearnedEnum {
    COMPLETE(1,"已学完");

    private Integer val;
    private String description;

    CourseSectionLearnedEnum(Integer val, String description) {
        this.val = val;
        this.description = description;
    }

    public Integer getVal() {
        return val;
    }

    public void setVal(Integer val) {
        this.val = val;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

package com.example.webapp.enums;

/**
 * 是否推荐:0否 1.是
 */
public enum RecommendEnum {
    recommend_yes(1,"推荐"),
    recommend_no(0,"不推荐");
    private Integer value;
    private String description;

    RecommendEnum(Integer value, String description) {
        this.value = value;
        this.description = description;
    }

    public Integer getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

}

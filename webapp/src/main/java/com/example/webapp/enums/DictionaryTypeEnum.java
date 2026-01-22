package com.example.webapp.enums;

public enum DictionaryTypeEnum {
    MECHANISM_ATTRIBUTE(1,"机构属性"),
    NEWS(2,"新闻来源");
    private int type;
    private String description;

    public int getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    DictionaryTypeEnum(int type, String description) {
        this.type = type;
        this.description = description;
    }
}

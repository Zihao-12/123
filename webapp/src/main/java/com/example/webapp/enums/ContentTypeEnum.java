package com.example.webapp.enums;

public enum ContentTypeEnum {
    /**
     * 内容类型1.通用资讯
     */
    TONG_YONG_ZIXUN(1,"通用资讯");
    private Integer type;
    private String description;

    ContentTypeEnum(Integer type, String description) {
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
        ContentTypeEnum e = getEnumByType(type);
        return e==null?"":e.description;
    }

    public static ContentTypeEnum getEnumByType(Integer type){
        ContentTypeEnum[] values = ContentTypeEnum.values();
        for (ContentTypeEnum value : values) {
            if (value.type.equals(type)) {
                return value;
            }
        }
        return null;
    }
}

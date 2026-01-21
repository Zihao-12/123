package com.example.webapp.enums;

public enum SourceTypeEnum {
    /**
     *轮播图类型1.自建 2.运营端
     */
    YUN_YING(0,"运营端"),
    ZI_JIANG(1,"自建");
    private Integer type;
    private String description;

    SourceTypeEnum(Integer type, String description) {
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
        SourceTypeEnum e = getEnumByType(type);
        return e==null?"":e.description;
    }

    public static SourceTypeEnum getEnumByType(Integer type){
        SourceTypeEnum[] values = SourceTypeEnum.values();
        for (SourceTypeEnum value : values) {
            if (value.type.equals(type)) {
                return value;
            }
        }
        return null;
    }

}

package com.example.webapp.enums;

public enum BannerTypeEnum {
    /**
     *轮播图类型 1.H5首页 2.小程序首页
     */
    H5_HOME(1,"H5首页"),
    APP_HOME(2,"小程序首页");
    private Integer type;
    private String description;

    BannerTypeEnum(Integer type, String description) {
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
        BannerTypeEnum e = getEnumByType(type);
        return e==null?"":e.description;
    }

    public static BannerTypeEnum getEnumByType(Integer type){
        BannerTypeEnum[] values = BannerTypeEnum.values();
        for (BannerTypeEnum value : values) {
            if (value.type.equals(type)) {
                return value;
            }
        }
        return null;
    }

}

package com.example.webapp.enums;

public enum RankingLocalTypeEnum {
    /**
     * 0全国 1馆内排行
     */
    QUAN_GUO(0,"全国排行"),
    GUAN_NEI(1,"馆内排行");
    private Integer type;
    private String description;

    RankingLocalTypeEnum(Integer type, String description) {
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
        RankingLocalTypeEnum e = getEnumByType(type);
        return e==null?"":e.description;
    }

    public static RankingLocalTypeEnum getEnumByType(Integer type){
        RankingLocalTypeEnum[] values = RankingLocalTypeEnum.values();
        for (RankingLocalTypeEnum value : values) {
            if (value.type.equals(type)) {
                return value;
            }
        }
        return null;
    }
}

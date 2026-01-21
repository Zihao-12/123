package com.example.webapp.enums;

public enum PrizeTypeEnum {
    /**
     * 奖品类型：0不中奖 1实物 2积分
     */
    THANKS(0,"不中奖"),
    REAL(1,"实物"),
    INTEGRAL(2,"积分");
    private Integer type;
    private String description;

    PrizeTypeEnum(Integer type, String description) {
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
        PrizeTypeEnum e = getEnumByType(type);
        return e==null?"":e.description;
    }

    public static PrizeTypeEnum getEnumByType(Integer type){
        PrizeTypeEnum[] values = PrizeTypeEnum.values();
        for (PrizeTypeEnum value : values) {
            if (value.type.equals(type)) {
                return value;
            }
        }
        return THANKS;
    }

}

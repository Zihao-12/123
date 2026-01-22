package com.example.webapp.enums;

public enum ActivityJoinFrequencyTypeEnum {
    /**
     *活动参加频次以及默认次数
     */
    ALL(0,1,"总共"),
    PER_DAY(1,3,"每天");
    private Integer type;
    private String description;
    private Integer defaultTimes;

    ActivityJoinFrequencyTypeEnum(Integer type,Integer defaultTimes, String description) {
        this.type = type;
        this.defaultTimes=defaultTimes;
        this.description = description;
    }

    public Integer getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public Integer getDefaultTimes() {
        return defaultTimes;
    }

    public void setDefaultTimes(Integer defaultTimes) {
        this.defaultTimes = defaultTimes;
    }

    public static String getNameByType(Integer type){
        ActivityJoinFrequencyTypeEnum e = getEnumByType(type);
        return e==null?"":e.description;
    }

    public static ActivityJoinFrequencyTypeEnum getEnumByType(Integer type){
        ActivityJoinFrequencyTypeEnum[] values = ActivityJoinFrequencyTypeEnum.values();
        for (ActivityJoinFrequencyTypeEnum value : values) {
            if (value.type.equals(type)) {
                return value;
            }
        }
        return null;
    }

}

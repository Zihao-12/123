package com.example.webapp.enums;

public enum ActivityManualTypeEnum {
    /**
     *选题规则
     */
    SYSTEM(0,"系统分配"),
    MANUAL(1,"手动配置");
    private Integer type;
    private String description;

    ActivityManualTypeEnum(Integer type, String description) {
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
        ActivityManualTypeEnum e = getEnumByType(type);
        return e==null?"":e.description;
    }

    public static ActivityManualTypeEnum getEnumByType(Integer type){
        ActivityManualTypeEnum[] values = ActivityManualTypeEnum.values();
        for (ActivityManualTypeEnum value : values) {
            if (value.type.equals(type)) {
                return value;
            }
        }
        return null;
    }

}

package com.example.webapp.enums;

public enum GenderTypeEnum {
    /**
     * 0未设置
     * 1男2女'
     */
    NOT_SET(1,"未设置"),
    MALE(2,"男"),
    FEMALE(3,"女");
    private Integer type;
    private String description;

    GenderTypeEnum(Integer type, String description) {
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
        GenderTypeEnum e = getEnumByType(type);
        return e==null?"":e.description;
    }

    public static Integer parseGender(Integer gender){
         if(MALE.getType().equals(gender) || FEMALE.getType().equals(gender)){
             return gender;
         }
         return NOT_SET.getType();
    }

    public static GenderTypeEnum getEnumByType(Integer type){
        GenderTypeEnum[] values = GenderTypeEnum.values();
        for (GenderTypeEnum value : values) {
            if (value.type.equals(type)) {
                return value;
            }
        }
        return null;
    }

}

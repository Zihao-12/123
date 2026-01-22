package com.example.webapp.enums;

public enum ObjectDeleteEnum {
    /**
     * 对象类型：0正常 1删除
     */
    NORMAL(0,"正常"),
    DELETE(1,"删除");
    private int type;
    private String name;

    ObjectDeleteEnum(int type, String name) {
        this.type = type;
        this.name = name;
    }

    public static String getTypeName(int type){
        ObjectDeleteEnum statusEnum = getTypeEnum(type);
        return statusEnum==null?"":statusEnum.name;
    }

    public static ObjectDeleteEnum getTypeEnum(int type){
        ObjectDeleteEnum[] values = ObjectDeleteEnum.values();
        for (ObjectDeleteEnum value : values) {
            if (value.type==type) {
                return value;
            }
        }
        return null;
    }
    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}

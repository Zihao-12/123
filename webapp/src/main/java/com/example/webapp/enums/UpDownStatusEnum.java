package com.example.webapp.enums;

public enum UpDownStatusEnum {
    /**
     *
     */
    UP(1,"已上架"),
    DOWN(0,"待上架");

    UpDownStatusEnum(int status, String name) {
        this.status = status;
        this.name = name;
    }

    private Integer status;
    private String name;

    public static String getUpDownStatusName(Integer status){
        UpDownStatusEnum statusEnum = getUpDownStatusEnum(status);
        return statusEnum==null?"":statusEnum.name;
    }

    public static UpDownStatusEnum getUpDownStatusEnum(Integer status){
        UpDownStatusEnum[] values = UpDownStatusEnum.values();
        for (UpDownStatusEnum value : values) {
            if (value.status.equals(status)) {
                return value;
            }
        }
        return null;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

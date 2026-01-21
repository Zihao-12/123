package com.example.webapp.enums;

public enum CoursePackageStatusEnum {
    /**
     *
     */
    NOT_USED(0,"待使用"),
    USED(1,"已使用");
    public Integer status;
    public String name;

    CoursePackageStatusEnum(Integer status, String name) {
        this.status = status;
        this.name = name;
    }

    public static String getStatusName(Integer status){
        if(status==null){
            return "";
        }
        CoursePackageStatusEnum statusEnum = getStatusEnum(status);
        return statusEnum==null?"":statusEnum.name;
    }

    public static CoursePackageStatusEnum getStatusEnum(int status){
        CoursePackageStatusEnum[] values = CoursePackageStatusEnum.values();
        for (CoursePackageStatusEnum value : values) {
            if (value.status==status) {
                return value;
            }
        }
        return null;
    }
}

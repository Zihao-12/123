package com.example.webapp.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * 平台标记枚举
 */
public enum PlatformMarkEnum {
    /**
     *
     */
    CLIENT ("CT",10,"用户端"),
    MECHANISM ("MT",11,"机构端"),
    ENTERPRISE ("ET",12,"运营端");

    /**  用于生成各端appId/cookie前缀,根据sid中的appid，判断token属于那个平台  */
    private String mark;
    private String desc;
    /** 平台类型  */
    private Integer type;
    PlatformMarkEnum(String mark,Integer type, String name){
        this.mark=mark;
        this.desc=name;
        this.type=type;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public static PlatformMarkEnum getTypeEnum(String mark){
        if(StringUtils.isBlank(mark)){
            return PlatformMarkEnum.CLIENT;
        }
        PlatformMarkEnum[] values = PlatformMarkEnum.values();
        for (PlatformMarkEnum value : values) {
            if (value.mark.equals(mark) || mark.startsWith(value.mark)) {
                return value;
            }
        }
        return null;
    }

    public static PlatformMarkEnum getPlatformEnum(Integer type){
        PlatformMarkEnum[] values = PlatformMarkEnum.values();
        for (PlatformMarkEnum value : values) {
            if(value.type.equals(type)){
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

}
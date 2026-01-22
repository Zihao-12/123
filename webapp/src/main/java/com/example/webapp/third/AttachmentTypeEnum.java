package com.example.webapp.third;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

public enum AttachmentTypeEnum {
    /**
     * OBS附件类型 0通用附件 1课程附件 2课程封面 3用户资料图片
     */
    GENERAL_ATTACHMENT(0,"通用附件","ck-reader/general/attachment"),
    COURSE_ATTACHMENT(1,"课程附件","ck-reader/course/attachment"),
    COURSE_COVER(2,"课程封面","ck-reader/course/cover"),
    PROFILE_PIC(3,"用户资料图片","ck-reader/user/profile"),
    MECHANISM_FILE(4,"机构端附件","ck-reader/mechanism/attachment");
    private Integer type;
    private String name;
    private String pathPerfix;

    AttachmentTypeEnum(Integer type, String name,String pathPerfix) {
        this.type = type;
        this.name = name;
        this.pathPerfix=pathPerfix;
    }

    public static AttachmentTypeEnum getEnumByType(Integer type){
        for (AttachmentTypeEnum obj : AttachmentTypeEnum.values()) {
            if (obj.type.equals(type)) {
                return obj;
            }
        }
        return null;
    }

    public static List<Map<String,String>> getEnumObjList(){
        List<Map<String,String>> list = Lists.newArrayList();
        for (AttachmentTypeEnum obj : AttachmentTypeEnum.values()) {
             Map<String,String> map = Maps.newHashMap();
             map.put("name",obj.getName());
             map.put("value",obj.getType().toString());
             list.add(map);
        }
        return list;
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

    public String getPathPerfix() {
        return pathPerfix;
    }

    public void setPathPerfix(String pathPerfix) {
        this.pathPerfix = pathPerfix;
    }
}

package com.example.webapp.utils.excel;

import com.google.common.collect.Maps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 表do枚举
 */
public enum TableDOEnum {
    /**
     *
     */
    USER("UserDO","com.zhihuiedu.business.entity.UserDO","用户");
    private String name;
    private String clazz;
    private String desc;

    private TableDOEnum(String name, String clazz, String desc) {
       this.name=name;
       this.clazz=clazz;
       this.desc =desc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public static String getClazzByName(String name){
        TableDOEnum[] values = TableDOEnum.values();
        for (TableDOEnum value : values) {
            if (value.name.equals(name)) {
                return value.getClazz();
            }
        }
        return null;
    }

    public static String getNameByClazz(String clazz){
        TableDOEnum[] values = TableDOEnum.values();
        for (TableDOEnum value : values) {
            if (value.clazz.equals(clazz)) {
                return value.getName();
            }
        }
        return null;
    }

    public static List<Map<String,String>> getEnumList(){
        List<Map<String,String>> list = new ArrayList<>();
        TableDOEnum[] values = TableDOEnum.values();
        for (TableDOEnum value : values) {
            HashMap map = Maps.newHashMap();
            map.put("name",value.getName());
            map.put("desc",value.getDesc());
            list.add(map);
        }
        return list;
    }
}


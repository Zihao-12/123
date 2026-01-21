package com.example.webapp.enums;

/**
 * @author gehaisong
 */
public enum CategoryNodeTypeEnum {
    /**
     *
     */
    NODE(0,"节点"),
    LEAF(1,"叶子");

    private Integer type;
    private String name;
    CategoryNodeTypeEnum(int type, String name){
        this.type=type;
        this.name=name;
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
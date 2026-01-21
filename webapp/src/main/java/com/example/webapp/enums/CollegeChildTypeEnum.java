package com.example.webapp.enums;

/**
 * 可建孩子节点类型：0 不可创建-此节点是班级 1可创建部门(此节点含部门结点) 2可创建班级(此节点是含班级的节点) 3可创建部门班级-此结点还没有子结点
 * @author gehaisong
 */
public enum CollegeChildTypeEnum {
    /**
     * 只能创建部门
     */
    NOT_CREATE_NODE(0,"不可创建-此节点是班级/叶子"),
    /**
     * 只能创建部门
     */
    CREATE_DEPARTMENT(1,"可创建部门(此节点含部门/分类结点)"),
    /**
     * 班级
     */
    CREATE_CLASS(2,"可创建班级(此节点是含班级/分类的节点)"),
    /**
     * 可创建部门班级
     */
    CREATE_DEPARTMENT_CLASS(3,"可创建部门/分类或班级/叶子-此结点还没有子结点");



    private Integer type;
    private String name;
    CollegeChildTypeEnum(int type, String name){
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
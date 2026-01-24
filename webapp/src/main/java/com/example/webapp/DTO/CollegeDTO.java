package com.example.webapp.DTO;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class CollegeDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String name;
    /**
     * 0表示是机构 node_type=0
     */
    private Integer parentId;
    /**
     * 机构ID
     */
    private Integer mechanismId;

    /**
     * id全路径
     */
    private String idFullPath;

    /**
     * name全路径
     */
    private String nameFullPath;

    /**
     * 0院系 1班级
     */
    private Integer nodeType;
    /**
     *可建孩子节点类型：1部门(ROOT节点) 2班级(含班级的节点) 3部门班级
     */
    private Integer childType;

    private Integer sort;
    private Date createTime;
    /**
     * 班主任/助教字符串
     */
    private String positionInfo;
    /**
     * 助教
     */
    private String assistant;
    /**
     * 班主任
     */
    private String headTeacher;
    /**
     * 班级学生数量
     */
    private Integer studentNum;
}

package com.example.webapp.DTO;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class DepartmentDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer nodeId;
    private String nodeName;
    /**
     * 部门下班级数量
     */
    private Integer classTotalNumber;
    /**
     * 老师（含助教）人数
     */
    private Integer teacherTotalNumber;
    /**
     * 学生人数
     */
    private Integer studentTotalNumber;
    //	private Integer allTotalNumber;
    private List<DepartmentDTO> departmentList;
}

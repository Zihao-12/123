package com.example.webapp.DTO;

import java.util.Date;
import java.util.List;

public class CoursePackageDTO {
    private static final long serialVersionUID = -4249983043983128058L;

    private Integer id;

    private String name;

    private String introduction;

    private Integer courseNumber;

    private Integer type;

    private Integer status;
    private String statusCn;

    private Integer tryStatus;
    private String tryStatusCn;

    private Integer used;
    private String usedCn;

    private Integer isDelete;

    private Date updateTime;

    private Date createTime;

    private Integer courseId;

    private List<CourseDTO> courseList;
}

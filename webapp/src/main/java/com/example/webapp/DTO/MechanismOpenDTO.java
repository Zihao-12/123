package com.example.webapp.DTO;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@ApiModel( "机构开通表")
@Data
public class MechanismOpenDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String mechanismName;
    private Integer mechanismId;
    private String packageName;
    private Integer coursePackageId;
    /**
     * 课程包课程数量
     */
    private Integer courseNumber;
    /**
     * 开通天数
     */
    private Integer openDays;
    /**
     * 剩余天数:当前时间到结束时间
     */
    private Integer surplusDays;
    private Date beginTime;
    private Date endTime;
    /**
     * 机构最大帐号数量限制，0表示不限制
     */
    private Integer accountNumber;


    private Integer status;
    private String statusView;
    private String openView;
    private Integer openType;
    private Integer isDelete;
    private Date updateTime;
    private Date createTime;

    /**
     * 课程
     */
    private List<CourseDTO> courseList;
}

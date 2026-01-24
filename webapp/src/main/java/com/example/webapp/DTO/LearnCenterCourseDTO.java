package com.example.webapp.DTO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@ApiModel( "学生学习中心课程dto")
public class LearnCenterCourseDTO implements Serializable {
    private Integer id;
    @ApiModelProperty(notes = "课程名称")
    private String name;
    @ApiModelProperty(notes = "课程简介")
    private String introduction;
    @ApiModelProperty(notes = "课程封面")
    private String cover;
    @ApiModelProperty(notes = "1.视频课 2.签到视频")
    private Integer type;
    @ApiModelProperty(notes = "更新时间")
    private Date updateTime;
    @ApiModelProperty(notes = "创建日期")
    private Date createTime;
    @ApiModelProperty(notes = "学习进度")
    private Integer progress;
    @ApiModelProperty(notes = "学习人数")
    private Integer learnUsers;
    @ApiModelProperty(notes = "学生端:最后一次听课的课节id")
    private Integer lastCourseSectionId;
}

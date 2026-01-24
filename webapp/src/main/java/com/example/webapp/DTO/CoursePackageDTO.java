package com.example.webapp.DTO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@ApiModel( "课程包表")
@Data
public class CoursePackageDTO implements Serializable {

    private static final long serialVersionUID = -4249983043983128058L;
    @ApiModelProperty(value = "id")
    private Integer id;
    @ApiModelProperty(notes = "课程包名称")
    private String name;
    @ApiModelProperty(notes = "课程包简介")
    private String introduction;
    @ApiModelProperty(notes = "包含课程数量")
    private Integer courseNumber;
    @ApiModelProperty(name = "type",notes = "1.通用包")
    private Integer type;
    @ApiModelProperty(notes = "是否上架：1上架 0下架")
    private Integer status;
    private String statusCn;
    @ApiModelProperty(value = "试看设置：0全部可看 1试看第一节 2试看前三节")
    private Integer tryStatus;
    private String tryStatusCn;
    @ApiModelProperty(notes = "课程包状态：0待使用 1已使用，机构开通只能选择待使用的")
    private Integer used;
    private String usedCn;
    @ApiModelProperty(notes = "是否删除:0表示正常；1表示删除")
    private Integer isDelete;
    @ApiModelProperty(notes = "更新时间")
    private Date updateTime;
    @ApiModelProperty(notes = "创建日期")
    private Date createTime;
    @ApiModelProperty( notes = "课程id")
    private Integer courseId;
    @ApiModelProperty( notes = "课程列表")
    private List<CourseDTO> courseList;
}

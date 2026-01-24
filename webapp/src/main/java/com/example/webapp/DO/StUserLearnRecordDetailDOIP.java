package com.example.webapp.DO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@ApiModel( "用户学习记录明细表")
@Data
public class StUserLearnRecordDetailDOIP implements Serializable {

    private static final long serialVersionUID = 683174113632281179L;
    @ApiModelProperty(notes ="id",hidden = true)
    private Integer id;
    @ApiModelProperty(notes ="机构id",hidden = true)
    private Integer mechanismId;
    @ApiModelProperty(notes ="ip",hidden = true)
    private String  ip;
    @ApiModelProperty(notes ="课程id")
    private Integer courseId;
    @ApiModelProperty(notes ="课节id")
    private Integer courseSectionId;
    @ApiModelProperty(notes ="视频学习时间")
    private Integer duration;
    @ApiModelProperty(notes ="是否完成:0未完成1已完成")
    private Integer complete;
    @ApiModelProperty(notes ="学习日期 yyyy-mm-dd")
    private Date studyDate;
    @ApiModelProperty(notes ="is_delete",hidden = true)
    private Integer isDelete;
    @ApiModelProperty(notes ="update_time",hidden = true)
    private Date updateTime;
    @ApiModelProperty(notes ="create_time",hidden = true)
    private Date createTime;
}

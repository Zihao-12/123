package com.example.webapp.DO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@ApiModel( "用户学习记录主表")
@Data
public class StUserLearnRecordDOIP implements Serializable {

    private static final long serialVersionUID = -6240099398294685197L;
    @ApiModelProperty(value = "id")
    private Integer id;
    @ApiModelProperty(value = "机构id")
    private Integer mechanismId;
    @ApiModelProperty(notes ="ip",hidden = true)
    private String  ip;
    /**
     * 课程id
     */
    @ApiModelProperty(value = "课程id")
    private Integer courseId;
    /**
     * 视频学习时间
     */
    @ApiModelProperty(value = "视频学习时间")
    private Integer duration;
    /**
     * 学习进度
     */
    @ApiModelProperty(value = "学习进度")
    private Integer progress;
    /**
     * 0未完成 1已完成
     */
    @ApiModelProperty(value = "0未完成 1已完成")
    private Integer complete;
    /**
     * 首次听课时间
     */
    @ApiModelProperty(value = "首次听课时间")
    private Date firstTime;
    /**
     * 完成时间
     */
    @ApiModelProperty(value = "完成时间")
    private Date completeTime;
    @ApiModelProperty(value = "is_delete")
    private Integer isDelete;
    @ApiModelProperty(value = "update_time")
    private Date updateTime;
    @ApiModelProperty(value = "create_time")
    private Date createTime;
}
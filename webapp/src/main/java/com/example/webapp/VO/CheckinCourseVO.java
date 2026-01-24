package com.example.webapp.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel( "签到课程dto（课程子集）")
public class CheckinCourseVO implements Serializable {
    private static final long serialVersionUID = -5617551860034624357L;
    private Integer courseId;
    @ApiModelProperty(notes = "课程名称")
    private String name;
    @ApiModelProperty(notes = "课程简介")
    private String introduction;
    @ApiModelProperty(notes = "封面")
    private String cover;
    @ApiModelProperty(notes = "课程详情")
    private String detail;
    @ApiModelProperty(notes = "视频地址")
    private String video;
    @ApiModelProperty(notes = "视频时长(秒)")
    private Integer videoDuration;
    @ApiModelProperty(notes = " 0未完成 1已完成")
    private Integer complete;

}

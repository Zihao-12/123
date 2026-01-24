package com.example.webapp.DO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@ApiModel( "用户签到表")
public class CheckinDO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    private Integer id;
    @ApiModelProperty(value = "用户id")
    private Integer userId;
    @ApiModelProperty(value = "签到类型：0视频签到 1活动签到")
    private Integer signType;
    @ApiModelProperty(value = "签到日期：yyyyMMdd")
    private Date signDate;
    @ApiModelProperty(value = "连续签到次数:获取前一次的签到记录，如果没有，说明没有连续签到，此时 checkin_times 设置为1；如果有，获取前一天记录的checkin_times记录加一设置为新记录的checkin_times字段值")
    private Integer checkinTimes;
    @ApiModelProperty(value = "签到视频ID/活动ID")
    private Integer objectId;
    @ApiModelProperty(value = "积分")
    private Integer score;
    @ApiModelProperty(value = "0正常 1删除" ,hidden = true)
    private Integer isDelete;
    @ApiModelProperty(value = "更新时间" ,hidden = true)
    private Date updateTime;
    @ApiModelProperty(value = "创建时间" ,hidden = true)
    private Date createTime;
}

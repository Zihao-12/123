package com.example.webapp.DO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@ApiModel( "用户活动明细表")
public class UserActivityDetailRefDO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    private Integer id;
    @ApiModelProperty(value = "用户活动ID")
    private Integer userActivityId;
    @ApiModelProperty(value = "得分")
    private Integer score;
    @ApiModelProperty(value = "用时(秒)")
    private Integer times;
    @ApiModelProperty(value = "签到日期：yyyyMMdd")
    private Date signDate;
    @ApiModelProperty(value = "0正常 1删除" ,hidden = true)
    private Integer isDelete;
    @ApiModelProperty(value = "更新时间" ,hidden = true)
    private Date updateTime;
    @ApiModelProperty(value = "创建时间" ,hidden = true)
    private Date createTime;
}
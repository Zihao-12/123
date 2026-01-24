package com.example.webapp.DO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@ApiModel( "用户抽奖表表")
public class UserLotteryDO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    private Integer id;
    @ApiModelProperty(value = "用户ID")
    private Integer userId;
    @ApiModelProperty(value = "活动ID")
    private Integer activityId;
    @ApiModelProperty(value = "共获得抽奖次数")
    private Integer lotteryNum;
    @ApiModelProperty(value = "已抽奖次数")
    private Integer joinedNum;

    @ApiModelProperty(value = "0正常 1删除" ,hidden = true)
    private Integer isDelete;
    @ApiModelProperty(value = "更新时间" ,hidden = true)
    private Date updateTime;
    @ApiModelProperty(value = "创建时间" ,hidden = true)
    private Date createTime;
}

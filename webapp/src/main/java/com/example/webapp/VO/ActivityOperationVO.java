package com.example.webapp.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel( "活动运营设置用户VO")
public class ActivityOperationVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "活动ID")
    private Integer activityId;
    @ApiModelProperty(value = "馆内榜单：1显示 0不显示")
    private Integer library;
    @ApiModelProperty(value = "全国榜单：1显示 0不显示")
    private Integer country;
    @ApiModelProperty(value = "1运营关闭抽奖")
    private Integer close;
    @ApiModelProperty(value = "抽奖说明")
    private String lotteryDescription;
    @ApiModelProperty(value = "0关闭弹幕 1显示弹幕")
    private Integer showBarrage;

    @ApiModelProperty(value = "用户可抽奖次数")
    private Integer availableLotteryNum;






}

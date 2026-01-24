package com.example.webapp.DO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@ApiModel( "活动运营设置表")
public class ActivityOperationRefDO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id",hidden = true)
    private Integer id;
    @ApiModelProperty(value = "活动ID")
    private Integer activityId;
    @ApiModelProperty(value = "活动参与次数")
    private Integer joinTimes;
    @ApiModelProperty(value = "活动参与频次： 0总共 1每天")
    private Integer joinFrequency;
    @ApiModelProperty(value = "积分值")
    private Integer integral;
    @ApiModelProperty(value = "活动积分次数")
    private Integer integralTimes;
    @ApiModelProperty(value = "活动积分频次： 0总共 1每天")
    private Integer integralFrequency;
    @ApiModelProperty(value = "馆内榜单：1显示 0不显示")
    private Integer library;
    @ApiModelProperty(value = "全国榜单：1显示 0不显示")
    private Integer country;
    @ApiModelProperty(value = "0设置抽奖 1关闭抽奖")
    private Integer lotteryClose;
    @ApiModelProperty(value = "设置用户可抽奖次数")
    private Integer lotteryTimes;
    @ApiModelProperty(value = "触发规则:答对指定题数获取抽奖资格")
    private Integer lotteryTriggerRules;
    @ApiModelProperty(value = "抽奖说明")
    private String lotteryDescription;
    @ApiModelProperty(value = "0关闭弹幕 1显示弹幕")
    private Integer showBarrage;
    @ApiModelProperty(value = "0正常 1删除" ,hidden = true)
    private Integer isDelete;
    @ApiModelProperty(value = "更新时间" ,hidden = true)
    private Date updateTime;
    @ApiModelProperty(value = "创建时间" ,hidden = true)
    private Date createTime;

    @ApiModelProperty(value = "选题规则：1手动配置 0系统分配")
    private Integer manual;
    @ApiModelProperty(value = "总题数")
    private Integer questionNum;
    @ApiModelProperty(value = "总分值")
    private Integer totalScore;

    @ApiModelProperty(value = "活动奖品列表")
    private List<ActivityPrizeDetailDO> prizeDetailList;
}
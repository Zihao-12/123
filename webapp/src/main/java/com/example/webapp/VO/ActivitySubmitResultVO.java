package com.example.webapp.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel( "答题结果vo")
public class ActivitySubmitResultVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "本次增加的抽奖次数 0表示本次答题不准抽奖")
    private Integer addLotteryTimes;

    @ApiModelProperty(value = "最好得分 ")
    private Integer highestScore;
    @ApiModelProperty(value = "最好得分用时 ")
    private Integer highestScoreTimes;
    @ApiModelProperty(value = "本次得分")
    private Integer score;
    @ApiModelProperty(value = "本次用时")
    private Integer times;


}

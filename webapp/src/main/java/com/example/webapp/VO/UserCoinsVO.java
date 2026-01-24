package com.example.webapp.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel( "用户积分信息")
public class UserCoinsVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "userId")
    private Integer userId;
    @ApiModelProperty(value = "用户总积分")
    private Integer score;
    @ApiModelProperty(value = "累计签到次数")
    private Integer cumulativeTimes;

    @ApiModelProperty(notes = "0今日未签到 1今日已经签到")
    private Integer checkin;
}

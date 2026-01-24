package com.example.webapp.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel( "活动中奖名单vo")
public class ActivityWinLotteryUserVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户名 ")
    private String nickName;
    @ApiModelProperty(value = "奖品名 ")
    private String prizeName;
    @ApiModelProperty(value = "奖品类型：0不中奖 1实物 2积分 ")
    private Integer prizeType;


}

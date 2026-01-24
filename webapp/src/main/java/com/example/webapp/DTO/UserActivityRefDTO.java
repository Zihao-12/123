package com.example.webapp.DTO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel( "用户活动表DTO")
public class UserActivityRefDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    private Integer id;
    @ApiModelProperty(value = "用户ID")
    private Integer userId;
    @ApiModelProperty(value = "活动ID")
    private Integer activityId;
    @ApiModelProperty(value = "最高得分")
    private Integer score;
    @ApiModelProperty(value = "最高得分用时(秒)")
    private Integer times;
    @ApiModelProperty(value = "0未完成 1完成活动")
    private Integer complete;






}

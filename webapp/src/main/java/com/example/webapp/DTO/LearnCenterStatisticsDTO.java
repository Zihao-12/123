package com.example.webapp.DTO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel( "学生学习中心统计dto")
public class LearnCenterStatisticsDTO implements Serializable {
    @ApiModelProperty(value = "今日学习时长")
    private Integer todayLearnDuration;
    @ApiModelProperty(value = "累计学习时长")
    private Integer totalLearnDuration;
    @ApiModelProperty(value = "累计登录天数")
    private Integer totalLogin;
    @ApiModelProperty(value = "课程完成数")
    private Integer courseCompleteNum;
    @ApiModelProperty(value = "兴趣分布集合")
    private List<InterestDTO> interestDTOList;
}

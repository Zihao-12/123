package com.example.webapp.DTO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel( "用户答案DTO")
public class UserAnswerDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户ID",hidden = true)
    private Integer userId;
    @ApiModelProperty(value = "活动ID")
    private Integer activityId;
    @ApiModelProperty(value = "用时(秒)")
    private Integer times;

    @ApiModelProperty(value = "答案列表,空题需提交")
    List<AnswerDTO> answerList;




}

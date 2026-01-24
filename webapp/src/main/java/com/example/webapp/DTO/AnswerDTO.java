package com.example.webapp.DTO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel( "用户答案DTO")
public class AnswerDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "题目ID")
    private Integer questionId;
    @ApiModelProperty(value = "用户答案(选项ID)")
    private List<Integer> userOptionIdList;
    @ApiModelProperty(value = "由userOptionIdList 生成有序的用户答案字符串：11_22_43 ",hidden = true)
    private String answer;
    @ApiModelProperty(value = "得分(后端计算)",hidden = true)
    private Integer score;
}

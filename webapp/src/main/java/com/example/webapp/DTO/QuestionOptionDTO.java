package com.example.webapp.DTO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@ApiModel( "题目选项表DTO")
@Data
public class QuestionOptionDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty( value= "id")
    private Integer id;
    @ApiModelProperty( value= "题id")
    private Integer questionId;
    @ApiModelProperty( value= "选项")
    private String optionContent;
    @ApiModelProperty( value= "1正确答案 0不正确")
    private Integer correct;
}

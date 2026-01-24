package com.example.webapp.DO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@ApiModel( "题目选项表")
@Data
public class QuestionOptionDO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty( value= "id")
    private Integer id;
    @ApiModelProperty( value= "题id")
    private Integer questionId;
    @ApiModelProperty( value= "选项")
    private String optionContent;
    @ApiModelProperty( value= "1正确答案 0不正确")
    private Integer correct;
    @ApiModelProperty( value= "0,表示正常；1,表示删除",hidden = true)
    private Integer isDelete;
    @ApiModelProperty( value= "更新时间",hidden = true)
    private Date updateTime;
    @ApiModelProperty( value= "创建日期",hidden = true)
    private Date createTime;
}

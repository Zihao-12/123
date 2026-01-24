package com.example.webapp.DO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@ApiModel( "题目难度等级关联表")
@Data
public class QuestionLevelRefDO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty( value= "id")
    private Integer id;
    @ApiModelProperty( value= "题目ID")
    private Integer questionId;
    @ApiModelProperty( value= "1星 2星 3星 4星 5星")
    private Integer level;
    @ApiModelProperty( value= "0,表示正常；1,表示删除",hidden = true)
    private Integer isDelete;
    @ApiModelProperty( value= "更新时间",hidden = true)
    private Date updateTime;
    @ApiModelProperty( value= "创建日期",hidden = true)
    private Date createTime;
}

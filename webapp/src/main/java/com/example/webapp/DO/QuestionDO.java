package com.example.webapp.DO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@ApiModel( "题表")
@Data
public class QuestionDO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty( value= "id")
    private Integer id;
    @ApiModelProperty( value= "题干")
    private String topic;
    @ApiModelProperty( value= "1.单选 2.多选 3.判断")
    private Integer type;
    @ApiModelProperty( value= "0下架1.上架")
    private Integer status;
    @ApiModelProperty( value= "编辑删除 0正常  1编辑删除-编辑时被试卷引用则删除，",hidden = true)
    private Integer editDel;
    @ApiModelProperty( value= "0正常；1删除 ",hidden = true)
    private Integer isDelete;
    @ApiModelProperty( value= "更新时间",hidden = true)
    private Date updateTime;
    @ApiModelProperty( value= "创建日期",hidden = true)
    private Date createTime;
    @ApiModelProperty( value= "选项")
    private List<QuestionOptionDO> optionList;
    @ApiModelProperty( value= "试题难度")
    private List<Integer> levelList;
    @ApiModelProperty(notes = "分类ID List (含父ID)")
    private List<Integer> categoryIdList;

}

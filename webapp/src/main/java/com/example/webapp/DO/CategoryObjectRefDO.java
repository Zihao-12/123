package com.example.webapp.DO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@ApiModel( "分类对象关联表")
public class CategoryObjectRefDO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    private Integer id;
    @ApiModelProperty(value = "分类ID")
    private Integer categoryId;
    @ApiModelProperty(value = "年龄分类ID")
    private Integer categoryAgeId;
    @ApiModelProperty(value = "对象ID")
    private Integer objectId;
    @ApiModelProperty(value = "对象类型：1课程")
    private Integer objectType;
    @ApiModelProperty(value = "0,表示正常；1,表示删除" ,hidden = true)
    private Integer isDelete;
    @ApiModelProperty(value = "更新时间" ,hidden = true)
    private Date updateTime;
    @ApiModelProperty(value = "创建日期" ,hidden = true)
    private Date createTime;
}
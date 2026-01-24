package com.example.webapp.DTO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel( "查询分类关联对象数量")
public class CategoryObjNumParam implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "分类ID")
    private List<Integer> actegoryIdList;
    @ApiModelProperty(value = "对象类型：1课程 2.新闻资讯 3活动 4题目")
    private Integer objectType;

}

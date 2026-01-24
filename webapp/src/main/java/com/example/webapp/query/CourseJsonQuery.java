package com.example.webapp.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel( "Json课程表查询")
@Data
public class CourseJsonQuery implements Serializable {

    private static final long serialVersionUID = -8873473813442439780L;
    @ApiModelProperty(value = "分类ID")
    private Integer categoryId;




}

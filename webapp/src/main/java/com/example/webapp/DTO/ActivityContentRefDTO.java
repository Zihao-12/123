package com.example.webapp.DTO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel( "活动答题内容表DTO")
public class ActivityContentRefDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "活动ID")
    private Integer activityId;
    @ApiModelProperty(value = "分类ID")
    private Integer categoryId;
    @ApiModelProperty(value = "题数")
    private Integer questionNum;






}

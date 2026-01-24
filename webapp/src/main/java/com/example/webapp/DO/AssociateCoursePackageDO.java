package com.example.webapp.DO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@ApiModel( "课程关联课程包")
@Data
public class AssociateCoursePackageDO implements Serializable {

    private static final long serialVersionUID = 5743448400803653552L;
    @ApiModelProperty(value = "课程ID")
    private Integer courseId;
    @ApiModelProperty(value = "课程包id集合")
    private List<Integer> coursePackageIdList;
}

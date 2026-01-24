package com.example.webapp.query;

import com.example.webapp.result.ResultPage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;



@ApiModel( "课程包查询")
@Data 
public class CoursePackageQuery extends ResultPage implements Serializable {

    private static final long serialVersionUID = -4249983043983128058L;

    @ApiModelProperty(notes = "课程包名称")
    private String name;
    @ApiModelProperty(value = "包含课程数量")
    private Integer courseNumber;
    @ApiModelProperty(value = "是否上架：1上架 0下架")
    private Integer status;
    @ApiModelProperty(value = "课程包状态：0待使用 1已使用，机构开通只能选择待使用的")
    private Integer used;
    @ApiModelProperty(value = "1.通用包")
    private Integer type;
    @ApiModelProperty(value = "已使用的所有课程包id")
    private List<Integer> coursePackageIdList;
}

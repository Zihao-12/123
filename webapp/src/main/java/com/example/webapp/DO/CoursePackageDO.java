package com.example.webapp.DO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


@ApiModel( "课程包表")
@Data 
public class CoursePackageDO implements Serializable {

    private static final long serialVersionUID = 9056320445762634698L;
    @ApiModelProperty(value = "id")
	private Integer id;
    @ApiModelProperty(value = "课程包名称")
	private String name;
    @ApiModelProperty(value = "课程包简介")
	private String introduction;
    @ApiModelProperty(value = "1.通用包")
	private Integer type;
    @ApiModelProperty(value = "是否上架：1上架 0下架")
    private Integer status;
    @ApiModelProperty(value = "试看设置：0全部可看 1试看第一节 2试看前三节")
    private Integer tryStatus;
    @ApiModelProperty(value = "课程包状态：0待使用 1已使用，机构开通只能选择待使用的")
    private Integer used;
    @ApiModelProperty(value = "创建时间" ,hidden = true)
    private Date createTime;
    @ApiModelProperty(value = "更新时间" ,hidden = true)
    private Date updateTime;
    @ApiModelProperty(value = "0正常 1删除" ,hidden = true)
    private Integer isDelete;
    @ApiModelProperty(value = "课程包关联课程 ")
	private List<CoursePackageRefDO> coursePackageRefList;
}

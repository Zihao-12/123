package com.example.webapp.DO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@ApiModel( "活动表表")
public class ActivityDO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    private Integer id;
    @ApiModelProperty(value = "活动名称")
    private String name;
    @ApiModelProperty(value = "活动简介")
    private String introduction;
    @ApiModelProperty(value = "活动封面")
    private String cover;
    @ApiModelProperty(value = "活动详情")
    private String detail;
    @ApiModelProperty(value = "活动类型(维护分类)")
    private Integer type;
    @ApiModelProperty(value = "适合年龄(维护分类)")
    private Integer age;
    @ApiModelProperty(value = "活动形式(维护分类)")
    private Integer shape;
    @ApiModelProperty(value = "是否上架：1上架 0下架")
    private Integer status;
    @ApiModelProperty(value = "开始时间")
    private Date beginTime;
    @ApiModelProperty(value = "结束时间")
    private Date endTime;
    @ApiModelProperty(value = "总题数")
    private Integer questionNum;
    @ApiModelProperty(value = "总分值")
    private Integer totalScore;
    @ApiModelProperty(value = "选题规则：1手动配置 0系统分配")
    private Integer manual;
    @ApiModelProperty(value = "0正常 1删除" ,hidden = true)
    private Integer isDelete;
    @ApiModelProperty(value = "更新时间" ,hidden = true)
    private Date updateTime;
    @ApiModelProperty(value = "创建时间" ,hidden = true)
    private Date createTime;
}

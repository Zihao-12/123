package com.example.webapp.DO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@ApiModel( "造访问量假数据(每天根据各日志表生成)")
public class StUserFakeDataDO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    private Integer id;
    @ApiModelProperty(value = "机构id")
    private Integer mechanismId;
    @ApiModelProperty(value = "用户id")
    private Integer userId;
    @ApiModelProperty(value = "访问量(含假数据)")
    private Integer num;
    @ApiModelProperty(value = "访问量(真实数据)")
    private Integer realNum;
    @ApiModelProperty(value = "分类ID")
    private Integer categoryId;
    @ApiModelProperty(value = "分类名称")
    private String categoryName;
    @ApiModelProperty(value = "数据时间")
    private Date date;
    @ApiModelProperty(value = "类型：1.书房(buy-course-list) 2.课程详情(course/view) 3.活动（含详情）(activity/list && /view ) 4.排行榜(/ranking-list) 5.我的(/common/my-info) 6.课程分类听课时长 7.课程年龄听课时长 8.听课时长")
    private Integer type;
    @ApiModelProperty(value = "0正常 1删除" ,hidden = true)
    private Integer isDelete;
    @ApiModelProperty(value = "更新时间" ,hidden = true)
    private Date updateTime;
    @ApiModelProperty(value = "创建时间" ,hidden = true)
    private Date createTime;
}

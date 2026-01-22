package com.example.webapp.DO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@ApiModel( "课程表")
@Data
public class CourseDO implements Serializable {

    private static final long serialVersionUID = -2328301804648328046L;
    @ApiModelProperty(value = "id",hidden = true)
    private Integer id;
    @ApiModelProperty(value = "课程名称")
    private String name;
    @ApiModelProperty(value = "课程简介")
    private String introduction;
    @ApiModelProperty(value = "课程封面")
    private String cover;
    @ApiModelProperty(value = "课程详情")
    private String detail;
    @ApiModelProperty(value = "内容分类ID")
    private Integer contentCategoryId;
    @ApiModelProperty(value = "课节数量",hidden = true)
    private int courseSectionNumber;
    @ApiModelProperty(value = "1.视频课 2.签到视频",hidden = true)
    private Integer type;
    @ApiModelProperty(notes = "机构ID（自建课）",hidden = true)
    private Integer mechanismId;
    @ApiModelProperty(value = "是否上架：1上架 0下架")
    private Integer status;
    @ApiModelProperty(value = "创建时间" ,hidden = true)
    private Date createTime;
    @ApiModelProperty(value = "更新时间" ,hidden = true)
    private Date updateTime;
    @ApiModelProperty(value = "0正常 1删除" ,hidden = true)
    private Integer isDelete;
    @ApiModelProperty(value = "课节列表（签到视频 中的视频ID 对应课程的一个 节（前端根据 视频ID 生成 节））")
    private List<CourseSectionDO> sectionList;

    @ApiModelProperty(notes = "标签（叶子）分类ID List,含标签父分类ID")
    private List<Integer> categoryIdList;
    @ApiModelProperty(notes = "年龄分类ID List")
    private List<Integer> categoryAgeIdList;

}

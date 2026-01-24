package com.example.webapp.DO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel("用户最后一次学习课节id")
public class StUserLearnLastIdDO {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "id")
    private Integer id;
    @ApiModelProperty(value = "机构id")
    private Integer mechanismId;
    @ApiModelProperty(value = "用户id")
    private Integer userId;
    @ApiModelProperty(value = "课程id")
    private Integer courseId;
    @ApiModelProperty(value = "课节id")
    private Integer courseSectionId;
    @ApiModelProperty(value = "最后一次听课时间",hidden = true)
    private Date lastDate;
    @ApiModelProperty(value = "is_delete",hidden = true)
    private Integer isDelete;
    @ApiModelProperty(value = "update_time",hidden = true)
    private Date updateTime;
    @ApiModelProperty(value = "create_time",hidden = true)
    private Date createTime;
}

package com.example.webapp.DO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@ApiModel( "用户活动表")
public class UserActivityRefDO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    private Integer id;
    @ApiModelProperty(value = "用户ID",hidden = true)
    private Integer userId;
    @ApiModelProperty(value = "活动ID")
    private Integer activityId;
    @ApiModelProperty(value = "机构ID",hidden = true)
    private Integer mechanismId;
    @ApiModelProperty(value = "最高得分")
    private Integer score;
    @ApiModelProperty(value = "答对数量",hidden = true)
    private Integer correctNum;
    @ApiModelProperty(value = "最高得分用时(秒)")
    private Integer times;
    @ApiModelProperty(value = "0未完成 1完成活动")
    private Integer complete;
    @ApiModelProperty(value = "0正常 1删除" ,hidden = true)
    private Integer isDelete;
    @ApiModelProperty(value = "更新时间" ,hidden = true)
    private Date updateTime;
    @ApiModelProperty(value = "创建时间" ,hidden = true)
    private Date createTime;
}
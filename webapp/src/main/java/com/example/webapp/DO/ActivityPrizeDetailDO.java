package com.example.webapp.DO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@ApiModel( "活动奖品明细表")
public class ActivityPrizeDetailDO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id",hidden = true)
    private Integer id;
    @ApiModelProperty(value = "活动ID")
    private Integer activityId;
    @ApiModelProperty(value = "奖品类型：0不中奖 1实物 2积分")
    private Integer prizeType;
    @ApiModelProperty(value = "奖品名称")
    private String prizeName;
    @ApiModelProperty(value = "奖品数量")
    private Integer prizeNum;
    @ApiModelProperty(value = "剩余奖品数量")
    private Integer surplusPrizeNum;
    @ApiModelProperty(value = "奖品权重(比例)")
    private Integer prizeWeight;
    @ApiModelProperty(value = "0正常 1删除" ,hidden = true)
    private Integer isDelete;
    @ApiModelProperty(value = "更新时间" ,hidden = true)
    private Date updateTime;
    @ApiModelProperty(value = "创建时间" ,hidden = true)
    private Date createTime;
}
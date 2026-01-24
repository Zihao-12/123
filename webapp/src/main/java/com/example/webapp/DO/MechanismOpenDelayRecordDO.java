package com.example.webapp.DO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@ApiModel( "代码帮助表")
@Data
public class MechanismOpenDelayRecordDO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    private Integer id;
    @ApiModelProperty(value = "mechanism_open_id")
    private Integer mechanismOpenId;
    @ApiModelProperty(value = "delay_days")
    private Integer delayDays;
    @ApiModelProperty(value = "begin_time")
    private Date beginTime;
    @ApiModelProperty(value = "end_time")
    private Date endTime;
    @ApiModelProperty(value = "last_open_days")
    private Integer lastOpenDays;
    @ApiModelProperty(value = "last_end_time")
    private Date lastEndTime;
    @ApiModelProperty(value = "is_delay")
    private Integer isDelay;
    @ApiModelProperty(value = "description")
    private String description;
    @ApiModelProperty(value = "is_delete")
    private Integer isDelete;
    @ApiModelProperty(value = "update_time")
    private Date updateTime;
    @ApiModelProperty(value = "create_time")
    private Date createTime;
}
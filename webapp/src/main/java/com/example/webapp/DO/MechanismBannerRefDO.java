package com.example.webapp.DO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@ApiModel( "机构轮播图关联表")
@Data
public class MechanismBannerRefDO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    private Integer id;
    @ApiModelProperty(value = "mechanism_id")
    private Integer mechanismId;
    @ApiModelProperty(value = "banner_id")
    private Integer bannerId;
    @ApiModelProperty(value = "is_delete")
    private Integer isDelete;
    @ApiModelProperty(value = "update_time")
    private Date updateTime;
    @ApiModelProperty(value = "create_time")
    private Date createTime;
}
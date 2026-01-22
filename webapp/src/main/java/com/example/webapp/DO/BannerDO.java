package com.example.webapp.DO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@ApiModel( "轮播图")
public class BannerDO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    private Integer id;
    @ApiModelProperty(value = "轮播图类型 1.H5首页 2.小程序首页")
    private Integer type;
    @ApiModelProperty(value = "0下架 1上架")
    private Integer status;
    @ApiModelProperty(value = "轮播图名称")
    private String name;
    @ApiModelProperty(value = "轮播图图片地址(H5/小程序)")
    private String imageUrl;
    @ApiModelProperty(value = "轮播图图片地址(PC)")
    private String imageUrlPc;
    @ApiModelProperty(value = "跳转地址类型: 1.站内 2.外链 3.站内H5")
    private Integer jumpType;
    @ApiModelProperty(value = "跳转地址")
    private String jumpUrl;
    @ApiModelProperty(value = "排序:正序" )
    private Integer sort;
    @ApiModelProperty(value = "机构ID（0运营）",hidden = true)
    private Integer mechanismId;
    @ApiModelProperty(value = "创建时间" ,hidden = true)
    private Date createTime;
    @ApiModelProperty(value = "更新时间" ,hidden = true)
    private Date updateTime;
    @ApiModelProperty(value = "0正常 1删除" ,hidden = true)
    private Integer isDelete;
}

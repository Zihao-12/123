package com.example.webapp.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel( "轮播图vo")
public class BannerVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    private Integer id;
    @ApiModelProperty(value = "轮播图名称")
    private String name;
    @ApiModelProperty(value = "轮播图图片地址")
    private String imageUrl;
    @ApiModelProperty(value = "轮播图图片地址(PC)")
    private String imageUrlPc;
    @ApiModelProperty(value = "跳转地址类型: 1.站内 2.外链")
    private Integer jumpType;
    @ApiModelProperty(value = "跳转地址")
    private String jumpUrl;
    @ApiModelProperty(value = "排序:正序" )
    private Integer sort;
}

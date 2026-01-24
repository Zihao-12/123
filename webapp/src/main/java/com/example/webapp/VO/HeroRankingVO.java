package com.example.webapp.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel( "排名用户vo")
public class HeroRankingVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "排名")
    private Long rank;
    @ApiModelProperty(value = "用户ID")
    private Integer userId;
    @ApiModelProperty(value = "用户名")
    private String name;
    @ApiModelProperty(value = "机构ID")
    private Integer mechanismId;
    @ApiModelProperty(value = "机构名")
    private String mechanismName;
    @ApiModelProperty(value = "头像")
    private String headImg;
    @ApiModelProperty(value = "得分")
    private Integer score;
    @ApiModelProperty(value = "用时")
    private Integer times;
    @ApiModelProperty(value = "false 未上榜")
    private boolean onlist = true;

    @ApiModelProperty(value = "PC-机构显示名称" )
    private String showName;
    @ApiModelProperty(value = "H5/小程序/APP-机构显示名称" )
    private String appShowName;
    @ApiModelProperty(value = "PC-登陆页logo" )
    private String loginLogo;
    @ApiModelProperty(value = "H5/小程序/APP-登陆页logo" )
    private String appLoginLogo;

}
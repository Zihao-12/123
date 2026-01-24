package com.example.webapp.DO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@ApiModel( "机构表")
@Data
public class MechanismDO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id非空时编辑")
    private Integer id;
    @ApiModelProperty(value = "机构名称",example = "清华大学")
    private String name;
    @ApiModelProperty(value = "账号不可重复")
    private String account;
    @ApiModelProperty(value = "密码")
    private String password;
    @ApiModelProperty(value = "机构属性 2普通本科 3高职高专")
    private Integer attribute;
    @ApiModelProperty(value = "省份ID")
    private Integer province;
    @ApiModelProperty(value = "城市ID")
    private Integer city;
    @ApiModelProperty(value = "详细地址")
    private String address;
    @ApiModelProperty(value = "PC-网站域名")
    private String domain;
    @ApiModelProperty(value = "PC-导航栏logo")
    private String navbarLogo;
    @ApiModelProperty(value = "PC-登陆页logo")
    private String loginLogo;
    @ApiModelProperty(value = "PC-机构显示名称")
    private String showName;
    @ApiModelProperty(value = "H5/小程序/APP-登陆页logo")
    private String appLoginLogo;
    @ApiModelProperty(value = "H5/小程序/APP-导航栏logo")
    private String appNavbarLogo;
    @ApiModelProperty(value = "H5/小程序/APP-网站域名")
    private String appDomain;
    @ApiModelProperty(value = "H5/小程序/APP-机构显示名称")
    private String appShowName;
    @ApiModelProperty(value = "限制IP：0否 1是" ,hidden = true)
    private Integer ipRestrict;
    @ApiModelProperty(value = "创建时间" ,hidden = true)
    private Date createTime;
    @ApiModelProperty(value = "更新时间" ,hidden = true)
    private Date updateTime;
    @ApiModelProperty(value = "0正常 1删除" ,hidden = true)
    private Integer isDelete;

}

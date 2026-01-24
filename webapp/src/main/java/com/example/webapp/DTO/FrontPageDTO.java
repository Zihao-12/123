package com.example.webapp.DTO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class FrontPageDTO implements Serializable {
    @ApiModelProperty(value = "机构名称")
    private String name;
    @ApiModelProperty(value = "PC-机构显示名称")
    private String showName;
    @ApiModelProperty(value = "H5/小程序/APP-机构显示名称")
    private String appShowName;


    @ApiModelProperty(value = "PC-登陆页logo")
    private String loginLogo;
    @ApiModelProperty(value = "H5/小程序/APP-登陆页logo")
    private String appLoginLogo;

    @ApiModelProperty(value = "PC-导航栏logo")
    private String navbarLogo;
    @ApiModelProperty(value = "H5/小程序/APP-导航栏logo")
    private String appNavbarLogo;

}

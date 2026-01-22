package com.example.webapp.utils;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;


/**
 * @author gehaisong
 */
@ApiModel( "微信用户电话结果DTO")
@Data 
public class WXPhoneNumberDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	@ApiModelProperty( notes= "用户绑定的手机号（国外手机号会有区号）" ,value = "phoneNumber")
	private String phoneNumber;
	@ApiModelProperty( notes= "没有区号的手机号" ,value = "purePhoneNumber")
	private String purePhoneNumber;
	@ApiModelProperty( notes= "区号" ,value = "countryCode")
	private String countryCode;
	@ApiModelProperty( notes= "watermark" ,value = "watermark")
	private String watermark;

}

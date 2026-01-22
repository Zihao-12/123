package com.example.webapp.utils;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;


/**
 * @author gehaisong
 */
@ApiModel( "微信用户信息结果DTO")
@Data 
public class WXUserInfoDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	@ApiModelProperty( notes= "用户唯一标识" ,value = "openid")
	private String openid;
	@ApiModelProperty( notes= "微信昵称" ,value = "nickName")
	private String nickName;
	@ApiModelProperty( notes= "性别" ,value = "gender")
	private Integer gender;
	@ApiModelProperty( notes= "头像" ,value = "avatarUrl")
	private String avatarUrl;
}

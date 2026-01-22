package com.example.webapp.utils;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;


/**
 * @author gehaisong
 */
@ApiModel( "微信code2Session结果DTO")
@Data 
public class WXCode2SessionDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	@ApiModelProperty( notes= "用户唯一标识" ,value = "openid")
	private String openid;
	@ApiModelProperty( notes= "会话密钥" ,value = "session_key")
	private String session_key;
	@ApiModelProperty( notes= "用户在开放平台的唯一标识符" ,value = "unionid")
	private String unionid;
	@ApiModelProperty( notes= "错误码" ,value = "errcode")
	private Integer errcode;
	@ApiModelProperty( notes= "错误信息" ,value = "errmsg")
	private String errmsg;

}

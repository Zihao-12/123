package com.example.webapp.DTO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel( "用户信息设置")
@Data
public class UpdateUserDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty( value = "头像")
    private String headImg;
    @ApiModelProperty( value = "昵称")
    private String nickName;
    @ApiModelProperty( value = "0未设置 1男 2女")
    private Integer gender;
    @ApiModelProperty( value = "年龄")
    private Integer age;


}

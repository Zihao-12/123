package com.example.webapp.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel( "机构账号vo")
public class AccountVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "true 账号已存在")
    private boolean exist;
    @ApiModelProperty(value = "账号存在时的机构ID")
    private Integer mechanismId;
}

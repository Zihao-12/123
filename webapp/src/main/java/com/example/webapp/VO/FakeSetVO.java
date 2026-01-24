package com.example.webapp.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel( "造数据配置vo")
public class FakeSetVO implements Serializable {
    private static final long serialVersionUID = 1L;


    @ApiModelProperty(value = "基数")
    private Integer baseValue;
    @ApiModelProperty(value = "倍数")
    private Integer multiplier;
}


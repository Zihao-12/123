package com.example.webapp.DTO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel( "兴趣分布dto")
public class InterestDTO implements Serializable {

    @ApiModelProperty(value = "id")
    private Integer categoryId;
    @ApiModelProperty(value = "名称")
    private String categoryName;
    @ApiModelProperty(value = "数量")
    private Integer number;
}

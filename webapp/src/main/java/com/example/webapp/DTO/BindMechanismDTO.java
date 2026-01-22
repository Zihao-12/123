package com.example.webapp.DTO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel( "对象绑定到指定机构dto")
public class BindMechanismDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "绑定机构的对象ID",required = true)
    private Integer id;
    @ApiModelProperty(value = "机构ID列表")
    private List<Integer> mechanismIdList;
}

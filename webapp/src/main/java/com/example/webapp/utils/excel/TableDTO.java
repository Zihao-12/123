package com.example.webapp.utils.excel;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel("表结构dto")
public class TableDTO implements Serializable {

    @ApiModelProperty(value = "类路径")
    private String clazz;
    @ApiModelProperty(value = "属性集合")
    private List<TableFieldDTO> fieldList;
}

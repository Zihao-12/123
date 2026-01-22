package com.example.webapp.utils.excel;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("表字段dto")
public class TableFieldDTO implements Serializable {

    @ApiModelProperty(value = "字段")
    private String field;
    @ApiModelProperty(value = "字段名")
    private String fieldName;
    @ApiModelProperty(value = "字段类型")
    private String fieldType;
}

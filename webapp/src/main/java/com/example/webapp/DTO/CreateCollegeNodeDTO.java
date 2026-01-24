package com.example.webapp.DTO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel
public class CreateCollegeNodeDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "父节点ID")
    private Integer parentId;
    @ApiModelProperty(value = "节点类型:0院系,1班级")
    private Integer nodeType;
    @ApiModelProperty(value = "新结点名称列表",example = "[\"经济学院\",\"文学院\"]")
    private List<String> collegeNameList;
}

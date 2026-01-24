package com.example.webapp.DTO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@ApiModel("分类CreateNodeDTO")
@Data
public class CreateNodeDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "父节点ID",required = true)
    private Integer parentId;
    @ApiModelProperty(value = "节点类型:0非叶节点,1叶节点",required = true)
    private Integer nodeType;
    @ApiModelProperty(value = "分类名称集合",example = "[\"经济学院\",\"文学院\"]",required = true)
    private List<String> nodeNameList;
}

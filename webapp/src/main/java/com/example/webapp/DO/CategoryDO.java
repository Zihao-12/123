package com.example.webapp.DO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@ApiModel( "分类表")
public class CategoryDO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    private Integer id;
    @ApiModelProperty(value = "节点名称")
    private String name;
    @ApiModelProperty(value = "0根节点")
    private Integer parentId;
    @ApiModelProperty(value = "id全路径")
    private String idFullPath;
    @ApiModelProperty(value = "name全路径")
    private String nameFullPath;
    @ApiModelProperty(value = "1叶子节点")
    private Integer leafNode;
    /**
     *可建孩子节点类型：1部门 2班级(含班级的节点) 3部门班级
     */
    @ApiModelProperty(value = "child_type")
    private Integer childType;
    @ApiModelProperty(value = "排序")
    private Integer sort;
    @ApiModelProperty(value = "0,表示正常；1,表示删除" ,hidden = true)
    private Integer isDelete;
    @ApiModelProperty(value = "更新时间" ,hidden = true)
    private Date updateTime;
    @ApiModelProperty(value = "创建日期" ,hidden = true)
    private Date createTime;
}

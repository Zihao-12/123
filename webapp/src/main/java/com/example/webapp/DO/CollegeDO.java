package com.example.webapp.DO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@ApiModel( "学院")
@Data
public class CollegeDO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    private Integer id;
    @ApiModelProperty(value = "name")
    private String name;
    @ApiModelProperty(value = "description")
    private String description;
    /**
     * 0表示是机构 node_type=0
     */
    @ApiModelProperty(value = "parent_id")
    private Integer parentId;
    /**
     * 机构ID
     */
    @ApiModelProperty(value = "mechanism_id")
    private Integer mechanismId;

    /**
     * id全路径
     */
    @ApiModelProperty(value = "id_full_path")
    private String idFullPath;

    /**
     * name全路径
     */
    @ApiModelProperty(value = "name_full_path")
    private String nameFullPath;

    /**
     * 0院系 1班级
     */
    @ApiModelProperty(value = "node_type")
    private Integer nodeType;
    /**
     *可建孩子节点类型：1部门(ROOT节点) 2班级(含班级的节点) 3部门班级
     */
    @ApiModelProperty(value = "child_type")
    private Integer childType;

    @ApiModelProperty(value = "sort")
    private Integer sort;
    @ApiModelProperty(value = "is_delete")
    private Integer isDelete;
    @ApiModelProperty(value = "update_time")
    private Date updateTime;
    @ApiModelProperty(value = "create_time")
    private Date createTime;

}

package com.example.webapp.DO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@ApiModel( "机构联系人表")
@Data
public class MechanismContactPersonDO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id非空时编辑")
    private Integer id;
    @ApiModelProperty(value = "机构ID")
    private Integer mechanismId;
    @ApiModelProperty(value = "name")
    private String name;
    @ApiModelProperty(value = "phone")
    private String phone;
    @ApiModelProperty(value = "email")
    private String email;
    @ApiModelProperty(value = "remark")
    private String remark;
    @ApiModelProperty(value = "创建时间" ,hidden = true)
    private Date createTime;
    @ApiModelProperty(value = "更新时间" ,hidden = true)
    private Date updateTime;
    @ApiModelProperty(value = "0正常 1删除" ,hidden = true)
    private Integer isDelete;
}

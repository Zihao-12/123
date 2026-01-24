package com.example.webapp.DO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@ApiModel( "机构开通表")
@Data
public class MechanismOpenDO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id非空时编辑")
    private Integer id;
    @ApiModelProperty(value = "机构ID")
    private Integer mechanismId;
    @ApiModelProperty(value = "实训开通课程包ID")
    private Integer coursePackageId;
    @ApiModelProperty(value = "开通天数")
    private Integer openDays;
    @ApiModelProperty(value = "开始时间")
    private Date beginTime;
    @ApiModelProperty(value = "结束时间")
    private Date endTime;
    @ApiModelProperty(value = "机构帐号最大数量限制,默认0不限制")
    private Integer accountNumber;
    @ApiModelProperty(value = "0已停用 1已启用")
    private Integer status;
    @ApiModelProperty(value = "开通类型：0 通用开通 ")
    private Integer openType;
    @ApiModelProperty(value = "创建时间" ,hidden = true)
    private Date createTime;
    @ApiModelProperty(value = "更新时间" ,hidden = true)
    private Date updateTime;
    @ApiModelProperty(value = "0正常 1删除" ,hidden = true)
    private Integer isDelete;


}

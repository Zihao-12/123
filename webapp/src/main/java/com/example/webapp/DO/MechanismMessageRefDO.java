package com.example.webapp.DO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@ApiModel( "机构消息关联表")
public class MechanismMessageRefDO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    private Integer id;
    @ApiModelProperty(value = "机构ID")
    private Integer mechanismId;
    @ApiModelProperty(value = "消息ID")
    private Integer messageId;
    @ApiModelProperty(value = "0,表示正常；1,表示删除" ,hidden = true)
    private Integer isDelete;
    @ApiModelProperty(value = "更新时间" ,hidden = true)
    private Date updateTime;
    @ApiModelProperty(value = "创建日期" ,hidden = true)
    private Date createTime;
}

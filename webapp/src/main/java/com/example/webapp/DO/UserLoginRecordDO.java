package com.example.webapp.DO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@ApiModel( "用户登录记录表")
@Data
public class UserLoginRecordDO implements Serializable {

    private static final long serialVersionUID = -4847897417754807421L;
    @ApiModelProperty(value = "id")
    private Integer id;
    @ApiModelProperty(notes = "机构id")
    private Integer mechanismId;
    @ApiModelProperty(notes = "用户id")
    private Integer userId;
    @ApiModelProperty(notes = "记录日期")
    private Integer day;
    @ApiModelProperty(notes = "0正常 其它值删除")
    private Integer isDelete;
    @ApiModelProperty(notes = "更新时间")
    private Date updateTime;
    @ApiModelProperty(notes = "创建时间")
    private Date createTime;
}

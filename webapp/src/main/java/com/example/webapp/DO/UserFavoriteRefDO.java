package com.example.webapp.DO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
@Data
@ApiModel( "用户收藏表")
public class UserFavoriteRefDO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    private Integer id;
    @ApiModelProperty(value = "机构id")
    private Integer mechanismId;
    @ApiModelProperty(value = "用户id")
    private Integer userId;
    @ApiModelProperty(value = "对象ID")
    private Integer objectId;
    @ApiModelProperty(value = "对象类型：1课程")
    private Integer objectType;
    @ApiModelProperty(value = "0正常 1删除" ,hidden = true)
    private Integer isDelete;
    @ApiModelProperty(value = "更新时间" ,hidden = true)
    private Date updateTime;
    @ApiModelProperty(value = "创建时间" ,hidden = true)
    private Date createTime;
}

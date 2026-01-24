package com.example.webapp.DO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@ApiModel( "消息管理表")
public class MessageDO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    private Integer id;
    @ApiModelProperty(value = "标题")
    private String name;
    @ApiModelProperty(value = "正文内容富文本")
    private String details;
    @ApiModelProperty(value = "附件上传")
    private String courseware;
    @ApiModelProperty(value = "附件文件名")
    private String coursewareName;
    @ApiModelProperty(value = "0待发布 1已发布")
    private Integer status;
    @ApiModelProperty(value = "机构ID（0运营）",hidden = true)
    private Integer mechanismId;
    @ApiModelProperty(value = "创建时间" ,hidden = true)
    private Date createTime;
    @ApiModelProperty(value = "更新时间" ,hidden = true)
    private Date updateTime;
    @ApiModelProperty(value = "0正常 1.删除" ,hidden = true)
    private Integer isDelete;
}

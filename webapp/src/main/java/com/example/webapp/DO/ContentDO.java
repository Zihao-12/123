package com.example.webapp.DO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@ApiModel( "新闻资讯-内容")
public class ContentDO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    private Integer id;
    @ApiModelProperty(value = "类型1.通用资讯 ")
    private Integer type;
    @ApiModelProperty(value = "内容类型1.图文 2.视频")
    private Integer contentType;
    @ApiModelProperty(value = "category表主键")
    private Integer categoryId;
    @ApiModelProperty(value = "列表样式 0.顶部 1无配图 2.底部 3.左侧 4.右侧")
    private Integer style;
    @ApiModelProperty(value = "标题")
    private String title;
    @ApiModelProperty(value = "视频地址")
    private String videoUrl;
    @ApiModelProperty(value = "新闻来源(参照字典表)")
    private Integer source;
    @ApiModelProperty(value = "正文内容富文本")
    private String details;
    @ApiModelProperty(value = "0下架 1上架")
    private Integer status;
    @ApiModelProperty(value = "0未置顶 1.置顶")
    private Integer top;
    @ApiModelProperty(value = "是否推荐:0否 1.是")
    private Integer recommend;
    @ApiModelProperty(value = "排序:正序")
    private Integer sort;
    @ApiModelProperty(value = "创建时间" ,hidden = true)
    private Date createTime;
    @ApiModelProperty(value = "更新时间" ,hidden = true)
    private Date updateTime;
    @ApiModelProperty(value = "0正常 1删除" ,hidden = true)
    private Integer isDelete;
    @ApiModelProperty(value = "图片地址: 0.50|&-&|content/953689289189752832.jpg|&-&|4冬奥志愿者宣传页设计.jpg")
    private List<String> imageUrlList;
}

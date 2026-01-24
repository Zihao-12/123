package com.example.webapp.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@ApiModel( "新闻资讯内容vo")
public class ContentVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    private Integer id;
    @ApiModelProperty(value = "类型1.通用资讯 ")
    private Integer type;
    @ApiModelProperty(value = "内容类型1.图文 2.视频")
    private Integer contentType;
    @ApiModelProperty(value = "列表样式 0.顶部 1无配图 2.底部 3.左侧 4.右侧")
    private Integer style;
    @ApiModelProperty(value = "标题")
    private String title;
    @ApiModelProperty(value = "视频地址")
    private String videoUrl;
    @ApiModelProperty(value = "新闻来源(参照字典表)",hidden = true)
    private Integer source;
    @ApiModelProperty(value = "新闻来源(参照字典表)")
    private String sourceCn;
    @ApiModelProperty(value = "正文内容富文本")
    private String details;
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
    @ApiModelProperty(value = "页面浏览量")
    private Integer pageView;
    @ApiModelProperty(value = "图片地址用逗号拼接",hidden=true)
    private String imageUrls;
    @ApiModelProperty(value = "图片地址")
    private List<String> imageUrlList;

}
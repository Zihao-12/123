package com.example.webapp.Query;

import com.example.webapp.result.ResultPage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel( "轮播图query")
public class BannerQuery extends ResultPage implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(notes = "名称")
    private String name;
    @ApiModelProperty(notes = "0下架 1上架")
    private Integer status;
    @ApiModelProperty(notes = "轮播图类型 1.H5首页 2.小程序首页",hidden = true)
    private Integer type;
    @ApiModelProperty(value = "机构ID（0运营）",hidden = true)
    private Integer mechanismId;
}

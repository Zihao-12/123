package com.example.webapp.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@ApiModel( "造假数据图表vo")
public class FakeDataVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "日期")
    private Date t;
    @ApiModelProperty(value = "值")
    private Integer v;
    @ApiModelProperty(value = "真实值")
    private Integer rv;
    @ApiModelProperty(value = "分类名称")
    private String c;
}


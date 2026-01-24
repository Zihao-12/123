package com.example.webapp.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel( "造假数据图表统计页面vo")
public class FakeDataTJVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "列表")
    private List<FakeDataVO> fakeDataVOList;
    @ApiModelProperty(value = "总值")
    private Integer total;
    @ApiModelProperty(value = "真实总值")
    private Integer realTotal;
}


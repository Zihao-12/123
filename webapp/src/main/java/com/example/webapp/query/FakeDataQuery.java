package com.example.webapp.query;

import com.example.webapp.result.ResultPage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@ApiModel( "造假数据query")
public class FakeDataQuery extends ResultPage implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(notes = "开始时间")
    private Date beginTime;
    @ApiModelProperty(notes = "结束时间")
    private Date endTime;
    @ApiModelProperty(notes = "数据类型",hidden = true)
    private List<Integer> typeList;
    @ApiModelProperty(value = "机构ID（0运营）")
    private Integer mechanismId;

}
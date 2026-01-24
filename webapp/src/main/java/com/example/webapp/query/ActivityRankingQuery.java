package com.example.webapp.query;

import com.example.webapp.result.ResultPage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel( "活动排行榜query")
public class ActivityRankingQuery extends ResultPage implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "0全国 1馆内排行")
    private Integer type;
    @ApiModelProperty(value = "活动ID")
    private Integer activityId;
    @ApiModelProperty(value = "排行榜数量")
    private Integer top;
    @ApiModelProperty(value = "机构ID",hidden = true)
    private Integer mechanismId;
    @ApiModelProperty(value = "用户ID",hidden = true)
    private Integer userId;
}
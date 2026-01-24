package com.example.webapp.query;

import com.example.webapp.result.ResultPage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel( "活动表表query")
public class ActivityQuery extends ResultPage implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(notes = "搜索关键字")
    private String name;
    @ApiModelProperty(notes = "是否上架：1上架 0下架")
    private Integer status;
    @ApiModelProperty(value = "活动状态 1待开始 2进行中 3已结束")
    private Integer activityStatus;
    @ApiModelProperty(value = "活动类型(维护分类)")
    private Integer type;
    @ApiModelProperty(value = "适合年龄(维护分类)")
    private Integer age;
    @ApiModelProperty(value = "活动形式(维护分类)")
    private Integer shape;
    @ApiModelProperty(value = "机构ID（0运营）",hidden = true)
    private Integer mechanismId;
    @ApiModelProperty(value = "用户ID",hidden = true)
    private Integer userId;
    @ApiModelProperty(value = "1 查询用户参加的活动",hidden = true)
    private Integer userJoined;
}
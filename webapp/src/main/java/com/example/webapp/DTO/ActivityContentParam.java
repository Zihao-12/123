package com.example.webapp.DTO;

import com.example.webapp.DO.ActivityContentRefDO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel( "活动答题内容提交类")
public class ActivityContentParam implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "活动ID")
    private Integer activityId;
    @ApiModelProperty(value = "总题数")
    private Integer questionNum;
    @ApiModelProperty(value = "总分值")
    private Integer totalScore;
    @ApiModelProperty(value = "选题规则：1手动配置 0系统分配")
    private Integer manual;
    List<ActivityContentRefDO> activityContentRefDOList;

}

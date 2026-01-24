package com.example.webapp.query;

import com.example.webapp.result.ResultPage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@ApiModel( "题目查询")
@Data
public class QuestionQuery extends ResultPage implements Serializable {
    private static final long serialVersionUID = -3226636602328151880L;

    @ApiModelProperty(value = "试题类型")
    private Integer type;
    @ApiModelProperty(value = "试题难度")
    private Integer level;
    @ApiModelProperty(value = "上下架状态")
    private Integer status;
    @ApiModelProperty(value = "查询关键字",required = false)
    private String topic;

    @ApiModelProperty(value = "分类ID: id1,id2")
    private List<Integer> categoryIdList;

}

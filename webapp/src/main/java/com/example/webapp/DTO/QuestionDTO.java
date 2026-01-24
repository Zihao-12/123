package com.example.webapp.DTO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@ApiModel( "题表DTO")
@Data
public class QuestionDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty( value= "id")
    private Integer id;
    @ApiModelProperty( value= "题干")
    private String topic;
    @ApiModelProperty( value= "1.单选 2.多选 3.判断")
    private Integer type;
    private String typeCn;

    @ApiModelProperty( value= "0下架1.上架")
    private Integer status;
    @ApiModelProperty( value= "上下架中文")
    private String statusCn;
    @ApiModelProperty( value= "编辑删除 0正常  1编辑删除-编辑时被试卷引用则删除，")
    private Integer editDel;
    @ApiModelProperty( value= "创建日期")
    private Date createTime;

    @ApiModelProperty( value= "试题选项")
    private List<QuestionOptionDTO> optionDTOList;
    @ApiModelProperty( value= "试题难度")
    private List<Integer> levelList;
    @ApiModelProperty(notes = "分类ID 集合")
    private List<Integer> categoryIdList;
    @ApiModelProperty(notes = "分类ID 全链路集合:格式：.3.88.108.@试题分类.阅读理解.测试1")
    private List<String> idFullPathList;
    @ApiModelProperty(notes = "分类名 全链路集合")
    private List<String> nameFullPathList;

    @ApiModelProperty(notes = "试题难度(1星 2星 3星 4星 5星)")
    private List<String> levelNameList;

    @ApiModelProperty(notes = "分类ID(活动抽题)",hidden = true)
    private String categoryIds;

    @ApiModelProperty(notes = "抽题权重 越小越优先")
    private Integer weights=0;
    @ApiModelProperty(notes = "排序随机索引")
    private Integer randomIndex=0;
}
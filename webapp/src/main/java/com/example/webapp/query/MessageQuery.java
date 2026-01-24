package com.example.webapp.query;

import com.example.webapp.result.ResultPage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel( "消息管理表query")
public class MessageQuery extends ResultPage implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(notes = "搜索关键字")
    private String name;
    @ApiModelProperty(value = "0待发布 1已发布")
    private Integer status;
    @ApiModelProperty(value = "机构ID（0运营）",hidden = true)
    private Integer mechanismId;
}

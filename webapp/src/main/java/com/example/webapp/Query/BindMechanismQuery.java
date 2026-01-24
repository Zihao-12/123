package com.example.webapp.Query;

import com.example.webapp.result.ResultPage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel( "查询对象已经绑定的机构列表")
@Data
public class BindMechanismQuery extends ResultPage implements Serializable {
    private static final long serialVersionUID = -3226636602328151880L;

    @ApiModelProperty(value = "查询关键字")
    private String name;
    @ApiModelProperty(value = "绑定机构的对象ID",required = true)
    private Integer id;

}

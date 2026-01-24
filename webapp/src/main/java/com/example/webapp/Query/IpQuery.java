package com.example.webapp.Query;

import com.example.webapp.result.ResultPage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel( "IP query")
public class IpQuery extends ResultPage implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "机构ID",hidden = true)
    private Integer mechanismId;
}

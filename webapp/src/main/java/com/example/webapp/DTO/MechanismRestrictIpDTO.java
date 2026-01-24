package com.example.webapp.DTO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@ApiModel( "机构IP限制表")
@Data
public class MechanismRestrictIpDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    private Integer id;
    @ApiModelProperty(value = "机构ID")
    private Integer mechanismId;
    @ApiModelProperty(value = "ip/掩码位: 218.240.38.234/24",example = "ip")
    private String ip;
    @ApiModelProperty(value = "创建时间" ,hidden = true)
    private Date createTime;


}

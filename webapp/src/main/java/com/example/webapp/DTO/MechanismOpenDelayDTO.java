package com.example.webapp.DTO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class MechanismOpenDelayDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(name = "id",notes = "机构开通ID",example = "37",required = false)
    private Integer id;
    @ApiModelProperty(name = "beginTime",notes = "开始时间")
    private Date beginTime;
    @ApiModelProperty(name = "endTime",notes = "结束时间")
    private Date endTime;
}

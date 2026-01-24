package com.example.webapp.DTO;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@ApiModel( "活动用户答题情况导出DTO")
public class UserAnswersExportDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "机构名称")
    @ExcelProperty( index = 0)
    private String mechanismName;

    @ExcelProperty( index = 1)
    @ApiModelProperty(value = "用户名")
    private String userName;

    @ExcelProperty( index = 2)
    @ApiModelProperty(value = "手机")
    private String phone;

    @ExcelProperty( index = 3)
    @ApiModelProperty(value = "活动ID")
    private Integer activityId;

    @ExcelProperty( index = 4)
    @ApiModelProperty(value = "活动名称")
    private String activityName;

    @ExcelProperty( index = 5)
    @ApiModelProperty(value = "得分")
    private Integer score;

    @ExcelProperty( index = 6)
    @ApiModelProperty(value = "用时(秒)")
    private Double times;

    @ExcelProperty( index = 7)
    @ApiModelProperty(value = "时间")
    private Date updateTime;

}
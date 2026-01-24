package com.example.webapp.DTO;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@ApiModel( "活动用户答题情况导出DTO")
public class LotteryDetailsExportDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    @ExcelProperty( index = 0)
    @ApiModelProperty(value = "活动ID")
    private Integer activityId;

    @ExcelProperty( index = 1)
    @ApiModelProperty(value = "活动名称")
    private String activityName;

    @ApiModelProperty(value = "机构名称")
    @ExcelProperty( index = 2)
    private String mechanismName;

    @ExcelProperty( index = 3)
    @ApiModelProperty(value = "用户名")
    private String userName;

    @ExcelProperty( index = 4)
    @ApiModelProperty(value = "手机")
    private String phone;

    @ExcelProperty( index = 5)
    @ApiModelProperty(value = "奖品")
    private String prizeName;

    @ExcelProperty( index = 6)
    @ApiModelProperty(value = "抽奖时间")
    private Date createTime;

    @ExcelProperty( index = 7)
    @ApiModelProperty(value = "参加活动时间")
    private Date joinTime;




}
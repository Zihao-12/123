package com.example.webapp.utils;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;



@ApiModel( "EasyDO")
@Data
@EqualsAndHashCode
public class EasyDO implements Serializable {
	private static final long serialVersionUID = 1L;
	@ExcelProperty("封面")
	private String cover;

	@ExcelProperty("简介")
	private String introduction;

	@ExcelProperty("名称")
	private String name;

}

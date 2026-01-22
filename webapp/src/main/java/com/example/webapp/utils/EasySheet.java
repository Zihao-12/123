package com.example.webapp.utils;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * @author gehaisong
 *
 */
@Data
public class EasySheet implements Serializable {

	/**
	 * 文件名
	 */
	String fileName;

	/**
	 * sheet名
	 */
	String sheetName;
	/**
	 * 表头字段名
	 */
	String[] headers;

	/**
	 * 数据列表
 	 */
	List<?> dataList;
}
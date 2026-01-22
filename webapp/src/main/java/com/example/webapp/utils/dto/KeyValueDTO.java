package com.example.webapp.utils.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;


/**
 * @author gehaisong
 */
@ApiModel( "keyvalueDTO")
@Data 
public class KeyValueDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer id;
	private String name;
}

package com.example.webapp.DO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@ApiModel( "课程包与课程关联表")
@Data 
public class CoursePackageRefDO implements Serializable {
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "id",hidden = true)
	private Integer id;
	@ApiModelProperty(value = "课程id")
	private Integer courseId;
	@ApiModelProperty(value = "课程包ID",hidden = true)
	private Integer coursePackageId;
	@ApiModelProperty(value = "排序 升序",hidden = true)
	private Integer sort;
	@ApiModelProperty(value = "创建时间" ,hidden = true)
	private Date createTime;
	@ApiModelProperty(value = "更新时间" ,hidden = true)
	private Date updateTime;
	@ApiModelProperty(value = "0正常 1删除" ,hidden = true)
	private Integer isDelete;
}

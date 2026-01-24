package com.example.webapp.DO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@ApiModel( "用户班级关系表")
@Data
public class CollegeUserRefDO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    private Integer id;
    @ApiModelProperty(value = "college_id")
    private Integer collegeId;
    @ApiModelProperty(value = "user_id")
    private Integer userId;
    @ApiModelProperty(value = "position")
    private Integer position;
    @ApiModelProperty(value = "is_delete")
    private Integer isDelete;
    @ApiModelProperty(value = "update_time")
    private Date updateTime;
    @ApiModelProperty(value = "create_time")
    private Date createTime;
    public CollegeUserRefDO(){

    }
    public CollegeUserRefDO(Integer collegeId, Integer userId, Integer position){
        this.collegeId=collegeId;
        this.userId=userId;
        this.position=position;

    }
}
package com.example.webapp.DTO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SelectUserBindDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "班级ID")
    private Integer collegeId;

    @ApiModelProperty(value = "调班-新班级ID")
    private Integer newCollegeId;

    @ApiModelProperty(value = "绑定-班级职务：0学生 1班主任 2助教 用户加入班级时确定和用户角色无关")
    private Integer position;
    @ApiModelProperty(value = "用户ID集合",example = "[\"1\",\"2\"]")
    private List<Integer> userIdList;
}

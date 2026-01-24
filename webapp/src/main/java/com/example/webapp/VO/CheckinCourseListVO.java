package com.example.webapp.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel( "领取签到课程")
public class CheckinCourseListVO implements Serializable {
    private static final long serialVersionUID = -5617551860034624357L;
    @ApiModelProperty(notes = "领取签到课程列表")
    private List<CheckinCourseVO> takeCourseList;
    @ApiModelProperty(notes = "今日签到课程")
    private CheckinCourseVO checkinCourse;

}

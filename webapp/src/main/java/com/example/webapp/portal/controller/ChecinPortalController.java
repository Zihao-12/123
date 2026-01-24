package com.example.webapp.portal.controller;

import com.example.webapp.DTO.UserDto;
import com.example.webapp.Service.Course.CourseService;
import com.example.webapp.Service.checkin.CheckinService;
import com.example.webapp.VO.CheckinCourseListVO;
import com.example.webapp.VO.CheckinCourseVO;
import com.example.webapp.annotation.LoginRequired;
import com.example.webapp.result.Result;
import com.example.webapp.utils.UserThreadLocal;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = {"1009-签到"})
@RefreshScope
@RestController
@Component
@Slf4j
@LoginRequired
@RequestMapping("/api/portal/checkin")
public class ChecinPortalController {
    @Autowired
    CheckinService checkinService;
    @Autowired
    CourseService courseService;

    @ApiOperation(value = "签到课程列表", notes = "签到课程列表,checkinCourse 空 表示今日还没领取签到课程")
    @ApiImplicitParams({@ApiImplicitParam(name = "categoryId", value = "分类ID", required = true)})
    @PostMapping(value = "/course-list/{categoryId}")
    public Result courseList(@PathVariable Integer  categoryId) {
        UserDto userDto = UserThreadLocal.get();
        List<CheckinCourseVO> takeCourseList = checkinService.courseList(categoryId,userDto.getId());
        CheckinCourseVO checkinCourse = checkinService.getTakeCourse(userDto.getId());
        CheckinCourseListVO crl = new CheckinCourseListVO();
        crl.setCheckinCourse(checkinCourse);
        crl.setTakeCourseList(takeCourseList);
        return Result.ok(crl);
    }

    @ApiOperation(value = "领取签到课程", notes = "领取签到课程(非免费自动消耗金豆)")
    @PostMapping(value = "/take-course")
    public Result<CheckinCourseVO> takeCourse() {
        UserDto userDto = UserThreadLocal.get();
        return checkinService.takeCourse(userDto.getId());
    }

    @ApiOperation(value = "完成课程(视频签到积分)", notes = "完成课程(视频签到积分)")
    @ApiImplicitParams({@ApiImplicitParam(name = "courseId", value = "签到课程ID", required = true)})
    @PostMapping(value = "/complete/{courseId}")
    public Result complete(@PathVariable Integer courseId) {
        UserDto userDto = UserThreadLocal.get();
        return checkinService.complete(userDto.getId(),courseId);
    }

    @ApiOperation(value = "用户总积分（金豆）", notes = "用户总积分（金豆）+ 累计签到天数")
    @PostMapping(value = "/get-user-coins")
    public Result getUserCoins() {
        UserDto userDto = UserThreadLocal.get();
        Result result = checkinService.getUserCoins(userDto.getId());
        return result;
    }

}
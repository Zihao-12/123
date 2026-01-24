package com.example.webapp.portal.controller;

import com.example.webapp.DTO.UserDto;
import com.example.webapp.Service.Course.CourseService;
import com.example.webapp.Service.statistics.StatisticsService;
import com.example.webapp.annotation.LoginRequired;
import com.example.webapp.query.CourseQuery;
import com.example.webapp.query.UserLearnRecordQuery;
import com.example.webapp.result.Result;
import com.example.webapp.result.ResultPage;
import com.example.webapp.utils.UserThreadLocal;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = {"007-学习中心"})
@RefreshScope
@RestController
@Component
@LoginRequired
@RequestMapping("/api/portal/learn-center")
public class LearningCenterController {
    @Autowired
    CourseService courseService;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    StatisticsService statisticsService;


    @ApiOperation(value = "已学习课程列表",notes = "已学习课程列表")
    @RequestMapping(value = "/learn-course-list",method = RequestMethod.POST)
    public ResultPage learnCourselist(@RequestBody UserLearnRecordQuery query){
        ResultPage result;
        try {
            UserDto user = UserThreadLocal.get();
            query.setMechanismId(user.getMechanismId());
            query.setUserId(user.getId());
            result = courseService.learnCourselist(query);
        } catch (Exception e) {
            e.printStackTrace();
            result =  ResultPage.fail(e.toString());
        }
        return result;
    }

    /**
     * 继续学习--获取最后一次学习课节id
     * @param query
     * @return
     */
    @ApiOperation(value = "继续学习获取最后一次学习课节id",notes = "继续学习获取最后一次学习课节id")
    @RequestMapping(value="/last-course-section",method = RequestMethod.POST)
    Result lastCourseSectionId(@RequestBody CourseQuery query){
        Result result;
        try {
            UserDto user = UserThreadLocal.get();
            query.setMechanismId(user.getMechanismId());
            query.setUserId(user.getId());
            Integer lastSectionId = courseService.lastCourseSectionId(query);
            result = Result.ok(lastSectionId);
        } catch (Exception e) {
            e.printStackTrace();
            result =  Result.fail(e.toString());
        }
        return result;
    }

    /**
     * 学习信息统计
     * @return
     */
    @ApiOperation(value = "学习信息统计",notes = "学习信息统计")
    @RequestMapping(value="/statistic",method = RequestMethod.POST)
    Result statistic(){
        Result result;
        try {
            CourseQuery query = new CourseQuery();
            UserDto user = UserThreadLocal.get();
            query.setMechanismId(user.getMechanismId());
            query.setUserId(user.getId());
            result = statisticsService.learnInfo(query);
        } catch (Exception e) {
            e.printStackTrace();
            result =  Result.fail(e.toString());
        }
        return result;
    }

}
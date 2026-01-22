package com.example.webapp.controller;

import com.google.common.collect.Lists;
import com.zhihuiedu.business.enums.LogRecordEnum;
import com.zhihuiedu.business.query.FakeDataQuery;
import com.zhihuiedu.business.service.statistics.StatisticsService;
import com.zhihuiedu.business.vo.FakeDataTJVO;
import com.zhihuiedu.framework.annotation.LoginRequired;
import com.zhihuiedu.framework.enums.PlatformMarkEnum;
import com.zhihuiedu.framework.result.Result;
import com.zhihuiedu.mechanism.controller.BaseMechController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = {"1008-造假数据统计"})
@RestController
@RefreshScope
@LoginRequired(platform= PlatformMarkEnum.ENTERPRISE)
@Slf4j
@RequestMapping("/api/bms/fake")
@Controller
public class FakeDataBmsController extends BaseMechController{
    @Autowired
    StatisticsService statisticsService;

    @ApiOperation(value = "总浏览量", notes = "书房+课程详情+活动列表+活动详情+我的 ")
    @RequestMapping(value = "/total-views", method = RequestMethod.POST)
    public Result<FakeDataTJVO>  totalViews(@RequestBody FakeDataQuery query) {
        List<Integer> typeList = Lists.newArrayList();
        typeList.add(LogRecordEnum.SHU_FANG.getType());
        typeList.add(LogRecordEnum.COURSE_XQ.getType());
        typeList.add(LogRecordEnum.HUO_DONG.getType());
        typeList.add(LogRecordEnum.MY.getType());
        query.setTypeList(typeList);
        Result<FakeDataTJVO>  result = statisticsService.getFakeDataList(query);
        return result;
    }

    @ApiOperation(value = "首页浏览量", notes = "书房")
    @RequestMapping(value = "/homepage-views", method = RequestMethod.POST)
    public Result<FakeDataTJVO>  homepageViews(@RequestBody FakeDataQuery query) {
        List<Integer> typeList = Lists.newArrayList();
        typeList.add(LogRecordEnum.SHU_FANG.getType());
        query.setTypeList(typeList);
        Result<FakeDataTJVO>  result = statisticsService.getFakeDataList(query);
        return result;
    }

    @ApiOperation(value = "课程浏览量", notes = "课程详情")
    @RequestMapping(value = "/course-views", method = RequestMethod.POST)
    public Result<FakeDataTJVO>  courseViews(@RequestBody FakeDataQuery query) {
        List<Integer> typeList = Lists.newArrayList();
        typeList.add(LogRecordEnum.COURSE_XQ.getType());
        query.setTypeList(typeList);
        Result<FakeDataTJVO>  result = statisticsService.getFakeDataList(query);
        return result;
    }

    @ApiOperation(value = "活动浏览量", notes = "活动列表+活动详情")
    @RequestMapping(value = "/activity-views", method = RequestMethod.POST)
    public Result<FakeDataTJVO>  activityViews(@RequestBody FakeDataQuery query) {
        List<Integer> typeList = Lists.newArrayList();
        typeList.add(LogRecordEnum.HUO_DONG.getType());
        query.setTypeList(typeList);
        Result<FakeDataTJVO>  result = statisticsService.getFakeDataList(query);
        return result;
    }

    @ApiOperation(value = "总学习时长", notes = "总学习时长")
    @RequestMapping(value = "/total-study-time", method = RequestMethod.POST)
    public Result<FakeDataTJVO>  totalStudyTime(@RequestBody FakeDataQuery query) {
        List<Integer> typeList = Lists.newArrayList();
        typeList.add(LogRecordEnum.COURSE_TM.getType());
        query.setTypeList(typeList);
        Result<FakeDataTJVO>  result = statisticsService.getFakeDataList(query);
        return result;
    }

    @ApiOperation(value = "机构注册人数",notes = "机构注册人数")
    @PostMapping(value = "reg-count")
    public Result  getRegCountByMid(@RequestBody FakeDataQuery query){
        try {
            return statisticsService.getRegCountByMid(query);
        } catch (Exception e) {
            log.error("{}", ExceptionUtils.getStackTrace(e));
        }
        return Result.fail();
    }


}

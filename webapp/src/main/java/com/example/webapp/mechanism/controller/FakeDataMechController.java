package com.example.webapp.mechanism.controller;

import com.example.webapp.DTO.UserDto;
import com.example.webapp.Service.statistics.StatisticsService;
import com.example.webapp.VO.FakeDataTJVO;
import com.example.webapp.annotation.LoginRequired;
import com.example.webapp.enums.LogRecordEnum;
import com.example.webapp.enums.PlatformMarkEnum;
import com.example.webapp.query.FakeDataQuery;
import com.example.webapp.result.Result;
import com.example.webapp.utils.UserThreadLocal;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = {"1008-造假数据统计"})
@RestController
@RefreshScope
@LoginRequired(platform= PlatformMarkEnum.MECHANISM)
@Slf4j
@RequestMapping("/api/mech/fake")
@Controller
public class FakeDataMechController extends BaseMechController{
    @Autowired
    StatisticsService statisticsService;

    @ApiOperation(value = "总浏览量", notes = "书房+课程详情+活动列表+活动详情+我的 ")
    @RequestMapping(value = "/total-views", method = RequestMethod.POST)
    public Result<FakeDataTJVO> totalViews(@RequestBody FakeDataQuery query) {
        UserDto userDto = UserThreadLocal.get();
        query.setMechanismId(userDto.getId());
        List<Integer> typeList = Lists.newArrayList();
        typeList.add(LogRecordEnum.SHU_FANG.getType());
        typeList.add(LogRecordEnum.COURSE_XQ.getType());
        typeList.add(LogRecordEnum.HUO_DONG.getType());
        typeList.add(LogRecordEnum.MY.getType());
        query.setTypeList(typeList);
        Result<FakeDataTJVO>  result = statisticsService.getFakeDataList(query);
        return result;
    }

    @ApiOperation(value = "总浏览人次", notes = "总浏览量的 UV")
    @RequestMapping(value = "/total-visits", method = RequestMethod.POST)
    public Result<FakeDataTJVO>  totalVisits(@RequestBody FakeDataQuery query) {
        UserDto userDto = UserThreadLocal.get();
        query.setMechanismId(userDto.getId());
        List<Integer> typeList = Lists.newArrayList();
        typeList.add(LogRecordEnum.SHU_FANG.getType());
        typeList.add(LogRecordEnum.COURSE_XQ.getType());
        typeList.add(LogRecordEnum.HUO_DONG.getType());
        typeList.add(LogRecordEnum.MY.getType());
        query.setTypeList(typeList);
        Result<FakeDataTJVO>  result = statisticsService.getFakeDataUVList(query);
        return result;
    }

    @ApiOperation(value = "总观看量", notes = "课程详情")
    @RequestMapping(value = "/total-watch", method = RequestMethod.POST)
    public Result<FakeDataTJVO>  totalWatch(@RequestBody FakeDataQuery query) {
        UserDto userDto = UserThreadLocal.get();
        query.setMechanismId(userDto.getId());
        List<Integer> typeList = Lists.newArrayList();
        typeList.add(LogRecordEnum.COURSE_XQ.getType());
        query.setTypeList(typeList);
        Result<FakeDataTJVO>  result = statisticsService.getFakeDataList(query);
        return result;
    }

    @ApiOperation(value = "首页浏览量", notes = "书房")
    @RequestMapping(value = "/home-page-views", method = RequestMethod.POST)
    public Result<FakeDataTJVO>  homePageViews(@RequestBody FakeDataQuery query) {
        UserDto userDto = UserThreadLocal.get();
        query.setMechanismId(userDto.getId());
        List<Integer> typeList = Lists.newArrayList();
        typeList.add(LogRecordEnum.SHU_FANG.getType());
        query.setTypeList(typeList);
        Result<FakeDataTJVO>  result = statisticsService.getFakeDataList(query);
        return result;
    }

    @ApiOperation(value = "总学习时长", notes = "总学习时长")
    @RequestMapping(value = "/total-study-time", method = RequestMethod.POST)
    public Result<FakeDataTJVO>  totalStudyTime(@RequestBody FakeDataQuery query) {
        UserDto userDto = UserThreadLocal.get();
        query.setMechanismId(userDto.getId());
        List<Integer> typeList = Lists.newArrayList();
        typeList.add(LogRecordEnum.COURSE_TM.getType());
        query.setTypeList(typeList);
        Result<FakeDataTJVO>  result = statisticsService.getFakeDataList(query);
        return result;
    }

    @ApiOperation(value = "分类学习时长", notes = "分类学习时长")
    @RequestMapping(value = "/category-study-time", method = RequestMethod.POST)
    public Result<FakeDataTJVO>  categoryStudyTime(@RequestBody FakeDataQuery query) {
        UserDto userDto = UserThreadLocal.get();
        query.setMechanismId(userDto.getId());
        List<Integer> typeList = Lists.newArrayList();
        typeList.add(LogRecordEnum.COURSE_FL.getType());
        query.setTypeList(typeList);
        Result<FakeDataTJVO>  result = statisticsService.getCategoryFakeDataList(query);
        return result;
    }

    @ApiOperation(value = "年龄学习时长", notes = "年龄学习时长")
    @RequestMapping(value = "/age-study-time", method = RequestMethod.POST)
    public Result<FakeDataTJVO>  ageStudyTime(@RequestBody FakeDataQuery query) {
        UserDto userDto = UserThreadLocal.get();
        query.setMechanismId(userDto.getId());
        List<Integer> typeList = Lists.newArrayList();
        typeList.add(LogRecordEnum.COURSE_NL.getType());
        query.setTypeList(typeList);
        Result<FakeDataTJVO>  result = statisticsService.getCategoryFakeDataList(query);
        return result;
    }


}
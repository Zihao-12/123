package com.example.webapp.portal.controller;

import com.example.webapp.DO.UserActivityRefDO;
import com.example.webapp.DTO.ActivityDTO;
import com.example.webapp.DTO.ActivityPrizeDetailDTO;
import com.example.webapp.DTO.UserAnswerDTO;
import com.example.webapp.DTO.UserDto;
import com.example.webapp.Service.Message.MessageService;
import com.example.webapp.Service.activity.ActivityService;
import com.example.webapp.VO.ActivityOperationVO;
import com.example.webapp.VO.ActivitySubmitResultVO;
import com.example.webapp.VO.ActivityWinLotteryUserVO;
import com.example.webapp.VO.HeroListVO;
import com.example.webapp.annotation.LogRecord;
import com.example.webapp.annotation.LoginRequired;
import com.example.webapp.common.Constant;
import com.example.webapp.enums.LogRecordEnum;
import com.example.webapp.enums.UpDownStatusEnum;
import com.example.webapp.query.ActivityQuery;
import com.example.webapp.query.ActivityRankingQuery;
import com.example.webapp.result.Result;
import com.example.webapp.result.ResultPage;
import com.example.webapp.utils.UserThreadLocal;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = {"1008-活动管理"})
@RefreshScope
@RestController
@Component
@Slf4j
@LoginRequired
@RequestMapping("/api/portal/activity")
public class ActivityPortalController {
    @Autowired
    ActivityService activityService;
    @Autowired
    MessageService messageService;

    @LogRecord(LOG_RECORD_ENUM = LogRecordEnum.HUO_DONG)
    @ApiOperation(value = "活动列表（H5/小程序）", notes = "活动列表（H5/小程序）")
    @PostMapping(value = "/list")
    public ResultPage<ActivityDTO> list(@RequestBody ActivityQuery query) {
        UserDto userDto = UserThreadLocal.get();
        query.setMechanismId(userDto.getMechanismId());
        query.setUserId(userDto.getId());
        query.setStatus(UpDownStatusEnum.UP.getStatus());
        ResultPage result = activityService.portalList(query);
        return result;
    }

    @ApiOperation(value = "我的活动列表", notes = "H我的活动列表")
    @PostMapping(value = "/joined-list")
    public ResultPage<ActivityDTO> joinedList() {
        ActivityQuery query = new ActivityQuery();
        UserDto userDto = UserThreadLocal.get();
        query.setMechanismId(userDto.getMechanismId());
        query.setUserId(userDto.getId());
        query.setUserJoined(Constant.USER_JOINED);
        query.setStatus(UpDownStatusEnum.UP.getStatus());
        ResultPage result = activityService.portalList(query);
        return result;
    }

    @LogRecord(LOG_RECORD_ENUM = LogRecordEnum.HUO_DONG)
    @ApiOperation(value = "用户活动详情（用户抽奖/排行榜设置信息）", notes = "用户活动详情（用户抽奖/排行榜设置信息）")
    @ApiImplicitParams({@ApiImplicitParam(name = "activityId", value = "活动ID", required = true)})
    @PostMapping(value = "/view/{activityId}")
    public Result<ActivityDTO> view(@PathVariable int activityId) {
        UserDto userDto = UserThreadLocal.get();
        ActivityDTO activityDTO =activityService.view(activityId);
        ActivityOperationVO operationVO =activityService.getUserActivityOperation(activityId,userDto.getId());
        activityDTO.setOperationVO(operationVO);
        return Result.ok(activityDTO);
    }

    @ApiOperation(value = "参加活动", notes = "参加活动")
    @ApiImplicitParams({@ApiImplicitParam(name = "activityId", value = "活动ID", required = true)})
    @PostMapping(value = "/join/{activityId}")
    public Result join(@PathVariable Integer activityId) {
        UserDto userDto = UserThreadLocal.get();
        //设置抽奖库存缓存
        activityService.setStockRedis(activityId);
        return activityService.join(activityId,userDto.getId());
    }


    @ApiOperation(value = "参加活动-提交", notes = "参加活动-提交")
    @PostMapping(value = "/submit")
    public Result<ActivitySubmitResultVO> submit(@RequestBody UserAnswerDTO userAnswer) {
        UserDto userDto = UserThreadLocal.get();
        userAnswer.setUserId(userDto.getId());
        UserActivityRefDO userActivityRefDO = activityService.judgeScore(userAnswer);
        userActivityRefDO.setMechanismId(userDto.getMechanismId());
        return activityService.submit(userActivityRefDO);
    }

    @LogRecord(LOG_RECORD_ENUM = LogRecordEnum.RANKING)
    @ApiOperation(value = "答题排行榜", notes = "答题排行榜")
    @PostMapping(value = "/ranking-list")
    public Result<HeroListVO> rankingList(@RequestBody ActivityRankingQuery query) {
        UserDto userDto = UserThreadLocal.get();
        query.setMechanismId(userDto.getMechanismId());
        query.setUserId(userDto.getId());
        return activityService.rankingList(query);
    }

    @ApiOperation(value = "活动奖品列表", notes = "活动奖品列表")
    @ApiImplicitParams({@ApiImplicitParam(name = "activityId", value = "活动ID", required = true)})
    @PostMapping(value = "/prize-list/{activityId}")
    public Result getActivityPrizeList(@PathVariable Integer activityId) {
        List<ActivityPrizeDetailDTO> list =activityService.getActivityPrizeList(activityId);
        return Result.ok(list);
    }

    @ApiOperation(value = "抽奖", notes = "抽奖")
    @ApiImplicitParams({@ApiImplicitParam(name = "activityId", value = "活动ID", required = true)})
    @PostMapping(value = "/lottery/{activityId}")
    public Result lottery(@PathVariable Integer activityId) {
        UserDto userDto = UserThreadLocal.get();
        return activityService.lottery(userDto.getId(),activityId);
    }

    @ApiOperation(value = "我的活动奖品列表", notes = "我的活动奖品列表")
    @ApiImplicitParams({@ApiImplicitParam(name = "activityId", value = "活动ID", required = true)})
    @PostMapping(value = "/my-prize-list/{activityId}")
    public Result<ActivityPrizeDetailDTO> getMyActivityPrizeList(@PathVariable Integer activityId) {
        UserDto userDto = UserThreadLocal.get();
        return activityService.getMyActivityPrizeList(userDto.getId(),activityId);
    }

    @ApiOperation(value = "活动中奖名单", notes = "活动中奖名单")
    @ApiImplicitParams({@ApiImplicitParam(name = "activityId", value = "活动ID", required = true)})
    @PostMapping(value = "/activity-win-user-list/{activityId}")
    public Result<ActivityWinLotteryUserVO> getWinLotteryuserlist(@PathVariable Integer activityId) {
        UserDto userDto = UserThreadLocal.get();
        return activityService.getWinLotteryuserlist(userDto.getId(),activityId);
    }

    @ApiOperation(value = "临时活动-报名", notes = "临时活动-报名")
    @ApiImplicitParams({@ApiImplicitParam(name = "name", value = "姓名", required = true),
            @ApiImplicitParam(name = "age", value = "年龄", required = true)})
    @PostMapping(value = "/enroll-in")
    public Result enrollIn(String name, Integer age) {
        UserDto userDto = UserThreadLocal.get();
        return activityService.enrollIn(userDto.getId(),name,age);
    }

    @ApiOperation(value = "临时活动-报名状态", notes = "临时活动-报名状态")
    @PostMapping(value = "/enroll-status")
    public Result enrollStatus() {
        UserDto userDto = UserThreadLocal.get();
        return activityService.enrollStatus(userDto.getId());
    }
}


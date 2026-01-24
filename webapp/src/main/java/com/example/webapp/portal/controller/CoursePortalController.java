package com.example.webapp.portal.controller;

import com.aliyuncs.vod.model.v20170321.GetPlayInfoResponse;
import com.example.webapp.DO.StUserLearnRecordDetailDO;
import com.example.webapp.DO.StUserLearnRecordDetailDOIP;
import com.example.webapp.DTO.CourseDTO;
import com.example.webapp.DTO.UserDto;
import com.example.webapp.Service.Course.CourseService;
import com.example.webapp.annotation.LogRecord;
import com.example.webapp.annotation.LoginRequired;
import com.example.webapp.enums.CourseListTypeEnum;
import com.example.webapp.enums.CourseTryStatusEnum;
import com.example.webapp.enums.LogRecordEnum;
import com.example.webapp.enums.UpDownStatusEnum;
import com.example.webapp.query.CourseQuery;
import com.example.webapp.result.CodeEnum;
import com.example.webapp.result.Result;
import com.example.webapp.result.ResultPage;
import com.example.webapp.third.AccessKeyIdSecretEnum;
import com.example.webapp.third.AliVideo;
import com.example.webapp.utils.UserThreadLocal;
import com.example.webapp.utils.http.IpUtil;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Api(tags = {"1004-课程"})
@RefreshScope
@RestController
@Component
@Slf4j
@LoginRequired
@RequestMapping("/api/portal/course")
public class CoursePortalController {
    @Autowired
    private CourseService courseService;

    @ApiOperation(value = "阿里-非加密视频播放", notes = "非加密视频播放")
    @ApiImplicitParams({@ApiImplicitParam(name = "videoId", value = "videoId", required = true)})
    @PostMapping(value = "/get-play-info/{videoId}")
    public Result<GetPlayInfoResponse> view(@PathVariable String videoId){
        try {
            AliVideo video = AliVideo.getInstanceInfo(AccessKeyIdSecretEnum.ALI_VIDEO.getAk(), AccessKeyIdSecretEnum.ALI_VIDEO.getAks(),AccessKeyIdSecretEnum.ALI_VIDEO.getEp());
            GetPlayInfoResponse playInfo =  video.getPlayInfo(videoId);
            return Result.ok(playInfo);
        } catch (Exception e){
            log.error("{}",ExceptionUtils.getStackTrace(e));
        }
        return Result.fail();
    }

    @ApiOperation(value = "阿里-加密视频播放",notes = "获取阿里云点播信息(加密视频播放需要数据获取)")
    @GetMapping(value = "/ali/video/{videoId}")
    public Result videoInfo(@PathVariable String videoId) {
        AliVideo video = AliVideo.getInstance(AccessKeyIdSecretEnum.ALI_VIDEO.getAk(), AccessKeyIdSecretEnum.ALI_VIDEO.getAks(),AccessKeyIdSecretEnum.ALI_VIDEO.getRegionId());
        Result result = video.getVideoInfo(videoId);
        if(CodeEnum.FAILED.getValue().equals(result.getCode())){
            return Result.fail("视频ID不存在");
        }
        return result;
    }

    @ApiOperation(value = "查看机构课程试看状态",notes = "查看结构课程试看状态，试看设置：0全部可看 1试看第一节 2试看前三节")
    @GetMapping(value = "/try-status")
    public Result getTryStatus() {
        UserDto userDto = UserThreadLocal.get();
        Integer tryStatus = courseService.getTryStatusOfMechanism(userDto.getMechanismId());
        String tryStatusCn = CourseTryStatusEnum.getTryStatusName(tryStatus);
        Map map = Maps.newConcurrentMap();
        map.put("tryStatus",tryStatus);
        map.put("tryStatusCn",tryStatusCn);
        return Result.ok(map);
    }


    @LogRecord(LOG_RECORD_ENUM = LogRecordEnum.SHU_FANG)
    @ApiOperation(value = "用户机构已购课程列表 1.视频课 2.签到视频", notes = "用户机构已购课程列表 1.视频课 2.签到视频")
    @PostMapping(value = "/buy-course-list")
    public ResultPage courseBuyList(@RequestBody CourseQuery query){
        UserDto userDto = UserThreadLocal.get();
        query.setMechanismId(userDto.getMechanismId());
        query.setUserId(userDto.getId());
        query.setCourseListType(CourseListTypeEnum.JIGOU_BUY.getType());
        //只显示课程包中已上架的课程
        query.setStatus(UpDownStatusEnum.UP.getStatus());
        PageInfo page = courseService.findPortalCourseList(query);
        if(page == null){
            return ResultPage.fail("无数据");
        }
        return ResultPage.ok(page.getList(),page.getPageNum(),page.getPageSize(),page.getTotal());
    }

    @ApiOperation(value = "用户机构自建课程列表", notes = "用户机构自建课程列表")
    @PostMapping(value = "/zj-course-list")
    public ResultPage zjCourseList(@RequestBody CourseQuery query){
        UserDto userDto = UserThreadLocal.get();
        query.setMechanismId(userDto.getMechanismId());
        query.setUserId(userDto.getId());
        query.setCourseListType(CourseListTypeEnum.ZJ_JIAN.getType());
        query.setStatus(UpDownStatusEnum.UP.getStatus());
        PageInfo page = courseService.findPortalCourseList(query);
        if(page == null){
            return ResultPage.fail("无数据");
        }
        return ResultPage.ok(page.getList(),page.getPageNum(),page.getPageSize(),page.getTotal());
    }


    @LogRecord(LOG_RECORD_ENUM = LogRecordEnum.COURSE_XQ)
    @ApiOperation(value = "课程详情", notes = "课程详情")
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "课程id", required = true)})
    @PostMapping(value = "/view/{id}")
    public Result view(@PathVariable Integer id){
        try {
            UserDto userDto = UserThreadLocal.get();
            boolean buy =courseService.isBuyCourse(userDto.getMechanismId(),id);
            if(!buy){
                return Result.fail("未购买");
            }
            CourseQuery query = new CourseQuery();
            query.setUserId(userDto.getId());
            query.setMechanismId(userDto.getMechanismId());
            query.setCourseId(id);
            CourseDTO course =  courseService.viewPortal(query);
            return Result.ok(course);
        } catch (Exception e){
            log.error("{}",ExceptionUtils.getStackTrace(e));
        }
        return Result.fail();
    }


    @ApiOperation(value = "保存听课时长", notes = "视频播放80%时，前端请求带上课节完成标志 complete=1")
    @RequestMapping(value = "/record", method = RequestMethod.POST)
    public Result dataAnalysis(@RequestBody StUserLearnRecordDetailDO detail) {
        Result result;
        try {
            UserDto userDto = UserThreadLocal.get();
            detail.setMechanismId(userDto.getMechanismId());
            detail.setUserId(userDto.getId());
            StUserLearnRecordDetailDOIP detailip = new StUserLearnRecordDetailDOIP();
            detailip.setMechanismId(userDto.getMechanismId());
            detailip.setIp(IpUtil.getIpAddr());
            detailip.setCourseId(detail.getCourseId());
            detailip.setCourseSectionId(detail.getCourseSectionId());
            detailip.setDuration(detail.getDuration());
            detailip.setStudyDate(detail.getStudyDate());
            detailip.setComplete(detail.getComplete());
            result = courseService.insertOrUpdateLearnRecordDetail(detail);
            result = courseService.insertOrUpdateLearnRecordDetailIP(detailip);
        } catch (Exception e) {
            result = Result.fail(e.toString());
            e.printStackTrace();
        }
        return result;
    }


}

package com.example.webapp.mechanism.controller;

import com.example.webapp.DO.CourseDO;
import com.example.webapp.DTO.CourseDTO;
import com.example.webapp.DTO.CourseSectionDTO;
import com.example.webapp.DTO.UserDto;
import com.example.webapp.Service.Course.CourseService;
import com.example.webapp.annotation.LoginRequired;
import com.example.webapp.common.Constant;
import com.example.webapp.enums.CourseListTypeEnum;
import com.example.webapp.enums.PlatformMarkEnum;
import com.example.webapp.enums.UpDownStatusEnum;
import com.example.webapp.query.CourseQuery;
import com.example.webapp.result.Result;
import com.example.webapp.result.ResultPage;
import com.example.webapp.third.AliOSS;
import com.example.webapp.utils.UserThreadLocal;
import com.github.pagehelper.PageInfo;
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

@Api(tags = {"1003-课程"})
@RefreshScope
@RestController
@Component
@Slf4j
@LoginRequired(platform= PlatformMarkEnum.MECHANISM)
@RequestMapping("/api/mech/course")
public class CourseMechController extends BaseMechController {
    @Autowired
    private CourseService courseService;

    @ApiOperation(value = "获取机构已购课程列表", notes = "获取机构已购课程列表")
    @PostMapping(value = "/buy-course-list")
    public ResultPage courseBuyList(@RequestBody CourseQuery query){
        query.setMechanismId(UserThreadLocal.get().getId());
        query.setCourseListType(CourseListTypeEnum.JIGOU_BUY.getType());
        //只显示课程包中已上架的课程
        query.setStatus(UpDownStatusEnum.UP.getStatus());
        PageInfo page = courseService.findMechanismCourseList(query);
        if(page == null){
            return ResultPage.fail("无数据");
        }
        return ResultPage.ok(page.getList(),page.getPageNum(),page.getPageSize(),page.getTotal());
    }

    @ApiOperation(value = "获取机构自建课程列表", notes = "获取机构自建课程列表")
    @PostMapping(value = "/zj-course-list")
    public ResultPage zjCourseList(@RequestBody CourseQuery query){
        query.setMechanismId(UserThreadLocal.get().getId());
        query.setCourseListType(CourseListTypeEnum.ZJ_JIAN.getType());
        PageInfo page = courseService.findMechanismCourseList(query);
        if(page == null){
            return ResultPage.fail("无数据");
        }
        return ResultPage.ok(page.getList(),page.getPageNum(),page.getPageSize(),page.getTotal());
    }

    @ApiOperation(value = "设置课程模式", notes = "设置课程模式")
    @ApiImplicitParams({@ApiImplicitParam(name = "courseId", value = "课程id", required = true),
            @ApiImplicitParam(name = "mode", value = "0自由模式 1闯关模式", required = true)})
    @PostMapping(value = "/set-course-mode/{courseId}/{mode}")
    public Result setCourseMode(@PathVariable Integer courseId, @PathVariable Integer mode){
        try {
            Integer mechanismId = UserThreadLocal.get().getId();
            return courseService.setCourseMode(mechanismId,courseId,mode);
        }catch (Exception e){
            log.error("{}", ExceptionUtils.getStackTrace(e));
        }
        return Result.fail();
    }

    @ApiOperation(value = "自建建课程", notes = "自建建课程")
    @PostMapping(value = "add")
    public Result add(@RequestBody CourseDO meCourseDO){
        try {
            Integer mechanismId = UserThreadLocal.get().getId();
            meCourseDO.setMechanismId(mechanismId);
            return courseService.insert(meCourseDO);
        }catch (Exception e){
            String msg="com.aliyun.tea.TeaException: code: 404, The video does not exist";
            if(e.toString().contains(msg)){
                return Result.fail("视频ID不存在");
            }
        }
        return Result.fail();
    }

    @ApiOperation(value = "课程详情", notes = "课程详情")
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "课程id", required = true)})
    @PostMapping(value = "/view/{id}")
    public Result<CourseDTO> view(@PathVariable Integer id){
        try {
            UserDto userDto = UserThreadLocal.get();
            boolean buy =courseService.isBuyCourse(userDto.getId(),id);
            if(!buy){
                return Result.fail("未购买");
            }
            CourseDTO course =  courseService.view(id,userDto.getId());
            return Result.ok(course);
        } catch (Exception e){
            log.error("{}",ExceptionUtils.getStackTrace(e));
        }
        return Result.fail();
    }

    @ApiOperation(value = "更新自建课", notes = "更新自建课")
    @PostMapping(value = "update")
    public Result update(@RequestBody CourseDO meCourseDO){
        try {
            UserDto userDto = UserThreadLocal.get();
            CourseDTO course = courseService.view(meCourseDO.getId());
            if(course == null){
                return Result.fail(Constant.NO_DATA);
            }
            if (!isBelongMechanism(userDto.getId(), course.getMechanismId())) {
                return Result.fail("无权限");
            }
            return courseService.update(meCourseDO);
        } catch (Exception e) {
            String msg="com.aliyun.tea.TeaException: code: 404, The video does not exist";
            if(e.toString().contains(msg)){
                return Result.fail("视频ID不存在");
            }
        }
        return Result.fail();
    }


    @ApiOperation(value = "删除自建课", notes = "删除自建课/封面及所有章节附件")
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "课程ID", required = true)})
    @PostMapping(value = "delete/{id}")
    public Result delete(@PathVariable Integer id){
        try {
            UserDto userDto = UserThreadLocal.get();
            CourseDTO course = courseService.view(id);
            if(course == null){
                return Result.fail(Constant.NO_DATA);
            }
            if (!isBelongMechanism(userDto.getId(), course.getMechanismId())) {
                return Result.fail("无权限");
            }
            return courseService.delete(id);
        } catch (Exception e){
            log.error("{}",ExceptionUtils.getStackTrace(e));
        }
        return Result.fail();
    }

    @ApiOperation(value = "删除自建课节附件", notes = "删除自建课节附件")
    @ApiImplicitParams({@ApiImplicitParam(name = "sectionId", value = "章节ID", required = true)})
    @PostMapping("/del-section-file/{sectionId}")
    public Result deleteSectionFile(@PathVariable Integer sectionId) {
        try {
            Result sectionResult = courseService.viewSection(sectionId);
            CourseSectionDTO dto = (CourseSectionDTO)sectionResult.getData();
            if(dto == null){
                return Result.fail(Constant.NO_DATA);
            }
            UserDto userDto = UserThreadLocal.get();
            CourseDTO course = courseService.view(dto.getCourseId());
            if (!isBelongMechanism(userDto.getId(), course.getMechanismId())) {
                return Result.fail("无权限");
            }
            //资料删除
            AliOSS.deleteObject(dto.getCourseware());
            return Result.ok("");
        } catch (Exception e) {
            log.error("{}",ExceptionUtils.getStackTrace(e));
        }
        return Result.fail();
    }



}

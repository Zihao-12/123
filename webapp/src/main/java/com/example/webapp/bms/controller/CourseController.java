package com.example.webapp.bms.controller;

import com.example.webapp.DO.AssociateCoursePackageDO;
import com.example.webapp.DO.CourseDO;
import com.example.webapp.DTO.CourseDTO;
import com.example.webapp.DTO.CourseSectionDTO;
import com.example.webapp.annotation.RepeatableCommit;
import com.example.webapp.query.CourseQuery;
import com.example.webapp.Service.Course.CourseService;
import com.example.webapp.common.Constant;
import com.example.webapp.enums.CourseListTypeEnum;
import com.example.webapp.enums.CourseTypeEnum;
import com.example.webapp.result.Result;
import com.example.webapp.result.ResultPage;
import com.example.webapp.third.AliOSS;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@RequestMapping("/api/bms/course")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @ApiOperation(value = "课程列表 1.视频课 2.签到视频", notes = "课程列表1.视频课 2.签到视频")
    @PostMapping(value = "list")
    public ResultPage list(@RequestBody CourseQuery query){
        try {
            query.setCourseListType(CourseListTypeEnum.YUN_YING.getType());
            return courseService.list(query);
        } catch (Exception e){
            log.error("{}", ExceptionUtils.getStackTrace(e));
        }
        return ResultPage.fail();
    }

    @ApiOperation(value = "课程列表-弹窗选择", notes = "不关联查询课程包信息")
    @PostMapping(value = "select-list")
    public ResultPage selectList(@RequestBody CourseQuery query){
        try {
            query.setCourseListType(CourseListTypeEnum.YUN_YING.getType());
            query.setType(CourseTypeEnum.VIDEO.getType());
            return courseService.selectList(query);
        } catch (Exception e){
            log.error("{}",ExceptionUtils.getStackTrace(e));
        }
        return ResultPage.fail();
    }

    @RepeatableCommit(timeout =10)
    @ApiOperation(value = "新建课程", notes = "新建课程")
    @PostMapping(value = "add")
    public Result add(@RequestBody CourseDO courseDO){
        try {
            courseDO.setMechanismId(Constant.YUNYING_MECHANISM_ID);
            return courseService.insert(courseDO);
        }catch (Exception e){
            String msg="com.aliyun.tea.TeaException: code: 404, The video does not exist";
            if(e.toString().contains(msg)){
                return Result.fail("视频ID不存在");
            }
        }
        return Result.fail();
    }

    @RepeatableCommit(timeout =10)
    @ApiOperation(value = "新建-签到视频", notes = "签到视频 中的视频ID 对应课程的一个 节（前端根据 视频ID 生成 节）")
    @PostMapping(value = "add-checkin")
    public Result addCheckin(@RequestBody CourseDO courseDO){
        try {
            courseDO.setType(CourseTypeEnum.CHECKIN_VIDEO.getType());
            courseDO.setMechanismId(Constant.YUNYING_MECHANISM_ID);
            return courseService.insert(courseDO);
        }catch (Exception e){
            String msg="com.aliyun.tea.TeaException: code: 404, The video does not exist";
            if(e.toString().contains(msg)){
                return Result.fail("视频ID不存在");
            }
        }
        return Result.fail();
    }

    @ApiOperation(value = "课程详情(包括签到视频)", notes = "课程详情")
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "课程id", required = true)})
    @PostMapping(value = "/view/{id}")
    public Result<CourseDTO> view(@PathVariable Integer id){
        try {
            CourseDTO course =  courseService.view(id);
            return Result.ok(course);
        } catch (Exception e){
            log.error("{}",ExceptionUtils.getStackTrace(e));
        }
        return Result.fail();
    }

    @RepeatableCommit(timeout =10)
    @ApiOperation(value = "更新视频课程(包括签到视频)", notes = "签到视频 中的视频ID 对应课程的一个 节（前端根据 视频ID 生成 节）")
    @PostMapping(value = "update")
    public Result update(@RequestBody CourseDO meCourseDO){
        try {
            return courseService.update(meCourseDO);
        } catch (Exception e) {
            String msg="com.aliyun.tea.TeaException: code: 404, The video does not exist";
            if(e.toString().contains(msg)){
                return Result.fail("视频ID不存在");
            }
        }
        return Result.fail();
    }


    @ApiOperation(value = "删除课程", notes = "删除课程/封面及所有章节附件")
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "课程ID", required = true)})
    @PostMapping(value = "delete/{id}")
    public Result delete(@PathVariable Integer id){
        try {
            return courseService.delete(id);
        } catch (Exception e){
            log.error("{}",ExceptionUtils.getStackTrace(e));
        }
        return Result.fail();
    }

    @ApiOperation(value = "删除课节附件", notes = "删除课节附件")
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "课程ID", required = true)})
    @PostMapping("/del-section-file/{sectionId}")
    public Result deleteSectionFile(@PathVariable Integer sectionId) {
        try {
            Result sectionResult = courseService.viewSection(sectionId);
            CourseSectionDTO dto = (CourseSectionDTO)sectionResult.getData();
            if(dto != null){
                //资料删除
                AliOSS.deleteObject(dto.getCourseware());
            }
        } catch (Exception e) {
            log.error("{}",ExceptionUtils.getStackTrace(e));
        }
        return Result.fail();
    }


    @ApiOperation(value = "上下架课程", notes = "上下架课程")
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "课程id", required = true),
            @ApiImplicitParam(name = "status", value = "上下架状态:1上架 0下架", required = true)})
    @PostMapping(value = "/update-status/{id}/{status}")
    public Result updateStatus(@PathVariable Integer id,@PathVariable Integer status){
        try {
            return courseService.updateStatus(id,status);
        }catch (Exception e){
            log.error("{}",ExceptionUtils.getStackTrace(e));
        }
        return Result.fail();
    }

    @ApiOperation(value = "课程列表-关联课程包", notes = "课程列表-关联课程包")
    @PostMapping(value = "/associate-course-package")
    public Result associateCoursePackage(@RequestBody AssociateCoursePackageDO associateCoursePackageDO){
        try {
            return courseService.associateCoursePackage(associateCoursePackageDO);
        }catch (Exception e){
            log.error("{}",ExceptionUtils.getStackTrace(e));
        }
        return Result.fail();
    }


    @ApiOperation(value = "课程列表-查询关联课程包", notes = "课程列表-查询关联课程包")
    @PostMapping(value = "/associated-course-package/{courseId}")
    public Result associatedCoursePackage(@PathVariable Integer courseId) {
        try {
            return courseService.associatedCoursePackage(courseId);
        }catch (Exception e){
            log.error("{}",ExceptionUtils.getStackTrace(e));
        }
        return Result.fail();
    }
}

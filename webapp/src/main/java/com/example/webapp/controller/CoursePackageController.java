package com.example.webapp.controller;


import com.example.webapp.DO.CoursePackageDO;
import com.example.webapp.Query.CoursePackageQuery;
import com.example.webapp.Service.coursepackage.CoursePackageService;
import com.example.webapp.enums.PlatformMarkEnum;
import com.example.webapp.result.Result;
import com.example.webapp.result.ResultPage;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

@RefreshScope
@RestController
@Component
@Slf4j
@LoginRequired(platform= PlatformMarkEnum.ENTERPRISE)
@RequestMapping("/api/bms/course-package")
public class CoursePackageController {
    @Autowired
    private CoursePackageService coursePackageService;

    @ApiOperation(value = "弹窗选择课程包", notes = "弹窗选择课程包")
    @PostMapping(value = "select-list")
    public ResultPage selectList(@RequestBody CoursePackageQuery query){
        try {
            return (ResultPage) coursePackageService.selectList(query);
        } catch (Exception e) {
           log.error("{}", ExceptionUtils.getStackTrace(e));
        }
        return ResultPage.fail();
    }

    @PostMapping(value = "list")
    public ResultPage list(@RequestBody CoursePackageQuery query){
        try {
            return coursePackageService.list(query);
        } catch (Exception e) {
            log.error("{}",ExceptionUtils.getStackTrace(e));
        }
        return ResultPage.fail();
    }

    @PostMapping(value = "add")
    public Result add(@RequestBody CoursePackageDO meCoursePackageDO){
        Result result;
        try {
            result = coursePackageService.insert(meCoursePackageDO);
        } catch (Exception e) {
            log.error("{}",ExceptionUtils.getStackTrace(e));
            result = Result.fail(e.toString());
        }
        return result;
    }

     @PostMapping(value = "/view/{id}")
    public Result view(@PathVariable Integer id){
        try {
             return coursePackageService.view(id);
        } catch (Exception e) {
           log.error("{}",ExceptionUtils.getStackTrace(e));
        }
        return Result.fail();
    }

    @PostMapping(value = "update")
    public Result update(@RequestBody CoursePackageDO meCoursePackageDO){
        try {
            return coursePackageService.update(meCoursePackageDO);
        } catch (Exception e) {
            log.error("{}",ExceptionUtils.getStackTrace(e));
        }
        return Result.fail();
    }

    @PostMapping(value = "delete/{id}")
    public Result delete(@PathVariable Integer id){
        try {
            return coursePackageService.delete(id);
        } catch (Exception e) {
            log.error("{}",ExceptionUtils.getStackTrace(e));
        }
        return Result.fail();
    }

    @PostMapping(value = "disassociate/{packageId}/{courseId}")
    public Result disassociate(@PathVariable Integer packageId,@PathVariable Integer courseId){
        try {
            return coursePackageService.disassociate(packageId,courseId);
        } catch (Exception e) {
            log.error("{}",ExceptionUtils.getStackTrace(e));
        }
        return Result.fail();
    }

    @PostMapping(value = "/update-status/{id}/{status}")
    public Result updateSale(@PathVariable Integer id, @PathVariable Integer status) {
        Result result = coursePackageService.updateSale(id, status);
        return result;
    }

    @PostMapping(value = "/set-trial-type/{id}/{status}")
    public Result setTrialType(@PathVariable Integer id, @PathVariable Integer status) {
        Result result = coursePackageService.updateTrySale(id, status);
        return result;
    }

    @PostMapping(value = "/copy-package/{id}")
    public Result copyPackage(@PathVariable Integer id) {
        try {
            return coursePackageService.copyPackage(id);
        } catch (Exception e) {
           log.error(ExceptionUtils.getStackTrace(e));
        }
        return Result.fail();
    }
}

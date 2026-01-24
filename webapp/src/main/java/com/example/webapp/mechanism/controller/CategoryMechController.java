package com.example.webapp.mechanism.controller;

import com.example.webapp.Service.category.CategoryService;
import com.example.webapp.annotation.LoginRequired;
import com.example.webapp.enums.PlatformMarkEnum;
import com.example.webapp.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = {"1003-分类接口"})
@RefreshScope
@RestController
@Component
@Slf4j
@LoginRequired(platform= PlatformMarkEnum.MECHANISM)
@RequestMapping("/api/mech/category")
public class CategoryMechController {
    /**
     * 课程内容分类
     */
    @Value("${portal.category.course.content.id}")
    private Integer courseContentId;
    /**
     * 课程年龄分类
     */
    @Value("${portal.category.course.age.id}")
    private Integer courseAgeId;
    /**
     * 活动类型分类
     */
    @Value("${portal.category.activity.type.id}")
    private Integer activityTypeId;
    /**
     * 活动形式分类
     */
    @Value("${portal.category.activity.form.id}")
    private Integer activityFormid;
    /**
     * 新闻资讯分类
     */
    @Value("${portal.category.news.type.id}")
    private Integer newsTypeId;
    /**
     * 新闻资讯来源分类
     */
    @Value("${portal.category.news.source.id}")
    private Integer newsSourceId;


    @Autowired
    CategoryService categoryService;

    @ApiOperation(value = "年龄分类列表",notes = "年龄分类列表")
    @PostMapping(value = "get-age-list")
    public Result getAgeList(){
        try {
            return categoryService.findChildNodeByParentId(courseAgeId);
        } catch (Exception e) {
            log.error("{}", ExceptionUtils.getStackTrace(e));
        }
        return Result.fail();
    }

    @ApiOperation(value = "内容分类列表",notes = "内容分类列表")
    @PostMapping(value = "get-content-list")
    public Result  getContentList(){
        try {
            return categoryService.findChildNodeByParentId(courseContentId);
        } catch (Exception e) {
            log.error("{}",ExceptionUtils.getStackTrace(e));
        }
        return Result.fail();
    }

    @ApiOperation(value = "查询子分类",notes = "查询子分类")
    @ApiImplicitParams({@ApiImplicitParam(name="parentId",value="parentId",required=true)})
    @PostMapping(value = "get-child-category/{parentId}")
    public Result  findChildNodeByParentId(@PathVariable Integer parentId){
        try {
            return categoryService.findChildNodeByParentId(parentId);
        } catch (Exception e) {
            log.error("{}",ExceptionUtils.getStackTrace(e));
        }
        return Result.fail();
    }

}


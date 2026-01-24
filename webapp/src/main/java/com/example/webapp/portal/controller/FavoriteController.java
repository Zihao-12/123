package com.example.webapp.portal.controller;

import com.example.webapp.DO.UserFavoriteRefDO;
import com.example.webapp.DTO.UserDto;
import com.example.webapp.Service.Course.CourseService;
import com.example.webapp.annotation.LoginRequired;
import com.example.webapp.enums.CourseListTypeEnum;
import com.example.webapp.enums.UpDownStatusEnum;
import com.example.webapp.query.CourseQuery;
import com.example.webapp.result.Result;
import com.example.webapp.result.ResultPage;
import com.example.webapp.utils.UserThreadLocal;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

@Api(tags = {"1005-用户收藏表"})
@RefreshScope
@RestController
@Component
@Slf4j
@LoginRequired
@RequestMapping("/api/portal/favorite")
public class FavoriteController {
    @Autowired
    CourseService courseService;


    @ApiOperation(value = "用户课程收藏列表", notes = "用户课程收藏列表")
    @PostMapping(value = "/favorite-course-list")
    public ResultPage favoriteCourseList(@RequestBody CourseQuery query){
        UserDto userDto = UserThreadLocal.get();
        query.setMechanismId(userDto.getMechanismId());
        query.setUserId(userDto.getId());
        query.setCourseListType(CourseListTypeEnum.FAVORITE.getType());
        query.setStatus(UpDownStatusEnum.UP.getStatus());
        PageInfo page = courseService.findPortalCourseList(query);
        if(page == null){
            return ResultPage.fail("无数据");
        }
        return ResultPage.ok(page.getList(),page.getPageNum(),page.getPageSize(),page.getTotal());
    }

    @ApiOperation(value = "收藏课程", notes = "收藏课程")
    @PostMapping(value = "/favorite/{courseId}")
    public Result favorite(@PathVariable Integer courseId ) {
        UserFavoriteRefDO favoriteDO = new UserFavoriteRefDO();
        UserDto userDto = UserThreadLocal.get();
        favoriteDO.setMechanismId(userDto.getMechanismId());
        favoriteDO.setUserId(userDto.getId());
        favoriteDO.setObjectId(courseId);
        Result result =  courseService.favorite(favoriteDO);
        return result;
    }

    @ApiOperation(value = "取消收藏课程", notes = "取消收藏课程")
    @PostMapping(value = "/cancel-favorite/{courseId}")
    public Result cancelFavorite(@PathVariable Integer courseId ) {
        UserFavoriteRefDO favoriteDO = new UserFavoriteRefDO();
        UserDto userDto = UserThreadLocal.get();
        favoriteDO.setMechanismId(userDto.getMechanismId());
        favoriteDO.setUserId(userDto.getId());
        favoriteDO.setObjectId(courseId);
        Result result =  courseService.cancelFavorite(favoriteDO);
        return result;
    }


}
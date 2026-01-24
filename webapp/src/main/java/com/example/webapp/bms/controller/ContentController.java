package com.example.webapp.bms.controller;

import com.example.webapp.DO.ContentDO;
import com.example.webapp.annotation.LoginRequired;
import com.example.webapp.query.ContentQuery;
import com.example.webapp.Service.content.ContentService;
import com.example.webapp.enums.PlatformMarkEnum;
import com.example.webapp.result.Result;
import com.example.webapp.result.ResultPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

@Api(tags = {"1008-新闻资讯内容管理"})
@RefreshScope
@RestController
@Component
@Slf4j
@LoginRequired(platform= PlatformMarkEnum.ENTERPRISE)
@RequestMapping("/api/bms/content")
public class ContentController {
    @Autowired
    ContentService contentService;

    @ApiOperation(value = "列表", notes = "列表")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public ResultPage list(@RequestBody ContentQuery query) {
        ResultPage result = contentService.list(query);
        return result;
    }

    @ApiOperation(value = "新建", notes = "新建")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Result add(@RequestBody ContentDO contentDO) {
        return contentService.insert(contentDO);
    }

    @ApiOperation(value = "详情", notes = "详情")
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "新闻资讯内容ID", required = true)})
    @RequestMapping(value = "/view/{id}", method = RequestMethod.POST)
    public Result view(@PathVariable int id) {
        return contentService.view(id);
    }

    @ApiOperation(value = "更新", notes = "更新")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public Result update(@RequestBody ContentDO contentDO) {
        return contentService.update(contentDO);
    }

    @ApiOperation(value = "删除", notes = "删除")
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "新闻资讯内容ID", required = true)})
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    public Result delete(@PathVariable int id) {
        return contentService.delete(id);
    }

    @ApiOperation(value = "上下架", notes = "上下架")
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "新闻资讯内容ID", required = true),
            @ApiImplicitParam(name = "status", value = "0下架 1上架", required = true)})
    @RequestMapping(value = "/update-status/{id}/{status}", method = RequestMethod.POST)
    public Result updateStatus(@PathVariable int id, @PathVariable int status) {
        return contentService.updateStatus(id,status);
    }

    @ApiOperation(value = "是否置顶", notes = "是否置顶")
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "新闻资讯内容ID", required = true),
            @ApiImplicitParam(name = "top", value = "0未置顶 1.置顶", required = true)})
    @RequestMapping(value = "/top/{id}/{top}", method = RequestMethod.POST)
    public Result top(@PathVariable int id, @PathVariable int top) {
        return contentService.top(id,top);
    }

    @ApiOperation(value = "是否推荐", notes = "是否推荐")
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "新闻资讯内容ID", required = true),
            @ApiImplicitParam(name = "recommend", value = "0否 1.是", required = true)})
    @RequestMapping(value = "/recommend/{id}/{recommend}", method = RequestMethod.POST)
    public Result recommend(@PathVariable int id, @PathVariable int recommend) {
        return contentService.recommend(id,recommend);
    }
}
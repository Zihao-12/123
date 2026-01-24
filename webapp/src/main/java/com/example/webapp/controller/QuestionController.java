package com.example.webapp.controller;

import com.example.webapp.result.ResultPage;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = {"1010-题目管理"})
@RefreshScope
@RestController
@LoginRequired(platform= PlatformMarkEnum.ENTERPRISE)
@RequestMapping("/api/bms/question")
public class QuestionController {
    @Autowired
    private QuestionService questionService;


    @ApiOperation(value = "题目列表", notes = "题目列表")
    @PostMapping(value = "list")
    public ResultPage list (@RequestBody QuestionQuery query){
        PageInfo page = questionService.findList(query);
        if(page==null){
            return ResultPage.fail("暂无数据");
        }
        return ResultPage.ok(page.getList(),page.getPageNum(),page.getPageSize(),page.getTotal());
    }


    @ApiOperation(value = "题目详情", notes = "题目详情")
    @ApiImplicitParams({@ApiImplicitParam(name = "questionId", value = "题目ID", required = true)})
    @GetMapping(value = "/view/{questionId}")
    public Result view(@PathVariable("questionId") Integer questionId){
        Result result = questionService.view(questionId);
        return result;
    }

    @ApiOperation(value = "添加题目", notes = "添加题目")
    @PostMapping(value = "insert")
    public Result insert(@RequestBody QuestionDO questionDO){
        Result result = questionService.insert(questionDO);
        return result;
    }

    @ApiOperation(value = "编辑题目", notes = "编辑题目")
    @PostMapping(value = "update")
    public Result update(@RequestBody QuestionDO questionDO){
        Result result = questionService.update(questionDO);
        return result;
    }


    @ApiOperation(value = "删除题目", notes = "删除题目")
    @ApiImplicitParams({@ApiImplicitParam(name = "questionId", value = "题目ID", required = true)})
    @GetMapping(value = "delete/{questionId}")
    public Result delete(@PathVariable Integer questionId){
        return questionService.delete(questionId);
    }

    @ApiOperation(value = "题目上下架", notes = "题目上下架")
    @ApiImplicitParams({@ApiImplicitParam(name = "questionId", value = "题目ID", required = true),
            @ApiImplicitParam(name = "status", value = "0下架1.上架", required = true)})
    @GetMapping(value = "/update-status/{questionId}/{status}")
    public Result updateStatus(@PathVariable("questionId") Integer questionId,@PathVariable("status") Integer status){
        return questionService.updateStatus(questionId,status);
    }

}
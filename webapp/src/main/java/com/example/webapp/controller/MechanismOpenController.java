package com.example.webapp.controller;

import com.example.webapp.DO.MechanismOpenDO;
import com.example.webapp.DTO.MechanismOpenDelayDTO;
import com.example.webapp.Service.mechanismOpen.MechanismOpenService;
import com.example.webapp.result.Result;
import com.example.webapp.result.ResultPage;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

@Api(tags = {"1005-开通机构"})
@RefreshScope
@RestController
@Component
@Slf4j
@LoginRequired(platform= PlatformMarkEnum.ENTERPRISE)
@RequestMapping("/api/bms/mech-open")
public class MechanismOpenController {
    @Autowired
    private MechanismOpenService mechanismOpenService;

    @ApiOperation(value = "机构实训开通列表",notes = "机构实训开通列表")
    @PostMapping(value = "list")
    public ResultPage findMechanismOpenList (@RequestBody MechanismOpenQuery query){
        PageInfo page = mechanismOpenService.findMechanismOpenList(query);
        if(page==null){
            page =new PageInfo();
        }
        return ResultPage.ok(page.getList(),page.getPageNum(),page.getPageSize(),page.getTotal());
    }

    @ApiOperation(value = "机构实训开通",notes = "创建机构开通,当id不为空时,为编辑机构信息")
    @PostMapping(value = "add")
    public Result saveMechanismOpenDO(@RequestBody MechanismOpenDO mechanismOpenDO){
        Result result = mechanismOpenService.saveMechanismOpenDO(mechanismOpenDO);
        return result;
    }

    @ApiOperation(value = "机构实训开通主键查询",notes = "机构实训开通主键查询")
    @ApiImplicitParams({@ApiImplicitParam(name="mechanismOpenId",value="机构开通ID",required=true,paramType="path")})
    @PostMapping(value = "find/{mechanismOpenId}")
    public Result findMechanismOpenById(@PathVariable Integer mechanismOpenId){
        Result result = mechanismOpenService.findMechanismOpenById(mechanismOpenId);
        return result;
    }

    @ApiOperation(value = "查询课程包",notes = "查询课程包")
    @ApiImplicitParams({@ApiImplicitParam(name="packageId",value="课程包ID",required=true,paramType="path")})
    @PostMapping(value = "find-course-package/{packageId}")
    public Result findCoursePackageById(@PathVariable Integer packageId){
        Result result = mechanismOpenService.findCoursePackageById(packageId);
        return result;
    }

    @ApiOperation(value = "机构实训开通删除",notes = "机构实训开通删除")
    @ApiImplicitParams({@ApiImplicitParam(name="openId",value="机构开通ID",required=true,paramType="path")})
    @PostMapping(value = "del-open/{openId}")
    public Result delOpen(@PathVariable Integer openId){
        Result result = mechanismOpenService.delOpen(openId);
        return result;
    }

    @ApiOperation(value = "判断 机构实训开通 是否可以删除 ",notes = "判断 机构实训开通 是否可以删除")
    @ApiImplicitParams({@ApiImplicitParam(name="openId",value="机构开通ID",required=true,paramType="path",example = "37")})
    @PostMapping(value = "is-del-open/{openId}")
    public Result isDelOpen(@PathVariable Integer openId){
        Result result = mechanismOpenService.isDelOpen(openId);
        return result;
    }

    @ApiOperation(value = "机构微软/实训开通停用/启用",notes = "机构微软/实训开通停用/启用")
    @ApiImplicitParams({@ApiImplicitParam(name="openId",value="机构开通ID",required=true,paramType="path")})
    @PostMapping(value = "deactivate-open/{openId}")
    public Result deactivateOpen(@PathVariable Integer openId){
        Result result = mechanismOpenService.deactivateOpen(openId);
        return result;
    }

    @ApiOperation(value = "机构实训开通延期",notes = "根据开始结束时间计算延期天数更新开通结束时间")
    @PostMapping(value = "open-delay")
    public Result openDelay(@RequestBody MechanismOpenDelayDTO openDelay){
        Result result = mechanismOpenService.openDelay(openDelay);
        return result;
    }

}
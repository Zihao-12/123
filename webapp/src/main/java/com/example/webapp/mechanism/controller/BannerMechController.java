package com.example.webapp.mechanism.controller;

import com.example.webapp.DO.BannerDO;
import com.example.webapp.DTO.BannerDTO;
import com.example.webapp.DTO.UserDto;
import com.example.webapp.Service.Banner.BannerService;
import com.example.webapp.annotation.LoginRequired;
import com.example.webapp.common.Constant;
import com.example.webapp.enums.BannerTypeEnum;
import com.example.webapp.enums.PlatformMarkEnum;
import com.example.webapp.query.BannerQuery;
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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Api(tags = {"1002-轮播图"})
@RestController
@RefreshScope
@LoginRequired(platform= PlatformMarkEnum.MECHANISM)
@Slf4j
@RequestMapping("/api/mech/banner")
@Controller
public class BannerMechController extends BaseMechController{
    @Autowired
    BannerService bannerService;

    @ApiOperation(value = "列表", notes = "列表")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public ResultPage list(@RequestBody BannerQuery query) {
        UserDto userDto = UserThreadLocal.get();
        query.setMechanismId(userDto.getId());
        query.setType(BannerTypeEnum.H5_HOME.getType());
        ResultPage result = bannerService.mechList(query);
        return result;
    }

    @ApiOperation(value = "新建", notes = "轮播图类型 1.H5首页 不能创建小程序内容")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Result add(@RequestBody BannerDO banner) {
        UserDto userDto = UserThreadLocal.get();
        banner.setMechanismId(userDto.getId());
        banner.setType(BannerTypeEnum.H5_HOME.getType());
        return bannerService.insert(banner);
    }

    @ApiOperation(value = "详情", notes = "详情")
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "id", required = true)})
    @RequestMapping(value = "/view/{id}", method = RequestMethod.POST)
    public Result view(@PathVariable int id) {
        BannerDTO bannerDTO = bannerService.view(id);
        if(bannerDTO == null){
            return Result.fail(Constant.NO_DATA);
        }
        return Result.ok(bannerDTO);
    }

    @ApiOperation(value = "更新", notes = "更新")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public Result update(@RequestBody BannerDO bannerDO) {
        UserDto userDto = UserThreadLocal.get();
        BannerDTO bannerDTO = bannerService.view(bannerDO.getId());
        if(bannerDTO == null){
            return Result.fail(Constant.NO_DATA);
        }
        if (!isBelongMechanism(userDto.getId(), bannerDTO.getMechanismId())) {
            return Result.fail("无权限");
        }
        return bannerService.update(bannerDO);
    }


    @ApiOperation(value = "删除", notes = "删除")
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "id", required = true)})
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    public Result delete(@PathVariable int id) {
        UserDto userDto = UserThreadLocal.get();
        BannerDTO bannerDTO = bannerService.view(id);
        if(bannerDTO == null){
            return Result.fail(Constant.NO_DATA);
        }
        if (!isBelongMechanism(userDto.getId(), bannerDTO.getMechanismId())) {
            return Result.fail("无权限");
        }
        return bannerService.delete(id);
    }

    @ApiOperation(value = "上下架:0下架 1上架", notes = "上下架:0下架 1上架")
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "id", required = true)})
    @RequestMapping(value = "/update-status/{id}/{status}", method = RequestMethod.POST)
    public Result updateStatus(@PathVariable int id, @PathVariable int status) {
        UserDto userDto = UserThreadLocal.get();
        BannerDTO bannerDTO = bannerService.view(id);
        if(bannerDTO == null){
            return Result.fail(Constant.NO_DATA);
        }
        if (!isBelongMechanism(userDto.getId(), bannerDTO.getMechanismId())) {
            return Result.fail("无权限");
        }
        return bannerService.updateStatus(id,status);
    }
}

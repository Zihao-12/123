package com.example.webapp.controller;

import com.example.webapp.DO.BannerDO;
import com.example.webapp.DTO.BannerDTO;
import com.example.webapp.Query.BannerQuery;
import com.example.webapp.Query.BindMechanismQuery;
import com.example.webapp.Service.Banner.BannerService;
import com.example.webapp.common.Constant;
import com.example.webapp.result.Result;
import com.example.webapp.result.ResultPage;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

public class BannerController {
    @Autowired
    BannerService bannerService;

    @ApiOperation(value = "列表", notes = "列表")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public ResultPage list(@RequestBody BannerQuery query) {
        query.setMechanismId(Constant.YUNYING_MECHANISM_ID);
        ResultPage result = bannerService.list(query);
        return result;
    }

    @ApiOperation(value = "新建", notes = "轮播图类型 1.H5首页 2.小程序首页")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Result add(@RequestBody BannerDO banner) {
        banner.setMechanismId(Constant.YUNYING_MECHANISM_ID);
        return bannerService.insert(banner);
    }

    @ApiOperation(value = "详情", notes = "详情")
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "id", required = true)})
    @RequestMapping(value = "/view/{id}", method = RequestMethod.POST)
    public Result view(@PathVariable int id) {
        BannerDTO bannerDTO = bannerService.view(id);
        return Result.ok(bannerDTO);
    }

    @ApiOperation(value = "更新", notes = "更新")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public Result update(@RequestBody BannerDO bannerDO) {
        return bannerService.update(bannerDO);
    }

    @ApiOperation(value = "删除", notes = "删除")
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "id", required = true)})
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    public Result delete(@PathVariable int id) {
        return bannerService.delete(id);
    }

    @ApiOperation(value = "上下架:0下架 1上架", notes = "上下架:0下架 1上架")
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "id", required = true)})
    @RequestMapping(value = "/update-status/{id}/{status}", method = RequestMethod.POST)
    public Result updateStatus(@PathVariable int id, @PathVariable int status) {
        return bannerService.updateStatus(id,status);
    }

    @ApiOperation(value = "轮播图绑定到全部机构", notes = "轮播图绑定到全部机构")
    @ApiImplicitParams({@ApiImplicitParam(name = "bannerId", value = "轮播图ID", required = true)})
    @PostMapping(value = "/bind-all-mechanism/{bannerId}")
    public Result bindAllMechanism(@PathVariable Integer bannerId) {
        Result result = bannerService.bindAllMechanism(bannerId);
        return result;
    }

    @ApiOperation(value = "轮播图绑定到指定部机构", notes = "轮播图绑定到指定部机构")
    @PostMapping(value = "/bind-specify-mechanism")
    public Result bindSpecifyMechanism(@RequestBody BindMechanismDTO bindMechanismDTO) {
        Result result = bannerService.bindSpecifyMechanism(bindMechanismDTO);
        return result;
    }

    @ApiOperation(value = "取消轮播图关联机构", notes = "取消轮播图关联机构")
    @ApiImplicitParams({@ApiImplicitParam(name = "bannerId", value = "轮播图ID", required = true),
            @ApiImplicitParam(name = "mechanismId", value = "机构ID", required = true)})
    @RequestMapping(value = "/disassociate/{bannerId}/{mechanismId}", method = RequestMethod.POST)
    public Result disassociate(@PathVariable Integer bannerId, @PathVariable Integer mechanismId) {
        return bannerService.disassociate(bannerId,mechanismId);
    }

    @ApiOperation(value = "获取轮播图关联的机构列表", notes = "获取轮播图关联的机构列表")
    @ApiImplicitParams({@ApiImplicitParam(name="bannerId",value="轮播图ID",required=true),
            @ApiImplicitParam(name="name",value="机构名称关键字搜索")})
    @PostMapping(value = "/get-ref-mechanism-list")
    public ResultPage getRefMechanismList (@RequestParam("bannerId") Integer bannerId,
                                           @RequestParam("name") String name){
        BindMechanismQuery query = new BindMechanismQuery();
        query.setId(bannerId);
        query.setName(name);
        query.setPageSize(10000);
        PageInfo page = bannerService.getRefMechanismList(query);
        if(page==null){
            return ResultPage.fail("暂无数据");
        }
        return ResultPage.ok(page.getList(),page.getPageNum(),page.getPageSize(),page.getTotal());
    }
}
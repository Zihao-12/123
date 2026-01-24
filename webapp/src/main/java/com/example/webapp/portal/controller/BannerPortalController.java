package com.example.webapp.portal.controller;

import com.example.webapp.DTO.BannerDTO;
import com.example.webapp.DTO.UserDto;
import com.example.webapp.Service.Banner.BannerService;
import com.example.webapp.VO.BannerVO;
import com.example.webapp.annotation.LoginRequired;
import com.example.webapp.enums.BannerTypeEnum;
import com.example.webapp.enums.UpDownStatusEnum;
import com.example.webapp.query.BannerQuery;
import com.example.webapp.result.CodeEnum;
import com.example.webapp.result.Result;
import com.example.webapp.result.ResultPage;
import com.example.webapp.utils.UserThreadLocal;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = {"1002-首页轮播图"})
@RestController
@RefreshScope
@LoginRequired
@Slf4j
@RequestMapping("/api/portal/banner")
@Controller
public class BannerPortalController {
    @Autowired
    BannerService bannerService;

    @ApiOperation(value = "H5首页轮播图", notes = "H5首页轮播图")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public Result list() {
        UserDto userDto = UserThreadLocal.get();
        BannerQuery query =new BannerQuery();
        query.setStatus(UpDownStatusEnum.UP.getStatus());
        query.setMechanismId(userDto.getMechanismId());
        query.setPageSize(Integer.MAX_VALUE);
        query.setType(BannerTypeEnum.H5_HOME.getType());
        ResultPage result = bannerService.mechList(query);
        return toBannerVoResult(result);
    }

    @ApiOperation(value = "小程序首页轮播图", notes = "小程序首页轮播图")
    @RequestMapping(value = "/app-list", method = RequestMethod.POST)
    public Result applist() {
        UserDto userDto = UserThreadLocal.get();
        BannerQuery query =new BannerQuery();
        query.setStatus(UpDownStatusEnum.UP.getStatus());
        query.setMechanismId(userDto.getMechanismId());
        query.setPageSize(Integer.MAX_VALUE);
        query.setType(BannerTypeEnum.APP_HOME.getType());
        ResultPage result = bannerService.mechList(query);
        return toBannerVoResult(result);
    }



    private Result toBannerVoResult(ResultPage result) {
        if(result.getCode().equals(CodeEnum.SUCCESS.getValue())){
            List<BannerDTO> bannerDTOList = (List<BannerDTO>) result.getData();
            if(CollectionUtils.isNotEmpty(bannerDTOList)){
                List<BannerVO> voList = Lists.newArrayList();
                bannerDTOList.forEach(n->{
                    BannerVO vo = new BannerVO();
                    vo.setImageUrl(n.getImageUrl());
                    vo.setImageUrlPc(n.getImageUrlPc());
                    vo.setJumpType(n.getJumpType());
                    vo.setJumpUrl(n.getJumpUrl());
                    vo.setSort(n.getSort());
                    vo.setId(n.getId());
                    vo.setName(n.getName());
                    voList.add(vo);
                });
                return Result.ok(voList);
            }
        }
        return Result.fail();
    }

}

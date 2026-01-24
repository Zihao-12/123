package com.example.webapp.mechanism.controller;

import com.example.webapp.DO.MessageDO;
import com.example.webapp.DTO.MessageDTO;
import com.example.webapp.DTO.UserDto;
import com.example.webapp.Service.Message.MessageService;
import com.example.webapp.annotation.LoginRequired;
import com.example.webapp.common.Constant;
import com.example.webapp.enums.PlatformMarkEnum;
import com.example.webapp.query.MessageQuery;
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
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

@Api(tags = {"1004-消息管理表"})
@RefreshScope
@RestController
@Component
@Slf4j
@LoginRequired(platform= PlatformMarkEnum.MECHANISM)
@RequestMapping("/api/mech/message")
public class MessageMechController extends BaseMechController{
    @Autowired
    MessageService messageService;

    @ApiOperation(value = "列表", notes = "列表")
    @PostMapping(value = "/list")
    public ResultPage<MessageDTO> list(@RequestBody MessageQuery query) {
        UserDto userDto = UserThreadLocal.get();
        query.setMechanismId(userDto.getId());
        ResultPage result = messageService.mechList(query);
        return result;
    }

    @ApiOperation(value = "新建", notes = "新建")
    @PostMapping(value = "/add")
    public Result add(@RequestBody MessageDO messageDO) {
        UserDto userDto = UserThreadLocal.get();
        messageDO.setMechanismId(userDto.getId());
        Result result =  messageService.insert(messageDO);
        return result;
    }

    @ApiOperation(value = "详情", notes = "详情")
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "消息管理表ID", required = true)})
    @PostMapping(value = "/view/{id}")
    public Result<MessageDTO> view(@PathVariable int id) {
        UserDto userDto = UserThreadLocal.get();
        MessageDTO messageDTO = messageService.view(id);
        if(messageDTO == null){
            return Result.fail(Constant.NO_DATA);
        }
        return Result.ok(messageDTO);
    }

    @ApiOperation(value = "更新", notes = "更新")
    @PostMapping(value = "/update")
    public Result update(@RequestBody MessageDO messageDO) {
        UserDto userDto = UserThreadLocal.get();
        MessageDTO messageDTO = messageService.view(messageDO.getId());
        if(messageDTO == null){
            return Result.fail(Constant.NO_DATA);
        }
        if (!isBelongMechanism(userDto.getId(), messageDTO.getMechanismId())) {
            return Result.fail("无权限");
        }
        return messageService.update(messageDO);
    }

    @ApiOperation(value = "删除", notes = "删除")
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "消息管理表ID", required = true)})
    @PostMapping(value = "/delete/{id}")
    public Result delete(@PathVariable int id) {
        UserDto userDto = UserThreadLocal.get();
        MessageDTO messageDTO = messageService.view(id);
        if(messageDTO == null){
            return Result.fail(Constant.NO_DATA);
        }
        if (!isBelongMechanism(userDto.getId(), messageDTO.getMechanismId())) {
            return Result.fail("无权限");
        }
        return messageService.delete(id);
    }

    @ApiOperation(value = "更新状态", notes = "更新状态")
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "消息管理表ID", required = true),
            @ApiImplicitParam(name = "status", value = "0待发布 1已发布", required = true)})
    @RequestMapping(value = "/update-status/{id}/{status}", method = RequestMethod.POST)
    public Result updateStatus(@PathVariable int id, @PathVariable int status) {
        UserDto userDto = UserThreadLocal.get();
        MessageDTO messageDTO = messageService.view(id);
        if(messageDTO == null){
            return Result.fail(Constant.NO_DATA);
        }
        if (!isBelongMechanism(userDto.getId(), messageDTO.getMechanismId())) {
            return Result.fail("无权限");
        }
        return messageService.updateStatus(id,status);
    }

}

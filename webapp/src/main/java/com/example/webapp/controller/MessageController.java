package com.example.webapp.controller;

import com.example.webapp.result.Result;
import com.github.pagehelper.PageInfo;
import com.zhihuiedu.business.common.Constant;
import com.zhihuiedu.business.dto.BindMechanismDTO;
import com.zhihuiedu.business.dto.MessageDTO;
import com.zhihuiedu.business.entity.MessageDO;
import com.zhihuiedu.business.query.BindMechanismQuery;
import com.zhihuiedu.business.query.MessageQuery;
import com.zhihuiedu.business.service.message.MessageService;
import com.zhihuiedu.framework.annotation.LoginRequired;
import com.zhihuiedu.framework.enums.PlatformMarkEnum;
import com.zhihuiedu.framework.result.Result;
import com.zhihuiedu.framework.result.ResultPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

/** 消息管理表
 * @author ghs 
 */
@Api(tags = {"1009-消息管理表"})
@RefreshScope
@RestController
@Component
@Slf4j
@LoginRequired(platform= PlatformMarkEnum.ENTERPRISE)
@RequestMapping("/api/bms/message")
public class MessageController {
    @Autowired
    MessageService messageService;

    @ApiOperation(value = "列表", notes = "列表")
    @PostMapping(value = "/list")
    public ResultPage<MessageDTO> list(@RequestBody MessageQuery query) {
        query.setMechanismId(Constant.YUNYING_MECHANISM_ID);
        ResultPage result = messageService.list(query);
        return result;
    }

    @ApiOperation(value = "新建", notes = "新建")
    @PostMapping(value = "/add")
    public Result add(@RequestBody MessageDO messageDO) {
        messageDO.setMechanismId(Constant.YUNYING_MECHANISM_ID);
        Result result =  messageService.insert(messageDO);
        return result;
    }

    @ApiOperation(value = "详情", notes = "详情")
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "消息管理表ID", required = true)})
    @PostMapping(value = "/view/{id}")
    public Result<MessageDTO> view(@PathVariable int id) {
        return Result.ok(messageService.view(id));
    }

    @ApiOperation(value = "更新", notes = "更新")
    @PostMapping(value = "/update")
    public Result update(@RequestBody MessageDO messageDO) {
        return messageService.update(messageDO);
    }

    @ApiOperation(value = "删除", notes = "删除")
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "消息管理表ID", required = true)})
    @PostMapping(value = "/delete/{id}")
    public Result delete(@PathVariable int id) {
        return messageService.delete(id);
    }

    @ApiOperation(value = "更新状态", notes = "更新状态")
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "消息管理表ID", required = true),
            @ApiImplicitParam(name = "status", value = "0待发布 1已发布", required = true)})
    @RequestMapping(value = "/update-status/{id}/{status}", method = RequestMethod.POST)
    public Result updateStatus(@PathVariable int id, @PathVariable int status) {
        return messageService.updateStatus(id,status);
    }

    @ApiOperation(value = "消息绑定到全部机构", notes = "消息绑定到全部机构")
    @ApiImplicitParams({@ApiImplicitParam(name = "messageId", value = "消息ID", required = true)})
    @PostMapping(value = "/bind-all-mechanism/{messageId}")
    public Result bindAllMechanism(@PathVariable Integer messageId) {
        Result result = messageService.bindAllMechanism(messageId);
        return result;
    }

    @ApiOperation(value = "消息绑定到指定部机构", notes = "消息绑定到指定部机构")
    @PostMapping(value = "/bind-specify-mechanism")
    public Result bindSpecifyMechanism(@RequestBody BindMechanismDTO bindMechanismDTO) {
        Result result = messageService.bindSpecifyMechanism(bindMechanismDTO);
        return result;
    }

    @ApiOperation(value = "取消消息关联机构", notes = "取消消息关联机构")
    @ApiImplicitParams({@ApiImplicitParam(name = "messageId", value = "消息ID", required = true),
            @ApiImplicitParam(name = "mechanismId", value = "机构ID", required = true)})
    @RequestMapping(value = "/disassociate/{messageId}/{mechanismId}", method = RequestMethod.POST)
    public Result disassociate(@PathVariable Integer messageId, @PathVariable Integer mechanismId) {
        return messageService.disassociate(messageId,mechanismId);
    }

    @ApiOperation(value = "获取消息关联的机构列表", notes = "获取消息关联的机构列表")
    @ApiImplicitParams({@ApiImplicitParam(name="messageId",value="消息ID",required=true),
            @ApiImplicitParam(name="name",value="机构名称关键字搜索")})
    @PostMapping(value = "/get-ref-mechanism-list")
    public ResultPage getRefMechanismList (@RequestParam("messageId") Integer messageId,
                                           @RequestParam("name") String name){
        BindMechanismQuery query = new BindMechanismQuery();
        query.setId(messageId);
        query.setName(name);
        query.setPageSize(10000);
        PageInfo page = messageService.getRefMechanismList(query);
        if(page==null){
            return ResultPage.fail("暂无数据");
        }
        return ResultPage.ok(page.getList(),page.getPageNum(),page.getPageSize(),page.getTotal());
    }
}


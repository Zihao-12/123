package com.example.webapp.bms.controller;

import com.example.webapp.DO.ActivityDO;
import com.example.webapp.DO.ActivityOperationRefDO;
import com.example.webapp.DTO.*;
import com.example.webapp.annotation.LoginRequired;
import com.example.webapp.query.ActivityQuery;
import com.example.webapp.query.BindMechanismQuery;
import com.example.webapp.Service.Message.MessageService;
import com.example.webapp.Service.activity.ActivityService;
import com.example.webapp.enums.PlatformMarkEnum;
import com.example.webapp.result.Result;
import com.example.webapp.result.ResultPage;
import com.example.webapp.utils.EasyExcelUtils;
import com.example.webapp.utils.EasySheet;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** 活动
 * @author ghs 
 */
@Api(tags = {"1011-活动管理"})
@RefreshScope
@RestController
@Component
@Slf4j
@LoginRequired(platform= PlatformMarkEnum.ENTERPRISE)
@RequestMapping("/api/bms/activity")
public class ActivityController {
    @Autowired
    ActivityService activityService;
    @Autowired
    MessageService messageService;

    @ApiOperation(value = "列表", notes = "列表")
    @PostMapping(value = "/list")
    public ResultPage<ActivityDTO> list(@RequestBody ActivityQuery query) {
        ResultPage result = activityService.list(query);
        return result;
    }

    @ApiOperation(value = "新建活动", notes = "新建活动")
    @PostMapping(value = "/add")
    public Result add(@RequestBody ActivityDO activityDO) {
        Result result =  activityService.insert(activityDO);
        return result;
    }

    @ApiOperation(value = "保存答题内容", notes = "保持答题内容")
    @PostMapping(value = "/save-content")
    public Result saveContent(@RequestBody ActivityContentParam param) {
        Result result =  activityService.saveContent(param);
        return result;
    }

    @ApiOperation(value = "保存活动运营设置", notes = "保存活动运营设置")
    @PostMapping(value = "/save-operation")
    public Result saveOperation(@RequestBody ActivityOperationRefDO activityOperation) {
        Result result =  activityService.saveOperation(activityOperation);
        return result;
    }

    @ApiOperation(value = "活动详情", notes = "活动详情")
    @ApiImplicitParams({@ApiImplicitParam(name = "activityId", value = "活动ID", required = true)})
    @PostMapping(value = "/view/{activityId}")
    public Result<ActivityDTO> view(@PathVariable int activityId) {
        return Result.ok(activityService.view(activityId));
    }

    @ApiOperation(value = "活动运营详情", notes = "活动运营详情")
    @ApiImplicitParams({@ApiImplicitParam(name = "activityId", value = "活动ID", required = true)})
    @PostMapping(value = "/operation-view/{activityId}")
    public Result<ActivityOperationRefDO> operationView(@PathVariable int activityId) {
        return Result.ok(activityService.operationView(activityId));
    }

    @ApiOperation(value = "活动答题内容详情", notes = "活动答题内容详情")
    @ApiImplicitParams({@ApiImplicitParam(name = "activityId", value = "活动ID", required = true)})
    @PostMapping(value = "/content-view/{activityId}")
    public Result<ActivityContentParam> contentView(@PathVariable int activityId) {
        return Result.ok(activityService.contentView(activityId));
    }

    
    @ApiOperation(value = "更新", notes = "更新")
    @PostMapping(value = "/update")
    public Result update(@RequestBody ActivityDO activityDO) {
        return activityService.update(activityDO);
    }

    @ApiOperation(value = "删除", notes = "删除")
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "活动ID", required = true)})
    @PostMapping(value = "/delete/{id}")
    public Result delete(@PathVariable int id) {
        return activityService.delete(id);
    }

   @ApiOperation(value = "更新状态", notes = "更新状态")
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "活动ID", required = true),
                            @ApiImplicitParam(name = "status", value = "是否上架：1上架 0下架", required = true)})
    @RequestMapping(value = "/update-status/{id}/{status}", method = RequestMethod.POST)
    public Result updateStatus(@PathVariable int id, @PathVariable int status) {
        return activityService.updateStatus(id,status);
    }

    @ApiOperation(value = "活动绑定到全部机构", notes = "活动绑定到全部机构")
    @ApiImplicitParams({@ApiImplicitParam(name = "activityId", value = "活动ID", required = true)})
    @PostMapping(value = "/bind-all-mechanism/{activityId}")
    public Result bindAllMechanism(@PathVariable Integer activityId) {
        Result result = activityService.bindAllMechanism(activityId);
        return result;
    }

    @ApiOperation(value = "活动绑定到指定部机构", notes = "活动绑定到指定部机构")
    @PostMapping(value = "/bind-specify-mechanism")
    public Result bindSpecifyMechanism(@RequestBody BindMechanismDTO bindMechanismDTO) {
        Result result = activityService.bindSpecifyMechanism(bindMechanismDTO);
        return result;
    }

    @ApiOperation(value = "取消活动关联机构", notes = "取消活动关联机构")
    @ApiImplicitParams({@ApiImplicitParam(name = "activityId", value = "活动ID", required = true),
            @ApiImplicitParam(name = "mechanismId", value = "机构ID", required = true)})
    @RequestMapping(value = "/disassociate/{activityId}/{mechanismId}", method = RequestMethod.POST)
    public Result disassociate(@PathVariable Integer activityId, @PathVariable Integer mechanismId) {
        return activityService.disassociate(activityId,mechanismId);
    }

    @ApiOperation(value = "获取活动关联的机构列表", notes = "获取活动关联的机构列表")
    @ApiImplicitParams({@ApiImplicitParam(name="activityId",value="活动ID",required=true),
            @ApiImplicitParam(name="name",value="机构名称关键字搜索")})
    @PostMapping(value = "/get-ref-mechanism-list")
    public ResultPage getRefMechanismList (@RequestParam("activityId") Integer activityId,
                                           @RequestParam("name") String name){
        BindMechanismQuery query = new BindMechanismQuery();
        query.setId(activityId);
        query.setName(name);
        query.setPageSize(10000);
        PageInfo page = activityService.getRefMechanismList(query);
        if(page==null){
            return ResultPage.fail("暂无数据");
        }
        return ResultPage.ok(page.getList(),page.getPageNum(),page.getPageSize(),page.getTotal());
    }



    @ApiOperation(value = "恢复抽奖库存缓存", notes = "恢复抽奖库存缓存")
    @GetMapping(value = "/recovery-lottery-redis")
    public Result recoveryLotteryRedis() {
        return activityService.recoveryLotteryRedis();
    }

    @ApiOperation(value = "导出活动用户答题情况 ", notes = "导出活动用户答题情况")
    @GetMapping(value = "down-user-answers/{activityId}")
    public void downUserAnswers (@PathVariable Integer activityId, HttpServletResponse response){
        try {
            EasySheet sheet =new EasySheet();
            sheet.setFileName("用户答题情况");
            sheet.setSheetName("用户答题情况");
            sheet.setHeaders(new String[]{"机构名称","用户名","手机","活动ID","活动名称","得分","用时(秒)","时间(秒)"});
            List<UserAnswersExportDTO> list =  activityService.downUserAnswers(activityId);
            sheet.setDataList(list);
            List<EasySheet> sheetList =  Lists.newArrayList();
            sheetList.add(sheet);
            EasyExcelUtils.exportExcel(sheetList,response);
        }catch (Exception e){
            log.error("excel下载失败:{}", ExceptionUtils.getStackTrace(e));
        }
    }

    @ApiOperation(value = "导出活动用户奖品明细", notes = "导出活动用户奖品明细")
    @GetMapping(value = "down-lottery-details/{activityId}")
    public void downLotteryDetails (@PathVariable Integer activityId, HttpServletResponse response){
        try {
            EasySheet sheet =new EasySheet();
            sheet.setFileName("用户奖品明细");
            sheet.setSheetName("用户奖品明细");
            sheet.setHeaders(new String[]{"活动ID","活动名称","机构名称","用户名","手机","奖品","抽奖时间","参加活动时间"});
            List<LotteryDetailsExportDTO> list =  activityService.downLotteryDetails(activityId);
            sheet.setDataList(list);
            List<EasySheet> sheetList =  Lists.newArrayList();
            sheetList.add(sheet);
            EasyExcelUtils.exportExcel(sheetList,response);
        }catch (Exception e){
            log.error("excel下载失败:{}", ExceptionUtils.getStackTrace(e));
        }
    }
}


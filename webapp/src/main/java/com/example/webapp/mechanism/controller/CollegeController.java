package com.example.webapp.mechanism.controller;

import com.example.webapp.DO.UserDO;
import com.example.webapp.DTO.CreateCollegeNodeDTO;
import com.example.webapp.DTO.SelectUserBindDTO;
import com.example.webapp.Service.college.CollegeService;
import com.example.webapp.annotation.LoginRequired;
import com.example.webapp.common.Constant;
import com.example.webapp.enums.ClassPositionEnum;
import com.example.webapp.enums.PlatformMarkEnum;
import com.example.webapp.enums.UserRoleEnum;
import com.example.webapp.query.UserQuery;
import com.example.webapp.result.Result;
import com.example.webapp.result.ResultPage;
import com.example.webapp.utils.EasyExcelUtils;
import com.example.webapp.utils.EasySheet;
import com.example.webapp.utils.UserThreadLocal;
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

@Api(tags = {"1001-学院班级管理"})
@RefreshScope
@RestController
@Component
@Slf4j
@LoginRequired(platform= PlatformMarkEnum.MECHANISM)
@RequestMapping("/api/mech/teaching")
public class CollegeController {
    @Autowired
    private CollegeService collegeService;


    @ApiOperation(value = "创建机构学院根结点",notes = "根结点不可重复创建")
    @ApiImplicitParams({@ApiImplicitParam(name="mechanismName",value="机构名称",required=true,paramType="query",example = "清华大学")})
    @PostMapping(value = "create-mechanism-college-root")
    public Result createMechanismCollegeRoot(@RequestParam("mechanismId") Integer mechanismId, @RequestParam("mechanismName") String mechanismName){
        return collegeService.createMechanismCollegeRoot(mechanismId,mechanismName);
    }

    @ApiOperation(value = "获取机构学院根结点",notes = "获取机构学院根结点")
    @PostMapping(value = "/get-mechanism-college-root")
    public Result getMechanismCollegeRoot(){
        Integer mechanismId = UserThreadLocal.get().getId();
        return collegeService.getMechanismCollegeRoot(mechanismId);
    }


    @ApiOperation(value = "创建学院结点",notes = "创建学院结点")
    @ApiImplicitParams({@ApiImplicitParam(name="parentId",value="父节点ID",required=true,paramType="query"),
            @ApiImplicitParam(name="name",value="新节点名称",required=true,paramType="query",example = "电子系2班"),
            @ApiImplicitParam(name="description",value="节点描述",paramType="query"),
            @ApiImplicitParam(name="nodeType",value="节点类型:0院系,1班级",required=true,paramType="query")})
    @PostMapping(value = "create-college-node")
    public Result createCollegeNode(@RequestParam("parentId")Integer parentId,
                                    @RequestParam("name")String name,
                                    @RequestParam("description")String description,
                                    @RequestParam("nodeType")Integer nodeType){
        Integer mechanismId = UserThreadLocal.get() .getId();
        if(!collegeService.isBelongToMechanism(mechanismId,parentId)){
            return Result.fail("当前节点不属于该机构");
        }
        return collegeService.createCollegeNode(parentId,name,description,nodeType);
    }

    @ApiOperation(value = "批量创建学院结点",notes = "一次最多创建20个结点")
    @PostMapping(value = "bulkcreate-college-node")
    public Result bulkCreateCollegeNode(@RequestBody CreateCollegeNodeDTO createCollegeNodeDTO){
        Integer mechanismId = UserThreadLocal.get() .getId();
        if(!collegeService.isBelongToMechanism(mechanismId,createCollegeNodeDTO.getParentId())){
            return Result.fail("当前节点不属于该机构");
        }
        return collegeService.bulkCreateCollegeNode(createCollegeNodeDTO);
    }


    @ApiOperation(value = "查询学院组织结构",notes = "查询学院组织结构")
    @PostMapping(value = "find-mechanism-node")
    public Result  findCollegeNodeByMechanismId(){
        try {
            Integer mechanismId = UserThreadLocal.get() .getId();
            return collegeService.findCollegeNodeByMechanismId(mechanismId);
        } catch (Exception e) {
            log.error("{}", ExceptionUtils.getStackTrace(e));
        }
        return Result.fail();
    }

    @ApiOperation(value = "查询所有子孙节点",notes = "查询所有子孙节点")
    @ApiImplicitParams({@ApiImplicitParam(name="nodeId",value="节点ID",required=true,paramType="path")})
    @PostMapping(value = "find-offspring-node/{nodeId}")
    public Result findOffspringNodeByParentId(@PathVariable Integer nodeId){
        try {
            Integer mechanismId = UserThreadLocal.get() .getId();
            if(!collegeService.isBelongToMechanism(mechanismId,nodeId)){
                return Result.fail("当前节点不属于该机构");
            }
            return collegeService.findOffspringNodeByParentId(nodeId);
        } catch (Exception e) {
            log.error("{}",ExceptionUtils.getStackTrace(e));
        }
        return Result.fail();
    }

    @ApiOperation(value = "查询所有子节点",notes = "查询所有子节点")
    @ApiImplicitParams({@ApiImplicitParam(name="nodeId",value="节点ID",required=true,paramType="path")})
    @PostMapping(value = "find-child-node/{nodeId}")
    public Result  findChildNodeByParentId(@PathVariable Integer nodeId){
        try {
            Integer mechanismId = UserThreadLocal.get() .getId();
            if(!collegeService.isBelongToMechanism(mechanismId,nodeId)){
                return Result.fail("当前节点不属于该机构");
            }
            return collegeService.findChildNodeByParentId(nodeId);
        } catch (Exception e) {
            log.error("{}",ExceptionUtils.getStackTrace(e));
        }
        return Result.fail();
    }


    @ApiOperation(value = "删除部门/班级(包含子孙节点)",notes = "删除部门/班级(包含子孙节点)")
    @ApiImplicitParam(name="nodeId",value="节点ID",required=true,paramType="path")
    @PostMapping(value = "delete-college-node/{nodeId}")
    public Result  deleteCollegeNode(@PathVariable Integer nodeId){
        try {
            Integer mechanismId = UserThreadLocal.get() .getId();
            if(!collegeService.isBelongToMechanism(mechanismId,nodeId)){
                return Result.fail("当前节点不属于该机构");
            }
            return collegeService.deleteCollegeNode(nodeId);
        } catch (Exception e) {
            log.error("{}",ExceptionUtils.getStackTrace(e));
        }
        return Result.fail();
    }

    @ApiOperation(value = "修改节点名称",notes = "包含本节点和子孙节点的name全路径的修改")
    @ApiImplicitParams({@ApiImplicitParam(name="nodeId",value="节点ID",required=true,paramType="query"),
            @ApiImplicitParam(name="name",value="新节点名称",required=true,paramType="query")})
    @PostMapping(value = "update-college-code-name")
    public Result updateCollegeNodeName(@RequestParam("nodeId")Integer nodeId,@RequestParam("name")String name){
        try {
            Integer mechanismId = UserThreadLocal.get() .getId();
            if(!collegeService.isBelongToMechanism(mechanismId,nodeId)){
                return Result.fail("当前节点不属于该机构");
            }
            return collegeService.updateCollegeNodeName(nodeId,name);
        } catch (Exception e) {
            log.error("{}",ExceptionUtils.getStackTrace(e));
        }
        return Result.fail();
    }

    @ApiOperation(value = "保存学生",notes = "保存学生")
    @ApiImplicitParams({@ApiImplicitParam(name="collegeId",value="班级ID 大于0时把用户添加到班级",paramType="query")})
    @PostMapping(value = "save-user-student")
    public Result  saveUserStudent(Integer collegeId,@RequestBody UserDO userDO){
        try {
            Integer mechanismId = UserThreadLocal.get() .getId();
            if(collegeId!=null && !collegeService.isBelongToMechanism(mechanismId,collegeId)){
                return Result.fail("当前班级不属于该机构");
            }
            return collegeService.saveUserStudent(mechanismId,collegeId,userDO);
        } catch (Exception e) {
            log.error("{}",ExceptionUtils.getStackTrace(e));
        }
        return Result.fail();
    }

    @ApiOperation(value = "保存老师",notes = "保存老师")
    @PostMapping(value = "save-user-teacher")
    public Result  saveUserTeacher(@RequestBody UserDO userDO){
        try {
            Integer mechanismId = UserThreadLocal.get() .getId();
            return collegeService.saveUserTeacher(mechanismId,userDO);
        } catch (Exception e) {
            log.error("{}",ExceptionUtils.getStackTrace(e));
        }
        return Result.fail();
    }

    /**
     * 学生列表
     * @param query
     * @return
     */
    @LoginRequired(platform= PlatformMarkEnum.MECHANISM)
    @ApiOperation(value = "学生列表",notes = "学生列表")
    @RequestMapping(value = "/query-student-list",method = RequestMethod.POST)
    public ResultPage findStudentList (@RequestBody UserQuery query){
        Integer mechanismId = UserThreadLocal.get() .getId();
        query.setMechanismId(mechanismId);
        query.setPosition(UserRoleEnum.STUDENT.getType());
        PageInfo page=  collegeService.findUserList(query);
        return ResultPage.ok(page.getList(),page.getPageNum(),page.getPageSize(),page.getTotal());
    }

    /**
     * 老师列表
     * @param query
     * @return
     */
    @LoginRequired(platform= PlatformMarkEnum.MECHANISM)
    @ApiOperation(value = "老师列表",notes = "老师列表")
    @RequestMapping(value = "/query-teacher-list",method = RequestMethod.POST)
    public ResultPage findTeacherList (@RequestBody UserQuery query){
        Integer mechanismId = UserThreadLocal.get() .getId();
        query.setMechanismId(mechanismId);
        if(!UserRoleEnum.TEACHER.getType().equals(query.getPosition()) && !UserRoleEnum.ASSISTANT.getType().equals(query.getPosition())){
            query.setPosition(Constant.TEACHER);
        }
        PageInfo page=  collegeService.findUserList(query);
        return ResultPage.ok(page.getList(),page.getPageNum(),page.getPageSize(),page.getTotal());
    }

    /**
     * 根据ID查询用户
     * @param userId
     * @return
     */
    @LoginRequired(platform= PlatformMarkEnum.MECHANISM)
    @ApiOperation(value = "根据ID查询用户",notes = "根据ID查询用户")
    @ApiImplicitParams({@ApiImplicitParam(name="userId",value="用户ID",required=true,paramType="path",example = "7")})
    @RequestMapping(value = "find-user/{userId}",method = RequestMethod.POST)
    public Result  findUserById(@PathVariable Integer userId){
        try {
            Integer mechanismId = UserThreadLocal.get() .getId();
            if(!collegeService.isUserBelongToMechanism(mechanismId,userId)){
                return Result.fail("当前用户不属于该机构");
            }
            return collegeService.findUserById(userId);
        } catch (Exception e) {
            log.error("{}",ExceptionUtils.getStackTrace(e));
        }
        return Result.fail();
    }

    /**
     * 用户密码重置
     * @param userId
     * @return
     */
    @LoginRequired(platform= PlatformMarkEnum.MECHANISM)
    @ApiOperation(value = "用户密码重置",notes = "用户密码重置")
    @ApiImplicitParams({@ApiImplicitParam(name="userId",value="用户ID",required=true,paramType="path",example = "7")})
    @RequestMapping(value = "resetpwd/{userId}",method = RequestMethod.POST)
    public Result  resetPwd(@PathVariable Integer userId){
        try {
            Integer mechanismId = UserThreadLocal.get() .getId();
            if(!collegeService.isUserBelongToMechanism(mechanismId,userId)){
                return Result.fail("当前用户不属于该机构");
            }
            return collegeService.resetPwd(userId);
        } catch (Exception e) {
            log.error("{}",ExceptionUtils.getStackTrace(e));
        }
        return Result.fail();
    }

    /**
     * 用户停用/启用
     * @param userId
     * @return
     */
    @LoginRequired(platform= PlatformMarkEnum.MECHANISM)
    @ApiOperation(value = "用户停用/启用",notes = "用户停用/启用")
    @ApiImplicitParams({@ApiImplicitParam(name="userId",value="用户ID",required=true)})
    @RequestMapping(value = "deactivate-user/{userId}",method = RequestMethod.POST)
    public Result  deactivateUserById(@PathVariable Integer userId){
        try {
            Integer mechanismId = UserThreadLocal.get() .getId();
            if(!collegeService.isUserBelongToMechanism(mechanismId,userId)){
                return Result.fail("当前用户不属于该机构");
            }
            return collegeService.deactivateUserById(userId);
        } catch (Exception e) {
            log.error("{}",ExceptionUtils.getStackTrace(e));
        }
        return Result.fail();
    }

    /**
     * 批量停用用户
     * @return
     */
    @LoginRequired(platform= PlatformMarkEnum.MECHANISM)
    @ApiOperation(value = "批量停用用户",notes = "参数示例：[1,2,3,4]")
    @PostMapping(value = "batch-deactivate-user")
    public Result batchDeactivateUserByIdList(@RequestBody List<Integer> userIdList){
        Result result = collegeService.batchDeactivateUserByIdList(userIdList);
        return result;
    }

    /**
     * 批量/单个删除用户
     * @return
     */
    @LoginRequired(platform= PlatformMarkEnum.MECHANISM)
    @ApiOperation(value = "批量/单个删除用户",notes = "参数示例：[1,2,3,4]")
    @PostMapping(value = "batch-delete-user")
    public Result batchDeleteUserByIdList(@RequestBody List<Integer> userIdList){
        Result result = collegeService.batchDeleteUserByIdList(userIdList);
        return result;
    }

    /**
     * 部门详情-获取部门下班级/教师/学生数量
     * @param nodeId
     * @return
     */
    @LoginRequired(platform= PlatformMarkEnum.MECHANISM)
    @ApiOperation(value = "部门详情",notes = "获取部门下班级/教师/学生数量")
    @ApiImplicitParams({@ApiImplicitParam(name="nodeId",value="部门ID",required=true,paramType="path",example = "18")})
    @RequestMapping(value = "find-department-detail/{nodeId}",method = RequestMethod.POST)
    public Result  findDepartmentDetail(@PathVariable Integer nodeId){
        try {
            Integer mechanismId = UserThreadLocal.get() .getId();
            if(!collegeService.isBelongToMechanism(mechanismId,nodeId)){
                return Result.fail("当前节点不属于该机构");
            }
            return collegeService.findDepartmentDetail(nodeId);
        } catch (Exception e) {
            log.error("{}",ExceptionUtils.getStackTrace(e));
        }
        return Result.fail();
    }

    /**
     * 获取班级列表详情
     * @param classId
     * @return
     */
    @LoginRequired(platform= PlatformMarkEnum.MECHANISM)
    @ApiOperation(value = "班级详情",notes = "获取班级列表详情")
    @ApiImplicitParams({@ApiImplicitParam(name="classId",value="班级ID",required=true,paramType="path",example = "24")})
    @RequestMapping(value = "find-class-detail/{classId}",method = RequestMethod.POST)
    public Result  findClassDetail(@PathVariable Integer classId){
        try {
            Integer mechanismId = UserThreadLocal.get() .getId();
            if(!collegeService.isBelongToMechanism(mechanismId,classId)){
                return Result.fail("当前节点不属于该机构");
            }
            return collegeService.findClassDetail(classId);
        } catch (Exception e) {
            log.error("{}",ExceptionUtils.getStackTrace(e));
        }
        return Result.fail();
    }

    @LoginRequired(platform= PlatformMarkEnum.MECHANISM)
    @ApiOperation(value = "导出班级学生列表", notes = "导出班级学生列表")
    @PostMapping(value = "down-student-list/{classId}")
    public void downStudentList (@PathVariable Integer classId, HttpServletResponse response){
        Integer mechanismId = UserThreadLocal.get() .getId();
        if(!collegeService.isBelongToMechanism(mechanismId,classId)){
            return;
        }
        try {
            EasySheet sheet =new EasySheet();
            sheet.setFileName(classId+"-班级学生列表");
            sheet.setSheetName("学生列表");
            sheet.setHeaders(new String[]{"ID","用户名", "姓名","学号/工号","手机号"});
            List<UserDO> list =  collegeService.findUserByClassIdAndPositionType(classId, ClassPositionEnum.STUDENT.getType());
            sheet.setDataList(list);
            List<EasySheet> sheetList =  Lists.newArrayList();
            sheetList.add(sheet);
            EasyExcelUtils.exportExcel(sheetList,response);
        }catch (Exception e){
            log.error("excel下载失败:{}", ExceptionUtils.getStackTrace(e));
        }
    }

    @GetMapping(value = "/easy/download")
    public void download(HttpServletResponse response) {

    }

    /**
     * 选择用户绑定到班级
     * @return
     */
    @LoginRequired(platform= PlatformMarkEnum.MECHANISM)
    @ApiOperation(value = "选择用户绑定到班级",notes = "选择用户绑定到班级")
    @RequestMapping(value = "select-user-bind-college",method = RequestMethod.POST)
    public Result selectUserBindCollege(@RequestBody SelectUserBindDTO selectUserBindDTO){
        try {
            Integer mechanismId = UserThreadLocal.get() .getId();
            if(!collegeService.isBelongToMechanism(mechanismId,selectUserBindDTO.getCollegeId())){
                return Result.fail("当前节点不属于该机构");
            }
            return collegeService.selectUserBindCollege(selectUserBindDTO);
        } catch (Exception e) {
            log.error("{}",ExceptionUtils.getStackTrace(e));
        }
        return Result.fail();
    }

    /**
     * 班级-单个用户移除
     * @return
     */
    @LoginRequired(platform= PlatformMarkEnum.MECHANISM)
    @ApiOperation(value = "班级-单个用户移除",notes = "班级-单个用户移除")
    @ApiImplicitParams({@ApiImplicitParam(name="userId",value="用户ID",required=true,paramType="path",example = "5"),
            @ApiImplicitParam(name="collegeId",value="班级ID",required=true,paramType="path",example = "28")})
    @RequestMapping(value = "detach-user-college/{userId}/{collegeId}",method = RequestMethod.POST)
    public Result  detachUserCollege(@PathVariable Integer userId, @PathVariable Integer collegeId){
        try {
            Integer mechanismId = UserThreadLocal.get() .getId();
            if(!collegeService.isBelongToMechanism(mechanismId,collegeId)){
                return Result.fail("当前节点不属于该机构");
            }
            return collegeService.detachUserCollege(userId,collegeId);
        } catch (Exception e) {
            log.error("{}",ExceptionUtils.getStackTrace(e));
        }
        return Result.fail();
    }
    /**
     * 班级-批量移除用户
     * @return
     */
    @LoginRequired(platform= PlatformMarkEnum.MECHANISM)
    @ApiOperation(value = "班级-批量移除用户",notes = "批量移除用户")
    @RequestMapping(value = "bulk-detach-user-college",method = RequestMethod.POST)
    public Result bulkDetachUserCollege(@RequestBody SelectUserBindDTO selectUserBindDTO){
        try {
            Integer mechanismId = UserThreadLocal.get() .getId();
            if(!collegeService.isBelongToMechanism(mechanismId,selectUserBindDTO.getCollegeId())){
                return Result.fail("当前节点不属于该机构");
            }
            return collegeService.bulkDetachUserCollege(selectUserBindDTO);
        } catch (Exception e) {
            log.error("{}",ExceptionUtils.getStackTrace(e));
        }
        return Result.fail();
    }
    /**
     * 班级-学生批量调班
     * @return
     */
    @LoginRequired(platform= PlatformMarkEnum.MECHANISM)
    @ApiOperation(value = "班级-学生批量调班",notes = "班级-学生批量调班")
    @RequestMapping(value = "bulk-change-college",method = RequestMethod.POST)
    public Result bulkChangeCollege(@RequestBody SelectUserBindDTO selectUserBindDTO){
        try {
            Integer mechanismId = UserThreadLocal.get() .getId();
            if(!collegeService.isBelongToMechanism(mechanismId,selectUserBindDTO.getCollegeId())){
                return Result.fail("当前班级不属于该机构");
            }
            if(!collegeService.isBelongToMechanism(mechanismId,selectUserBindDTO.getNewCollegeId())){
                return Result.fail("当前新班级不属于该机构");
            }
            return collegeService.bulkChangeCollege(selectUserBindDTO);
        } catch (Exception e) {
            log.error("{}",ExceptionUtils.getStackTrace(e));
        }
        return Result.fail();
    }


}
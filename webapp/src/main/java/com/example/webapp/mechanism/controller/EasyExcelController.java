package com.example.webapp.mechanism.controller;

import com.alibaba.excel.EasyExcel;
import com.example.webapp.DO.UserDO;
import com.example.webapp.DTO.UserDto;
import com.example.webapp.Service.excel.NoModleDataListener;
import com.example.webapp.Service.excel.UserDataListener;
import com.example.webapp.annotation.LoginRequired;
import com.example.webapp.annotation.RepeatableCommit;
import com.example.webapp.common.Constant;
import com.example.webapp.enums.PlatformMarkEnum;
import com.example.webapp.enums.UserRoleEnum;
import com.example.webapp.result.Result;
import com.example.webapp.utils.ListOperateDTO;
import com.example.webapp.utils.ListUtil;
import com.example.webapp.utils.UserThreadLocal;
import com.example.webapp.utils.excel.TableBeanUtils;
import com.example.webapp.utils.excel.TableDOEnum;
import com.example.webapp.utils.excel.TableDTO;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Api(tags = "X9-通用EasyExcel导入接口")
@CrossOrigin
@RestController
@Slf4j
@LoginRequired(platform= PlatformMarkEnum.MECHANISM)
@RequestMapping("/api/mech/excel")
public class EasyExcelController {
    private Integer startReadRowNum = 1;

    @ApiOperation(value = "批量上传-添加学生")
    @ApiImplicitParams({@ApiImplicitParam(name="collegeId",value="collegeId大于0时，学生则绑定到班级",required=true)})
    @ResponseBody
    @RequestMapping(value = "bulk-upload-student",method = RequestMethod.POST)
    public Result bulkUploadStudent(MultipartFile file, Integer collegeId){
        try {
            UserDto mechanism = UserThreadLocal.get();
            //读取并插入excel数据  headRowNumber(n)n代表从第n+1行开始读
            EasyExcel.read(file.getInputStream(), UserDO.class,
                            new UserDataListener(mechanism.getId(),collegeId, UserRoleEnum.STUDENT.getType()))
                    .headRowNumber(startReadRowNum).ignoreEmptyRow(true).autoCloseStream(true).sheet().doRead();
        } catch (Exception e) {
            log.error("{}", ExceptionUtils.getStackTrace(e));
        }
        return Result.fail();
    }

    @ApiOperation(value = "批量上传-添加老师")
    @RequestMapping(value = "bulk-upload-teacher",method = RequestMethod.POST)
    public Result bulkUploadTeacher(MultipartFile file){
        try {
            UserDto mechanism = UserThreadLocal.get();
            //读取并插入excel数据  headRowNumber(n)n代表从第n+1行开始读
            EasyExcel.read(file.getInputStream(), UserDO.class,
                            new UserDataListener(mechanism.getId(),0,UserRoleEnum.TEACHER.getType()))
                    .headRowNumber(startReadRowNum).ignoreEmptyRow(true).autoCloseStream(true).sheet().doRead();
        } catch (Exception e) {
            log.error("{}",ExceptionUtils.getStackTrace(e));
        }
        return Result.fail();
    }

    @ApiOperation(value = "获取表集合", notes = "获取表集合")
    @PostMapping("/get-table-list")
    private Result<List<Map<String,String>>> getTableList(){
        List<Integer> newRefIdList = Lists.newArrayList();
        newRefIdList.add(1);
        newRefIdList.add(2);
        newRefIdList.add(3);
        List<Integer> existedRefIdList = Lists.newArrayList();
        existedRefIdList.add(3);
        existedRefIdList.add(4);
        existedRefIdList.add(5);
        ListOperateDTO lo = ListUtil.getListOperateDTO(newRefIdList,existedRefIdList);
        log.info("{}",new Gson().toJson(lo));
        return Result.ok(TableDOEnum.getEnumList());
    }

    @ApiOperation(value = "获取表结构", notes = "获取表结构")
    @ApiImplicitParams({@ApiImplicitParam(name="name",value="取值 TableDOEnum.name",required=true)})
    @PostMapping("/get-table-structure")
    private Result<TableDTO> getTableStructure(@RequestParam(value = "name",required = true) String name){
        return Result.ok(TableBeanUtils.getTableStructure(name));
    }

    /**
     *
     * @param clazz   数据库实体类路径 cn.piesat.supervision.entity.BaseInfoDO
     * @param relation 实体类属性 - excel列下标
     *                  [{"attribute":"baseId","index":0},{"attribute":"status","index":5},
     *                   {"attribute":"实体类属性名","index-excel列索引":6}]
     * @param file
     * @return
     */
    @RepeatableCommit(timeout = 5)
    @ApiOperation(value = "通用上传", notes = "通用上传")
    @ApiImplicitParams({@ApiImplicitParam(name="clazz",value="数据库实体类路径 com.zhihuiedu.business.entity.UserDO",required=true,
            example = "com.zhihuiedu.business.entity.UserDO"),
            @ApiImplicitParam(name="relation",value="[{\"attribute\":\"属性名\",\"excel列索引\":6}]",required=true,
                    example = "[{\"attribute\":\"userName\",\"index\":0},{\"attribute\":\"phone\",\"index\":1}]")})
    @PostMapping("/universal-upload")
    @ResponseBody
    public Result<String> uploadExcel(@RequestParam(value = "clazz") String clazz,
                                      @RequestParam(value = "relation") String relation,
                                      @RequestParam(value = "file") MultipartFile file) {
        try {
            Map<String,String> map = Maps.newConcurrentMap();
            map.put(Constant.CLAZZ,clazz);
            map.put(Constant.RELATION,relation);
            //读取并插入excel数据  headRowNumber(n)n代表从第n+1行开始读
            EasyExcel.read(file.getInputStream(), new NoModleDataListener(map))
                    .headRowNumber(startReadRowNum).ignoreEmptyRow(true).autoCloseStream(true)
                    .sheet().doRead();
        } catch (IOException e) {
            log.error("{}",ExceptionUtils.getStackTrace(e));
        }
        return Result.ok("ok");
    }

}
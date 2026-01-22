package com.example.webapp.controller;

import com.zhihuiedu.business.dto.CategoryDTO;
import com.zhihuiedu.business.dto.CategoryObjNumParam;
import com.zhihuiedu.business.dto.CreateNodeDTO;
import com.zhihuiedu.business.service.category.CategoryService;
import com.zhihuiedu.framework.annotation.LoginRequired;
import com.zhihuiedu.framework.enums.PlatformMarkEnum;
import com.zhihuiedu.framework.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

/** 分类表
 * @author ghs 
 */
@Api(tags = {"1006-分类维护"})
@RefreshScope
@RestController
@Component
@Slf4j
@LoginRequired(platform= PlatformMarkEnum.ENTERPRISE)
@RequestMapping("/api/bms/category")
public class CategoryController {
    /**
     * 内容分类
     */
    @Value("${portal.category.course.content.id}")
    private Integer courseContentId;
    /**
     * 年龄分类
     */
    @Value("${portal.category.course.age.id}")
    private Integer courseAgeId;
    /**
     * 试题分类
     */
    @Value("${portal.category.question.type.id}")
    private Integer questionTypeId;

    /**
     * 活动类型
     */
    @Value("${portal.category.activity.type.id}")
    private Integer activityTypeId;
    /**
     * 活动形式
     */
    @Value("${portal.category.activity.form.id}")
    private Integer activityFormid;
    /**
     * 新闻资讯
     */
    @Value("${portal.category.news.type.id}")
    private Integer newsTypeId;
    /**
     * 新闻资讯来源
     */
    @Value("${portal.category.news.source.id}")
    private Integer newsSourceId;

    @Autowired
    CategoryService categoryService;


    @ApiOperation(value = "年龄分类",notes = "年龄分类")
    @PostMapping(value = "get-age-list")
    public Result  getAgeList(){
        try {
            return categoryService.findChildNodeByParentId(courseAgeId);
        } catch (Exception e) {
            log.error("{}",ExceptionUtils.getStackTrace(e));
        }
        return Result.fail();
    }

    @ApiOperation(value = "内容分类",notes = "内容分类")
    @PostMapping(value = "get-content-list")
    public Result  getContentList(){
        try {
            return categoryService.findChildNodeByParentId(courseContentId);
        } catch (Exception e) {
            log.error("{}",ExceptionUtils.getStackTrace(e));
        }
        return Result.fail();
    }

    @ApiOperation(value = "试题分类",notes = "试题分类")
    @PostMapping(value = "get-question-list")
    public Result  getQuestionList(){
        try {
            return categoryService.findChildNodeByParentId(questionTypeId);
        } catch (Exception e) {
            log.error("{}",ExceptionUtils.getStackTrace(e));
        }
        return Result.fail();
    }

    @ApiOperation(value = "活动类型",notes = "活动类型")
    @PostMapping(value = "get-activity-list")
    public Result  getActivityList(){
        try {
            return categoryService.findChildNodeByParentId(activityTypeId);
        } catch (Exception e) {
            log.error("{}",ExceptionUtils.getStackTrace(e));
        }
        return Result.fail();
    }

   @ApiOperation(value = "活的形式",notes = "活的形式")
    @PostMapping(value = "get-activity-form-list")
    public Result  getActivityFormList(){
        try {
            return categoryService.findChildNodeByParentId(activityFormid);
        } catch (Exception e) {
            log.error("{}",ExceptionUtils.getStackTrace(e));
        }
        return Result.fail();
    }

    @ApiOperation(value = "新闻资讯",notes = "新闻资讯")
    @PostMapping(value = "get-news-type-list")
    public Result  getNewsTypeList(){
        try {
            return categoryService.findChildNodeByParentId(newsTypeId);
        } catch (Exception e) {
            log.error("{}",ExceptionUtils.getStackTrace(e));
        }
        return Result.fail();
    }

    @ApiOperation(value = "新闻资讯来源",notes = "新闻资讯来源")
    @PostMapping(value = "get-news-source-list")
    public Result  getNewsSourceList(){
        try {
            return categoryService.findChildNodeByParentId(newsSourceId);
        } catch (Exception e) {
            log.error("{}",ExceptionUtils.getStackTrace(e));
        }
        return Result.fail();
    }

    @ApiOperation(value = "创建一个新分类树",notes = "创建一个新分类树")
    @ApiImplicitParams({@ApiImplicitParam(name="treeName",value="分类树名",required=true)})
    @PostMapping(value = "create-category-tree")
    public Result createCategorytree(@RequestParam("treeName") String treeName){
        return categoryService.createCategorytree(treeName);
    }

    @ApiOperation(value = "创建分类节点（不能创建树）",notes = "支持最多同时创建20个分类")
    @PostMapping(value = "create-category-node")
    public Result createCategoryNode(@RequestBody CreateNodeDTO createNodeDTO){
        return categoryService.createCategoryNode(createNodeDTO.getParentId(),createNodeDTO.getNodeType(),createNodeDTO.getNodeNameList());
    }

    @ApiOperation(value = "修改分类节点名称",notes = "包含本节点和子孙节点的name全路径的修改")
    @ApiImplicitParams({@ApiImplicitParam(name="nodeId",value="节点ID",required=true,paramType="query"),
            @ApiImplicitParam(name="name",value="新节点名称",required=true,paramType="query")})
    @PostMapping(value = "update-category-name")
    public Result updateCategoryName(@RequestParam("nodeId")Integer nodeId,@RequestParam("name")String name){
        try {
            return categoryService.updateCategoryName(nodeId,name);
        } catch (Exception e) {
            log.error("{}", ExceptionUtils.getStackTrace(e));
        }
        return Result.fail();
    }

    @ApiOperation(value = "详情", notes = "详情")
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "id", required = true, example = "0")})
    @RequestMapping(value = "/view/{id}", method = RequestMethod.POST)
    public Result<CategoryDTO> view(@PathVariable int id) {
        return categoryService.view(id);
    }

    @ApiOperation(value = "查询所有分类树",notes = "查询所有分类树")
    @PostMapping(value = "find-category-tree")
    public Result findcategoryTree(){
        try {
            return categoryService.findcategoryTree();
        } catch (Exception e) {
            log.error("{}",ExceptionUtils.getStackTrace(e));
        }
        return Result.fail();
    }


    @ApiOperation(value = "查询所有子孙节点",notes = "查询所有子孙节点")
    @ApiImplicitParams({@ApiImplicitParam(name="nodeId",value="节点ID",required=true,paramType="path")})
    @PostMapping(value = "find-offspring-node/{nodeId}")
    public Result findOffspringNodeByParentId(@PathVariable Integer nodeId){
        try {
            return categoryService.findOffspringNodeByParentId(nodeId);
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
            return categoryService.findChildNodeByParentId(nodeId);
        } catch (Exception e) {
            log.error("{}",ExceptionUtils.getStackTrace(e));
        }
        return Result.fail();
    }


    @ApiOperation(value = "删除分类节点",notes = "删除分类")
    @ApiImplicitParam(name="nodeId",value="节点ID",required=true,paramType="path")
    @PostMapping(value = "delete-node/{nodeId}")
    public Result  deleteNode(@PathVariable Integer nodeId){
        try {
            return categoryService.deleteNode(nodeId);
        } catch (Exception e) {
            log.error("{}",ExceptionUtils.getStackTrace(e));
        }
        return Result.fail();
    }

    @ApiOperation(value = "分类节点-拖拽排序",notes = "偏移量：元素从 x 移动到 y 时, offset = y - x")
    @ApiImplicitParams({@ApiImplicitParam(name="nodeId",value="节点ID",required=true),
            @ApiImplicitParam(name="offset",value="元素从 x 移动到 y 时, offset = y - x",required=true)})
    @PostMapping(value = "move-node")
    public Result  moveNode(@RequestParam Integer nodeId,@RequestParam Integer offset){
        try {
            return categoryService.moveNode(nodeId,offset);
        } catch (Exception e) {
            log.error("{}",ExceptionUtils.getStackTrace(e));
        }
        return Result.fail();
    }

    @ApiOperation(value = "查询分类关系对象数量", notes = "查询分类关系对象数量")
    @PostMapping(value = "/get-category-obj-num")
    public Result getCategoryObjNum(@RequestBody CategoryObjNumParam param) {
        Result result =  categoryService.getCategoryObjNum(param);
        return result;
    }

    @ApiOperation(value = "机构已购买内容分类列表",notes = "机构已购买内容分类列表")
    @ApiImplicitParam(name="mid",value="机构ID",required=true,paramType="path")
    @PostMapping(value = "get-buy-content-list/{mid}")
    public Result  getBuyContentList(@PathVariable Integer mid){
        try {
            return categoryService.getBuyCategoryList(mid);
        } catch (Exception e) {
            log.error("{}",ExceptionUtils.getStackTrace(e));
        }
        return Result.fail();
    }
}


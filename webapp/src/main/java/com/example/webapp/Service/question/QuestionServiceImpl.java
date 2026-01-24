package com.example.webapp.Service.question;

import com.example.webapp.DO.CategoryObjectRefDO;
import com.example.webapp.DO.QuestionDO;
import com.example.webapp.DO.QuestionLevelRefDO;
import com.example.webapp.DTO.ObjectCategoryDTO;
import com.example.webapp.DTO.QuestionDTO;
import com.example.webapp.DTO.QuestionOptionDTO;
import com.example.webapp.Mapper.category.CategoryMapper;
import com.example.webapp.Mapper.question.QuestionMapper;
import com.example.webapp.annotation.Cacheable;
import com.example.webapp.annotation.RepeatableCommit;
import com.example.webapp.query.QuestionQuery;
import com.example.webapp.Service.category.CategoryService;
import com.example.webapp.common.redis.RedisLock;
import com.example.webapp.common.redis.RedisUtils;
import com.example.webapp.enums.ObjectTypeEnum;
import com.example.webapp.enums.QuestionEditDelEnum;
import com.example.webapp.enums.QuestionTypeEnum;
import com.example.webapp.enums.UpDownStatusEnum;
import com.example.webapp.result.Result;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Mapper
public class QuestionServiceImpl implements QuestionService, Serializable {
    public static final int PAGE_SIZE = 20;
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private RedisLock redisLock;

    @Override
    public PageInfo findList(QuestionQuery query) {
        try {
            query.setPageNo(query.getPageNo()==null?0:query.getPageNo());
            query.setPageSize(query.getPageSize()==null? PAGE_SIZE :query.getPageSize());
            //使用分页插件,核心代码就这一行 #分页配置#
            Page page = PageHelper.startPage(query.getPageNo(), query.getPageSize());
            List<QuestionDTO> list = questionMapper.findList(query);
            if(CollectionUtils.isNotEmpty(list)){
                List<Integer> questionIdList = list.stream().map(QuestionDTO::getId).collect(Collectors.toList());
                Map<Integer, ObjectCategoryDTO> categoryMap = categoryService.getObjectCategoryMap(questionIdList, ObjectTypeEnum.QUESTION.getType());
                Map<Integer, ObjectCategoryDTO> levelMap = categoryService.getObjectLevelMap(questionIdList);
                list.forEach(q->{
                    q.setStatusCn(UpDownStatusEnum.getUpDownStatusName(q.getStatus()));
                    q.setTypeCn(QuestionTypeEnum.getTypeName(q.getType()));
                    if(levelMap.get(q.getId()) != null){
                        q.setLevelNameList(parseStrToStringList(levelMap.get(q.getId()).getLevelNames()));
                        q.setLevelList(parseStrToIntegerList(levelMap.get(q.getId()).getLevelIds()));
                    }
                    if(categoryMap.get(q.getId()) != null){
                        q.setIdFullPathList(parseStrToStringList(categoryMap.get(q.getId()).getIdFullPaths()));
                        q.setNameFullPathList(parseStrToStringList(categoryMap.get(q.getId()).getNameFullPaths()));
                        q.setCategoryIdList(getCategorIdListByIdFullPath(q.getIdFullPathList()));
                    }
                });
            }
            PageInfo pageInfo = new PageInfo(list);
            return pageInfo;
        }catch (Exception e){
            log.error("{}", ExceptionUtils.getStackTrace(e));
            e.printStackTrace();
        }
        return  null;
    }

    @Cacheable(prefix = "view",fieldKey = "#questionId")
    @Override
    public Result view(Integer questionId) {
        Result result;
        try{
            //题干
            QuestionDTO question = questionMapper.view(questionId);
            if(question==null){
                return Result.ok("题目不存在");
            }
            //难度
            List<Integer> questionIdList = Lists.newArrayList();
            questionIdList.add(questionId);
            Map<Integer, ObjectCategoryDTO> categoryMap = categoryService.getObjectCategoryMap(questionIdList,ObjectTypeEnum.QUESTION.getType());
            Map<Integer, ObjectCategoryDTO> levelMap = categoryService.getObjectLevelMap(questionIdList);
            if(levelMap.get(question.getId()) != null){
                question.setLevelNameList(parseStrToStringList(levelMap.get(question.getId()).getLevelNames()));
                question.setLevelList(parseStrToIntegerList(levelMap.get(question.getId()).getLevelIds()));
            }
            if(categoryMap.get(question.getId()) != null){
                question.setIdFullPathList(parseStrToStringList(categoryMap.get(question.getId()).getIdFullPaths()));
                question.setNameFullPathList(parseStrToStringList(categoryMap.get(question.getId()).getNameFullPaths()));
                question.setCategoryIdList(getCategorIdListByIdFullPath(question.getIdFullPathList()));
            }
            question.setTypeCn(QuestionTypeEnum.getTypeName(question.getType()));
            question.setStatusCn(UpDownStatusEnum.getUpDownStatusName(question.getStatus()));
            //选项
            List<QuestionOptionDTO> optionList = questionMapper.listOptionByQuestionId(questionId);
            question.setOptionDTOList(optionList);
            result = Result.ok(question);
        }catch (Exception e){
            String exceptionStr = ExceptionUtils.getStackTrace(e);
            log.error("{}",exceptionStr);
            result = Result.fail(exceptionStr);
        }
        return result;
    }


    @RepeatableCommit
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result insert(QuestionDO questionDO) {
        try{
            questionDO.setEditDel(QuestionEditDelEnum.NORMAL.getValue());
            questionMapper.insert(questionDO);
            //插入题目难度
            insertQuestionLevel(questionDO);
            //插入选项
            questionMapper.insertOption(questionDO.getId(),questionDO.getOptionList());
            //插入标签
            List<CategoryObjectRefDO> coRefList =categoryService.parseCategoryObjectRefList(questionDO.getId(),questionDO.getCategoryIdList(),ObjectTypeEnum.QUESTION.getType());
            categoryMapper.insertCategoryObjectRefList(coRefList);
            return Result.ok(questionDO.getId());
        }catch (Exception e){
            log.error("{}",ExceptionUtils.getStackTrace(e));
            throw e;
        }
    }

    /**
     * 插入难度
     * @param questionDO
     */
    private void insertQuestionLevel(QuestionDO questionDO) {
        List<Integer> levelList = questionDO.getLevelList();
        if (CollectionUtils.isEmpty(levelList)) {
            return ;
        }
        List<QuestionLevelRefDO> levelRefDOList = new ArrayList<>();
        levelList.forEach(level->{
            QuestionLevelRefDO ref = new QuestionLevelRefDO();
            ref.setQuestionId(questionDO.getId());
            ref.setLevel(level);
            levelRefDOList.add(ref);
        });
        questionMapper.insertLevel(questionDO.getId(),levelRefDOList);
    }

    @Cacheable(prefix = "view",fieldKey = "#questionDO.id",cacheOperation = Cacheable.CacheOperation.UPDATE)
    @RepeatableCommit
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result update(QuestionDO questionDO) {
        try {
            questionMapper.update(questionDO);
            questionMapper.deleteLevel(questionDO.getId());
            questionMapper.deleteOption(questionDO.getId());
            categoryMapper.deleteCategoryRefByObjectId(questionDO.getId(), ObjectTypeEnum.QUESTION.getType());
            //插入选项
            questionMapper.insertOption(questionDO.getId(), questionDO.getOptionList());
            //插入题目难度
            insertQuestionLevel(questionDO);
            //插入标签
            List<CategoryObjectRefDO> coRefList =categoryService.parseCategoryObjectRefList(questionDO.getId(),questionDO.getCategoryIdList(),ObjectTypeEnum.QUESTION.getType());
            categoryMapper.insertCategoryObjectRefList(coRefList);
            return Result.ok(questionDO.getId());
        } catch (Exception e) {
            String exceptionStr = ExceptionUtils.getStackTrace(e);
            log.error("{}", exceptionStr);
            return Result.fail(exceptionStr);
        }
    }

    @Cacheable(prefix = "view",fieldKey = "#id",cacheOperation = Cacheable.CacheOperation.UPDATE)
    @Override
    public Result delete(Integer id) {
        try {
            return Result.ok(questionMapper.deleteById(id));
        }catch (Exception e){
            String exceptionStr = ExceptionUtils.getStackTrace(e);
            log.error("{}", exceptionStr);
            return Result.fail(exceptionStr);
        }
    }


    @Cacheable(prefix = "view",fieldKey = "#questionId",cacheOperation = Cacheable.CacheOperation.UPDATE)
    @Override
    public Result updateStatus(Integer questionId, Integer status) {
        try {
            return Result.ok(questionMapper.updateStatus(questionId,status));
        }catch (Exception e){
            String exceptionStr = ExceptionUtils.getStackTrace(e);
            log.error("{}", exceptionStr);
            return Result.fail(exceptionStr);
        }
    }

    /**
     * 获取题目选项map
     * @param questionIds
     * @return key 题目ID
     */
    @Override
    public Map<Integer, List<QuestionOptionDTO>> getOptionListByQuestionIdList(List<Integer> questionIds) {
        List<QuestionOptionDTO> optionDTOList = questionMapper.getOptionListByQuestionIdList(questionIds);
        Map<Integer,List<QuestionOptionDTO>> map = Maps.newConcurrentMap();
        if(CollectionUtils.isNotEmpty(optionDTOList)){
            optionDTOList.stream().forEach(o->{
                List<QuestionOptionDTO> subList = map.get(o.getQuestionId());
                if(CollectionUtils.isEmpty(subList)){
                    subList= Lists.newArrayList();
                }
                subList.add(o);
                map.put(o.getQuestionId(),subList);
            });
        }
        return map;
    }

    /**
     * 获取末节节点ID :   .3.88.108.@试题分类.阅读理解.测试1
     * @param idFullPathList
     * @return
     */
    private List<Integer> getCategorIdListByIdFullPath(List<String> idFullPathList) {
        List<Integer> list = Lists.newArrayList();
        if(CollectionUtils.isNotEmpty(idFullPathList)){
            for (String idFull : idFullPathList) {
                if(idFull.indexOf("@")>=0){
                    idFull = idFull.split("@")[0];
                }
                String[] arr = idFull.split("\\.");
                list.add(Integer.parseInt(arr[arr.length-1]));
            }
        }
        return list;
    }

    private List<Integer> parseStrToIntegerList(String ids) {
        List<Integer> idList = Lists.newArrayList();
        if(StringUtils.isNotBlank(ids)){
            idList = Arrays.asList(ids.split(",")).stream().map(s -> Integer.parseInt(s.trim())).collect(Collectors.toList());
        }
        return idList;
    }

    private List<String> parseStrToStringList(String names) {
        List<String> nameList = Lists.newArrayList();
        if(StringUtils.isNotBlank(names)){
            nameList = Arrays.asList(names.split(","));
        }
        return nameList;
    }

}
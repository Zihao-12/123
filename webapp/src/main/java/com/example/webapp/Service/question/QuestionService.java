package com.example.webapp.Service.question;

import com.example.webapp.DO.QuestionDO;
import com.example.webapp.DTO.QuestionOptionDTO;
import com.example.webapp.Query.QuestionQuery;
import com.example.webapp.result.Result;
import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.Map;

public interface QuestionService {
    /**
     * 列表
     * @param query
     * @return
     */
    PageInfo findList(QuestionQuery query);

    /**
     * 删除
     * @param id
     * @return
     */
    Result delete(Integer id);

    /**
     * 插入
     * @param questionDO
     * @return
     */
    Result insert(QuestionDO questionDO);

    /**
     * 更新
     * @param questionDO
     * @return
     */
    Result update(QuestionDO questionDO);


    /**
     * 上下架
     * @param questionId
     * @param status
     * @return
     */
    Result updateStatus(Integer questionId, Integer status);

    /**
     * 获取题目选项map
     * @param questionIds
     * @return key 题目ID
     */
    Map<Integer, List<QuestionOptionDTO>> getOptionListByQuestionIdList(List<Integer> questionIds);

    /**
     * 获取题目
     * @param questionId
     * @return
     */
    Result view(Integer questionId);

}
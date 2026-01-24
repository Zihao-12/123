package com.example.webapp.Mapper.question;

import com.example.webapp.DO.QuestionDO;
import com.example.webapp.DO.QuestionLevelRefDO;
import com.example.webapp.DO.QuestionOptionDO;
import com.example.webapp.DTO.QuestionDTO;
import com.example.webapp.DTO.QuestionOptionDTO;
import com.example.webapp.query.QuestionQuery;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Mapper
public interface QuestionMapper {

    /**
     * 分页查询
     * @param query
     * @return
     */
    @SelectProvider(type= QuestionMapperDynaSqlProvider.class,method="findList")
    List<QuestionDTO> findList(QuestionQuery query);

    /**
     * 新建
     * @param questionDO
     * @return
     */
    @Insert(" INSERT INTO `question`(`topic`, `type`, `status`, `edit_del`, `is_delete`) " +
            " values(#{topic},#{type},#{status},#{editDel},0)")
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    Integer insert(QuestionDO questionDO);

    @Select(" select  q.id, q.`topic`,q.`type`,q.`status`,q.edit_del,q.create_time,q.is_delete " +
            "FROM question q " +
            "WHERE q.id =#{questionId} ")
    QuestionDTO view(@Param("questionId") Integer questionId);

    /**
     * 修改
     * @param questionDO
     * @return
     */
    @UpdateProvider(type = QuestionMapperDynaSqlProvider.class,method = "update")
    Integer update(QuestionDO questionDO);

    @Update("UPDATE question set is_delete=1 where id =#{id}")
    int deleteById(Integer id);

    /**
     * 插入题目难度
     */
    @InsertProvider(type = QuestionMapperDynaSqlProvider.class, method = "insertLevel")
    void insertLevel(@Param("questionId") Integer questionId, @Param("list") List<QuestionLevelRefDO> levelList);

    /**
     * 插入选项
     */
    @InsertProvider(type = QuestionMapperDynaSqlProvider.class, method = "insertOption")
    void insertOption(@Param("questionId") Integer questionId, @Param("list") List<QuestionOptionDO> optionList);

    /**
     * 删除选项
     */
    @Update("update question_option set is_delete=1 where question_id=#{questionId}")
    void deleteOption(@Param("questionId") Integer questionId);
    /**
     * 删除难度
     */
    @Update("update question_level_ref set is_delete=1 where question_id=#{questionId}")
    void deleteLevel(@Param("questionId") Integer questionId);

    @Update(" update question set status=#{status} where id=#{questionId} ")
    int updateStatus(@Param("questionId") Integer questionId, @Param("status") Integer status);

    /**
     * 查询题目选项
     * @param questionId
     * @return
     */
    @Select(" select * from question_option qo " +
            " where qo.is_delete=0 and qo.question_id=#{questionId} ")
    List<QuestionOptionDTO> listOptionByQuestionId(@Param("questionId") Integer questionId);


    /**
     * 批量查询 题目选项
     * @param questionIds
     * @return
     */
    @Select("<script>"
            + " SELECT id,question_id,option_content,`correct` FROM question_option WHERE is_delete =0 and question_id in "
            + "<foreach item='item' index='index' collection='questionIds' open='(' separator=',' close=')'>"
            + "#{item}"
            + "</foreach>"
            + "</script>")
    List<QuestionOptionDTO> getOptionListByQuestionIdList(@Param("questionIds") List<Integer> questionIds);

    @Select({" <script> ",
            " select DISTINCT q.id,q.topic,q.type, GROUP_CONCAT(distinct co.category_id ) categoryIds ",
            "  FROM question q   ",
            "  LEFT JOIN category_object_ref co on co.object_id = q.id and co.object_type =4 and co.is_delete=0  ",
            " WHERE q.is_delete =0 and q.`status` =1 and co.category_id in  ",
            " <foreach item='item' index='index' collection='categoryIdList' open='(' separator=',' close=')'> ",
            " #{item} ",
            " </foreach> ",
            " GROUP BY q.id,q.topic,q.type ",
            " </script>",
    })
    List<QuestionDTO> getQuestionListByCategoryIdList(@Param("categoryIdList") List<Integer> categoryIdList);

}
package com.example.webapp.Mapper.question;

import com.example.webapp.DO.QuestionDO;
import com.example.webapp.DO.QuestionLevelRefDO;
import com.example.webapp.DO.QuestionOptionDO;
import com.example.webapp.Query.QuestionQuery;
import com.example.webapp.enums.ObjectTypeEnum;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.jdbc.SQL;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

public class QuestionMapperDynaSqlProvider {

    public String findList(final QuestionQuery query){
        SQL sql = new SQL(){
            {
                SELECT(" distinct q.id,q.topic,q.type,q.status,q.edit_del,q.create_time ");
                FROM(" question q ");
                LEFT_OUTER_JOIN(" question_level_ref qlr on qlr.question_id=q.id and qlr.is_delete=0 ");
                LEFT_OUTER_JOIN(" category_object_ref cof on q.id = cof.object_id and cof.object_type ="+ ObjectTypeEnum.QUESTION.getType()
                        +" and cof.is_delete =0 ");
                WHERE(" q.is_delete=0 ");
                if(query.getType()!=null){
                    WHERE("q.type=#{type}");
                }
                if(query.getLevel()!=null){
                    WHERE("qlr.level=#{level}");
                }
                if(query.getStatus()!=null){
                    WHERE("q.status=#{status}");
                }
                if(CollectionUtils.isNotEmpty(query.getCategoryIdList())){
                    WHERE(" cof.category_id in ("+ StringUtils.join(query.getCategoryIdList(),",")+")");
                }
                if(StringUtils.isNotBlank(query.getTopic())){
                    if(StringUtils.isNotBlank(query.getTopic())){
                        query.setTopic("%"+query.getTopic()+"%");
                    }
                    WHERE("q.topic like #{topic}");
                }

                GROUP_BY(" q.id,q.topic,q.type,q.status,q.create_time ");
                ORDER_BY(" q.id desc ");
            }
        };
        return sql.toString();
    }


    /**
     * 修改对象
     * @param questionDO
     * @return
     */
    public String update(final QuestionDO questionDO){
        return new SQL(){
            {
                UPDATE("question");
                SET("topic=#{topic}");
                SET("status=#{status}");
                WHERE("id=#{id}");
            }
        }.toString();
    }

    /**
     * 插入难度
     * @param map
     * @return
     */
    public String insertLevel(Map map) {
        List<QuestionLevelRefDO> insertList = (List<QuestionLevelRefDO>) map.get("list");
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO `question_level_ref`(`question_id`, `level`, `is_delete`) ");
        sb.append(" VALUES ");
        MessageFormat mf = new MessageFormat("( #'{'questionId},#'{'list[{0}].level},0)");
        for (int i = 0; i < insertList.size(); i++) {
            sb.append(mf.format(new Object[]{i}));
            if (i < insertList.size() - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

    /**
     * 插入选项
     * @param map
     * @return
     */
    public String insertOption(Map map) {
        List<QuestionOptionDO> insertList = (List<QuestionOptionDO>) map.get("list");
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO `question_option` ");
        sb.append("(`question_id`, `option_content`, `correct`, `is_delete`)  ");
        sb.append(" VALUES ");
        MessageFormat mf = new MessageFormat("( #'{'questionId},#'{'list[{0}].optionContent},#'{'list[{0}].correct} ,0)");
        for (int i = 0; i < insertList.size(); i++) {
            sb.append(mf.format(new Object[]{i}));
            if (i < insertList.size() - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

}


package com.example.webapp.Mapper.json;

import com.example.webapp.query.CourseJsonQuery;
import org.apache.ibatis.jdbc.SQL;

public class JsonMapperDynaSqlProvider {

    /**
     * 课程查询- 列表不展示分类
     * @param query
     * @return
     */
    public String selectAll(final CourseJsonQuery query){
        SQL sql = new SQL(){
            {
                SELECT(" DISTINCT c.id,c.`name`,c.introduction,c.cover,c.detail,c.type,c.`status`,c.is_delete,c.update_time,c.create_time ,c.mechanism_id,c.content_category_id , " +
                        "GROUP_CONCAT(distinct ca.id ) categoryIds, " +
                        "GROUP_CONCAT(distinct ca.`name` ORDER BY ca.`name` DESC  SEPARATOR \"｜\") categoryNames ");
                FROM("course c ");
                LEFT_OUTER_JOIN(" category_object_ref cof on c.id = cof.object_id and cof.object_type =1 and cof.is_delete =0");
                LEFT_OUTER_JOIN(" category ca on ca.id = cof.category_id and ca.leaf_node=1 ");
                WHERE(" cof.category_id = #{categoryId}");
                WHERE(" c.status = 1");
                //课程类型不能空 1视频课 2签到视频
                WHERE(" c.type = 1");
                WHERE(" c.is_delete=0");
                GROUP_BY(" c.id,c.`name`,c.introduction,c.cover,c.detail,c.type,c.`status`,c.is_delete,c.update_time,c.create_time,c.mechanism_id ");
                ORDER_BY(" c.id desc");
            }
        };
        return sql.toString();
    }

}
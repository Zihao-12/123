package com.example.webapp.Mapper.course;

import com.example.webapp.DO.CourseDO;
import com.example.webapp.DO.CourseSectionDO;
import com.example.webapp.DO.MechanismCourseUnlockDO;
import com.example.webapp.DO.UserCategoryRefDO;
import com.example.webapp.enums.CourseListTypeEnum;
import com.example.webapp.enums.UpDownStatusEnum;
import com.example.webapp.query.CourseQuery;
import com.example.webapp.query.UserLearnRecordQuery;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.jdbc.SQL;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

public class CourseMapperDynaSqlProvider {

    /**
     * 课程查询- 列表不展示分类
     * @param query
     * @return
     */
    public String selectAll(final CourseQuery query){
        SQL sql = new SQL(){
            {
                SELECT(" DISTINCT c.id,c.`name`,c.introduction,c.cover,c.detail,c.type,c.`status`,c.is_delete,c.update_time,c.create_time ,c.mechanism_id,c.content_category_id , " +
                        "GROUP_CONCAT(distinct ca.id ) categoryIds, " +
                        "GROUP_CONCAT(distinct ca.`name` ORDER BY ca.`name` DESC  SEPARATOR \"｜\") categoryNames, " +
                        "GROUP_CONCAT(distinct ag.id) categoryAgeIds, " +
                        "GROUP_CONCAT(distinct ag.`name` ORDER BY ag.`name` ASC  SEPARATOR \"｜\") categoryAgeNames ");
                FROM("course c ");
                LEFT_OUTER_JOIN(" category_object_ref cof on c.id = cof.object_id and cof.object_type =1 and cof.is_delete =0");
                LEFT_OUTER_JOIN(" category ca on ca.id = cof.category_id and ca.leaf_node=1 ");
                LEFT_OUTER_JOIN(" category ag on ag.id = cof.category_age_id ");
                if(CourseListTypeEnum.YUN_YING.getType().equals(query.getCourseListType())){
                    //运营端课程
                    WHERE(" c.mechanism_id=0");
                }else if(CourseListTypeEnum.JIGOU_BUY.getType().equals(query.getCourseListType())){
                    //机构已购课程(含用户端)
                    LEFT_OUTER_JOIN(" course_package_ref cpr on cpr.course_id = c.id and cpr.is_delete=0 ");
                    WHERE(" cpr.course_package_id=#{coursePackageId}");
                }else if(CourseListTypeEnum.ZJ_JIAN.getType().equals(query.getCourseListType())){
                    //机构自建课(含用户端)
                    WHERE(" c.mechanism_id=#{mechanismId}");
                }else if(CourseListTypeEnum.FAVORITE.getType().equals(query.getCourseListType())){
                    //用户收藏列表
                    LEFT_OUTER_JOIN(" user_favorite_ref ufr on ufr.object_id = c.id and ufr.object_type =1 and ufr.is_delete=0 ");
                    WHERE(" ufr.user_id=#{userId}");
                }

                if(StringUtils.isNotBlank(query.getName())){
                    WHERE(" c.name like #{name}");
                }
                if(CollectionUtils.isNotEmpty(query.getCategoryIdList())){
                    WHERE(" cof.category_id in ("+StringUtils.join(query.getCategoryIdList(),",")+")");
                }
                if(CollectionUtils.isNotEmpty(query.getCategoryAgeIdList())){
                    WHERE(" cof.category_age_id in ("+StringUtils.join(query.getCategoryAgeIdList(),",")+") ");
                }
                if(query.getStatus()!=null){
                    WHERE(" c.status = #{status}");
                }
                //课程类型不能空 1视频课 2签到视频
                WHERE(" c.type = #{type}");
                WHERE(" c.is_delete=0");
                GROUP_BY(" c.id,c.`name`,c.introduction,c.cover,c.detail,c.type,c.`status`,c.is_delete,c.update_time,c.create_time,c.mechanism_id ");
                if(CourseListTypeEnum.JIGOU_BUY.getType().equals(query.getCourseListType())){
                    //用户&机构 随机排序
                    ORDER_BY(" c.random_sort desc");
                }else {
                    ORDER_BY(" c.id desc");
                }
            }
        };
        return sql.toString();
    }


    public String update(final CourseDO meCourseDO){
        return new SQL(){
            {
                UPDATE("course");
                SET("name=#{name},introduction=#{introduction},cover=#{cover},detail=#{detail},status=#{status},content_category_id=#{contentCategoryId}");
                WHERE("id=#{id}");
            }
        }.toString();
    }

    public String updateSection(final CourseSectionDO meCourseSectionDO){
        return new SQL(){
            {
                UPDATE("course_section");
                SET("`name` = #{name}");
                SET("`video` = #{video}");
                SET("`video_duration` = #{videoDuration}");
                SET("`courseware` = #{courseware}");
                SET("`courseware_name` = #{coursewareName}");
                if(meCourseSectionDO.getParentId()!=null){
                    SET("`parent_id` = #{parentId}");
                }
                if(meCourseSectionDO.getType()!=null){
                    SET("`type` = #{type}");
                }
                if(meCourseSectionDO.getSort()!=null){
                    SET("`sort` = #{sort}");
                }
                WHERE("`id` = #{id}");
            }
        }.toString();
    }

    /**
     * 批量保存机构课程解锁模式
     * @param map
     * @return
     */
    public String bachSaveMechanismCourseUnlockList(Map map) {
        List<MechanismCourseUnlockDO> insertList = (List<MechanismCourseUnlockDO>) map.get("list");
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO mechanism_course_unlock ");
        sb.append("(course_id ,mechanism_id,mode) ");
        sb.append("VALUES ");
        MessageFormat mf = new MessageFormat("( #'{'list[{0}].courseId},#'{'list[{0}].mechanismId},#'{'list[{0}].mode})");
        for (int i = 0; i < insertList.size(); i++) {
            sb.append(mf.format(new Object[]{i}));
            if (i < insertList.size() - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

    public String learnCourselist(final UserLearnRecordQuery query){
        SQL sql = new SQL(){
            {
                SELECT(" distinct c.*,ulr.progress ");
                FROM(" course_package_ref cpr ");
                INNER_JOIN(" course c on c.id=cpr.course_id and c.is_delete=0 ");
                INNER_JOIN(" st_user_learn_record ulr on ulr.course_id=c.id and ulr.user_id=#{userId} ");
                WHERE(" cpr.is_delete=0  and cpr.course_package_id=#{coursePackageId} and ulr.complete=#{complete}");
                WHERE(" c.status="+ UpDownStatusEnum.UP.getStatus());
                ORDER_BY(" ulr.update_time desc ");
            }
        };
        return sql.toString();
    }


    /**
     * 批量保存用户兴趣
     * @param map
     * @return
     */
    public String bachSaveUserCategoryRefList(Map map) {
        List<UserCategoryRefDO> insertList = (List<UserCategoryRefDO>) map.get("list");
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO user_category_ref ");
        sb.append("(category_id ,mechanism_id,user_id,course_id) ");
        sb.append("VALUES ");
        MessageFormat mf = new MessageFormat(
                "( #'{'list[{0}].categoryId},#'{'list[{0}].mechanismId},#'{'list[{0}].userId},#'{'list[{0}].courseId})");
        for (int i = 0; i < insertList.size(); i++) {
            sb.append(mf.format(new Object[]{i}));
            if (i < insertList.size() - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }
}

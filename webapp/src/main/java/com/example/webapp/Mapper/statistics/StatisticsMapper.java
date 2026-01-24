package com.example.webapp.Mapper.statistics;

import com.example.webapp.DO.*;
import com.example.webapp.DTO.InterestDTO;
import com.example.webapp.VO.FakeDataVO;
import com.example.webapp.query.CourseQuery;
import com.example.webapp.query.FakeDataQuery;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Mapper
public interface StatisticsMapper {

    /**
     * ignore 一个用户一天只记录一次
     * @param userLoginRecordDO
     * @return
     */
    @Insert(" insert ignore into st_user_login_record(mechanism_id,user_id,day,create_time,is_delete) values(#{mechanismId},#{userId},#{day},now(),0) ")
    int loginRecord(UserLoginRecordDO userLoginRecordDO);

    @Select(" select count(*) from st_user_login_record where is_delete=0 and user_id=#{userId} ")
    Integer countUserLoginByUserId(CourseQuery query);

    @Select(" select count(*) from st_user_learn_record where is_delete=0 and complete=#{completed} and mechanism_id=#{query.mechanismId} and user_id=#{query.userId}  ")
    int countCourseCompleteByUserId(@Param("query") CourseQuery query, @Param("completed") Integer completed);

    @Select(" select sum(duration) duration from st_user_learn_record_detail " +
            " where is_delete=0 and mechanism_id=#{query.mechanismId} and user_id=#{query.userId} and study_date=#{today} ")
    StUserLearnRecordDetailDO countUserLearnRecordTodayByUserId(@Param("query") CourseQuery query, @Param("today") Date today);

    @Select(" select sum(duration) duration  from st_user_learn_record " +
            " where is_delete=0 and mechanism_id=#{mechanismId} and user_id=#{userId}  ")
    StUserLearnRecordDetailDO countUserLearnRecordByUserId(CourseQuery query);

    @Select({
            "<script> ",
            " select * from st_user_learn_record  ",
            "<where> ",
            " mechanism_id=#{mechanismId} and user_id=#{userId} and is_delete=0 and course_id in ",
            "<foreach collection='courseIdList' index='index' item='item' open='(' separator=',' close=')'> " ,
            "#{item} ",
            "</foreach>",
            "</where> ",
            "</script>"
    })
    List<StUserLearnRecordDO> listUserLearnRecord(@Param("mechanismId") Integer mechanismId, @Param("userId") Integer userId,
                                                  @Param("courseIdList") List<Integer> courseIdList);

    /**
     * 兴趣分类只查一级分类（暂时排除叶子节点）
     * @param userId
     * @return
     */
    @Select(" SELECT ucr.category_id categoryId,c.`name` categoryName,count(1) number " +
            " from  category c " +
            " LEFT JOIN user_category_ref ucr  on c.id = ucr.category_id and ucr.is_delete =0 and c.`leaf_node` <>1 " +
            " WHERE user_id =#{userId} " +
            " GROUP BY ucr.category_id ,c.`name`")
    List<InterestDTO> getInterestListOfUser(Integer userId);


    @Insert(" INSERT INTO `st_user_log_record`(`ip`,`user_id`,`mechanism_id`,`course_id`,`type`,`date`) " +
            " VALUES (#{ip},#{userId},#{mechanismId},#{courseId},#{type},#{date})")
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    int saveLogRecordDo(StUserLogRecordDO stUserLogRecordDO);

    @Select("SELECT  mechanism_id,user_id,type,date,count(1) num " +
            "FROM st_user_log_record WHERE date = #{dataDate} " +
            "GROUP BY  mechanism_id,user_id,type,date")
    List<StUserFakeDataDO> getUserLogRecordByDate( @Param("dataDate") Date dataDate);

    @Select("SELECT  mechanism_id,user_id,study_date date,SUM(duration) num   " +
            "  FROM st_user_learn_record_detail WHERE study_date = #{dataDate} " +
            "  GROUP BY  mechanism_id,user_id,study_date")
    List<StUserFakeDataDO> getUserLearnRecordDetailByDate(Date dataDate);


    @Select(" SELECT DISTINCT t.mechanism_id,t.date,t.num,t.realNum,co.category_id,c.`name` categoryName,6 type,0 userId " +
            " FROM (SELECT  mechanism_id,course_id,study_date date,SUM(duration) num ,SUM(duration)  realNum   " +
            "      FROM st_user_learn_record_detail WHERE study_date =  #{dataDate}  " +
            "      GROUP BY  mechanism_id,course_id,study_date ) t " +
            " LEFT JOIN category_object_ref  co on co.object_id = t. course_id and co.object_type =1 and co.is_delete=0 " +
            " LEFT JOIN category c on c.id = co.category_id"+
            " WHERE  c.`parent_id` =1")
    List<StUserFakeDataDO> getCategoryUserLearnRecordDetailByDate(Date dataDate);

    @Select(" SELECT t.mechanism_id,t.date,t.num,t.realNum,co.category_age_id categoryId,c.`name` categoryName, 7 type,0 userId " +
            " FROM (SELECT  mechanism_id,course_id,study_date date,SUM(duration) num ,SUM(duration)  realNum    " +
            "      FROM st_user_learn_record_detail WHERE study_date =   #{dataDate}   " +
            "      GROUP BY  mechanism_id,course_id,study_date ) t " +
            " LEFT JOIN category_object_ref  co on co.object_id = t. course_id and co.object_type =1 and co.is_delete=0 " +
            " LEFT JOIN category c on c.id = co.category_age_id")
    List<StUserFakeDataDO> getAgeUserLearnRecordDetailByDate(Date dataDate);

    /**
     *
     * @param mechanismIdList
     * @return
     */
    @MapKey("mechanismId")
    @Select("<script>"
            + " SELECT * " +
            "   from mechanism_fake_set WHERE is_delete =0  and mechanism_id in  "
            + "<foreach item='item' index='index' collection='mechanismIdList' open='(' separator=',' close=')'>"
            + "#{item}"
            + "</foreach>"
            + "</script>")
    Map<Integer, FakeDateSetDO> getFakesetMap(@Param("mechanismIdList") Set<Integer> mechanismIdList);

    /**
     * 保存机构联系人
     * @param fakeDataDOS
     * @return
     */
    @InsertProvider(type = StatisticMapperDynaSqlProvider.class, method = "insertUserFakeDataList")
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    Integer insertUserFakeDataList(List<StUserFakeDataDO> fakeDataDOS);

    @Select("<script>"+
            "  SELECT sum(num) v,`date` t ,sum(real_num) rv " +
            "   from st_user_fake_data " +
            " <where> "+
            "    <if test='mechanismId !=null'>and mechanism_id =#{mechanismId}</if>"+
            "    and date <![CDATA[ >= ]]>  #{beginTime} and date <![CDATA[ <= ]]> #{endTime}" +
            "    and type in  <foreach item='item' index='index' collection='typeList' open='(' separator=',' close=')'> #{item} </foreach>"+
            " </where>"+
            "  GROUP BY `date` "+
            "</script>")
    List<FakeDataVO> getFakeDataList(FakeDataQuery query);

    /**
     * 浏览人次 ： 浏览总理/13 + 人次
     * @param query
     * @return
     */
    @Select("<script>"+
            "  SELECT count(user_id) + FLOOR(sum(num)/13 )   v,`date` t ,count(user_id) rv " +
            "   from st_user_fake_data WHERE mechanism_id =#{mechanismId} " +
            "   and date <![CDATA[ >= ]]>  #{beginTime} and date <![CDATA[ <= ]]> #{endTime}" +
            "   and type in  <foreach item='item' index='index' collection='typeList' open='(' separator=',' close=')'> #{item} </foreach>"+
            "  GROUP BY `date` "+
            "</script>")
    List<FakeDataVO> getFakeDataUVList(FakeDataQuery query);


    @Select("<script>"+
            "  SELECT sum(num) v ,category_name c ,sum(real_num) rv" +
            "   from st_user_fake_data WHERE mechanism_id =#{mechanismId} " +
            "   and date <![CDATA[ >= ]]>  #{beginTime} and date <![CDATA[ <= ]]> #{endTime}" +
            "   and type in  <foreach item='item' index='index' collection='typeList' open='(' separator=',' close=')'> #{item} </foreach>"+
            "  GROUP BY category_name,`category_id` "+
            "</script>")
    List<FakeDataVO> getCategoryFakeDataList(FakeDataQuery query);

    @Select("<script>"+
            " SELECT count(1) from `user` " +
            " <where> "+
            "  <if test='mechanismId !=null'>and mechanism_id =#{mechanismId}</if>"+
            " </where>"+
            "</script>")
    Integer getRegCountByMid(FakeDataQuery query);
}
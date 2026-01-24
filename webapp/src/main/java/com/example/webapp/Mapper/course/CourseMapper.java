package com.example.webapp.Mapper.course;

import com.example.webapp.DO.*;
import com.example.webapp.DTO.CourseDTO;
import com.example.webapp.DTO.CoursePackageDTO;
import com.example.webapp.DTO.CourseSectionDTO;
import com.example.webapp.DTO.LearnCenterCourseDTO;
import com.example.webapp.VO.CheckinCourseVO;
import com.example.webapp.query.CourseQuery;
import com.example.webapp.query.UserLearnRecordQuery;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Mapper
public interface CourseMapper {


    @Results(id = "courseResultsMap",value = {
            @Result(id=true,property="id",column="id"),
            @Result(property="name",column="name")
    })
    @SelectProvider(type= CourseMapperDynaSqlProvider.class,method="selectAll")
    List<CourseDTO> selectAll(CourseQuery query);

    @Insert("INSERT INTO `course`( `name`, `introduction`, `cover`, `detail`, `type`, `status`, `is_delete`, `create_time`,mechanism_id,content_category_id,random_sort) " +
            " values(#{name},#{introduction},#{cover},#{detail},#{type},#{status},0,now(),#{mechanismId},#{contentCategoryId},floor(rand()*1000000+100) )")
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    int insert(CourseDO meCourseDO);

    @UpdateProvider(type = CourseMapperDynaSqlProvider.class,method = "update")
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    int update(CourseDO meCourseDO);

    @Update("update course set is_delete=1 where id=#{id}")
    int delete(int id);

    @Update("update course set status=#{status} where id=#{id}")
    int updateStatus(int id, int status);

    @Select("SELECT DISTINCT c.id,c.`name`,c.introduction,c.cover,c.detail,c.type,c.`status`,c.is_delete,c.update_time,c.create_time  ,c.mechanism_id, c.content_category_id," +
            "GROUP_CONCAT(distinct ca.id ) categoryIds, " +
            "GROUP_CONCAT(distinct ca.`name` ORDER BY ca.`name` desc  SEPARATOR \"｜\") categoryNames, " +
            "GROUP_CONCAT(distinct ag.id) categoryAgeIds, " +
            "GROUP_CONCAT(distinct ag.`name` ORDER BY ag.`name` ASC  SEPARATOR \"｜\") categoryAgeNames " +
            "FROM course c  " +
            "LEFT JOIN category_object_ref cof on c.id = cof.object_id and cof.object_type =1 and cof.is_delete =0 " +
            "LEFT JOIN category ca on ca.id = cof.category_id and ca.leaf_node=1 " +
            "LEFT JOIN category ag on ag.id = cof.category_age_id  " +
            " WHERE c.id=#{id} and c.is_delete=0")
    CourseDTO view(int id);

    @Insert("INSERT INTO course_section(`unique_code`, `course_id`, `name`, `video`, `video_duration`, `parent_id`, `type`, `courseware`,`courseware_name`, `sort`, `is_delete`, `create_time`)" +
            " VALUES(#{uniqueCode},#{courseId},#{name},#{video},#{videoDuration},#{parentId},#{type},#{courseware},#{coursewareName},#{sort},0,now())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertSection(CourseSectionDO section);

    @Select("select * from course_section where course_id=#{id} and is_delete=0 order by sort asc,id asc")
    List<CourseSectionDTO> viewSection(int id);

    @Update("update course_section set is_delete=1 where course_id=#{id}")
    int deleteSection(Integer id);

    @Select({
            "<script> ",
            "select cp.id,cp.name,cpr.course_id from course_package_ref cpr ",
            "INNER JOIN course_package cp on cp.id=cpr.course_package_id ",
            "<where> ",
            " cpr.is_delete=0 and cp.is_delete=0 and cpr.course_id in ",
            "<foreach collection='courseIdList' index='index' item='item' open='(' separator=',' close=')'> " ,
            "#{item} ",
            "</foreach>",
            "</where> ",
            "</script>"
    })
    List<CoursePackageDTO> selectPackageList(@Param("courseIdList") List<Integer> courseIdList);

    @Select(" select id from course_section where is_delete=0 and course_id=#{courseId} ")
    List<Integer> listSectionId(Integer courseId);

    @Update({
            "<script> ",
            " update course_section set is_delete=1 ",
            "<where> ",
            " is_delete=0 and id in ",
            "<foreach collection='sectionIdList' index='index' item='item' open='(' separator=',' close=')'> " ,
            "#{item} ",
            "</foreach>",
            "</where> ",
            "</script>"
    })
    int deleteSectionById(@Param("sectionIdList") List<Integer> sectionIdList);

    @UpdateProvider(type = CourseMapperDynaSqlProvider.class,method = "updateSection")
    int updateSection(CourseSectionDO meCourseSectionDO);

    @Select("select * from course_section where id=#{sectionId} and is_delete=0")
    CourseSectionDTO viewSectionById(int sectionId);

    @Select({
            "<script> ",
            " select cs.course_id id,count(*) courseSectionNumber from course_section cs ",
            "<where> ",
            " cs.is_delete=0 and cs.type=#{type} and cs.course_id in ",
            "<foreach collection='courseIdList' index='index' item='item' open='(' separator=',' close=')'> " ,
            "#{item} ",
            "</foreach>",
            "</where> ",
            " GROUP BY cs.course_id ",
            "</script>"
    })
    List<CourseDTO> listSectionNum(@Param("courseIdList") List<Integer> courseIdList, @Param("type") Integer type);

    @Select({
            "<script> ",
            " select * from course_section cs ",
            "<where> ",
            " cs.is_delete=0 and cs.id in ",
            "<foreach collection='sectionIdList' index='index' item='item' open='(' separator=',' close=')'> " ,
            "#{item} ",
            "</foreach>",
            "</where> ",
            "</script>"
    })
    List<CourseSectionDTO> listSectionBySectionId(@Param("sectionIdList") List<Integer> sectionIdList);

    @Insert("INSERT INTO `mechanism_course_unlock`( `course_id`, `mode`,mechanism_id) " +
            " values(#{courseId},#{mode},#{mechanismId})")
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    void insertMechanismCourseUnlock(MechanismCourseUnlockDO unlockDO);

    @Select(" select course_id FROM mechanism_course_unlock WHERE is_delete =0 and mechanism_id =#{mechanismId} ")
    List<Integer> getUnlockCourseIdByMechanismId(Integer mechanismId);

    @InsertProvider(type = CourseMapperDynaSqlProvider.class, method = "bachSaveMechanismCourseUnlockList")
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    Integer bachSaveMechanismCourseUnlockList(List<MechanismCourseUnlockDO> unlockDOList);

    @Update("update mechanism_course_unlock set mode=#{mode} where course_id=#{courseId} and mechanism_id =#{mechanismId}")
    int setCourseMode(Integer mechanismId, Integer courseId, Integer mode);

    @MapKey("courseId")
    @Select("<script>"
            + " SELECT * " +
            "   from mechanism_course_unlock WHERE mechanism_id =#{mechanismId} and is_delete =0  and course_id in  "
            + "<foreach item='item' index='index' collection='courseIdList' open='(' separator=',' close=')'>"
            + "#{item}"
            + "</foreach>"
            + "</script>")
    Map<Integer, MechanismCourseUnlockDO> getCourseModeMap(Integer mechanismId, List<Integer> courseIdList);

    @SelectProvider(type = CourseMapperDynaSqlProvider.class,method = "learnCourselist")
    List<LearnCenterCourseDTO> learnCourselist(UserLearnRecordQuery query);

    @MapKey("id")
    @Select("<script>" +
            "select course_id id,count(1) learnUsers  from st_user_learn_record where   course_id in("
            +"<foreach collection='courseIdList' separator=',' item='id'>"
            + "#{id} "
            + "</foreach> "
            +") " +
            " group by course_id </script>")
    Map<Integer, LearnCenterCourseDTO> getCourseLearnUsers(@Param("courseIdList") List<Integer> courseIdList);

    /**
     * 插入最后一次学习课节ID
     * @param stUserLearnLastIdDO
     * @return
     */
    @Insert("INSERT INTO `st_user_learn_last_id`(`mechanism_id`, `user_id`, `course_id`, `course_section_id`, `last_date`, `create_time`, `is_delete`) " +
            "VALUES (#{mechanismId}, #{userId}, #{courseId}, #{courseSectionId}, now(), now(), 0)")
    Integer insertLastCourseSectionId(StUserLearnLastIdDO stUserLearnLastIdDO);

    /**
     * 更新最后一次学习课节ID
     * @param stUserLearnLastIdDO
     * @return
     */
    @Update(" update st_user_learn_last_id set course_section_id=#{courseSectionId} " +
            " where is_delete=0 and mechanism_id=#{mechanismId} and user_id=#{userId} and course_id=#{courseId} ")
    int updateLastCourseSectionId(StUserLearnLastIdDO stUserLearnLastIdDO);
    /**
     * 查询已学习的章节ID
     * @param mechanismId
     * @param userId
     * @param sectionIdList
     * @param complete
     * @return
     */
    @Select({
            "<script> ",
            " select DISTINCT course_section_id from st_user_learn_record_detail  ",
            "<where> ",
            " is_delete=0 and mechanism_id=#{mechanismId} and user_id=#{userId} and complete=#{complete} and course_section_id in ",
            "<foreach collection='sectionIdList' index='index' item='item' open='(' separator=',' close=')'> " ,
            "#{item} ",
            "</foreach>",
            "</where> ",
            "</script>"
    })
    List<Integer> listLearnedCourseSectionId(@Param("mechanismId") Integer mechanismId, @Param("userId") Integer userId, @Param("sectionIdList") List<Integer> sectionIdList,@Param("complete") Integer complete);

    /**
     * 插入听课记录主表
     * @param userRecord
     * @return
     */
    @Insert(" INSERT INTO `st_user_learn_record`(`mechanism_id`, `user_id`, `course_id`, `duration`, `progress`, `complete`, `first_time`, `complete_time`, `create_time`, `is_delete`) " +
            " VALUES (#{mechanismId}, #{userId}, #{courseId}, #{duration}, #{progress},#{complete}, #{firstTime}, #{completeTime}, now(), 0) ")
    int insertLearnRecord(StUserLearnRecordDO userRecord);

    /**
     * 更新听课记录主表
     * @param userRecord
     */
    @Update(" UPDATE `st_user_learn_record` SET `duration` = #{duration}, `progress` = #{progress}, `complete` = #{complete}, `complete_time` = #{completeTime} WHERE `id` = #{id} ")
    void updateLearnRecord(StUserLearnRecordDO userRecord);

    /**
     * 插入听课明细
     */
    @Insert(" INSERT INTO `st_user_learn_record_detail`(`mechanism_id`, `user_id`, `course_id`, `course_section_id`, `duration`, `complete`, `study_date`, `create_time`, `is_delete`) " +
            "VALUES (#{mechanismId}, #{userId}, #{courseId}, #{courseSectionId}, #{duration}, #{complete}, #{studyDate}, now(), 0) ")
    int insertLearnRecordDetail(StUserLearnRecordDetailDO detail);

    /**
     * 更新听课明细
     */
    @Update(" update st_user_learn_record_detail set duration=#{duration},complete=#{complete} where id=#{id} ")
    int updateLearnRecordDetail(StUserLearnRecordDetailDO detail);

    /**
     * 课程章节数量
     * @param courseId
     * @param type
     * @return
     */
    @Select("select count(*) courseSectionNumber from course_section cs " +
            " where cs.is_delete=0 and cs.type=#{type} and cs.course_id=#{courseId}")
    int countCourseSectionNum(Integer courseId, Integer type);

    /**
     * 查询是否存在相同明细(是否已有听课明细,保证一天一节课只有一条听课记录)
     */
    @Select("select * from st_user_learn_record_detail ulrd " +
            " where ulrd.is_delete=0 " +
            " and ulrd.mechanism_id=#{mechanismId} " +
            " and ulrd.user_id=#{userId} " +
            " and ulrd.course_id=#{courseId} " +
            " and ulrd.course_section_id=#{courseSectionId} " +
            " and ulrd.study_date=#{studyDate} limit 1 ")
    StUserLearnRecordDetailDO selectLearnRecordDetail(StUserLearnRecordDetailDO detail);


    /**
     * 查询已完成的课节数(不含当前完成的课节)
     * @param detail
     * @return
     */
    @Select("select count(distinct ulrd.course_section_id)  from st_user_learn_record_detail ulrd " +
            " where ulrd.is_delete=0 " +
            " and ulrd.mechanism_id=#{detail.mechanismId} " +
            " and ulrd.user_id=#{detail.userId} " +
            " and ulrd.course_id=#{detail.courseId} " +
            " and ulrd.course_section_id<>#{detail.courseSectionId} " +
            " and complete=#{complete} ")
    Integer selectCompleteLearnRecordDetail(@Param("detail") StUserLearnRecordDetailDO detail, @Param("complete") int complete);

    /**
     * 已经学习,查询学习进度   查询听课记录主表
     */
    @Select(" select * from st_user_learn_record ulr where ulr.is_delete=0 and ulr.mechanism_id=#{mechanismId} and ulr.user_id=#{userId} and ulr.course_id=#{courseId} limit 1")
    StUserLearnRecordDO selectLearnRecord(Integer mechanismId, Integer userId, Integer courseId);

    /**
     * 课程已学习人数
     * @param courseId
     * @return
     */
    @Select(" select count(*) from st_user_learn_record where is_delete=0 and course_id=#{courseId} ")
    Integer getLearnedPersonNum(Integer courseId);

    /**
     * 获取用户最后一次听课
     * @param mechanismId
     * @param userId
     * @param courseId
     * @return
     */
    @Select(" select course_section_id from st_user_learn_last_id " +
            " where is_delete=0 and mechanism_id=#{mechanismId} and user_id=#{userId} and course_id=#{courseId} limit 1 ")
    Integer getLastCourseSectionId(Integer mechanismId, Integer userId, Integer courseId);

    /**
     * 返回课程列表中最后学习课节ID
     * @param mechanismId
     * @param userId
     * @param courseIdList
     * @return
     */
    @MapKey("id")
    @Select({
            "<script> ",
            " select course_id id,course_section_id lastCourseSectionId from st_user_learn_last_id  ",
            "<where> ",
            " mechanism_id=#{mechanismId} and user_id=#{userId} and is_delete=0 and course_id in ",
            "<foreach collection='courseIdList' index='index' item='item' open='(' separator=',' close=')'> " ,
            "#{item} ",
            "</foreach>",
            "</where> ",
            "</script>"
    })
    Map<Integer, CourseDTO> getUserLastCourseSectionIdMap(@Param("mechanismId")Integer mechanismId, @Param("userId") Integer userId,
                                                          @Param("courseIdList") List<Integer> courseIdList);

    /**
     * 课程已学习人数
     * @param courseIdList
     * @return
     */
    @MapKey("id")
    @Select({
            "<script>",
            " select course_id id,count(*) learnedPersonNum from st_user_learn_record ",
            " <where> ",
            " is_delete=0 and course_id in ",
            " <foreach collection='courseIdList' index='index' item='item' open='(' separator=',' close=')'> ",
            "#{item}",
            " </foreach> ",
            " </where> ",
            " group by course_id ",
            "</script>"
    })
    Map<Integer, CourseDTO> getLearnedPersonNumMap(@Param("courseIdList") List<Integer> courseIdList);

    /**
     * 用户课程学习记录
     * @param mechanismId
     * @param userId
     * @param courseIdList
     * @return
     */
    @MapKey("courseId")
    @Select({
            "<script>",
            " select ulr.mechanism_id,ulr.user_id,ulr.course_id,ulr.duration,ulr.progress,ulr.first_time,ulr.complete_time ",
            " from st_user_learn_record ulr ",
            " <where> ",
            " ulr.is_delete=0 and ulr.mechanism_id=#{mechanismId} and ulr.user_id=#{userId} and ulr.course_id in ",
            " <foreach collection='courseIdList' index='index' item='item' open='(' separator=',' close=')'> ",
            "#{item}",
            " </foreach> ",
            " </where> ",
            "</script>"
    })
    Map<Integer, StUserLearnRecordDO> getUserLearnRecordMap(@Param("mechanismId") Integer mechanismId, @Param("userId") Integer userId, @Param("courseIdList") List<Integer> courseIdList);

    /**
     * 查询用户课程分类
     * @param userId
     * @param courseId
     * @return
     */
    @Select("SELECT DISTINCT category_id from user_category_ref " +
            "WHERE user_id =#{userId} and course_id = #{courseId} and is_delete=0")
    List<Integer> getUserCategoryIdList(Integer userId,Integer courseId);

    @InsertProvider(type = CourseMapperDynaSqlProvider.class, method = "bachSaveUserCategoryRefList")
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    void bachSaveUserCategoryRefList(List<UserCategoryRefDO> refDOList);

    /**
     * 查询所以签到视频
     * @return
     */
    @Select("SELECT DISTINCT c.id courseId,c.`name`,c.introduction,c.cover,c.detail " +
            "FROM course c   " +
            "LEFT JOIN category_object_ref cor on cor.object_id = c.id and cor.object_type =1 and cor.is_delete =0 " +
            "WHERE c.type =2 and c.`status`=1 and cor.category_id =#{categoryId}  and c.`is_delete` =0 ")
    List<CheckinCourseVO> getAllCheckinCourse(Integer categoryId);

    /**
     * 签到视频详情
     * @param courseId
     * @return
     */
    @Select("select c.id courseId,c.`name`,c.introduction,c.cover,c.detail ,cs.video,cs.video_duration " +
            "from course c " +
            "LEFT JOIN course_section cs on cs.course_id =c.id and cs.is_delete =0 " +
            "WHERE c.id=#{courseId}")
    CheckinCourseVO checkinCourseView(Integer courseId);

    @Select({
            "<script>",
            " SELECT DISTINCT c.id,c.`name`,c.introduction,c.cover,c.detail,c.type,c.`status`,c.is_delete,c.update_time,c.create_time ,c.mechanism_id,c.content_category_id ,  ",
            "  GROUP_CONCAT(distinct ca.id ) categoryIds,   ",
            "  GROUP_CONCAT(distinct ca.`name` ORDER BY ca.`name` DESC  SEPARATOR '｜') categoryNames,   ",
            "  GROUP_CONCAT(distinct ag.id) categoryAgeIds,   ",
            "  GROUP_CONCAT(distinct ag.`name` ORDER BY ag.`name` ASC  SEPARATOR '｜') categoryAgeNames  ",
            " FROM course c   ",
            " LEFT JOIN  category_object_ref cof on c.id = cof.object_id and cof.object_type =1 and cof.is_delete =0  ",
            " LEFT JOIN  category ca on ca.id = cof.category_id and ca.leaf_node=1  ",
            " LEFT JOIN  category ag on ag.id = cof.category_age_id  ",
            " <where> ",
            " c.id in ",
            " <foreach collection='courseIdList' index='index' item='item' open='(' separator=',' close=')'> ",
            "#{item}",
            " </foreach> ",
            " </where> ",
            " GROUP BY c.id,c.`name`,c.introduction,c.cover,c.detail,c.type,c.`status`,c.is_delete,c.update_time,c.create_time,c.mechanism_id  ",
            "</script>"
    })
    List<CourseDTO> findCourseListByIdList(@Param("courseIdList") List<Integer> courseIdList);




    /**
     * ip纬度 查询已完成的课节数(不含当前完成的课节)
     * @param detail
     * @return
     */
    @Select("select count(distinct ulrd.course_section_id)  from st_user_learn_record_detail_ip ulrd " +
            " where ulrd.is_delete=0 " +
            " and ulrd.mechanism_id=#{detail.mechanismId} " +
            " and ulrd.ip=#{detail.ip} " +
            " and ulrd.course_id=#{detail.courseId} " +
            " and ulrd.course_section_id<>#{detail.courseSectionId} " +
            " and complete=#{complete} ")
    Integer selectCompleteLearnRecordDetailIP(@Param("detail") StUserLearnRecordDetailDOIP detail, @Param("complete") int complete);

    /**
     * ip纬度 已经学习,查询学习进度   查询听课记录主表
     */
    @Select(" select * from st_user_learn_record_ip ulr where ulr.is_delete=0 and ulr.mechanism_id=#{mechanismId} and ulr.ip=#{ip} and ulr.course_id=#{courseId} limit 1")
    StUserLearnRecordDOIP selectLearnRecordIP(Integer mechanismId, String ip, Integer courseId);

    /**
     * ip纬度 插入听课记录主表
     * @param userRecord
     * @return
     */
    @Insert(" INSERT INTO `st_user_learn_record_ip`(`mechanism_id`, `ip`, `course_id`, `duration`, `progress`, `complete`, `first_time`, `complete_time`, `create_time`, `is_delete`) " +
            " VALUES (#{mechanismId}, #{ip}, #{courseId}, #{duration}, #{progress},#{complete}, #{firstTime}, #{completeTime}, now(), 0) ")
    int insertLearnRecordIP(StUserLearnRecordDOIP userRecord);

    /**
     * ip纬度 更新听课记录主表
     * @param userRecord
     */
    @Update(" UPDATE `st_user_learn_record_ip` SET `duration` = #{duration}, `progress` = #{progress}, `complete` = #{complete}, `complete_time` = #{completeTime} WHERE `id` = #{id} ")
    void updateLearnRecordIP(StUserLearnRecordDOIP userRecord);
    /**
     * ip纬度插入听课明细
     */
    @Insert(" INSERT INTO `st_user_learn_record_detail_ip`(`mechanism_id`, `ip`, `course_id`, `course_section_id`, `duration`, `complete`, `study_date`, `create_time`, `is_delete`) " +
            "VALUES (#{mechanismId}, #{ip}, #{courseId}, #{courseSectionId}, #{duration}, #{complete}, #{studyDate}, now(), 0) ")
    int insertLearnRecordDetailIP(StUserLearnRecordDetailDOIP detail);

    /**
     * ip纬度 查询是否存在相同明细(是否已有听课明细,保证一天一节课只有一条听课记录)
     */
    @Select("select * from st_user_learn_record_detail_ip ulrd " +
            " where ulrd.is_delete=0 " +
            " and ulrd.mechanism_id=#{mechanismId} " +
            " and ulrd.ip=#{ip} " +
            " and ulrd.course_id=#{courseId} " +
            " and ulrd.course_section_id=#{courseSectionId} " +
            " and ulrd.study_date=#{studyDate} limit 1 ")
    StUserLearnRecordDetailDOIP selectLearnRecordDetailIP(StUserLearnRecordDetailDOIP detail);

    /**
     * ip纬度 更新听课明细
     */
    @Update(" update st_user_learn_record_detail_ip set duration=#{duration},complete=#{complete} where id=#{id} ")
    int updateLearnRecordDetailIP(StUserLearnRecordDetailDOIP detail);
}
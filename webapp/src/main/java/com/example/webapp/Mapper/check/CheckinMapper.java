package com.example.webapp.Mapper.check;

import com.example.webapp.DO.CheckinDO;
import com.example.webapp.DO.UserTakeCourseDO;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Mapper
public interface CheckinMapper {


    @Insert("insert into user_take_course (user_id ,course_id,complete,take_date) " +
            " values(#{userId},#{courseId},#{complete},#{takeDate})")
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    Integer insertUserTakeCourse(UserTakeCourseDO takeCourseDO);

    @Select("SELECT id,user_id,course_id,take_date,complete  " +
            "FROM user_take_course " +
            "WHERE is_delete =0 and  take_date = #{date} and user_id =#{userId}")
    UserTakeCourseDO getTakeCourse(Integer userId, Date date);

    @Update("update user_take_course set is_delete=1 where id=#{id}")
    int delUserTakeCourseById(Integer id);

    @Update("update user_take_course set complete=1 where id=#{id}")
    int updateUserTakeCourseComplete(Integer id);

    /**
     * 用户在 某天 某个对象上签到次数
     * @param userId
     * @param signDate
     * @param signType
     * @param objectId
     * @return
     */
    @Select("SELECT count(1) FROM checkin " +
            " WHERE is_delete =0 and user_id =#{userId} and sign_type =#{signType} " +
            " and sign_date= #{signDate} and object_id=#{objectId}  ")
    int isSignObjectToday(Integer userId, Date signDate, Integer signType, Integer objectId);

    /**
     * 用户在 在 某个对象上 总签到次数
     * @param userId
     * @param signType
     * @param objectId
     * @return
     */
    @Select("SELECT count(1) FROM checkin " +
            " WHERE is_delete =0 and user_id =#{userId} and sign_type =#{signType} " +
            " and object_id=#{objectId}  ")
    int isSignObjectAll(Integer userId, Integer signType, Integer objectId);

    /**
     * 用户签到次数
     * @param userId
     * @param signDate
     * @param signType
     * @return
     */
    @Select("SELECT count(1) FROM checkin " +
            " WHERE is_delete =0 and user_id =#{userId} and sign_type =#{signType} and sign_date= #{signDate}  ")
    int isSignToday(Integer userId, Date signDate, Integer signType);

    /**
     * 获取昨天  视频签到 记录
     * @param userId
     * @return
     */
    @Select(" select id,user_id,sign_type,sign_date,checkin_times,object_id,score " +
            " from checkin  " +
            " where sign_date = DATE_SUB(curdate(),INTERVAL 1 DAY)   " +
            " and sign_type = 0 and user_id=#{userId}")
    CheckinDO findSignOfYesterDay(Integer userId);

    @Insert("insert into checkin (user_id ,sign_type,sign_date,checkin_times,object_id,`score`) " +
            " values(#{userId},#{signType},#{signDate},#{checkinTimes},#{objectId},#{score})")
    int save(CheckinDO checkin);

    /**
     * 用户总积分（金豆）
     * @param userId
     * @return
     */
    @Select("select SUM(score) FROM checkin WHERE user_id =#{userId} and is_delete=0")
    Integer getUserCoins(Integer userId);

    /**
     * 累计签到天数
     * @param userId
     * @return
     */
    @Select("select count(1) FROM checkin WHERE user_id =#{userId} and is_delete=0 and sign_type =0")
    Integer getCumulativeTimes(Integer userId);
}

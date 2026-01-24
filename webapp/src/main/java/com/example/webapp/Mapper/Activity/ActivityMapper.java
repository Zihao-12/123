package com.example.webapp.Mapper.Activity;

import com.example.webapp.DO.*;
import com.example.webapp.DTO.*;
import com.example.webapp.query.ActivityQuery;
import com.example.webapp.query.ActivityRankingQuery;
import com.example.webapp.query.BindMechanismQuery;
import com.example.webapp.VO.ActivityWinLotteryUserVO;
import com.example.webapp.VO.HeroRankingVO;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@Mapper
public interface ActivityMapper {

    @SelectProvider(type= ActivityMapperDynaSqlProvider.class,method="selectAll")
    List<ActivityDTO> selectAll(ActivityQuery query);

    @Insert(" INSERT INTO `activity`(`name`,`introduction`,`cover`,`detail`,`type`,`age`,`shape`,`status`,`begin_time`,`end_time`) " +
            " VALUES (#{name},#{introduction},#{cover},#{detail},#{type},#{age},#{shape},#{status},#{beginTime},#{endTime})")
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    int insert(ActivityDO activityDO);


    @UpdateProvider(type = ActivityMapperDynaSqlProvider.class,method = "update")
    int update(ActivityDO ActivityDO);

    @Update("update activity set is_delete=1 where id=#{id}")
    int delete(Integer id);

    @Select("select a.`id`,a.`name`,a.`introduction`,a.`cover`,a.`detail`,a.`type`,a.`age`,a.`shape`,a.`status`,a.`begin_time`,a.`end_time`,a.`is_delete`,a.`update_time`,a.`create_time`,a.`question_num`,a.`total_score`,a.`manual`" +
            " from activity a  " +
            " where a.id=#{id}")
    ActivityDTO view(Integer id);

    @Update("update activity set status=#{status} where id=#{id}")
    int updateStatus(int id, int status);

    /**
     * 获取关联机构ID
     * @param activityId
     * @return
     */
    @Select(" select DISTINCT mechanism_id  from mechanism_activity_ref where is_delete =0 and activity_id=#{activityId} ")
    List<Integer> getRefMechanismIdList(Integer activityId);

    /**
     * 获取消息关联的机构列表
     * @param query
     * @return
     */
    @SelectProvider(type= ActivityMapperDynaSqlProvider.class,method="getRefMechanismList")
    List<MechanismDTO> getRefMechanismList(BindMechanismQuery query);

    /**
     * 批量保存机构活动关联
     * @param mechanismActivityRefDOList
     * @return
     */
    @InsertProvider(type = ActivityMapperDynaSqlProvider.class, method = "insertMechanismActivityRefList")
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    Integer insertMechanismActivityRefList(List<MechanismActivityRefDO> mechanismActivityRefDOList);

    /**
     * 取消活动机构关联
     * @param activityId
     * @param mechanismId
     * @return
     */
    @Update("update mechanism_activity_ref set is_delete=1 where mechanism_id = #{mechanismId} and activity_id=#{activityId}")
    int disassociate(Integer activityId, Integer mechanismId);

    /**
     * 批量取消活动机构关联
     * @param activityId
     * @param mechanismIdList
     * @return
     */
    @Update("<script>"
            + "update mechanism_activity_ref set is_delete=1 where   activity_id=#{activityId} and mechanism_id in  "
            + "<foreach item='item' index='index' collection='mechanismIdList' open='(' separator=',' close=')'>"
            + "#{item}"
            + "</foreach>"
            + "</script>")
    int disassociateMechanismRefList(Integer activityId, List<Integer> mechanismIdList);


    /**
     * 批量查询活动 关联 图书馆数量
     * @param activityIdList
     * @return
     */
    @MapKey("id")
    @Select({" <script> ",
            " SELECT activity_id id ,count(1) num ",
            " from mechanism_activity_ref   ",
            " WHERE is_delete =0 and activity_id in ",
            " <foreach item='item' index='index' collection='activityIdList' open='(' separator=',' close=')'> ",
            " #{item} ",
            " </foreach> ",
            " GROUP BY activity_id",
            " </script>",
    })
    Map<Integer, RefLibraryNumDTO> getLibraryNumMap(@Param("activityIdList") List<Integer> activityIdList);

    @Update("UPDATE activity set question_num=#{questionNum},total_score=#{totalScore},manual=#{manual} where  id =#{activityId} ")
    int updateActivityScoreAndNum(ActivityContentParam param);

    /**
     * 批量保存活动内容
     * @param activityContentRefDOList
     * @return
     */
    @InsertProvider(type = ActivityMapperDynaSqlProvider.class, method = "insertActivityContentRefList")
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    Integer  insertActivityContentRefList(List<ActivityContentRefDO> activityContentRefDOList);

    @Update("UPDATE activity_content_ref set is_delete=1 where  activity_id =#{activityId} ")
    int delActivityContent(Integer activityId);


    @Select(" select ao.* ,a.manual,a.question_num,a.total_score " +
            " from activity_operation_ref ao " +
            " LEFT JOIN activity a on a.id =ao.activity_id and ao.is_delete =0 " +
            " where ao.activity_id=#{activityId}")
    ActivityOperationRefDO getOperationByActivityId(Integer activityId);

    @Insert(" INSERT INTO `activity_operation_ref`(`activity_id`,`join_times`,`join_frequency`,`integral`,`integral_times`,`integral_frequency`,`library`,`country`,`lottery_close`,`lottery_times`,`lottery_trigger_rules`,`lottery_description`,`show_barrage`) " +
            " VALUES (#{activityId},#{joinTimes},#{joinFrequency},#{integral},#{integralTimes},#{integralFrequency},#{library},#{country},#{lotteryClose},#{lotteryTimes},#{lotteryTriggerRules},#{lotteryDescription},#{showBarrage})")
    int insertOperation(ActivityOperationRefDO activityOperation);

    @UpdateProvider(type = ActivityMapperDynaSqlProvider.class,method = "updateOperation")
    int updateOperation(ActivityOperationRefDO activityOperation);

    /**
     * 获得用户参加活动的总次数 进入时计算次数（初始化user_activity_detail_ref 和user_activity_ref）
     * @param userId
     * @param activityId
     * @return
     */
    @Select("SELECT COUNT(1)  FROM user_activity_detail_ref uadr " +
            " LEFT JOIN user_activity_ref ua on ua.id =uadr.user_activity_id   " +
            " WHERE ua.is_delete =0 and uadr.is_delete =0  " +
            " and ua.user_id =#{userId} and ua.activity_id =#{activityId} ")
    Integer getAllJoinedTimes(Integer userId, Integer activityId);

    /**
     * 获得用户 每天参加活动的次数  进入时计算次数（初始化user_activity_detail_ref 和user_activity_ref）
     * @param userId
     * @param activityId
     * @return
     */
    @Select("SELECT COUNT(1)  FROM user_activity_detail_ref uadr " +
            " LEFT JOIN user_activity_ref ua on ua.id =uadr.user_activity_id   " +
            " WHERE ua.is_delete =0 and uadr.is_delete =0  " +
            " and ua.user_id =#{userId} and ua.activity_id =#{activityId} " +
            " and uadr.sign_date = #{today}")
    Integer getPerDayJoinedtimes(Integer userId, Integer activityId, Date today);

    /**
     *
     * @param activityId
     * @return
     */
    @Select("select id,activity_id,category_id,question_num  from activity_content_ref where activity_id =#{activityId} and is_delete =0")
    List<ActivityContentRefDTO> getActivityContent(Integer activityId);

    @Select("SELECT * FROM user_activity_ref WHERE is_delete =0 and user_id=#{userId} and activity_id =#{activityId}")
    UserActivityRefDTO findUserActivityRefDTO(Integer userId, Integer activityId);

    @Insert(" INSERT INTO `user_activity_ref`(`user_id`,`activity_id`,`score`,`times`,`complete`) " +
            " VALUES (#{userId},#{activityId},#{score},#{times},#{complete})")
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    int insertUserActivityRef(UserActivityRefDO userActivityRefDO);

    @Insert(" INSERT INTO `user_activity_detail_ref`(`user_activity_id`,`score`,`times`,sign_date) " +
            " VALUES (#{userActivityId},#{score},#{times},#{signDate})")
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    int insertUserActivityDetail(UserActivityDetailRefDO userActivityDetailRefDO);

    @Update("UPDATE user_activity_detail_ref SET score =#{score},times=#{times} WHERE id =#{id}")
    int updateUserActivityDetail(UserActivityDetailRefDO detail);


    @Update("UPDATE user_activity_ref set `score`=#{score} ,times =#{times},complete=1 where  id =#{id} ")
    int updateScoreAndTimes(Integer score, Integer times, Integer id);

    @Update("UPDATE user_activity_ref set complete=1 where  id =#{id} ")
    int completeUserActivity(Integer id);

    /**
     * 初始化排行榜 不能作为榜单结果返回
     * @param query
     * @return
     */
    @SelectProvider(type= ActivityMapperDynaSqlProvider.class,method="rankingList")
    List<HeroRankingVO> rankingList(ActivityRankingQuery query);

    /**
     * 获取排行榜用户信息（含登录用户）
     * @param topUserIdList
     * @return
     */
    @MapKey("userId")
    @Select("<script>"
            + " SELECT u.id userId,u.nick_name name,u.head_img,ua.score,ua.times,u.mechanism_id,m.`name` mechanismName ,m.show_name,m.app_show_name,m.login_logo,m.app_login_logo  " +
            "   from user_activity_ref ua " +
            "   left join  `user` u on u.id = ua.user_id and ua.is_delete =0 " +
            "   left join  mechanism m on m.id = u.mechanism_id" +
            "  WHERE  ua.`activity_id` =#{activityId} and u.id in  "
            + "<foreach item='item' index='index' collection='topUserIdList' open='(' separator=',' close=')'>"
            + "#{item}"
            + "</foreach>"
            + "</script>")
    Map<Integer, HeroRankingVO> rankingListByTopUserIdList(@Param("activityId") Integer activityId, @Param("topUserIdList")List<Integer> topUserIdList);
    /**
     * 保存活动奖品列表
     * @param prizeDetailList
     * @return
     */
    @InsertProvider(type = ActivityMapperDynaSqlProvider.class, method = "insertPrizeDetailList")
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    int insertPrizeDetailList(List<ActivityPrizeDetailDO> prizeDetailList);

    @Update("UPDATE activity_prize_detail set is_delete=1 where  activity_id =#{activityId} ")
    int delPrizeDetail(Integer activityId);

    /**
     * 查询用户抽奖信息
     * @param userId
     * @param activityId
     * @return
     */
    @Select("SELECT * FROM user_lottery WHERE is_delete =0 and user_id=#{userId} and activity_id =#{activityId}")
    UserLotteryDO findUserLotteryByActivityId(Integer userId, Integer activityId);

    /**
     * 修改用户抽奖总次数
     * @param id
     * @param lotteryNum
     */
    @Update("update user_lottery set lottery_num= #{lotteryNum} where id =#{id}")
    void updateLotteryNum(Integer id,Integer lotteryNum);

    /**
     * 修改用户已抽奖次数
     * @param id
     * @param joinedNum
     */
    @Update("update user_lottery set joined_num= #{joinedNum} where id =#{id}")
    void updateJoinedNum(Integer id,Integer joinedNum);


    @Insert(" INSERT INTO `user_lottery`(`user_id`,`activity_id`,`lottery_num`,`joined_num`) " +
            " VALUES (#{userId},#{activityId},#{lotteryNum},#{joinedNum})")
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    int insertUserLottery(UserLotteryDO ul);

    @Select("SELECT * FROM activity_prize_detail WHERE is_delete =0 and activity_id =#{activityId}")
    List<ActivityPrizeDetailDTO> getActivityPrizeList(Integer activityId);
    @Select("SELECT * FROM activity_prize_detail WHERE is_delete =0 and activity_id =#{activityId}")
    List<ActivityPrizeDetailDO> getActivityPrizeDOList(Integer activityId);

    @Select("SELECT apd.id,apd.prize_name,apd.prize_type,apd.create_time " +
            "FROM user_activity_prize_detail_ref upd  " +
            "LEFT JOIN activity_prize_detail apd on upd.activity_prize_detail_id =apd.id  " +
            "WHERE upd.user_id =#{userId} and apd.activity_id =#{activityId} and upd.is_delete =0 and upd.activity_prize_detail_id >0 and apd.prize_type > 0")
    List<ActivityPrizeDetailDTO> getMyActivityPrizeList(Integer userId, Integer activityId);

    @Select("SELECT apd.prize_name,u.nick_name ,apd.`prize_type` " +
            "FROM user_activity_prize_detail_ref upd  " +
            "LEFT JOIN activity_prize_detail apd on upd.activity_prize_detail_id =apd.id " +
            "LEFT JOIN `user` u on u.id =upd.user_id  " +
            " WHERE   apd.activity_id =#{activityId} and upd.is_delete =0 and upd.activity_prize_detail_id >0 and apd.prize_type > 0")
    List<ActivityWinLotteryUserVO> getWinLotteryuserlist(Integer activityId);

    @Select("SELECT * from  activity_content_ref WHERE activity_id =#{activityId} and is_delete =0")
    List<ActivityContentRefDO> findContentOfActivity(Integer activityId);
    @Insert("INSERT INTO `user_activity_prize_detail_ref`(`user_id`, `activity_prize_detail_id`, `is_delete`) VALUES (#{userId}, #{activityPrizeDetailId}, 0)")
    void insertUserPrizie(UserActivityPrizeDetailRefDO userPrize);

    @Update("update activity_prize_detail set surplus_prize_num=surplus_prize_num-1 where id=#{id}")
    void activityPrizeDetailSurplusNumDecrOne(Integer id);

    @Select(" select apd.* from activity a " +
            " INNER JOIN activity_operation_ref aor on a.id=aor.activity_id and aor.is_delete=0 and lottery_close=0 " +
            " INNER JOIN activity_prize_detail apd on apd.activity_id=a.id and apd.is_delete=0 " +
            " where #{dateNow} >= a.begin_time and #{dateNow} <= a.end_time and aor.lottery_close=#{lottery}")
    List<ActivityPrizeDetailDTO> allEffectivePrizeDetail(Date dateNow, int lottery);

    /**
     * 活动最后一次活动参加明细
     * @param userActivityId
     * @return
     */
    @Select("SELECT id,user_activity_id,`score`,times,sign_date  " +
            "FROM user_activity_detail_ref " +
            "WHERE user_activity_id =#{userActivityId} and is_delete=0 " +
            "ORDER BY id DESC " +
            "LIMIT 1")
    UserActivityDetailRefDO getLastDetailByUserActivityId(Integer userActivityId);

    @Select("select count(1) from enroll where is_delete=0 and user_id =#{userId}")
    int enrollStatus(Integer userId);

    @Insert(" INSERT INTO `enroll`(user_id,`name`,`age`) " +
            " VALUES (#{userId},#{name},#{age})")
    int insertEnrolIn(Integer userId, String name, Integer age);

    @Select(" SELECT m.`name` mechanismName ,u.`nick_name` userName,u.`phone`  ,ua.activity_id ,a.`name` activityName ,ua.`score` ,ua.`times`/1000 times  ,ua.`update_time` " +
            " FROM user_activity_ref ua" +
            " LEFT JOIN `user` u on u.id= ua.`user_id` " +
            " LEFT JOIN `mechanism` m on m.`id` =u.`mechanism_id`" +
            " LEFT JOIN `activity` a on ua.`activity_id` =a.id" +
            " WHERE ua.`activity_id` =#{activityId} and ua.`is_delete` =0  " +
            " ORDER BY ua.activity_id")
    List<UserAnswersExportDTO> downUserAnswers(Integer activityId);

    @Select(" SELECT ua.activity_id ,a.`name` activityName ,m.`name` mechanismName ,u.`nick_name` userName,u.`phone`  ,apd.`prize_name` ,uap.`create_time` ,ua.`create_time` joinTime" +
            " FROM  `user_activity_ref` ua" +
            " inner JOIN `activity_prize_detail`  apd on apd.`activity_id`  = ua.`activity_id`   and  apd.`is_delete` =0" +
            " inner JOIN   `user_activity_prize_detail_ref` uap on uap.`activity_prize_detail_id` = apd.`id`  and uap.`is_delete` =0" +
            " inner JOIN `user` u on u.`id` = ua.`user_id` and uap.`user_id` =u.id" +
            " inner JOIN  `mechanism` m on m.id =u.`mechanism_id` " +
            " inner JOIN  `activity` a on a.`id` = ua.`activity_id` " +
            " WHERE  ua.`activity_id` =#{activityId} and apd.`prize_name`  IS NOT NULL " +
            " ORDER BY ua.activity_id")
    List<LotteryDetailsExportDTO> downLotteryDetails(Integer activityId);
}
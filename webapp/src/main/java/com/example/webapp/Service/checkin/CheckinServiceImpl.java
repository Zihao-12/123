package com.example.webapp.Service.checkin;

import com.example.webapp.DO.CheckinDO;
import com.example.webapp.DO.UserTakeCourseDO;
import com.example.webapp.Mapper.check.CheckinMapper;
import com.example.webapp.Mapper.course.CourseMapper;
import com.example.webapp.VO.CheckinCourseVO;
import com.example.webapp.VO.UserCoinsVO;
import com.example.webapp.annotation.Cacheable;
import com.example.webapp.annotation.RepeatableCommit;
import com.example.webapp.common.redis.RedisKeyGenerator;
import com.example.webapp.common.redis.RedisUtils;
import com.example.webapp.enums.CheckinTypeEnum;
import com.example.webapp.enums.ConsumeGoldCoinsTypeEnum;
import com.example.webapp.result.CodeEnum;
import com.example.webapp.result.Result;
import com.example.webapp.utils.DateTimeUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

@EnableTransactionManagement
@Slf4j
@Service
public class CheckinServiceImpl implements CheckinService, Serializable {
    private static final long serialVersionUID = 4800994516532057532L;
    private static final Integer NOT_COMPLETE = 0;

    private static final Integer manual =1;
    public static final String COURSE_LIST = "COURSE_LIST";
    @Autowired
    private CheckinMapper checkinMapper;
    @Autowired
    private CourseMapper courseMapper;
    @Autowired
    private RedisUtils redisUtils;


    /**
     * 签到课程列表 (随机八个课程)
     * @param userId
     * @return
     */
    @Override
    public List<CheckinCourseVO> courseList(Integer categoryId, Integer userId) {
        int size =8;
        List<CheckinCourseVO> list = courseMapper.getAllCheckinCourse(categoryId);
        if(CollectionUtils.isNotEmpty(list)){
            Collections.shuffle(list);
            if(list.size() > size) {
                List sublist = Lists.newArrayList();
                sublist.addAll(list.subList(0,size));
                list = sublist;
            }
            String key = RedisKeyGenerator.getKey(CheckinServiceImpl.class, COURSE_LIST,userId);
            redisUtils.set(key,list,RedisUtils.TIME_DAY_1);
        }
        return list;
    }

    /**
     * 领取签到课程(非免费自动消耗金豆)
     * @param userId
     * @return
     */
    @Cacheable(prefix = "getUserCoins",fieldKey = "#userId",cacheOperation = Cacheable.CacheOperation.UPDATE)
    @RepeatableCommit
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result takeCourse(Integer userId) {
        String key = RedisKeyGenerator.getKey(CheckinServiceImpl.class, COURSE_LIST,userId);
        List<CheckinCourseVO> list = (List<CheckinCourseVO>) redisUtils.get(key);
        if(CollectionUtils.isEmpty(list)){
            return Result.fail("领取失败");
        }
        Random random = new Random();
        Integer i = random.nextInt(list.size());
        CheckinCourseVO checkinCourseVO = list.get(i);
        Date date = DateTimeUtil.parse(DateTimeUtil.format(new Date(),DateTimeUtil.DATE_FORMAT_YYYYMMDD));
        int signNum = checkinMapper.isSignToday(userId,date,CheckinTypeEnum.VIDEO.getType());
        if(signNum > 0){
            return Result.fail("今日签到已完成，不能在领取课程");
        }
        UserTakeCourseDO old = checkinMapper.getTakeCourse(userId,date);
        if(old !=null ){
            //消耗金豆
            Result consumeResult = consumeGoldCoins(userId, ConsumeGoldCoinsTypeEnum.TAKE_COURSE.getScore());
            if(CodeEnum.FAILED.getValue().equals(consumeResult.getCode())){
                return consumeResult;
            }
            checkinMapper.delUserTakeCourseById(old.getId());
        }
        UserTakeCourseDO takeCourseDO = new UserTakeCourseDO();
        takeCourseDO.setComplete(0);
        takeCourseDO.setUserId(userId);
        takeCourseDO.setCourseId(checkinCourseVO.getCourseId());
        takeCourseDO.setTakeDate(date);
        checkinMapper.insertUserTakeCourse(takeCourseDO);
        return Result.ok(checkinCourseVO);
    }



    /**
     * 获取用户今日签到课程
     * @param userId
     * @return
     */
    @Override
    public CheckinCourseVO getTakeCourse(Integer userId) {
        Date  date = DateTimeUtil.parse(DateTimeUtil.format(new Date(),DateTimeUtil.DATE_FORMAT_YYYYMMDD));
        UserTakeCourseDO takeCourseDO = checkinMapper.getTakeCourse(userId,date);
        if(takeCourseDO != null){
            CheckinCourseVO courseVO = courseMapper.checkinCourseView(takeCourseDO.getCourseId());
            if(courseVO !=null){
                courseVO.setComplete(takeCourseDO.getComplete());
                return courseVO;
            }
        }
        return null;
    }

    /**
     * 完成课程(视频签到积分)
     * @param userId
     * @param courseId
     * @return
     */
    @Cacheable(prefix = "getUserCoins",fieldKey = "#userId",cacheOperation = Cacheable.CacheOperation.UPDATE)
    @RepeatableCommit
    @Override
    public Result complete(Integer userId, Integer courseId) {
        Date  date = DateTimeUtil.parse(DateTimeUtil.format(new Date(),DateTimeUtil.DATE_FORMAT_YYYYMMDD));
        UserTakeCourseDO takeCourseDO = checkinMapper.getTakeCourse(userId,date);
        if(takeCourseDO == null || !takeCourseDO.getCourseId().equals(courseId)){
            return Result.ok("未领取签到课程");
        }
        if(!NOT_COMPLETE.equals(takeCourseDO.getComplete())){
            return Result.ok("重复完成");
        }
        checkinMapper.updateUserTakeCourseComplete(takeCourseDO.getId());
        Result result = checkIn(userId,date,CheckinTypeEnum.VIDEO.getType(),courseId,CheckinTypeEnum.VIDEO.getScore());
        if(CodeEnum.SUCCESS.getValue().equals(result.getCode())){
            return Result.ok("签到成功");
        }else {
            return Result.ok("签到失败");
        }
    }

    /**
     * 签到
     * @param userId   签到用户
     * @param signDate 签到日期
     * @param signType 0视频签到 1活动签到
     * @param objectId 签到对象
     * @param score    签到得分
     * @return
     */
    @Cacheable(prefix = "getUserCoins",fieldKey = "#userId",cacheOperation = Cacheable.CacheOperation.UPDATE)
    @RepeatableCommit
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result checkIn(Integer userId, Date signDate, Integer signType,Integer objectId,Integer score) {
        Date  date = DateTimeUtil.parse(DateTimeUtil.format(signDate,DateTimeUtil.DATE_FORMAT_YYYYMMDD));
        int signNum = checkinMapper.isSignObjectToday(userId,date,signType,objectId);
        if(signNum == 0 || !CheckinTypeEnum.VIDEO.getType().equals(signType)){
            CheckinDO checkin = new CheckinDO();
            checkin.setUserId(userId);
            checkin.setSignType(signType);
            checkin.setSignDate(date);
            checkin.setCheckinTimes(0);
            checkin.setObjectId(objectId);
            checkin.setScore(score);
            if (CheckinTypeEnum.VIDEO.getType().equals(signType)) {
                //签到
                CheckinDO checkinOfYesterDay = checkinMapper.findSignOfYesterDay(userId);
                //连续签到天数
                int temp = checkinOfYesterDay == null ? 1 : checkinOfYesterDay.getCheckinTimes() + 1;
                checkin.setCheckinTimes(temp);
            }
            checkinMapper.save(checkin);
            return Result.ok(0);
        }
        return Result.fail();
    }


    /**
     * 用户总积分（金豆）+ 累计签到天数
     * @param userId
     * @return
     */
    @Cacheable(prefix = "getUserCoins",fieldKey = "#userId",expireTime = 60*60*12)
    @Override
    public Result getUserCoins(Integer userId) {
        UserCoinsVO coinsVO = new UserCoinsVO();
        Integer coins = checkinMapper.getUserCoins(userId);
        if(coins == null){
            coins =0;
        }
        Integer cumulativeTimes = checkinMapper.getCumulativeTimes(userId);
        Date  date = DateTimeUtil.parse(DateTimeUtil.format(new Date(),DateTimeUtil.DATE_FORMAT_YYYYMMDD));
        UserTakeCourseDO takeCourseDO = checkinMapper.getTakeCourse(userId,date);
        coinsVO.setCheckin(takeCourseDO == null?0:takeCourseDO.getComplete());
        coinsVO.setUserId(userId);
        coinsVO.setCumulativeTimes(cumulativeTimes);
        coinsVO.setScore(coins);
        return Result.ok(coinsVO);
    }


    /**
     * 消耗金豆
     * @param userId
     * @param score
     */
    @Cacheable(prefix = "getUserCoins",fieldKey = "#userId",cacheOperation = Cacheable.CacheOperation.UPDATE)
    @RepeatableCommit(timeout = 5)
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result consumeGoldCoins(Integer userId, Integer score) {
        Integer coins = checkinMapper.getUserCoins(userId);
        if(coins == null || coins <score){
            coins = coins == null?0:coins;
            return Result.fail("您的金豆不足");
        }
        Date  date = DateTimeUtil.parse(DateTimeUtil.format(new Date(),DateTimeUtil.DATE_FORMAT_YYYYMMDD));
        log.info("消耗金豆");
        CheckinDO checkin = new CheckinDO();
        checkin.setUserId(userId);
        checkin.setSignType(CheckinTypeEnum.CONSUME.getType());
        checkin.setSignDate(date);
        checkin.setCheckinTimes(0);
        checkin.setObjectId(0);
        checkin.setScore(score*(-1));
        checkinMapper.save(checkin);
        return Result.ok(0);
    }
}


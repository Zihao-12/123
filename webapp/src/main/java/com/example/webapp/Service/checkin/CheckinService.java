package com.example.webapp.Service.checkin;

import com.example.webapp.VO.CheckinCourseVO;
import com.example.webapp.result.Result;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public interface CheckinService {

    /**
     * 签到课程列表
     * @param userId
     * @return
     */
    List<CheckinCourseVO> courseList(Integer categoryId, Integer userId);

    /**
     * 领取签到课程(非免费自动消耗金豆)
     * @param userId
     * @return
     */
    Result takeCourse(Integer userId);

    /**
     * 获取用户今日签到课程
     * @param userId
     * @return
     */
    CheckinCourseVO getTakeCourse(Integer userId);

    /**
     * 完成课程(视频签到积分)
     * @param userId
     * @param courseId
     * @return
     */
    Result complete(Integer userId, Integer courseId);

    /**
     * 签到
     * @param userId   签到用户
     * @param signDate 签到日期
     * @param signType 0视频签到 1活动签到
     * @param objectId 签到对象
     * @param score    签到得分
     * @return
     */
    Result checkIn(Integer userId, Date signDate, Integer signType, Integer objectId, Integer score);

    /**
     * 用户总积分（金豆）+ 累计签到天数
     * @param userId
     * @return
     */
    Result getUserCoins(Integer userId);

    /**
     * 消耗金豆
     * @param userId
     * @param score
     */
    Result consumeGoldCoins(Integer userId, Integer score);
}


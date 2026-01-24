package com.example.webapp.Service.activity;


import com.example.webapp.DO.ActivityDO;
import com.example.webapp.DO.ActivityOperationRefDO;
import com.example.webapp.DO.UserActivityRefDO;
import com.example.webapp.DTO.*;
import com.example.webapp.query.ActivityQuery;
import com.example.webapp.query.ActivityRankingQuery;
import com.example.webapp.query.BindMechanismQuery;
import com.example.webapp.VO.ActivityOperationVO;
import com.example.webapp.result.Result;
import com.example.webapp.result.ResultPage;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface ActivityService {

    /**
     * 分页列表
     * @param query
     * @return
     */
    ResultPage list(ActivityQuery query);

    ResultPage portalList(ActivityQuery query);

    /**
     * 新建
     * @return
     */
    Result insert(ActivityDO activityDO);

    /**
     * 更新
     * @return
     */
    Result update(ActivityDO activityDO);

    /**
     * 删除
     * @param id
     * @return
     */
    Result delete(Integer id);

    /**
     * 详情
     * @param id
     * @return
     */
    ActivityDTO view(Integer id);
    /**
     * 是否上架：1上架 0下架
     * @param id
     * @param status
     * @return
     */
    Result updateStatus(int id, int status);


    /**
     * 活动绑定到全部机构
     * @param activityId
     * @return
     */
    Result bindAllMechanism(Integer activityId);

    /**
     * 活动绑定到指定部机构
     * @param bindMechanismDTO
     * @return
     */
    Result bindSpecifyMechanism(BindMechanismDTO bindMechanismDTO);

    /**
     * 取消活动关联机构
     * @param activityId
     * @param mechanismId
     * @return
     */
    Result disassociate(Integer activityId, Integer mechanismId);

    /**
     * 获取活动关联的机构列表
     * @param query
     * @return
     */
    PageInfo getRefMechanismList(BindMechanismQuery query);

    /**
     * 保持答题内容
     * @param param
     * @return
     */
    Result saveContent(ActivityContentParam param);

    /**
     * 保存活动运营设置
     * @param activityOperation
     * @return
     */
    Result saveOperation(ActivityOperationRefDO activityOperation);

    /**
     * 参加活动
     * @param activityId
     * @param id
     * @return
     */
    Result join(Integer activityId, Integer id);

    /**
     * 参加活动-提交
     * @param userActivityRefDO
     * @return
     */
    Result submit(UserActivityRefDO userActivityRefDO);

    Result rankingList(ActivityRankingQuery query);

    /**
     * 活动提交-计算得分
     * @param userAnswer
     * @return
     */
    UserActivityRefDO judgeScore(UserAnswerDTO userAnswer);

    /**
     * 用户活动的抽奖/排行榜设置信息
     * @param activityId
     * @param userId
     * @return
     */
    ActivityOperationVO getUserActivityOperation(Integer activityId, Integer userId);

    String getHreoCacheKey(Integer mechanismId, Integer activityId, Integer type);

    void initRanking(Integer mechanismId, Integer activityId, Integer type);

    /**
     * 抽奖
     * @param userId
     * @param activityId
     * @return
     */
    Result lottery(Integer userId, Integer activityId);

    /**
     * 活动奖品列表
     * @param activityId
     * @return
     */
    List<ActivityPrizeDetailDTO> getActivityPrizeList(Integer activityId);

    /**
     * 我的活动奖品列表
     * @param userId
     * @param activityId
     * @return
     */
    Result getMyActivityPrizeList(Integer userId, Integer activityId);

    /**
     * 活动中奖名单
     * @param userId
     * @param activityId
     * @return
     */
    Result getWinLotteryuserlist(Integer userId, Integer activityId);

    /**
     * 活动运营详情
     * @param activityId
     * @return
     */
    ActivityOperationRefDO operationView(Integer activityId);

    /**
     * 活动答题内容详情
     * @param activityId
     * @return
     */
    ActivityContentParam contentView(Integer activityId);

    /**
     * 恢复抽奖库存缓存
     * @return
     */
    Result recoveryLotteryRedis();

    /**
     * 设置抽奖库存缓存
     * @param activityId
     */
    void setStockRedis(Integer activityId);

    /**
     * 报名参加临时活动
     * @param userId
     * @param name
     * @param age
     * @return
     */
    Result enrollIn(Integer userId, String name, Integer age);

    /**
     * 报名状态
     * @param userId
     * @return
     */
    Result enrollStatus(Integer userId);

    List<UserAnswersExportDTO> downUserAnswers(Integer activityId);

    List<LotteryDetailsExportDTO> downLotteryDetails(Integer activityId);
}
package com.example.webapp.Service.activity;

import com.example.webapp.DO.*;
import com.example.webapp.DTO.*;
import com.example.webapp.Mapper.Activity.ActivityMapper;
import com.example.webapp.Mapper.MechanismOpen.MechanismOpenMapper;
import com.example.webapp.Mapper.question.QuestionMapper;
import com.example.webapp.Query.ActivityQuery;
import com.example.webapp.Query.ActivityRankingQuery;
import com.example.webapp.Query.BindMechanismQuery;
import com.example.webapp.VO.*;
import com.example.webapp.common.Constant;
import com.example.webapp.common.redis.RedisLock;
import com.example.webapp.common.redis.RedisUtils;
import com.example.webapp.enums.*;
import com.example.webapp.result.CodeEnum;
import com.example.webapp.result.Result;
import com.example.webapp.result.ResultPage;
import com.example.webapp.utils.DateTimeUtil;
import com.example.webapp.utils.ListOperateDTO;
import com.example.webapp.utils.ListUtil;
import com.example.webapp.utils.SortField;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
@Slf4j
@Service
public class ActivityServiceImpl implements ActivityService, Serializable {
    private static final long serialVersionUID = 4800994516532057532L;
    private static final Integer CORRECT_VALUE = 1;
    private static final Integer COMPLETE = 1;
    private static final Integer TIMES_NOT_SUBMIT = -1;
    //0设置抽奖 1关闭抽奖
    private static final Integer LOTTERY = 0;
    public static final String GET_ACTIVITY_PRIZE_LIST = "GET_ACTIVITY_PRIZE_LIST";
    public static final String RANKING_LIST = "RANKING_LIST";
    //剩余奖品数量(奖品库存)key + 奖品明细id
    public static final String SURPLUS_PRIZE = "SURPLUS_PRIZE_";
    //剩余奖品数量(奖品库存)key + 奖品明细id
    public static final String ACTIVITY_PRIZE_DETAIL = "activity_prize_detail_";
    public static final String RANDOM_QUESTION_LIST_OF_USER_ACTIVITY = "RANDOM_QUESTION_LIST_OF_USER_ACTIVITY";
    @Value("${spring.application.name}")
    private String applicationName;

    /**
     * 手动配置
     */
    private static final Integer manual =1;
    @Autowired
    private ActivityMapper activityMapper;
    @Autowired
    private CheckinMapper checkinMapper;
    @Autowired
    private CheckinService checkinService;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private MechanismOpenMapper mechanismOpenMapper;
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private RedisLock redisLock;


    /**
     * 运营列表
     * @param query
     * @return
     */
    @Override
    public ResultPage list(ActivityQuery query) {
        PageInfo pageInfo = null;
        try {
            //没有传入页码，则默认设为0
            query.setPageNo(query.getPageNo() == null ? 0 : query.getPageNo());
            query.setPageSize(query.getPageSize() == null ? PAGE_SIZE : query.getPageSize());
            if(StringUtils.isNotBlank(query.getName())){
                query.setName("%"+query.getName()+"%");
            }
            //使用分页插件,核心代码就这一行 #分页配置#
            PageHelper.startPage(query.getPageNo(), query.getPageSize());
            List<ActivityDTO> list = activityMapper.selectAll(query);
            pageInfo = new PageInfo(list);
            if (CollectionUtils.isEmpty(list)) {
                return ResultPage.ok(pageInfo.getList(), pageInfo.getPageNum(), pageInfo.getPageSize(), pageInfo.getTotal());
            }else {
                List<Integer> activityIdList = list.stream().map(ActivityDTO::getId).collect(Collectors.toList());
                Map<Integer, RefLibraryNumDTO> map = activityMapper.getLibraryNumMap(activityIdList);
                list.stream().forEach(a->{
                    ExamStatusEnum status =ExamStatusEnum.TO_START;
                    //设置活动状态
                    if(DateTimeUtil.isAfterNow(a.getBeginTime())){
                        status = ExamStatusEnum.TO_START;
                    }else if(DateTimeUtil.isBeforeNow(a.getBeginTime()) && DateTimeUtil.isAfterNow(a.getEndTime())){
                        status =ExamStatusEnum.START;
                    }else if(DateTimeUtil.isBeforeNow(a.getEndTime())){
                        status =ExamStatusEnum.OVER;
                    }
                    a.setActivityStatus(status.getType());
                    a.setActivityStatusCn(status.getName());
                    a.setStatusCn(UpDownStatusEnum.getUpDownStatusName(a.getStatus()));
                    if(map!=null && map.get(a.getId()) !=null){
                        a.setLibraryNum(map.get(a.getId()).getNum());
                    }
                });
            }
            return ResultPage.ok(list, pageInfo.getPageNum(), pageInfo.getPageSize(), pageInfo.getTotal());
        }catch (Exception e){
            log.error("{}", ExceptionUtils.getStackTrace(e));
            throw e;
        }
    }

    /**
     * 用户列表
     * @param query
     * @return
     */
    @Override
    public ResultPage portalList(ActivityQuery query) {
        PageInfo pageInfo = null;
        try {
            query.setPageNo(query.getPageNo() == null ? 0 : query.getPageNo());
            query.setPageSize(query.getPageSize() == null ? PAGE_SIZE : query.getPageSize());
            if(StringUtils.isNotBlank(query.getName())){
                query.setName("%"+query.getName()+"%");
            }
            //使用分页插件,核心代码就这一行 #分页配置#
            PageHelper.startPage(query.getPageNo(), query.getPageSize());
            List<ActivityDTO> list = activityMapper.selectAll(query);
            pageInfo = new PageInfo(list);
            if (CollectionUtils.isEmpty(list)) {
                return ResultPage.ok(pageInfo.getList(), pageInfo.getPageNum(), pageInfo.getPageSize(), pageInfo.getTotal());
            }else {
                Map<Integer, CategoryDTO> categoryMap = getCategoryIdList(list);
                list.stream().forEach(a->{
                    ExamStatusEnum status = ExamStatusEnum.TO_START;
                    //设置活动状态
                    if(DateTimeUtil.isAfterNow(a.getBeginTime())){
                        status = ExamStatusEnum.TO_START;
                    }else if(DateTimeUtil.isBeforeNow(a.getBeginTime()) && DateTimeUtil.isAfterNow(a.getEndTime())){
                        status =ExamStatusEnum.START;
                    }else if(DateTimeUtil.isBeforeNow(a.getEndTime())){
                        status =ExamStatusEnum.OVER;
                    }
                    a.setActivityStatus(status.getType());
                    a.setActivityStatusCn(status.getName());
                    a.setStatusCn(UpDownStatusEnum.getUpDownStatusName(a.getStatus()));
                    if(categoryMap.get(a.getAge()) !=null){
                        a.setAgeCn(categoryMap.get(a.getAge()).getName());
                    }
                    if(categoryMap.get(a.getShape()) !=null){
                        a.setShapeCn(categoryMap.get(a.getShape()).getName());
                    }
                    if(categoryMap.get(a.getType()) !=null){
                        a.setTypeCn(categoryMap.get(a.getType()).getName());
                    }
                });
            }
            return ResultPage.ok(list, pageInfo.getPageNum(), pageInfo.getPageSize(), pageInfo.getTotal());
        }catch (Exception e){
            log.error("{}", ExceptionUtils.getStackTrace(e));
            throw e;
        }
    }


    /**
     * 新建活动必须默认下架，上架时验证 是否有试题 且 分值大于0
     * @param activityDO
     * @return
     */
    @RepeatableCommit
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result insert(ActivityDO activityDO) {
        try {
            activityDO.setStatus(UpDownStatusEnum.DOWN.getStatus());
            activityMapper.insert(activityDO);
            //设置默认运营规则
            ActivityOperationRefDO operation =new ActivityOperationRefDO();
            operation.setActivityId(activityDO.getId());
            operation.setJoinTimes(ActivityJoinFrequencyTypeEnum.PER_DAY.getDefaultTimes());
            operation.setJoinFrequency(ActivityJoinFrequencyTypeEnum.PER_DAY.getType());
            operation.setIntegral(10);
            operation.setIntegralTimes(ActivityJoinFrequencyTypeEnum.ALL.getDefaultTimes());
            operation.setIntegralFrequency(ActivityJoinFrequencyTypeEnum.ALL.getType());
            //榜单默认全显示
            operation.setLibrary(1);
            operation.setCountry(1);
            //默认关闭抽奖
            operation.setLotteryClose(1);
            saveOperation(operation);
        }catch (Exception e){
            log.error("save error >>>>>>");
            throw e;
        }
        return Result.ok(activityDO.getId());
    }

    @Cacheable(prefix = "view",fieldKey = "#activityDO.id",cacheOperation = Cacheable.CacheOperation.UPDATE)
    @RepeatableCommit
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result update(ActivityDO activityDO) {
        try {
            if(activityDO.getId() != null){
                if(activityDO.getStatus() == null){
                    activityDO.setStatus(UpDownStatusEnum.DOWN.getStatus());
                }
                activityMapper.update(activityDO);
            }
        }catch (Exception e){
            log.error("update error >>>>>>");
            throw e;
        }
        return Result.ok(activityDO.getId());
    }

    @Cacheable(prefix = "view",fieldKey = "#id",cacheOperation = Cacheable.CacheOperation.UPDATE)
    @Override
    public Result delete(Integer id) {
        try {
            activityMapper.delete(id);
        }catch (Exception e){
            log.error("delete error >>>>>>");
        }
        return Result.ok(id);
    }

    @Cacheable(prefix = "view",fieldKey = "#id")
    @Override
    public ActivityDTO view(Integer id) {
        ActivityDTO a = null;
        try {
            a = activityMapper.view(id);
            if(a==null){
                a = new ActivityDTO();
            }else {
                ExamStatusEnum status =ExamStatusEnum.TO_START;
                //设置活动状态
                if(DateTimeUtil.isAfterNow(a.getBeginTime())){
                    status = ExamStatusEnum.TO_START;
                }else if(DateTimeUtil.isBeforeNow(a.getBeginTime()) && DateTimeUtil.isAfterNow(a.getEndTime())){
                    status =ExamStatusEnum.START;
                }else if(DateTimeUtil.isBeforeNow(a.getEndTime())){
                    status =ExamStatusEnum.OVER;
                }
                a.setActivityStatus(status.getType());
                a.setActivityStatusCn(status.getName());
            }
        }catch (Exception e){
            log.error("{}", ExceptionUtils.getStackTrace(e));
        }
        return a;

    }

    /**
     * 活动运营详情
     * @param activityId
     * @return
     */
    @Cacheable(prefix = "operationView",fieldKey = "#activityId")
    @Override
    public ActivityOperationRefDO operationView(Integer activityId) {
        ActivityDTO activity = activityMapper.view(activityId);
        ActivityOperationRefDO operation = null;
        if(activity !=null){
            operation = activityMapper.getOperationByActivityId(activityId);
            if(operation != null){
                List<ActivityPrizeDetailDO> prizeDetailList = activityMapper.getActivityPrizeDOList(activityId);
                operation.setPrizeDetailList(prizeDetailList);
            }
        }
        return operation;
    }

    /**
     * 活动答题内容详情
     * @param activityId
     * @return
     */
    @Cacheable(prefix = "contentView",fieldKey = "#activityId")
    @Override
    public ActivityContentParam contentView(Integer activityId) {
        ActivityDTO activity = activityMapper.view(activityId);
        ActivityContentParam contentParam = null;
        if(activity !=null){
            List<ActivityContentRefDO> activityContentRefDOList = activityMapper.findContentOfActivity(activityId);
            if(CollectionUtils.isNotEmpty(activityContentRefDOList)){
                List<Integer> categoryIdList = activityContentRefDOList.stream().map(ActivityContentRefDO::getCategoryId).collect(Collectors.toList());
                Map<Integer,CategoryObjectNumDTO> categoryMap = categoryMapper.getObjNumOfCategoryMap(ObjectTypeEnum.QUESTION.getType(),categoryIdList);
                activityContentRefDOList.stream().forEach(c ->{
                    CategoryObjectNumDTO cb=categoryMap.get(c.getCategoryId());
                    if(cb!=null){
                        c.setCategoryName(cb.getCategoryName());
                        c.setQuestionNum(cb.getObjNum());
                    }
                });
                contentParam = new ActivityContentParam();
                contentParam.setManual(activity.getManual());
                contentParam.setQuestionNum(activity.getQuestionNum());
                contentParam.setTotalScore(activity.getTotalScore());
                contentParam.setActivityId(activityId);
                contentParam.setActivityContentRefDOList(activityContentRefDOList);
            }
        }
        return contentParam;
    }

    /**
     * 是否上架：1上架 0下架
     *
     * @param id
     * @param status
     * @return
     */
    @Cacheable(prefix = "view",fieldKey = "#id",cacheOperation = Cacheable.CacheOperation.UPDATE)
    @Override
    public Result updateStatus(int id, int status) {
        int count = 0;
        try {
            if(UpDownStatusEnum.UP.getStatus().equals(status )){
                ActivityDTO activityDTO = view(id);
                if(activityDTO == null || activityDTO.getQuestionNum()<=0 || activityDTO.getTotalScore()<=0){
                    return Result.ok("请先设置答题内容在上架！");
                }
            }
            count = activityMapper.updateStatus(id,status);
        }catch (Exception e){
            log.error("{}", ExceptionUtils.getStackTrace(e));
            throw e;
        }
        return Result.ok(count);
    }

    /**
     * 活动绑定到全部机构
     * @param messageId
     * @return
     */
    @RepeatableCommit(timeout = 10)
    @Override
    public Result bindAllMechanism(Integer messageId) {
        List<Integer> mechanismIdList = mechanismOpenMapper.getOpenMechanismIdList();
        if(CollectionUtils.isNotEmpty(mechanismIdList)){
            BindMechanismDTO bindMechanismDTO =new BindMechanismDTO();
            bindMechanismDTO.setId(messageId);
            bindMechanismDTO.setMechanismIdList(mechanismIdList);
            bindSpecifyMechanism(bindMechanismDTO);
        }
        return Result.ok(0);
    }

    /**
     * 活动绑定到指定部机构
     * @param bindMechanismDTO
     * @return
     */
    @RepeatableCommit(timeout = 10)
    @Override
    public Result bindSpecifyMechanism(BindMechanismDTO bindMechanismDTO) {
        if(CollectionUtils.isEmpty(bindMechanismDTO.getMechanismIdList())){
            return  Result.fail("机构ID不能空");
        }
        Integer activityId = bindMechanismDTO.getId();
        List<Integer> existedRefIdList =activityMapper.getRefMechanismIdList(bindMechanismDTO.getId());
        ListOperateDTO<Integer> lo = ListUtil.getListOperateDTO(bindMechanismDTO.getMechanismIdList(),existedRefIdList);
        //新增
        if(CollectionUtils.isNotEmpty(lo.getAddList())){
            List<MechanismActivityRefDO> refDOList = Lists.newArrayList();
            lo.getAddList().stream().forEach(mid ->{
                MechanismActivityRefDO refDO = new MechanismActivityRefDO();
                refDO.setActivityId(activityId);
                refDO.setMechanismId(mid);
                refDOList.add(refDO);
            });
            activityMapper.insertMechanismActivityRefList(refDOList);
        }
        //删除
        if(CollectionUtils.isNotEmpty(lo.getDeleteList())){
            activityMapper.disassociateMechanismRefList(activityId,lo.getDeleteList());
        }
        return Result.ok(0);
    }

    /**
     * 取消活动关联机构
     * @param messageId
     * @param mechanismId
     * @return
     */
    @Override
    public Result disassociate(Integer messageId, Integer mechanismId) {
        activityMapper.disassociate(messageId,mechanismId);
        return Result.ok(0);
    }

    /**
     * 获取消息关联的机构列表
     * @param query
     * @return
     */
    @Override
    public PageInfo getRefMechanismList(BindMechanismQuery query) {
        try {
            query.setPageNo(query.getPageNo()==null?0:query.getPageNo());
            query.setPageSize(query.getPageSize()==null? Constant.PAGE_SIZE :query.getPageSize());
            if(StringUtils.isNotBlank(query.getName())){
                query.setName("%"+query.getName()+"%");
            }
            //使用分页插件,核心代码就这一行 #分页配置#
            Page page = PageHelper.startPage(query.getPageNo(), query.getPageSize());
            List<MechanismDTO> list = activityMapper.getRefMechanismList(query);
            PageInfo pageInfo = new PageInfo(list);
            return pageInfo;
        }catch (Exception e){
            log.error("{}", ExceptionUtils.getStackTrace(e));
        }
        return  null;
    }

    /**
     * 保存答题内容
     * @param param
     * @return
     */
    @Cacheable(prefix = "contentView",fieldKey = "#param.activityId",cacheOperation = Cacheable.CacheOperation.UPDATE)
    @Transactional(rollbackFor = Exception.class)
    @RepeatableCommit
    @Override
    public Result saveContent(ActivityContentParam param) {
        AtomicInteger atomicInteger = new AtomicInteger(0);
        if(CollectionUtils.isNotEmpty(param.getActivityContentRefDOList())){
            param.getActivityContentRefDOList().stream().forEach(ac->{
                ac.setActivityId(param.getActivityId());
                if(manual.equals(param.getManual())){
                    atomicInteger.addAndGet(ac.getQuestionNum());
                }
            });
        }
        if(manual.equals(param.getManual())){
            param.setQuestionNum(atomicInteger.get());
        }else {
            param.setManual(0);
        }
        activityMapper.updateActivityScoreAndNum(param);
        activityMapper.delActivityContent(param.getActivityId());
        activityMapper.insertActivityContentRefList(param.getActivityContentRefDOList());
        return Result.ok(0);
    }

    @Cacheable(prefix = "operationView",fieldKey = "#activityOperation.activityId",cacheOperation = Cacheable.CacheOperation.UPDATE)
    @Transactional(rollbackFor = Exception.class)
    @RepeatableCommit
    @Override
    public Result saveOperation(ActivityOperationRefDO activityOperation) {
        ActivityOperationRefDO old = activityMapper.getOperationByActivityId(activityOperation.getActivityId());
        if(old == null){
            activityMapper.insertOperation(activityOperation);
        }else {
            activityOperation.setId(old.getId());
            activityMapper.updateOperation(activityOperation);
            activityMapper.delPrizeDetail(activityOperation.getActivityId());
        }
        if(CollectionUtils.isNotEmpty(activityOperation.getPrizeDetailList())){
            Integer allPrizeNum =activityOperation.getPrizeDetailList().stream().mapToInt(ActivityPrizeDetailDO::getPrizeNum).sum();
            activityOperation.getPrizeDetailList().stream().forEach(p->{
                p.setActivityId(activityOperation.getActivityId());
                p.setPrizeType(PrizeTypeEnum.getEnumByType(p.getPrizeType()).getType());
                p.setPrizeWeight(SnowflakeIdWorker.calculatePercentageInt(p.getPrizeNum(),allPrizeNum));
                p.setSurplusPrizeNum(p.getPrizeNum());
            });
            activityMapper.insertPrizeDetailList(activityOperation.getPrizeDetailList());
            updateStockRedis(activityOperation.getActivityId());
        }
        return Result.ok(0);
    }

    /**
     * 参加活动(提交活动*消耗积分-清除缓存)
     * @param activityId
     * @param userId
     * @return
     */
//    @Cacheable(prefix = "join",fieldKey = "#activityId+'_'+#userId",expireTime = 60*60*12)
    @Override
    public Result join(Integer activityId, Integer userId) {
        Result allow = isWithinValidityPeriod(activityId);
        if(CodeEnum.FAILED.getValue().equals(allow.getCode())){
            log.info("抽奖-活动不在有效期内,activityId:{},userId:{}",activityId,userId);
            return allow;
        }
        //含 总题数 总分值 和分配规则
        ActivityOperationRefDO operationRefDO = activityMapper.getOperationByActivityId(activityId);
        Result pass = judgeJoinedTimes(activityId, userId, operationRefDO);
        if (CodeEnum.FAILED.getValue().equals(pass.getCode())) {
            log.info("抽奖-无法参加活动,activityId:{},userId:{},message:{}",activityId,userId,pass.getMessage());
            return pass;
        }
        List<ActivityContentRefDTO> contentList =  activityMapper.getActivityContent(activityId);
        if(CollectionUtils.isEmpty(contentList)){
            log.info("抽奖-未设置答题内容,activityId:{},userId:{}",activityId,userId);
            return Result.fail(ActivityErrorInfoEnum.JA_NOT_SET_CONTENT.getInfo());
        }
        List<QuestionDTO> questionList = randomQuestionListOfUserActivity(activityId,userId,contentList);
        List<QuestionDTO> resultlist = Lists.newArrayList();
        if(CollectionUtils.isNotEmpty(questionList)){
            if(ActivityManualTypeEnum.MANUAL.getType().equals(operationRefDO.getManual())){
                Map<Integer, List<QuestionDTO>> questionMap = getQuestionListMap(questionList);
                List<QuestionDTO> manuallist = Lists.newArrayList();
                //手动（总分值* 分类题数）
                contentList.stream().forEach(con->{
                    List<QuestionDTO> subCategoryQuestionList =  getShuffleQuestionList(con.getQuestionNum(), questionMap.get(con.getCategoryId()),activityId,userId);
                    log.info("抽奖-手工配置,activityId:{},userId:{},categoryId：{},题库数量:{},抽题数量:{}",activityId,userId,con.getCategoryId(),
                            subCategoryQuestionList==null?0:subCategoryQuestionList.size(),con.getQuestionNum());
                    manuallist.addAll(subCategoryQuestionList);
                });
                Collections.shuffle(manuallist);
                log.info("抽奖-手工配置抽题结果,activityId:{},userId:{},manuallist:{}",activityId,userId,manuallist);
                resultlist = manuallist;
            }else {
                //系统（总题数&总分值）
                resultlist = getShuffleQuestionList(operationRefDO.getQuestionNum(), questionList,activityId,userId);
                Collections.shuffle(resultlist);
                log.info("抽奖-系统配置,activityId:{},userId:{},题库数量:{},抽题数量:{}",activityId,userId,questionList.size(),operationRefDO.getQuestionNum());
            }
        }
        if(CollectionUtils.isNotEmpty(resultlist)){
            log.info("抽奖-加载选项,activityId:{},userId:{}",activityId,userId);
            List<Integer> questionIdList = resultlist.stream().map(QuestionDTO::getId).collect(Collectors.toList());
            Map<Integer, List<QuestionOptionDTO>> optionMap = getOptionListMap(questionIdList);
            resultlist.stream().forEach(question -> {
                List<QuestionOptionDTO> optionDTOList = optionMap.get(question.getId());
                if(CollectionUtils.isNotEmpty(optionDTOList)){
                    optionDTOList.stream().forEach(o->{
                        o.setCorrect(null);
                    });
                }
                question.setOptionDTOList(optionDTOList);
            });
        }
        initUserActivityWhenJoin(userId,activityId);
        return Result.ok(resultlist);
    }

    /**
     * 根据答题内容分类 查询题库所有题目
     * @param contentList
     * @return
     */
    private List<QuestionDTO> randomQuestionListOfUserActivity(Integer activityId, Integer userId,List<ActivityContentRefDTO> contentList) {
        String key = RedisKeyGenerator.getKey(applicationName,ActivityServiceImpl.class, RANDOM_QUESTION_LIST_OF_USER_ACTIVITY,activityId,userId);
        List<QuestionDTO> list = (List<QuestionDTO>) redisUtils.get(key);
        if(CollectionUtils.isEmpty(list)){
            List<Integer> categoryIdList = contentList.stream().map(ActivityContentRefDTO::getCategoryId).collect(Collectors.toList());
            list = questionMapper.getQuestionListByCategoryIdList(categoryIdList);
            if(CollectionUtils.isEmpty(list)){
                log.info("抽奖-题库无题,activityId:{},userId:{},categoryIdList:{}",activityId,userId,categoryIdList);
            }else {
                Random random = new Random();
                list.stream().forEach(q->{
                    q.setRandomIndex(random.nextInt(100000));
                });
            }
            Collections.shuffle(list);
            redisUtils.set(key,list,RedisUtils.TIME_MINUTE_10);
        }
        return list;
    }

    /**
     * 活动提交-计算得分
     * @param userAnswer
     * @return
     */
    @Override
    public UserActivityRefDO judgeScore(UserAnswerDTO userAnswer) {
        UserActivityRefDO userActivityRefDO = new UserActivityRefDO();
        userActivityRefDO.setTimes(userAnswer.getTimes());
        userActivityRefDO.setUserId(userAnswer.getUserId());
        userActivityRefDO.setActivityId(userAnswer.getActivityId());
        userActivityRefDO.setScore(0);
        if(CollectionUtils.isEmpty(userAnswer.getAnswerList())){
            return userActivityRefDO;
        }
        List<Integer> questionIdList = userAnswer.getAnswerList().stream().map(AnswerDTO::getQuestionId).collect(Collectors.toList());
        Map<Integer, AnswerDTO> correctAnswerAndScoreMap = getQuestionCorrectAnswerAndScoreMap(userAnswer.getActivityId(), questionIdList);
        AtomicInteger score = new AtomicInteger(0);
        AtomicInteger correctNum = new AtomicInteger(0);
        userAnswer.getAnswerList().forEach(us->{
            if(CollectionUtils.isNotEmpty(us.getUserOptionIdList())){
                us.getUserOptionIdList().sort((a, b) -> a.compareTo(b));
                us.setAnswer(StringUtils.join(us.getUserOptionIdList(),"_"));
            }else {
                us.setAnswer("0");
            }
            AnswerDTO currectAnswer = correctAnswerAndScoreMap.get(us.getQuestionId());
            if(currectAnswer!=null && StringUtils.isNotBlank(us.getAnswer())
                    && us.getAnswer().equals(currectAnswer.getAnswer())){
                score.addAndGet(currectAnswer.getScore());
                correctNum.getAndIncrement();
                log.info("正确：questionId:{},用户答案:{},正确答案：{},分值：{}",
                        us.getQuestionId(),us.getAnswer(),currectAnswer.getAnswer(),currectAnswer.getScore());
            }else {
                log.info("错误：questionId:{},用户答案:{},正确答案：{}",us.getQuestionId(),us.getAnswer(),currectAnswer.getAnswer());
            }
        });
        userActivityRefDO.setScore(score.get());
        userActivityRefDO.setCorrectNum(correctNum.get());
        log.info("总得分:{},题目数量:{}",userActivityRefDO.getScore(),questionIdList.size());
        return userActivityRefDO;
    }

    @Override
    public ActivityOperationVO getUserActivityOperation(Integer activityId, Integer userId) {
        ActivityOperationVO ao =  new ActivityOperationVO();
        ActivityOperationRefDO option= activityMapper.getOperationByActivityId(activityId);
        ao.setActivityId(option.getActivityId());
        ao.setCountry(option.getCountry());
        ao.setClose(option.getLotteryClose());
        ao.setLibrary(option.getLibrary());
        ao.setLotteryDescription(option.getLotteryDescription());
        ao.setShowBarrage(option.getShowBarrage());
        UserLotteryDO ul = activityMapper.findUserLotteryByActivityId(userId,activityId);
        if(ul!=null){
            ao.setAvailableLotteryNum(ul.getLotteryNum() - ul.getJoinedNum());
        }
        return ao;
    }

    /**
     * 参加活动-提交
     * @param userActivityRefDO
     * @return
     */
    @Cacheable(prefix = "join",fieldKey = "#userActivityRefDO.activityId+'_'+#userActivityRefDO.userId",cacheOperation = Cacheable.CacheOperation.UPDATE)
    @Transactional(rollbackFor = Exception.class)
    @RepeatableCommit
    @Override
    public Result submit(UserActivityRefDO userActivityRefDO) {
        Integer activityId = userActivityRefDO.getActivityId();
        Integer userId = userActivityRefDO.getUserId();
        ActivityOperationRefDO operationRefDO= activityMapper.getOperationByActivityId(activityId);
        userActivityRefDO.setScore(userActivityRefDO.getScore() == null ?0:userActivityRefDO.getScore());
        userActivityRefDO.setTimes(userActivityRefDO.getTimes() == null ?0:userActivityRefDO.getTimes());
        Date date = DateTimeUtil.parse(DateTimeUtil.format(new Date(),DateTimeUtil.DATE_FORMAT_YYYYMMDD));
        UserActivityDetailRefDO detail = new UserActivityDetailRefDO();
        detail.setScore(userActivityRefDO.getScore());
        detail.setTimes(userActivityRefDO.getTimes());
        detail.setSignDate(date);
        UserActivityRefDTO old = activityMapper.findUserActivityRefDTO(userId,activityId);
        Integer highestScore =old.getScore();
        Integer highestScoreTimes =old.getTimes();
        if(old == null){
            userActivityRefDO.setComplete(1);
            activityMapper.insertUserActivityRef(userActivityRefDO);
            detail.setUserActivityId(userActivityRefDO.getId());
        }else {
            if(old.getScore() < userActivityRefDO.getScore()
                    ||(old.getScore().equals(userActivityRefDO.getScore()) && old.getTimes() > userActivityRefDO.getTimes())){
                //更新最好成绩
                activityMapper.updateScoreAndTimes(userActivityRefDO.getScore(),userActivityRefDO.getTimes(),old.getId());
                highestScore =userActivityRefDO.getScore();
                highestScoreTimes =userActivityRefDO.getTimes();
            }else if(!COMPLETE.equals(old.getComplete())){
                activityMapper.completeUserActivity(old.getId());
            }
            detail.setUserActivityId(old.getId());
        }
        UserActivityDetailRefDO oldDetail =  activityMapper.getLastDetailByUserActivityId(detail.getUserActivityId());
        if(oldDetail != null && TIMES_NOT_SUBMIT.equals(oldDetail.getTimes())){
            detail.setId(oldDetail.getId());
            activityMapper.updateUserActivityDetail(detail);
        }

        //活动积分
        judgeCheckinTimes(activityId, userId, operationRefDO);
        ActivitySubmitResultVO submitResultVO = new ActivitySubmitResultVO();
        // 2。答题后直接去抽取的前提： 1打开抽奖  2正确题数超过设置规则,累计一次抽奖次数
        submitResultVO.setAddLotteryTimes(0);
        if(LOTTERY.equals(operationRefDO.getLotteryClose()) && userActivityRefDO.getCorrectNum()>=operationRefDO.getLotteryTriggerRules()){
            UserLotteryDO ul = activityMapper.findUserLotteryByActivityId(userId,activityId);
            Integer joinedNum = ul == null || ul.getJoinedNum() ==null ?0:ul.getJoinedNum();
            if(operationRefDO.getLotteryTimes()>joinedNum){
                addUserLotteryTimes( activityId, userId,userActivityRefDO.getCorrectNum(), operationRefDO.getLotteryTriggerRules());
                submitResultVO.setAddLotteryTimes(1);
            }
        }
        //更新排行榜
        addUserToRankList(userActivityRefDO.getMechanismId(),activityId, userId,highestScore,highestScoreTimes);
        //最好成绩
        submitResultVO.setHighestScore(highestScore);
        submitResultVO.setHighestScoreTimes(highestScoreTimes);
        submitResultVO.setScore(userActivityRefDO.getScore());
        submitResultVO.setTimes(userActivityRefDO.getTimes());
        return Result.ok(submitResultVO);
    }
    /**
     * 用户参加完活动实时更新榜单
     * @param mechanismId
     * @param activityId
     */
    private void addUserToRankList(Integer mechanismId,Integer activityId,Integer userId,Integer score,Integer times){
        String mechRankingKey=getHreoCacheKey(mechanismId,activityId,RankingLocalTypeEnum.GUAN_NEI.getType());
        double scoreTimes = redisUtils.getScoreTtimes(score,times);
        redisUtils.zadd(mechRankingKey,userId,scoreTimes);
        String quanguoRankingKey=getHreoCacheKey(mechanismId,activityId,RankingLocalTypeEnum.QUAN_GUO.getType());
        redisUtils.zadd(quanguoRankingKey,userId,scoreTimes);
    }

    /**
     * 答题正确率超过指定量，累计一次抽奖次数
     * @param activityId
     * @param userId
     * @param correctNum  用户答对题数
     * @param lotteryTriggerRules 累计抽奖次数标准
     */
    private void addUserLotteryTimes( Integer activityId, Integer userId,Integer correctNum ,Integer lotteryTriggerRules) {
        if(correctNum >=lotteryTriggerRules){
            UserLotteryDO oldUl = activityMapper.findUserLotteryByActivityId(userId,activityId);
            if(oldUl == null ){
                UserLotteryDO ul = new UserLotteryDO();
                ul.setActivityId(activityId);
                ul.setUserId(userId);
                ul.setJoinedNum(0);
                ul.setLotteryNum(1);
                activityMapper.insertUserLottery(ul);
            }else {
                //抽奖次数加一
                activityMapper.updateLotteryNum(oldUl.getId(),oldUl.getLotteryNum()+1);
            }
        }
    }

    /**
     * 封装题目 正确答案和分值
     * @param activityId
     * @param questionIdList
     * @return  正确答案： 选项ID1_选项ID2_选项ID3 (多选 ID 正序排序)
     */
    private Map<Integer, AnswerDTO> getQuestionCorrectAnswerAndScoreMap( Integer activityId,List<Integer> questionIdList) {
        Map<Integer, AnswerDTO> questionCorrectAnswerAndScoreMap = Maps.newHashMap();
        Integer lastQuestionId = questionIdList.get(questionIdList.size()-1);
        ActivityDTO activityDO =view(activityId);
        log.info("活动ID：{},活动总分:{}",activityId,activityDO.getTotalScore());
        Integer avg = activityDO.getTotalScore() / questionIdList.size();
        Integer mod = activityDO.getTotalScore() % questionIdList.size();
        Map<Integer, List<QuestionOptionDTO>> optionMap = getOptionListMap(questionIdList);
        questionIdList.stream().forEach(questionId ->{
            AnswerDTO answer = new AnswerDTO();
            answer.setQuestionId(questionId);
            String answerStr="";
            List<QuestionOptionDTO> optionList = optionMap.get(questionId);
            if(CollectionUtils.isNotEmpty(optionList)){
                List<Integer> correctOptionIdList = optionList.stream()
                        .filter(o ->CORRECT_VALUE.equals(o.getCorrect()) )
                        .map(QuestionOptionDTO::getId).collect(Collectors.toList());
                correctOptionIdList.sort((a, b) -> a.compareTo(b));
                answerStr = StringUtils.join(correctOptionIdList,"_");
            }
            answer.setScore(avg);
            if(lastQuestionId.equals(questionId)){
                answer.setScore(avg+mod);
            }
            answer.setAnswer(answerStr);
            questionCorrectAnswerAndScoreMap.put(questionId,answer);
        });
        return questionCorrectAnswerAndScoreMap;
    }

    /**
     * 判断活动是否在有效期内
     * @param activityId
     * @return
     */
    private Result isWithinValidityPeriod(Integer activityId) {
        ActivityDTO activityDO =view(activityId);
        if(activityDO == null || DateTimeUtil.isBeforeNow(activityDO.getEndTime())  ){
            return Result.fail(ActivityErrorInfoEnum.JA_END.getInfo());
        }
        if(DateTimeUtil.isAfterNow(activityDO.getBeginTime())){
            return Result.fail(ActivityErrorInfoEnum.JA_NO_START.getInfo());
        }
        return Result.ok(0);
    }

    /**
     * 进入活动时 初始化用户记录
     * @param userId
     * @param activityId
     */
    private void initUserActivityWhenJoin(Integer userId, Integer activityId) {
        UserActivityRefDTO old = activityMapper.findUserActivityRefDTO(userId,activityId);
        Integer userActivityId=0;
        if(old == null){
            UserActivityRefDO init = new UserActivityRefDO();
            init.setActivityId(activityId);
            init.setUserId(userId);
            init.setTimes(0);
            init.setScore(0);
            init.setComplete(0);
            activityMapper.insertUserActivityRef(init);
            userActivityId = init.getId();
        }else {
            userActivityId = old.getId();
        }
        UserActivityDetailRefDO detail = new UserActivityDetailRefDO();
        detail.setUserActivityId(userActivityId);
        detail.setScore(0);
        Date today = DateTimeUtil.parse(DateTimeUtil.format(new Date(),DateTimeUtil.DATE_FORMAT_YYYYMMDD));
        detail.setSignDate(today);
        // -1  表示进入活动没提交
        detail.setTimes(TIMES_NOT_SUBMIT);
        activityMapper.insertUserActivityDetail(detail);
    }
    /**
     * 排行榜
     * @param query
     * @return
     */
    @Override
    public Result rankingList(ActivityRankingQuery query) {
        Integer top = query.getTop() == null ?100:query.getTop();
        String herokey = getHreoCacheKey(query.getMechanismId(),query.getActivityId(),query.getType());
        Long count = redisUtils.zcard(herokey);
        if(count<1){
            initRanking(query.getMechanismId(),query.getActivityId(),query.getType());
        }
        Set set = redisUtils.reverseRangeWithScores(herokey,0,top-1);
        if(CollectionUtils.isEmpty(set)){
            return Result.ok(new HeroListVO());
        }
        Map<Integer,HeroRankingVO> rankUserMap = Maps.newHashMap();
        List<Integer> rankUserIdList = Lists.newArrayList();
        AtomicLong rankIndex = new AtomicLong(0);
        set.stream().forEach(s->{
            HeroRankingVO user = new HeroRankingVO();
            ZSetOperations.TypedTuple kv = (ZSetOperations.TypedTuple) s;
            user.setRank(rankIndex.incrementAndGet());
            user.setUserId((Integer) kv.getValue());
            int score = redisUtils.getScore(kv.getScore().intValue());
            user.setScore(score);
            rankUserIdList.add(user.getUserId());
            rankUserMap.put(user.getUserId(),user);
        });
        List<Integer> queryIdList = Lists.newArrayList(rankUserIdList);
        queryIdList.add(query.getUserId());
        Map<Integer,HeroRankingVO> userInfoMap = activityMapper.rankingListByTopUserIdList(query.getActivityId(),queryIdList);
        List<HeroRankingVO> userList = Lists.newArrayList();
        AtomicBoolean inint = new AtomicBoolean(false);
        rankUserIdList.stream().forEach(userId->{
            HeroRankingVO user = userInfoMap.get(userId);
            HeroRankingVO rank = rankUserMap.get(userId);
            if(user != null && rank != null){
                user.setRank(rank.getRank());
                user.setScore(rank.getScore());
                userList.add(user);
            }else {
                inint.set(true);
            }
        });
        if(inint.get()){
            initRanking(query.getMechanismId(),query.getActivityId(),query.getType());
        }
        HeroListVO heroListVO = new HeroListVO();
        heroListVO.setRankingList(userList);

        HeroRankingVO loggedUser =userInfoMap.get(query.getUserId());
        Long  lgrank = redisUtils.zreverseRank(herokey,query.getUserId());
        if(lgrank!=null){
            loggedUser.setOnlist(lgrank<=top);
            loggedUser.setUserId(query.getUserId());
            loggedUser.setRank(lgrank+1);
            heroListVO.setLoggedUser(loggedUser);
        }
        return Result.ok(heroListVO);
    }

    /**
     * 获取排行榜key
     * @param mechanismId  机构ID
     * @param activityId   活动ID
     * @param type         0全国 1馆内排行
     * @return
     */
    @Override
    public String getHreoCacheKey(Integer mechanismId, Integer activityId, Integer type) {
        if(RankingLocalTypeEnum.QUAN_GUO.getType().equals(type)){
            mechanismId = RankingLocalTypeEnum.QUAN_GUO.getType();
        }
        return RedisKeyGenerator.getKey(applicationName, ActivityServiceImpl.class, RANKING_LIST,mechanismId,activityId,type);
    }

    /**
     * 初始化榜单
     * @param mechanismId
     * @param activityId
     * @param type
     */
    @Override
    public void initRanking(Integer mechanismId,Integer activityId,Integer type){
        String mechRankingKey=getHreoCacheKey(mechanismId,activityId,RankingLocalTypeEnum.GUAN_NEI.getType());
        String quanguoRankingKey=getHreoCacheKey(mechanismId,activityId,RankingLocalTypeEnum.QUAN_GUO.getType());
        Long delMNum = redisUtils.removeRange(mechRankingKey,0,-1);
        Long delQMun = redisUtils.removeRange(quanguoRankingKey,0,-1);
        log.info("初始化榜单:delMNum:{},delQMun:{}",delMNum,delQMun);
        ActivityRankingQuery query = new ActivityRankingQuery();
        query.setMechanismId(mechanismId);
        query.setActivityId(activityId);
        query.setType(type);
        List<HeroRankingVO> list = activityMapper.rankingList(query);
        if(CollectionUtils.isNotEmpty(list)){
            list.stream().forEach(u->{
                double scoreTimes = redisUtils.getScoreTtimes(u.getScore(),u.getTimes());
                if(mechanismId.equals(u.getMechanismId())){
                    redisUtils.zadd(mechRankingKey,u.getUserId(),scoreTimes);
                }
                redisUtils.zadd(quanguoRankingKey,u.getUserId(),scoreTimes);
            });
        }
    }

    /**
     * 抽奖
     *   1。后台关闭抽奖， 屏蔽活动抽奖入口，答题后不累计抽奖次数
     *   2。答题后直接去抽取的前提： 1打开抽奖  2正确题数超过设置规则
     * @param userId
     * @param activityId
     * @return
     */
//    @Cacheable(prefix = "lottery",fieldKey = "#userId+'_'+#activityId",expireTime = 20)
    @Transactional(rollbackFor = Exception.class)
    @RepeatableCommit
    @Override
    public Result lottery(Integer userId, Integer activityId) {
        //redis锁
        String keyLock = userId+"_user_activity_"+activityId;
        String token = redisLock.tryLock(keyLock,5000);
        Result result;
        try {
            ActivityOperationRefDO option= activityMapper.getOperationByActivityId(activityId);
            UserLotteryDO ul = activityMapper.findUserLotteryByActivityId(userId,activityId);
            Integer lotteryTimes = ul == null ?0: ul.getLotteryNum() - ul.getJoinedNum();
            if(lotteryTimes <=0 || option.getLotteryTimes() <= ul.getJoinedNum()){
                return Result.fail(ActivityErrorInfoEnum.LO_NO_TIMES.getInfo());
            }

            if( !LOTTERY.equals(option.getLotteryClose())){
                return Result.fail(ActivityErrorInfoEnum.LO_CLOSE.getInfo());
            }
            if(StringUtils.isNotBlank(token)){
                List<ActivityPrizeDetailDTO> prizeList =getActivityPrizeList(activityId);
                if(CollectionUtils.isEmpty(prizeList)){
                    return Result.fail(ActivityErrorInfoEnum.LO_NOT_SET_PRIZE.getInfo());
                }
                try {
                    //抽奖实现
                    Result resultLottery = lotterying(prizeList, activityId,userId,ul);
                    //抽奖更新库存异常,恢复库存
                    if(CodeEnum.FAILED.getValue().equals(resultLottery.getCode())){
                        return resultLottery;
                    }
                    ActivityPrizeDetailDTO prize = (ActivityPrizeDetailDTO)resultLottery.getData();
                    if(prize == null){
                        prize = new ActivityPrizeDetailDTO();
                        prize.setPrizeType(PrizeTypeEnum.THANKS.getType());
                        //奖品抽完，返回 谢谢参与
                        prize.setId(-1);
                    }
                    Map<String,Object> map =Maps.newHashMap();
                    map.put("prizeId",prize.getId());
                    map.put("prizeName",prize.getPrizeName());
                    map.put("prizeType",prize.getPrizeType());
                    result = Result.ok(map);
                }catch (Exception e){
                    result = Result.fail(ExceptionUtils.getStackTrace(e));
                    throw new RuntimeException();
                }
            }else{
                return Result.fail("请等待,抽奖中");
            }
        }catch (Exception e){
            result = Result.fail(ExceptionUtils.getStackTrace(e));
            throw new RuntimeException(e);
        }finally {
            redisLock.unlock(keyLock,token);
        }
        return result;
    }

    /**
     * 更新用户数据(消耗金币,减少用户抽奖次数,保存用户获奖数据)
     * @param userId
     * @param ul
     * @param prize
     * @return
     */
    private Result handleUserData(Integer userId,UserLotteryDO ul,ActivityPrizeDetailDTO prize) {
        Result result = null;
        try {
            if(PrizeTypeEnum.INTEGRAL.getType().equals(prize.getPrizeType())){
                Integer score = NumberUtils.isDigits(prize.getPrizeName()) ? Integer.parseInt(prize.getPrizeName()) : 0;
                checkinService.checkIn(userId,new Date(),CheckinTypeEnum.LOTTERY.getType(),ul.getActivityId(),score);
            }
            //修改用户抽奖次数
            activityMapper.updateJoinedNum(ul.getId(),ul.getJoinedNum()+1);
            // 保存用户奖品
            UserActivityPrizeDetailRefDO userPrize = new UserActivityPrizeDetailRefDO();
            userPrize.setUserId(userId);
            userPrize.setActivityPrizeDetailId(prize.getId());
            activityMapper.insertUserPrizie(userPrize);
            result = Result.ok(prize);
        }catch (Exception e){
            result = Result.fail("更新用户数据失败");
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 抽奖实现
     * @param prizeList
     * @return
     */
    private Result lotterying(List<ActivityPrizeDetailDTO> prizeList,Integer activityId,Integer userId,UserLotteryDO ul) {
        Result<ActivityPrizeDetailDTO> result = Result.ok(null);
        //得到剩余库存最大值,用于计算随机数
        int maxPrizesNum = 0;
        //计算剩余库存最大值,及设置中奖范围最大最小值
        List<ActivityPrizeDetailDTO> prizeSurplusList = new ArrayList<>();
        for (ActivityPrizeDetailDTO p : prizeList) {
            Integer surplusPrizeNum = (Integer)redisUtils.incrementScore(ACTIVITY_PRIZE_DETAIL+activityId,SURPLUS_PRIZE+p.getId(),0L).intValue();
            if (surplusPrizeNum==null||surplusPrizeNum<=0) {
                continue;
            }
            p.setPrizeRangeMin(maxPrizesNum + 1);
            maxPrizesNum+=surplusPrizeNum;
            p.setPrizeRangeMax(maxPrizesNum);
            prizeSurplusList.add(p);
        }
        //所有奖品抽完,库存全部为0,则返回无奖品
        if (CollectionUtils.isEmpty(prizeSurplusList)) {
            ActivityPrizeDetailDTO prize = new ActivityPrizeDetailDTO();
            prize.setPrizeType(PrizeTypeEnum.THANKS.getType());
            //奖品抽完，返回 谢谢参与
            prize.setId(-1);
            return handleUserData(userId,ul,prize);
        }
        Random r = new Random(100000);
        int randomVal = r.nextInt(maxPrizesNum);
        randomVal = randomVal==0?randomVal+1:randomVal;
        for (ActivityPrizeDetailDTO dto : prizeSurplusList) {
            Integer surplusPrize = (Integer) redisUtils.incrementScore(ACTIVITY_PRIZE_DETAIL+activityId,SURPLUS_PRIZE+dto.getId(),0L).intValue();
            if(surplusPrize!=null&&surplusPrize<=0){
                continue;
            }
            if (randomVal >= dto.getPrizeRangeMin() && randomVal<=dto.getPrizeRangeMax()) {
                //库存减1
                Integer surplusPrizeNum = (Integer)redisUtils.incrementScore(ACTIVITY_PRIZE_DETAIL+activityId,SURPLUS_PRIZE+dto.getId(),-1L).intValue();
                if(surplusPrizeNum!=null&&surplusPrizeNum>=0){
                    try {
                        //数据库剩余奖品数量库存减1
                        activityMapper.activityPrizeDetailSurplusNumDecrOne(dto.getId());
                        result = handleUserData(userId,ul,dto);
                    }catch (Exception e){
                        redisUtils.incrementScore(ACTIVITY_PRIZE_DETAIL+activityId,SURPLUS_PRIZE+dto.getId(),1L);
                        result=  new Result(CodeEnum.FAILED.getValue(), "更新库存失败", dto);
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
        return result;
    }

    /**
     * 答题获取抽奖机会 不在消耗积分
     * @param userId
     * @param score
     * @return
     */
    public Result consumeGoldCoins(Integer userId, Integer score) {
        Integer coins = checkinMapper.getUserCoins(userId);
        if(coins == null || coins <score){
            coins = coins == null?0:coins;
            return Result.fail("您的金豆不足");
        }
        Date  date = DateTimeUtil.parse(DateTimeUtil.format(new Date(),DateTimeUtil.DATE_FORMAT_YYYYMMDD));
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

    /**
     * 活动奖品列表
     * @param activityId
     * @return
     */
    @Override
    public List<ActivityPrizeDetailDTO> getActivityPrizeList(Integer activityId) {
        String key = RedisKeyGenerator.getKey(applicationName,ActivityServiceImpl.class, GET_ACTIVITY_PRIZE_LIST,activityId);
        List<ActivityPrizeDetailDTO> list = (List<ActivityPrizeDetailDTO>) redisUtils.get(key);
        if(CollectionUtils.isEmpty(list)){
            list = activityMapper.getActivityPrizeList(activityId);
            redisUtils.set(key,list, RedisTimeConstant.MINUTE_10);
        }
        return list;
    }

    /**
     * 我的活动奖品列表
     * @param userId
     * @param activityId
     * @return
     */
    @Cacheable(prefix = "getMyActivityPrizeList",fieldKey = "#userId+'_'+#activityId",expireTime = 5)
    @Override
    public Result getMyActivityPrizeList(Integer userId, Integer activityId) {
        List<ActivityPrizeDetailDTO> prizelist = activityMapper.getMyActivityPrizeList(userId,activityId);
        return Result.ok(prizelist);
    }

    /**
     * 活动中奖名单
     * @param userId
     * @param activityId
     * @return
     */
    @Cacheable(prefix = "getWinLotteryuserlist",fieldKey = "#activityId",expireTime = 5)
    @Override
    public Result getWinLotteryuserlist(Integer userId, Integer activityId) {
        List<ActivityWinLotteryUserVO> list = activityMapper.getWinLotteryuserlist(activityId);
        //脱敏处理 todo
        return Result.ok(list);
    }

    /**
     * 判断活动参加次数是否符合条件
     * @param activityId
     * @param userId
     * @param operationRefDO
     * @return
     */
    private Result judgeJoinedTimes(Integer activityId, Integer userId, ActivityOperationRefDO operationRefDO) {
        if(operationRefDO == null || operationRefDO.getJoinTimes() == null ){
            return Result.fail(ActivityErrorInfoEnum.JA_NOT_SET_OPERATION.getInfo());
        }
        Integer joinedTimes =0;
        String info =ActivityErrorInfoEnum.JA_TODAY_NO_TIMES.getInfo();
        if(ActivityJoinFrequencyTypeEnum.PER_DAY.getType().equals(operationRefDO.getJoinFrequency())){
            //当天次数
            Date today = DateTimeUtil.parse(DateTimeUtil.format(new Date(),DateTimeUtil.DATE_FORMAT_YYYYMMDD));
            joinedTimes = activityMapper.getPerDayJoinedtimes(userId,activityId,today);
        }else {
            joinedTimes = activityMapper.getAllJoinedTimes(userId,activityId);
            info =ActivityErrorInfoEnum.JA_ALL_NO_TIMES.getInfo();
        }
        if(joinedTimes >= operationRefDO.getJoinTimes() ){
            return Result.fail(info);
        }
        return Result.ok(0);
    }

    /**
     * 判断活动签到次数是否符合条件
     * @param activityId
     * @param userId
     * @param operationRefDO
     * @return
     */
    private Result judgeCheckinTimes(Integer activityId, Integer userId, ActivityOperationRefDO operationRefDO) {
        if(operationRefDO == null || operationRefDO.getIntegralTimes() == null ){
            log.info("活动积分次数达到上限,activityId:{},userId:{}",activityId,userId);
            return Result.fail("请设置活动运营规则");
        }
        Integer checkinTimes =0;
        if(ActivityJoinFrequencyTypeEnum.PER_DAY.getType().equals(operationRefDO.getIntegralFrequency())){
            //当天次数
            Date today = DateTimeUtil.parse(DateTimeUtil.format(new Date(),DateTimeUtil.DATE_FORMAT_YYYYMMDD));
            checkinTimes = checkinMapper.isSignObjectToday(userId,today, CheckinTypeEnum.ACTIVITY.getType(),activityId);
        }else {
            checkinTimes = checkinMapper.isSignObjectAll(userId, CheckinTypeEnum.ACTIVITY.getType(),activityId);
        }
        if(checkinTimes >= operationRefDO.getIntegralTimes() ){
            log.info("活动积分次数达到上限,activityId:{},userId:{}",activityId,userId);
            return Result.fail("活动积分次数达到上限");
        }
        Date  date = DateTimeUtil.parse(DateTimeUtil.format(new Date(),DateTimeUtil.DATE_FORMAT_YYYYMMDD));
        checkinService.checkIn(userId,date,CheckinTypeEnum.ACTIVITY.getType(),activityId,operationRefDO.getIntegral());
        return Result.ok(0);
    }

    /**
     * 查询选项
     * @param questionIdList
     * @return
     */
    private Map<Integer, List<QuestionOptionDTO>> getOptionListMap(List<Integer> questionIdList) {
        Map<Integer,List<QuestionOptionDTO> > optionMap = Maps.newHashMap();
        List<QuestionOptionDTO> optionList = questionMapper.getOptionListByQuestionIdList(questionIdList);
        if(CollectionUtils.isNotEmpty(optionList)){
            optionList.stream().forEach(option->{
                List<QuestionOptionDTO> subList =optionMap.get(option.getQuestionId());
                if(CollectionUtils.isEmpty(subList)){
                    subList = Lists.newArrayList();
                }
                subList.add(option);
                optionMap.put(option.getQuestionId(),subList);
            });
        }
        return optionMap;
    }

    /**
     * 分类 -》 question list
     * @param questionList
     * @return
     */
    private Map<Integer, List<QuestionDTO>> getQuestionListMap(List<QuestionDTO> questionList) {
        Map<Integer,List<QuestionDTO>> questionMap = Maps.newHashMap();
        if(CollectionUtils.isEmpty(questionList)){
            return questionMap;
        }
        questionList.stream().forEach(questionDTO->{
            if(StringUtils.isNotBlank(questionDTO.getCategoryIds())){
                List<Integer> categoryIdList = Splitter.on(",").trimResults().splitToList(questionDTO.getCategoryIds())
                        .stream().map(Integer::parseInt).collect(Collectors.toList());
                if(CollectionUtils.isNotEmpty(categoryIdList)){
                    categoryIdList.stream().forEach(categoryId->{
                        List<QuestionDTO> subList =questionMap.get(categoryId);
                        if(CollectionUtils.isEmpty(subList)){
                            subList = Lists.newArrayList();
                        }
                        subList.add(questionDTO);
                        questionMap.put(categoryId,subList);
                    });
                }
            }
        });
        return questionMap;
    }

    /**
     *  抽题
     * @param num
     * @param questionList
     * @param activityId
     * @param userId
     * @return
     */
    private List<QuestionDTO> getShuffleQuestionList(Integer num, List<QuestionDTO> questionList,Integer activityId, Integer userId) {
        if(CollectionUtils.isEmpty(questionList)){
            return Lists.newArrayList();
        }
        List<SortField> sortFieldList=new ArrayList<SortField>();
        SortField weightsSortField =new SortField();
        weightsSortField.setSortName("weights");
        weightsSortField.setAsc(true);
        SortField indexSortField =new SortField();
        indexSortField.setSortName("randomIndex");
        sortFieldList.add(weightsSortField);
        sortFieldList.add(indexSortField);
        ListUtil.sortList(questionList, sortFieldList);
        List sublist = Lists.newArrayList();
        if(questionList.size() <= num){
            sublist = questionList;
        }else {
            sublist.addAll(questionList.subList(0,num));
        }
        changeQuestionWeights(activityId, userId,sublist);
        return sublist;
    }

    /**
     * 修改抽中题的权重
     * @param activityId
     * @param userId
     * @param sublist
     */
    private void changeQuestionWeights(Integer activityId, Integer userId, List<QuestionDTO> sublist) {
        String key = RedisKeyGenerator.getKey(applicationName,ActivityServiceImpl.class, RANDOM_QUESTION_LIST_OF_USER_ACTIVITY,activityId,userId);
        List<QuestionDTO> allList = (List<QuestionDTO>) redisUtils.get(key);
        if(CollectionUtils.isNotEmpty(allList) && CollectionUtils.isNotEmpty(sublist)){
            Map<Integer,QuestionDTO> map = Maps.newConcurrentMap();
            allList.stream().forEach(q->{
                map.put(q.getId(),q);
            });
            sublist.stream().forEach(sub->{
                QuestionDTO questionDTO = map.get(sub.getId());
                questionDTO.setWeights(questionDTO.getWeights()+1);
            });
            redisUtils.set(key,allList,RedisUtils.TIME_MINUTE_10);
        }
    }

    private Map<Integer, CategoryDTO> getCategoryIdList(List<ActivityDTO> list) {
        List<Integer> categoryIdList = Lists.newArrayList();
        List<Integer> shapeIdList = list.stream().map(ActivityDTO::getShape).collect(Collectors.toList());
        List<Integer> typeIdList = list.stream().map(ActivityDTO::getType).collect(Collectors.toList());
        List<Integer> ageIdList = list.stream().map(ActivityDTO::getAge).collect(Collectors.toList());
        categoryIdList.addAll(shapeIdList);
        categoryIdList.addAll(typeIdList);
        categoryIdList.addAll(ageIdList);

        Map<Integer, CategoryDTO> map = categoryMapper.getCategoryMap(categoryIdList);
        if(map == null){
            map = Maps.newHashMap();
        }
        return map;
    }

    /**
     * 用户参加活动时设置抽奖库存缓存
     * @param activityId
     */
    @Override
    public void setStockRedis(Integer activityId) {
        String setKey = ACTIVITY_PRIZE_DETAIL+activityId;
        long size = redisUtils.zcard(setKey);
        if (size==0) {
            List<ActivityPrizeDetailDTO> activityPrizeDetailList = getActivityPrizeList(activityId);
            if(CollectionUtils.isNotEmpty(activityPrizeDetailList)){
                for (ActivityPrizeDetailDTO dto : activityPrizeDetailList) {
                    String key = SURPLUS_PRIZE+dto.getId();
                    redisUtils.zadd(setKey,key,dto.getSurplusPrizeNum());
                }
            }
        }
    }

    /**
     * 后台设置抽奖库存缓存（列表不要加缓存，运营修改奖品时调此方法刷新缓存）
     * @param activityId
     */
    public void updateStockRedis(Integer activityId) {
        String setKey = ACTIVITY_PRIZE_DETAIL+activityId;
        redisUtils.del(setKey);
        String listKey = RedisKeyGenerator.getKey(applicationName,ActivityServiceImpl.class, GET_ACTIVITY_PRIZE_LIST,activityId);
        redisUtils.del(listKey);
        List<ActivityPrizeDetailDTO> activityPrizeDetailList = activityMapper.getActivityPrizeList(activityId);
        if(CollectionUtils.isNotEmpty(activityPrizeDetailList)){
            for (ActivityPrizeDetailDTO dto : activityPrizeDetailList) {
                String key = SURPLUS_PRIZE+dto.getId();
                redisUtils.zadd(setKey,key,dto.getSurplusPrizeNum());
            }
        }
    }

    /**
     * 恢复抽奖库存缓存
     * @return
     */
    @Override
    public Result recoveryLotteryRedis() {
        List<ActivityPrizeDetailDTO> prizeDetailList = activityMapper.allEffectivePrizeDetail(new Date(),0);
        for (ActivityPrizeDetailDTO dto : prizeDetailList) {
            redisUtils.zadd(ACTIVITY_PRIZE_DETAIL+dto.getActivityId(),SURPLUS_PRIZE+dto.getId(),dto.getSurplusPrizeNum());
        }
        return Result.ok("成功");
    }

    public static void main(String[] args) {
        List<ActivityPrizeDetailDTO> prizeList = new ArrayList<>();
        ActivityPrizeDetailDTO d1 = new ActivityPrizeDetailDTO();
        d1.setSurplusPrizeNum(1);
        prizeList.add(d1);
        ActivityPrizeDetailDTO d2 = new ActivityPrizeDetailDTO();
        d2.setSurplusPrizeNum(1);
        prizeList.add(d2);
        ActivityPrizeDetailDTO d3 = new ActivityPrizeDetailDTO();
        d3.setSurplusPrizeNum(1);
        prizeList.add(d3);
        //得到剩余库存最大值,用于计算随机数
        int maxPrizesNum = 0;
        //计算剩余库存最大值,及设置中奖范围最大最小值
        for (ActivityPrizeDetailDTO p : prizeList) {
            ActivityPrizeDetailDTO dto = p;
            dto.setPrizeRangeMin(maxPrizesNum + 1);
            maxPrizesNum+=dto.getSurplusPrizeNum();
            dto.setPrizeRangeMax(maxPrizesNum);
        }
        Random r = new Random(maxPrizesNum);
        int randomVal = r.nextInt(maxPrizesNum);
        System.out.println(JSON.toJSONString(prizeList));
        System.out.println(randomVal);
        for (ActivityPrizeDetailDTO p : prizeList) {
            ActivityPrizeDetailDTO dto = p;
            if(dto.getSurplusPrizeNum()<=0){
                continue;
            }
            if (randomVal >= dto.getPrizeRangeMin() && randomVal<=dto.getPrizeRangeMax()) {
                dto.setSurplusPrizeNum(dto.getSurplusPrizeNum()-1<0?dto.getSurplusPrizeNum():dto.getSurplusPrizeNum()-1);
                dto.setPrizeRangeMin(dto.getPrizeRangeMin()-1<0?dto.getPrizeRangeMin():dto.getPrizeRangeMin()-1);
                dto.setPrizeRangeMax(dto.getPrizeRangeMax()-1<0?dto.getPrizeRangeMax():dto.getPrizeRangeMax()-1);
            }else if (randomVal<=dto.getPrizeRangeMin()) {
                dto.setPrizeRangeMin(dto.getPrizeRangeMin()-1<0?dto.getPrizeRangeMin():dto.getPrizeRangeMin()-1);
                dto.setPrizeRangeMax(dto.getPrizeRangeMax()-1<0?dto.getPrizeRangeMax():dto.getPrizeRangeMax()-1);
            }
        }
        System.out.println(JSON.toJSONString(prizeList));
    }

    /**
     * 报名参加临时活动
     * @param userId
     * @param name
     * @param age
     * @return
     */
    @RepeatableCommit
    @Override
    public Result enrollIn(Integer userId, String name, Integer age) {
        int count = activityMapper.enrollStatus(userId);
        if(count<=0){
            activityMapper.insertEnrolIn(userId,name,age);
        }
        return Result.ok(count<=0);
    }

    /**
     * 报名状态
     * @param userId
     * @return
     */
    @Override
    public Result enrollStatus(Integer userId) {
        int count = activityMapper.enrollStatus(userId);
        return Result.ok( count>0);
    }

    @Override
    public List<UserAnswersExportDTO> downUserAnswers(Integer activityId) {
        return activityMapper.downUserAnswers(activityId);
    }

    @Override
    public List<LotteryDetailsExportDTO> downLotteryDetails(Integer activityId) {
        return activityMapper.downLotteryDetails(activityId);
    }
}


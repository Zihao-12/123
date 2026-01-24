package com.example.webapp.Service.statistics;

import com.example.webapp.DO.*;
import com.example.webapp.DTO.CategoryDTO;
import com.example.webapp.DTO.InterestDTO;
import com.example.webapp.DTO.LearnCenterStatisticsDTO;
import com.example.webapp.DTO.MechanismOpenDTO;
import com.example.webapp.Mapper.statistics.StatisticsMapper;
import com.example.webapp.Service.category.CategoryService;
import com.example.webapp.Service.mechanismOpen.MechanismOpenService;
import com.example.webapp.VO.FakeDataTJVO;
import com.example.webapp.VO.FakeDataVO;
import com.example.webapp.VO.FakeDateSetVO;
import com.example.webapp.annotation.Cacheable;
import com.example.webapp.annotation.RepeatableCommit;
import com.example.webapp.enums.LogRecordEnum;
import com.example.webapp.enums.OpenStatusEnum;
import com.example.webapp.enums.UserLearnRecordCompleteEnum;
import com.example.webapp.query.CourseQuery;
import com.example.webapp.query.FakeDataQuery;
import com.example.webapp.result.Result;
import com.example.webapp.utils.DateTimeUtil;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@EnableTransactionManagement
@Slf4j
@Service
public class StatisticsServiceImpl implements StatisticsService, Serializable {
    private static final int DB_SUB_SIZE = 1000;
    @Autowired
    private MechanismOpenService mechanismOpenService;
    @Autowired
    CategoryService categoryService;
    @Autowired
    private StatisticsMapper statisticsMapper;
    @Value("${default.fake_set:{\"homepageVisits\":{\"baseValue\":10,\"multiplier\":2},\"courseVisits\":{\"baseValue\":10,\"multiplier\":2},\"activityVisits\":{\"baseValue\":10,\"multiplier\":2},\"studyVisits\":{\"baseValue\":10,\"multiplier\":2},\"myVisits\":{\"baseValue\":10,\"multiplier\":2}}}")
    private String defaultfakeSet;
    @Override
    public Result loginRecord(UserLoginRecordDO userLoginRecordDO) {
        int count = 0;
        try {
            count = statisticsMapper.loginRecord(userLoginRecordDO);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return Result.ok(count);
    }

    @Cacheable(prefix = "learnInfo",fieldKey = "#query.userId +'_'+ #query.mechanismId")
    @Override
    public Result learnInfo(CourseQuery query) {
        Result result = null;
        try {
            LearnCenterStatisticsDTO dto = new LearnCenterStatisticsDTO();
            //今日学习时长
            StUserLearnRecordDetailDO userLearnDuration = statisticsMapper.countUserLearnRecordTodayByUserId(query, DateTimeUtil.todayOfZero());
            dto.setTodayLearnDuration(userLearnDuration==null?0:userLearnDuration.getDuration());
            //累计学习时长
            StUserLearnRecordDetailDO userLearnDurationTotal = statisticsMapper.countUserLearnRecordByUserId(query);
            dto.setTotalLearnDuration(userLearnDurationTotal==null?0:userLearnDurationTotal.getDuration());
            //累计登录
            Integer loginDays = statisticsMapper.countUserLoginByUserId(query);
            dto.setTotalLogin(loginDays);
            //课程完成数
            int completeNum = statisticsMapper.countCourseCompleteByUserId(query, UserLearnRecordCompleteEnum.COMPLETE_YES.getStatus());
            //兴趣分布
            List<InterestDTO> interestDTOList = statisticsMapper.getInterestListOfUser(query.getUserId());
            dto.setInterestDTOList(interestDTOList);
            dto.setCourseCompleteNum(completeNum);
            result = Result.ok(dto);
        } catch (Exception e) {
            e.printStackTrace();
            result = Result.fail(ExceptionUtils.getStackTrace(e));
        }
        return result;
    }

    @RepeatableCommit
    @Override
    public Result saveLogRecordDo(StUserLogRecordDO lr) {
        statisticsMapper.saveLogRecordDo(lr);
        return Result.ok(0);
    }

    /**
     *  造数据规则  访问量 = (原始数据+基数) * 倍数 + （0 到 100随机数）
     * @param dataDate
     */
    @Override
    public void fakeData(Date dataDate) {
        //同步logRecord
        tongBuLogRecord(dataDate);
        tongBuStudyTime(dataDate);
        tongBuCategoryStudyTime(dataDate);

    }

    /**
     * 同步 分类/年龄学习时长
     * @param dataDate
     */
    private void tongBuCategoryStudyTime(Date dataDate) {
        try {
            List<StUserFakeDataDO> list = Lists.newArrayList();
            List<StUserFakeDataDO> categoryList = statisticsMapper.getCategoryUserLearnRecordDetailByDate(dataDate);
//            List<StUserFakeDataDO> ageList = statisticsMapper.getAgeUserLearnRecordDetailByDate(dataDate);
            list.addAll(categoryList);
//            list.addAll(ageList);
            if(CollectionUtils.isNotEmpty(list)){
                Set<Integer> mechanismIdList = list.stream().map(StUserFakeDataDO::getMechanismId).collect(Collectors.toSet());
                mechanismIdList.forEach(mid ->{
                    Integer practiceType = mechanismOpenService.getPracticeOpenStatus(mid);
                    if(OpenStatusEnum.PRACTICE_NORMAL.getType().equals(practiceType)){
                        List<List<StUserFakeDataDO>> subLists = Lists.partition(list,DB_SUB_SIZE);
                        subLists.parallelStream().forEach(
                                sb->statisticsMapper.insertUserFakeDataList(sb)
                        );
                    } else {
                        log.error("机构 {} 未开通，数据生成失败",mid);
                    }
                });
            }
        }catch (Exception e){
            log.error("{}", ExceptionUtils.getStackTrace(e));
        }
    }

    /**
     * 同步学习时长
     * @param dataDate
     */
    private void tongBuStudyTime(Date dataDate) {
        try {
            List<StUserFakeDataDO> list = statisticsMapper.getUserLearnRecordDetailByDate(dataDate);
            if(CollectionUtils.isNotEmpty(list)){
                Set<Integer> mechanismIdList = list.stream().map(StUserFakeDataDO::getMechanismId).collect(Collectors.toSet());
                Map<Integer, FakeDateSetDO> fakesetMap = statisticsMapper.getFakesetMap(mechanismIdList);
                Map<Integer, List<StUserFakeDataDO>> listMap = list.stream().collect(Collectors.groupingBy(StUserFakeDataDO::getMechanismId));
                listMap.forEach((mid,fakeDataDOList) ->{
                    Integer practiceType = mechanismOpenService.getPracticeOpenStatus(mid);
                    if(OpenStatusEnum.PRACTICE_NORMAL.getType().equals(practiceType)){
                        fakeDataDOList.forEach(o ->{
                            o.setCategoryId(0);
                            o.setCategoryName("");
                            o.setRealNum(o.getNum());
                            FakeDateSetVO fakeSet= getFakeSet(o.getMechanismId(),fakesetMap);
                            o.setType(LogRecordEnum.COURSE_TM.getType());
                            Integer num = parseNum(o.getNum(),fakeSet.getStudyVisits().getBaseValue(),fakeSet.getStudyVisits().getMultiplier(),60);
                            o.setNum(num);
                        });
                        List<List<StUserFakeDataDO>> subLists = Lists.partition(fakeDataDOList,DB_SUB_SIZE);
                        subLists.parallelStream().forEach(
                                sb->statisticsMapper.insertUserFakeDataList(sb)
                        );
                    } else {
                        log.error("机构 {} 未开通，数据生成失败",mid);
                    }
                });
            }
        }catch (Exception e){
            log.error("{}", ExceptionUtils.getStackTrace(e));
        }
    }


    /**
     * 同步logRecord
     * @param dataDate
     */
    private void tongBuLogRecord(Date dataDate) {
        try {
            List<StUserFakeDataDO> list = statisticsMapper.getUserLogRecordByDate(dataDate);
            if(CollectionUtils.isNotEmpty(list)){
                Set<Integer> mechanismIdList = list.stream().map(StUserFakeDataDO::getMechanismId).collect(Collectors.toSet());
                Map<Integer, FakeDateSetDO> fakesetMap = statisticsMapper.getFakesetMap(mechanismIdList);
                Map<Integer, List<StUserFakeDataDO>> listMap = list.stream().collect(Collectors.groupingBy(StUserFakeDataDO::getMechanismId));
                listMap.forEach((mid,fakeDataDOList) ->{
                    Integer practiceType = mechanismOpenService.getPracticeOpenStatus(mid);
                    if(OpenStatusEnum.PRACTICE_NORMAL.getType().equals(practiceType)){
                        fakeDataDOList.forEach(o ->{
                            o.setCategoryId(0);
                            o.setCategoryName("");
                            o.setRealNum(o.getNum());
                            FakeDateSetVO fakeSet= getFakeSet(o.getMechanismId(),fakesetMap);
                            if(LogRecordEnum.SHU_FANG.getType().equals(o.getType())){
                                Integer num = parseNum(o.getNum(),fakeSet.getHomepageVisits().getBaseValue(),fakeSet.getHomepageVisits().getMultiplier());
                                o.setNum(num);
                            }else if(LogRecordEnum.COURSE_XQ.getType().equals(o.getType())){
                                Integer num = parseNum(o.getNum(),fakeSet.getCourseVisits().getBaseValue(),fakeSet.getCourseVisits().getMultiplier());
                                o.setNum(num);
                            }else if(LogRecordEnum.HUO_DONG.getType().equals(o.getType())){
                                Integer num = parseNum(o.getNum(),fakeSet.getActivityVisits().getBaseValue(),fakeSet.getActivityVisits().getMultiplier());
                                o.setNum(num);
                            }else if(LogRecordEnum.MY.getType().equals(o.getType())){
                                Integer num = parseNum(o.getNum(),fakeSet.getMyVisits().getBaseValue(),fakeSet.getMyVisits().getMultiplier());
                                o.setNum(num);
                            }
                        });
                        List<List<StUserFakeDataDO>> subLists = Lists.partition(fakeDataDOList,DB_SUB_SIZE);
                        subLists.parallelStream().forEach(
                                sb->statisticsMapper.insertUserFakeDataList(sb)
                        );
                    } else {
                        log.error("机构 {} 未开通，数据生成失败",mid);
                    }
                });
            }
        }catch (Exception e){
            log.error("{}", ExceptionUtils.getStackTrace(e));
        }
    }

    /**
     * 数据造假公式  课程模版单位分钟，保存转化成秒 beishu
     * @param num
     * @param baseValue
     * @param multiplier
     * @return
     */
    private Integer parseNum(Integer num, Integer baseValue, Integer multiplier) {
        return parseNum(num, baseValue, multiplier,1);
    }

    private Integer parseNum(Integer num, Integer baseValue, Integer multiplier,Integer beishu) {
        Random random = new Random();
        Integer r = random.nextInt(100);
        r = beishu >=60 ? r*60 : r;
        return (num + baseValue * beishu) * multiplier +r;
    }

    private FakeDateSetVO getFakeSet(Integer mechanismId, Map<Integer, FakeDateSetDO> fakesetMap) {
        FakeDateSetDO setdo = fakesetMap.get(mechanismId);
        String fakeset = defaultfakeSet;
        try {
            if(setdo !=null && StringUtils.isNotBlank(setdo.getFakeset())){
                fakeset = setdo.getFakeset();
            }
            FakeDateSetVO fakeDateSetVO =new Gson().fromJson(fakeset,FakeDateSetVO.class);
            if(CollectionUtils.isEmpty(fakeDateSetVO.getCategroyList())){
                FakeDateSetVO defaultCategory =new Gson().fromJson(defaultfakeSet,FakeDateSetVO.class);
                fakeDateSetVO.setCategroyList(defaultCategory.getCategroyList());
            }
            return  fakeDateSetVO;
        }catch (Exception e){

        }
        return new Gson().fromJson(fakeset,FakeDateSetVO.class);
    }

    @Override
    public Result<FakeDataTJVO> getFakeDataList(FakeDataQuery query) {
        FakeDataTJVO tjvo = new FakeDataTJVO();
        List<FakeDataVO> list = statisticsMapper.getFakeDataList(query);
        tjvo.setFakeDataVOList(list);
        if(CollectionUtils.isNotEmpty(list)){
            tjvo.setTotal(list.stream().mapToInt(FakeDataVO::getV).sum());
            tjvo.setRealTotal(list.stream().mapToInt(FakeDataVO::getRv).sum());
        }
        return Result.ok(tjvo);
    }

    @Override
    public Result<FakeDataTJVO> getFakeDataUVList(FakeDataQuery query) {
        FakeDataTJVO tjvo = new FakeDataTJVO();
        List<FakeDataVO> list = statisticsMapper.getFakeDataUVList(query);
        tjvo.setFakeDataVOList(list);
        if(CollectionUtils.isNotEmpty(list)){
            tjvo.setTotal(list.stream().mapToInt(FakeDataVO::getV).sum());
            tjvo.setRealTotal(list.stream().mapToInt(FakeDataVO::getRv).sum());
        }
        return Result.ok(tjvo);
    }

    @Override
    public Result<FakeDataTJVO> getCategoryFakeDataList(FakeDataQuery query) {
        FakeDataTJVO tjvo = new FakeDataTJVO();
        List<FakeDataVO> list = statisticsMapper.getCategoryFakeDataList(query);
        tjvo.setFakeDataVOList(list);
        if(CollectionUtils.isNotEmpty(list)){
            tjvo.setTotal(list.stream().mapToInt(FakeDataVO::getV).sum());
            tjvo.setRealTotal(list.stream().mapToInt(FakeDataVO::getRv).sum());
        }
        return Result.ok(tjvo);
    }


    /**
     * @param dataDate
     */
    @Override
    public void generateDefaultFakeData(Date dataDate) {
        List<MechanismOpenDTO> openDTOList = mechanismOpenService.findMechanismOpeningList();
        if(CollectionUtils.isNotEmpty(openDTOList)){
            List<StUserFakeDataDO> fakeDataList = Lists.newArrayList();
            Set<Integer> mechanismIdList = openDTOList.stream().map(MechanismOpenDTO::getMechanismId).collect(Collectors.toSet());
            Map<Integer, FakeDateSetDO> fakesetMap = statisticsMapper.getFakesetMap(mechanismIdList);
            mechanismIdList.stream().forEach(mid ->{
                FakeDateSetVO fakeSet= getFakeSet(mid,fakesetMap);
                fakeDataList.add(getDefaultFakeData(dataDate, mid, fakeSet,LogRecordEnum.COURSE_XQ));
                fakeDataList.add(getDefaultFakeData(dataDate, mid, fakeSet,LogRecordEnum.SHU_FANG));
                fakeDataList.add(getDefaultFakeData(dataDate, mid, fakeSet,LogRecordEnum.HUO_DONG));
                fakeDataList.add(getDefaultFakeData(dataDate, mid, fakeSet,LogRecordEnum.MY));
                fakeDataList.add(getDefaultFakeData(dataDate, mid, fakeSet,LogRecordEnum.COURSE_TM));
                fakeDataList.addAll(getDefaultCategoryFakeData(dataDate, mid, fakeSet));
            });

            List<List<StUserFakeDataDO>> subLists = Lists.partition(fakeDataList,DB_SUB_SIZE);
            subLists.parallelStream().forEach(
                    sb->statisticsMapper.insertUserFakeDataList(sb)
            );
        }
    }

    @Override
    public Result getRegCountByMid(FakeDataQuery query) {
        Integer num = statisticsMapper.getRegCountByMid(query);
        return Result.ok(num);
    }

    private Collection<? extends StUserFakeDataDO> getDefaultCategoryFakeData(Date dataDate, Integer mid, FakeDateSetVO fakeSet) {
        List<StUserFakeDataDO> fakeDataList = Lists.newArrayList();
        List<CategoryDTO> nodeList = (List<CategoryDTO>) categoryService.getBuyCategoryList(mid).getData();
        if(CollectionUtils.isNotEmpty(nodeList)){
            Set<Integer> categoryIdSet = nodeList.stream().map(CategoryDTO::getId).collect(Collectors.toSet());
            log.info("分类时长生成-机构ID-{},购买分类ID：{}",mid,new Gson().toJson(categoryIdSet));
            fakeSet.getCategroyList().stream().forEach(cfv->{
                if(categoryIdSet.contains(cfv.getId())){
                    StUserFakeDataDO fakedata = new StUserFakeDataDO();
                    fakedata.setCategoryId(cfv.getId());
                    fakedata.setCategoryName(cfv.getName());
                    fakedata.setUserId(-1);
                    fakedata.setDate(dataDate);
                    fakedata.setMechanismId(mid);
                    fakedata.setType(LogRecordEnum.COURSE_FL.getType());
                    fakedata.setNum(parseNum(1,cfv.getBaseValue(),cfv.getMultiplier(),60));
                    fakedata.setRealNum(0);
                    fakeDataList.add(fakedata);
                    log.info("分类时长生成-机构ID-{}-成功，categoryId:{},categoryName:{}",mid,cfv.getId(),cfv.getName());
                }
            });
        }else {
            log.info("分类时长生成-机构ID-{}-失败",mid);
        }
        return fakeDataList;
    }

    private StUserFakeDataDO getDefaultFakeData(Date dataDate, Integer mid, FakeDateSetVO fakeSet,LogRecordEnum logRecordEnum) {
        StUserFakeDataDO fakedata = new StUserFakeDataDO();
        fakedata.setCategoryId(-1);
        fakedata.setCategoryName("-1");
        fakedata.setUserId(-1);
        fakedata.setDate(dataDate);
        fakedata.setMechanismId(mid);
        fakedata.setRealNum(0);
        fakedata.setType(logRecordEnum.getType());
        Integer num=0;
        if(LogRecordEnum.COURSE_TM.equals(logRecordEnum)){
            num = parseNum(1, fakeSet.getStudyVisits().getBaseValue(), fakeSet.getStudyVisits().getMultiplier(),60);
        }else if(LogRecordEnum.SHU_FANG.equals(logRecordEnum)){
            num = parseNum(1, fakeSet.getHomepageVisits().getBaseValue(), fakeSet.getHomepageVisits().getMultiplier());
        }else if(LogRecordEnum.COURSE_XQ.equals(logRecordEnum)){
            num = parseNum(1,fakeSet.getCourseVisits().getBaseValue(),fakeSet.getCourseVisits().getMultiplier());
        }else if(LogRecordEnum.HUO_DONG.equals(logRecordEnum)){
            num = parseNum(1,fakeSet.getActivityVisits().getBaseValue(),fakeSet.getActivityVisits().getMultiplier());
        }else if(LogRecordEnum.MY.equals(logRecordEnum)){
            num = parseNum(1,fakeSet.getMyVisits().getBaseValue(),fakeSet.getMyVisits().getMultiplier());
        }
        fakedata.setNum(num);
        return fakedata;
    }

}
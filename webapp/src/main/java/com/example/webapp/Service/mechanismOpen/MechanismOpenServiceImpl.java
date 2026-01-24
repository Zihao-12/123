package com.example.webapp.Service.mechanismOpen;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MechanismOpenServiceImpl implements MechanismOpenService, Serializable {
    public static final String MECHANISM_OPEN_ID = "MECHANISM_OPEN_ID";
    public static final String FIND_MECHANISM_OPEN_BY_ID = "FIND_MECHANISM_OPEN_BY_ID_4";
    public static final String SAVE_MECHANISM_OPEN_DO = "SAVE_MECHANISM_OPEN_DO_1";
    public static final String SAVE_CONTACT_PERSON = "saveContactPerson_";
    public static final String MESSAGE = "message";
    public static final int PAGE_SIZE = 20;
    public static final String OPEN_DELAY = "openDelay";
    public static final String DEACTIVATE_OPEN = "deactivateOpen";
    public static final String DEL_OPEN = "delOpen";
    public static final String FIND_COURSE_PACKAGE_BY_ID = "findCoursePackageById";
    public static final String IS_DEL_OPEN = "isDelOpen";
    @Autowired
    private MechanismOpenMapper mechanismOpenMapper;
    @Autowired
    private CoursePackageMapper coursePackageMapper;
    @Autowired
    private CourseMapper courseMapper;
    @Autowired
    private MechanismService mechanismService;
    @Autowired
    private CourseService courseService;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private RedisLock redisLock;

    /**
     * 机构开通列表分页查询
     * @param query
     * @return MechanismOpenStatusEnum
     */
    @Override
    public PageInfo findMechanismOpenList(MechanismOpenQuery query) {
        try {
            query.setPageNo(query.getPageNo()==null?0:query.getPageNo());
            query.setPageSize(query.getPageSize()==null? PAGE_SIZE :query.getPageSize());
            if(StringUtils.isNotBlank(query.getName())){
                query.setName("%"+query.getName()+"%");
            }
            query.setOpenType(query.getOpenType()==null? MechanismOpenTypeEnum.PRACTICE.getType():query.getOpenType());
            //使用分页插件,核心代码就这一行 #分页配置#
            Page page = PageHelper.startPage(query.getPageNo(), query.getPageSize());
            List<MechanismOpenDTO> list = mechanismOpenMapper.findMechanismOpenList(query);
            PageInfo pageInfo = new PageInfo(list);
            list.stream().forEach(o->{
                parseOpenStatusShow(o);
            });
            return pageInfo;
        }catch (Exception e){
            log.error("{}", ExceptionUtils.getStackTrace(e));
        }
        return  null;
    }

    /**
     * 获取所有 开通进行中的 机构 不分页
     * @return MechanismOpenStatusEnum
     */
    @Override
    public List<MechanismOpenDTO> findMechanismOpeningList() {
        try {
            MechanismOpenQuery query = new MechanismOpenQuery();
            query.setOpen(MechanismOpenEnum.IN_PROGRESS.getType());
            query.setStatus(StatusEnum.ENABLE.getType());
            query.setOpenType(MechanismOpenTypeEnum.PRACTICE.getType());
            List<MechanismOpenDTO> list = mechanismOpenMapper.findMechanismOpenList(query);
            return list;
        }catch (Exception e){
            log.error("{}", ExceptionUtils.getStackTrace(e));
        }
        return  null;
    }

    /**
     * 处理开通状态
     * @param o
     */
    private void parseOpenStatusShow(MechanismOpenDTO o) {
        if(DateTimeUtil.isAfterNow(o.getBeginTime())){
            //待开始
            o.setOpenView(MechanismOpenEnum.TO_START.getName());
        }else  if(DateTimeUtil.isBeforeNow(o.getEndTime())){
            //已结束
            o.setOpenView(MechanismOpenEnum.FINISHED.getName());
        }else if(!DateTimeUtil.isAfterNow(o.getBeginTime()) && !DateTimeUtil.isBeforeNow(o.getEndTime())){
            //进行中
            o.setOpenView(MechanismOpenEnum.IN_PROGRESS.getName());

        }
        if(StatusEnum.ENABLE.getType().equals(o.getStatus())){
            o.setStatusView(StatusEnum.ENABLE.getName());
        }else   if(StatusEnum.STOP.getType().equals(o.getStatus())){
            o.setStatusView(StatusEnum.STOP.getName());
        }
    }


    /**
     * 编辑机构微软/实训开通 ID not null is edit else is insert
     * 有效开通 = 待开始+进行中
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result saveMechanismOpenDO(MechanismOpenDO mechanismOpenDO) {
        try {
            MechanismOpenDO meMechanismOpenDO =parseInfo(mechanismOpenDO);
            String key  = SAVE_MECHANISM_OPEN_DO+meMechanismOpenDO.getMechanismId();
            String token = redisLock.tryLock(key, TimeUnit.SECONDS.toMillis(3 * 60));
            try{
                if(token != null) {
                    MechanismDTO mechanism=mechanismService.getMechanismById(meMechanismOpenDO.getMechanismId());
                    if(mechanism==null){
                        return Result.fail(CodeEnum.FAILED.getValue(),"机构不存在");
                    }
                    if(meMechanismOpenDO.getOpenType()==null){
                        meMechanismOpenDO.setOpenType(MechanismOpenTypeEnum.PRACTICE.getType());
                    }
                    Map<String,Object> rs= Maps.newHashMap();
                    if(MechanismOpenTypeEnum.PRACTICE.getType().equals(meMechanismOpenDO.getOpenType())&&
                            DateTimeUtil.isBeforeNow(meMechanismOpenDO.getEndTime())){
                        return Result.fail(CodeEnum.FAILED.getValue(),"开通日期无效");
                    }
                    //判断该机构是否开通过
                    MechanismOpenDO effective = mechanismOpenMapper.findEffectiveOpenByMechanismId(meMechanismOpenDO.getMechanismId(),meMechanismOpenDO.getOpenType());
                    meMechanismOpenDO.setOpenDays(getOpenDays(meMechanismOpenDO.getBeginTime(), meMechanismOpenDO.getEndTime()));
                    if(meMechanismOpenDO.getId()!=null){
                        //更新课程包状态
                        if(effective!=null && meMechanismOpenDO.getCoursePackageId().compareTo(effective.getCoursePackageId())!=0){
                            coursePackageMapper.updateUse(effective.getCoursePackageId(), CoursePackageStatusEnum.NOT_USED.status);
                            coursePackageMapper.updateUse(meMechanismOpenDO.getCoursePackageId(), CoursePackageStatusEnum.USED.status);
                        }
                        //修改
                        mechanismOpenMapper.updateMechanismOpen(meMechanismOpenDO);
                        rs.put(MESSAGE,"机构开通更新");
                    }else{
                        if(effective==null){
                            //新增且没有未过期的开通
                            mechanismOpenMapper.insertMechanismOpen(meMechanismOpenDO);
                            rs.put(MESSAGE,"机构开通");
                            //更新课程包状态为已使用
                            coursePackageMapper.updateUse(meMechanismOpenDO.getCoursePackageId(), CoursePackageStatusEnum.USED.status);
                        }else {
                            return Result.fail(CodeEnum.FAILED.getValue(),"该机构已开通,请不要重复开通");
                        }
                    }
                    //初始化机构课程解锁模式
                    courseService.initMechanismCourseUnlock(meMechanismOpenDO.getMechanismId(),meMechanismOpenDO.getCoursePackageId());
                    rs.put(MECHANISM_OPEN_ID,meMechanismOpenDO.getId());
                    redisUtils.del(FIND_MECHANISM_OPEN_BY_ID +meMechanismOpenDO.getId());
                    return Result.ok(rs);
                } else {
                    return Result.fail(CodeEnum.FAILED.getValue(),"重复提交");
                }
            } finally {
                if(token!=null) {
                    redisLock.unlock(key, token);
                }
            }
        }catch (Exception e){
            log.error("机构开通失败{}",ExceptionUtils.getStackTrace(e));
            return Result.fail(CodeEnum.FAILED.getValue(),"机构开通失败");
        }

    }


    private MechanismOpenDO parseInfo(MechanismOpenDO meMechanismOpenDO) {
        if(meMechanismOpenDO.getStatus()==null){
            meMechanismOpenDO.setStatus(StatusEnum.ENABLE.getType());
        }else if(meMechanismOpenDO.getStatus()>StatusEnum.ENABLE.getType() || meMechanismOpenDO.getStatus()<StatusEnum.STOP.getType()){
            meMechanismOpenDO.setStatus(StatusEnum.STOP.getType());
        }
        if(meMechanismOpenDO.getAccountNumber()==null){
            //0 不限制机构账号数量
            meMechanismOpenDO.setAccountNumber(0);
        }
        return meMechanismOpenDO;
    }

    private Integer getOpenDays(Date beginTime, Date endTime) {
        return DateTimeUtil.daysBetween(beginTime, endTime)+1;
    }

    /**
     * @param mechanismOpenId
     * @return
     */
    @Override
    public Result findMechanismOpenById(Integer mechanismOpenId) {
        MechanismOpenDTO mechanismOpenDTO = mechanismOpenMapper.findMechanismOpenById(mechanismOpenId);
        if(mechanismOpenDTO!=null){
            if(MechanismOpenTypeEnum.PRACTICE.getType().equals(mechanismOpenDTO.getOpenType())){
                parseOpenStatusShow(mechanismOpenDTO);
                //查询课程包课程
                List<CourseDTO> courseList = coursePackageMapper.viewCoursePackageRef(mechanismOpenDTO.getCoursePackageId());
                mechanismOpenDTO.setCourseList(courseList);
                if(CollectionUtils.isNotEmpty(courseList)){
                    //查询课节数
                    List<Integer> courseIdList = courseList.stream().map(CourseDTO::getId).collect(Collectors.toList());
                    List<CourseDTO> sectionNumList = courseMapper.listSectionNum(courseIdList,CourseSectionTypeEnum.SECTION.getType());
                    Map<Integer, Integer> sectionNumMap = convertSectionNumToMap(sectionNumList);
                    courseList.forEach(p -> {
                        Integer sectionNum = sectionNumMap.get(p.getId());
                        p.setCourseSectionNumber(sectionNum==null?0:sectionNum);
                    });
                }
            }
        }
        return Result.ok(mechanismOpenDTO) ;
    }

    /**
     * 查询课程包
     * @param packageId
     * @return CoursePackage
     */
    @Override
    public Result findCoursePackageById(Integer packageId) {
        String key = RedisKeyGenerator.getKey(MechanismOpenService.class, FIND_COURSE_PACKAGE_BY_ID, packageId);
        CoursePackageDTO packageDTO = (CoursePackageDTO) redisUtils.get(key);
        if(packageDTO==null){
            packageDTO = mechanismOpenMapper.findCoursePackageById(packageId);
            if(packageDTO!=null){
                //查询课程包课程
                List<CourseDTO> courseList = coursePackageMapper.viewCoursePackageRef(packageId);
                if(CollectionUtils.isNotEmpty(courseList)){
                    //查询课节数
                    List<Integer> courseIdList = courseList.stream().map(CourseDTO::getId).collect(Collectors.toList());
                    List<CourseDTO> sectionNumList = courseMapper.listSectionNum(courseIdList,CourseSectionTypeEnum.SECTION.getType());
                    Map<Integer, Integer> sectionNumMap = convertSectionNumToMap(sectionNumList);
                    courseList.forEach(p -> {
                        Integer sectionNum = sectionNumMap.get(p.getId());
                        p.setCourseSectionNumber(sectionNum==null?0:sectionNum);
                    });
                }
                packageDTO.setCourseList(courseList);
            }
            redisUtils.set(key,packageDTO,RedisUtils.TIME_MINUTE_1);
        }
        return Result.ok(packageDTO);
    }

    /**
     * 课节数量转map
     * @param sectionNumList
     * @return
     */
    private Map<Integer, Integer> convertSectionNumToMap(List<CourseDTO> sectionNumList) {
        Map<Integer, Integer> result = new HashMap<>();
        if (CollectionUtils.isEmpty(sectionNumList)) {
            return result;
        }
        for (CourseDTO dto : sectionNumList) {
            result.put(dto.getId(),dto.getCourseSectionNumber());
        }
        return result;
    }

    /**
     * 机构实训开通 删除
     * @param id 开通ID
     *
     * @return
     */
    @Override
    public Result delOpen(Integer id){
        String key = RedisKeyGenerator.getKey(MechanismOpenService.class, DEL_OPEN, id);
        String lock = redisLock.tryLock(key, TimeUnit.SECONDS.toMillis(5));
        try{
            if(lock != null) {
                MechanismOpenDTO open = mechanismOpenMapper.findMechanismOpen(id);
                MechanismOpenDO up=new MechanismOpenDO();
                up.setIsDelete(1);
                up.setId(id);
                if(open==null){
                    return Result.fail(CodeEnum.FAILED.getValue(),"机构还未开通或开通已过期");
                }
                if( MechanismOpenTypeEnum.PRACTICE.getType().equals(open.getOpenType())){
                    //删除实训开通
                    //进行中
                    boolean running =!DateTimeUtil.isAfterNow(open.getBeginTime()) && !DateTimeUtil.isBeforeNow(open.getEndTime());
                    //已结束
                    boolean end =DateTimeUtil.isBeforeNow(open.getEndTime());
                    if(running ||end ){
                        return Result.fail(CodeEnum.FAILED.getValue(),"开通已执行，不允许删除");
                    }
                    coursePackageMapper.updateUse(open.getCoursePackageId(), CoursePackageStatusEnum.NOT_USED.status);
                    mechanismOpenMapper.updateMechanismOpen(up);
                }else {
                    return Result.fail(CodeEnum.FAILED.getValue(),"开通类型错误");
                }
            } else {
                return Result.fail(CodeEnum.FAILED.getValue(),"重复提交");
            }
            return Result.ok("删除成功");
        }catch (Exception e){
            log.error("{}",ExceptionUtils.getStackTrace(e));
        } finally {
            if(lock!=null) {
                redisLock.unlock(key, lock);
            }
        }
        return Result.fail(CodeEnum.FAILED.getValue(),"操作失败");
    }


    /**
     * 机构实训开通 判断机构开通是否可以删除
     * @param id 开通ID
     *
     * @return  0允许删除    -1 开通已执行，不允许删除
     */
    @Override
    public Result isDelOpen(Integer id){
        String key = RedisKeyGenerator.getKey(MechanismOpenService.class, IS_DEL_OPEN, id);
        String lock = redisLock.tryLock(key, TimeUnit.SECONDS.toMillis(5));
        try{
            if(lock != null) {
                MechanismOpenDTO open = mechanismOpenMapper.findMechanismOpen(id);
                if(open==null){
                    return Result.fail(CodeEnum.FAILED.getValue(),"机构还未开通或开通已过期");
                }
                if( MechanismOpenTypeEnum.PRACTICE.getType().equals(open.getOpenType())){
                    //删除实训开通
                    //进行中
                    boolean running =!DateTimeUtil.isAfterNow(open.getBeginTime()) && !DateTimeUtil.isBeforeNow(open.getEndTime());
                    //已结束
                    boolean end =DateTimeUtil.isBeforeNow(open.getEndTime());
                    if(running ||end ){
                        return Result.fail(CodeEnum.FAILED.getValue(),"开通已执行，不允许删除");
                    }
                }else {
                    return Result.fail(CodeEnum.FAILED.getValue(),"开通类型错误");
                }
            } else {
                return Result.fail(CodeEnum.FAILED.getValue(),"重复提交");
            }
            return Result.ok("允许删除");
        }catch (Exception e){
            log.error("{}",ExceptionUtils.getStackTrace(e));
        } finally {
            if(lock!=null) {
                redisLock.unlock(key, lock);
            }
        }
        return Result.fail(CodeEnum.FAILED.getValue(),"操作失败");
    }


    /**
     * 机构开通 停用启用
     * @param id 开通ID
     * @return
     */
    @Override
    public Result deactivateOpen(Integer id){
        String key = RedisKeyGenerator.getKey(MechanismOpenService.class, DEACTIVATE_OPEN, id);
        String lock = redisLock.tryLock(key, TimeUnit.SECONDS.toMillis(5));
        try{
            if(lock != null) {
                MechanismOpenDTO open = mechanismOpenMapper.findMechanismOpenById(id);
                if(open!=null){
                    MechanismOpenDO up=new MechanismOpenDO();
                    StatusEnum status = StatusEnum.ENABLE.getType().equals(open.getStatus())? StatusEnum.STOP: StatusEnum.ENABLE;
                    up.setStatus(status.getType());
                    up.setId(id);
                    mechanismOpenMapper.updateMechanismOpen(up);
                    return Result.ok(status.getName());
                }else {
                    return Result.fail(CodeEnum.FAILED.getValue(),"不存在");
                }
            } else {
                return Result.fail(CodeEnum.FAILED.getValue(),"重复提交");
            }
        }catch (Exception e){
            log.error("{}",ExceptionUtils.getStackTrace(e));
        } finally {
            if(lock!=null) {
                redisLock.unlock(key, lock);
            }
        }
        return Result.fail(CodeEnum.FAILED.getValue(),"操作失败");
    }

    /**
     * 机构延期
     * @return
     */
    @Override
    public Result openDelay(MechanismOpenDelayDTO openDelay){
        Result result =Result.fail("延期失败");
        Integer id =openDelay.getId();
        Date beginTime = openDelay.getBeginTime();
        Date endTime = openDelay.getEndTime();
        String message="";
        MechanismOpenDelayRecordDO delayRecord = new MechanismOpenDelayRecordDO();
        delayRecord.setBeginTime(beginTime);
        delayRecord.setEndTime(endTime);
        delayRecord.setMechanismOpenId(id);
        Integer delayDays = getOpenDays(beginTime, endTime);
        delayRecord.setDelayDays(delayDays);
        String key = RedisKeyGenerator.getKey(MechanismOpenService.class, OPEN_DELAY, id);
        String lock = redisLock.tryLock(key, TimeUnit.SECONDS.toMillis(5));
        try{
            if(lock != null) {
                MechanismOpenDTO open = mechanismOpenMapper.findMechanismOpenById(id);
                if(open!=null && !DateTimeUtil.isBeforeNow(open.getEndTime())){
                    delayRecord.setLastOpenDays(open.getOpenDays());
                    delayRecord.setLastEndTime(open.getEndTime());
                    if(DateTimeUtil.isAfter(endTime,open.getEndTime()) && DateTimeUtil.isAfter(beginTime,open.getEndTime())){
                        //延期的开始结束日期要大于 当前开通的结束日期
                        MechanismOpenDO up=new MechanismOpenDO();
                        up.setId(id);
                        up.setEndTime(endTime);
                        Integer days = getOpenDays(open.getBeginTime(), up.getEndTime());
                        up.setOpenDays(days);
                        mechanismOpenMapper.updateMechanismOpen(up);
                        message ="延期成功";
                        result.setCode(CodeEnum.SUCCESS.getValue());
                        delayRecord.setIsDelay(1);
                        delayRecord.setDescription(message);
                    }else {
                        message ="错误的延期日期";
                        delayRecord.setIsDelay(0);
                        delayRecord.setDescription(message);
                    }
                }else {
                    message="开通不存在或已经结束不能延期";
                    delayRecord.setIsDelay(0);
                    delayRecord.setDescription(message);
                }
                mechanismOpenMapper.insertMechanismOpenDelayRecord(delayRecord);
            } else {
                message="重复提交";
                delayRecord.setIsDelay(0);
                delayRecord.setDescription(message);
            }
        }catch (Exception e){
            message="延期异常";
            delayRecord.setIsDelay(0);
            delayRecord.setDescription(message);
            log.error("延期失败");
            mechanismOpenMapper.insertMechanismOpenDelayRecord(delayRecord);
        } finally {
            if(lock!=null) {
                redisLock.unlock(key, lock);
            }
        }
        result.setMessage(message);
        return result;
    }


    /**
     * 获取开通状态
     * @param mechanismId
     */
    @Override
    public Integer getPracticeOpenStatus(Integer mechanismId) {
        List<MechanismOpenDO> practiceList = mechanismOpenMapper.findOpenRecordByMechanismId(mechanismId, MechanismOpenTypeEnum.PRACTICE.getType());
        //1 未开通实训 2 实训开通正常 3实训开通已过期
        AtomicReference<Integer> practiceType= new AtomicReference<>(OpenStatusEnum.PRACTICE_NO_OPEN.getType());
        if(!CollectionUtils.isEmpty(practiceList)){
            for (MechanismOpenDO m : practiceList) {
                if(!DateTimeUtil.isExpired(m.getEndTime())){
                    if(Constant.STOP.equals(m.getStatus())){
                        practiceType.set(OpenStatusEnum.PRACTICE_STOP.getType());
                    }else {
                        practiceType.set(OpenStatusEnum.PRACTICE_NORMAL.getType());
                    }
                    break;
                }else {
                    practiceType.set(OpenStatusEnum.PRACTICE_EXPIRED.getType());
                }
            }
        }
        return practiceType.get();
    }


}

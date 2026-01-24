package com.example.webapp.Service.mechanism;

import com.example.webapp.DO.*;
import com.example.webapp.DTO.FrontPageDTO;
import com.example.webapp.DTO.MechanismContactPersonDTO;
import com.example.webapp.DTO.MechanismDTO;
import com.example.webapp.DTO.MechanismRestrictIpDTO;
import com.example.webapp.Mapper.Mechanism.MechanismMapper;
import com.example.webapp.Mapper.MechanismOpen.MechanismOpenMapper;
import com.example.webapp.Service.area.AreaService;
import com.example.webapp.Service.college.CollegeService;
import com.example.webapp.VO.AccountVO;
import com.example.webapp.annotation.Cacheable;
import com.example.webapp.annotation.RepeatableCommit;
import com.example.webapp.common.Constant;
import com.example.webapp.common.redis.RedisKeyGenerator;
import com.example.webapp.common.redis.RedisLock;
import com.example.webapp.common.redis.RedisUtils;
import com.example.webapp.enums.MechanismOpenTypeEnum;
import com.example.webapp.query.IpQuery;
import com.example.webapp.query.MechanismQuery;
import com.example.webapp.result.CodeEnum;
import com.example.webapp.result.Result;
import com.example.webapp.utils.Md5Util;
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
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MechanismServiceImpl implements MechanismService, Serializable {
    public static final String UTF_8 = "utf-8";
    public static final String FIND_MECHANISM_LIST = "FIND_MECHANISM_LIST_";
    public static final String FIND_MECHANISM_BY_ID = "FIND_MECHANISM_BY_ID";
    public static final String FIND_MECHANISM_BYCCOUNT = "FIND_MECHANISM_BYCCOUNT";
    public static final String SAVE_MECHANISM_DO = "SAVE_MECHANISM_DO";
    public static final String SAVE_CONTACT_PERSON = "SAVE_CONTACT_PERSON";

    public static final String MESSAGE = "message";
    public static final String MECHANISM_ID = "mechanismId";
    public static final int PAGE_SIZE = 20;
    public static final String IS_OPENED_OFMECHANISM = "IS_OPENED_OFMECHANISM";
    public static final int MECHANISM_ATTRIBUTE = 1;
    public static final String GET_MECHANISM_ACCESS_URL = "GET_MECHANISM_ACCESS_URL";
    @Autowired
    private MechanismMapper mechanismMapper;
    @Autowired
    private MechanismOpenMapper mechanismOpenMapper;

    @Autowired
    private CollegeService collegeService;

    @Autowired
    AreaService areaService;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private RedisLock redisLock;

    /**
     * 机构列表分页查询
     * @param query
     * @return
     */
    @Override
    public PageInfo findMechanismList(MechanismQuery query) {
        try {
            query.setPageNo(query.getPageNo()==null?0:query.getPageNo());
            query.setPageSize(query.getPageSize()==null? PAGE_SIZE :query.getPageSize());
            if(StringUtils.isNotBlank(query.getName())){
                query.setName("%"+query.getName()+"%");
            }
            //使用分页插件,核心代码就这一行 #分页配置#
            Page page = PageHelper.startPage(query.getPageNo(), query.getPageSize());
            List<MechanismDTO> list = mechanismMapper.findMechanismList(query);
            PageInfo pageInfo = new PageInfo(list);
            return pageInfo;
        }catch (Exception e){
            log.error("{}", ExceptionUtils.getStackTrace(e));
        }
        return  null;
    }

    /**
     * 根据ID查询
     * @param mechanismId
     * @return
     */
    @Override
    public Result findMechanismById(Integer mechanismId) {
        MechanismDTO meMechanismDTO = getMechanismById(mechanismId);
        return Result.ok(meMechanismDTO) ;
    }

    @Override
    public MechanismDTO getMechanismById(Integer mechanismId) {
        MechanismDTO meMechanismDTO  = mechanismMapper.findMechanismById(mechanismId);
        //封装联系人
        if(meMechanismDTO!=null){
            meMechanismDTO.setContactPersonList(findContactPersonByMechanismId(mechanismId));
        }
        return meMechanismDTO;
    }

    /**
     * 查询机构联系人
     * @param mechanismId
     * @return
     */
    private List<MechanismContactPersonDTO> findContactPersonByMechanismId(Integer mechanismId) {
        return mechanismMapper.findContactPersonByMechanismId(mechanismId);
    }

    @Override
    public MechanismDTO findMechanismByccount(String account) {
        String key = FIND_MECHANISM_BYCCOUNT +account;
        MechanismDTO meMechanismDTO = (MechanismDTO) redisUtils.get(key);
        if(meMechanismDTO ==null ){
            meMechanismDTO = mechanismMapper.findMechanismByccount(account);
            redisUtils.set(key,meMechanismDTO, RedisUtils.TIME_MINUTE_10);
        }
        return meMechanismDTO;
    }
    /**
     * 编辑机构 ID not null is edit else is insert
     * @param mechanismDO
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result saveMechanismDO(MechanismDO mechanismDO) {
        String key  = SAVE_MECHANISM_DO+mechanismDO.getAccount();
        String token = redisLock.tryLock(key, TimeUnit.SECONDS.toMillis(5));
        try{
            if(token != null) {
                if(StringUtils.isNotBlank(mechanismDO.getPassword())){
                    mechanismDO.setPassword(Md5Util.MD5(mechanismDO.getPassword()));
                }
                if(mechanismDO.getId()!=null){
                    MechanismDTO old = mechanismMapper.findMechanismById(mechanismDO.getId());
                    if(old == null){
                        return Result.fail("机构不存在");
                    }
                    //判断帐号是否重复
                    MechanismDTO oldAccount = mechanismMapper.findMechanismByccount(mechanismDO.getAccount());
                    if(oldAccount!=null && !oldAccount.getId().equals(mechanismDO.getId())){
                        return Result.fail(CodeEnum.FAILED.getValue(),"机构账号已存在");
                    }

                    //判断机构名称是否重复
                    MechanismDTO oldName = mechanismMapper.findMechanismByName(mechanismDO.getName());
                    if(oldName!=null && !oldName.getId().equals(mechanismDO.getId())){
                        return Result.fail(CodeEnum.FAILED.getValue(),"机构名称已存在");
                    }
                    String rootmsg="-";
                    //更新
                    mechanismMapper.updateMechanism(mechanismDO);
                    if(StringUtils.isNotBlank(mechanismDO.getName()) && !mechanismDO.getName().equals(oldAccount.getName())){
                        //更新机构根结点名称
                        Result result = collegeService.updateCollegeRootNodeName(mechanismDO.getId(), mechanismDO.getName());
                        rootmsg += result.getMessage();
                    }
                    Map<String,Object> rs= Maps.newHashMap();
                    rs.put(MECHANISM_ID,mechanismDO.getId());

                    rs.put(MESSAGE,"机构更新成功"+rootmsg);
                    return Result.ok(rs);
                }

                //判断帐号是否重复
                MechanismDTO oldAccount = mechanismMapper.findMechanismByccount(mechanismDO.getAccount());
                if(oldAccount!=null){
                    return Result.fail(CodeEnum.FAILED.getValue(),"机构账号已存在");
                }
                //判断机构名称是否重复
                MechanismDTO oldName = mechanismMapper.findMechanismByName(mechanismDO.getName());
                if(oldName!=null){
                    return Result.fail(CodeEnum.FAILED.getValue(),"机构名称已存在");
                }
                mechanismMapper.insertMechanism(mechanismDO);
                collegeService.createMechanismCollegeRoot(mechanismDO.getId(),mechanismDO.getName());
                Map<String,Object> rs= Maps.newHashMap();
                rs.put(MECHANISM_ID,mechanismDO.getId());
                rs.put(MESSAGE,"机构创建成功");
                return Result.ok(rs);
            } else {
                return Result.fail(CodeEnum.FAILED.getValue(),"重复提交");
            }
        }catch (Exception e){
            log.error("机构创建失败");
            throw new RuntimeException(ExceptionUtils.getStackTrace(e));
        }finally {
            if(token!=null) {
                redisLock.unlock(key, token);
            }
        }
    }

    /**
     * 编辑机构联系人 ID not null is edit else is insert
     * @param personList
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result saveContactPerson(List<MechanismContactPersonDO> personList) {
        if(!CollectionUtils.isEmpty(personList)){
            MechanismDTO mechanism=getMechanismById(personList.get(0).getMechanismId());
            if(mechanism==null){
                return Result.fail(CodeEnum.FAILED.getValue(),"机构不存在");
            }
            personList.stream().forEach(p->p.setIsDelete(0));
            Integer mechanismId =personList.get(0).getMechanismId();
            String key = SAVE_CONTACT_PERSON+mechanismId;
            String token = redisLock.tryLock(key, TimeUnit.SECONDS.toMillis(10));
            try{
                if(token != null) {
                    List<MechanismContactPersonDO> addList = personList.stream().filter(p-> p.getId() ==null ).collect(Collectors.toList());
                    List<MechanismContactPersonDO> updateList = personList.stream().filter(p-> p.getId() !=null ).collect(Collectors.toList());
                    List<Integer> updateIdList =null;
                    if(!CollectionUtils.isEmpty(updateList)){
                        updateIdList = updateList.stream().map(MechanismContactPersonDO::getId).collect(Collectors.toList());
                        mechanismMapper.updateContactPersonList(updateList);
                    }
                    //删除不在修改列表的联系人(修改列表空则全删)
                    mechanismMapper.delPersonOfNotInUpdateList(mechanismId ,updateIdList);
                    if(!CollectionUtils.isEmpty(addList)){
                        mechanismMapper.insertContactPersonList(addList);
                    }
                    String mechanismKey = FIND_MECHANISM_BY_ID+mechanismId;
                    redisUtils.del(mechanismKey);
                } else {
                    return Result.fail(CodeEnum.FAILED.getValue(),"未获得锁");
                }
            } catch (Exception e){
                log.error("{}",ExceptionUtils.getStackTrace(e));
            }
        }
        return Result.ok(personList);
    }

    /**
     * 删除机构
     * @param id
     * @return
     */
    @Override
    public Result deleteMechanism(Integer id) {
        if(Constant.YUNYING_MECHANISM_ID.equals(id)){
            return Result.ok("默认图书馆不可删除");
        }
        MechanismOpenDO effective = mechanismOpenMapper.findEffectiveOpenByMechanismId(id, MechanismOpenTypeEnum.ALL_OPEN.getType());
        if(effective== null){
            Integer num = mechanismMapper.deleteMechanism(id);
            return Result.ok(num);
        }
        return Result.fail("机构已存在开通记录，不允许删除");
    }

    /**
     * 判断机构是否有开通记录
     * @param mechanismId
     * @return
     */
    @Override
    public Result isOpened(Integer mechanismId) {
        String key = IS_OPENED_OFMECHANISM +mechanismId;
        Result result = (Result) redisUtils.get(key);
        if(result ==null ){
            result = new Result(CodeEnum.SUCCESS.getValue(),"机构没有开通记录",false);
            MechanismOpenDO effective = mechanismOpenMapper.findEffectiveOpenByMechanismId(mechanismId, MechanismOpenTypeEnum.ALL_OPEN.getType());
            if(effective!=null){
                result.setData(true);
                result.setMessage("机构已存在开通记录，不允许删除");
            }
            redisUtils.set(key,result, RedisUtils.TIME_MINUTE_10);
        }
        return result;
    }

    @Override
    public FrontPageDTO getMechanismAccessUrl(String mechanismId) {
        String keys = RedisKeyGenerator.getKey(MechanismService.class, GET_MECHANISM_ACCESS_URL,mechanismId);
        FrontPageDTO frontPageDTO = (FrontPageDTO) redisUtils.get(keys);
        if(frontPageDTO == null){
            frontPageDTO = new FrontPageDTO();
            MechanismDTO mechanismDTO = getMechanismById(Integer.parseInt(mechanismId));
            if(mechanismDTO != null){
                frontPageDTO.setName(mechanismDTO.getName());
                frontPageDTO.setLoginLogo(mechanismDTO.getLoginLogo());
                frontPageDTO.setNavbarLogo(mechanismDTO.getNavbarLogo());
                frontPageDTO.setShowName(mechanismDTO.getShowName());
                frontPageDTO.setAppLoginLogo(mechanismDTO.getAppLoginLogo());
                frontPageDTO.setAppNavbarLogo(mechanismDTO.getAppNavbarLogo());
                frontPageDTO.setAppShowName(mechanismDTO.getAppShowName());
            }
            redisUtils.set(keys,frontPageDTO, RedisUtils.TIME_MINUTE_10);
        }
        return frontPageDTO;
    }

    /**
     * 是否限制IP
     * @param mid
     * @param restrict
     * @return
     */
    @Override
    public Result restrictIp(Integer mid, Integer restrict) {
        try {
            restrict = restrict.equals(1)?restrict:0;
            mechanismMapper.restrictIp(mid,restrict);
            return Result.ok(0);
        }catch (Exception e){
            return Result.fail();
        }
    }

    /**
     * 添加IP
     * @param mechanismId
     * @param ip
     * @return
     */
    @Cacheable(prefix = "ipList",fieldKey = "#mechanismId",cacheOperation = Cacheable.CacheOperation.UPDATE)
    @Transactional(rollbackFor = Exception.class)
    @RepeatableCommit
    @Override
    public Result addIp(Integer mechanismId, String ip) {
        MechanismRestrictIpDO restrictIpDO = new  MechanismRestrictIpDO();
        try {
            restrictIpDO.setIp(ip);
            restrictIpDO.setMechanismId(mechanismId);
            mechanismMapper.insertIp(restrictIpDO);
        }catch (Exception e){
            log.error("{}", ExceptionUtils.getStackTrace(e));
            throw e;
        }
        return Result.ok(restrictIpDO.getId());
    }

    /**
     * 删除机构限制IP
     * @param ipId
     * @return
     */
    @Cacheable(prefix = "ipList",fieldKey = "#mechanismId",cacheOperation = Cacheable.CacheOperation.UPDATE)
    @RepeatableCommit
    @Override
    public Result deleteIp(Integer mechanismId, Integer ipId) {
        Integer num = mechanismMapper.deleteIp(ipId);
        return Result.ok(num);
    }
    /**
     * IP列表
     * @param query
     * @return
     */
    @Cacheable(prefix = "ipList",fieldKey = "#query.mechanismId")
    @Override
    public List ipList(IpQuery query) {
        try {
            List<MechanismRestrictIpDTO> list = mechanismMapper.ipList(query);
            return list;
        }catch (Exception e){
            log.error("{}", ExceptionUtils.getStackTrace(e));
            throw e;
        }
    }

    /**
     * 验证机构账号是否可用
     * @param account
     * @return
     */
    @Override
    public Result verifyAccount(String account) {
        AccountVO accountVO = new AccountVO();
        //判断帐号是否重复
        MechanismDTO oldAccount = mechanismMapper.findMechanismByccount(account);
        accountVO.setExist(oldAccount != null);
        if(accountVO.isExist()){
            accountVO.setMechanismId(oldAccount.getId());
        }
        return Result.ok(accountVO);
    }

    @RepeatableCommit
    @Override
    public Result saveFakeset(FakeDateSetDO fakeDateSetDO) {
        if(fakeDateSetDO.getMechanismId() == null || fakeDateSetDO.getMechanismId() <=0){
            return Result.fail("机构不在");
        }
        FakeDateSetDO old =  mechanismMapper.getFakeset(fakeDateSetDO.getMechanismId());
        if(old!=null){
            mechanismMapper.updateFakeset(fakeDateSetDO);
        }else {
            mechanismMapper.saveFakeset(fakeDateSetDO);
        }
        return Result.ok(0);
    }

    @Override
    public Result getFakeset(Integer mechanismId) {
        FakeDateSetDO fakeDateSetDO =  mechanismMapper.getFakeset(mechanismId);
        return Result.ok(fakeDateSetDO);
    }
}

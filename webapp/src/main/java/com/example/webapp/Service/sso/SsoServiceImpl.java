package com.example.webapp.Service.sso;

import com.example.webapp.DO.MechanismDO;
import com.example.webapp.DO.UserDO;
import com.example.webapp.result.Result;
import com.example.webapp.utils.AppletsUtil;
import com.example.webapp.utils.UserThreadLocal;
import com.example.webapp.utils.WXCode2SessionDTO;
import com.example.webapp.utils.WXUserInfoDTO;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class SsoServiceImpl implements SsoService {
    public static final int USER_TOKENEXP_MINUTES = 30 * 24 * 60;
    public static final int USER_TOKENEXP_MINUTES_10Year = 10*12*30 * 24 * 60;
    public static final String SEND_VERIFY_CODE = "SEND_VERIFY_CODE_1";
    public static final String GET_MECHANISM_BY_ACCOUNT = "getMechanismByAccount";
    public static final String ERROR_MSG_MOBILE_REGISTER = "当前手机号已注册其他图书馆，请更换手机号登录";
    public static final String CODE = "code";
    public static final String DISPLAYNAME = "displayname";
    public static final Integer INT_1 = 1;

    public static final int ERROR_NUM = 5;
    public static final String LOCK = "LOCK";
    public static final String USER_LOGIN_FAILE_NUM_KEY = "USER_LOGIN_FAILE_NUM_KEY_";
    public static final String USER_LOGIN_FAILE_NUM_LOCK_KEY = "USER_LOGIN_FAILE_NUM_LOCK_KEY_";
    public static final String ADMIN = "admin";

    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private RedisLock redisLock;
    @Autowired
    private SsoMapper ssoMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private MechanismService mechanismService;
    @Autowired
    private ReaderDockingConfig readerDockingConfig;
    @Autowired
    private MechanismOpenService mechanismOpenService;

    @Value("${reader.docking.shoudu.code:''}")
    private String shouDuTuShuGuanCode;
    @Value("${reader.docking.shoudu.aleph.session.url:''}")
    private String alephSessionUrl;

    @Value("${default.mobile:#{null}}")
    private String defaultMobile;
    @Value("${default.verifycode:#{null}}")
    private String defaultVerifycode;

    @Value("${yunying.admin.pwd:#{null}}")
    private String yunyingAdminPwd;


    /**
     * 微信手机授权
     * @param code
     * @param encryptedData
     * @param iv
     * @param mark
     * @param aci        机构认证信息
     * @return
     */
    @Override
    public Result loginWx(String code, String encryptedData, String iv, String mark,String aci){
        try {
            String mid;
            try {
                mid = EncryptUtil.getUserIdBySid(aci);
            }catch (Exception e){
                return Result.fail("机构信息认证失败");
            }

            WXCode2SessionDTO code2SessionDTO = AppletsUtil.code2Session(code);
            if(AppletsUtil.isSuccess(code2SessionDTO.getErrcode())){
                WXPhoneNumberDTO phoneNumberDTO = AppletsUtil.getPhoneNumber(encryptedData,code2SessionDTO.getSession_key(),iv);
                if(phoneNumberDTO != null){
                    return mobileLogin(phoneNumberDTO.getPhoneNumber(), mark,mid,code2SessionDTO.getOpenid());
                }
            }else {
                return Result.fail(code2SessionDTO.getErrcode(),code2SessionDTO.getErrmsg());
            }
            return Result.fail("微信手机授权失败");
        } catch (Exception e) {
            log.error("{}", ExceptionUtils.getStackTrace(e));
            return Result.fail(ExceptionUtils.getStackTrace(e));
        }
    }

    /**
     * 读者证号登录
     * @param cardNo 读者证号
     * @param password 密码
     * @param aci      机构认证信息( 空 或 sid 或 机构ID)
     * @param mark
     * @return
     */
    @Override
    public Result loginid(String cardNo, String password, String aci, String mark) {
        try {
            String mid = EncryptUtil.getUserIdBySid(aci);
            Result<UserDO> r = certifiedReaderCardNo(cardNo, password, mid);
            if(CodeEnum.SUCCESS.getValue().equals(r.getCode())){
                //读者证认证成功，验证/注册 本地用户
                return readerLogin(r.getData(), mid,mark);
            }
        }catch (Exception e){
            return Result.fail("读者证未对接，请联系图书馆");
        }
        return Result.fail("读者证未对接，请联系图书馆");
    }


    /** 验证码 注册/登录
     * @param mobile     手机号
     * @param verifyCode 随机验证码
     * @param use        使用方式 1-快速注册时使用  2-快速登录时使用 3-找回密码时使用的发送验证码 4-添加或者修改绑定手机时使用 5-修改密码时使用
     * @param aci        机构认证信息( 空 或 sid 或 机构ID)
     * @return
     */
    @RepeatableCommit
    @Override
    public Result loginvc(String mobile, String verifyCode, String use, String appId,String aci) {
        String mid;
        try {
            mid = EncryptUtil.getUserIdBySid(aci);
        }catch (Exception e){
            return Result.fail("机构信息认证失败");
        }
        if (verifyCode(mobile,verifyCode,use) && VerifyCodeTypeEnum.VERIFY_CODE_USE_QUICK_REGIST.getValue().equals(use) ) {
            return mobileLogin(mobile, appId,mid);
        }
        return Result.fail("验证码错误，请确认");
    }

    /**
     * 手机一键登录
     * @param mobile
     * @param appId
     * @param aci        机构认证信息
     * @return
     */
    @Override
    public Result loginkey(String mobile, String appId,String aci) {
        String mid;
        try {
            mid = EncryptUtil.getUserIdBySid(aci);
        }catch (Exception e){
            return Result.fail("机构信息认证失败");
        }
        try {
            mobile = AESForNodejs.decrypt(mobile,  AESForNodejs.SEED);
            return mobileLogin(mobile, appId,mid);
        } catch (Exception e) {
            log.error("{}",ExceptionUtils.getStackTrace(e));
        }
        return Result.fail("手机一键登录失败");
    }

    /**
     * 用户端自动登录机构,机构没开启限制 不让自动登录
     * @param aci
     * @return
     */
    @Override
    public Result loginAuto(String aci) {
        UserDto userDto = new UserDto();
        try {
            String mid = EncryptUtil.getUserIdBySid(aci);
            if(!IpUtil.isComputer() ){
                return Result.fail("非PC端不能自动登录");
            }
            MechanismDTO mechanismDO= mechanismService.getMechanismById(Integer.valueOf(mid));
            if(mechanismDO !=null && !Constant.RESTRICT_IP.equals(mechanismDO.getIpRestrict())){
                return Result.fail("未开启IP限制不能自动登录");
            }

            Result verifyResult = verifyMechanism(Integer.valueOf(mid));
            if (CodeEnum.FAILED.getValue().equals(verifyResult.getCode())){
                return verifyResult;
            }
            userDto.setNickName(Constant.TOURIST_NAME);
            userDto.setId(Constant.TOURIST_ID);
            userDto.setMechanismId(Integer.valueOf(mid));
            userDto.setType(UserTypeEnum.TOURIST.getType());
            userDto.setMechanismName(mechanismDO.getName());
            userDto.setShowName(mechanismDO.getShowName());
            userDto.setAppShowName(mechanismDO.getAppShowName());
            return getLoginToken(userDto, PlatformMarkEnum.CLIENT.getMark(), null);
        } catch (Exception e) {
            log.error("{}", ExceptionUtils.getStackTrace(e));
            Result.fail(ExceptionUtils.getStackTrace(e));
        }
        return Result.ok(userDto);
    }

    /**
     * 读者证免登录
     * @param aci
     * @return
     */
    @Override
    public Result loginNo(String aci) {
        UserDto userDto = new UserDto();
        try {
            String mid = EncryptUtil.getUserIdBySid(aci);
            MechanismDTO mechanismDO= mechanismService.getMechanismById(Integer.valueOf(mid));
            Result verifyResult = verifyMechanism(Integer.valueOf(mid),false);
            if (CodeEnum.FAILED.getValue().equals(verifyResult.getCode())){
                return verifyResult;
            }
            userDto.setNickName(Constant.TOURIST_NAME);
            userDto.setId(Constant.TOURIST_ID);
            userDto.setMechanismId(Integer.valueOf(mid));
            userDto.setType(UserTypeEnum.TOURIST.getType());
            userDto.setMechanismName(mechanismDO.getName());
            userDto.setShowName(mechanismDO.getShowName());
            userDto.setAppShowName(mechanismDO.getAppShowName());
            return getLoginToken(userDto, PlatformMarkEnum.CLIENT.getMark(), null,USER_TOKENEXP_MINUTES_10Year);
        } catch (Exception e) {
            log.error("{}", ExceptionUtils.getStackTrace(e));
            Result.fail(ExceptionUtils.getStackTrace(e));
        }
        return Result.ok(userDto);
    }

    /**
     * 机构运营端-用户名密码登录
     * @param userName 用户名
     * @param password 密码
     * @param mark
     * @return
     */
    @Override
    public Result login(String userName, String password, String mark){
        return login(userName,password, mark,"");
    }
    /**
     * 用户端-用户名密码登录
     * @param userName 用户名
     * @param password 密码
     * @param mark
     * @param aci 机构认证信息
     * @return
     */
    @Override
    public Result login(String userName, String password, String mark,String aci) {
        UserDto userDto = new UserDto();
        try {
            PlatformMarkEnum prefix= PlatformMarkEnum.getTypeEnum(mark);
            if(prefix.equals(PlatformMarkEnum.ENTERPRISE)){
                if(isFreeeze(userName,PlatformMarkEnum.ENTERPRISE)){
                    Long le = redisUtils.getExpire(USER_LOGIN_FAILE_NUM_LOCK_KEY+PlatformMarkEnum.ENTERPRISE.getMark()+userName);
                    return Result.fail("连续5次登录不成功，您的账号暂时被系统锁定30分钟，请"+(le/60+1)+"分钟后重新登录。");
                }
                if(ADMIN.equals(userName) && StringUtils.isNotBlank(yunyingAdminPwd) && yunyingAdminPwd.equals(EncryptUtil.md5(password))){
                    userDto.setNickName("管理员");
                    userDto.setId(0);
                }else {
                    addLoginErrorNum(userName,PlatformMarkEnum.ENTERPRISE);
                    return Result.fail("账号或密码错误!!");
                }
            }else if(prefix.equals(PlatformMarkEnum.MECHANISM)){
                if(isFreeeze(userName,PlatformMarkEnum.MECHANISM)){
                    Long le = redisUtils.getExpire(USER_LOGIN_FAILE_NUM_LOCK_KEY+PlatformMarkEnum.MECHANISM.getMark()+userName);
                    return Result.fail("连续5次登录不成功，您的账号暂时被系统锁定30分钟，请"+(le/60+1)+"分钟后重新登录。");
                }
                MechanismDO mechanismDO =getMechanismByAccount(userName);
                if(mechanismDO != null && Md5Util.MD5(password).equals(mechanismDO.getPassword())){
                    userDto = parseMechanismUser( mechanismDO);
                }else {
                    addLoginErrorNum(userName,PlatformMarkEnum.MECHANISM);
                    return Result.fail("账号或密码错误!!");
                }
            }else if(prefix.equals(PlatformMarkEnum.CLIENT)){
                String mid;
                try {
                    mid = EncryptUtil.getUserIdBySid(aci);
                }catch (Exception e){
                    return Result.fail("机构信息认证失败");
                }
                Result verifyResult = verifyMechanism(Integer.valueOf(mid));
                if (CodeEnum.FAILED.getValue().equals(verifyResult.getCode())){
                    return verifyResult;
                }
                UserDO old = userService.getUserByName(userName);
                if(old == null || !old.getIsDelete().equals(0) || !old.getPassword().equals(Md5Util.MD5(password))){
                    return Result.fail("用户不存在,请联系管理员!");
                }
                if(Constant.STOP.equals(old.getStatus())){
                    return Result.fail("已停用,请联系管理员!");
                }
                if(isNotMechanismUser(mid, old)){
                    return Result.fail(ERROR_MSG_MOBILE_REGISTER);
                }
                updateMechanismId(mid, old);
                userDoToDto(userDto, old);
            }
            return getLoginToken(userDto, mark, null);
        } catch (Exception e) {
            log.error("{}", ExceptionUtils.getStackTrace(e));
            Result.fail(ExceptionUtils.getStackTrace(e));
        }
        return Result.ok(userDto);
    }

    private boolean isFreeeze(String userName,PlatformMarkEnum platformMark) {
        String l = (String) redisUtils.get(USER_LOGIN_FAILE_NUM_LOCK_KEY+platformMark.getMark()+userName);
        return LOCK.equals(l);
    }

    private void addLoginErrorNum(String userName,PlatformMarkEnum platformMark){
        Integer num = (Integer) redisUtils.get(USER_LOGIN_FAILE_NUM_KEY+platformMark.getMark()+userName);
        num = num == null ?1: num+1;
        redisUtils.set(USER_LOGIN_FAILE_NUM_KEY+platformMark.getMark()+userName,num, TimeUnit.MINUTES.toSeconds(1));
        if(num >=ERROR_NUM){
            redisUtils.set(USER_LOGIN_FAILE_NUM_LOCK_KEY+platformMark.getMark()+userName, LOCK, TimeUnit.MINUTES.toSeconds(30));
        }
        String l = (String) redisUtils.get(USER_LOGIN_FAILE_NUM_LOCK_KEY+platformMark.getMark()+userName);
        Long le = redisUtils.getExpire(USER_LOGIN_FAILE_NUM_LOCK_KEY+platformMark.getMark()+userName);
        Integer n =(Integer) redisUtils.get(USER_LOGIN_FAILE_NUM_KEY+platformMark.getMark()+userName);
        Long ne = redisUtils.getExpire(USER_LOGIN_FAILE_NUM_KEY+platformMark.getMark()+userName);
        log.info("登陆失败次数:{},次数过期:{} 秒,lock:{},lock过期:{} 秒",n,ne,l,le);
    }

    private void updateMechanismId(String mid, UserDO old) {
        if (Constant.YUNYING_MECHANISM_ID.equals(old.getMechanismId()) && !Constant.YUNYING_MECHANISM_ID.equals( Integer.valueOf(mid))) {
            UserDO userDO = new UserDO();
            userDO.setId(old.getId());
            userDO.setMechanismId(Integer.valueOf(mid));
            MechanismDTO mechanismDO = mechanismService.getMechanismById(userDO.getMechanismId());
            if(mechanismDO != null){
                old.setAppShowName(mechanismDO.getAppShowName());
                old.setMechanismName(mechanismDO.getName());
                old.setShowName(mechanismDO.getShowName());
            }
            userService.updateUserInfo(userDO);
        }
    }

    /**
     * 用户端登录验证机构
     * @param mechanismId
     * @return
     */
    private Result verifyMechanism(Integer mechanismId) {
        return  verifyMechanism(mechanismId,true);
    }
    private Result verifyMechanism(Integer mechanismId,boolean verifyIp) {
        if(Constant.YUNYING_MECHANISM_ID.equals(mechanismId)){
            //默认图书馆不做校验
            return Result.ok(0);
        }
        MechanismDTO mechanismDO= mechanismService.getMechanismById(mechanismId);
        String errorInfo ="图书馆已过期或未开通，请联系图书馆";
        if(mechanismDO==null || mechanismDO.getIsDelete().equals(1)){
            return Result.fail(errorInfo);
        }
        Integer practiceType = mechanismOpenService.getPracticeOpenStatus(mechanismId);
        if(OpenStatusEnum.PRACTICE_NO_OPEN.getType().equals(practiceType)){
            return Result.fail(errorInfo);
        } else{
            //开通实训
            if(OpenStatusEnum.PRACTICE_STOP.getType().equals(practiceType)){
                return Result.fail(errorInfo);
            }
            if(OpenStatusEnum.PRACTICE_EXPIRED.getType().equals(practiceType)){
                return Result.fail(errorInfo);
            }
        }
        // pc端 IP限制
        if(verifyIp && IpUtil.isComputer() && Constant.RESTRICT_IP.equals(mechanismDO.getIpRestrict())){
            IpQuery query =new IpQuery();
            query.setMechanismId(mechanismId);
            List<MechanismRestrictIpDTO> list = mechanismService.ipList(query);
            String ip = IpUtil.getIpAddr();
            if(CollectionUtils.isNotEmpty(list)){
                boolean restrict = false;
                for (MechanismRestrictIpDTO restrictIpDO : list) {
                    if(IpUtil.isInRange(ip,restrictIpDO.getIp())){
                        restrict = true;
                        break;
                    }
                }
                log.info("区域限制 客户端ip:{}",ip);
                if(!restrict){
                    return Result.fail(ip+" 该ip已被限制访问系统，请联系管理员!");
                }
            }else {
                return Result.fail(ip+" 该ip已被限制访问系统，请联系管理员!");
            }
        }
        return Result.ok(0);
    }

    /**
     * 手机验证码/一键登录调用
     * @param mobile
     * @param appId
     * @param mid
     * @return
     */
    private Result mobileLogin(String mobile, String appId,String mid) {
        return  mobileLogin(mobile,appId,mid,null);
    }

    /**
     * 微信手机授权登录调用
     * @param mobile
     * @param appId
     * @param mid
     * @param openId
     * @return
     */
    private Result mobileLogin(String mobile, String appId,String mid,String openId) {
        UserDto userDto = new UserDto();
        try{
            if(StringUtils.isBlank(mobile)){
                return Result.fail("用户不存在");
            }
            Result verifyResult = verifyMechanism(Integer.valueOf(mid));
            if (CodeEnum.FAILED.getValue().equals(verifyResult.getCode())){
                return verifyResult;
            }
            if(mobile.equals(defaultMobile)){
                userDto.setMechanismId(Integer.valueOf(mid));
                userDto.setPhone(mobile);
                userDto.setPhone(mobile);
                userDto.setNickName("可登录所有图书馆");
                return getLoginToken(userDto, appId, null);
            }
            UserDO old = getUserOfWxInfo(openId, mobile);
            if (old != null) {
                log.info("用户登录验证所属机构:mobile={},aci={},userId={},midOfUser={}",mobile,mid,old.getId(),old.getMechanismId());
                if(isNotMechanismUser(mid, old)){
                    return Result.fail(ERROR_MSG_MOBILE_REGISTER);
                }
                log.info("用户登录成功:mobile={},openId={},userId={}",mobile,openId,old.getId());
                updateMechanismId(mid, old);
                userDoToDto(userDto, old);
            } else {
                UserDO user = new UserDO();
                user.setMechanismId(Integer.valueOf(mid));
                user.setPhone(mobile);
                user.setOpenId(getDefaultValue(openId));
                String userName = userService.getRandomUserName();
                if (StringUtils.isNotBlank(userName)) {
                    user.setUserName(userName);
                    // 默认密码：dc483e80a7a0bd9ef71d8cf973673924
                    user.setPassword(Md5Util.MD5(Constant.ADD_USER_DEFAULT_PWD));
                    user.setNickName(userName);
                }
                MechanismDTO mechanismDO = mechanismService.getMechanismById(user.getMechanismId());
                if(mechanismDO != null){
                    user.setAppShowName(mechanismDO.getAppShowName());
                    user.setMechanismName(mechanismDO.getName());
                    user.setShowName(mechanismDO.getShowName());
                }
                user.setHeadImg("");
                user.setUnionId("");
                user.setDockingType("");
                user.setReaderBadge("");
                user.setPinyinAcronym("");
                user.setJobNumber("");
                userService.insert(user);
                log.info("用户登录成功 创建新用户:mobile={},userId={}",mobile,user.getId());
                userDoToDto(userDto, user);
            }
            return getLoginToken(userDto, appId, null);
        }catch (Exception e){
            log.error("登录失败:{}",ExceptionUtils.getStackTrace(e));
            throw new RuntimeException(ExceptionUtils.getStackTrace(e));
        }
    }

    /**
     * 读者证登录调用
     * @param user
     * @param appId
     * @return
     */
    private Result readerLogin(UserDO user, String mid,String appId) {
        UserDto userDto = new UserDto();
        try{
            Result verifyResult = verifyMechanism(user.getMechanismId());
            if (CodeEnum.FAILED.getValue().equals(verifyResult.getCode())){
                return Result.fail("读者证未对接，请联系图书馆");
            }

            UserDO old = userService.findUserByReaderBadge(user.getReaderBadge(),user.getMechanismId(),user.getDockingType());
            if (old != null) {
                if(isNotMechanismUser(mid, old)){
                    return Result.fail(ERROR_MSG_MOBILE_REGISTER);
                }
                updateMechanismId(mid, old);
                userDoToDto(userDto, old);
            } else {
                user.setPhone("");
                user.setOpenId("");
                String userName = userService.getRandomUserName();
                if (StringUtils.isNotBlank(userName)) {
                    user.setUserName(userName);
                    user.setPassword(Md5Util.MD5(Constant.ADD_USER_DEFAULT_PWD));
                }
                user.setHeadImg("");
                user.setUnionId("");
                user.setJobNumber("");
                userService.insert(user);
                userDoToDto(userDto, user);
            }
            return getLoginToken(userDto, appId, null);
        }catch (Exception e){
            log.error("登录失败:{}",ExceptionUtils.getStackTrace(e));
            throw new RuntimeException(ExceptionUtils.getStackTrace(e));
        }
    }

    /**
     * 发送手机号机对应的验证码 一分钟内不可重复发送验证码
     * @param mobile 手机号
     * @param use    使用方式 1-快速注册时使用
     *                       2-快速登录时使用
     *                       3-找回密码时使用的发送验证码
     *                       4-添加或者修改绑定手机时使用
     */
    @Override
    public Result sendVerifyCode(String mobile, String use) {
        return sendVerifyCode(mobile, use, AlismsTempletEnum.TPL_VERIFY_CODE.getTempletCode());
    }

    /**
     * 发送手机号机对应的验证码 一分钟内不可重复发送验证码
     * @param mobile 手机号
     * @param use    使用方式
     * @param templetCode 消息模版
     */
    @Override
    public Result sendVerifyCode(String mobile, String use, String templetCode) {
        String key  = RedisKeyGenerator.getKey(SsoService.class, SEND_VERIFY_CODE,mobile,use);
        String token = redisLock.tryLock(key, TimeUnit.MINUTES.toMillis(1));
        try{
            if(token != null) {
                if(StringUtils.isNotBlank(mobile) && mobile.equals(defaultMobile)){
                    //测试代码
                    this.saveVerifyCode(mobile, defaultVerifycode, VerifyCodeTypeEnum.instance(use));
                    log.info("默认验证码 redis-save-code ,测试手机mobile:{},random:{},use:{}",mobile,defaultVerifycode,use);
                    return Result.ok(0);
                }
                String random = VerifyUtil.randomMobiCode();
                Result result = AliDysms.send(mobile,random,templetCode);
                log.info("send-code,mobile:{},random:{},use:{}",mobile,random,use);
                if(result.getCode().equals(CodeEnum.SUCCESS.getValue())){
                    this.saveVerifyCode(mobile, random, VerifyCodeTypeEnum.instance(use));
                    log.info("redis-save-code,mobile:{},random:{},use:{}",mobile,random,use);
                }
                return result;
            } else {
                String dbVc = (String) redisUtils.get(JedisKeys.verifyCodeKey(mobile, use));
                if(StringUtils.isNotBlank(dbVc)){
                    return Result.ok("验证码已发送到手机，请查收！");
                }
                return Result.fail(CodeEnum.FAILED.getValue(),"验证码获取失败，请重新发送！");
            }
        }catch (Exception e){
            throw new RuntimeException(ExceptionUtils.getStackTrace(e));
        }finally {
            if(token!=null) {
                redisLock.unlock(key, token);
            }
        }

    }

    /** 验证码 修改密码
     * @param mobile     手机号
     * @param verifyCode 随机验证码
     * @param use        使用方式 1-快速注册时使用  2-快速登录时使用 3-找回密码时使用的发送验证码 4-添加或者修改绑定手机时使用 5-修改密码时使用
     * @return
     */
    @Override
    public Result resetPwdByVerifycode(String mobile, String verifyCode, String use, String passwd) {
        if (verifyCode(mobile,verifyCode,use) && VerifyCodeTypeEnum.VERIFY_CODE_USE_UPDATE_PASSWORD.getValue().equals(use)) {
            UserDO old =  userService.findUserByPhone(mobile);
            if(old!=null){
                UserDO user = new UserDO();
                user.setId(old.getId());
                user.setPassword(Md5Util.MD5(passwd));
                userService.updateUserInfo(user);
                return Result.ok("已修改");
            }

        }
        return Result.fail("验证码错误，请确认");
    }

    /**
     * 修改手机
     * @param mobile 新手机
     * @param verifyCode
     * @param use
     * @param userId
     * @return
     */
    @Override
    public Result bindMobile(String mobile, String verifyCode, String use, Integer userId) {
        if (verifyCode(mobile,verifyCode,use) && VerifyCodeTypeEnum.VERIFY_CODE_USE_BIND_MOBILE.getValue().equals(use)) {
            UserDO user = new UserDO();
            user.setId(userId);
            user.setPhone(mobile);
            userService.updateUserInfo(user);
            return Result.ok("已修改");

        }
        return Result.fail("验证码错误，请确认");
    }

    @Override
    public Result verifyOldMobile(String mobile, String verifyCode, String use) {
        if (verifyCode(mobile,verifyCode,use) && VerifyCodeTypeEnum.VERIFY_OLD_MOBILE.getValue().equals(use)) {
            return Result.ok("OK");
        }
        return Result.fail("验证码错误，请确认");
    }

    /**
     * 保存手机号机对应的验证码
     * @param mobile 手机号
     * @param random 随机验证码
     * @param use    使用方式
     */
    public boolean saveVerifyCode(String mobile, String random, VerifyCodeTypeEnum use) {
        if (StringUtils.isBlank(mobile) ||StringUtils.isBlank(random) ||use == null) {
            return false;
        }
        String key = JedisKeys.verifyCodeKey(mobile, use.getValue());
        redisUtils.set(key,random,JedisKeys.VERIFY_CODE_EXPIRE);
        return true;
    }

    /** 验证通过删除验证码
     * @param mobile     手机号
     * @param verifyCode 随机验证码
     * @param use        使用方式 1-快速注册时使用  2-快速登录时使用 3-找回密码时使用的发送验证码 4-添加或者修改绑定手机时使用 5-修改密码时使用
     * @return
     */
    public boolean verifyCode(String mobile, String verifyCode, String use) {
        if (StringUtils.isBlank(mobile) || StringUtils.isBlank(verifyCode) || StringUtils.isBlank(use)) {
            return false;
        }
        String key = JedisKeys.verifyCodeKey(mobile, use);
        String dbVc = (String) redisUtils.get(key);
        if (StringUtils.isBlank(dbVc)) {
            log.info("verifyCode is null key:{},dbVc:{}",key,dbVc);
            return false;
        }
        if (verifyCode.equalsIgnoreCase(dbVc)) {
            clearMobileVerifyCode(mobile,dbVc,use);
            log.info("verifyCode clear key:{},dbVc:{}",key,dbVc);
            return true;
        }
        return false;
    }
    /**
     * 删除手机号机对应的验证码
     * @param mobile
     * @param random
     * @param use
     * @return
     */
    public boolean clearMobileVerifyCode(String mobile, String random, String use) {
        return this.clearMobileVerifyCode(mobile, random, VerifyCodeTypeEnum.instance(use));
    }

    public boolean clearMobileVerifyCode(String mobile, String random, VerifyCodeTypeEnum use) {
        if (StringUtils.isBlank(mobile)) {
            throw new RuntimeException("mobile is null");
        }
        if (StringUtils.isBlank(random)) {
            throw new RuntimeException("random is null");
        }
        if (use == null) {
            throw new RuntimeException("use is null");
        }
        if (log.isDebugEnabled()) {
            log.debug("===execute login service----mobile:[{}], random:[{}], use:[{}] ", mobile, random, use.getValue());
        }
        try {
            redisUtils.del(JedisKeys.verifyCodeKey(mobile, use.getValue()));
            return true;
        }  catch (Exception e) {
            log.error("Exception {}", e);
        }
        return false;
    }

    /**
     * 读者证信息认证
     * @param cardNo
     * @param password
     * @param mid
     * @return
     */
    private Result<UserDO> certifiedReaderCardNo(String cardNo, String password, String mid) throws Exception {
        Result r = Result.fail("读者证或密码错误！");
        DockingMechConfig dockingMechConfig = getDockingMechConfigByMechanism(mid);
        UserDO userDO = new UserDO();
        userDO.setMechanismId(Integer.valueOf(mid));
        userDO.setDockingType(dockingMechConfig.getDockingType());
        userDO.setReaderBadge(cardNo);
        if(DockingTypeEnum.SHOU_DU_TU_SHU_GUAN.getDockingType().equals(dockingMechConfig.getDockingType())){
            //首都图书馆认证:读者证登录（aleph登录）时,先获取sessionid
            String sessionXML = HttpUtil.sendGet(alephSessionUrl);
            String sessionId = HttpUtil.getXmlNodeString(sessionXML,"//session-id");
            //首都图书馆aleph系统对接读者证号认证地址
            String alephCardURL =dockingMechConfig.getDockingUrl() + "?OP=bor-auth_valid&id=" + cardNo + "&verification=" + password +"&session=" + sessionId;
            String htmlStr =HttpUtil.sendGet(alephCardURL);
            htmlStr = CharsetUtils.changeCharset(htmlStr,"UTF-8");
            String userId = HttpUtil.getXmlNodeString(htmlStr,"//bor-auth-valid/z303/z303-id");
            String nickName = HttpUtil.getXmlNodeString(htmlStr,"//bor-auth-valid/z303/z303-name");
            String error = HttpUtil.getXmlNodeString(htmlStr,"//bor-auth-valid/error");
            //账号密码验证通过后返回的xml有个 <z303-budget> 节点，里面如果是“STE01”（大小写敏感），就是首图少儿卡  ST001（大小写敏感）,成人卡
            String type = HttpUtil.getXmlNodeString(htmlStr,"//bor-auth-valid/z303/z303-budget");
            if(StringUtils.isBlank(error) && shouDuTuShuGuanCode.contains(DateTimeUtil.DELIMITER_ONE_UNDERLINE+type+DateTimeUtil.DELIMITER_ONE_UNDERLINE)){
                userDO.setPinyinAcronym(userId);
                userDO.setNickName(nickName);
                r = Result.ok(userDO);
            }
        }else if(DockingTypeEnum.TU_CHUANG.getDockingType().equals(dockingMechConfig.getDockingType())){
            //图创接口统一认证
            String time = DateTimeUtil.format(new Date(),DateTimeUtil.DATE_FORMAT_YYYYMMDDHHMMSS);
            String sn = EncryptUtil.md5(cardNo+dockingMechConfig.getStaticKey() +password+time);
            String url = dockingMechConfig.getDockingUrl()+"?uid="+cardNo+"&sn="+sn+"&time="+time;
            String rs = HttpUtil.sendGet(url);
            if(StringUtils.isNotBlank(rs)){
                JsonObject rsj  = new JsonParser().parse(rs).getAsJsonObject();
                Integer code =rsj.get(CODE).getAsInt();
                if(code.equals(INT_1)){
                    String displayname=rsj.get(DISPLAYNAME).getAsString();
                    userDO.setNickName(displayname);
                    r = Result.ok(userDO);
                }
            }
        }
        return r;
    }

    /**
     * 获取图书馆读者证对接信息
     * @param mid
     * @return
     */
    private DockingMechConfig getDockingMechConfigByMechanism(String mid) {
        DockingMechConfig dockingMechConfig =  null;
        try {
            Integer mechanismId = Integer.valueOf(mid);
            List<DockingMechConfig> mechanisms = readerDockingConfig.getMechanisms();
            for (DockingMechConfig mechanism : mechanisms) {
                if(mechanismId.equals(mechanism.getMechanismId())){
                    dockingMechConfig = mechanism;
                }
            }
        }catch (Exception e){
            log.error("读者证登录配置读取失败:{}",ExceptionUtils.getStackTrace(e));
        }
        if(dockingMechConfig == null){
            throw new RuntimeException("读者证登录配置读取失败");
        }
        return dockingMechConfig;
    }


    private String getDefaultValue(String value) {
        return StringUtils.isNotBlank(value)?value:"";
    }

    /**
     * 非机构用户
     * @param mid
     * @param old
     * @return
     */
    private boolean isNotMechanismUser(String mid, UserDO old) {
        if(String.valueOf(Constant.YUNYING_MECHANISM_ID).equals(mid)){
            //默认馆登录不验证机构
            log.info("默认馆登录不验证机构,mid:{},userId ={},midOfUser={}",mid,old.getId(),old.getMechanismId());
            return false;
        }
        if(Constant.YUNYING_MECHANISM_ID.equals(old.getMechanismId())){
            //默认馆用户用户登录其他馆 自动绑定改馆
            log.info("默认馆用户用户登录其他馆 ,mid:{},userId ={},midOfUser={}",mid,old.getId(),old.getMechanismId());
            old.setMechanismId(Integer.valueOf(mid));
            userService.updateUserInfo(old);
            return false;
        }
        boolean flag=  !Integer.valueOf(mid).equals(old.getMechanismId());
        log.info("验证是否是本机构用户,aci={},userId ={},midOfUser={},result={}",mid,old.getId(),old.getMechanismId(),flag);
        return flag;
    }

    private Result getLoginToken(UserDto user, String appId, String deviceId) {
        return  getLoginToken( user, appId, deviceId,USER_TOKENEXP_MINUTES);
    }
    private Result getLoginToken(UserDto user, String appId, String deviceId,Integer expMinutes) {
        deviceId ="a4:83:e7:65:56:7d";
        long leftLiveTime = RedisUtils.TIME_DAY_1;
        // 生成sid
        String sid = EncryptUtil.generateSid(String.valueOf(user.getId()),appId);
        //记录sid有效时间：一个月 里面存储的sid无实际意义
        redisUtils.set(JedisKeys.liveTimeKey(sid, appId), sid,leftLiveTime);
        // 策略:key是userid value是一个sid的set集合(一个用户id可以被多台设备登陆)
        redisUtils.sSet(JedisKeys.userLoginAppKey(appId, String.valueOf(user.getId())), sid);
        //记录用户本次登录的设备情况  并以设备id为key 记录设备上登录了那些 app 和对应的sid
        if (StringUtils.isNotBlank(deviceId)) {
            redisUtils.set(JedisKeys.liveUserDeviceIdKey(appId, sid),deviceId, leftLiveTime);
            //记录用户的设备上的在那些app上登录了那些用户和哪些
            redisUtils.hset(JedisKeys.deviceLiveLoginKey(deviceId), appId, sid);
        }
        user.setSid(sid);
        user.setExpMinutes(expMinutes);
        String token = JwtUtil.createToken(user);
        Map<String,String> tokenMap= Maps.newHashMap();
        tokenMap.put(ParamKeys.JWT_TOKEN,token);
        return Result.ok(tokenMap);
    }
    private void userDoToDto(UserDto userDto, UserDO old) {
        userDto.setId(old.getId());
        userDto.setUserName(old.getUserName());
        userDto.setPhone(old.getPhone());
        userDto.setHeadImg(old.getHeadImg());
        userDto.setNickName(old.getNickName());
        userDto.setOpenId(old.getOpenId());
        userDto.setGender(old.getGender());
        userDto.setMechanismId(old.getMechanismId());
        userDto.setMechanismName(old.getMechanismName());
        userDto.setAppLoginLogo(old.getAppLoginLogo());
        userDto.setLoginLogo(old.getLoginLogo());
        userDto.setShowName(old.getShowName());
        userDto.setAppShowName(old.getAppShowName());
        userDto.setPosition(old.getPosition());
        userDto.setType(old.getType());
    }
    private UserDto parseMechanismUser( MechanismDO mechanismDO) {
        UserDto user= new UserDto();
        user.setId(mechanismDO.getId());
        user.setMechanismId(mechanismDO.getId());
        user.setHeadImg(mechanismDO.getLoginLogo());
        user.setNickName(mechanismDO.getName());
        user.setUserName(mechanismDO.getAccount());
        return user;
    }

    /**
     * 微信手机授权登录时，根据微信授权信息 判断用户是否存在，同时更新用户openid和手机号
     * @param openId
     * @param phone
     * @return
     */
    private UserDO getUserOfWxInfo(String openId, String  phone) {
        UserDO old;
        UserDO openIdUser = null;
        if(StringUtils.isNotBlank(openId)){
            openIdUser = userService.findUserByOpenId(openId);
        }
        if(openIdUser != null){
            if(StringUtils.isBlank(openIdUser.getPhone()) || !openIdUser.getPhone().equals(phone)){
                openIdUser.setPhone(phone);
                userService.updateUserInfo(openIdUser);
            }
            old = openIdUser;
        }else {
            UserDO phoneUser = userService.findUserByPhone(phone);
            if(phoneUser !=null && StringUtils.isNotBlank(openId)){
                phoneUser.setOpenId(openId);
                userService.updateUserInfo(phoneUser);
            }
            old = phoneUser;
        }
        return old;
    }

    /**
     * 更新微信用户信息
     * @param code
     * @param encryptedData
     * @param iv
     * @return
     */
    @Override
    public Result updateUserInfo(String code, String encryptedData, String iv) {
        try {
            WXCode2SessionDTO code2SessionDTO = AppletsUtil.code2Session(code);
            if(AppletsUtil.isSuccess(code2SessionDTO.getErrcode())) {
                WXUserInfoDTO userInfo = AppletsUtil.getUserInfo(encryptedData, code2SessionDTO.getSession_key(), iv);
                if(userInfo != null){
                    UserDto userDto = UserThreadLocal.get();
                    UserDO  u=new UserDO();
                    u.setId(userDto.getId());
                    u.setNickName(userInfo.getNickName());
                    u.setHeadImg(userInfo.getAvatarUrl());
                    userService.updateUserInfo(u);
                    return Result.ok(userInfo) ;
                }
            }else {
                log.error("微信用户信息更新失败");
            }
        }catch (Exception e){
            log.error("{}", ExceptionUtils.getStackTrace(e));
            Result.fail(ExceptionUtils.getStackTrace(e));
        }
        return Result.ok(0);
    }

    private MechanismDO getMechanismByAccount(String userName) {
        String key = RedisKeyGenerator.getKey(SsoService.class, GET_MECHANISM_BY_ACCOUNT,userName);
        MechanismDO mechanismDO = (MechanismDO) redisUtils.get(key);
        if(mechanismDO==null){
            mechanismDO=ssoMapper.getMechanismByAccount(userName);
            redisUtils.set(key,mechanismDO,RedisUtils.TIME_SECOND_10);
        }
        return mechanismDO;
    }




    @Override
    public void logout(String sid) {
    }
}

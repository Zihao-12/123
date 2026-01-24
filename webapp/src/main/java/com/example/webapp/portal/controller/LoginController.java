package com.example.webapp.portal.controller;

import com.example.webapp.DO.UserDO;
import com.example.webapp.DTO.UpdateUserDTO;
import com.example.webapp.DTO.UserDto;
import com.example.webapp.Service.User.UserService;
import com.example.webapp.Service.sso.SsoService;
import com.example.webapp.annotation.*;
import com.example.webapp.common.redis.RedisKeyGenerator;
import com.example.webapp.common.redis.RedisUtils;
import com.example.webapp.enums.PlatformMarkEnum;
import com.example.webapp.enums.VerifyCodeTypeEnum;
import com.example.webapp.result.Result;
import com.example.webapp.third.AccessKeyIdSecretEnum;
import com.example.webapp.third.AliOSS;
import com.example.webapp.third.AttachmentTypeEnum;
import com.example.webapp.utils.AppletsUtilEnum;
import com.example.webapp.utils.ResponseUtil;
import com.example.webapp.utils.SnowflakeIdWorker;
import com.example.webapp.utils.UserThreadLocal;
import com.example.webapp.utils.http.HttpUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

@Api(tags = {"1000-登录"})
@RefreshScope
@RestController
@Component
@Slf4j
@RequestMapping("/")
public class LoginController  {

    public static final String CARDNO = "001000071864";
    public static final String DELIMITER_ONE_SLASH = "/";
    public static final String DELIMITER_ONE_PERIOD = ".";
    public static final String GET_UNLIMITED = "GET_UNLIMITED_1";
    @Autowired
    private SsoService ssoService;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private UserService userService;

    @Login
    @ApiOperation(value = "IP自动登录")
    @ApiImplicitParams({@ApiImplicitParam(name="aci",value="机构认证信息",required = true)})
    @GetMapping("/login-auto")
    public Result loginAuto(String aci) {
        Result result=  ssoService.loginAuto(aci);
        return result;
    }

    @Login
    @ApiOperation(value = "读者证免登录")
    @ApiImplicitParams({@ApiImplicitParam(name="aci",value="机构认证信息",required = true)})
    @GetMapping("/login-no")
    public Result loginNo(String aci) {
        Result result=  ssoService.loginNo(aci);
        return result;
    }


    @Login
    @LoginRecord
    @ApiOperation(value = "用户名密码登录- 手机注册登录生成用户名密码")
    @ApiImplicitParams({@ApiImplicitParam(name="username",value="用户名",required=true),
            @ApiImplicitParam(name="password",value="密码",required=true),
            @ApiImplicitParam(name="aci",value="机构认证信息")})
    @GetMapping("/login-c")
    public Result loginC(String username,String password,String aci) {
        Result result=  ssoService.login(username,password, PlatformMarkEnum.CLIENT.getMark(),aci);
        return result;
    }


    @Login
    @LoginRecord
    @RepeatableCommit
    @ApiOperation(value = "微信手机授权登陆接口")
    @ApiImplicitParams({@ApiImplicitParam(name="code",value="code",required=true),
            @ApiImplicitParam(name="encryptedData",value="encryptedData",required=true),
            @ApiImplicitParam(name="iv",value="iv",required=true),
            @ApiImplicitParam(name="aci",value="机构认证信息")})
    @GetMapping("/login-wx")
    public Result loginWx(String code,String encryptedData,String iv,String aci){
        Result result = ssoService.loginWx(code,encryptedData,iv, PlatformMarkEnum.CLIENT.getMark(),aci);
        return result;
    }

    @Login
    @LoginRecord
    @RepeatableCommit
    @ApiOperation(value = "读者证号登录",notes =
            " 1.H5登录（运营端生成登录地址） 支持手机和读者证登录         aci不能空，取登录地址中的aci值 \n" +
                    " 2.小程序登录 （H5跳转）       支持手机和读者证登录         aci不能空，值取H5登录用户中的 mechanismId "+
                    " 3.小程序登录（搜索进来）      只支持手机号登录             aci 为空 ")
    @ApiImplicitParams({@ApiImplicitParam(name="cardNo",value="读者证号",required=true,example = "001000071864"),
            @ApiImplicitParam(name="password",value="密码",required=true,example = "800108"),
            @ApiImplicitParam(name="aci",value="机构认证信息")})
    @GetMapping("/login-cn")
    public Result loginid(String cardNo,String password,String aci){
        aci=null;
        if(!CARDNO.equals(cardNo)){
            return Result.fail("读者证未对接，请联系图书馆!");
        }
        Result result = ssoService.loginid(cardNo,password, aci,PlatformMarkEnum.CLIENT.getMark());
        return result;
    }

    @Login
    @LoginRecord
    @RepeatableCommit
    @ApiOperation(value ="手机验证码注册/登陆",notes= "新用户会创建一个新账号用于用户名密码登录，用户名密码随机生成（测试使用openId作为登录密码）\n" +
            " 1.H5登录（运营端生成登录地址） 支持手机和读者证登录         aci不能空，取登录地址中的aci值 \n" +
            " 2.小程序登录 （H5跳转）       支持手机和读者证登录         aci不能空，值取H5登录用户中的 mechanismId "+
            " 3.小程序登录（搜索进来）      只支持手机号登录             aci 为空   \n")
    @ApiImplicitParams({@ApiImplicitParam(name="mobile",value="mobile",required=true),
            @ApiImplicitParam(name="verifyCode",value="verifyCode",required=true),
            @ApiImplicitParam(name="aci",value="机构认证信息")})
    @GetMapping("/login-vc")
    public Result loginvc(String mobile, String verifyCode,String aci){
        Result result = ssoService.loginvc(mobile,verifyCode, VerifyCodeTypeEnum.VERIFY_CODE_USE_QUICK_REGIST.getValue(), PlatformMarkEnum.CLIENT.getMark(),aci);
        return result;
    }

    @Login
    @LoginRecord
    @RepeatableCommit
    @ApiOperation(value ="手机一键登录")
    @ApiImplicitParams({@ApiImplicitParam(name="mobile",value="加密手机",required=true,example = "ad184fdc1bfaccbcf954288de8c61941"),
            @ApiImplicitParam(name="aci",value="机构认证信息")})
    @GetMapping("/login-ky")
    public Result loginkey(String mobile,String aci){
        Result result = ssoService.loginkey(mobile, PlatformMarkEnum.CLIENT.getMark(),aci);
        return result;
    }


    @ApiOperation(value = "1.发送验证码（通用）",notes = " 1-快速注册/登录 3-找回密码 4-添加或者修改绑定手机 5-修改密码 6-验证老手机号")
    @ApiImplicitParams({@ApiImplicitParam(name="mobile",value="mobile",required=true),
            @ApiImplicitParam(name="use",value="use",required=true)})
    @GetMapping("/send-verify-code")
    public Result sendVerifyCode(String mobile,String use){
        Result result = ssoService.sendVerifyCode(mobile, use);
        return result;
    }

    @LoginRequired
    @ApiOperation(value ="2.发送验证码（用于修改密码）")
    @GetMapping("/api/portal/send-reset-pwd-code")
    public Result sendResetPwdCode(){
        UserDto login = UserThreadLocal.get();
        if(login == null){
            Result.fail("登陆失效");
        }
        Result result = ssoService.sendVerifyCode(login.getPhone(), VerifyCodeTypeEnum.VERIFY_CODE_USE_UPDATE_PASSWORD.getValue());
        return result;
    }

    @LoginRequired
    @ApiOperation(value = "3.发送验证码(用于验证老手机号)")
    @GetMapping("/api/portal/send-old-mobile-code")
    public Result sendOldMobileCode(){
        UserDto login = UserThreadLocal.get();
        if(login == null){
            Result.fail("登陆失效");
        }
        Result result = ssoService.sendVerifyCode(login.getPhone(), VerifyCodeTypeEnum.VERIFY_OLD_MOBILE.getValue());
        return result;
    }

    @LoginRequired
    @ApiOperation(value ="修改密码",notes = "需要手机验证")
    @ApiImplicitParams({@ApiImplicitParam(name="verifyCode",value="verifyCode",required=true),
            @ApiImplicitParam(name="passwd",value="passwd",required=true)})
    @GetMapping("/api/portal/reset-pwd-by-verifycode")
    public Result resetPwdByVerifycode(String verifyCode,String passwd){
        UserDto login = UserThreadLocal.get();
        if(login == null){
            Result.fail("登陆失效");
        }
        Result result = ssoService.resetPwdByVerifycode(login.getPhone(),verifyCode,VerifyCodeTypeEnum.VERIFY_CODE_USE_UPDATE_PASSWORD.getValue(), passwd);
        return result;
    }

    @LoginRequired
    @ApiOperation(value = "修改/绑定手机号")
    @ApiImplicitParams({@ApiImplicitParam(name="mobile",value="需要绑定/修改的手机号",required=true),
            @ApiImplicitParam(name="verifyCode",value="验证码",required=true)})
    @GetMapping("/api/portal/bind-mobile")
    public Result bindMobile(String mobile ,String verifyCode){
        UserDto login = UserThreadLocal.get();
        if(login == null){
            Result.fail("登陆失效");
        }
        Result result = ssoService.bindMobile(mobile,verifyCode,VerifyCodeTypeEnum.VERIFY_CODE_USE_BIND_MOBILE.getValue(), login.getId());
        return result;
    }

    @LoginRequired
    @ApiOperation(value = "验证旧手机号-需要手机验证")
    @ApiImplicitParams({@ApiImplicitParam(name="verifyCode",value="verifyCode",required=true)})
    @GetMapping("/api/portal/verify-old-mobile")
    public Result verifyOldMobile(String verifyCode){
        UserDto login = UserThreadLocal.get();
        if(login == null){
            Result.fail("登陆失效");
        }
        Result result = ssoService.verifyOldMobile(login.getPhone(),verifyCode,VerifyCodeTypeEnum.VERIFY_OLD_MOBILE.getValue());
        return result;
    }


    /**
     * 修改用户资料
     * @param updateUserDTO
     * @return
     */
    @LoginRequired
    @ApiOperation(value = "修改用户资料",notes = "修改用户资料")
    @PostMapping(value = "/api/portal/save-user")
    public Result saveUser(@RequestBody UpdateUserDTO updateUserDTO){
        try {
            UserDto ssoUser = UserThreadLocal.get();
            if(ssoUser==null ||ssoUser.getId()==null){
                return Result.fail("用户不存在");
            }
            UserDO userDO =new UserDO();
            userDO.setId(ssoUser.getId());
            userDO.setHeadImg(updateUserDTO.getHeadImg());
            userDO.setNickName(updateUserDTO.getNickName());
            userDO.setAge(updateUserDTO.getAge());
            userDO.setGender(updateUserDTO.getGender());
            Integer num = userService.updateUserInfo(userDO);
            return Result.ok(num);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail();
    }

    @LoginRequired
    @ApiOperation(value = "微信用户信息授权接口")
    @ApiImplicitParams({@ApiImplicitParam(name="code",value="code",required=true),
            @ApiImplicitParam(name="encryptedData",value="encryptedData",required=true),
            @ApiImplicitParam(name="iv",value="iv",required=true)})
    @GetMapping("/api/portal/authorize-userinfo")
    public Result userinfo(String code,String encryptedData,String iv){
        Result result = ssoService.updateUserInfo(code,encryptedData,iv);
        return result;
    }


    @LoginRequired
    @ApiOperation(value = "获取小程序二维码",notes = "H5用户端生成的小程序二维码， scene 必须包含 aci=1001 （aci 值取H5登录用户中的 mechanismId）")
    @ApiImplicitParams({@ApiImplicitParam(name="scene",value="scene"),
            @ApiImplicitParam(name="page",value="page")})
    @RequestMapping(value = "/api/wx/get-unlimited",method = RequestMethod.GET,produces = "image/png")
    public void getUnlimited(String scene, String page, HttpServletResponse response){
        InputStream is =null;
        InputStream dis =null;
        try {
            String key = RedisKeyGenerator.getKey(LoginController.class, GET_UNLIMITED,scene);
            String qrUrl = (String) redisUtils.get(key);
            if(StringUtils.isBlank(qrUrl)){
                is = AppletsUtilEnum.INSTANCE.getInstance().getUnlimited(scene,page);
                SnowflakeIdWorker idWorker = new SnowflakeIdWorker(0, 0);
                qrUrl = AttachmentTypeEnum.GENERAL_ATTACHMENT.getPathPerfix()+DELIMITER_ONE_SLASH  +"qrcode"+DELIMITER_ONE_SLASH+
                        idWorker.nextId() + DELIMITER_ONE_PERIOD + "jpg";
                AliOSS.putObject(qrUrl,is);
                redisUtils.set(key,qrUrl, TimeUnit.DAYS.toSeconds(365));
                log.info("图书馆二维码-oss scene:{},qrcode:{}",scene,qrUrl);
            }
            String https =  "https://"+ AccessKeyIdSecretEnum.ALI_OSS.getBucketName()+DELIMITER_ONE_PERIOD+AccessKeyIdSecretEnum.ALI_OSS.getEp();
            dis = HttpUtil.getStreamByUrl(https+DELIMITER_ONE_SLASH +qrUrl);
            //获取响应输出流对象。
            OutputStream outputStream = response.getOutputStream();
            IOUtils.copy(dis,outputStream );
            outputStream.flush();
            outputStream.close();
            log.info("图书馆二维码-下载 scene:{},qrcode:{}",scene,qrUrl);
        } catch (IOException e) {
            log.error("{}", ExceptionUtils.getStackTrace(e));
        }finally {
            if(is != null){
                try {
                    is.close();
                } catch (IOException e) {
                    log.error("{}",e);
                }
                ;
            }
            if(dis != null){
                try {
                    dis.close();
                } catch (IOException e) {
                    log.error("{}",e);
                }
                ;
            }
        }
    }

    /**
     * @param request
     * @param response
     * @throws Exception
     * @throws
     * @title: logout
     * @description:用户退出
     */
    @Logout
    @ApiOperation(value = "用户退出接口")
    @GetMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        UserDto userDto = UserThreadLocal.get();
        ssoService.logout(userDto.getSid());
        ResponseUtil.printWriterAjax(request, response, Result.ok("用户已退出"));
    }
}

package com.example.webapp.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Api(tags = {"1000-登陆"})
@RefreshScope
@RestController
@Component
@Slf4j
@RequestMapping("/")
public class BmsLoginController {

    @Autowired
    private SsoService ssoService;

    /**
     * @param request
     * @throws
     * @title: login
     * @description: 登陆
     */
    @Login(platform = PlatformMarkEnum.ENTERPRISE)
    @ApiOperation(value = "运营端登录接口")
    @ApiImplicitParams({@ApiImplicitParam(name="username",value="用户名",required=true,paramType="query",example = "admin"),
            @ApiImplicitParam(name="password",value="密码",required=true,paramType="query",example = "LFhe8j1UI2yCQ0rn"),
            @ApiImplicitParam(name="verifyCode",value="验证码",required=true)})
    @GetMapping("/loginb")
    public Result login(HttpServletRequest request) throws Exception {
        String userName = StringUtils.trim(request.getParameter(ParamKeys.PARAM_USER_NAME));
        String password = StringUtils.trim(request.getParameter(ParamKeys.PARAM_PASSWORD));
        String verifyCode = StringUtils.trim(request.getParameter(ParamKeys.VERIFY_CODE));
        String sessionCode = (String) request.getSession().getAttribute(ParamKeys.VERIFY_CODE);
        if(StringUtils.isBlank(sessionCode) || !sessionCode.equals(verifyCode)){
            return Result.fail("验证码错误");
        }
        request.getSession().removeAttribute(ParamKeys.VERIFY_CODE);
        return ssoService.login(userName,password, PlatformMarkEnum.ENTERPRISE.getMark());
    }

    /**
     * @param request
     * @param response
     * @throws Exception
     * @throws
     * @title: logout
     * @description:用户退出
     */
    @Logout(platform = PlatformMarkEnum.ENTERPRISE)
    @ApiOperation(value = "退出接口")
    @GetMapping("/logoutb")
    public void logout(HttpServletRequest request, HttpServletResponse response) throws Exception {
        UserDto userDto = UserThreadLocal.get();
        ssoService.logout(userDto.getSid());
        ResponseUtil.printWriterAjax(request, response, Result.ok("用户已退出"));
    }

    /**
     * 依赖redis-session
     * @param request
     * @param response
     * @throws IOException
     */
    @ApiOperation(value = "生成验证码图片")
    @GetMapping("/captcha")
    public void Verify(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //定义图形验证码的长、宽、验证码字符数、干扰线宽度
        ShearCaptcha captcha = CaptchaUtil.createShearCaptcha(150, 40, 5, 4);
        //图形验证码写出，可以写出到文件，也可以写出到流
        captcha.write(response.getOutputStream());
        //获取验证码中的文字内容
        String verifyCode = captcha.getCode();
        request.getSession().setAttribute(ParamKeys.VERIFY_CODE,verifyCode);
    }
}

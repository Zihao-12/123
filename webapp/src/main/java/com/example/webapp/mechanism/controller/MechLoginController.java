package com.example.webapp.mechanism.controller;

import com.example.webapp.DTO.UserDto;
import com.example.webapp.Service.sso.SsoService;
import com.example.webapp.annotation.Login;
import com.example.webapp.annotation.Logout;
import com.example.webapp.enums.PlatformMarkEnum;
import com.example.webapp.result.Result;
import com.example.webapp.utils.ParamKeys;
import com.example.webapp.utils.ResponseUtil;
import com.example.webapp.utils.UserThreadLocal;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = {"1000-登陆"})
@RefreshScope
@RestController
@Component
@Slf4j
@RequestMapping("/")
public class MechLoginController {

    @Autowired
    private SsoService ssoService;

    /**
     * @param request
     * @throws
     * @title: login
     * @description: 登陆
     */
    @Login(platform = PlatformMarkEnum.MECHANISM)
    @ApiOperation(value = "机构端登录接口")
    @ApiImplicitParams({@ApiImplicitParam(name="username",value="用户名",required=true),
            @ApiImplicitParam(name="password",value="密码",required=true),
            @ApiImplicitParam(name="verifyCode",value="验证码",required=true)})
    @GetMapping("/loginm")
    public Result login(HttpServletRequest request) throws Exception {
        String userName = StringUtils.trim(request.getParameter(ParamKeys.PARAM_USER_NAME));
        String password = StringUtils.trim(request.getParameter(ParamKeys.PARAM_PASSWORD));
        String verifyCode = StringUtils.trim(request.getParameter(ParamKeys.VERIFY_CODE));
        String sessionCode = (String) request.getSession().getAttribute(ParamKeys.VERIFY_CODE);
        if(StringUtils.isBlank(sessionCode) || !sessionCode.equals(verifyCode)){
            return Result.fail("验证码错误");
        }
        request.getSession().removeAttribute(ParamKeys.VERIFY_CODE);
        return ssoService.login(userName,password, PlatformMarkEnum.MECHANISM.getMark());
    }

    /**
     * @param request
     * @param response
     * @throws Exception
     * @throws
     * @title: logout
     * @description:用户退出
     */
    @Logout(platform = PlatformMarkEnum.MECHANISM)
    @ApiOperation(value = "退出接口")
    @GetMapping("/logoutm")
    public void logout(HttpServletRequest request, HttpServletResponse response) throws Exception {
        UserDto userDto = UserThreadLocal.get();
        ssoService.logout(userDto.getSid());
        ResponseUtil.printWriterAjax(request, response, Result.ok("用户已退出"));
    }

}
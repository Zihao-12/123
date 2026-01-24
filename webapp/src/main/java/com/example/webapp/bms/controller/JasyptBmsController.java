package com.example.webapp.bms.controller;

import com.example.webapp.DTO.UserDto;
import com.example.webapp.annotation.LoginRequired;
import com.example.webapp.enums.LoginTokenSourceEnum;
import com.example.webapp.enums.PlatformMarkEnum;
import com.example.webapp.result.Result;
import com.example.webapp.utils.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = {"1009-配置加密管理"})
@RefreshScope
@RestController
@Component
@Slf4j
@LoginRequired(platform= PlatformMarkEnum.ENTERPRISE)
@RequestMapping("/api/bms/jasypt")
public class JasyptBmsController {
    @Autowired
    StringEncryptor stringEncryptor;

    @ApiOperation(value = "加密")
    @ApiImplicitParams({@ApiImplicitParam(name = "value", value = "原文", required = true)})
    @GetMapping(value = "/encrypt")
    public Result encrypt(String  value){
        //获取 vm 中的密钥值
        log.info("密钥:{}",System.getProperty("jasypt.encryptor.password"));
        String encStr = stringEncryptor.encrypt(value);
        return Result.ok("密文:"+encStr);
    }

    @ApiOperation(value = "解密")
    @ApiImplicitParams({@ApiImplicitParam(name = "value", value = "密文", required = true)})
    @GetMapping(value = "/decrypt")
    public Result decrypt(String  value){
        String decStr = stringEncryptor.decrypt(value);
        return Result.ok("原文:"+decStr);
    }

    @ApiOperation(value = "token")
    @ApiImplicitParams({@ApiImplicitParam(name = "token", value = "密文", required = true)})
    @GetMapping(value = "/token")
    public Result token(String  token){
        UserDto loginUser =new UserDto();
        loginUser.setExpMinutes(1);
        loginUser.setNickName("测试过期");
        log.info("有效期：{}分钟,token: {}",loginUser.getExpMinutes(),JwtUtil.createToken(loginUser));
        return Result.ok(JwtUtil.getLoginUser(token, LoginTokenSourceEnum.LoginAdvice.getSource()));
    }

}


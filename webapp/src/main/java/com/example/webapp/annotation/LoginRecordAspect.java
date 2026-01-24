package com.example.webapp.annotation;

import com.example.webapp.DO.UserLoginRecordDO;
import com.example.webapp.DTO.UserDto;
import com.example.webapp.Service.statistics.StatisticsService;
import com.example.webapp.enums.LoginTokenSourceEnum;
import com.example.webapp.result.CodeEnum;
import com.example.webapp.result.Result;
import com.example.webapp.utils.DateTimeUtil;
import com.example.webapp.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;

@Slf4j
@Aspect
@Component
public class LoginRecordAspect {
    @Autowired
    @Lazy
    private StatisticsService statisticsService;

    /**
     * @param point
     */
    @AfterReturning(returning="result", pointcut="@annotation(com.example.webapp.annotation.LoginRecord)")
    public Result afterReturning(JoinPoint point, Result result) throws Throwable {
        //登录成功写累计登录信息
        if(CodeEnum.SUCCESS.getValue().equals(result.getCode())){
            HashMap<String,String> tokenMap = (HashMap)result.getData();
            UserDto user = JwtUtil.getLoginUser(tokenMap.get("token"), LoginTokenSourceEnum.LoginRecordAspect.getSource());
            UserLoginRecordDO userLogin = new UserLoginRecordDO();
            userLogin.setDay(DateTimeUtil.dateInt(new Date()));
            userLogin.setMechanismId(user.getMechanismId());
            userLogin.setUserId(user.getId());
            statisticsService.loginRecord(userLogin);
        }
        return Result.fail("");
    }

}

package com.example.webapp.annotation;

import com.example.webapp.DO.StUserLogRecordDO;
import com.example.webapp.DTO.UserDto;
import com.example.webapp.Service.statistics.StatisticsService;
import com.example.webapp.enums.LogRecordEnum;
import com.example.webapp.utils.DateTimeUtil;
import com.example.webapp.utils.UserThreadLocal;
import com.example.webapp.utils.http.IpUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Date;

/**
 * @description
 * @date 2020/10/25
 *          方法返回值 必须要用 Result 包装
 */
@Slf4j
@Aspect
@Component
public class LogRecordAspect {

    @Autowired
    StatisticsService statisticsService;

    /**
     * @param point
     */
    @Around("@annotation(com.example.webapp.annotation.LogRecord)")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        //获取方法签名
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        LogRecord logRecord =  method.getAnnotation(LogRecord.class);
        try {
            Integer courseId =0;
            UserDto userDto = UserThreadLocal.get();
            String ip = IpUtil.getIpAddr();
            Object[] args = point.getArgs();
            if(LogRecordEnum.COURSE_XQ.equals(logRecord.LOG_RECORD_ENUM())){
                courseId = (Integer) args[0];
            }
            StUserLogRecordDO lr = new StUserLogRecordDO();
            lr.setIp(ip);
            lr.setMechanismId(userDto.getMechanismId());
            lr.setUserId(userDto.getId());
            lr.setCourseId(courseId);
            lr.setType(logRecord.LOG_RECORD_ENUM().getType());
            Date date = DateTimeUtil.parse(DateTimeUtil.format(new Date(),DateTimeUtil.DATE_FORMAT_YYYYMMDD));
            lr.setDate(date);
            statisticsService.saveLogRecordDo(lr);
        }catch (Exception e){
            log.info("日志记录失败,日志类型：{},{}",logRecord.LOG_RECORD_ENUM(), ExceptionUtils.getStackTrace(e));
        }
        Object object = point.proceed();
        return object;
    }

}

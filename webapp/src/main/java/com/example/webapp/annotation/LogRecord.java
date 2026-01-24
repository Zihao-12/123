package com.example.webapp.annotation;

import com.example.webapp.enums.LogRecordEnum;

import java.lang.annotation.*;

/**
 * 登录接口处理
 * @author gehaisong
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface LogRecord {
    LogRecordEnum LOG_RECORD_ENUM() default LogRecordEnum.SHU_FANG;
}

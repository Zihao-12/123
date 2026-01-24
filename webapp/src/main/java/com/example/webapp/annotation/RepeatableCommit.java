package com.example.webapp.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Component
@Documented
@Inherited
public @interface RepeatableCommit {
    /**
     * 时间内不可重复提交,单位秒
     * @return
     */
    long timeout()  default 1 ;
}

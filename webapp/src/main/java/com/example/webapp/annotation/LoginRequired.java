package com.example.webapp.annotation;

import com.example.webapp.enums.PlatformMarkEnum;

import java.lang.annotation.*;

@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface LoginRequired {
    public enum Required {
        TRUE,
        FALSE
    }
    Required required() default Required.TRUE;
    PlatformMarkEnum platform() default PlatformMarkEnum.CLIENT;
}

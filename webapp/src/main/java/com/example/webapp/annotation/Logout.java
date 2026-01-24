package com.example.webapp.annotation;

import com.example.webapp.enums.PlatformMarkEnum;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Logout {
    PlatformMarkEnum platform() default PlatformMarkEnum.CLIENT;
}

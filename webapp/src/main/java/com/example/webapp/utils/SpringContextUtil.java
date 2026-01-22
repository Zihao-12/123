package com.example.webapp.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * 获取上下文工具类
 */
@Component
public class SpringContextUtil implements ApplicationContextAware {

    protected static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        setFactory(applicationContext);
    }

    private static void setFactory(ApplicationContext applicationContext) {
        context = applicationContext;
    }

    /**
     * 获取上下文
     * @return
     */
    public static ApplicationContext getContext() {
        return context;
    }
}
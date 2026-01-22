package com.example.webapp.utils;


import com.zhihuiedu.framework.sso.UserDto;

public class UserThreadLocal {
    /**
     * 关于参数的说明
     * 	如果需要传递多值则使用Map集合封装
     */
    private static ThreadLocal<UserDto> threadLocal = new ThreadLocal<UserDto>();

    public static void set(UserDto user){
        threadLocal.set(user);
    }

    public static UserDto get(){
        return threadLocal.get();
    }

    //防止内存泄漏
    public static void remove(){
        threadLocal.remove();
    }
}

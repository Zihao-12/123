package com.example.webapp.advice;

import com.example.webapp.DTO.UserDto;
import com.example.webapp.annotation.Login;
import com.example.webapp.enums.LoginTokenSourceEnum;
import com.example.webapp.result.CodeEnum;
import com.example.webapp.result.Result;
import com.example.webapp.utils.CookieUtil;
import com.example.webapp.utils.JwtUtil;
import com.example.webapp.utils.ParamKeys;
import com.google.common.collect.Maps;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.Map;

@ControllerAdvice
public class LoginAdvice implements ResponseBodyAdvice<Object> {

    public static final String LOGIN = "login";
    public static final String USER = "user";

    @Override
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> aClass) {
        //指定拦截条件
        return true;
    }

    /**
     * 统一处理返回结果，写cookie
     * @param returnValue
     * @param methodParameter
     * @param mediaType
     * @param aClass
     * @param serverHttpRequest
     * @param serverHttpResponse
     * @return
     */
    @Override
    public Object beforeBodyWrite(Object returnValue, MethodParameter methodParameter, MediaType mediaType, Class<? extends HttpMessageConverter<?>> aClass,
                                  ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        ServletServerHttpResponse response = (ServletServerHttpResponse)serverHttpResponse;
        ServletServerHttpRequest request = (ServletServerHttpRequest) serverHttpRequest;
        String methodName = methodParameter.getMethod().getName();
        if (methodName.startsWith(LOGIN)){
            try {
                Login login = methodParameter.getAnnotatedElement().getDeclaredAnnotation(Login.class);
                if(login == null){
                    return Result.fail("登录失败,方法缺少@Login注解");
                }
                Result result = (Result) returnValue;
                if(CodeEnum.SUCCESS.getValue().equals(result.getCode())){
                    Map<String,String> tokenMap = (Map<String, String>) result.getData();
                    String token =tokenMap.get(ParamKeys.JWT_TOKEN);
                    UserDto loginUser = JwtUtil.getLoginUser(token, LoginTokenSourceEnum.LoginAdvice.getSource());
                    CookieUtil.writeCookie( response.getServletResponse(), JwtUtil.getCookieTokenKey(login.platform()),token);
                    Map<String,Object> map= Maps.newConcurrentMap();
                    map.put(ParamKeys.JWT_TOKEN,token);
                    map.put(USER,loginUser);
                    return Result.ok(map);
                }else {
                    CookieUtil.deleteCookie(request.getServletRequest(), response.getServletResponse(), JwtUtil.getCookieTokenKey(login.platform()));
                }
            }catch (Exception e){
                return Result.fail("LoginAdvice--登录失败");
            }

        }
        return returnValue;
    }
}

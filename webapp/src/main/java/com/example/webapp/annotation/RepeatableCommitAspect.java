package com.example.webapp.annotation;

import com.example.webapp.common.redis.RedisLock;
import com.example.webapp.result.Result;
import com.example.webapp.utils.Md5Util;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.ibatis.binding.MapperMethod;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Method;
import java.math.BigInteger;
import java.security.MessageDigest;

@Slf4j
@Aspect
@Component
public class RepeatableCommitAspect {
    @Autowired
    private RedisLock redisLock;

    /**
     * @param point
     */
    @Around("@annotation(com.example.webapp.annotation.RepeatableCommit)")
    public Result around(ProceedingJoinPoint point) throws Throwable {
        //获取方法签名
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        String className = method.getDeclaringClass().getName();
        String name = method.getName();
        //参数
        Object[] args = point.getArgs();
        StringBuilder sb = new StringBuilder();
        for (Object object : args) {
            if(object == null){
                continue;
            }
            if(object instanceof MultipartFile){
                String fileMd5 = getMd5((MultipartFile) object);
                sb.append(fileMd5);
            }else {
                sb.append(object.toString());
            }
        }
        String key = className + name + Md5Util.MD5(sb.toString());
        RepeatableCommit repeatableCommit =  method.getAnnotation(RepeatableCommit.class);
        long timeout = repeatableCommit.timeout();
        //获取缓存值,获取到则重复提交
        String token = redisLock.tryLock(key,timeout*1000);
        try {
            if(StringUtils.isNotBlank(token)){
                //执行方法
                Object object = point.proceed();
                return (Result) object;
            }
        }catch (Exception e){
            log.error("repeated >>>>方法执行失败,class:{},method:{},args:{}",className,name,sb.toString());
            throw e;
        }
        return Result.fail("处理中...... 请"+timeout+"秒之后在操作！");
    }

    /**
     * 获取上传文件的md5
     * @param file
     * @return
     */
    public String getMd5(MultipartFile file) {

        try {
            byte[] uploadBytes = file.getBytes();
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] digest = md5.digest(uploadBytes);
            String hashString = new BigInteger(1, digest).toString(16);
            return hashString;
        } catch (Exception e) {
            log.error("{}", ExceptionUtils.getStackTrace(e));
        }
        return null;

    }
}

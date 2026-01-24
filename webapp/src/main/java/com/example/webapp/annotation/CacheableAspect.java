package com.example.webapp.annotation;

import com.example.webapp.common.redis.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.StandardReflectionParameterNameDiscoverer;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import java.lang.reflect.Method;

@Slf4j
@Aspect
@Component
public class CacheableAspect {

    @Value("${spring.application.name}")
    private String applicationName;

    @Autowired
    @Lazy
    private RedisUtils redisUtils;

    @Pointcut("@annotation(com.example.webapp.annotation.Cacheable)")
    public void dealCacheServiceCut() {}

    /**
     * 环绕通知(Advice)：
     *   环绕通知非常强大，可以决定目标方法是否执行，什么时候执行，执行时是否需要替换方法参数，执行完毕是否需要替换返回值。
     *   环绕通知第一个参数必须是org.aspectj.lang.ProceedingJoinPoint类型
     */
    @Around(value = "dealCacheServiceCut()")
    @SuppressWarnings("all")
    public Object dealCacheService(ProceedingJoinPoint point) throws Throwable {
        try {
            log.info("Cacheable.........");
            //获取方法签名
            MethodSignature signature = (MethodSignature) point.getSignature();
            Method method = signature.getMethod();
            String clazzName = signature.getDeclaringTypeName();
            // 获取注解对象
            Cacheable cacheableAnnotation = method.getAnnotation(Cacheable.class);
            //所有方法参数
            Object[] args = point.getArgs();
            String fieldKey = parseKey(cacheableAnnotation.fieldKey(), method, args);
            if (StringUtils.isEmpty(fieldKey)) {
                return point.proceed();
            }
            String prefix = StringUtils.isNotBlank(cacheableAnnotation.prefix())?cacheableAnnotation.prefix()+"-":"";
            String cacheKey = applicationName+":"+clazzName.substring(clazzName.lastIndexOf(".")+1)+":"+ prefix + fieldKey;
            Cacheable.CacheOperation cacheOperation = cacheableAnnotation.cacheOperation();
            if (cacheOperation == Cacheable.CacheOperation.QUERY) {
                return processQuery(point, cacheableAnnotation, cacheKey);
            }
            if (cacheOperation == Cacheable.CacheOperation.UPDATE || cacheOperation == Cacheable.CacheOperation.DELETE) {
                return processUpdateAndDelete(point, cacheKey);
            }
        } catch (Exception e) {
            log.error("dealCacheService error,JoinPoint:{}", point.getSignature(), e);
        }
        return point.proceed();
    }

    /**
     * 查询处理
     */
    private Object processQuery(ProceedingJoinPoint point, Cacheable cacheable, String cacheKey)
            throws Throwable {
        if (redisUtils.hasKey(cacheKey)) {
            log.info("cacheKey:{}", cacheKey);
            return redisUtils.get(cacheKey);
        } else {
            Object result = null;
            try {
                return result = point.proceed();
            } finally {
                redisUtils.set(cacheKey, result, cacheable.expireTime());
            }
        }
    }

    /**
     * 删除和修改处理
     *   数据库update操作后,只需删除掉原来在缓存中的数据,下次查询时就会刷新
     * @param point
     * @param cacheKey
     * @return
     * @throws Throwable
     */
    private Object processUpdateAndDelete(ProceedingJoinPoint point, String cacheKey) throws Throwable {
        try {
            return point.proceed();
        } finally {
            log.info("@Cacheable-删除缓存,key {}",cacheKey);
            redisUtils.del(cacheKey);
        }
    }

    /**
     * 获取redis的key
     * @param fieldKey
     * @param method
     * @param args
     * @return
     */
    private String parseKey(String fieldKey, Method method, Object[] args) {
        //获取被拦截方法参数名列表(使用Spring支持类库)

        // 旧写法 (已删除)
// ParameterNameDiscoverer pnd = new LocalVariableTableParameterNameDiscoverer();

// ✅ 新写法 (Spring 6.1+ 推荐)
        ParameterNameDiscoverer u = new StandardReflectionParameterNameDiscoverer();
        String[] paraNameArr = u.getParameterNames(method);
        //使用SPEL进行key的解析
        ExpressionParser parser = new SpelExpressionParser();
        //SPEL上下文
        StandardEvaluationContext context = new StandardEvaluationContext();
        //把方法参数放入SPEL上下文中
        for (int i = 0; i < paraNameArr.length; i++) {
            context.setVariable(paraNameArr[i], args[i]);
        }
        String key= parser.parseExpression(fieldKey).getValue(context, String.class);
        return  key;
    }
}

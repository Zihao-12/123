package com.example.webapp.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Cacheable {
    /**
     * key前缀
     */
    String prefix();

    /**
     * 后缀表达式，为空时候表示不需要后缀表达式，采用 SPEL
     * eg:
     *  1.不支持加固定后缀！！！ 目前 SPEL 数据源是来自方法入参，要是需要固定后缀，放置在前缀就好了;
     *  2.访问参数 比如 methodName(String userId, String pageId)  ->"#userId+'_'+#pageId";
     *  3.访问对象内属性 比如 methodName(UserBO userBO)  ->"#userBO.name";
     *  4.访问集合数据 比如 methodName(List<String> list) -> "#list.toString()";
     *
     * @return
     */
    String fieldKey();

    /**
     * 过期时间(秒)
     */
    int expireTime() default 300;

    CacheOperation cacheOperation() default CacheOperation.QUERY;


    enum CacheOperation {
        /**
         * 缓存操作类型
         */
        QUERY,
        UPDATE,
        DELETE;
    }
}

package com.example.webapp.common.redis;


import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 键生成器
 * @author gehaisong
 * Created by gehaisong on 2019/3/19.
 */
@Component
public class RedisKeyGenerator {

    /**应用名空 和注解不匹配
     * @param clazz  调用类
     * @param prefix   名字
     * @param params 参数
     * @return 获取键值
     */
    public static String getKey(Class<?> clazz, String prefix, Object... params) {
        return getKey("", clazz, prefix, params);
    }

    /** 注解 Cacheable
     * @param applicationName 应用名
     * @param clazz           调用类 对应注解所在类
     * @param prefix          前缀  对应注解  prefix = "join"
     * @param params          参数  生成字符串后   对应注解中 fieldKey = "#activityId+'_'+#userId"
     * @return 获取键值
     */
    public static String getKey(String applicationName, Class<?> clazz, String prefix, Object... params) {
        StringBuilder result = new StringBuilder();
        if (StringUtils.isNotBlank(applicationName)) {
            result.append(applicationName).append(":");
        }
        if (clazz != null) {
            result.append(clazz.getSimpleName()).append(":");
        }
        if (StringUtils.isNotBlank(prefix)) {
            result.append(prefix).append(":");
        }
        if (result.toString().length() > 0) {
            deleteLastChar(result);
        }
        if (ArrayUtils.isNotEmpty(params)) {
            result.append("-");
            for (Object each : params) {
                if(each == null){
                    continue;
                }
                result.append(each.toString()).append("_");
            }
            deleteLastChar(result);
        }
        if (result.length() == 0) {
            return null;
        }
        return result.toString();
    }

    public static void deleteLastChar(StringBuilder result) {
        if(result!=null && result.toString().length()>0){
            result.deleteCharAt(result.length()-1);
        }
    }
}
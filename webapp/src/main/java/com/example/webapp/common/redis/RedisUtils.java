package com.example.webapp.common.redis;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundZSetOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * redisTemplate封装
 *
 */
@Slf4j
@Component
public class RedisUtils {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    public static final long TIME_SECOND_10 = 10;
    public static final long TIME_SECOND_30 = 30;
    public static final long TIME_SECOND_60 = 60;
    public static final long TIME_MINUTE_1 = 1 * 60;
    public static final long TIME_MINUTE_5 = 5 * 60;
    public static final long TIME_MINUTE_10 = 10 * 60;
    public static final long TIME_MINUTE_30 = 30 * 60;
    public static final long TIME_HOUR_1 = 1 * 60 * 60;
    public static final long TIME_DAY_1 = 60 * 60 * 24 ;
    public static final long TIME_HOUR_6 = 6 * 60 * 60;
    public static final String SPLIT = "_";

    public RedisUtils(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 指定缓存失效时间
     * @param key 键
     * @param time 时间(秒)
     * @return
     */
    public boolean expire(String key,long time){
        try {
            if(time>0){
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            log.error("{}", ExceptionUtils.getStackTrace(e));
            return false;
        }
    }

    /**
     * 根据key 获取过期时间
     * @param key 键 不能为null
     * @return 时间(秒) 返回0代表为永久有效
     */
    public long getExpire(String key){
        return redisTemplate.getExpire(key,TimeUnit.SECONDS);
    }

    /**
     * 判断key是否存在
     * @param key 键
     * @return true 存在 false不存在
     */
    public boolean hasKey(String key){
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            log.error("{}", ExceptionUtils.getStackTrace(e));
            return false;
        }
    }

    /**
     * 删除缓存
     * @param key 可以传一个值 或多个
     */
    @SuppressWarnings("unchecked")
    public void del(String ... key){
        if(key!=null&&key.length>0){
            if(key.length==1){
                redisTemplate.delete(key[0]);
            }else{
                redisTemplate.delete(CollectionUtils.arrayToList(key));
            }
        }
    }

    //============================String=============================
    /**
     * 普通缓存获取
     * @param key 键
     * @return 值
     */
    public Object get(String key){
        return key==null?null:redisTemplate.opsForValue().get(key);
    }

    /**
     * 普通缓存放入
     * @param key 键
     * @param value 值
     * @return true成功 false失败
     */
    public boolean set(String key,Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            log.error("{}", ExceptionUtils.getStackTrace(e));
            return false;
        }
    }

    /**
     * 普通缓存放入并设置时间
     * @param key 键
     * @param value 值
     * @param time 时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */
    public boolean set(String key,Object value,long time){
        try {
            if(time>0){
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            }else{
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            log.error("{}", ExceptionUtils.getStackTrace(e));
            return false;
        }
    }

    /**
     * 递增
     * @param key 键
     * @param delta 要增加几(大于0)
     * @return
     */
    public long incr(String key, long delta){
        if(delta<0){
            throw new RuntimeException("递增因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 递增
     * @param key 键
     * @return
     */
    public long incr(String key){
        return redisTemplate.opsForValue().increment(key);
    }

    /**
     * 递减
     * @param key 键
     * @param delta 要减少几(小于0)
     * @return
     */
    public long decr(String key, long delta){
        if(delta<0){
            throw new RuntimeException("递减因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, -delta);
    }

    /**
     * 递减1
     * @param key 键
     * @return
     */
    public long decr(String key){
        return redisTemplate.opsForValue().decrement(key);
    }

    //================================Map=================================
    /**
     * HashGet
     * @param key 键 不能为null
     * @param item 项 不能为null
     * @return 值
     */
    public Object hget(String key,String item){
        return redisTemplate.opsForHash().get(key, item);
    }

    /**
     * 获取hashKey对应的所有键值
     * @param key 键
     * @return 对应的多个键值
     */
    public Map<Object,Object> hmget(String key){
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * HashSet
     * @param key 键
     * @param map 对应多个键值
     * @return true 成功 false 失败
     */
    public boolean hmset(String key, Map<String,Object> map){
        try {
            redisTemplate.opsForHash().putAll(key, map);
            return true;
        } catch (Exception e) {
            log.error("{}", ExceptionUtils.getStackTrace(e));
            return false;
        }
    }

    /**
     * HashSet 并设置时间
     * @param key 键
     * @param map 对应多个键值
     * @param time 时间(秒)
     * @return true成功 false失败
     */
    public boolean hmset(String key, Map<String,Object> map, long time){
        try {
            redisTemplate.opsForHash().putAll(key, map);
            if(time>0){
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            log.error("{}", ExceptionUtils.getStackTrace(e));
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     * @param key 键
     * @param item 项
     * @param value 值
     * @return true 成功 false失败
     */
    public boolean hset(String key,String item,Object value) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            return true;
        } catch (Exception e) {
            log.error("{}", ExceptionUtils.getStackTrace(e));
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     * @param key 键
     * @param item 项
     * @param value 值
     * @param time 时间(秒)  注意:如果已存在的hash表有时间,这里将会替换原有的时间
     * @return true 成功 false失败
     */
    public boolean hset(String key,String item,Object value,long time) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            if(time>0){
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            log.error("{}", ExceptionUtils.getStackTrace(e));
            return false;
        }
    }

    /**
     * 删除hash表中的值
     * @param key 键 不能为null
     * @param item 项 可以使多个 不能为null
     */
    public void hdel(String key, Object... item){
        redisTemplate.opsForHash().delete(key,item);
    }

    /**
     * 判断hash表中是否有该项的值
     * @param key 键 不能为null
     * @param item 项 不能为null
     * @return true 存在 false不存在
     */
    public boolean hHasKey(String key, String item){
        return redisTemplate.opsForHash().hasKey(key, item);
    }

    /**
     * hash递增 如果不存在,就会创建一个 并把新增后的值返回
     * @param key 键
     * @param item 项
     * @param by 要增加几(大于0)
     * @return
     */
    public double hincr(String key, String item,double by){
        return redisTemplate.opsForHash().increment(key, item, by);
    }

    /**
     * hash递减
     * @param key 键
     * @param item 项
     * @param by 要减少记(小于0)
     * @return
     */
    public double hdecr(String key, String item,double by){
        return redisTemplate.opsForHash().increment(key, item,-by);
    }

    //============================set=============================
    /**
     * 根据key获取Set中的所有值
     * @param key 键
     * @return
     */
    public Set<Object> sGet(String key){
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            log.error("{}", ExceptionUtils.getStackTrace(e));
            return null;
        }
    }

    /**
     * 根据value从一个set中查询,是否存在
     * @param key 键
     * @param value 值
     * @return true 存在 false不存在
     */
    public boolean sHasKey(String key,Object value){
        try {
            return redisTemplate.opsForSet().isMember(key, value);
        } catch (Exception e) {
            log.error("{}", ExceptionUtils.getStackTrace(e));
            return false;
        }
    }

    /**
     * 将数据放入set缓存
     * @param key 键
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public long sSet(String key, Object...values) {
        try {
            return redisTemplate.opsForSet().add(key, values);
        } catch (Exception e) {
            log.error("{}", ExceptionUtils.getStackTrace(e));
            return 0;
        }
    }

    /**
     * 将set数据放入缓存
     * @param key 键
     * @param time 时间(秒)
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public long sSetAndTime(String key,long time,Object...values) {
        try {
            Long count = redisTemplate.opsForSet().add(key, values);
            if(time>0) {
                expire(key, time);
            }
            return count;
        } catch (Exception e) {
            log.error("{}", ExceptionUtils.getStackTrace(e));
            return 0;
        }
    }

    /**
     * 获取set缓存的长度
     * @param key 键
     * @return
     */
    public long sGetSetSize(String key){
        try {
            return redisTemplate.opsForSet().size(key);
        } catch (Exception e) {
            log.error("{}", ExceptionUtils.getStackTrace(e));
            return 0;
        }
    }

    /**
     * 移除值为value的
     * @param key 键
     * @param values 值 可以是多个
     * @return 移除的个数
     */
    public long setRemove(String key, Object ...values) {
        try {
            Long count = redisTemplate.opsForSet().remove(key, values);
            return count;
        } catch (Exception e) {
            log.error("{}", ExceptionUtils.getStackTrace(e));
            return 0;
        }
    }
    //===============================list=================================

    /**
     * 获取list缓存的内容
     * @param key 键
     * @param start 开始
     * @param end 结束  0 到 -1代表所有值
     * @return
     */
    public List<Object> lGet(String key, long start, long end){
        try {
            return redisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            log.error("{}", ExceptionUtils.getStackTrace(e));
            return null;
        }
    }

    /**
     * 获取list缓存的长度
     * @param key 键
     * @return
     */
    public long lGetListSize(String key){
        try {
            return redisTemplate.opsForList().size(key);
        } catch (Exception e) {
            log.error("{}", ExceptionUtils.getStackTrace(e));
            return 0;
        }
    }

    /**
     * 通过索引 获取list中的值
     * @param key 键
     * @param index 索引  index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
     * @return
     */
    public Object lGetIndex(String key,long index){
        try {
            return redisTemplate.opsForList().index(key, index);
        } catch (Exception e) {
            log.error("{}", ExceptionUtils.getStackTrace(e));
            return null;
        }
    }

    /**
     * 将 value 放入 redis 的 List中  (value 若是List，则作为 redis List的一个元素)
     * @param key 键
     * @param value 值
     * @return
     */
    public boolean lSetValue(String key, Object value) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            return true;
        } catch (Exception e) {
            log.error("{}", ExceptionUtils.getStackTrace(e));
            return false;
        }
    }

    /**
     * 将 value 放入 redis 的 List中  (value 若是List，则作为 redis List的一个元素)
     * @param key 键
     * @param value 值
     * @param time 时间(秒)
     * @return
     */
    public boolean lSetValue(String key, Object value, long time) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            log.error("{}", ExceptionUtils.getStackTrace(e));
            return false;
        }
    }

    /**
     * 将list 放入 redis 的 List中  (平行转化)
     * @param key 键
     * @param value 值
     * @return
     */
    public boolean lSetList(String key, List<Object> value) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            return true;
        } catch (Exception e) {
            log.error("{}", ExceptionUtils.getStackTrace(e));
            return false;
        }
    }

    /**
     *  将list 放入 redis 的 List中  (平行转化)
     * @param key 键
     * @param value 值
     * @param time 时间(秒)
     * @return
     */
    public boolean lSetList(String key, List<Object> value, long time) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            log.error("{}", ExceptionUtils.getStackTrace(e));
            return false;
        }
    }

    /**
     * 根据索引修改list中的某条数据
     * @param key 键
     * @param index 索引
     * @param value 值
     * @return
     */
    public boolean lUpdateIndex(String key, long index,Object value) {
        try {
            redisTemplate.opsForList().set(key, index, value);
            return true;
        } catch (Exception e) {
            log.error("{}", ExceptionUtils.getStackTrace(e));
            return false;
        }
    }

    /**
     * 移除N个值为value
     * @param key 键
     * @param count 移除多少个
     * @param value 值
     * @return 移除的个数
     */
    public long lRemove(String key,long count,Object value) {
        try {
            Long remove = redisTemplate.opsForList().remove(key, count, value);
            return remove;
        } catch (Exception e) {
            log.error("{}", ExceptionUtils.getStackTrace(e));
            return 0;
        }
    }
    /**
     * 根据value从一个set中查询,是否存在
     * @param key 键
     * @param value 值
     * @return true 存在 false不存在
     */
    public boolean isMember(String key,Object value){
        try {
            return redisTemplate.opsForSet().isMember(key, value);
        } catch (Exception e) {
            log.error("{}", ExceptionUtils.getStackTrace(e));
            return false;
        }
    }

    /**
     * 计数
     * @param key
     * @param value
     */
    public Long hyperlologAdd(String key, Object... value) {
        return redisTemplate.opsForHyperLogLog().add(key,value);
    }
    /**
     * 获取总数
     * @param key
     */
    public Long hyperlologCount(String key) {
        return redisTemplate.opsForHyperLogLog().size(key);
    }

    /**
     * 1.绑定键 中 添加元素，同时指定元素的分数
     * @param rankKey  绑定键(缓存key)
     * @param value    元素   （userId）
     * @param score    元素值 （排序）
     * @return
     */
    public boolean zadd(String rankKey,Integer value,double score){
        // 当前分数, 分数.Integer.MAX_VALUE-数据库id 用于相同分数下排名;分数相同的用户，先获取到该分数的用户排名更高
       // BigDecimal currentScore = new BigDecimal(score+"."+(Integer.MAX_VALUE-i));
        BoundZSetOperations zSetOps = redisTemplate.boundZSetOps(rankKey);
        return zSetOps.add(value,score);
    }


    public boolean zadd(String rankKey,String name,double score){
        // 当前分数, 分数.Integer.MAX_VALUE-数据库id 用于相同分数下排名;分数相同的用户，先获取到该分数的用户排名更高
        // BigDecimal currentScore = new BigDecimal(score+"."+(Integer.MAX_VALUE-i));
        BoundZSetOperations zSetOps = redisTemplate.boundZSetOps(rankKey);
        return zSetOps.add(name,score);
    }

    /**
     * 1.绑定键 中 的元素 自增值
     * @param rankKey  绑定键(缓存key)
     * @param value    元素   （userId）
     * @param score    元素值 （排序）
     * @return
     */
    public Double incrementScore(String rankKey,Integer value,double score){
        BoundZSetOperations zSetOps = redisTemplate.boundZSetOps(rankKey);
        return zSetOps.incrementScore(value,score);
    }

    /**
     * 1.绑定键 中 的元素 自增值
     * @param rankKey  绑定键(缓存key)
     * @param name    元素
     * @param score    元素值 （排序）
     * @return
     */
    public Double incrementScore(String rankKey,String name,double score){
        BoundZSetOperations zSetOps = redisTemplate.boundZSetOps(rankKey);
        return zSetOps.incrementScore(name,score);
    }

    /**
     * 2.0 获取 绑定键 的 指定 下标区间 的值 (分值正叙)
     * @param rankKey 绑定键(缓存key)
     * @param start
     * @param end
     * @return 返回值（value元素集合）  [1001,1002,1003]
     */
    public Set zrange(String rankKey,long start, long end){
        BoundZSetOperations zSetOps = redisTemplate.boundZSetOps(rankKey);
        Set set = zSetOps.range(start,end);
        return set;
    }

    /**
     * 2.1 获取 绑定键 的 指定 下标区间 的值    (分值倒叙)  下标从0开始
     *     取全部：start =0 and end =-1
     * @param rankKey 绑定键(缓存key)
     * @param start
     *  @param end
     * @return  返回值 [{"score": 99,"value": 1009}]
     */
    public Set reverseRangeWithScores(String rankKey,long start, long end){
        BoundZSetOperations zSetOps = redisTemplate.boundZSetOps(rankKey);
        Set set = zSetOps.reverseRangeWithScores(start, end);
        return set;
    }

    /**
     * 2.1 清空集合
     *     取全部：start =0 and end =-1
     * @param rankKey 绑定键(缓存key)
     * @param start
     *  @param end
     */
    public Long removeRange(String rankKey,long start, long end){
        BoundZSetOperations zSetOps = redisTemplate.boundZSetOps(rankKey);
        return zSetOps.removeRange(start, end);
    }

    /**
     * 3.根据分数区间值排序取值
     * @param rankKey 绑定键(缓存key)
     * @param minScore
     * @param maxScore
     * @return 返回值（value元素集合）  [1001,1002,1003]
     */
    public Set zrangeByScore(String rankKey,double minScore, double maxScore){
        BoundZSetOperations zSetOps = redisTemplate.boundZSetOps(rankKey);
        Set set = zSetOps.rangeByScore(minScore, maxScore);
        return set;
    }

    /**
     * 4.统计分数在某个区间的个数  闭区间
     * @param rankKey 绑定键(缓存key)
     * @param minScore
     * @param maxScore
     * @return
     */
    public Long zcount(String rankKey,double minScore, double maxScore){
        BoundZSetOperations zSetOps = redisTemplate.boundZSetOps(rankKey);
        return zSetOps.count(minScore, maxScore);
    }

    /**
     * 5. 获取 绑定键 中 元素 的分数
     * @param rankKey 绑定键(缓存key)
     * @param value    元素   （userId）
     * @return
     */
    public Double zscore(String rankKey,Integer value){
        BoundZSetOperations zSetOps = redisTemplate.boundZSetOps(rankKey);
        return zSetOps.score(value);
    }

    /**
     * 6. 获取 绑定键 中 个数
     * @param rankKey 绑定键(缓存key)
     * @return
     */
    public Long zcard(String rankKey){
        BoundZSetOperations zSetOps = redisTemplate.boundZSetOps(rankKey);
        return zSetOps.zCard();
    }

    /**
     * 7. 获取绑定键中的元素的下标   下标从0开始
     * @param rankKey 绑定键(缓存key)
     * @param value    元素   （userId）
     * @return
     */
    public Long zrank(String rankKey,Integer value){
        BoundZSetOperations zSetOps = redisTemplate.boundZSetOps(rankKey);
        return zSetOps.rank(value);
    }

    /**
     * 8. 获取绑定键中的元素的下标  (榜单排名) 下标从0开始
     * @param rankKey 绑定键(缓存key)
     * @param value    元素   （userId）
     * @return
     */
    public Long zreverseRank(String rankKey,Integer value){
        BoundZSetOperations zSetOps = redisTemplate.boundZSetOps(rankKey);
        return zSetOps.reverseRank(value);
    }

    /**
     *  分数加上 用时 存储到 有序集合
     * @param score
     * @param times
     * @return
     */
    public  double getScoreTtimes(int score,int times) {
        BigDecimal sc = new BigDecimal(score);
        BigDecimal ts = new BigDecimal(intToLessOne(times));
        BigDecimal rs = sc.add(ts);
        return rs.doubleValue();
    }

    public  int getScore(double scoreTimes) {
        Double st = new Double(scoreTimes);
        return st.intValue();
    }

    /**
     * 把整数转成 小于1的数
     * @param times
     * @return
     */
    private  double intToLessOne(int times) {
        double mu = Double.valueOf(times);
        if(times<=1D){
            mu = 1.451D;
        }
        return 1/mu;
    }

    public long zSize(String setKey){
        BoundZSetOperations zSetOperation = redisTemplate.boundZSetOps(setKey);
        return zSetOperation.size();
    }


    public void test(String setKey,String key,long val){
        BoundZSetOperations<String, Object> zSetOperation = redisTemplate.boundZSetOps("activity_20");
        System.out.println(zSetOperation.size());
        zSetOperation.add("jiangping1",10);
        Double d1 = zSetOperation.incrementScore("jiangping1",-1);
        zSetOperation.add("jiangping2",20);
        Double d2 = zSetOperation.incrementScore("jiangping2",-1);
        zSetOperation.add("jiangping3",30);
        Double d3 = zSetOperation.incrementScore("jiangping3", -1);
        System.out.println(d1);
        System.out.println(d2);
        System.out.println(d3);
        System.out.println(zSetOperation.size());
        RedisOperations<String, Object> ss = zSetOperation.getOperations();
        redisTemplate.boundZSetOps(setKey).incrementScore(key,val);
    }
}
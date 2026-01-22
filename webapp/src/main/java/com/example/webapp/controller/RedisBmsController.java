package com.example.webapp.controller;

import com.zhihuiedu.business.service.activity.ActivityService;
import com.zhihuiedu.common.redis.RedisUtils;
import com.zhihuiedu.framework.annotation.LoginRequired;
import com.zhihuiedu.framework.enums.PlatformMarkEnum;
import com.zhihuiedu.framework.result.Result;
import com.zhihuiedu.framework.utils.http.HttpUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;
import java.util.Set;

/** 活动表表
 * @author ghs 
 */
@Api(tags = {"1009-Redis缓存管理"})
@RefreshScope
@RestController
@Component
@Slf4j
@LoginRequired(platform= PlatformMarkEnum.ENTERPRISE)
@RequestMapping("/api/bms/redis")
public class RedisBmsController {
    @Autowired
    RedisUtils redisUtils;
    @Autowired
    ActivityService activityService;

    @ApiOperation(value = "查看缓存", notes = "")
    @ApiImplicitParams({@ApiImplicitParam(name = "key", value = "key", required = true)})
    @GetMapping(value = "/find-cache")
    public Result findCache(String  key){
       return Result.ok(redisUtils.get(key));
    }


    @ApiOperation(value = "纯真IP", notes = "")
    @ApiImplicitParams({@ApiImplicitParam(name = "ip", value = "ip", required = true)})
    @GetMapping(value = "/czip")
    public Result czip(String ip){
        return Result.ok(HttpUtils.getIpFromCz(ip));
    }

    @ApiOperation(value = "初始化排行榜", notes = "")
    @ApiImplicitParams({@ApiImplicitParam(name = "mechanismId", value = "机构ID", required = true),
            @ApiImplicitParam(name = "activityId", value = "活动ID", required = true)})
    @GetMapping(value = "/initRanking")
    public void initRanking(Integer mechanismId,Integer activityId){
        activityService.initRanking(mechanismId,activityId,0);
    }

    @ApiOperation(value = "获取有序集合", notes = "")
    @ApiImplicitParams({@ApiImplicitParam(name = "key", value = "key", required = true)})
    @GetMapping(value = "/getzList")
    public Set getzList(String key){
        Set set = redisUtils.reverseRangeWithScores(key,0,-1);
        return set;
    }

    @ApiOperation(value = "获取排行榜", notes = "")
    @ApiImplicitParams({@ApiImplicitParam(name = "mechanismId", value = "机构ID", required = true),
            @ApiImplicitParam(name = "activityId", value = "活动ID", required = true),
            @ApiImplicitParam(name = "type", value = "0全国 1馆内排行", required = true)})
    @GetMapping(value = "/get-ranking-list")
    public Set getRankingList(Integer mechanismId,Integer activityId,Integer type){
        String key  = activityService.getHreoCacheKey(mechanismId,activityId,type);
        Set set = redisUtils.reverseRangeWithScores(key,0,-1);
        return set;
    }


    @ApiOperation(value = "绑定键 中 添加元素，同时指定元素的分数", notes = "绑定键 中 添加元素，同时指定元素的分数")
    @ApiImplicitParams({@ApiImplicitParam(name = "rankKey", value = "绑定键(缓存key)", required = true),
            @ApiImplicitParam(name = "value", value = "元素   （userId）", required = true),
            @ApiImplicitParam(name = "score", value = "元素值 （排序）", required = true),
            @ApiImplicitParam(name = "times", value = "用时", required = true)})
    @GetMapping(value = "/zadd")
    public boolean zadd(String rankKey,Integer value,Integer score,int times){
        Random random =new Random();
        double ts =redisUtils.getScoreTtimes(score,times);
        return redisUtils.zadd(rankKey,value,ts);
    }

    @ApiOperation(value = "1.绑定键 中 的元素 自增值", notes = "1.绑定键 中 的元素 自增值")
    @ApiImplicitParams({@ApiImplicitParam(name = "rankKey", value = "绑定键(缓存key)", required = true),
            @ApiImplicitParam(name = "value", value = "元素   （userId）", required = true),
            @ApiImplicitParam(name = "score", value = "元素值 （排序）", required = true)})
    @GetMapping(value = "/incrementScore")
    public Double incrementScore(String rankKey,Integer value,double score){
        return redisUtils.incrementScore(rankKey,value,score);
    }

    @ApiOperation(value = "2.0 获取 绑定键 的 指定 下标区间 的值 (分值正叙)", notes = "2.0 获取 绑定键 的 指定 下标区间 的值 (分值正叙)")
    @ApiImplicitParams({@ApiImplicitParam(name = "rankKey", value = "绑定键(缓存key)", required = true),
            @ApiImplicitParam(name = "start", value = "开始下标", required = true),
            @ApiImplicitParam(name = "end", value = "结束下标", required = true)})
    @GetMapping(value = "/zrange")
    public Set zrange(String rankKey, long start, long end){
        Set set = redisUtils.zrange(rankKey,start,end);
        return set;
    }

    @ApiOperation(value = "2.1 获取 绑定键 的 指定 下标区间 的值    (分值倒叙)", notes = "取全部：start =0 and end =-1")
    @ApiImplicitParams({@ApiImplicitParam(name = "rankKey", value = "绑定键(缓存key)", required = true),
            @ApiImplicitParam(name = "start", value = "开始下标", required = true),
            @ApiImplicitParam(name = "end", value = "结束下标", required = true)})
    @GetMapping(value = "/reverseRangeWithScores")
    public Set reverseRangeWithScores(String rankKey,long start, long end){
        start=0;
        end=-1;
        Set set = redisUtils.reverseRangeWithScores(rankKey,start,end);
        return set;
    }

    @ApiOperation(value = "2.1 清空集合", notes = "")
    @ApiImplicitParams({@ApiImplicitParam(name = "rankKey", value = "绑定键(缓存key)", required = true),
            @ApiImplicitParam(name = "start", value = "开始下标", required = true),
            @ApiImplicitParam(name = "end", value = "结束下标", required = true)})
    @GetMapping(value = "/removeRange")
    public Long removeRange(String rankKey,long start, long end){
        start=0;
        end=-1;
        Long set = redisUtils.removeRange(rankKey,start,end);
        return set;
    }

    @ApiOperation(value = "3.根据分数区间值排序取值", notes = "3.根据分数区间值排序取值")
    @ApiImplicitParams({@ApiImplicitParam(name = "rankKey", value = "绑定键(缓存key)", required = true),
            @ApiImplicitParam(name = "minScore", value = "最低分", required = true),
            @ApiImplicitParam(name = "maxScore", value = "最高分", required = true)})
    @GetMapping(value = "/zrangeByScore")
    public Set zrangeByScore(String rankKey,double minScore, double maxScore){
        Set set = redisUtils.zrangeByScore(rankKey,minScore, maxScore);
        return set;
    }

    @ApiOperation(value = "4.统计分数在某个区间的个数", notes = "4.统计分数在某个区间的个数")
    @ApiImplicitParams({@ApiImplicitParam(name = "rankKey", value = "绑定键(缓存key)", required = true),
            @ApiImplicitParam(name = "minScore", value = "最低分", required = true),
            @ApiImplicitParam(name = "maxScore", value = "最高分", required = true)})
    @GetMapping(value = "/zcount")
    public Long zcount(String rankKey,double minScore, double maxScore){
        return redisUtils.zcount(rankKey,minScore, maxScore);
    }

    @ApiOperation(value = "获取 绑定键 中 元素 的分数", notes = "获取 绑定键 中 元素 的分数")
    @ApiImplicitParams({@ApiImplicitParam(name = "rankKey", value = "绑定键(缓存key)", required = true),
            @ApiImplicitParam(name = "value", value = "元素   （userId）", required = true)})
    @GetMapping(value = "/zscore")
    public Double zscore(String rankKey, Integer value){
        return redisUtils.zscore(rankKey,value);
    }

    @ApiOperation(value = "6. 获取 绑定键 中 个数", notes = "6. 获取 绑定键 中 个数")
    @ApiImplicitParams({@ApiImplicitParam(name = "rankKey", value = "绑定键(缓存key)", required = true)})
    @GetMapping(value = "/zcard")
    public Long zcard(String rankKey){
        return redisUtils.zcard(rankKey);
    }

    @ApiOperation(value = "7. 获取绑定键中的元素的下标", notes = "7. 获取绑定键中的元素的下标")
    @ApiImplicitParams({@ApiImplicitParam(name = "rankKey", value = "绑定键(缓存key)", required = true),
            @ApiImplicitParam(name = "value", value = "元素   （userId）", required = true)})
    @GetMapping(value = "/zrank")
    public Long zrank(String rankKey,Integer value){
        return redisUtils.zrank(rankKey,value);
    }

    @ApiOperation(value = "8. 获取绑定键中的元素的下标  (榜单排名)", notes = "8. 获取绑定键中的元素的下标  (榜单排名)")
    @ApiImplicitParams({@ApiImplicitParam(name = "rankKey", value = "绑定键(缓存key)", required = true),
            @ApiImplicitParam(name = "value", value = "元素   （userId）", required = true)})
    @GetMapping(value = "/zreverseRank")
    public Long zreverseRank(String rankKey,Integer value){
        return redisUtils.zreverseRank(rankKey,value);
    }
}


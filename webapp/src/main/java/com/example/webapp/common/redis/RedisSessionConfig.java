package com.example.webapp.common.redis;

import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * session托管到redis
 *  maxInactiveIntervalInSeconds 失效时间 默认1800秒，30分钟
 */
@Configuration
@EnableRedisHttpSession(maxInactiveIntervalInSeconds= 1800*2, redisNamespace = "yidu-session")
public class RedisSessionConfig {
}

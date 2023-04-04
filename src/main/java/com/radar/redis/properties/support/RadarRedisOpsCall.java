package com.radar.redis.properties.support;

import org.springframework.data.redis.core.RedisOperations;

public interface RadarRedisOpsCall {
    void execute(RedisOperations<String, String> ops);
}

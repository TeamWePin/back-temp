package com.radar.redis.properties.support;

import org.springframework.data.redis.core.RedisOperations;

public interface RadarRedisOpsCallback<T> {
    T execute(RedisOperations<String, String> ops);
}

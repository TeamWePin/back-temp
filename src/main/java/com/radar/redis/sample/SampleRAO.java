package com.radar.redis.sample;

import com.radar.redis.properties.RedisServer;
import com.radar.redis.properties.annotations.RedisCache;
import com.radar.redis.properties.support.RadarRedisTemplateSupport;
import org.springframework.data.redis.core.RedisOperations;

@RedisCache
public class SampleRAO extends RadarRedisTemplateSupport {
    public String getHealth(RedisServer server) {
        return execute(server, RedisOperations::randomKey);
    }
}

package com.radar.redis.configuration.health;

import com.radar.redis.properties.HanteoRedisProperties;
import com.radar.redis.properties.RedisConfig;
import com.radar.redis.properties.RedisServer;
import com.radar.redis.sample.SampleRAO;
import com.radar.redis.util.health.ConnectionCheck;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class RedisConnectionCheck extends ConnectionCheck<HanteoRedisProperties> {

    private final SampleRAO sampleRAO;
    public RedisConnectionCheck(SampleRAO sampleRAO, RedisConfig redisConfig) {
        this.sampleRAO = sampleRAO;

        setTargetInfoList(redisConfig.getServers());
    }

    @Override
    public void connTest(String key, HanteoRedisProperties properties) throws Exception {
        logger.debug("{} PING: {}", key, sampleRAO.getHealth(RedisServer.findByKey(key)));
    }

    @Override
    public String getUrl(HanteoRedisProperties properties) {
        return properties.getHost();
    }
}

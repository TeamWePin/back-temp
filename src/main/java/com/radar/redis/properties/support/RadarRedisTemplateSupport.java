package com.radar.redis.properties.support;

import com.radar.redis.properties.RedisServer;
import com.radar.redis.properties.template.RadarRedisTemplate;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisConnectionUtils;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;

public class RadarRedisTemplateSupport extends MultiRedisSupport {
    /* 기본적으로 SessionCallback과 RedisQuery/RedisTemplate 쪽은 상성이 맞지 않음
     * (동적으로 생성된 RedisTemplate에 SessionCallback의 제네릭이 들어맞질 않음)
     * 따라서 SessionCallback을 rawtype으로 사용함 */
    @Deprecated
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Object legacyExecute(RedisServer server, SessionCallback sessionCallback) {
        return redisTemplate(server.getKey()).execute(sessionCallback);
    }

    public <T> T execute(RedisServer server, RadarRedisOpsCallback<T> callback) {
        RadarRedisTemplate redisTemplate = redisTemplate(server.getKey());

        RedisConnectionFactory factory = redisTemplate.getRequiredConnectionFactory();
        RedisConnectionUtils.bindConnection(factory, false);

        T result;
        try {
            result = callback.execute(getRedisOperations(redisTemplate));
        } finally {
            RedisConnectionUtils.unbindConnection(factory);
        }

        return result;
    }

    public void consume(RedisServer server, RadarRedisOpsCall call) {
        RadarRedisTemplate redisTemplate = redisTemplate(server.getKey());

        RedisConnectionFactory factory = redisTemplate.getRequiredConnectionFactory();
        RedisConnectionUtils.bindConnection(factory, false);

        try {
            call.execute(getRedisOperations(redisTemplate));
        } finally {
            RedisConnectionUtils.unbindConnection(factory);
        }
    }

    private RedisOperations<String, String> getRedisOperations(RedisTemplate<?, ?> redisTemplate) {
        @SuppressWarnings("unchecked")
        RedisOperations<String, String> ops = (RedisOperations<String, String>) redisTemplate;

        return ops;
    }
}
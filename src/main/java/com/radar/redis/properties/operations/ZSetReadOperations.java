package com.radar.redis.properties.operations;

import lombok.Getter;

import java.util.Set;

public class ZSetReadOperations {
    @Getter private final RedisReadOperations operations;

    public ZSetReadOperations(RedisReadOperations operations) {
        this.operations = operations;
    }

    public Set rangeByScore(String key, int min, int max, int offset, int limit) {
        return getOperations().getBaseOps().opsForZSet().rangeByScore(key, min, max, offset, limit);
    }
    public Set reverseRangeByScore(String key, int min, int max, int offset, int limit) {
        return getOperations().getBaseOps().opsForZSet().reverseRangeByScore(key, min, max, offset, limit);
    }

    public Long size(String key) {
        // O(1)
        return getOperations().getBaseOps().opsForZSet().zCard(key);
    }

}

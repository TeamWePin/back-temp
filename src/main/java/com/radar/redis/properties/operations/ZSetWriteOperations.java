package com.radar.redis.properties.operations;

import lombok.Getter;

public class ZSetWriteOperations {
    @Getter private final RedisWriteOperations operations;

    public ZSetWriteOperations(RedisWriteOperations operations) {
        this.operations = operations;
    }

    public void add(String key, String value, int score) {
        getOperations().getBaseOps().opsForZSet().add(key, value, score);
    }

    public void remove(String key, Object ...values) {
        getOperations().getBaseOps().opsForZSet().remove(key, values);
    }

    public Long size(String key){
        return getOperations().getBaseOps().opsForZSet().zCard(key);
    }
}

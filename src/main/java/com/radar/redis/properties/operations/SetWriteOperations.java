package com.radar.redis.properties.operations;

import lombok.Getter;

public class SetWriteOperations {
    @Getter
    private final RedisWriteOperations operations;

    public SetWriteOperations(RedisWriteOperations operations) {
        this.operations = operations;
    }

    public void add(String key, String ...value) {
        getOperations().getBaseOps().opsForSet().add(key, value);
    }
    public void remove(String key, String ...value) {
        getOperations().getBaseOps().opsForSet().remove(key, value);
    }
}

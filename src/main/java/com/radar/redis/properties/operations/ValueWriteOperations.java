package com.radar.redis.properties.operations;

import lombok.Getter;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ValueWriteOperations {
    @Getter private final RedisWriteOperations operations;
    public ValueWriteOperations(RedisWriteOperations operations) {
        this.operations = operations;
    }

    public void set(String key, String value) {
        getOperations().getBaseOps().opsForValue().set(key, value);
    }

    public void set(String key, String value, long timeout, TimeUnit unit) {
        getOperations().getBaseOps().opsForValue().set(key, value, timeout, unit);
    }

    public void multiSet(Map<String, String> map) {
        getOperations().getBaseOps().opsForValue().multiSet(map);
    }

    public void increment(String key) {
        getOperations().getBaseOps().opsForValue().increment(key);
    }

    public void set(String key, String value, long offset) {
        getOperations().getBaseOps().opsForValue().set(key, value, offset);
    }
}

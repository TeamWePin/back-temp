package com.radar.redis.properties.operations;

import lombok.Getter;
import org.springframework.data.redis.core.HashOperations;

import java.util.Map;

public class HashWriteOperations {
    @Getter private final RedisWriteOperations operations;
    public HashWriteOperations(RedisWriteOperations operations) {
        this.operations = operations;
    }

    public void delete(String key, String... hashKeys) {
        HashOperations<String, String, String> hashOperations = getOperations().getBaseOps().opsForHash();

        hashOperations.delete(key, (Object[]) hashKeys);
    }

    public void putAll(String key, Map<String, String> m) {
        getOperations().getBaseOps().opsForHash().putAll(key, m);
    }

    public void put(String key, String hashKey, String value) {
        getOperations().getBaseOps().opsForHash().put(key, hashKey, value);
    }
}

package com.radar.redis.properties.operations;

import lombok.Getter;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ScanOptions;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HashReadOperations {
    @Getter private final RedisReadOperations operations;
    public HashReadOperations(RedisReadOperations operations) {
        this.operations = operations;
    }

    public Boolean hasKey(String key, String hashKey) {
        return getOperations().getBaseOps().opsForHash().hasKey(key, hashKey);
    }

    public String get(String key, String hashKey) {
        HashOperations<String, String, String> hashOperations = getOperations().getBaseOps().opsForHash();
        return hashOperations.get(key, hashKey);
    }

    public List<String> multiGet(String key, Collection<String> hashKeys) {
        HashOperations<String, String, String> hashOperations = getOperations().getBaseOps().opsForHash();
        return hashOperations.multiGet(key, hashKeys);
    }

    public Set<String> keys(String key) {
        HashOperations<String, String, String> hashOperations = getOperations().getBaseOps().opsForHash();
        return hashOperations.keys(key);
    }

    public Long lengthOfValue(String key, String hashKey) {
        return getOperations().getBaseOps().opsForHash().lengthOfValue(key, hashKey);
    }

    public Long size(String key) {
        return getOperations().getBaseOps().opsForHash().size(key);
    }

    public List<String> values(String key) {
        HashOperations<String, String, String> hashOperations = getOperations().getBaseOps().opsForHash();
        return hashOperations.values(key);
    }

    public Map<String, String> entries(String key) {
        HashOperations<String, String, String> hashOperations = getOperations().getBaseOps().opsForHash();
        return hashOperations.entries(key);
    }

    public Cursor<Map.Entry<String, String>> scan(String key, ScanOptions options) {
        HashOperations<String, String, String> hashOperations = getOperations().getBaseOps().opsForHash();
        return hashOperations.scan(key, options);
    }
}

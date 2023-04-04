package com.radar.redis.properties.operations;

import lombok.Getter;
import org.springframework.data.redis.connection.BitFieldSubCommands;

import java.util.Collection;
import java.util.List;

public class ValueReadOperations {
    @Getter private final RedisReadOperations operations;
    public ValueReadOperations(RedisReadOperations operations) {
        this.operations = operations;
    }

    public String get(String key) {
        return getOperations().getBaseOps().opsForValue().get(key);
    }

    public List<String> multiGet(Collection<String> keys) {
        return getOperations().getBaseOps().opsForValue().multiGet(keys);
    }

    public String get(String key, long start, long end) {
        return getOperations().getBaseOps().opsForValue().get(key, start, end);
    }

    public Long size(String key) {
        return getOperations().getBaseOps().opsForValue().size(key);
    }

    public Boolean getBit(String key, long offset) {
        return getOperations().getBaseOps().opsForValue().getBit(key, offset);
    }

    public List<Long> bitField(String key, BitFieldSubCommands subCommands) {
        return getOperations().getBaseOps().opsForValue().bitField(key, subCommands);
    }
}

package com.radar.redis.properties.operations;

import lombok.Getter;

import java.util.Set;

public class SetReadOperations {
    @Getter
    private final RedisReadOperations operations;

    public SetReadOperations(RedisReadOperations operations) {
        this.operations = operations;
    }

    public boolean isMember(String key, Object o) {
        return getOperations().getBaseOps().opsForSet().isMember(key, o);
    }

    public Set members(String key){
        return getOperations().getBaseOps().opsForSet().members(key);
    }
}

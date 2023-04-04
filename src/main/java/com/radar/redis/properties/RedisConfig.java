package com.radar.redis.properties;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

public class RedisConfig {
    @Getter @Setter private Map<String, HanteoRedisProperties> servers;
    @Getter @Setter private String defaultServer;
}

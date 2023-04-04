package com.radar.redis.properties;

import lombok.Getter;

public enum RedisServer {

    // 3.0
    COMMENT_SERVER("comment"),
    CONTENTS_SERVER("contents"),

    // 4.0
    AUTH_SERVER("auth"),
    NEWS_SERVER("news"),
    META_SERVER("meta"),
    CHART_SERVER("chart"),
    USER_SERVER("user");
    @Getter private final String key;

    RedisServer(String key) {
        this.key = key;
    }

    /**
     * 레디스 키로 RedisServer ENUM 조회를 위한 메서드
     *
     * @param key redis-config.yml에 기재된 레디스서버 이름(문자열 값)
     * @return RedisServer ENUM
     */
    public static RedisServer findByKey(String key) {
        for (RedisServer server : RedisServer.values()) {
            if (server.getKey().equals(key)) {
                return server;
            }
        }

        return null;
    }

}

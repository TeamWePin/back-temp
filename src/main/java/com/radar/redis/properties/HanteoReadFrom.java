package com.radar.redis.properties;

import io.lettuce.core.ReadFrom;
import lombok.Getter;

public enum HanteoReadFrom {

    UPSTREAM("UPSTREAM", ReadFrom.UPSTREAM),
    UPSTREAM_PREFERRED("UPSTREAM_PREFERRED", ReadFrom.UPSTREAM_PREFERRED),
    REPLICA_PREFERRED("REPLICA_PREFERRED", ReadFrom.REPLICA_PREFERRED),
    REPLICA("REPLICA", ReadFrom.REPLICA),
    ANY("ANY", ReadFrom.ANY),
    ANY_REPLICA("ANY_REPLICA", ReadFrom.ANY_REPLICA),
    NEAREST("NEAREST", ReadFrom.NEAREST);


    private final String key;
    @Getter private final ReadFrom readFrom;

    HanteoReadFrom(String key, ReadFrom readFrom) {
        this.key = key;
        this.readFrom = readFrom;
    }

}

package com.radar.redis.properties;

import io.lettuce.core.ReadFrom;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;

import java.time.Duration;

public class HanteoRedisProperties extends RedisProperties {
    @Getter @Setter private String readFrom;

    public ReadFrom getReadFromValue() {
        if (readFrom == null) {
            return ReadFrom.REPLICA_PREFERRED;
        } else {
            return HanteoReadFrom.valueOf(readFrom).getReadFrom();
        }
    }

    private long commandTimeout;
    public long getCommandTimeout() {
        return this.commandTimeout;
    }
    public void setCommandTimeout(long timeout) {
        super.setTimeout(Duration.ofSeconds(timeout));
        this.commandTimeout = timeout;
    }

    @Getter @Setter private boolean compressValue = true; // 기본적으로 ZLIB 압축은 디폴트로 사용한다는 전제를 깔고 감, 필요할 경우 YAML 파일에서 비활성화바람

    @Deprecated // 추후 삭제예정
    @Getter @Setter private boolean legacySerializer = false; // 레거시 직렬화기 (HanteoJacksonRedisSerializer) 가 필요할 경우 true로 설정
}

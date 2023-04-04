package com.radar.redis.util.health;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.radar.redis.util.ExceptionUtils.getCauses;

@Slf4j
public class HealthCheck implements HealthIndicator {
    public static final String UP = "UP";
    public static final String DOWN = "DOWN";
    public static final String STATUS = "status";

    private final ConnectionCheck<?> connectionCheck;
    public HealthCheck(ConnectionCheck<?> connectionCheck) {
        this.connectionCheck = connectionCheck;
    }

    @Override
    public Health health() {
        try {
            Map<String, Map<String, Object>> targetResponseList = connectionCheck.doConnTest();
            if (targetResponseList == null) return null;

            boolean healthy = true;
            for (Map<String, Object> targetResponse : targetResponseList.values()) {
                if (DOWN.equals(targetResponse.get(STATUS))) {
                    healthy = false;
                }
            }

            if (healthy) {
                return Health.up()
                        .withDetails(targetResponseList)
                        .build();
            } else {
                return Health.down()
                        .withDetails(targetResponseList)
                        .build();
            }
        } catch (Exception e) {
            logger.error("Some other exception", e);

            Map<String, Object> causes = new LinkedHashMap<>();
            causes.put(e.getClass().getCanonicalName(), e.getMessage());
            for (Throwable cause : getCauses(e)) {
                causes.put(cause.getClass().getCanonicalName(), cause.getMessage());
            }

            return Health.down()
                    .withDetail("type", e.getClass().getCanonicalName())
                    .withDetail("message", e.getMessage())
                    .withDetail("causes", causes)
                    .build();
        }
    }

}

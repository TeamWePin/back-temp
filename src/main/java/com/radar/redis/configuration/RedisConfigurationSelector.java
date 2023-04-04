package com.radar.redis.configuration;

import com.radar.redis.configuration.health.RedisHealthCheckConfiguration;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

public class RedisConfigurationSelector implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {

        return new String[]{
                RedisConfiguration.class.getName(),
                RedisHealthCheckConfiguration.class.getName()
        };

    }
}

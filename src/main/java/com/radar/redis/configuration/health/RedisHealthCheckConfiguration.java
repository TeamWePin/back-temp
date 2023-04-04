package com.radar.redis.configuration.health;

import com.radar.configuration.CommonHealthCheckConfiguration;
import com.radar.redis.configuration.RedisConfigurationSelector;
import com.radar.redis.properties.RedisConfig;
import com.radar.redis.properties.annotations.EnableRedisSource;
import com.radar.redis.sample.SampleRAO;
import com.radar.redis.util.health.HealthCheck;
import org.springframework.context.annotation.Bean;

/**
 * 멀티 데이터소스들의 헬스 체크 빈을 관리하는 Configuration
 *
 * database-config.yml 파일에 쓰여진 데이터소스 링크들에 대한 정상 접속 여부 등을 확인하는 헬스체크를 수행함
 *
 * {@link EnableRedisSource} 애노테이션 설정 시 {@link RedisConfigurationSelector} 클래스에 의해 해당 클래스가 로드됨
 * 이후 헬스체크 페이지 접속 시 DB 접속 체크를 추가적으로 수행하여 결과를 같이 출력함
 *
 *
 * @see CommonHealthCheckConfiguration
 */

public class RedisHealthCheckConfiguration {

    @Bean(name = "REDIS CONNECTION")
    public HealthCheck redisConnectionCheck(SampleRAO sampleRAO, RedisConfig redisConfig) {
        return new HealthCheck(new RedisConnectionCheck(sampleRAO, redisConfig));
    }

}

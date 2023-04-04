package com.radar.redis.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.radar.core.exception.RadarCommonException;
import com.radar.redis.properties.RedisConfig;
import com.radar.redis.properties.RedisConstants;
import com.radar.redis.properties.support.MultipleRedisConnectionFactoryPostProcessor;
import com.radar.redis.properties.template.RadarRedisTemplateMap;
import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.DefaultClientResources;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;

import static com.radar.core.exception.RadarErrorType.ERROR_SYSTEM;
import static com.radar.core.exception.RadarServiceStatusCode.*;


@Slf4j
@ComponentScan(
        basePackages = {
                "com.radar.redis",
        }
)
@EnableRedisRepositories
public class RedisConfiguration {

    @Bean
    public RedisConfig redisConfig() throws RadarCommonException {
        Yaml yaml = new Yaml();
        try (InputStream in = (new ClassPathResource("/redis-source.yml")).getInputStream()) {
            return yaml.loadAs(in, RedisConfig.class);
        } catch (Exception e) {
            throw new RadarCommonException(ERROR_SYSTEM, ERROR_SYSTEM_EXCEPTION, e);
        }
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.registerModules(new JavaTimeModule(), new Jdk8Module());
        return mapper;
    }

    @Bean(destroyMethod = "shutdown")
    @ConditionalOnMissingBean(ClientResources.class)
    public DefaultClientResources lettuceClientResources() {
        return DefaultClientResources.create();
    }

    @Bean
    public MultipleRedisConnectionFactoryPostProcessor multipleRedisConnectionFactoryPostProcessor(RedisConfig redisConfig, ClientResources clientResources, ObjectMapper objectMapper) {
        MultipleRedisConnectionFactoryPostProcessor multipleRedisConnectionFactoryPostProcessor = new MultipleRedisConnectionFactoryPostProcessor();
        multipleRedisConnectionFactoryPostProcessor.setRedisServerMap(redisConfig.getServers());
        multipleRedisConnectionFactoryPostProcessor.setClientResources(clientResources);
        multipleRedisConnectionFactoryPostProcessor.setObjectMapper(objectMapper);
        return multipleRedisConnectionFactoryPostProcessor;
    }

    @Bean("hanteoRedisTemplate")
    public RadarRedisTemplateMap hanteoRedisTemplateMap(RedisConfig redisConfig) {
        RadarRedisTemplateMap hanteoRedisTemplateMap = new RadarRedisTemplateMap();
        hanteoRedisTemplateMap.setRedisServerMap(redisConfig.getServers());
        hanteoRedisTemplateMap.setDefaultServerKey(redisConfig.getDefaultServer());
        hanteoRedisTemplateMap.setHanteoTemplatePrefix(RedisConstants.TEMPLATE_PREFIX);
        return hanteoRedisTemplateMap;
    }

}

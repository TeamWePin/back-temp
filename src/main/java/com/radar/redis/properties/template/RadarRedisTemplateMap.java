package com.radar.redis.properties.template;

import com.radar.redis.properties.HanteoRedisProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class RadarRedisTemplateMap implements InitializingBean, ApplicationContextAware {
    @Setter private ApplicationContext applicationContext;

    @Setter private String hanteoTemplatePrefix;
    @Setter private Map<String, HanteoRedisProperties> redisServerMap;

    @Getter @Setter private HashMap<String, RadarRedisTemplate> redisTemplateHashMap;
    @Getter @Setter private String defaultServerKey;

    @Override
    public void afterPropertiesSet() throws Exception {
        if(redisServerMap==null) return;

        redisTemplateHashMap = new HashMap<>();
        logger.debug("------------------------------------------------------------------------------------------");
        for (String key : redisServerMap.keySet()) {
            RadarRedisTemplate redisTemplate = applicationContext.getBean(hanteoTemplatePrefix + key, RadarRedisTemplate.class);
            logger.debug("put !!!! => sqlSessionTemplate.jdbc.{} :: {}", key, redisTemplate);
            redisTemplateHashMap.put(key, redisTemplate);
        }
        logger.debug("------------------------------------------------------------------------------------------");
    }
}

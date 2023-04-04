package com.radar.redis.properties.support;

import com.radar.core.exception.RadarCommonException;
import com.radar.redis.properties.template.RadarRedisTemplate;
import com.radar.redis.properties.template.RadarRedisTemplateMap;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Arrays;
import java.util.HashMap;

@Slf4j
public class MultiRedisSupport implements ApplicationContextAware {

    @Getter private RadarRedisTemplateMap radarRedisTemplateMap;

    @Getter @Setter private String server;

    private HashMap<String, RadarRedisTemplate> redisTemplateHashMap;
    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        radarRedisTemplateMap = context.getBean("hanteoRedisTemplate", RadarRedisTemplateMap.class);

        redisTemplateHashMap = radarRedisTemplateMap.getRedisTemplateHashMap();
        server = radarRedisTemplateMap.getDefaultServerKey();
    }

    protected RadarRedisTemplate redisTemplate(String key) {
        if (redisTemplateHashMap.containsKey(key)) {
            return redisTemplateHashMap.get(key);
        } else {
            logStackTrace(key);
            throw new RadarCommonException("The redis map has not '" + key + "' session");
        }
    }

    private void logStackTrace(String key) {
        logger.info("Not find key : [{}]", key);

        Thread th = Thread.currentThread();

        StackTraceElement[] elements = th.getStackTrace();
        if (ArrayUtils.isEmpty(elements)) {
            logger.error("Fail to th.getStackTrace()!");
            return;
        }

        Arrays.stream(elements)
                .forEach(element -> {
                    if (StringUtils.isNotEmpty(element.getFileName())) {
                        logger.error("\tat {}.{}({}:{})", element.getClassName(), element.getMethodName(), element.getFileName(), element.getLineNumber());
                    } else {
                        logger.error("\tat {}.{}", element.getClassName(), element.getMethodName());
                    }
                });
    }
}

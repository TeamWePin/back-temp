package com.radar.core.properties.cache;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

public class HanteoKeyGenerator implements KeyGenerator {

    @Override
    public Object generate(Object o, Method method, Object... params) {
        CacheConfig cacheConfig = o.getClass().getDeclaredAnnotation(CacheConfig.class);
        String key = "ht_";
        key = key + StringUtils.arrayToDelimitedString(cacheConfig.cacheNames(), "_") + "_";
        key = key + StringUtils.arrayToDelimitedString(params, "_");

        return key;
    }

}

package com.radar.redis.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.radar.core.exception.RadarCommonException;
import com.radar.redis.properties.operations.RedisReadOperations;
import com.radar.redis.properties.operations.RedisWriteOperations;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.radar.core.util.ObjectUtils.getJavaType;


/**
 * BasicRAO로 받아온 READ 및 WRITE Operations를 이용해 자주 사용되는 로직들을 유틸리티로 묶어 사용할 수 있게 함.
 *
 * Operations를 사용하는 것을 로직화시켰기에 RedisOperations를 인자로 받으며, static 메서드로 구성됨
 */
@Slf4j
public class SimpleOps {
    private static final ObjectMapper MAPPER = new ObjectMapper();

//    static {
//        SimpleModule module = new SimpleModule();
//        module.addSerializer(String.class, new JsonSerializer<String>() {
//            @Override
//            public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
//                gen.writeString(StringCompressUtils.zip(value));
//            }
//        });
//        module.addDeserializer(String.class, new JsonDeserializer<String>() {
//            @Override
//            public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
//                return StringCompressUtils.unzip(p.getText());
//            }
//        });
//
//        MAPPER.registerModule(module);
//    }

    public static void setExpire(RedisWriteOperations ops, String key, long expiresIn, TimeUnit expiryUnit) {
        ops.expire(key, expiresIn, expiryUnit);
    }

    public static Long getExpire(RedisReadOperations ops, String key) {
        return ops.getExpire(key);
    }

    public static void setCacheRaw(RedisWriteOperations ops, String key, String rawValue) {
        // value를 넣어주지 않으면 기본적으로 값이 들어가지 않게끔 처리함
        // 또한 경고를 띄워 추후에 문제를 찾기 용이하게 함
        if (rawValue == null) {
            ops.delete(key);
            logger.warn("Try to set null in redis key {}, deleting!", key);
        } else {
            ops.opsForValue().set(key, rawValue);
        }
    }
    public static void setCache(RedisWriteOperations ops, String key, Object value) {
        String json = json(value);
        setCacheRaw(ops, key, json);
    }

    public static void setCacheRaw(RedisWriteOperations ops, String key, String hashKey, String rawValue) {
        if (rawValue == null) {
            // value를 넣어주지 않으면 기본적으로 값을 삭제하도록 함
            // 또한 경고를 띄워 추후에 문제를 찾기 용이하게 함
            ops.opsForHash().delete(key, hashKey);
            logger.warn("Try to set null in redis hash {} {}, deleting!", key, hashKey);
        } else {
            ops.opsForHash().put(key, hashKey, rawValue);
        }
    }
    public static void setCache(RedisWriteOperations ops, String key, String hashKey, Object value) {
        String json = json(value);
        setCacheRaw(ops, key, hashKey, json);
    }

    public static String getCacheRaw(RedisReadOperations ops, String key) {
        return ops.opsForValue().get(key);
    }
    public static <T> T getCache(RedisReadOperations ops, String key, Class<T> cls) {
        String json = getCacheRaw(ops, key);
        return parse(json, cls);
    }
    public static <T> T getCache(RedisReadOperations ops, String key, TypeReference<T> typeRef) {
        String json = getCacheRaw(ops, key);
        return parse(json, typeRef);
    }
    public static <T> T getCache(RedisReadOperations ops, String key, Type type) {
        String json = getCacheRaw(ops, key);
        return parse(json, getJavaType(type));
    }

    public static List<String> getCacheRaw(RedisReadOperations ops, Collection<String> key) {
        return ops.opsForValue().multiGet(key);
    }
    public static <T> List<T> getCache(RedisReadOperations ops, Collection<String> key, Class<T> cls) {
        List<String> jsonList = getCacheRaw(ops, key);
        return convertList(jsonList, cls);
    }
    public static <T> List<T> getCache(RedisReadOperations ops, Collection<String> key, TypeReference<T> typeRef) {
        List<String> jsonList = getCacheRaw(ops, key);
        return convertList(jsonList, typeRef);
    }
    public static <T> List<T> getCache(RedisReadOperations ops, Collection<String> key, Type type) {
        List<String> jsonList = getCacheRaw(ops, key);
        return convertList(jsonList, type);
    }

    public static String getCacheRaw(RedisReadOperations ops, String key, String hashKey) {
        return ops.opsForHash().get(key, hashKey);
    }
    public static <T> T getCache(RedisReadOperations ops, String key, String hashKey, Class<T> cls) {
        String json = getCacheRaw(ops, key, hashKey);
        return parse(json, cls);
    }
    public static <T> T getCache(RedisReadOperations ops, String key, String hashKey, TypeReference<T> typeRef) {
        String json = getCacheRaw(ops, key, hashKey);
        return parse(json, typeRef);
    }
    public static <T> T getCache(RedisReadOperations ops, String key, String hashKey, Type type) {
        String json = getCacheRaw(ops, key, hashKey);
        return parse(json, getJavaType(type));
    }

    public static List<String> getCacheRaw(RedisReadOperations ops, String key, Collection<String> hashKey) {
        return ops.opsForHash().multiGet(key, hashKey);
    }
    public static <T> List<T> getCache(RedisReadOperations ops, String key, Collection<String> hashKey, Class<T> cls) {
        List<String> jsonList = getCacheRaw(ops, key, hashKey);
        return convertList(jsonList, cls);
    }
    public static <T> List<T> getCache(RedisReadOperations ops, String key, Collection<String> hashKey, TypeReference<T> typeRef) {
        List<String> jsonList = getCacheRaw(ops, key, hashKey);
        return convertList(jsonList, typeRef);
    }
    public static <T> List<T> getCache(RedisReadOperations ops, String key, Collection<String> hashKey, Type type) {
        List<String> jsonList = getCacheRaw(ops, key, hashKey);
        return convertList(jsonList, type);
    }

    public static Map<String, String> getCacheEntriesRaw(RedisReadOperations ops, String key) {
        return ops.opsForHash().entries(key);
    }
    public static <T> Map<String, T> getCacheEntries(RedisReadOperations ops, String key, Class<T> cls) {
        Map<String, String> jsonMap = getCacheEntriesRaw(ops, key);
        return convertMap(jsonMap, cls);
    }
    public static <T> Map<String, T> getCacheEntries(RedisReadOperations ops, String key, TypeReference<T> typeRef) {
        Map<String, String> jsonMap = getCacheEntriesRaw(ops, key);
        return convertMap(jsonMap, typeRef);
    }
    public static <T> Map<String, T> getCacheEntries(RedisReadOperations ops, String key, Type type) {
        Map<String, String> jsonMap = getCacheEntriesRaw(ops, key);
        return convertMap(jsonMap, type);
    }

    public static void delCache(RedisWriteOperations ops, String key) {
        ops.delete(key);
    }
    public static void delCache(RedisWriteOperations ops, String key, String hashKey) {
        ops.opsForHash().delete(key, hashKey);
    }

    private static <T> List<T> convertList(List<String> jsonList, Class<T> cls) {
        List<T> result = new ArrayList<>();
        for (String json : jsonList) {
            result.add(parse(json, cls));
        }
        return result;
    }
    private static <T> List<T> convertList(List<String> jsonList, TypeReference<T> typeRef) {
        List<T> result = new ArrayList<>();
        for (String json : jsonList) {
            result.add(parse(json, typeRef));
        }
        return result;
    }
    private static <T> List<T> convertList(List<String> jsonList, Type type) {
        List<T> result = new ArrayList<>();
        for (String json : jsonList) {
            result.add(parse(json, getJavaType(type)));
        }
        return result;
    }

    private static <T> Map<String, T> convertMap(Map<String, String> jsonMap, Class<T> cls) {
        Map<String, T> result = new HashMap<>();
        for (String key : jsonMap.keySet()) {
            if (jsonMap.get(key) != null) {
                result.put(key, parse(jsonMap.get(key), cls));
            }
        }
        return result;
    }
    private static <T> Map<String, T> convertMap(Map<String, String> jsonMap, TypeReference<T> typeRef) {
        Map<String, T> result = new HashMap<>();
        for (String key : jsonMap.keySet()) {
            if (jsonMap.get(key) != null) {
                result.put(key, parse(jsonMap.get(key), typeRef));
            }
        }
        return result;
    }
    private static <T> Map<String, T> convertMap(Map<String, String> jsonMap, Type type) {
        Map<String, T> result = new HashMap<>();
        for (String key : jsonMap.keySet()) {
            if (jsonMap.get(key) != null) {
                result.put(key, parse(jsonMap.get(key), getJavaType(type)));
            }
        }
        return result;
    }

    private static String json(Object value) {
        String json = null;
        if (value != null) {
            try {
                json = MAPPER.writeValueAsString(value);
            } catch (IOException e) {
                throw new RadarCommonException(e);
            }
        }
        return json;
    }

    private static <T> T parse(String json, Class<T> cls) {
        try {
            if (json != null) {
                return MAPPER.readValue(json, cls);
            } else {
                return null;
            }
        } catch (IOException e) {
            throw new RadarCommonException(e);
        }
    }
    private static <T> T parse(String json, TypeReference<T> typeRef) {
        try {
            if (json != null) {
                return MAPPER.readValue(json, typeRef);
            } else {
                return null;
            }
        } catch (IOException e) {
            throw new RadarCommonException(e);
        }
    }
    private static <T> T parse(String json, Type type) {
        try {
            if (json != null) {
                return MAPPER.readValue(json, getJavaType(type));
            } else {
                return null;
            }
        } catch (IOException e) {
            throw new RadarCommonException(e);
        }
    }
}

package com.radar.redis.properties.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Deprecated
public class RadarJacksonRedisSerializer extends GenericJackson2JsonRedisSerializer {

    private final StringRedisSerializer redisSerializer;
    public RadarJacksonRedisSerializer(ObjectMapper mapper) {
        super(mapper);
        redisSerializer = new StringRedisSerializer();
    }

    @Override
    public byte[] serialize(Object source) throws SerializationException {
        if (source == null) throw new SerializationException("Source cannot be null!");
        if (source.getClass().getName().equals("java.lang.String")) {
            return redisSerializer.serialize((String) source);
        }
        return super.serialize(source);
    }

    @Override
    public Object deserialize(byte[] source) throws SerializationException {
        /** String 하고 Object를 어떻게 구분하지. ????? */
        try {
            return super.deserialize(source);
        } catch (SerializationException e) {
            return redisSerializer.deserialize(source);
        }
    }

}

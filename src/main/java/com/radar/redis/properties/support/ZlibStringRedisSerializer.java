package com.radar.redis.properties.support;

import com.radar.redis.util.ZlibUtils;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * java.util.zip.Deflater 및 java.util.zip.Inflater 클래스를 이용하여 String을 압축하는 로직이 추가로 들어간 Serializer
 * ZLIB 압축을 사용하며, 반복되는 String 덩어리가 많을수록 압축 효율이 올라감
 * -> 반복되는 문자열로 가득한 매우 큰 데이터(75MB) 기준 최대 95% 압축률(4MB로 압축됨)을 보임
 *
 * 압축 로직은 해당 링크를 참조하였음: https://dzone.com/articles/how-compress-and-uncompress
 */
@Slf4j
@ToString
public class ZlibStringRedisSerializer extends StringRedisSerializer {
    @Override
    public String deserialize(byte[] bytes) {
        // 숫자에 대한 예외처리, 숫자에 대해서는 REDIS에서 사용 가능한 전용 로직 (Incr, Decr 등 숫자 연산에 관한 로직) 이 있기에, ZLIB 압축 처리하지 않음
        if (isSmallBytes(bytes)) { // 너무 큰 bytes에 대해 처리하려고 할 때 서버에 부하가 갈 것을 우려하여 1차 필터링
            String result = super.deserialize(bytes); // 1차적으로 역직렬화, 이후 값이 정상적인 숫자면 그대로 반환, 문제가 있다면 다시 아래에서 압축해제후 역직렬화
            if (NumberUtils.isParsable(result)) {
                return result;
            }
        }

        return super.deserialize(ZlibUtils.decompress(bytes));
    }

    @Override
    public byte[] serialize(String string) {
        byte[] bytes = super.serialize(string);

        // 숫자에 대한 예외처리, 숫자에 대해서는 REDIS에서 사용 가능한 전용 로직 (Incr, Decr 등 숫자 연산에 관한 로직) 이 있기에, ZLIB 압축 처리하지 않음
        if (isSmallBytes(bytes)) {
            if (NumberUtils.isParsable(string)) {
                return bytes;
            }
        }

        return ZlibUtils.compress(bytes);
    }


    private boolean isSmallBytes(byte[] bytes) {
        return bytes != null && bytes.length < 1024;
    }
}

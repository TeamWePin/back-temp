package com.radar.core.properties.cache;

import com.radar.core.model.type.HtMetaType;
import org.springframework.cache.interceptor.KeyGenerator;

import java.lang.reflect.Method;
import java.util.StringJoiner;


/**
 * 메타데이터 캐시의 키 설정
 *
 * 검색
 * cacheName: searchMetaData, key: ht_HtMetaType_Keyword ex)ht_all_청하, ht_album_청하
 *
 * 상세조회
 * cacheName: getMetaDataDetail, key: ht_HtMetaType_idx ex)ht_album_34204146, ht_artist_34204146
 */
public class HanteoMetaKeyGenerator implements KeyGenerator {

    @Override
    public Object generate(Object o, Method method, Object... objects) {
        return createCacheKey(objects);
    }

    public static String createCacheKey(Object... objects) {
        StringJoiner stringJoiner = new StringJoiner("_");
        stringJoiner.add("ht");
        for (Object object : objects) {
            if (object instanceof HtMetaType) {
                HtMetaType type = (HtMetaType) object;
                stringJoiner.add(type.getType());
            } else {
                stringJoiner.add(object.toString());
            }
        }

        // System.out.println("metadata keyname : " + stringJoiner.toString());
        return stringJoiner.toString();
    }
}
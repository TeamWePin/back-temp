package com.radar.redis.properties.operations;

import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.*;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class RedisReadOperations extends RedisBaseOperations {
    public RedisReadOperations(RedisOperations<String, String> ops) {
        super(ops);
    }

    public Boolean hasKey(String key) {
        return getBaseOps().hasKey(key);
    }

    public Long countExistingKeys(Collection<String> keys) {
        return getBaseOps().countExistingKeys(keys);
    }

    public DataType type(String key) {
        return getBaseOps().type(key);
    }

    public Set<String> keys(String pattern) {
        return getBaseOps().keys(pattern);
    }

    public String randomKey() {
        return getBaseOps().randomKey();
    }

    public Long getExpire(String key) {
        return getBaseOps().getExpire(key);
    }

    public Long getExpire(String key, TimeUnit timeUnit) {
        return getBaseOps().getExpire(key, timeUnit);
    }

    public ValueReadOperations opsForValue() {
        return new ValueReadOperations(this);
    }

    public HashReadOperations opsForHash() {
        return new HashReadOperations(this);
    }

    public ZSetReadOperations opsForZSet() {
        return new ZSetReadOperations(this);
    }

    public SetReadOperations opsForSet() {
        return new SetReadOperations(this);
    }

    // TODO 이외의 Operations가 필요할 경우 임시로 getBaseOps().~~~() 를 불러와서 사용할 수는 있음
    // 하지만, 추후엔 READ 메서드 및 WRITE 메서드의 분리를 위해 추가로 클래스를 만드는 것을 권장함
//    public BoundValueOperations<Object, Object> boundValueOps(Object key) {
//        return null;
//    }
//
//    public <HK, HV> BoundHashOperations<Object, HK, HV> boundHashOps(Object key) {
//        return null;
//    }

//    public ClusterOperations<Object, Object> opsForCluster() {
//        return null;
//    }
//
//    public GeoOperations<Object, Object> opsForGeo() {
//        return null;
//    }
//
//    public BoundGeoOperations<Object, Object> boundGeoOps(Object key) {
//        return null;
//    }
//
//    public HyperLogLogOperations<Object, Object> opsForHyperLogLog() {
//        return null;
//    }
//
//    public ListOperations<Object, Object> opsForList() {
//        return null;
//    }
//
//    public BoundListOperations<Object, Object> boundListOps(Object key) {
//        return null;
//    }
//
//
//    public BoundSetOperations<Object, Object> boundSetOps(Object key) {
//        return null;
//    }
//
//    public <HK, HV> StreamOperations<Object, HK, HV> opsForStream() {
//        return null;
//    }
//
//    public <HK, HV> StreamOperations<Object, HK, HV> opsForStream(HashMapper<? super Object, ? super HK, ? super HV> hashMapper) {
//        return null;
//    }
//
//    public <HK, HV> BoundStreamOperations<Object, HK, HV> boundStreamOps(Object key) {
//        return null;
//    }
//
//
//    public BoundZSetOperations<Object, Object> boundZSetOps(Object key) {
//        return null;
//    }
}

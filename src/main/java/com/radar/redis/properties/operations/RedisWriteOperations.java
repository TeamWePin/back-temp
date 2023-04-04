package com.radar.redis.properties.operations;

import org.springframework.data.redis.core.*;
import org.springframework.data.redis.core.query.SortQuery;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class RedisWriteOperations extends RedisBaseOperations {
    public RedisWriteOperations(RedisOperations<String, String> ops) {
        super(ops);
    }

    public Boolean delete(String key) {
        return getBaseOps().delete(key);
    }

    public void delete(Collection<String> keys) {
        getBaseOps().delete(keys);
    }

    public void unlink(String key) {
        getBaseOps().unlink(key);
    }

    public void unlink(Collection<String> keys) {
        getBaseOps().unlink(keys);
    }

    public void rename(String oldKey, String newKey) {
        getBaseOps().rename(oldKey, newKey);
    }

    public void expire(String key, long timeout, TimeUnit unit) {
        getBaseOps().expire(key, timeout, unit);
    }

    public void expire(String key, Duration timeout) {
        getBaseOps().expire(key, timeout);
    }

    public void expireAt(String key, Date date) {
        getBaseOps().expireAt(key, date);
    }

    public void expireAt(String key, Instant expireAt) {
        getBaseOps().expireAt(key, expireAt);
    }

    public void persist(String key) {
        getBaseOps().persist(key);
    }

    public void move(String key, int dbIndex) {
        getBaseOps().move(key, dbIndex);
    }

    public void dump(String key) {
        getBaseOps().dump(key);
    }

    public void restore(String key, byte[] value, long timeToLive, TimeUnit unit, boolean replace) {
        getBaseOps().restore(key, value, timeToLive, unit, replace);
    }

    public void sort(SortQuery<String> query) {
        getBaseOps().sort(query);
    }

    public void sort(SortQuery<String> query, RedisSerializer<?> resultSerializer) {
        getBaseOps().sort(query, resultSerializer);
    }

    public <T> void sort(SortQuery<String> query, BulkMapper<T, String> bulkMapper) {
        getBaseOps().sort(query, bulkMapper);
    }

    public <T, S> void sort(SortQuery<String> query, BulkMapper<T, S> bulkMapper, RedisSerializer<S> resultSerializer) {
        getBaseOps().sort(query, bulkMapper, resultSerializer);
    }

    public void sort(SortQuery<String> query, String storeKey) {
        getBaseOps().sort(query, storeKey);
    }

    public ValueWriteOperations opsForValue() {
        return new ValueWriteOperations(this);
    }

    public HashWriteOperations opsForHash() {
        return new HashWriteOperations(this);
    }
    public ZSetWriteOperations opsForZSet() {
        return new ZSetWriteOperations(this);
    }

    public SetWriteOperations opsForSet() {
        return new SetWriteOperations(this);
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
//    public BoundZSetOperations<Object, Object> boundZSetOps(Object key) {
//        return null;
//    }
}

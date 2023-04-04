package com.radar.redis.properties.template;

import org.springframework.data.redis.core.RedisTemplate;

/**
 * TODO 레디스에 넣고 빼는 타입에 대한 정리
 * {@literal
 * RedisTemplate<String, String> 을 상속받는 것을 목표로 함
 *
 * 또한, HanteoRedisTemplateSupport에서의
 *   private RedisOperations<String, String> getRedisOperations(RedisTemplate redisTemplate)
 * 메서드도 없애는 것을 목표로 함
 *
 * Lettuce 모듈에선 String <-> byte[] 의 처리만 담당, 이후 JSON을 파싱하는 등의 처리는 모두 한터 유틸리티들에게 맡김
 * }
 */
@SuppressWarnings("rawtypes")
public class RadarRedisTemplate extends RedisTemplate {}

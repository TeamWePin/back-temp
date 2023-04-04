package com.radar.redis.properties.operations;

import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * READ와 WRITE용 메서드를 분리하고, RedisServer를 저장 후 RedisServer ENUM과 비교하기 위해 만든 카피 클래스.
 * 내부적으로 RedisOperations를 가지고 있으며, RedisOperations의 기능들을 제한하기 위해 만듦.
 * GET과 SET이 동시에 일어나는 경우 multi() 사용된 환경이라던가, 여러가지로 캐싱 이상의 무언가를 하기 위해서는 생기는 문제점이 존재함.
 *
 * 기본적으로 READ는 중간에 일어날 수 있어도 WRITE는 로직의 끝에서만 일어난다고 가정하고,
 * 무조건 WRITE 명령어는 MULTI를 사용하여 한번에 명령어를 전송한다고 가정하고 해당 클래스를 제작함.
 * - WRITE 구문은 모두 multi() 및 exec() 사이에 들어가므로 중간에 결과값으로 받아오는 값이 모두 null, 따라서 리턴값을 void로 처리함.
 * - READ/WRITE Operations를 나눔으로써 WRITE Operations에 대해서만 DryRun이 수행되었을 때 명령 셋들을 아예 무시하게끔 처리하여
 * DryRun 기능이 정상 동작되도록 함 (레디스 값들에 영향을 주지 않는 READ Operations는 DryRun 유무에 상관없이 수행될 수 있음)
 */
public class RedisBaseOperations {
    @Getter(value = AccessLevel.PACKAGE) private final RedisOperations<String, String> baseOps;
    public RedisBaseOperations(RedisOperations<String, String> baseOps) {
        this.baseOps = baseOps;
    }

    // Watch와 Unwatch의 작동 방식 및 multi와 exec간의 연관성이 어떻게 되는지에 대해 조사가 필요함
    // 서버에 도입해도 문제없는 기능이고(테스트 필요), 여러 방면으로 괜찮은 기능이다 싶으면 시간을 두고 도입하면 될 듯
//    public void watch(Object key) {
//        getBaseOps().watch(key);
//    }
//
//    public void watch(Collection<Object> keys) {
//        getBaseOps().watch(keys);
//    }
//
//    public void unwatch() {
//        getBaseOps().unwatch();
//    }

    // 클라이언트에 대한 정보 혹은 죽이는 기능은 좀 위험할 것 같아서 비활성화시킴
    // 이 기능이 꼭 필요하게 된다면 한번 종합적으로 검토해볼 것
//    public List<RedisClientInfo> getClientList() {
//        return getOperations().getClientList();
//    }
//
//    public void killClient(String host, int port) {
//        getOperations().killClient(host, port);
//    }
//
//    public void slaveOf(String host, int port) {
//        getOperations().slaveOf(host, port);
//    }
//
//    public void slaveOfNoOne() {
//        getOperations().slaveOfNoOne();
//    }
//
//    public void convertAndSend(String destination, Object message) {
//        getBaseOps().convertAndSend(destination, message);
//    }

    public RedisSerializer<?> getKeySerializer() {
        return getBaseOps().getKeySerializer();
    }

    public RedisSerializer<?> getValueSerializer() {
        return getBaseOps().getValueSerializer();
    }

    public RedisSerializer<?> getHashKeySerializer() {
        return getBaseOps().getHashKeySerializer();
    }

    public RedisSerializer<?> getHashValueSerializer() {
        return getBaseOps().getHashValueSerializer();
    }
}

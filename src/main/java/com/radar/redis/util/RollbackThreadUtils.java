package com.radar.redis.util;

/**
 * 웹 서비스는 쓰레드 단위로 동작한다는 사실을 이용, 쓰레드 (요청한 API 한 건) 에 대해 데이터 쓰기 관련 기능의
 * 전체 롤백 플래그를 담당하는 유틸리티.
 *
 * DB는 트랜잭션 커밋 전에 JtaTransactionManager측에서 예외를 발생시켜 트랜잭션을 롤백시킴.
 * 레디스는 Write 명령을 모두 무시처리함으로써 롤백된것처럼 보이게끔 위장함 (레디스는 별도의 트랜잭션이 없음)
 * (Write 명령을 무시하는 것이므로 캐싱 이상의 목적, 즉 set 후 get을 더 하는 로직 작성시 오작동의 위험성이 있음)
 */

public class RollbackThreadUtils {
    private static final ThreadLocal<Boolean> rollbackThread = ThreadLocal.withInitial(() -> false);

    /**
     * 메서드를 실행시킨 쓰레드의 롤백 여부를 결정함
     * 해당 기능이 활성화되면 수동으로 비활성화시킬때까지 해당 쓰레드의 트랜잭션은 무조건 롤백됨
     *
     * 따라서, 해당 쓰레드에서 예외가 발생할 수 있는 상황이라면 예외 처리 시에 setRollbackThread(false)를 호출해야만 함
     * (특히, 쓰레드 풀을 사용하는 톰캣/스프링 환경이라면 필수적으로 해 주어야 함)
     *
     * @param rollbackThread 롤백 활성화 여부
     */
    public static void set(boolean rollbackThread) {
        if (rollbackThread) {
            RollbackThreadUtils.rollbackThread.set(true);
        } else {
            RollbackThreadUtils.rollbackThread.remove();
        }
    }

    public static boolean get() {
        return RollbackThreadUtils.rollbackThread.get();
    }
}

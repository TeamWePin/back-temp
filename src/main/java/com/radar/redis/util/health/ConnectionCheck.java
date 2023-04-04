package com.radar.redis.util.health;

import com.radar.redis.util.ExceptionUtils;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static com.radar.core.util.ObjectUtils.isEmpty;
import static com.radar.redis.util.health.HealthCheck.*;


@Slf4j
abstract public class ConnectionCheck<T> {
    private static final long HEALTH_TIMEOUT = 3000; // 밀리세컨드(ms) 단위, 헬스체크 실패까지의 타임아웃 시간

    @Setter private Map<String, T> targetInfoList;

    abstract public void connTest(String key, T value) throws Exception;
    abstract public String getUrl(T value);

    /**
     * CompletableFuture를 사용하여 비동기 방식으로 여러 커넥션 체크가 동시에 진행되도록 함
     */
    public final Map<String, Map<String, Object>> doConnTest() throws Exception {
        if(!isEmpty(targetInfoList)) {
            // 결과값으로 보내줄 리스트 (여러 쓰레드에서 사용하므로 동기화가 필요함)
            Map<String, Map<String, Object>> targetResponseList = Collections.synchronizedMap(new LinkedHashMap<>());

            List<CompletableFuture<Void>> futureList = new ArrayList<>(targetInfoList.size());
            for (String targetName : targetInfoList.keySet()) {
                /* 헬스체크에서 정확한 정보를 전달해주기 위해 CompletableFuture를 두겹으로 감쌈
                 *  - CompletableFuture.allOf() 로 묶어서 실행 후 결과 도출이 되면 그 다음 코드로 넘어가는 형식
                 *  - 여기서 allOf() 기준으로 타임아웃 코드를 적용하면 하나가 타임아웃됬을 때 전체 코드를 중단시켜버리기에,
                 *    개별 CompletableFuture에 타임아웃을 걸어주기 위해 두겹으로 감쌈
                 *
                 * 1. 바깥쪽 CompletableFuture는 CompletableFuture.allOf()를 이용해 CompletableFuture 리스트들의 작업이 다 끝날때까지 블락(대기) 처리를 위함
                 * 2. 안쪽 CompletableFuture는 타임아웃을 걸면서 해당 작업이 끝나길 기다리는 실제 비동기 작업
                 *    안쪽 CompletableFuture에서 타임아웃 혹은 다른 문제 발생 시, catch 블럭을 통해 원하는 작업을 수행함 */
                futureList.add(CompletableFuture.runAsync(() -> {
                    try {
                        // doConnTest(targetName)의 결과를 targetResponseList에 담는 작업을 수행하는 CompletableFuture 생성
                        // CompletableFuture.allOf()의 timeout으로 세팅해줄 수 없는, 개별 커넥션 테스트에 대한 타임아웃 예외 처리를 할 수 있음
                        // 또한, 따로 ExecutorService를 설정해주지 않음으로써, 기본 제공되는 풀을 사용함 (헬스체크는 빈번하게 일어나진 않음)
                        CompletableFuture.runAsync(() -> {
                            Map<String, Object> targetResponse = doConnTest(targetName);

                            targetResponseList.put(targetName, targetResponse);
                        }).exceptionally(e -> {
                            // 작업에 실패하면 예외를 던짐
                            throw ExceptionUtils.wrapThrowable(e);
                        }).get(HEALTH_TIMEOUT, TimeUnit.MILLISECONDS); // 타임아웃을 걸고, 위의 CompletableFuture를 실행함 (바깥쪽 CompletableFuture에 blocking, 동기 형식으로 작업함)
                    } catch (Exception e) {
                        // 타임아웃이나 내부 로직의 문제로 예외 발생 시, HealthCheck에 예외 관련 정보를 표시하기 위해 리스폰스 데이터를 생성
                        // 예외의 스택트레이스를 담아 헬스체크 리스폰스에 보내줌
                        Map<String, Object> targetResponse = createTargetResponse(targetName, DOWN);
                        targetResponse.put("causes", getCauses(e));

                        targetResponseList.put(targetName, targetResponse);
                    }
                }));
            }

            // CompletableFuture를 모두 실행하고 모든 결과값이 나올때까지 블락(대기) 처리
            // 아래 .get(timeout seconds) 코드에 의해 너무 오랫동안 커넥션을 잡으면 해당 헬스체크는 모두 실패로 처리
            try {
                CompletableFuture.allOf(futureList.toArray(new CompletableFuture[]{}))
                        .get(HEALTH_TIMEOUT + 1000, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                // 원래 타임아웃 걸려야하는 시간보다 1초이상 느리면 CompletableFuture 내부에 뭔가 문제가 있을 것
                // 쓰레드 수가 너무 많아도 해당 사항에 걸릴테니, 너무 많은 정보가 호출된다 싶으면 확인바람

                // 예외의 스택트레이스를 담아 헬스체크 리스폰스에 보내줌
                Map<String, Object> targetResponse = new LinkedHashMap<>();
                targetResponse.put("causes", getCauses(e));

                targetResponseList.put("*SOMETHING OTHER EXCEPTION", targetResponse);
            }

            return targetResponseList;
        } else {
            return null;
        }
    }

    private Map<String, Object> doConnTest(String targetName) {
        T targetInfo = targetInfoList.get(targetName);

        Map<String, Object> targetResponse = createTargetResponse(targetName, UP);

        // 커넥션 테스트 소요 시간 측정
        long startTime = System.nanoTime();
        long performTime;
        try {
            connTest(targetName, targetInfo);
        } catch (Exception e) {
            throw ExceptionUtils.wrapThrowable(e);
        } finally {
            performTime = System.nanoTime() - startTime;
            targetResponse.put("time", nanoToSec(performTime));
        }

        return targetResponse;
    }

    private Map<String, Object> createTargetResponse(String targetName, String status) {
        T targetInfo = targetInfoList.get(targetName);

        Map<String, Object> targetResponse = new LinkedHashMap<>();
        targetResponse.put(STATUS, status);
        if (targetInfo != null) targetResponse.put("url", getUrl(targetInfo));

        return targetResponse;
    }

    private Map<String, Object> getCauses(Exception e) {
        Map<String, Object> causes = new LinkedHashMap<>();
        causes.put(e.getClass().getCanonicalName(), e.getMessage());
        for (Throwable cause : ExceptionUtils.getCauses(e)) {
            causes.put(cause.getClass().getCanonicalName(), cause.getMessage());
        }

        return causes;
    }

    private static final BigDecimal nanosec = new BigDecimal("1000000000");
    private BigDecimal nanoToSec(long performTime) {
        BigDecimal nanoToSec = new BigDecimal(String.valueOf(performTime));

        return nanoToSec.divide(nanosec, 5, RoundingMode.HALF_UP);
    }
}

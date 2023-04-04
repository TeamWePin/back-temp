package com.radar.redis.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.radar.core.exception.RadarCommonException;
import com.radar.core.util.JsonUtils;
import com.radar.redis.properties.RedisServer;
import com.radar.redis.properties.annotations.RedisCache;
import com.radar.redis.properties.operations.RedisReadOperations;
import com.radar.redis.properties.operations.RedisWriteOperations;
import com.radar.redis.properties.support.RadarRedisTemplateSupport;
import com.radar.redis.util.ExceptionUtils;
import com.radar.redis.util.RollbackThreadUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisOperations;

import java.lang.reflect.Type;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 기본적으로 사용될 만한 레디스 작업들에 대해 메서드 형태로 단순하고 빠르게 사용할 수 있도록 메서드화시킨 RAO.
 * 또한 Read 및 Write 커맨드에 대해 분리해서 작업되도록 하여 AOP 및 여러가지 기능(DryRun 등)에 대한 적용을 수월하게 함.
 *
 * @since 2021-10-22
 * @author donghans
 */
@Slf4j
@RedisCache
public class BasicRAO extends RadarRedisTemplateSupport {
    public interface RedisTaskInterface {} // RedisReadTask 및 RedisWriteTask 를 묶어주기 위한 용도, 내부적으로 doExecute()의 파라미터로 태우기 위함
    public interface RedisReadTaskInterface<O, R> extends RedisTaskInterface { R read(O ops) throws Exception; }
    public interface RedisWriteTaskInterface<O> extends RedisTaskInterface { void write(O ops) throws Exception; }

    public interface RedisReadTask<R> extends RedisReadTaskInterface<RedisReadOperations, R> { R read(RedisReadOperations ops) throws Exception; }

    /**
     * 레디스 읽기 명령어를 실행 후 결과값을 받아옴
     *
     * @param server 해당 레디스 태스크를 보낼 서버
     * @param task 람다식 이용 가능
     * @return query 후 결과로 나온 값
     */
    public <R> R read(RedisServer server, RedisReadTask<R> task) {
        return execute(server, task);
    }

    public <T> T read(RedisServer server, RedisReadTask<String> task, Class<T> cls) {
        String json = execute(server, task);

        return JsonUtils.toObject(json, cls);
    }

    public <T> T read(RedisServer server, RedisReadTask<String> task, TypeReference<T> typeRef) {
        String json = execute(server, task);

        return JsonUtils.toObject(json, typeRef);
    }

    public <T> T read(RedisServer server, RedisReadTask<String> task, Type type) {
        String json = execute(server, task);

        return JsonUtils.toType(json, type);
    }

    public interface RedisWriteTask extends RedisWriteTaskInterface<RedisWriteOperations> { void write(RedisWriteOperations ops) throws Exception; }

    /**
     * 레디스 쓰기 명령어를 실행
     *
     * @param server 해당 레디스 태스크를 보낼 서버
     * @param task 람다식 이용 가능
     */
    public void write(RedisServer server, RedisWriteTask task) {
        consume(server, task);
    }

    public interface RedisRawReadTask<R> extends RedisReadTaskInterface<RedisOperations<String, String>, R> { R read(RedisOperations<String, String> ops) throws Exception; }

    /**
     * 레디스 Raw Operations을 실행 후 결과값을 받아옴
     *
     * @param server 해당 레디스 태스크를 보낼 서버
     * @param task 람다식 이용 가능
     */
    public <R> R rawRead(RedisServer server, RedisRawReadTask<R> task) {
        return execute(server, task);
    }

    public <T> T rawRead(RedisServer server, RedisRawReadTask<String> task, Class<T> cls) {
        String json = execute(server, task);

        return JsonUtils.toObject(json, cls);
    }

    public <T> T rawRead(RedisServer server, RedisRawReadTask<String> task, TypeReference<T> typeRef) {
        String json = execute(server, task);

        return JsonUtils.toObject(json, typeRef);
    }

    public <T> T rawRead(RedisServer server, RedisRawReadTask<String> task, Type type) {
        String json = execute(server, task);

        return JsonUtils.toType(json, type);
    }

    public interface RedisRawWriteTask extends RedisWriteTaskInterface<RedisOperations<String, String>> { void write(RedisOperations<String, String> ops) throws Exception; }

    /**
     * 레디스 Raw Operations을 실행
     *
     * @param server 해당 레디스 태스크를 보낼 서버
     * @param task 람다식 이용 가능
     */
    public void rawWrite(RedisServer server, RedisRawWriteTask task) {
        consume(server, task);
    }

    /**
     * 실제 레디스 로직이 실행되는 부분.
     * RedisExecutable을 받아와 Query 혹은 Task에 따라 리턴값을 보내줄 지 보내주지 않을 지 결정함.
     * Task 혹은 Query 실행 중 로직상의 에러가 발생하거나 레디스 연결이 끊김 등의 에러 발생 시 캐치 후 예외 re-throw.
     *
     * @param server 해당 작업을 날릴 레디스 서버
     * @param task 람다식 혹은 구현된 클래스로 이루어진 작업 리스트
     * @param <R> 결과로 갖게되는 타입, 강제 형변환을 실행하며 레디스에 들어간 타입과 맞지 않을 경우 ClassCastException 발생
     * @return Query의 경우 결과값, Task일 경우 null
     * @throws RadarCommonException Task 혹은 Query의 실행 도중 로직상 에러가 있을 경우 예외 발생
     */
    private <O, R> R execute(RedisServer server, RedisReadTaskInterface<O, R> task) {
        if (task == null) throw new RadarCommonException("RedisTask must not be null!");

        AtomicReference<Exception> exception = new AtomicReference<>();

        R result = super.execute(server, operations -> {
            try {
                if (task instanceof RedisReadTask) {
                    @SuppressWarnings("unchecked") RedisReadTask<R> readTask = (RedisReadTask<R>) task;

                    return readTask.read(new RedisReadOperations(operations));
                } else if (task instanceof RedisRawReadTask) {
                    @SuppressWarnings("unchecked") RedisRawReadTask<R> readTask = (RedisRawReadTask<R>) task;

                    return readTask.read(operations);
                }
            } catch (Exception e) {
                exception.set(e);

                if (task instanceof RedisWriteTask) {
                    // FIXME 클러스터링 시 MULTI/EXEC/DISCARD 명령어가 먹히지 않으므로 임시로 try-catch 및 ignore 처리, 다른 문제가 생겼을 때에 대한 대처가 필요하므로 더 좋은 방안을 강구해야함
                    try { operations.discard(); } catch (Exception ignore) {}
                }
            }

            return null;
        });

        if (exception.get() != null) throw ExceptionUtils.wrapThrowable(exception.get());
        return result;
    }

    private void consume(RedisServer server, RedisWriteTaskInterface<?> task) {
        if (task == null) throw new RadarCommonException("RedisTask must not be null!");

        AtomicReference<Exception> exception = new AtomicReference<>();

        super.consume(server, operations -> {
            try {
                if (task instanceof RedisWriteTask) {
                    // FIXME 클러스터링 시 MULTI/EXEC/DISCARD 명령어가 먹히지 않으므로 임시로 try-catch 및 ignore 처리, 다른 문제가 생겼을 때에 대한 대처가 필요하므로 더 좋은 방안을 강구해야함
                    try { operations.multi(); } catch (Exception ignore) {}
                    ((RedisWriteTask) task).write(new RedisWriteOperations(operations));

                    // DryRun 기능 활성화시 레디스 쓰기 명령어를 모두 무시함
                    // 어차피 DryRun 자체가 테스트 기능이며 캐싱 이상의 복잡한 레디스 로직을 쓰지 않는 이상
                    // 쓰기 기능에 대해선 로직적 에러가 없다는 가정하에 비활성화하나 활성화하나 같음
                    if (RollbackThreadUtils.get()) {
                        // FIXME 클러스터링 시 MULTI/EXEC/DISCARD 명령어가 먹히지 않으므로 임시로 try-catch 및 ignore 처리, 다른 문제가 생겼을 때에 대한 대처가 필요하므로 더 좋은 방안을 강구해야함
                        try { operations.discard(); } catch (Exception ignore) {}
                    } else {
                        // FIXME 클러스터링 시 MULTI/EXEC/DISCARD 명령어가 먹히지 않으므로 임시로 try-catch 및 ignore 처리, 다른 문제가 생겼을 때에 대한 대처가 필요하므로 더 좋은 방안을 강구해야함
                        try { operations.exec(); } catch (Exception ignore) {}
                    }
                } else if (task instanceof RedisRawWriteTask) {
                    ((RedisRawWriteTask) task).write(operations);
                }
            } catch (Exception e) {
                exception.set(e);

                if (task instanceof RedisWriteTask) {
                    // FIXME 클러스터링 시 MULTI/EXEC/DISCARD 명령어가 먹히지 않으므로 임시로 try-catch 및 ignore 처리, 다른 문제가 생겼을 때에 대한 대처가 필요하므로 더 좋은 방안을 강구해야함
                    try { operations.discard(); } catch (Exception ignore) {}
                }
            }
        });

        if (exception.get() != null) throw ExceptionUtils.wrapThrowable(exception.get());
    }
}

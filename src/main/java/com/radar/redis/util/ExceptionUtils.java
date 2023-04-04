package com.radar.redis.util;


import com.radar.core.exception.RadarCommonException;

import java.util.ArrayList;
import java.util.List;

public class ExceptionUtils {
    /**
     *  예외가 생긴 원인(Cause)를 모두 추적해서 보여줌
     * "모든" 원인(Cause)을 추적하기 위해 while문을 사용했으며
     * Cause가 없는 경우 로깅하지 않기 위해 if (cause != null)을 do {} while(); 문 앞에 집어넣음
     */
    public static List<Throwable> getCauses(Exception e) {
        List<Throwable> result = new ArrayList<>();

        Throwable cause = e.getCause();
        if (cause != null) do {
            result.add(cause);
        } while ((cause = cause.getCause()) != null);

        return result;
    }

    /**
     * 예외를 HanteoCommonException으로 변환해줌
     */
    public static RadarCommonException wrapThrowable(Throwable t) {
        if (t instanceof RadarCommonException) {
            return (RadarCommonException) t;
        } else if (t instanceof Exception) {
            return new RadarCommonException(t);
        } else {
            return new RadarCommonException(new Exception(t));
        }
    }
}

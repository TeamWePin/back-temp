package com.radar.core.util;

import com.radar.core.exception.RadarCommonException;
import com.radar.core.exception.RadarServiceStatusCode;
import com.radar.core.model.common.ResultInfo;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.lang.reflect.Type;
import java.util.Map;

import static com.radar.core.exception.RadarErrorType.*;
import static com.radar.core.exception.RadarServiceStatusCode.*;


@Component
public class ResultInfoUtil {
    public static final String ADDITIONAL_RESULT_DATA = "AdditionalResultData";

    public static void setAdditionalResultData(Object resultData) {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();

        attributes.setAttribute(ADDITIONAL_RESULT_DATA, resultData, RequestAttributes.SCOPE_REQUEST);
    }

    public static Object getAdditionalResultData() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();

        return attributes.getAttribute(ADDITIONAL_RESULT_DATA, RequestAttributes.SCOPE_REQUEST);
    }

    /**
     *  RestApi Return시 사용
     *  Result
     *  Code : 반드시 성공/실패 포함
     *  Message : 검색한 키워드(idx) or null or input messageVal
     *  ResultMap : result 그대로 전달.
     */


    public static ResultInfo setResultInfo(Object result) {
        return setResultInfoForMap(SUCCESS, result, null);
    }


    public static ResultInfo setResultInfo(RadarServiceStatusCode code, Object result) {
        return setResultInfoForMap(code, result, null);
    }

    public static ResultInfo setResultInfo(RadarServiceStatusCode code, Object result, int keyword) {
        return setResultInfoForMap(code, result, String.valueOf(keyword));
    }

    public static ResultInfo setResultInfo(RadarServiceStatusCode code, Object result, String msg) {
        return setResultInfoForMap(code, result, msg);
    }

    public static ResultInfo setResultInfo(RadarServiceStatusCode code, Map<String, Object> resultData) {
        return setResultInfoForMap(code, resultData, null);
    }

    public static ResultInfo setResultInfo(RadarServiceStatusCode code, Map<String, Object> resultData, String msg) {
        return setResultInfoForMap(code, resultData, msg);
    }
    public static ResultInfo setResultInfo(RadarCommonException e) {
        return setResultInfoForMap(e);
    }
    public static ResultInfo setResultInfo(RadarCommonException e, Object result) {
        return setResultInfoForMap(e, result);
    }

    private static ResultInfo setResultInfoForMap(RadarServiceStatusCode code, Object resultData, String msg) {
        ResultInfo resultInfo = new ResultInfo();
        resultInfo.setCode(code.getError());
        resultInfo.setResultData(resultData);
        resultInfo.setMessage(msg);
        return resultInfo;
    }

    private static ResultInfo setResultInfoForMap(RadarCommonException e) {
        return setResultInfoForMap(e, null);
    }
    private static ResultInfo setResultInfoForMap(RadarCommonException e, Object result) {
        ResultInfo resultInfo = new ResultInfo();
        resultInfo.setCode(e.getReason().getError());
        resultInfo.setResultData(result);
        resultInfo.setMessage(e.getMessage());
        return resultInfo;
    }


    /**
     * getResultFromJson(String json, Type resultDataType) 사용을 권장함
     *
     * 타입 변환을 Object로 할 경우, 일반적인 상황에서 재변환이 필요함 (resultData가 LinkedHashMap으로 변환됨)
     */
    @Deprecated
    public static ResultInfo getResultFromJson(String json) {
        return getResultFromJson(json, Object.class);
    }

    /**
     *  Controller Return시 사용
     *  API 서버와의 통신을 통한 결과는 Json String으로 전달된다.
     *  전달값을 ResultInfo 형태로 형변환 시켜서 리턴한다.
     *
     * @return ResultInfo
     */
    public static ResultInfo getResultFromJson(String json, Type resultDataType) {
        ResultInfo resultInfo = new ResultInfo();
        if(!StringUtils.isBlank(json)) {
            Integer code = JsonUtils.toDataObject(json, "code", Integer.class);
            if (code == null) {
                throw new RadarCommonException(ERROR_SYSTEM, ERROR_NULL, "'code' property is null!");
            }

            String message = JsonUtils.toDataObject(json, "message", String.class);

            Object resultData;
            if (resultDataType instanceof Class<?> && ((Class<?>) resultDataType).isPrimitive()) {
                resultData = JsonUtils.toDataObject(json, "resultData", ClassUtils.primitiveToWrapper((Class<?>) resultDataType));
            } else {
                resultData = JsonUtils.toDataObject(json, "resultData", resultDataType);
            }

            resultInfo.setCode(code);
            resultInfo.setMessage(message);
            resultInfo.setResultData(resultData);
        } else  {
            return setResultInfo(new RadarCommonException(ERROR_SYSTEM, ERROR_SYSTEM_EXCEPTION, "API Server communication FAIL !"));
        }
        return resultInfo;
    }

}

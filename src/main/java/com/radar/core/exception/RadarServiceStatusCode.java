package com.radar.core.exception;

public enum RadarServiceStatusCode {

    // 기본적인 상태 코드
    SUCCESS(100, "[SUCCESS]"),
    FAILED(101, "[FAILED]"),

    // DB관련 에러를 구분하기 위해 사용되는 상태 코드들
    ERROR_QUERY(200, "[ERROR_QUERY]"),
    ERROR_INSERT(201, "[ERROR_INSERT]"),
    ERROR_READ(202, "[ERROR_READ]"),
    ERROR_UPDATE(203, "[ERROR_UPDATE]"),
    ERROR_DELETE(204, "[ERROR_DELETE]"),

    /* 유효성 확인 에러 */
    ERROR_NULL(601, "[ERROR_NULL]"),
    ERROR_NO_PARAM(602, "[ERROR_NO_PARAM]"),
    ERROR_NOT_SUPPORT_TYPE(603, "[ERROR_NOT_SUPPORT_TYPE]"),
    ERROR_PARAM_VALIDITY(604, "[ERROR_PARAM_VALIDITY]"),
    ERROR_PROCESS_FAILED(605, "[ERROR_PROCESS_FAILED]"),
    ERROR_DATA_DUPLICATE(606, "[ERROR_DATA_DUPLICATE]"),

    // 네트워크(통신) 관련 에러
    ERROR_NETWORK(701, "[ERROR_NETWORK]"),
    ERROR_INTERNAL_SERVER(702, "[ERROR_INTERNAL_SERVER]"),
    ERROR_INTERNAL_SERVER_DEAD(703, "[ERROR_INTERNAL_SERVER_DEAD]"),


    // 시스템 에러를 나타내는 상태 코드
    ERROR_SYSTEM_EXCEPTION(1000, "[ERROR_SYSTEM_EXCEPTION]")
    ;



    private int error;
    private String reason;

    RadarServiceStatusCode(int error, String reason) {
        this.error = error;
        this.reason = reason;
    }

    public int getError() {
        return error;
    }

    public String getReason() {
        return reason;
    }

}

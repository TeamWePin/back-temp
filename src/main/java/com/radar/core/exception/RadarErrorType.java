package com.radar.core.exception;

public enum RadarErrorType {

    ERROR(0, "HT_ER_"),
    ERROR_SQL(1, "HT_SQ_"),
    ERROR_TEST_DATA(2, "HT_TD_"),
    ERROR_USER_DATA(3, "HT_US_"),
    ERROR_SYSTEM(10, "HT_SY_");

    private int type;
    private String name;

    RadarErrorType(int type, String name) {
        this.type = type;
        this.name = name;
    }

    public int getType() {
        return this.type;
    }

    public String getName() {
        return this.name;
    }

}

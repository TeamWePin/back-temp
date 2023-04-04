package com.radar.core.model.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 2021-02-02 YOONDDANG
 * Test 용도로 생성, 향후 테스트 또는 별도의 전략적 사용이 없으면 삭제 요망
 */
@Getter
@RequiredArgsConstructor
public class Ping {

    private final String name;
    private final int amount;
}

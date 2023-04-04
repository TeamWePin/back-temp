package com.radar.core.model.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.IOException;
import java.io.Serializable;

import static com.radar.core.exception.RadarServiceStatusCode.*;

@Getter
@Setter
@ToString(exclude = {"messageKey"}) // 레거시 API RestStub 호출시 리턴값으로 받는 messageKey를 ToString에 표시하지 않음
public class ResultInfo implements Serializable {

	private static final long serialVersionUID = 7021191539395886945L;

	// HanteoServiceStatusCode 코드를 사용, Jackson 애노테이션 중 ENUM->INT 변환 관련 코드가 있는 경우 수정바람
	@JsonDeserialize(using = ResultInfoDeserializer.class)
	private int code;

	// 에러/실패시 표시되는 상세 메세지, HanteoCommonException 포맷에 맞춰 시간/에러코드/상세내용이 표시됨
	private String message;

	@JsonIgnore // 레거시 API RestStub 호출시 리턴값으로 받기 위해 사용(실제 L1단 리스폰스에선 보이지 않게끔 JsonIgnore 처리)
	private String messageKey;

	// 성공시 표시되는 실제 리스폰스
	private Object resultData;

	public static class ResultInfoDeserializer extends JsonDeserializer<Integer> {
		@Override
		public Integer deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {
			String raw = parser.getValueAsString();
			if (raw == null) return FAILED.getError();
			if (raw.matches("^\\d+$")) return Integer.parseInt(raw);

			if ("SUCCESS".equals(raw)) {
				return SUCCESS.getError();
			} else {
				return FAILED.getError();
			}
		}
	}
}
package com.radar.redis.util.health;

import com.radar.core.exception.RadarCommonException;
import com.radar.core.properties.ProjectData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import static com.radar.core.exception.RadarErrorType.ERROR_SYSTEM;
import static com.radar.core.exception.RadarServiceStatusCode.ERROR_NETWORK;
import static org.apache.commons.lang3.StringUtils.startsWithIgnoreCase;

@Slf4j
public class RestStubConnectionCheck extends ConnectionCheck<String> {
    private final RestTemplate restTemplate;
    private final ProjectData projectData;
    public RestStubConnectionCheck(RestTemplate restTemplate, ProjectData projectData) {
        this.restTemplate = restTemplate;
        this.projectData = projectData;

        setTargetInfoList(projectData.getServers().getUrl());
    }

    @Override
    public void connTest(String key, String url) throws Exception {
        // 프로세스 서버간의 재귀호출을 막기 위해, 프로세스 서버에선 단순 헬스체크만 실시함 (루트패스 헬스체크)
        ResponseEntity<String> healthResponse;
        if (startsWithIgnoreCase(projectData.getName(), "hanteo-front-")) {
            healthResponse = restTemplate.getForEntity(getUrl(url), String.class);
        } else {
            healthResponse = restTemplate.getForEntity(getUrlWithoutHealth(url), String.class);
        }

        if (!healthResponse.getStatusCode().equals(HttpStatus.OK)) {
            throw new RadarCommonException(ERROR_SYSTEM, ERROR_NETWORK, "Server '" + key + "' sent status code " + healthResponse.getStatusCode() + "!");
        }
    }

    @Override
    public String getUrl(String url) {
        return UriComponentsBuilder.fromHttpUrl(url).path("/health").build().toUriString();
    }

    private String getUrlWithoutHealth(String url) {
        return UriComponentsBuilder.fromHttpUrl(url).build().toUriString();
    }
}

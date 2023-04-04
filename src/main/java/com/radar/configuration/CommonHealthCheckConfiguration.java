package com.radar.configuration;

import com.radar.core.properties.ProjectData;
import com.radar.redis.util.health.BasicInformationEndpoint;
import com.radar.redis.util.health.DevelopEnvSwitcher;
import com.radar.redis.util.health.HealthCheck;
import com.radar.redis.util.health.RestStubConnectionCheck;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.lang.Nullable;
import org.springframework.web.client.RestTemplate;

import static org.springframework.util.ObjectUtils.nullSafeEquals;

/**
 * 기본적인 헬스체크를 위한 Configuration
 *
 * project-data.yml 파일에 쓰여진 로드 밸런서들의 정상 접속 여부 등을 확인하는 헬스체크를 수행함
 * 해당 yml 파일에 servers 속성으로 URL 들을 기입한 경우, 헬스체크 페이지에 접속 시 해당 링크 체크를 같이 출력함
 * servers 속성이 없을 경우 로드밸런서 체크는 건너뜀
 *
 * @see HealthCheck
 */

@Slf4j
@Configuration
@ComponentScan( // ProjectData 빈이 필요하므로, 해당 빈을 생성하는 BasicConfiguration 클래스를 불러오기 위해 ComponentScan
        basePackages = {
                "com.radar"
        }
)
public class CommonHealthCheckConfiguration implements InitializingBean {
    @Value("${spring.profiles.active:local}")
    private String profile;

    @Override
    public void afterPropertiesSet() throws Exception {
        if (nullSafeEquals(profile, "real")) DevelopEnvSwitcher.setDevelopEnv(false);
    }



    @Autowired private ProjectData projectData;

    @Bean(name = "basicInformationEndpoint")
    public BasicInformationEndpoint basicInformationEndpoint() {
        return new BasicInformationEndpoint(projectData);
    }

    @Bean(name = "*BASIC INFORMATION")
    @DependsOn("basicInformationEndpoint")
    public HealthIndicator basicInformationComponent(BasicInformationEndpoint basicInformationEndpoint) {
        return () -> Health.up()
                    .withDetails(basicInformationEndpoint.basicInformation())
                    .build();
    }

    @Bean(name = "RESTSTUB CONNECTION")
    public HealthCheck restStubConnectionCheck(@Nullable RestTemplate restTemplate) {
        if (restTemplate == null ) return null;

        if (projectData.isServersInitialized() && projectData.getServers() != null) {
            return new HealthCheck(new RestStubConnectionCheck(restTemplate, projectData));
        } else {
            return null;
        }
    }
}

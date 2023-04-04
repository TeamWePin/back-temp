package com.radar.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * 백단 로직 서버의 경우 내부에서만 접속 가능하도록 애초부터 조치가 되어있으므로, Security를 비활성화함
 *
 * 만약 검사를 원한다면 @EnableAccessCheck 로 액세스체크를 활성화하고,
 * hanteo-process-rest 쪽 okhttp 통신에 Authorization 헤더를 무조건 붙이는 형태로 작성하면 될 듯함
 */

@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().anyRequest();
    }
}

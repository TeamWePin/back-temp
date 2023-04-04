package com.radar.redis.util.health;

import com.radar.core.properties.ProjectData;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * 정말로 살았는지 죽었는지 여부만 표시해주는 헬스체크용 url
 *
 * DB 커넥션 및 REST 통신 등의 네트워크 부하가 있는 작업을 매 인스턴스마다
 * 그리고 매초마다 ELB 혹은 L1단에서의 호출로 인해 자원의 사용량이 문제가 되므로
 * 진짜 살아있는지 여부만 체크할 수 있도록 하는 엔드포인트 (/, 루트 패스)를 지정함
 */
@Slf4j
@AllArgsConstructor
@RestController
public class BasicInformationEndpoint {
    private ProjectData projectData;

    @RequestMapping(method = GET, value = "")
    public Map<String, Object> basicInformation() {
        String address = null; try {
            address = InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) { logger.warn("Failed to grep localhost address", e); }

        Map<String, Object> info = new LinkedHashMap<>();
        info.put("name", projectData.getName());
        info.put("address", address);
        info.put("uptime", ManagementFactory.getRuntimeMXBean().getUptime()/1000);

        return info;
    }
}

package com.radar.configuration;

import com.radar.core.exception.RadarCommonException;
import com.radar.core.properties.ProjectData;
import com.radar.core.util.HtDateTime;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.format.Formatter;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.Locale;

import static com.radar.core.util.HtDateTimeUtil.getDateTimeFromUTC;


/**
 * 서비스의 기본 설정값 초기화
 *
 */
@Configuration
@ComponentScan(
        basePackages = {
                "com.radar.service"
        }
)
public class BasicConfiguration {

    @Bean
    public ProjectData projectData() throws Exception {
        Yaml yaml = new Yaml();
        try (InputStream in = (new ClassPathResource("project-data.yml")).getInputStream()) {
            ProjectData projectData = yaml.loadAs(in, ProjectData.class);
            return projectData;
        }
    }


    @Bean
    public Formatter<HtDateTime> htDateTimeFormatter() {
        return new Formatter<HtDateTime>() {
            @Override
            public HtDateTime parse(String text, Locale locale) throws ParseException {
                try {
                    return getDateTimeFromUTC(text);
                } catch (RadarCommonException e) {
                    throw new ParseException(MessageFormat.format("Wrong DataTime text, please check parameter (input text: {0})", text), 0);
                }
            }

            @Override
            public String print(HtDateTime object, Locale locale) {
                return null;
            }
        };
    }


}

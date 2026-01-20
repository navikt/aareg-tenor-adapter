package no.nav.aareg.tenor.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.json.JsonMapper;

@Configuration
public class ApplicationConfig {

    @Bean
    ObjectMapper objectMapper() {
        return JsonMapper.builder()
                .enable(DateTimeFeature.WRITE_DATES_WITH_ZONE_ID)
                .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .build();
    }
}

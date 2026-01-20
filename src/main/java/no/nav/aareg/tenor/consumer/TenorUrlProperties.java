package no.nav.aareg.tenor.consumer;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@ConfigurationProperties(prefix = "api.url.tenor")
public class TenorUrlProperties {
    private final String freg;
    private final String brreg;
    private final String datasett;
}

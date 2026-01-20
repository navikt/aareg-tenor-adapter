package no.nav.aareg.tenor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableCaching
public class AaregTenorAdapter {

    static void main(String[] args) {
        SpringApplication.run(AaregTenorAdapter.class, args);
    }
}

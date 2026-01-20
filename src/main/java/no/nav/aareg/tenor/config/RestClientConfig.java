package no.nav.aareg.tenor.config;

import lombok.extern.slf4j.Slf4j;
import no.nav.aareg.tenor.exception.ApplicationException;
import org.apache.hc.client5.http.impl.DefaultHttpRequestRetryStrategy;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Slf4j
@Configuration
public class RestClientConfig {

    @Bean
    RestClient maskinportenRestClient() {
        return restClientBuilder("maskinporten")
                .build();
    }

    @Bean
    RestClient aaregServicesRestClient(@Value("${app.url.aareg.services}") String baseUrl) {
        return restClientBuilder("aareg-services")
                .baseUrl(baseUrl)
                .build();
    }

    @Bean
    RestClient tenorRestClient() {
        return restClientBuilder("tenor")
                .build();
    }

    @Bean
    RestClient texasRestClient() {
        return restClientBuilder("texas").build();
    }

    private RestClient.Builder restClientBuilder(String tjeneste) {
        return RestClient.builder()
                .requestFactory(clientHttpRequestFactory())
                .defaultStatusHandler(HttpStatusCode::is4xxClientError, (clientRequest, clientResponse) -> {
                    var melding = String.format("Tjeneste [%s] returnerte client error med status %s og melding %s", tjeneste, clientResponse.getStatusCode(), new String(clientResponse.getBody().readAllBytes()));
                    log.error(melding);
                    throw new ApplicationException(melding);
                })
                .defaultStatusHandler(HttpStatusCode::is5xxServerError, (clientRequest, clientResponse) -> {
                    var melding = String.format("Tjeneste [%s] returnerte server error med status %s og melding %s", tjeneste, clientResponse.getStatusCode(), new String(clientResponse.getBody().readAllBytes()));
                    log.warn(melding);
                    throw new ApplicationException(melding);
                });
    }

    @Bean
    public HttpComponentsClientHttpRequestFactory clientHttpRequestFactory() {
        var client = HttpClients.custom()
                .useSystemProperties()
                .setRetryStrategy(new DefaultHttpRequestRetryStrategy())
                .build();
        return new HttpComponentsClientHttpRequestFactory(client);
    }
}

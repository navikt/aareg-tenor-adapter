package no.nav.aareg.tenor.consumer.texas;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.aareg.tenor.consumer.texas.dto.TexasResponse;
import no.nav.aareg.tenor.consumer.texas.dto.TokenRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Slf4j
@Component
@RequiredArgsConstructor
public class TexasConsumer {

    public static final String IDP_AZURE_AD = "azuread";
    public static final String IDP_MASKINPORTEN = "maskinporten";

    private static final String ERROR = "Feil ved henting av token fra texas";

    private final RestClient texasRestClient;

    @Value("${nais.token.endpoint}")
    private String tokenEndpoint;

    public String hentToken(String idp, String target) {
        var tokenRequest = TokenRequest.builder()
                .identityProvider(idp)
                .target(target)
                .build();

        var response = texasRestClient.post()
                .uri(tokenEndpoint)
                .headers(httpHeaders -> httpHeaders.setContentType(APPLICATION_JSON))
                .body(tokenRequest)
                .retrieve()
                .body(TexasResponse.class);

        if (response == null || response.getAccessToken() == null) {
            log.error(ERROR);
            throw new RuntimeException(ERROR);
        }

        return response.getAccessToken();
    }
}

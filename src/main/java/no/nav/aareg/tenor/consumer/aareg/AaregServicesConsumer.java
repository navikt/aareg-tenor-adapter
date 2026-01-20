package no.nav.aareg.tenor.consumer.aareg;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.aareg.tenor.consumer.aareg.domain.v2.Arbeidsforhold;
import no.nav.aareg.tenor.consumer.texas.TexasConsumer;
import no.nav.aareg.tenor.exception.ApplicationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.Map;

import static java.lang.Boolean.TRUE;
import static java.util.UUID.randomUUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class AaregServicesConsumer {

    public static final String URL_PARAM_HISTORIKK = "historikk";
    public static final String URL_PARAM_TEKNISK_HISTORIKK = "tekniskHistorikk";
    public static final String URL_PARAM_SPORINGSINFORMASJON = "sporingsinformasjon";
    public static final String URL_PARAM_NAV_ARBEIDSFORHOLD_ID = "navArbeidsforholdId";

    public static final String URL_ARBEIDSFORHOLD = "/v2/arbeidsforhold/{navArbeidsforholdId}" +
            "?" + URL_PARAM_HISTORIKK + "={" + URL_PARAM_HISTORIKK + "}" +
            "&" + URL_PARAM_TEKNISK_HISTORIKK + "={" + URL_PARAM_TEKNISK_HISTORIKK + "}" +
            "&" + URL_PARAM_SPORINGSINFORMASJON + "={" + URL_PARAM_SPORINGSINFORMASJON + "}";

    private final RestClient aaregServicesRestClient;

    private final TexasConsumer texasConsumer;

    @Value("${app.name}")
    private String appName;

    @Value("${app.scope.aareg.services}")
    private String aaregServicesScope;

    public Arbeidsforhold finnArbeidsforhold(Long navArbeidsforholdId) {
        var queryParams = Map.of(
                URL_PARAM_NAV_ARBEIDSFORHOLD_ID, navArbeidsforholdId.toString(),
                URL_PARAM_HISTORIKK, TRUE,
                URL_PARAM_TEKNISK_HISTORIKK, TRUE,
                URL_PARAM_SPORINGSINFORMASJON, TRUE
        );

        try {
            return aaregServicesRestClient.get()
                    .uri(URL_ARBEIDSFORHOLD, queryParams)
                    .headers(httpHeaders -> {
                        httpHeaders.set("Nav-Call-Id", randomUUID().toString());
                        httpHeaders.set("Nav-Consumer-Id", appName);
                        httpHeaders.setBearerAuth(texasConsumer.hentToken(TexasConsumer.IDP_AZURE_AD, aaregServicesScope));
                    })
                    .retrieve()
                    .onStatus(status -> status.value() == 404, (_, _) -> {
                       throw new ApplicationException("Fant ikke arbeidsforhold med id " + navArbeidsforholdId);
                    })
                    .body(Arbeidsforhold.class);
        } catch (ApplicationException e) {
            return null;
        }
    }
}

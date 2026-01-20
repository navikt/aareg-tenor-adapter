package no.nav.aareg.tenor.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.aareg.tenor.consumer.aareg.domain.v2.Arbeidsforhold;
import no.nav.aareg.tenor.consumer.texas.TexasConsumer;
import no.nav.aareg.tenor.exception.ApplicationException;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.lang.String.format;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Slf4j
@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(TenorUrlProperties.class)
public class TenorConsumer {

    private static final String SLETT = "SLETT";

    private final RestClient tenorRestClient;
    private final TenorUrlProperties tenorUrlProperties;
    private final TenorUtil tenorUtil;
    private final TexasConsumer texasConsumer;
    private final JsonMapper jsonMapper = new JsonMapper();
    private final HttpClient httpClient = HttpClient.newBuilder().build();

    @Cacheable("tenorHarPerson")
    public boolean harPerson(String personnummer) {
        return fantTreff(tenorUrlProperties.getFreg(), personnummer);
    }

    @Cacheable("tenorHarVirksomhet")
    public boolean harVirksomhet(String organisasjonsnummer) {
        return fantTreff(tenorUrlProperties.getBrreg(), organisasjonsnummer);
    }

    public void lastOppArbeidsforhold(Map<Arbeidsforhold, no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.Arbeidsforhold> arbeidsforholdMap) {
        if (arbeidsforholdMap.isEmpty()) {
            return;
        }
        request(arbeidsforholdMap.entrySet().stream()
                .map(tenorUtil::lagOppdatering)
                .filter(Objects::nonNull)
                .toList());
    }

    public void slettArbeidsforhold(List<Arbeidsforhold> arbeidsforhold) {
        var sletteOperasjon = lagSletteOperasjon(arbeidsforhold);
        request(sletteOperasjon);
    }

    private List<Map<String, Object>> lagSletteOperasjon(List<Arbeidsforhold> arbeidsforhold) {
        return arbeidsforhold.stream().map(
                arfo -> (Map<String, Object>) new HashMap<String, Object>(Map.of(
                        "id", arfo.getNavUuid(),
                        // Denne 'hacken' sørger for at vi kan slette et aktivt arbeidsforhold ved å
                        // late som at vi laster opp en ny versjon av arbeidsforholdet.
                        // Tilsynelatende skaper ikke dette problemer, da det er fullt mulig å laste opp
                        // arbeidsforhold med samme id og versjon etter sletting.
                        "versjon", arfo.getNavVersjon() + 1,
                        "type", SLETT))
        ).toList();
    }

    public void request(List<Map<String, Object>> operasjoner) {
        if (operasjoner.isEmpty()) {
            log.debug("Lastet ikke opp, da det ikke var noen operasjoner å utføre: [{}]", operasjoner);
            return;
        }
        try {
            var scopes = "skatteetaten:testnorge/testdata.write skatteetaten:testnorge/testdata.read";
            var request = HttpRequest.newBuilder().
                    uri(URI.create(format("%s/latest/testdata", tenorUrlProperties.getDatasett()))).
                    method("PATCH",
                            HttpRequest.BodyPublishers.ofString(
                                    jsonMapper.writeValueAsString(
                                            Map.of("operasjoner", operasjoner)
                                    )
                            )).
                    header("Content-Type", "application/json").
                    header("Accept", "application/json").
                    header("Authorization", format("Bearer %s", texasConsumer.hentToken(TexasConsumer.IDP_MASKINPORTEN, scopes))).
                    build();
            var resultat = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            var operasjonsResultat = jsonMapper.readTree(resultat.body()).path("operasjoner").properties().stream()
                    .map(x -> new OperasjonsResultat(x.getKey(), x.getValue().get("status").asString()))
                    .toList();
            log.info("Resultat: {} {}", resultat.statusCode(), operasjonsResultat);

            var operasjonerIKonflikt = operasjonsResultat.stream()
                    .filter(OperasjonsResultat::feilet)
                    .toList();
            if (!operasjonerIKonflikt.isEmpty()) {
                log.error("Noen operasjoner feilet: {}", operasjonerIKonflikt);
            }
        } catch (JacksonException e) {
            throw new ApplicationException("Feil ved generering av JSON-payload ved opplasting av data i Tenor", e);
        } catch (IOException e) {
            throw new ApplicationException("Uopprettelig feil ved opplasting av data til Tenor", e);
        } catch (InterruptedException e) {
            throw new ApplicationException("Avbrudd ved opplasting av data til Tenor", e);
        }
    }

    private boolean fantTreff(String url, String identifikator) {
        var scopes = "skatteetaten:testnorge/testdata.write skatteetaten:testnorge/testdata.read";
        var response = tenorRestClient.get()
                .uri(format("%s%s", url, identifikator))
                .headers(httpHeaders -> {
                    httpHeaders.setBearerAuth(texasConsumer.hentToken(TexasConsumer.IDP_MASKINPORTEN, scopes));
                    httpHeaders.setContentType(APPLICATION_JSON);
                    httpHeaders.setAccept(List.of(APPLICATION_JSON));
                })
                .retrieve()
                .body(String.class);
        try {
            return jsonMapper.readTree(response).get("treff").asInt() > 0;
        } catch (JacksonException e) {
            throw new ApplicationException("Feil ved parsing av tenor-søk", e);
        }
    }
}

class OperasjonsResultat {
    private final String id;
    private final String status;

    public OperasjonsResultat(String id, String status) {
        this.id = id;
        this.status = status;
    }

    public boolean feilet() {
        return status.equals("CONFLICT");
    }

    @Override
    public String toString() {
        return "OperasjonsResultat{" +
                "id='" + id + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
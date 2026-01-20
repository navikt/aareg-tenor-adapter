package no.nav.aareg.tenor.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.aareg.tenor.consumer.TenorUrlProperties;
import no.nav.aareg.tenor.consumer.texas.TexasConsumer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.http.ResponseEntity.status;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class Admin {

    private final TexasConsumer texasConsumer;
    private final RestClient tenorRestClient;
    private final TenorUrlProperties tenorUrlProperties;

    @Async
    @PostMapping(path = "/ruller")
    public ResponseEntity<String> ruller() {
        rullerDatasett();
        return ok("Datasett rullert");
    }

    @PostMapping(path = "/kriterier")
    public ResponseEntity<String> kriterier() {
        try {
            lastKriterier();
            return ok("Kriterier oppdatert");
        } catch (Exception e) {
            return status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping(path = "/visning")
    public ResponseEntity<String> visning() {
        try {
            lastVisning();
            return ok("Visning oppdatert");
        } catch (Exception e) {
            return status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    private void lastKriterier() throws JacksonException {
        lastKonfigurasjonsData("kriterier", "soekekriterier");
    }

    private void lastVisning() throws JacksonException {
        lastKonfigurasjonsData("visning", "soekevisning");
    }

    private void lastKonfigurasjonsData(String endepunkt, String noekkel) throws JacksonException {
        var konfigData = lastKonfigurasjon();
        if (noekkel != null) {
            konfigData = konfigData.get(noekkel);
        }
        var visning = new JsonMapper().writeValueAsString(konfigData);

        var response = tenorRestClient.put()
                .uri(tenorUrlProperties.getDatasett() + "/" + endepunkt)
                .headers(httpHeaders -> {
                    httpHeaders.setBearerAuth(texasConsumer.hentToken(TexasConsumer.IDP_MASKINPORTEN, "todo"));
                    httpHeaders.setContentType(APPLICATION_JSON);
                    httpHeaders.setAccept(List.of(APPLICATION_JSON));
                })
                .body(visning)
                .retrieve()
                .toBodilessEntity();

        if (response.getStatusCode().is2xxSuccessful()) {
            log.info("Konfigurasjon lastet opp: {}", visning);
        } else {
            log.error("Feil ved lasting av konfigurasjon: {}", visning);
        }
    }

    private void rullerDatasett() {

        var versjonsnummer = tenorRestClient.post()
                .uri(tenorUrlProperties.getDatasett())
                .headers(httpHeaders -> {
                    httpHeaders.setBearerAuth(texasConsumer.hentToken(TexasConsumer.IDP_MASKINPORTEN, "todo"));
                    httpHeaders.setContentType(APPLICATION_JSON);
                    httpHeaders.setAccept(List.of(APPLICATION_JSON));
                })
                .body("{}")
                .retrieve()
                .body(Integer.class);

        var response = tenorRestClient.put()
                .uri(tenorUrlProperties.getDatasett() + "/latest")
                .headers(httpHeaders -> {
                    httpHeaders.setBearerAuth(texasConsumer.hentToken(TexasConsumer.IDP_MASKINPORTEN, "todo"));
                    httpHeaders.setContentType(APPLICATION_JSON);
                    httpHeaders.setAccept(List.of(APPLICATION_JSON));
                })
                .body(versjonsnummer)
                .retrieve()
                .toBodilessEntity();

        log.info(response.toString());
    }

    private JsonNode lastKonfigurasjon() {
        try {
            var konfigString = Files.readString(Paths.get(Admin.class.getClassLoader().getResource("konfigurasjon.json").toURI()), StandardCharsets.UTF_8);
            return new JsonMapper().readTree(konfigString);
        } catch (IOException | URISyntaxException | NullPointerException e) {
            log.error("Feil med opplasting av konfigurasjon til tenor", e);
            return null;
        }
    }
}

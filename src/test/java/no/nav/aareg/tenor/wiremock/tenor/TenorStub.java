package no.nav.aareg.tenor.wiremock.tenor;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.extension.Extension;
import no.nav.aareg.tenor.wiremock.WireMockStub;

public class TenorStub implements WireMockStub {

    private final TenorTransformer tenorTransformer = new TenorTransformer();

    @Override
    public void registerResponseMappingBeforeAll() {
        WireMock.stubFor(get(urlPathMatching("/tenor/fnr/(.*)"))
                .willReturn(aResponse()
                        .withStatus(OK.value())
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                        .withTransformers(tenorTransformer.getName())));
        WireMock.stubFor(get(urlPathMatching("/tenor/orgnr/(.*)"))
                .willReturn(aResponse()
                        .withStatus(OK.value())
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                        .withTransformers(tenorTransformer.getName())));
    }

    @Override public Extension getWireMockExtension() {
        return tenorTransformer;
    }

    @Override
    public void registerResponseMappingBeforeEach() {
        registerResponseMappingBeforeAll();
    }
}

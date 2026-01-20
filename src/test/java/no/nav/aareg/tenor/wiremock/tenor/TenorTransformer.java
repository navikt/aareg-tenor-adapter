package no.nav.aareg.tenor.wiremock.tenor;

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.extension.ResponseDefinitionTransformerV2;
import com.github.tomakehurst.wiremock.http.ResponseDefinition;
import com.github.tomakehurst.wiremock.stubbing.ServeEvent;

public class TenorTransformer implements ResponseDefinitionTransformerV2 {
    public static final String GYLDIG_ORG = "89012345";
    public static final String UGYLDIG_ORG = "81234565";
    public static final String GYLDIG_PERSON = "12345678901";
    public static final String UGYLDIG_PERSON = "01234567891";
    public static final String FEIL = "feil";

    @Override public String getName() {
        return "brregTransformer";
    }

    @Override
    public ResponseDefinition transform(ServeEvent serveEvent) {
        var request = serveEvent.getRequest();
        var responseDef = serveEvent.getResponseDefinition();

        if (request.getUrl().endsWith(GYLDIG_ORG) || request.getUrl().endsWith(GYLDIG_PERSON)) {
            return ResponseDefinitionBuilder.
                    like(responseDef).
                    withStatus(200).
                    withBody("{\"treff\": 1}").
                    build();
        } else if (request.getUrl().endsWith(FEIL)){
            return ResponseDefinitionBuilder.
                    like(responseDef).
                    withStatus(500).
                    withBody("Jeg kræsja!").
                    build();
        } else {
            return ResponseDefinitionBuilder.
                    like(responseDef).
                    withStatus(200).
                    withBody("{\"treff\": 0}").
                    build();
        }
    }
}

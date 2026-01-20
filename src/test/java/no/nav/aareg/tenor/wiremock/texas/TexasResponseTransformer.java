package no.nav.aareg.tenor.wiremock.texas;

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.extension.ResponseDefinitionTransformerV2;
import com.github.tomakehurst.wiremock.http.ResponseDefinition;
import com.github.tomakehurst.wiremock.stubbing.ServeEvent;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import no.nav.aareg.tenor.consumer.texas.dto.TexasResponse;
import org.springframework.http.HttpHeaders;
import tools.jackson.databind.json.JsonMapper;

@RequiredArgsConstructor
public class TexasResponseTransformer implements ResponseDefinitionTransformerV2 {

    private final TexasStub texasStub;
    private final JsonMapper jsonMapper;

    @Override
    public String getName() {
        return "texas-stub";
    }

    @Override
    public ResponseDefinition transform(ServeEvent serveEvent) {
        return ResponseDefinitionBuilder
                .like(serveEvent.getResponseDefinition())
                .withStatus(200)
                .withHeader(HttpHeaders.CONNECTION, "close")
                .withBody(getResponseBody())
                .build();
    }

    @Override
    public boolean applyGlobally() {
        return false;
    }

    @SneakyThrows
    private String getResponseBody() {
        var tokenXResponse = TexasResponse.builder()
                .accessToken("token")
                .expiresIn(10000)
                .tokenType("Bearer")
                .build();

        return jsonMapper.writeValueAsString(tokenXResponse);
    }
}

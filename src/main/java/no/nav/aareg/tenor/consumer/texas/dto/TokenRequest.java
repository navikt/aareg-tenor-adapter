package no.nav.aareg.tenor.consumer.texas.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokenRequest {

    @JsonProperty("identity_provider")
    private String identityProvider;
    private String target;
}

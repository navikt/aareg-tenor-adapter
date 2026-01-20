package no.nav.aareg.tenor.config.security.idp;

import lombok.Data;

@Data
public class IdentityProvider {

    private String issuerUrl;
    private String jwkSetUri;
}

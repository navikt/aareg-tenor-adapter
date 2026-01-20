package no.nav.aareg.tenor.config.security.idp;

import no.nav.aareg.tenor.config.security.exception.OidcException;

import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;

public class IdentityProviderRegistry {

    private final Map<String, IdentityProvider> idpByIssuerMap = new HashMap<>();

    public IdentityProviderRegistry(Map<String, IdentityProvider> identityProviders) {
        identityProviders.forEach(this::add);
    }

    private void add(String name, IdentityProvider identityProvider) {
        if (identityProvider.getIssuerUrl().isBlank()) {
            throw new OidcException(format("IdentityProvider with name: %s does not have issuer url", name));
        }
        if (identityProvider.getJwkSetUri().isBlank()) {
            throw new OidcException(format("IdentityProvider with name: %s does not have jwkSet uri", name));
        }
        idpByIssuerMap.put(identityProvider.getIssuerUrl(), identityProvider);
    }

    public IdentityProvider getIdp(String idpName) {
        return idpByIssuerMap.get(idpName);
    }
}

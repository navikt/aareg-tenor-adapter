package no.nav.aareg.tenor.config.security;

import com.nimbusds.jwt.JWTParser;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.aareg.tenor.config.security.idp.IdentityProvider;
import no.nav.aareg.tenor.config.security.idp.IdentityProviderRegistry;
import org.jspecify.annotations.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.String.format;
import static org.springframework.security.oauth2.jwt.NimbusJwtDecoder.withJwkSetUri;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationManagerIssuerResolver implements AuthenticationManagerResolver<HttpServletRequest> {

    private final BearerTokenResolver resolver = new DefaultBearerTokenResolver();
    private final JwtClaimIssuerConverter issuerConverter = new JwtClaimIssuerConverter();
    private final ConcurrentHashMap<IdentityProvider, AuthenticationManager> authenticationManagers = new ConcurrentHashMap<>();

    private final IdentityProviderRegistry identityProviderRegistry;

    @Override
    public AuthenticationManager resolve(HttpServletRequest context) {
        var issuerName = issuerConverter.convert(context);
        var identityProvider = identityProviderRegistry.getIdp(issuerName);

        if (identityProvider != null) {
            return authenticationManagers.computeIfAbsent(identityProvider, idp -> {
                log.info("Creating AuthenticationManager for unregistered idp, {}", idp);
                return authenticationProvider(idp)::authenticate;
            });
        } else {
            throw new InvalidBearerTokenException(format("Untrusted issuer %s", issuerName));
        }
    }

    private JwtAuthenticationProvider authenticationProvider(IdentityProvider identityProvider) {
        var jwtDecoder = withJwkSetUri(identityProvider.getJwkSetUri()).jwsAlgorithm(SignatureAlgorithm.RS256).build();
        jwtDecoder.setJwtValidator(JwtValidators.createDefaultWithIssuer(identityProvider.getIssuerUrl()));

        return new JwtAuthenticationProvider(jwtDecoder);
    }

    private class JwtClaimIssuerConverter implements Converter<HttpServletRequest, String> {

        @Override
        public String convert(@NonNull HttpServletRequest request) {
            try {
                return Optional.ofNullable(
                                JWTParser.parse(resolver.resolve(request))
                                        .getJWTClaimsSet()
                                        .getIssuer())
                        .orElseThrow(() -> new InvalidBearerTokenException("Missing issuer"));
            } catch (Exception ex) {
                throw new InvalidBearerTokenException(ex.getMessage(), ex);
            }
        }
    }
}

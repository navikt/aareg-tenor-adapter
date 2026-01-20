package no.nav.aareg.tenor.config.security;

import lombok.RequiredArgsConstructor;
import no.nav.aareg.tenor.config.security.idp.IdentityProviderConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

@Import({IdentityProviderConfig.class})
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationManagerIssuerResolver jwtAuthenticationManagerIssuerResolver;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) {
        var pprm = PathPatternRequestMatcher.withDefaults();
        return httpSecurity
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer(oauth -> oauth.authenticationManagerResolver(jwtAuthenticationManagerIssuerResolver))
                .authorizeHttpRequests(
                        request -> request
                                .requestMatchers(
                                        pprm.matcher("/internal/**"),
                                        pprm.matcher("/actuator/**")
                                ).permitAll()
                                .requestMatchers(
                                        pprm.matcher("/api/**")
                                ).fullyAuthenticated()
                                .anyRequest().denyAll()
                )
                .build();
    }
}

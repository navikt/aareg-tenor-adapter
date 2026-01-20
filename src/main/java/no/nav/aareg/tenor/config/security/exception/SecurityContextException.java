package no.nav.aareg.tenor.config.security.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class SecurityContextException extends RuntimeException {

    public SecurityContextException(String message) {
        super(message);
    }
}

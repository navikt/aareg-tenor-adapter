package no.nav.aareg.tenor;

import static no.nav.aareg.tenor.wiremock.tenor.TenorTransformer.FEIL;
import static no.nav.aareg.tenor.wiremock.tenor.TenorTransformer.GYLDIG_ORG;
import static no.nav.aareg.tenor.wiremock.tenor.TenorTransformer.GYLDIG_PERSON;
import static no.nav.aareg.tenor.wiremock.tenor.TenorTransformer.UGYLDIG_ORG;
import static no.nav.aareg.tenor.wiremock.tenor.TenorTransformer.UGYLDIG_PERSON;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import no.nav.aareg.tenor.exception.ApplicationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import no.nav.aareg.tenor.consumer.TenorConsumer;

class TenorConsumerTest extends TenorTest {

    @Autowired
    TenorConsumer tenorConsumer;

    @Test
    void fregTest() {
        assertTrue(tenorConsumer.harPerson(GYLDIG_PERSON));
        assertFalse(tenorConsumer.harPerson(UGYLDIG_PERSON));
        assertThrows(ApplicationException.class, () -> tenorConsumer.harPerson(FEIL));
    }

    @Test
    void brregTest() {
        assertTrue(tenorConsumer.harVirksomhet(GYLDIG_ORG));
        assertFalse(tenorConsumer.harVirksomhet(UGYLDIG_ORG));
        assertThrows(ApplicationException.class, () -> tenorConsumer.harVirksomhet(FEIL));
    }
}

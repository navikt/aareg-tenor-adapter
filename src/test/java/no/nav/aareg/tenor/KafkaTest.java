package no.nav.aareg.tenor;

import no.nav.aareg.tenor.consumer.TenorConsumer;
import no.nav.aareg.tenor.consumer.aareg.AaregServicesConsumer;
import no.nav.aareg.tenor.consumer.aareg.domain.v2.Arbeidsforhold;
import no.nav.aareg.tenor.consumer.aareg.domain.v2.Arbeidstaker;
import no.nav.aareg.tenor.consumer.aareg.domain.v2.Bruksperiode;
import no.nav.aareg.tenor.consumer.aareg.domain.v2.Hovedenhet;
import no.nav.aareg.tenor.consumer.aareg.domain.v2.Ident;
import no.nav.aareg.tenor.consumer.aareg.domain.v2.Underenhet;
import no.nav.aareg.tenor.kafka.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.io.InputStream;
import java.time.Duration;
import java.util.List;

import static java.time.LocalDateTime.now;
import static no.nav.aareg.tenor.consumer.aareg.domain.v2.Identtype.FOLKEREGISTERIDENT;
import static no.nav.aareg.tenor.consumer.aareg.domain.v2.Identtype.ORGANISASJONSNUMMER;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class KafkaTest extends TenorTest {

    @MockitoBean
    private TenorConsumer tenorConsumer;

    @MockitoBean
    private TenorService tenorService;

    @MockitoBean
    private AaregServicesConsumer aaregConsumer;

    @Autowired
    private KafkaConsumer kafkaConsumer;

    @Test
    void mottaHendelseTest() {
        try (InputStream hendelse = getClass().getClassLoader().getResourceAsStream("hendelse.json")) {
            var arbeidsforhold = Arbeidsforhold.builder().navArbeidsforholdId(1L).
                    arbeidssted(Underenhet.builder().identer(List.of(new Ident(ORGANISASJONSNUMMER, "12345678", true))).build()).
                    arbeidstaker(Arbeidstaker.builder().identer(List.of(new Ident(FOLKEREGISTERIDENT, "1234567890", true))).build()).
                    opplysningspliktig(Hovedenhet.builder().identer(List.of(new Ident(FOLKEREGISTERIDENT, "234567890", true))).build()).
                    bruksperiode(Bruksperiode.builder()
                            .fom(now().minusDays(1))
                            .tom(null)
                            .build()).
                    build();
            when(aaregConsumer.finnArbeidsforhold(3058067L)).thenReturn(arbeidsforhold);
            when(tenorService.erKandidatForTenorOpplasting(any())).thenReturn(true);
            doNothing().when(tenorConsumer).lastOppArbeidsforhold(any());

            assert hendelse != null;

            var hendelseJsonString = new String(hendelse.readAllBytes());
            var ack = new Acknowledgment() {
                public boolean success = false;

                @Override
                public void acknowledge() {
                    success = true;
                }

                @Override
                public void nack(Duration duration) {
                    success = false;
                }
            };
            kafkaConsumer.onMessage(new ConsumerRecord<>("", 0, 0, 0L, hendelseJsonString), ack);
            assertTrue(ack.success);
        } catch (Exception e) {
            fail(e.getMessage());
        } finally {
            verify(aaregConsumer, times(1)).finnArbeidsforhold(3058067L);
            verify(tenorService, times(1)).erKandidatForTenorOpplasting(any());
            verify(tenorService, times(1)).lastOppArbeidsforhold(any());
        }
    }
}

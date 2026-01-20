package no.nav.aareg.tenor.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.aareg.tenor.TenorService;
import no.nav.aareg.tenor.consumer.TenorConsumer;
import no.nav.aareg.tenor.consumer.TenorUtil;
import no.nav.aareg.tenor.consumer.aareg.AaregServicesConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.MDC;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static no.nav.aareg.tenor.consumer.TenorUtil.arbeidsforholdInnenforBruksperiode;
import static no.nav.aareg.tenor.kafka.Hendelse.Endringstype.Endring;
import static no.nav.aareg.tenor.kafka.Hendelse.Endringstype.Opprettelse;
import static no.nav.aareg.tenor.kafka.Hendelse.Endringstype.Sletting;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumer {
    private final AaregServicesConsumer aaregConsumer;
    private final TenorService tenorService;
    private final TenorUtil tenorUtil;
    private final JsonMapper jsonMapper = new JsonMapper();

    private final Set<Hendelse.Endringstype> RELEVANTE_ENDRINGSTYPER_FOR_OPPLASTING = Set.of(Endring, Opprettelse);
    private static final String HENDELSE_ID_KEY = "hendelseId";
    private static final String ARBEIDSFORHOLD_ID_KEY = "arbeidsforholdId";
    private static final String HENDELSESTYPE_KEY = "hendelsestype";

    @KafkaListener(topics = {"${app.kafka.topic.arbeidsforholdhendelse}"}, id = "${app.name}")
    public void onMessage(ConsumerRecord<Long, String> record, Acknowledgment ack) {
        try {
            var hendelse = jsonMapper.readValue(record.value(), Hendelse.class);
            var arbeidsforholdId = hendelse.getArbeidsforhold().getNavArbeidsforholdId();

            MDC.put(HENDELSE_ID_KEY, String.valueOf(hendelse.getId()));
            MDC.put(ARBEIDSFORHOLD_ID_KEY, String.valueOf(arbeidsforholdId));
            MDC.put(HENDELSESTYPE_KEY, hendelse.getEndringstype().name());

            var aaregArbeidsforhold = aaregConsumer.finnArbeidsforhold(arbeidsforholdId);
            if (aaregArbeidsforhold == null) {
                log.warn("Fant ikke arbeidsforhold med id {} i Aareg", arbeidsforholdId);
                ack.acknowledge();
            } else {
                log.info("Fant arbeidsforhold: {}", jsonMapper.writeValueAsString(aaregArbeidsforhold));
                var arbeidsforholdPar = tenorUtil.konverterArbeidsforhold(aaregArbeidsforhold);
                var erKandidatForEndring = tenorService.erKandidatForTenorOpplasting(arbeidsforholdPar.getValue());

                if (erKandidatForEndring && RELEVANTE_ENDRINGSTYPER_FOR_OPPLASTING.contains(hendelse.getEndringstype()) && arbeidsforholdInnenforBruksperiode(arbeidsforholdPar.getKey())) {
                    log.info("Arbeidsforhold er relevant for Tenor og vil lastes opp");
                    tenorService.lastOppArbeidsforhold(Map.ofEntries(arbeidsforholdPar));
                } else if (erKandidatForEndring && hendelse.getEndringstype().equals(Sletting)) {
                    log.info("Arbeidsforhold skal slettes fra Tenor");
                    // Kan ikke bruke arbeidsforholdet i hendelsen, da denne ikke inneholder versjonsnummer
                    tenorService.slettArbeidsforhold(List.of(arbeidsforholdPar.getKey()));
                } else {
                    log.info("Arbeidsforhold er ikke relevant for Tenor");
                }
                ack.acknowledge();
            }
        } catch (JacksonException e) {
            log.error("Parsing av hendelse feilet", e);
            ack.nack(Duration.of(1, ChronoUnit.HOURS));
        } catch (Exception e) {
            log.error("Ukjent feil oppstod ved prosessering av melding", e);
            ack.nack(Duration.of(1, ChronoUnit.MINUTES));
        } finally {
            MDC.remove(HENDELSE_ID_KEY);
            MDC.remove(ARBEIDSFORHOLD_ID_KEY);
            MDC.remove(HENDELSESTYPE_KEY);
        }
    }
}

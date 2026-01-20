package no.nav.aareg.tenor.consumer;

import lombok.RequiredArgsConstructor;
import no.nav.aareg.tenor.consumer.aareg.domain.v2.Arbeidsforhold;
import no.nav.aareg.tenor.consumer.aareg.domain.AaregApiV2ToAaregDistApiV1Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

import java.util.Map;

import static java.time.LocalDateTime.now;
import static java.util.Collections.reverseOrder;
import static java.util.Comparator.comparing;
import static java.util.Comparator.nullsFirst;

@Component
@RequiredArgsConstructor
public class TenorUtil {
    private static final String OPPDATER = "OPPDATER";
    private static final Logger log = LoggerFactory.getLogger(TenorUtil.class);
    private final JsonMapper jsonMapper;

    /**
     * Sjekk om nåværende tidspunkt er innenfor arbeidsforholdet sin bruksperiode
     * (at det ikke er blitt satt til teknisk historikk)
     */
    public static boolean arbeidsforholdInnenforBruksperiode(Arbeidsforhold arbeidsforhold) {
        return arbeidsforhold.getBruksperiode().erInnenforPeriode(now());
    }

    private ArbeidsforholdInformasjon lagNoekkelinformasjon(no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.Arbeidsforhold arbeidsforhold) {
        var gjeldendeDetaljer = arbeidsforhold.getAnsettelsesdetaljer().stream().
                min(comparing(detaljer -> detaljer.getRapporteringsmaaneder().getTil(), nullsFirst(reverseOrder()))).orElse(null);
        return ArbeidsforholdInformasjon.builder().
                uuid(arbeidsforhold.getUuid()).
                arbeidsforholdId(arbeidsforhold.getId()).
                arbeidstaker(arbeidsforhold.getArbeidstaker().getIdent()).
                arbeidssted(arbeidsforhold.getArbeidssted().getIdent()).
                opplysningspliktig(arbeidsforhold.getOpplysningspliktig().getIdent()).
                startDato(arbeidsforhold.getAnsettelsesperiode().getStartdato().toString()).
                sluttDato(arbeidsforhold.getAnsettelsesperiode().getSluttdato() != null ? arbeidsforhold.getAnsettelsesperiode().getSluttdato().toString() : null).
                arbeidsforholdtype(arbeidsforhold.getType().getKode()).
                arbeidsforholdtypeBeskrivelse(arbeidsforhold.getType().getBeskrivelse()).
                stillingsprosent(gjeldendeDetaljer.getAvtaltStillingsprosent()).
                timerPerUke(gjeldendeDetaljer.getAntallTimerPrUke()).
                ansettelsesform(gjeldendeDetaljer.getAnsettelsesform() != null ? gjeldendeDetaljer.getAnsettelsesform().getKode() : null).
                ansettelsesformBeskrivelse(gjeldendeDetaljer.getAnsettelsesform() != null ? gjeldendeDetaljer.getAnsettelsesform().getBeskrivelse() : null).
                harPermitteringer(arbeidsforhold.getPermitteringer() != null && !arbeidsforhold.getPermitteringer().isEmpty()).
                permitteringer(arbeidsforhold.getPermitteringer() == null ? 0 : arbeidsforhold.getPermitteringer().size()).
                harPermisjoner(arbeidsforhold.getPermisjoner() != null && !arbeidsforhold.getPermisjoner().isEmpty()).
                permisjoner(arbeidsforhold.getPermisjoner() == null ? 0 : arbeidsforhold.getPermisjoner().size()).
                harTimerMedTimeloenn(arbeidsforhold.getTimerMedTimeloenn() != null && !arbeidsforhold.getTimerMedTimeloenn().isEmpty()).
                timerMedTimeloenn(arbeidsforhold.getTimerMedTimeloenn() == null ? 0 : arbeidsforhold.getTimerMedTimeloenn().size()).
                harUtenlandsopphold(arbeidsforhold.getUtenlandsopphold() != null && !arbeidsforhold.getUtenlandsopphold().isEmpty()).
                utenlandsopphold(arbeidsforhold.getUtenlandsopphold() == null ? 0 : arbeidsforhold.getUtenlandsopphold().size()).
                harHistorikk(arbeidsforhold.getAnsettelsesdetaljer().size() > 1).
                historikk(arbeidsforhold.getAnsettelsesdetaljer().size() - 1).
                build();
    }

    public Map<String, Object> lagOppdatering(Map.Entry<Arbeidsforhold, no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.Arbeidsforhold> entry) {
        try {
            var arbeidsforhold = entry.getKey();
            var mappetForhold = entry.getValue();

            return Map.of(
                    "id", arbeidsforhold.getNavUuid(),
                    "versjon", arbeidsforhold.getNavVersjon(),
                    "type", OPPDATER,
                    "soekdata", lagNoekkelinformasjon(mappetForhold),
                    "kildedata", Map.of(
                            "mimeType", "application/json",
                            "data", jsonMapper.writeValueAsString(mappetForhold)
                    )
            );
        } catch (JacksonException e) {
            log.error("Parsing av Arbeidsforhold feilet", e);
            return null;
        }
    }

    public Map.Entry<Arbeidsforhold, no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.Arbeidsforhold> konverterArbeidsforhold(Arbeidsforhold arbeidsforhold) {
        return Map.of(arbeidsforhold, AaregApiV2ToAaregDistApiV1Mapper.map(arbeidsforhold)).entrySet().iterator().next();
    }
}

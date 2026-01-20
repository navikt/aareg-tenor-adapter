package no.nav.aareg.tenor.consumer.aareg.domain.v2;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class Arbeidsforhold {

    private String id;

    private Kodeverksentitet type;

    private Arbeidstaker arbeidstaker;

    private Arbeidssted arbeidssted;

    private Opplysningspliktig opplysningspliktig;

    private Ansettelsesperiode ansettelsesperiode;

    private List<Ansettelsesdetaljer> ansettelsesdetaljer;

    private List<Permisjon> permisjoner;

    private List<Permittering> permitteringer;

    private List<TimerMedTimeloenn> timerMedTimeloenn;

    private List<Utenlandsopphold> utenlandsopphold;

    private List<IdHistorikk> idHistorikk;

    private List<Varsel> varsler;

    private Kodeverksentitet rapporteringsordning;

    private Long navArbeidsforholdId;

    private Integer navVersjon;

    private String navUuid;

    private LocalDateTime opprettet;

    private LocalDateTime sistBekreftet;

    private LocalDateTime sistEndret;

    private Bruksperiode bruksperiode;

    private Sporingsinformasjon sporingsinformasjon;
}
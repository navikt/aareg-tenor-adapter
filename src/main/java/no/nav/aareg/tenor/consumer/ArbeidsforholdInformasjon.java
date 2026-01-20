package no.nav.aareg.tenor.consumer;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
class ArbeidsforholdInformasjon {
    String uuid;
    String arbeidsforholdId;
    String arbeidstaker;
    String arbeidssted;
    String opplysningspliktig;
    String startDato;
    String sluttDato;
    String arbeidsforholdtype;
    String arbeidsforholdtypeBeskrivelse;
    Double stillingsprosent;
    Double timerPerUke;
    String ansettelsesform;
    String ansettelsesformBeskrivelse;
    boolean harPermitteringer;
    int permitteringer;
    boolean harPermisjoner;
    int permisjoner;
    boolean harTimerMedTimeloenn;
    int timerMedTimeloenn;
    boolean harUtenlandsopphold;
    int utenlandsopphold;
    boolean harHistorikk;
    int historikk;
}

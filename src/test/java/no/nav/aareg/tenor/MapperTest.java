package no.nav.aareg.tenor;

import static no.nav.aareg.tenor.consumer.aareg.domain.AaregApiV2ToAaregDistApiV1Mapper.map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import org.junit.jupiter.api.Test;

import no.nav.aareg.tenor.consumer.aareg.domain.v2.Ansettelsesdetaljer;
import no.nav.aareg.tenor.consumer.aareg.domain.v2.Ansettelsesperiode;
import no.nav.aareg.tenor.consumer.aareg.domain.v2.Arbeidsforhold;
import no.nav.aareg.tenor.consumer.aareg.domain.v2.Arbeidssted;
import no.nav.aareg.tenor.consumer.aareg.domain.v2.Arbeidstaker;
import no.nav.aareg.tenor.consumer.aareg.domain.v2.Bruksperiode;
import no.nav.aareg.tenor.consumer.aareg.domain.v2.ForenkletAnsettelsesdetaljer;
import no.nav.aareg.tenor.consumer.aareg.domain.v2.FrilanserAnsettelsesdetaljer;
import no.nav.aareg.tenor.consumer.aareg.domain.v2.Hovedenhet;
import no.nav.aareg.tenor.consumer.aareg.domain.v2.IdHistorikk;
import no.nav.aareg.tenor.consumer.aareg.domain.v2.Ident;
import no.nav.aareg.tenor.consumer.aareg.domain.v2.Identtype;
import no.nav.aareg.tenor.consumer.aareg.domain.v2.Kodeverksentitet;
import no.nav.aareg.tenor.consumer.aareg.domain.v2.MaritimAnsettelsesdetaljer;
import no.nav.aareg.tenor.consumer.aareg.domain.v2.Opplysningspliktig;
import no.nav.aareg.tenor.consumer.aareg.domain.v2.OrdinaerAnsettelsesdetaljer;
import no.nav.aareg.tenor.consumer.aareg.domain.v2.Permisjon;
import no.nav.aareg.tenor.consumer.aareg.domain.v2.Permittering;
import no.nav.aareg.tenor.consumer.aareg.domain.v2.Person;
import no.nav.aareg.tenor.consumer.aareg.domain.v2.Rapporteringsmaaneder;
import no.nav.aareg.tenor.consumer.aareg.domain.v2.Sporingsinformasjon;
import no.nav.aareg.tenor.consumer.aareg.domain.v2.TimerMedTimeloenn;
import no.nav.aareg.tenor.consumer.aareg.domain.v2.Underenhet;
import no.nav.aareg.tenor.consumer.aareg.domain.v2.Utenlandsopphold;
import no.nav.aareg.tenor.consumer.aareg.domain.v2.Varsel;
import no.nav.aareg.tenor.consumer.aareg.domain.v2.Varselentitet;

class MapperTest {
    private static final Sporingsinformasjon sporingsinformasjon = new Sporingsinformasjon(
            LocalDateTime.of(2022, 1, 1, 0, 0),
            "OPPRETTER",
            "OKILDE",
            "OREF",
            LocalDateTime.of(2022, 2, 1, 0, 0),
            "ENDRER",
            "EKILDE",
            "EREF"
    );

    @Test
    void kodeverkTest() {
        assertEquals(new no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.Kodeverksentitet(
                null,
                null
        ), map(new Kodeverksentitet(null, null)));

        assertNull(map((Kodeverksentitet) null));

        assertEquals(new no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.Kodeverksentitet(
                "a",
                "b"
        ), map(new Kodeverksentitet("a", "b")));
    }

    @Test
    void arbeidstakerTest() {
        assertNull(map((Arbeidstaker) null));

        var person = new Arbeidstaker();
        person.setIdenter(List.of(new Ident(Identtype.AKTORID, "aktorid", false),
                new Ident(Identtype.FOLKEREGISTERIDENT, "gammeltfnr", false),
                new Ident(Identtype.FOLKEREGISTERIDENT, "a", true)));

        assertEquals(new no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.Arbeidstaker("a"),
                map(person));
    }

    @Test
    void varselTest() {
        assertNull(map((Varsel) null));

        assertEquals(
                new no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.Varsel(
                        no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.Varselentitet.Ansettelsesperiode,
                        new no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.Kodeverksentitet("a", "b")),
                map(new Varsel(Varselentitet.Ansettelsesperiode, new Kodeverksentitet("a", "b")))
        );

        assertEquals(
                new no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.Varsel(
                        no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.Varselentitet.Permisjon,
                        new no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.Kodeverksentitet("a", "b")),
                map(new Varsel(Varselentitet.Permisjon, new Kodeverksentitet("a", "b")))
        );

        assertEquals(
                new no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.Varsel(
                        no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.Varselentitet.Permittering,
                        new no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.Kodeverksentitet("a", "b")),
                map(new Varsel(Varselentitet.Permittering, new Kodeverksentitet("a", "b")))
        );

        assertEquals(
                new no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.Varsel(
                        no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.Varselentitet.Arbeidsforhold,
                        new no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.Kodeverksentitet("a", "b")),
                map(new Varsel(Varselentitet.Arbeidsforhold, new Kodeverksentitet("a", "b")))
        );

        assertEquals(
                new no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.Varsel(null, null),
                map(new Varsel(null, null))
        );
    }

    @Test
    void utenlandsoppholdTest() {
        assertNull(map((Utenlandsopphold) null));

        assertEquals(new no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.Utenlandsopphold(null, null),
                map(new Utenlandsopphold(null, null, null)));

        assertEquals(new no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.Utenlandsopphold(
                        new no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.Kodeverksentitet("a", "b"),
                        YearMonth.of(2022, 1)),
                map(new Utenlandsopphold(
                        new Kodeverksentitet("a", "b"),
                        YearMonth.of(2022, 1),
                        sporingsinformasjon
                )));
    }

    @Test
    void timerMedTimeloennTest() {
        assertNull(map((TimerMedTimeloenn) null));

        assertEquals(new no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.TimerMedTimeloenn(null, null),
                map(new TimerMedTimeloenn(null, null, null)));

        assertEquals(new no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.TimerMedTimeloenn(2.3, YearMonth.of(2022, 1)),
                map(new TimerMedTimeloenn(2.3, YearMonth.of(2022, 1), sporingsinformasjon)));
    }

    @Test
    void permitteringTest() {
        assertNull(map((Permittering) null));

        var distPermittering = new no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.Permittering();
        var permittering = new Permittering();

        assertEquals(distPermittering, map(permittering));

        distPermittering.setId("a");
        permittering.setId("a");
        distPermittering.setVarsling(new no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.Kodeverksentitet("a", "b"));
        permittering.setVarsling(new Kodeverksentitet("a", "b"));
        distPermittering.setType(new no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.Kodeverksentitet("c", "d"));
        permittering.setType(new Kodeverksentitet("c", "d"));
        distPermittering.setStartdato(LocalDate.of(2022, 1, 1));
        permittering.setStartdato(LocalDate.of(2022, 1, 1));
        distPermittering.setSluttdato(LocalDate.of(2022, 1, 10));
        permittering.setSluttdato(LocalDate.of(2022, 1, 10));
        distPermittering.setProsent(12.3);
        permittering.setProsent(12.3);
        distPermittering.setIdHistorikk(List.of(new no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.IdHistorikk("a")));
        permittering.setIdHistorikk(List.of(new IdHistorikk("a", new Bruksperiode(
                LocalDateTime.of(2022, 1, 1, 1, 1),
                LocalDateTime.of(2022, 1, 10, 1, 1)))));
        permittering.setSporingsinformasjon(sporingsinformasjon);

        assertEquals(distPermittering, map(permittering));
    }

    @Test
    void permisjonsTest() {
        assertNull(map((Permisjon) null));

        var distPermisjon = new no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.Permisjon();
        var permisjon = new Permisjon();

        assertEquals(distPermisjon, map(permisjon));

        distPermisjon.setId("a");
        permisjon.setId("a");
        distPermisjon.setVarsling(new no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.Kodeverksentitet("a", "b"));
        permisjon.setVarsling(new Kodeverksentitet("a", "b"));
        distPermisjon.setType(new no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.Kodeverksentitet("c", "d"));
        permisjon.setType(new Kodeverksentitet("c", "d"));
        distPermisjon.setStartdato(LocalDate.of(2022, 1, 1));
        permisjon.setStartdato(LocalDate.of(2022, 1, 1));
        distPermisjon.setSluttdato(LocalDate.of(2022, 1, 10));
        permisjon.setSluttdato(LocalDate.of(2022, 1, 10));
        distPermisjon.setProsent(12.3);
        permisjon.setProsent(12.3);
        distPermisjon.setIdHistorikk(List.of(new no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.IdHistorikk("a")));
        permisjon.setIdHistorikk(List.of(new IdHistorikk("a", new Bruksperiode(
                LocalDateTime.of(2022, 1, 1, 1, 1),
                LocalDateTime.of(2022, 1, 10, 1, 1)))));
        permisjon.setSporingsinformasjon(sporingsinformasjon);

        assertEquals(distPermisjon, map(permisjon));
    }

    @Test
    void ordinaereAnsettelsesdetaljerTest() {
        assertNull(map((OrdinaerAnsettelsesdetaljer) null));

        assertEquals(new no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.OrdinaerAnsettelsesdetaljer(), map(new OrdinaerAnsettelsesdetaljer()));

        var distDetaljer = new no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.OrdinaerAnsettelsesdetaljer();
        var detaljer = new OrdinaerAnsettelsesdetaljer();

        standardAnsettelsesdetaljverdier(distDetaljer, detaljer);

        assertEquals(distDetaljer, map(detaljer));
    }

    @Test
    void forenkletAnsettelsesdetaljerTest() {
        assertNull(map((ForenkletAnsettelsesdetaljer) null));

        assertEquals(new no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.ForenkletAnsettelsesdetaljer(), map(new ForenkletAnsettelsesdetaljer()));

        var distDetaljer = new no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.ForenkletAnsettelsesdetaljer();
        var detaljer = new ForenkletAnsettelsesdetaljer();

        standardAnsettelsesdetaljverdier(distDetaljer, detaljer);

        assertEquals(distDetaljer, map(detaljer));
    }

    @Test
    void frilanserAnsettelsesdetaljerTest() {
        assertNull(map((FrilanserAnsettelsesdetaljer) null));

        assertEquals(new no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.FrilanserAnsettelsesdetaljer(), map(new FrilanserAnsettelsesdetaljer()));

        var distDetaljer = new no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.FrilanserAnsettelsesdetaljer();
        var detaljer = new FrilanserAnsettelsesdetaljer();

        standardAnsettelsesdetaljverdier(distDetaljer, detaljer);

        assertEquals(distDetaljer, map(detaljer));
    }

    @Test
    void maritimeAnsettelsesdetaljerTest() {
        assertNull(map((MaritimAnsettelsesdetaljer) null));

        assertEquals(new no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.MaritimAnsettelsesdetaljer(), map(new MaritimAnsettelsesdetaljer()));

        var distDetaljer = new no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.MaritimAnsettelsesdetaljer();
        var detaljer = new MaritimAnsettelsesdetaljer();

        standardAnsettelsesdetaljverdier(distDetaljer, detaljer);
        distDetaljer.setFartsomraade(new no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.Kodeverksentitet("fartsområde", "a"));
        detaljer.setFartsomraade(new Kodeverksentitet("fartsområde", "a"));
        distDetaljer.setFartoeystype(new no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.Kodeverksentitet("fartøystype", "a"));
        detaljer.setFartoeystype(new Kodeverksentitet("fartøystype", "a"));
        distDetaljer.setSkipsregister(new no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.Kodeverksentitet("skipsregister", "a"));
        detaljer.setSkipsregister(new Kodeverksentitet("skipsregister", "a"));

        assertEquals(distDetaljer, map(detaljer));
    }

    @Test
    void ansettelsesperiodeTest() {
        assertNull(map((Ansettelsesperiode) null));

        var distPeriode = new no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.Ansettelsesperiode(
                new no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.Kodeverksentitet("sluttårsak", "a"),
                new no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.Kodeverksentitet("varsling", "b")
        );
        var periode = new Ansettelsesperiode(
                new Kodeverksentitet("sluttårsak", "a"),
                new Kodeverksentitet("varsling", "b"),
                sporingsinformasjon);
        distPeriode.setStartdato(LocalDate.of(2022, 1, 1));
        periode.setStartdato(LocalDate.of(2022, 1, 1));
        distPeriode.setSluttdato(LocalDate.of(2022, 1, 5));
        periode.setSluttdato(LocalDate.of(2022, 1, 5));

        assertEquals(distPeriode, map(periode));
    }

    @Test
    void personArbeidsstedTest() {
        assertNull(map((Arbeidssted) null));

        var person = new Person();
        person.setIdenter(List.of(new Ident(Identtype.AKTORID, "aktorid", false),
                new Ident(Identtype.FOLKEREGISTERIDENT, "gammeltfnr", false),
                new Ident(Identtype.FOLKEREGISTERIDENT, "a", true)));

        assertEquals(new no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.Person("a"), map((Arbeidssted) person));
    }

    @Test
    void underenhetArbeidsstedTest() {
        assertNull(map((Arbeidssted) null));

        var distUnderenhet = new no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.Underenhet();
        distUnderenhet.setIdent("a");
        var underenhet = new Underenhet();
        underenhet.setIdenter(List.of(new Ident(Identtype.ORGANISASJONSNUMMER, "a", true)));

        assertEquals(distUnderenhet, map(underenhet));
    }

    @Test
    void personOpplysningspliktigTest() {
        assertNull(map((Opplysningspliktig) null));

        var person = new Person();
        person.setIdenter(List.of(new Ident(Identtype.AKTORID, "aktorid", false),
                new Ident(Identtype.FOLKEREGISTERIDENT, "gammeltfnr", false),
                new Ident(Identtype.FOLKEREGISTERIDENT, "a", true)));

        assertEquals(new no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.Person("a"), map((Opplysningspliktig) person));
    }

    @Test
    void hovedenhetOpplysningspliktigTest() {
        assertNull(map((Opplysningspliktig) null));

        var distHovedenhet = new no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.Hovedenhet();
        distHovedenhet.setIdent("a");
        var hovedenhet = new Hovedenhet();
        hovedenhet.setIdenter(List.of(new Ident(Identtype.ORGANISASJONSNUMMER, "a", true)));

        assertEquals(distHovedenhet, map(hovedenhet));
    }

    @Test
    public void arbeidsforholdTest() {
        assertNull(map((Arbeidsforhold) null));

        var distOpplysningspliktig = new no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.Hovedenhet();
        var opplysningspliktig = new Hovedenhet();
        distOpplysningspliktig.setIdent("hoved");
        opplysningspliktig.setIdenter(List.of(new Ident(Identtype.ORGANISASJONSNUMMER, "hoved", true)));

        var distArbeidssted = new no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.Person();
        var arbeidssted = new Person();
        distArbeidssted.setIdent("arbeidssted");
        arbeidssted.setIdenter(List.of(new Ident(Identtype.FOLKEREGISTERIDENT, "arbeidssted", true)));

        var arbeidstaker = new Arbeidstaker();
        arbeidstaker.setIdenter(List.of(new Ident(Identtype.FOLKEREGISTERIDENT, "arbeidstaker", true)));

        assertEquals(new no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.Arbeidsforhold(
                "id",
                new no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.Kodeverksentitet("type", "a"),
                new no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.Arbeidstaker("arbeidstaker"),
                distArbeidssted,
                distOpplysningspliktig,
                new no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.Ansettelsesperiode(
                        new no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.Kodeverksentitet("sluttet", "a"),
                        new no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.Kodeverksentitet("periodevarsel", "b")
                ),
                List.of(new no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.OrdinaerAnsettelsesdetaljer()),
                List.of(new no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.Permisjon()),
                List.of(new no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.Permittering()),
                List.of(new no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.TimerMedTimeloenn()),
                List.of(new no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.Utenlandsopphold()),
                List.of(new no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.IdHistorikk("gammelId")),
                List.of(new no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.Varsel(no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.Varselentitet.Ansettelsesperiode, new no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.Kodeverksentitet("varsel", "n"))),
                new no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.Kodeverksentitet("aordningen", "x"),
                "uuid",
                LocalDateTime.of(2022, 1, 1, 1, 1, 1),
                LocalDateTime.of(2022, 1, 1, 1, 1, 10),
                LocalDateTime.of(2022, 1, 1, 1, 1, 20)
        ), map(new Arbeidsforhold(
                "id",
                new Kodeverksentitet("type", "a"),
                arbeidstaker,
                arbeidssted,
                opplysningspliktig,
                new Ansettelsesperiode(
                        new Kodeverksentitet("sluttet", "a"),
                        new Kodeverksentitet("periodevarsel", "b"),
                        sporingsinformasjon
                ),
                List.of(new OrdinaerAnsettelsesdetaljer()),
                List.of(new Permisjon()),
                List.of(new Permittering()),
                List.of(new TimerMedTimeloenn()),
                List.of(new Utenlandsopphold()),
                List.of(new IdHistorikk("gammelId", new Bruksperiode(LocalDateTime.MIN, null))),
                List.of(new Varsel(Varselentitet.Ansettelsesperiode, new Kodeverksentitet("varsel", "n"))),
                new Kodeverksentitet("aordningen", "x"),
                12L,
                2,
                "uuid",
                LocalDateTime.of(2022, 1, 1, 1, 1, 1),
                LocalDateTime.of(2022, 1, 1, 1, 1, 10),
                LocalDateTime.of(2022, 1, 1, 1, 1, 20),
                new Bruksperiode(LocalDateTime.MIN, LocalDateTime.MAX),
                sporingsinformasjon
        )));
    }

    private static void standardAnsettelsesdetaljverdier(no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.Ansettelsesdetaljer distDetaljer, Ansettelsesdetaljer detaljer) {
        distDetaljer.setAnsettelsesform(new no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.Kodeverksentitet("form", "a"));
        detaljer.setAnsettelsesform(new Kodeverksentitet("form", "a"));
        distDetaljer.setYrke(new no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.Kodeverksentitet("yrke", "a"));
        detaljer.setYrke(new Kodeverksentitet("yrke", "a"));
        distDetaljer.setArbeidstidsordning(new no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.Kodeverksentitet("ordning", "a"));
        detaljer.setArbeidstidsordning(new Kodeverksentitet("ordning", "a"));
        distDetaljer.setRapporteringsmaaneder(new no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.Rapporteringsmaaneder(YearMonth.of(2022, 1), YearMonth.of(2022, 2)));
        detaljer.setRapporteringsmaaneder(new Rapporteringsmaaneder(YearMonth.of(2022, 1), YearMonth.of(2022, 2)));
        distDetaljer.setAvtaltStillingsprosent(12.2);
        detaljer.setAvtaltStillingsprosent(12.2);
        distDetaljer.setAntallTimerPrUke(37.5);
        detaljer.setAntallTimerPrUke(37.5);
        distDetaljer.setSisteLoennsendring(LocalDate.of(2022, 1, 5));
        detaljer.setSisteLoennsendring(LocalDate.of(2022, 1, 5));
        distDetaljer.setSisteStillingsprosentendring(LocalDate.of(2022, 1, 7));
        detaljer.setSisteStillingsprosentendring(LocalDate.of(2022, 1, 7));
        detaljer.setSporingsinformasjon(sporingsinformasjon);
    }
}

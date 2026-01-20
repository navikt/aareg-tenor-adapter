package no.nav.aareg.tenor.consumer.aareg.domain;

import static java.lang.Boolean.TRUE;
import static java.util.Optional.ofNullable;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import no.nav.aareg.tenor.consumer.aareg.domain.v2.Ansettelsesdetaljer;
import no.nav.aareg.tenor.consumer.aareg.domain.v2.Ansettelsesperiode;
import no.nav.aareg.tenor.consumer.aareg.domain.v2.Arbeidsforhold;
import no.nav.aareg.tenor.consumer.aareg.domain.v2.Arbeidssted;
import no.nav.aareg.tenor.consumer.aareg.domain.v2.Arbeidstaker;
import no.nav.aareg.tenor.consumer.aareg.domain.v2.ForenkletAnsettelsesdetaljer;
import no.nav.aareg.tenor.consumer.aareg.domain.v2.FrilanserAnsettelsesdetaljer;
import no.nav.aareg.tenor.consumer.aareg.domain.v2.Hovedenhet;
import no.nav.aareg.tenor.consumer.aareg.domain.v2.IdHistorikk;
import no.nav.aareg.tenor.consumer.aareg.domain.v2.Kodeverksentitet;
import no.nav.aareg.tenor.consumer.aareg.domain.v2.MaritimAnsettelsesdetaljer;
import no.nav.aareg.tenor.consumer.aareg.domain.v2.Opplysningspliktig;
import no.nav.aareg.tenor.consumer.aareg.domain.v2.OrdinaerAnsettelsesdetaljer;
import no.nav.aareg.tenor.consumer.aareg.domain.v2.Permisjon;
import no.nav.aareg.tenor.consumer.aareg.domain.v2.PermisjonPermittering;
import no.nav.aareg.tenor.consumer.aareg.domain.v2.Permittering;
import no.nav.aareg.tenor.consumer.aareg.domain.v2.Person;
import no.nav.aareg.tenor.consumer.aareg.domain.v2.Rapporteringsmaaneder;
import no.nav.aareg.tenor.consumer.aareg.domain.v2.TimerMedTimeloenn;
import no.nav.aareg.tenor.consumer.aareg.domain.v2.Underenhet;
import no.nav.aareg.tenor.consumer.aareg.domain.v2.Utenlandsopphold;
import no.nav.aareg.tenor.consumer.aareg.domain.v2.Varsel;
import no.nav.aareg.tenor.consumer.aareg.domain.v2.Varselentitet;

public class AaregApiV2ToAaregDistApiV1Mapper {

    private static String finnGjeldendeFolkeregisterIdent(List<no.nav.aareg.tenor.consumer.aareg.domain.v2.Ident> identer) {
        if (identer == null) {
            return null;
        }
        return identer.stream()
                .filter(ident -> no.nav.aareg.tenor.consumer.aareg.domain.v2.Identtype.FOLKEREGISTERIDENT.equals(ident.getType()) && TRUE.equals(ident.getGjeldende()))
                .map(no.nav.aareg.tenor.consumer.aareg.domain.v2.Ident::getIdent)
                .findFirst()
                .orElse(null);
    }

    public static no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.Kodeverksentitet map(Kodeverksentitet kodeverksentitet) {
        if (kodeverksentitet == null) {
            return null;
        }
        return new no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.Kodeverksentitet(
                kodeverksentitet.getKode(),
                kodeverksentitet.getBeskrivelse()
        );
    }

    public static no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.Arbeidstaker map(Arbeidstaker arbeidstaker) {
        if (arbeidstaker == null) {
            return null;
        }
        return new no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.Arbeidstaker(
                finnGjeldendeFolkeregisterIdent(arbeidstaker.getIdenter())
        );
    }

    public static no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.Varsel map(Varsel varsel) {
        if (varsel == null) {
            return null;
        }
        return new no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.Varsel(
                map(varsel.getEntitet()),
                map(varsel.getVarsling())
        );
    }

    private static no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.Varselentitet map(Varselentitet entitet) {
        if (entitet == null) {
            return null;
        }
        return switch (entitet) {
            case Arbeidsforhold -> no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.Varselentitet.Arbeidsforhold;
            case Ansettelsesperiode -> no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.Varselentitet.Ansettelsesperiode;
            case Permisjon -> no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.Varselentitet.Permisjon;
            case Permittering -> no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.Varselentitet.Permittering;
        };
    }

    public static no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.Utenlandsopphold map(Utenlandsopphold utenlandsopphold) {
        if (utenlandsopphold == null) {
            return null;
        }
        return new no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.Utenlandsopphold(
                map(utenlandsopphold.getLand()),
                utenlandsopphold.getRapporteringsmaaned()
        );
    }

    public static no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.TimerMedTimeloenn map(TimerMedTimeloenn timerMedTimeloenn) {
        if (timerMedTimeloenn == null) {
            return null;
        }
        return new no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.TimerMedTimeloenn(
                timerMedTimeloenn.getAntall(),
                timerMedTimeloenn.getRapporteringsmaaned()
        );
    }

    public static no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.Permittering map(Permittering permittering) {
        if (permittering == null) {
            return null;
        }
        var permitteringdist = new no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.Permittering();
        addAll(permitteringdist, permittering);
        return permitteringdist;
    }

    public static no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.Permisjon map(Permisjon permisjon) {
        if (permisjon == null) {
            return null;
        }
        var permisjonDist = new no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.Permisjon();
        addAll(permisjonDist, permisjon);
        return permisjonDist;
    }

    private static void addAll(no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.PermisjonPermittering permisjonPermitteringDist, PermisjonPermittering permisjonPermittering) {
        permisjonPermitteringDist.setId(permisjonPermittering.getId());
        permisjonPermitteringDist.setType(map(permisjonPermittering.getType()));
        permisjonPermitteringDist.setProsent(permisjonPermittering.getProsent());
        permisjonPermitteringDist.setStartdato(permisjonPermittering.getStartdato());
        permisjonPermitteringDist.setSluttdato(permisjonPermittering.getSluttdato());
        permisjonPermitteringDist.setVarsling(map(permisjonPermittering.getVarsling()));
        permisjonPermitteringDist.setIdHistorikk(mapList(AaregApiV2ToAaregDistApiV1Mapper::map, permisjonPermittering.getIdHistorikk()));
    }

    private static no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.IdHistorikk map(IdHistorikk idHistorikk) {
        if (idHistorikk == null) {
            return null;
        }
        return new no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.IdHistorikk(idHistorikk.getId());
    }

    private static <T, R> List<R> mapList(Function<T, R> mapper, List<T> elementer) {
        return ofNullable(elementer)
                .map(x -> x.stream().map(mapper)
                        .collect(Collectors.toList()))
                .orElse(null);
    }

    public static no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.Ansettelsesdetaljer map(Ansettelsesdetaljer ansettelsesdetaljer) {
        switch (ansettelsesdetaljer) {
        case OrdinaerAnsettelsesdetaljer detaljer -> {
            var dist = new no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.OrdinaerAnsettelsesdetaljer();
            addAll(dist, detaljer);
            return dist;
        }
        case MaritimAnsettelsesdetaljer detaljer -> {
            var dist = new no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.MaritimAnsettelsesdetaljer();
            addAll(dist, detaljer);
            dist.setFartoeystype(map(detaljer.getFartoeystype()));
            dist.setSkipsregister(map(detaljer.getSkipsregister()));
            dist.setFartsomraade(map(detaljer.getFartsomraade()));
            return dist;
        }
        case ForenkletAnsettelsesdetaljer detaljer -> {
            var dist = new no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.ForenkletAnsettelsesdetaljer();
            addAll(dist, detaljer);
            return dist;
        }
        case FrilanserAnsettelsesdetaljer detaljer -> {
            var dist = new no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.FrilanserAnsettelsesdetaljer();
            addAll(dist, detaljer);
            return dist;
        }
        case null -> {
            return null;
        }
        default -> throw new IllegalStateException("Ukjent type for ansettelsesdetalj: " + ansettelsesdetaljer.getClass().getSimpleName());
        }
    }

    private static void addAll(no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.Ansettelsesdetaljer distDetaljer, Ansettelsesdetaljer detaljer) {
        distDetaljer.setAnsettelsesform(map(detaljer.getAnsettelsesform()));
        distDetaljer.setArbeidstidsordning(map(detaljer.getArbeidstidsordning()));
        distDetaljer.setRapporteringsmaaneder(map(detaljer.getRapporteringsmaaneder()));
        distDetaljer.setYrke(map(detaljer.getYrke()));
        distDetaljer.setAvtaltStillingsprosent(detaljer.getAvtaltStillingsprosent());
        distDetaljer.setSisteLoennsendring(detaljer.getSisteLoennsendring());
        distDetaljer.setAntallTimerPrUke(detaljer.getAntallTimerPrUke());
        distDetaljer.setSisteStillingsprosentendring(detaljer.getSisteStillingsprosentendring());
    }

    private static no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.Rapporteringsmaaneder map(Rapporteringsmaaneder rapporteringsmaaneder) {
        if (rapporteringsmaaneder == null) {
            return null;
        }
        return new no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.Rapporteringsmaaneder(
                rapporteringsmaaneder.getFra(),
                rapporteringsmaaneder.getTil()
        );
    }

    public static no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.Ansettelsesperiode map(Ansettelsesperiode ansettelsesperiode) {
        if (ansettelsesperiode == null) {
            return null;
        }
        var periode = new no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.Ansettelsesperiode(
                map(ansettelsesperiode.getSluttaarsak()),
                map(ansettelsesperiode.getVarsling())
        );
        periode.setStartdato(ansettelsesperiode.getStartdato());
        periode.setSluttdato(ansettelsesperiode.getSluttdato());
        return periode;
    }

    public static no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.Arbeidssted map(Arbeidssted arbeidssted) {
        if (arbeidssted == null) {
            return null;
        }
        if (arbeidssted instanceof Underenhet enhet) {
            var underenhet = new no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.Underenhet();
            underenhet.setIdent(enhet.getIdent());
            return underenhet;
        } else if (arbeidssted instanceof Person personV1) {
            var person = new no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.Person();
            person.setIdent(finnGjeldendeFolkeregisterIdent(personV1.getIdenter()));
            return person;
        } else {
            throw new IllegalStateException("Ukjent type for arbeidssted:" + arbeidssted.getClass().getSimpleName());
        }
    }

    public static no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.Opplysningspliktig map(Opplysningspliktig opplysningspliktig) {
        if (opplysningspliktig == null) {
            return null;
        }
        if (opplysningspliktig instanceof Hovedenhet hovedenhet) {
            var enhet = new no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.Hovedenhet();
            enhet.setIdent(hovedenhet.getIdent());
            return enhet;
        } else if (opplysningspliktig instanceof Person person) {
            var personDist = new no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.Person();
            personDist.setIdent(finnGjeldendeFolkeregisterIdent(person.getIdenter()));
            return personDist;
        } else {
            throw new IllegalStateException("Ukjent type for opplysningspliktig: " + opplysningspliktig.getClass().getSimpleName());
        }
    }

    public static no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.Arbeidsforhold map(Arbeidsforhold arbeidsforhold) {
        if (arbeidsforhold == null) {
            return null;
        }
        return new no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.Arbeidsforhold(
                arbeidsforhold.getId(),
                map(arbeidsforhold.getType()),
                map(arbeidsforhold.getArbeidstaker()),
                map(arbeidsforhold.getArbeidssted()),
                map(arbeidsforhold.getOpplysningspliktig()),
                map(arbeidsforhold.getAnsettelsesperiode()),
                mapList(AaregApiV2ToAaregDistApiV1Mapper::map, arbeidsforhold.getAnsettelsesdetaljer()),
                mapList(AaregApiV2ToAaregDistApiV1Mapper::map, arbeidsforhold.getPermisjoner()),
                mapList(AaregApiV2ToAaregDistApiV1Mapper::map, arbeidsforhold.getPermitteringer()),
                mapList(AaregApiV2ToAaregDistApiV1Mapper::map, arbeidsforhold.getTimerMedTimeloenn()),
                mapList(AaregApiV2ToAaregDistApiV1Mapper::map, arbeidsforhold.getUtenlandsopphold()),
                mapList(AaregApiV2ToAaregDistApiV1Mapper::map, arbeidsforhold.getIdHistorikk()),
                mapList(AaregApiV2ToAaregDistApiV1Mapper::map, arbeidsforhold.getVarsler()),
                map(arbeidsforhold.getRapporteringsordning()),
                arbeidsforhold.getNavUuid(),
                arbeidsforhold.getOpprettet(),
                arbeidsforhold.getSistBekreftet(),
                arbeidsforhold.getSistEndret()
        );
    }
}
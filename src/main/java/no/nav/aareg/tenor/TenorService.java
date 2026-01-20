package no.nav.aareg.tenor;

import lombok.RequiredArgsConstructor;
import no.nav.aareg.tenor.consumer.aareg.domain.v2.Arbeidsforhold;
import no.nav.aareg.tenor.consumer.TenorConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TenorService {

    private static final Logger log = LoggerFactory.getLogger(TenorService.class);
    private final TenorConsumer tenorConsumer;

    public boolean erKandidatForTenorOpplasting(no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.Arbeidsforhold arbeidsforhold) {
        var harPerson = tenorConsumer.harPerson(arbeidsforhold.getArbeidstaker().getIdent());
        var harVirksomhet = tenorConsumer.harVirksomhet(arbeidsforhold.getArbeidssted().getIdent());
        var harOpplysningspliktig = tenorConsumer.harVirksomhet(arbeidsforhold.getOpplysningspliktig().getIdent());
        return harPerson && harVirksomhet && harOpplysningspliktig;
    }

    public void lastOppArbeidsforhold(Map<Arbeidsforhold, no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.Arbeidsforhold> arbeidsforhold) {
        tenorConsumer.lastOppArbeidsforhold(arbeidsforhold);
    }

    public void slettArbeidsforhold(List<Arbeidsforhold> arbeidsforhold) {
        log.info("Sletter arbeidsforhold: {}", arbeidsforhold.stream().map(Arbeidsforhold::getNavUuid).toList());
        tenorConsumer.slettArbeidsforhold(arbeidsforhold);
    }
}

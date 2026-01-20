package no.nav.aareg.tenor.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.aareg.tenor.TenorService;
import no.nav.aareg.tenor.consumer.TenorConsumer;
import no.nav.aareg.tenor.consumer.TenorUtil;
import no.nav.aareg.tenor.consumer.aareg.AaregServicesConsumer;
import no.nav.aareg.tenor.consumer.aareg.domain.v2.Arbeidsforhold;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


@RequestMapping("/api/datalast")
@RestController
@RequiredArgsConstructor
@Slf4j
public class Datalast {
    private final AaregServicesConsumer aaregConsumer;
    private final TenorConsumer tenorConsumer;
    private final TenorService tenorService;
    private final TenorUtil tenorUtil;

    private static final int MAKS_ARBEIDSFORHOLD_PER_TENOR_OPPLASTING = 400;

    @PostMapping(path = "/arbeidsforhold")
    public List<Long> lastArbeidsforhold(@RequestBody List<Long> arbeidsforholdIder) {
        List<Long> result = new ArrayList<>();
        if (!arbeidsforholdIder.isEmpty()) {
            if (arbeidsforholdIder.size() > MAKS_ARBEIDSFORHOLD_PER_TENOR_OPPLASTING) {
                AtomicInteger counter = new AtomicInteger();
                arbeidsforholdIder.stream().collect(Collectors.groupingBy(i -> counter.getAndIncrement() / 1000)).forEach((key, value) -> {
                    var bolk = this.lastOppBolk(new ArrayList<>(value));
                    result.addAll(bolk);
                });
            } else {
                result.addAll(this.lastOppBolk(arbeidsforholdIder));
            }
        }
        return result;
    }

    @PostMapping(path = "/arbeidsforhold/slett")
    public void slettArbeidsforhold(@RequestBody List<Long> arbeidsforholdIder) {
        var arbeidsforholdListe = arbeidsforholdIder.stream()
                .map(aaregConsumer::finnArbeidsforhold)
                .toList();
        tenorService.slettArbeidsforhold(arbeidsforholdListe);
    }

    private List<Long> lastOppBolk(List<Long> arbeidsforholdId) {
        var relevanteArbeidsforholdPar = arbeidsforholdId.stream()
                .map(aaregConsumer::finnArbeidsforhold)
                .filter(TenorUtil::arbeidsforholdInnenforBruksperiode)
                .map(tenorUtil::konverterArbeidsforhold)
                .filter(this::kandidatForTenorOpplasting)
                .flatMap(e -> Map.ofEntries(e).entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, HashMap::new));

        tenorConsumer.lastOppArbeidsforhold(relevanteArbeidsforholdPar);

        return relevanteArbeidsforholdPar.keySet().stream()
                .map(Arbeidsforhold::getNavArbeidsforholdId)
                .toList();
    }

    private boolean kandidatForTenorOpplasting(Map.Entry<Arbeidsforhold, no.nav.aareg.tenor.consumer.aareg.domain.dist.v1.Arbeidsforhold> arbeidsforholdPar) {
        return tenorService.erKandidatForTenorOpplasting(arbeidsforholdPar.getValue());
    }
}

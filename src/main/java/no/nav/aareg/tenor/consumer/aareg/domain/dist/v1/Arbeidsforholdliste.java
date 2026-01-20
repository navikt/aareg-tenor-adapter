package no.nav.aareg.tenor.consumer.aareg.domain.dist.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class Arbeidsforholdliste {

    private List<Arbeidsforhold> arbeidsforhold;
}

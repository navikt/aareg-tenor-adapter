package no.nav.aareg.tenor.consumer.aareg.domain.v2;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
@JsonPropertyOrder({
        "fom",
        "tom"
})
public class Bruksperiode {

    private LocalDateTime fom;

    private LocalDateTime tom;

    public boolean erInnenforPeriode(LocalDateTime now) {
        var erFraOgMed = this.getFom().isBefore(now) || this.getFom().equals(now);
        var erTilOgMed = this.getTom() == null || this.getTom().isAfter(now) || this.getTom().equals(now);
        return erFraOgMed && erTilOgMed;
    }
}

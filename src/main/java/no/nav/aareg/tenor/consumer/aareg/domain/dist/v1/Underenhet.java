package no.nav.aareg.tenor.consumer.aareg.domain.dist.v1;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Underenhet extends Enhet implements Arbeidssted {

    @Override
    public String getType() {
        return this.getClass().getSimpleName();
    }
}

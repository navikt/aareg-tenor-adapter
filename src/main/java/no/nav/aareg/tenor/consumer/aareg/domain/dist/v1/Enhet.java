package no.nav.aareg.tenor.consumer.aareg.domain.dist.v1;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode
@ToString
public abstract class Enhet {

    private String ident;

}

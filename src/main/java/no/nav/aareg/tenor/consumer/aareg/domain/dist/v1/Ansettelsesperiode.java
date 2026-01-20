package no.nav.aareg.tenor.consumer.aareg.domain.dist.v1;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Ansettelsesperiode extends Periode {

    private Kodeverksentitet sluttaarsak;

    private Kodeverksentitet varsling;
}

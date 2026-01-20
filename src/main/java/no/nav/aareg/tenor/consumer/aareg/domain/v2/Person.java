package no.nav.aareg.tenor.consumer.aareg.domain.v2;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import static no.nav.aareg.tenor.consumer.aareg.domain.v2.Identtype.FOLKEREGISTERIDENT;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
        "type",
        "identer"
})
public class Person extends Identer
        implements Persontype, Opplysningspliktig, Arbeidssted {

    @Override
    public String getType() {
        return this.getClass().getSimpleName();
    }

    public String getIdent() {
        return getIdent(FOLKEREGISTERIDENT);
    }
}

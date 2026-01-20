package no.nav.aareg.tenor.consumer.aareg.domain.v2;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode
@ToString
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public abstract class Identer {

    private List<Ident> identer;

    public String getIdent(Identtype identtype) {
        return getIdenter().stream()
                .filter(ident -> identtype != null && identtype.equals(ident.getType()))
                .findFirst()
                .map(Ident::getIdent)
                .orElse(null);
    }
}
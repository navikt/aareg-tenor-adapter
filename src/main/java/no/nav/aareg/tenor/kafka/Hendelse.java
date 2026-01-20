package no.nav.aareg.tenor.kafka;

import java.time.LocalDateTime;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.aareg.tenor.consumer.aareg.domain.v2.Arbeidsforhold;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
        "id",
        "endringstype",
        "entitetsendringer",
        "arbeidsforhold",
        "tidsstempel"
})
public class Hendelse {

    private Long id;

    private Endringstype endringstype;

    private Set<Endringsentitet> entitetsendringer;

    private Arbeidsforhold arbeidsforhold;

    private LocalDateTime tidsstempel;

    @JsonProperty("tidsstempel")
    public void setTidsstempelAsString(String tidsstempel) {
        this.tidsstempel = LocalDateTime.parse(tidsstempel);
    }

    public enum Endringstype {
        Opprettelse,
        Endring,
        Sletting
    }

    public enum Endringsentitet {
        Ansettelsesperiode,
        Ansettelsesdetaljer,
        Permisjon,
        Permittering,
        TimerMedTimeloenn,
        Utenlandsopphold
    }
}
package no.nav.aareg.tenor.consumer.aareg.domain.v2;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@JsonPropertyOrder({
        "opprettetTidspunkt",
        "opprettetAv",
        "opprettetKilde",
        "opprettetKildereferanse",
        "endretTidspunkt",
        "endretAv",
        "endretKilde",
        "endretKildereferanse"
})
public class Sporingsinformasjon {

    private LocalDateTime opprettetTidspunkt;

    private String opprettetAv;

    private String opprettetKilde;

    private String opprettetKildereferanse;

    private LocalDateTime endretTidspunkt;

    private String endretAv;

    private String endretKilde;

    private String endretKildereferanse;
}

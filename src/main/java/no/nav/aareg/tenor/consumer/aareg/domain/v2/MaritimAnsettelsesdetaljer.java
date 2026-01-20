package no.nav.aareg.tenor.consumer.aareg.domain.v2;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
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
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
        "fartsomraade",
        "skipsregister",
        "fartoeystype",
        "arbeidstidsordning",
        "ansettelsesform",
        "yrke",
        "antallTimerPrUke",
        "avtaltStillingsprosent",
        "sisteStillingsprosentendring",
        "sisteLoennsendring",
        "rapporteringsmaaneder",
        "sporingsinformasjon"
})
public class MaritimAnsettelsesdetaljer extends Ansettelsesdetaljer {

    public static final String TYPE = "Maritim";

    private Kodeverksentitet fartsomraade;

    private Kodeverksentitet skipsregister;

    private Kodeverksentitet fartoeystype;

    @Override
    public String getType() {
        return TYPE;
    }
}

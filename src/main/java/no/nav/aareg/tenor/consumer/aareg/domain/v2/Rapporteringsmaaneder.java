package no.nav.aareg.tenor.consumer.aareg.domain.v2;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.YearMonth;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
@JsonPropertyOrder({
        "fra",
        "til"
})
public class Rapporteringsmaaneder {

    private YearMonth fra;

    private YearMonth til;
}

package no.nav.aareg.tenor;

import no.nav.aareg.tenor.wiremock.WireMockStubs;
import no.nav.aareg.tenor.wiremock.maskinporten.MaskinportenStub;
import no.nav.aareg.tenor.wiremock.tenor.TenorStub;
import no.nav.aareg.tenor.wiremock.texas.TexasStub;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@WireMockStubs({
        TenorStub.class,
        MaskinportenStub.class,
        TexasStub.class
})
@SpringBootTest(
        classes = {TenorTestApp.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class TenorTest {

}

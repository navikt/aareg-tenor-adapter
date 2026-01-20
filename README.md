# Aareg-tenor-adapter

NB: Dokumentasjonen er delvis utdatert og vil bli oppdatert.

Denne appen tar seg av opplasting av data til [Tenor](https://testdata.skatteetaten.no/web/testnorge/) 
(og [Tenor sitt testmiljø](https://skatt-utv1.sits.no/web/testnorge/)).
Adapteren tilbyr et API hvor man kan fylle inn arbeidsforhold-id'er som skal lastes opp 
(**id = radnummeret i database**).

For hvert enkelt arbeidsforhold vil adapteren gjøre et søk opp mot Tenor for å undersøke om
arbeidsforholdet skal lastes opp; sjekken er om arbeidstaker, arbeidssted og opplysningspliktig
eksisterer i Tenor eller ikke (Nav har også egne syntetiske personer og virksomheter).

Vedlagte [Postman-samling](Tenor.postman_collection.json) beskriver hvordan api-kall gjøres.

API'et er sikret med enkel Basic auth. 
Brukernavn og passord finner man i [appen's Vault](https://vault.adeo.no/ui/vault/secrets/kv%2Fpreprod%2Ffss/show/aareg-tenor-adapter/arbeidsforhold)

# Systemoversikt

![](doc-resources/systemoversikt.png)

Tenor-adapteren består av et API, et "kommandolinjeverktøy", og en kafka-konsument som behandler 
hendelser fra Aa-registeret.

I daglig drift vil tenor-adapteren automatisk laste opp meldinger når den leser en hendelse fra Kafka.
Utover det vil man bruke kommandolinje og api til forskjellige formål:

- Bruk **APIet** for å laste opp eller slette arbeidsforhold som av ulike årsaker ikke ble lastet opp riktig av kafka-konsumenten
- Bruk **kommandolinjen** for å "rullere datasettet" (tenk på det som å "formatere" Tenor) eller for å utføre endringer i konfigurasjonen (nytt søkegrensesnitt, ny nøkkelinformasjon, etc)

## Endring av Tenor-konfigurasjon

Typiske endringer man ønsker å gjøre i Tenor er å enten

- endre søkekriterier (hva man kan søke på), eller å
- endre hva slags nøkkelinformasjon som vises

![](doc-resources/tenorskjermbilde.png)

Kommandolinjeverktøyet tar imot et argument slik at man kan velge om man vil oppdatere nøkkelinformasjon eller søkekriterier.
All konfigurasjonen defineres i [json-filen som beskriver hele konfigurasjonen for Arbeidsforhold-datakilden](src/main/resources/konfigurasjon.json).

**Merk:** Når man legger til eller fjerner nøkkelinformasjon er det også en fordel å endre klassen [ArbeidsforholdInformasjon](src/main/java/no/nav/aareg/tenor/consumer/ArbeidsforholdInformasjon.java)

### Miljøvariabler for kommandolinjekjøring

Følgende miljøvariabler må være satt for å kjøre kommandoene under, og alle kan hentes ut fra adapteren sin "pod":
- MASKINPORTEN_WELL_KNOWN_URL
- MASKINPORTEN_CLIENT_ID
- MASKINPORTEN_CLIENT_JWK
- MASKINPORTEN_SCOPES
- API_URL_TENOR_DATASETT

### For å endre nøkkelinformasjonskonfigurasjonen

Gjør de nødvendige endringene i json-filen, under feltet `soekevisning`. Feks for å legge til yrkeskode:

![](doc-resources/endre-nøkkelinformasjon.png)

Deretter må konfigurasjonen lastes opp til Tenor. Eksempel med Bash-skript fra monorepoet sin "root folder":

```bash
mvn clean compile --projects=:aareg-tenor-adapter exec:java -Dexec.mainClass=no.nav.aareg.tenor.AaregTenorConfigUploader -Dexec.args="visning"
```

### For å endre søkekriteriene

Gjør de nødvendige endringene i json-filen, under feltet `soekekriterier`. Feks for å legge til søk på yrkeskode:

![](doc-resources/endre-søkekriterier.png)

Deretter må konfigurasjonen lastes opp til Tenor. Eksempel med Bash-skript fra monorepoet sin "root folder":

```bash
mvn clean compile --projects=:aareg-tenor-adapter exec:java -Dexec.mainClass=no.nav.aareg.tenor.AaregTenorConfigUploader -Dexec.args="kriterier"
```

### For å rullere datasett

*TODO utbedre dokumentasjon*

## Sekvensdiagrammer

### API

![](doc-resources/api-sequence.svg)

### KafkaConsumer

Her er et par termer som er nyttige å vite når man leser sekvensdiagrammet:

`ack`: Dette betyr at man forteller kafka at meldingen er lest og at man vil motta neste

`nack`: Dette betyr at man ikke lyktes med å prosessere meldingen. I praksis betyr dette at kafka-consumeren vil henge seg opp hvis en melding alltid feiler

`dist-format`: Utility-funksjonene som lager opplastingsformatet som skal til tenor bruker "aareg-dist-api-formatet", så vi må "mappe om" svaret fra aareg services

`bruksperiode`: Hvis "nåtiden" ikke er innenfor arbeidsforholdet sin bruksperiode, har det blitt satt til teknisk historikk og bør ikke lastes opp til Tenor

![](doc-resources/kafkaconsumer-sequence.svg)

## Ekstra

- Lenke til Tenor sin [openapi-dokumentasjon for datalast](https://skatteetaten.github.io/testnorge-tenor-dokumentasjon/api/openapi-mottak-api/?spec=test#patch-/datasett/%7Bdatasett%7D/latest/testdata)
- Epost for Tenor-support: [Tenor@skatteetaten.no](mailto::Tenor@skatteetaten.no)
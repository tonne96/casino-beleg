# Aufgabenplan Softwareengineering-Beleg

Diese Datei ist ein Arbeitsplan für das Casino-Microservice-Projekt. Sie fasst die Anforderungen aus der Belegbeschreibung zusammen und übersetzt sie in konkrete Aufgaben mit Prioritäten.

## Ziel des Projekts

Es soll eine RESTful Microservice-Anwendung fuer ein Online-Casino entstehen. Die Anwendung besteht aus drei Kernservices:

- Banking-Service
- Roulette-Service
- Slots-Service

Jeder Service läuft in einem eigenen Docker-Container und besitzt eine eigene PostgreSQL-Datenbank in einem eigenen Container. Insgesamt entstehen also sechs Container:

- banking-service
- roulette-service
- slots-service
- banking-db
- roulette-db
- slots-db

Die Spielservices dürfen nicht direkt auf die Banking-Datenbank zugreifen. Sie müssen über HTTP mit dem Banking-Service kommunizieren.

## Prioritäten

- P0: Muss zuerst erledigt werden, sonst blockiert es andere Aufgaben.
- P1: Pflichtanforderung fuer eine gute lauffähige Abgabe.
- P2: Wichtig für Bewertung, Qualität und Präsentation.
- P3: Optional oder Verbesserung, wenn Zeit bleibt.

## Teamaufteilung

### Person 1: Banking-Service

Verantwortlich für:

- User-Verwaltung
- Transaktionen
- Kontostandlogik
- Banking-Datenbank
- Banking-Unit-Tests
- Banking-Swagger-Endpunkte

### Person 2: Roulette-Service

Verantwortlich für:

- Roulette-Spielregeln
- Play-Endpunkt
- Roulette-Gewinnberechnung
- Roulette-Spielhistorie
- Roulette-Statistiken
- Roulette-Datenbank
- Roulette-Unit-Tests

### Person 3: Slots-Service

Verantwortlich für:

- Slot-Symbole
- Slot-Gewinnregeln
- Play-Endpunkt
- Slot-Spielhistorie
- Slot-Statistiken
- Slots-Datenbank
- Slots-Unit-Tests

### Person 4: Architektur, Integration, DevOps, Dokumentation

Verantwortlich für:

- gemeinsame Projektstruktur
- Dockerfiles
- docker-compose.yml
- Datenbank-Konfigurationen
- Swagger-Integration prüfen
- Service-zu-Service-Kommunikation vereinheitlichen
- PlantUML-Diagramme
- README
- manueller End-to-End-Testablauf
- Abgabevorbereitung

Wichtig: Tests gehören nicht nur Person 4. Jede Person testet die Klassen, die sie selbst baut.

## Phase 0: Projektgrundlage klaeren

### P0 - Maven-Struktur entscheiden und korrigieren

In der Belegarbeit steht nicht, welche Maven-Struktur genau verwendet werden soll. Es wird nur Maven als Build-Tool gefordert und dass die Anwendung mit Docker + Maven baubar sein muss. Optisch sieht es erstmal nach Multi Module aus, weil es im Root eine pom.xml gibt und in jeden Service auch eine pom.xml. Aber ich wollte diesen Punkt ansprechen, weil in Root das pom.xml noch keine packaging drin steht.

Aufgaben:

- Entscheiden: echtes Maven-Multi-Module-Projekt oder getrennte Maven-Projekte.
- Wenn Multi-Module:
    - Root-pom.xml auf packaging `pom` umstellen.
    - Module eintragen: `banking-service`, `roulette-service`, `slots-service`.
    - gemeinsame Dependencies zentral verwalten.
- Wenn getrennte Projekte:
    - Root-Projekt entweder entfernen oder klar dokumentieren.
    - Jeder Service bleibt eigenständig baubar.
- Fehler in `banking-service/pom.xml` (line 43) beheben: Der Service darf sich nicht selbst als Dependency eintragen.


### P0 - Ports festlegen

Empfohlene Ports:

- Banking: `localhost:8080`
- Roulette: `localhost:8081`
- Slots: `localhost:8082`

Hinweis: Im PDF steht für Roulette und Slots jeweils `8081`. Das geht lokal nicht gleichzeitig ohne Gateway. Deshalb sollte `8082` fuer Slots genutzt und in README/Diagrammen dokumentiert werden.

### P0 - Basisstruktur pro Service anlegen

Jeder Service braucht eine klare Struktur.

Banking-Service, strenger nach MVC + Vertical Slice:

```text
banking-service/
└── src/main/java/.../
    ├── controller/
    │   ├── user/
    │   ├── transaction/
    │   └── stat/
    ├── handler/
    │   ├── user/
    │   ├── transaction/
    │   └── stat/
    ├── model/
    │   ├── entity/
    │   └── dto/
    ├── repository/
    └── view/
```

Spielservices, klassisch geschichtet:

```text
roulette-service/
└── src/main/java/.../
    ├── controller/
    ├── service/
    ├── model/
    ├── repository/
    ├── client/
    └── view/
```

```text
slots-service/
└── src/main/java/.../
    ├── controller/
    ├── service/
    ├── model/
    ├── repository/
    ├── client/
    └── view/
```

## Phase 1: Banking-Service

### P0 - Banking-Domain modellieren

Entities:

- `User`
    - `Long id`
    - `String firstName`
    - `String lastName`
    - `BigDecimal balance`
- `Transaction`
    - `Long id`
    - `Long userId` oder Relation zu `User`
    - `String invoicingParty`
    - `BigDecimal amount`

Regeln:

- IDs als `Long`.
- Geldbetraege als `BigDecimal`.
- Neuer User startet mit Balance `0`.
- Negative oder ungültige Deposit-Betraege ablehnen.
- Transaktionen müssen den Kontostand korrekt verändern.
- Unbekannte Rechnungssteller ablehnen, z. B. alles ausser `roulette-service`, `slots-service`, optional `banking-service`.

### P1 - User-APIs bauen

Pflicht-Endpunkte:

```text
GET    /casino/bank/api/user/{id}
GET    /casino/bank/api/users
POST   /casino/bank/api/user
PUT    /casino/bank/api/user/{user_id}
DELETE /casino/bank/api/user/{user_id}
POST   /casino/bank/api/user/{user_id}/deposit/{amount}/{decimals}
```

Aufgaben:

- Controller für User-Endpunkte bauen.
- Request-DTOs bauen, z. B. `CreateUserRequest`, `UpdateUserRequest`.
- Response/View-Objekte bauen, z. B. `UserView`, `DeletedUserView`.
- Handler/Services für User-Logik bauen.
- Repository für User bauen.
- Fehlerfälle sauber behandeln:
    - `404 Not Found`, wenn User nicht existiert.
    - `400 Bad Request`, wenn Request ungültig ist.

### P1 - Transaction-APIs bauen

Pflicht-Endpunkte:

```text
GET    /casino/bank/api/transactions
GET    /casino/bank/api/transactions/user/{id}
POST   /casino/bank/api/transaction/user/{user_id}
PUT    /casino/bank/api/transaction/{transaction_id}
DELETE /casino/bank/api/transaction/{transaction_id}
```

Aufgaben:

- Controller für Transaction-Endpunkte bauen.
- Request-DTOs bauen, z. B. `CreateTransactionRequest`, `UpdateTransactionRequest`.
- Response/View-Objekte bauen, z. B. `TransactionView`, `DeletedTransactionView`.
- Handler/Services fuer Transaction-Logik bauen.
- Repository für Transaktionen bauen.
- Beim Erstellen einer Transaktion Balance des Users anpassen.
- Bei Ändern/Löschen einer Transaktion klären und dokumentieren, wie die Balance korrigiert wird.

### P2 - Banking-Stat-Slice vorbereiten ? (weil in Banking service keine konkret vorgeschriebene Banking-Stats-API genannt wurde. In Roulette und Slot aber schon. Dort sind Stats Endpunkte ausdrücklich genannt)

Das PDF nennt im Banking-Service die Subdomain `Stat`. Es gibt aber keine konkret ausformulierten Banking-Stat-Endpunkte in der Funktionsliste.

Aufgaben:

- Minimal eine interne Struktur für `stat` vorbereiten.
- Optional einfache Banking-Statistiken anbieten, z. B. Anzahl User, Anzahl Transaktionen, Summe aller Balances.
- Falls keine externen Banking-Stat-Endpunkte gebaut werden, Entscheidung in README dokumentieren.

## Phase 2: Roulette-Service

### P0 - Roulette-Regeln festlegen

Empfohlener einfacher Umfang:

- Zahlenbereich: `0` bis `36`
- Bet-Typen:
    - `COLOR`: `RED` oder `BLACK`
    - `PARITY`: `EVEN` oder `ODD`
    - `NUMBER`: konkrete Zahl `0` bis `36`

Aufgaben:

- Roulette-Zahlen/Farben definieren.
- Auszahlungsregeln definieren:
    - Farbe: 1:1
    - Gerade/Ungerade: 1:1
    - Zahl: 35:1
- Umgang mit `0` sauber festlegen.
- Ungueltige Wetten ablehnen.

### P1 - Roulette-Play-Endpunkt bauen

Pflicht-Endpunkt:

```text
POST /casino/roulette/api/play
```

Request-Beispiel:

```json
{
  "user": 5,
  "betAmount": 10.00,
  "betType": "COLOR",
  "betValue": "RED"
}
```

Response-Beispiel:

```json
{
  "user": 5,
  "winning": true,
  "amount": 10.00,
  "ballPosition": 18,
  "betType": "COLOR",
  "betValue": "RED"
}
```

Aufgaben:

- `PlayRouletteRequest` bauen.
- `RoulettePlayView` bauen.
- Zufallszahl von `0` bis `36` erzeugen.
- Gewinn/Verlust berechnen.
- Ergebnis in Roulette-DB speichern.
- Banking-Service aufrufen:
    - User existiert?
    - genug Guthaben?
    - Transaktion buchen.
- Fehlerfaelle:
    - `404`, wenn User nicht existiert.
    - `400`, wenn Request ungültig ist.

### P1 - Roulette-Info-Endpunkte bauen

Pflicht-Endpunkte:

```text
GET /casino/roulette/api/info/rules
GET /casino/roulette/api/info/chances
```

Aufgaben:

- Regeln als Text oder strukturiertes JSON liefern.
- Gewinnchancen und Auszahlungsformeln sichtbar machen.
- Ziel der Aufgabe beachten: Risiken und Wahrscheinlichkeiten transparent machen.

### P1 - Roulette-Stats-Endpunkte bauen

Pflicht-Endpunkte:

```text
GET    /casino/roulette/api/stats
GET    /casino/roulette/api/stats/user/{user_id}
GET    /casino/roulette/api/stats/games
GET    /casino/roulette/api/stat/{game_id}
DELETE /casino/roulette/api/stat/{game_id}
```

Aufgaben:

- `RouletteGame` Entity bauen.
- `RouletteGameRepository` bauen.
- Gesamtstatistik berechnen.
- User-Statistik berechnen.
- Alle Spiele anzeigen.
- Einzelnes Spiel anzeigen.
- Einzelnes Spiel löschen.
- `404`, wenn Spiel oder User-Statistik nicht existiert.

## Phase 3: Slots-Service

### P0 - Slot-Regeln festlegen

Empfohlener einfacher Umfang:

- Drei Walzen.
- Symbole, z. B.:
    - `CHERRY`
    - `LEMON`
    - `BELL`
    - `BAR`
    - `SEVEN`

Moegliche Gewinnregeln:

- Drei gleiche Symbole: Gewinn.
- Drei `SEVEN`: Jackpot.
- Zwei gleiche Symbole: kleiner Gewinn, optional.
- Sonst Verlust.

Aufgaben:

- `SlotSymbol` enum bauen.
- Gewinnregeln definieren.
- Auszahlungen festlegen.
- Ungueltige Einsaetze ablehnen.

### P1 - Slots-Play-Endpunkt bauen

Pflicht-Endpunkt:

```text
POST /casino/slots/api/play
```

Request-Beispiel:

```json
{
  "user": 5,
  "betAmount": 10.00
}
```

Response-Beispiel:

```json
{
  "user": 5,
  "winning": false,
  "amount": -10.00,
  "slotStates": ["CHERRY", "LEMON", "SEVEN"]
}
```

Aufgaben:

- `PlaySlotsRequest` bauen.
- `SlotsPlayView` bauen.
- Slot-Symbole zufaellig erzeugen.
- Gewinn/Verlust berechnen.
- Ergebnis in Slots-DB speichern.
- Banking-Service aufrufen:
    - User existiert?
    - genug Guthaben?
    - Transaktion buchen.
- Fehlerfaelle:
    - `404`, wenn User nicht existiert.
    - `400`, wenn Request ungueltig ist.

### P1 - Slots-Info-Endpunkte bauen

Pflicht-Endpunkte:

```text
GET /casino/slots/api/info/rules
GET /casino/slots/api/info/chances
```

Aufgaben:

- Slot-Regeln erklaeren.
- Gewinnchancen und Auszahlungen darstellen.
- Beispiel: Bei 5 Symbolen und 3 Walzen gibt es `5^3 = 125` moegliche Ergebnisse.

### P1 - Slots-Stats-Endpunkte bauen

Pflicht-Endpunkte:

```text
GET    /casino/slots/api/stats
GET    /casino/slots/api/stats/user/{user_id}
GET    /casino/slots/api/stats/games
GET    /casino/slots/api/stat/{game_id}
DELETE /casino/slots/api/stat/{game_id}
```

Aufgaben:

- `SlotGame` Entity bauen.
- `SlotGameRepository` bauen.
- Gesamtstatistik berechnen.
- User-Statistik berechnen.
- Alle Spiele anzeigen.
- Einzelnes Spiel anzeigen.
- Einzelnes Spiel loeschen.
- `404`, wenn Spiel oder User-Statistik nicht existiert.

## Phase 4: Service-Kommunikation

### P0 - Banking-Client für Spielservices bauen

Roulette und Slots brauchen jeweils einen Client, um den Banking-Service per HTTP aufzurufen.

Aufgaben:

- Gemeinsame Schnittstelle fuer Banking-Aufrufe definieren.
- In Roulette einen `BankingClient` bauen.
- In Slots einen `BankingClient` bauen.
- Konfigurierbare Banking-URL verwenden, nicht hart `localhost` im Code.
- In Docker intern z. B. `http://banking-service:8080` verwenden.
- Lokal z. B. `http://localhost:8080` verwenden.

Mindestens benötigte Banking-Aufrufe:

- User abrufen/pruefen.
- Transaktion fuer User erstellen.

Optional:

- Kontostand explizit pruefen.
- Eigener Banking-Endpunkt fuer Balance-Check, falls noetig.

### P1 - Fehlerfälle bei Service-Kommunikation klaeren

Aufgaben:

- Was passiert, wenn Banking nicht erreichbar ist?
- Was passiert, wenn User nicht existiert?
- Was passiert, wenn User nicht genug Guthaben hat?
- Was passiert, wenn Banking eine Transaktion ablehnt?
- Antworten fuer Swagger/Clients sauber definieren.

## Phase 5: Datenbanken und Docker

### P0 - Datenbank-Konfiguration pro Service

Jeder Service braucht eigene DB-Zugangsdaten.

Aufgaben:

- `application.properties` oder `application.yml` pro Service konfigurieren.
- Datasource-URL, User, Passwort ueber Environment-Variablen ermoeglichen.
- JPA/Hibernate konfigurieren.
- Tabellen aus Entities erzeugen lassen, z. B. mit `spring.jpa.hibernate.ddl-auto=update`.

Empfohlene Datenbanken:

- `banking_db`
- `roulette_db`
- `slots_db`

### P0 - Dockerfiles bauen

Je Service ein Dockerfile:

- `banking-service/Dockerfile`
- `roulette-service/Dockerfile`
- `slots-service/Dockerfile`

Aufgaben:

- Maven Build im Image oder Jar vorher bauen.
- Spring-Boot-Jar starten.
- Port intern `8080` verwenden.

### P0 - docker-compose.yml bauen

Eine zentrale `docker-compose.yml` im Root.

Muss enthalten:

- `banking-service`
- `roulette-service`
- `slots-service`
- `banking-db`
- `roulette-db`
- `slots-db`

Aufgaben:

- Service-Container bauen.
- PostgreSQL-Container starten.
- Ports mappen:
    - Banking: `8080:8080`
    - Roulette: `8081:8080`
    - Slots: `8082:8080`
- Datenbank-Environment-Variablen setzen.
- `depends_on` fuer Datenbanken und Banking-Service setzen.
- Gemeinsames Docker-Netzwerk nutzen.

## Phase 6: Swagger / OpenAPI

### P1 - Swagger für jeden Service einbauen

Aufgaben:

- Swagger/OpenAPI Dependency in jeden Service eintragen.
- Swagger UI für jeden Service erreichbar machen:
    - Banking: `http://localhost:8080/swagger-ui.html`
    - Roulette: `http://localhost:8081/swagger-ui.html`
    - Slots: `http://localhost:8082/swagger-ui.html`
- Controller und DTOs so gestalten, dass Swagger sinnvolle Request/Response-Modelle zeigt.

### P2 - Swagger-Dokumentation verbessern

Aufgaben:

- Endpunkte mit Beschreibungen versehen.
- Request-Body-Beispiele hinzufuegen.
- Fehlercodes dokumentieren.
- Service-Namen und API-Titel setzen.

## Phase 7: Tests

### P0 - Teststrategie festlegen

Pflicht laut PDF:

- JUnit für Unit-Tests.
- Mockito für Mocking.
- Jede Klasse möglichst mit eigener Testklasse.
- Path-Coverage und Blackbox-Testing.
- Randomisierte Tests.
- Grenzwerte und Extremwerte testen.

### P1 - Banking-Unit-Tests

Testfaelle:

- User erstellen startet mit Balance `0`.
- User lesen erfolgreich.
- Nicht existierender User ergibt Fehler.
- User aktualisieren.
- User löschen.
- Deposit mit gültigem Betrag.
- Deposit mit negativem Betrag.
- Deposit mit ungültigen Decimals.
- Transaktion erstellt Balance-Aenderung.
- Unbekannter Rechnungssteller wird abgelehnt.
- Transaktion für nicht existierenden User wird abgelehnt.

### P1 - Roulette-Unit-Tests

Testfaelle:

- Zufallszahl ist immer zwischen `0` und `36`.
- Gewinn bei richtiger Farbe.
- Verlust bei falscher Farbe.
- Gewinn bei richtiger Parity.
- Verlust bei falscher Parity.
- Gewinn bei richtiger Zahl.
- Ungueltige Zahl wird abgelehnt.
- Negativer Einsatz wird abgelehnt.
- BankingClient wird gemockt.
- Spiel wird gespeichert, wenn Banking erfolgreich ist.

### P1 - Slots-Unit-Tests

Testfaelle:

- Es werden genau drei Slot-Symbole erzeugt.
- Jedes Symbol ist gueltig.
- Drei gleiche Symbole gewinnen.
- Jackpot wird korrekt berechnet.
- Verlust wird korrekt berechnet.
- Negativer Einsatz wird abgelehnt.
- BankingClient wird gemockt.
- Spiel wird gespeichert, wenn Banking erfolgreich ist.

### P2 - Manueller End-to-End-Testablauf

Nicht zwingend als automatisierter Test gefordert, aber sehr empfehlenswert für README und Präsentation.

Ablauf:

1. Docker Compose starten.
2. Banking Swagger oeffnen.
3. User erstellen.
4. Geld einzahlen.
5. Roulette Swagger oeffnen.
6. Roulette-Spiel fuer diesen User ausfuehren.
7. Banking-Transaktionen pruefen.
8. Banking-Kontostand pruefen.
9. Roulette-Statistik pruefen.
10. Slots Swagger oeffnen.
11. Slot-Spiel fuer diesen User ausfuehren.
12. Banking-Transaktionen erneut pruefen.
13. Slots-Statistik pruefen.

## Phase 8: PlantUML-Dokumentation

### P1 - Vier Pflichtdiagramme erstellen

Pflichtdiagramme:

1. Gesamtsystem mit allen sechs Containern.
2. Banking-Service intern.
3. Roulette-Service intern.
4. Slots-Service intern.

### P1 - Gesamtsystemdiagramm

Soll zeigen:

- Client oder API-User.
- Banking-Service.
- Roulette-Service.
- Slots-Service.
- Banking-DB.
- Roulette-DB.
- Slots-DB.
- Kommunikation:
    - Roulette -> Banking
    - Slots -> Banking
    - jeder Service -> eigene DB

### P1 - Banking-Service-Diagramm

Soll zeigen:

- UserController
- TransactionController
- StatController
- UserHandler
- TransactionHandler
- StatHandler
- UserRepository
- TransactionRepository
- User Entity
- Transaction Entity
- View/DTO-Objekte

### P1 - Roulette-Service-Diagramm

Soll zeigen:

- RouletteGameController
- RouletteInfoController
- RouletteStatsController
- RouletteGameService
- RouletteStatsService
- BankingClient
- RouletteGameRepository
- RouletteGame Entity
- Request/View-Objekte

### P1 - Slots-Service-Diagramm

Soll zeigen:

- SlotsGameController
- SlotsInfoController
- SlotsStatsController
- SlotsGameService
- SlotsStatsService
- BankingClient
- SlotGameRepository
- SlotGame Entity
- Request/View-Objekte

## Phase 9: README und Abgabe

### P1 - README ausbauen

README muss enthalten:

- Projektbeschreibung.
- Ziel der Anwendung.
- Architekturuebersicht.
- Services und Ports.
- Voraussetzungen.
- Startanleitung mit Docker Compose.
- Swagger-URLs.
- Beispielrequests.
- Testanleitung.
- Autorenangaben.
- Lizenz, optional Creative Commons.

### P1 - Docker-Images exportieren

Pflicht laut PDF:

- Drei Service-Images als `.tar` Dateien.
- Gemeinsam verpackt in einer `.zip` Datei.

Beispiel:

```bash
docker save -o banking-service.tar banking-service:latest
docker save -o roulette-service.tar roulette-service:latest
docker save -o slots-service.tar slots-service:latest
```

Danach:

```text
docker-images.zip
├── banking-service.tar
├── roulette-service.tar
└── slots-service.tar
```

### P1 - Code als ZIP abgeben

Abgabe sollte enthalten:

- kompletter Quellcode
- README
- docker-compose.yml
- Dockerfiles
- PlantUML-Dateien
- Maven Wrapper



## Grobe Reihenfolge für die Umsetzung

1. Maven-Struktur reparieren.
2. Ordnerstruktur pro Service sauber anlegen.
3. Banking User + Transaction Grundfunktion bauen.
4. Banking mit PostgreSQL verbinden.
5. Banking Swagger bereitstellen.
6. Banking Unit-Tests schreiben.
7. Roulette-Regeln und Play-Endpunkt bauen.
8. Roulette-DB und Stats bauen.
9. Roulette mit Banking verbinden.
10. Roulette Tests schreiben.
11. Slots-Regeln und Play-Endpunkt bauen.
12. Slots-DB und Stats bauen.
13. Slots mit Banking verbinden.
14. Slots Tests schreiben.
15. Dockerfiles bauen.
16. docker-compose.yml bauen.
17. Gesamtsystem starten und manuell testen.
18. PlantUML-Diagramme erstellen.
19. README fertigstellen.
20. Docker-Images exportieren.
21. Abgabe-ZIP vorbereiten.

## Definition of Done

Ein Feature gilt erst als fertig, wenn:

- der Endpunkt implementiert ist,
- Request- und Response-Objekte sauber sind,
- Fehlerfaelle behandelt werden,
- Swagger den Endpunkt korrekt anzeigt,
- Unit-Tests existieren,
- Tests erfolgreich laufen,

## Offene Entscheidungen

Diese Punkte sollte das Team bewusst entscheiden und dokumentieren:

- Multi-Module-Maven oder getrennte Service-Projekte?
- Finale Ports für alle Services.
- Exakte Roulette-Wettarten.
- Exakte Slot-Gewinnregeln.
- Wie wird bei Ändern/Löschen einer Transaktion die Balance korrigiert?
- Welche Rechnungssteller sind im Banking-Service erlaubt?
- Wie wird "nicht genug Guthaben" behandelt?
- Werden Banking-Stat-Endpunkte gebaut oder nur intern vorbereitet?
- Wie detailliert werden Swagger-Beschreibungen gepflegt?
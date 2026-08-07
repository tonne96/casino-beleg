# Casino Microservices

## Projektbeschreibung

Dieses Projekt modelliert ein risikofreies Online-Casino als RESTful
Microservice-Anwendung. Benutzer koennen ein Konto anlegen, Guthaben verwalten
und einzelne Runden Roulette oder Slots spielen. Spielhistorien, Statistiken,
Regeln und Gewinnchancen machen die Ergebnisse nachvollziehbar.

 Im Mittelpunkt stehen eine saubere Architektur,
SOLID-Prinzipien, robuste Domain-Modelle, automatisierte Tests und die
Kommunikation zwischen unabhaengigen Services.

## Architektur

Die Anwendung besteht aus drei Spring-Boot-Services und drei getrennten
PostgreSQL-Datenbanken:

| Service | Aufgabe | HTTP-Port | Datenbank-Port |
|---|---|---:|---:|
| Banking-Service | Benutzer, Guthaben und Transaktionen | `8080` | `5432` |
| Roulette-Service | Roulette-Runden, Regeln, Historie und Statistiken | `8081` | `5433` |
| Slots-Service | Slot-Runden, Regeln, Historie und Statistiken | `8082` | `5434` |

Jeder Service greift ausschliesslich auf seine eigene Datenbank zu. Roulette
und Slots verifizieren Benutzer und buchen Spielergebnisse ueber die HTTP-API
des Banking-Service. Sie greifen niemals direkt auf die Banking-Datenbank zu.

```text
Client
  |-- Banking-Service  <--> banking_db
  |-- Roulette-Service <--> roulette_db
  |        `--------------> Banking-Service
  `-- Slots-Service    <--> slots_db
           `--------------> Banking-Service
```

Der Slots-Service verwendet lokal bewusst Port `8082`. In der
Belegbeschreibung ist fuer Roulette und Slots Port `8081` angegeben; zwei
gleichzeitig laufende Services koennen jedoch nicht denselben Host-Port
verwenden.

## Technologie-Stack

- Java 25
- Spring Boot 4
- Maven Multi-Module-Projekt mit Maven Wrapper
- PostgreSQL 17
- Docker und Docker Compose
- Swagger UI / OpenAPI
- JUnit und Mockito
- JaCoCo fuer Testabdeckung
- PlantUML fuer die Architekturdokumentation

## Voraussetzungen

Fuer den empfohlenen Start der Gesamtanwendung werden nur folgende Programme
benoetigt:

- Git
- Docker mit Docker Compose

Maven, Java und PostgreSQL muessen fuer den Docker-Betrieb nicht separat
installiert werden. Fuer einen lokalen Maven-Build ausserhalb von Docker wird
zusaetzlich ein Java-25-JDK benoetigt. Maven selbst wird ueber `./mvnw`
bereitgestellt.

## Installation und Start

Repository herunterladen und in das Projektverzeichnis wechseln:

```bash
git clone <REPOSITORY-URL>
cd casino-beleg
```

Alle sechs Container bauen und starten:

```bash
docker compose up --build
```

Der erste Build kann mehrere Minuten dauern. Der Status der Container kann in
einem zweiten Terminal geprueft werden:

```bash
docker compose ps
```

Die Anwendung beenden:

```bash
docker compose down
```

Anwendungscontainer, Datenbankcontainer und gespeicherte Daten vollstaendig
loeschen:

```bash
docker compose down -v
```

**Achtung:** Durch `-v` werden die drei Docker-Datenbank-Volumes und damit alle
Benutzer, Transaktionen und Spielhistorien geloescht.

## Swagger und OpenAPI

Nach dem Start stehen drei getrennte Swagger-Oberflaechen zur Verfuegung:

| Service | Swagger UI | OpenAPI-Dokument |
|---|---|---|
| Banking | <http://localhost:8080/swagger-ui.html> | <http://localhost:8080/api-docs> |
| Roulette | <http://localhost:8081/swagger-ui.html> | <http://localhost:8081/api-docs> |
| Slots | <http://localhost:8082/swagger-ui.html> | <http://localhost:8082/api-docs> |

Swagger ist die vollstaendige technische API-Dokumentation. Dort koennen alle
Requests direkt ausgefuehrt und die jeweiligen Response-Bodys sowie
HTTP-Statuscodes geprueft werden.

## Beispielhafter End-to-End-Ablauf mit Swagger

Die folgenden Schritte zeigen das Zusammenspiel von Banking- und Slots-Service.
Alle Requests werden direkt ueber die jeweilige Swagger UI ausgefuehrt. Dazu
wird der gewuenschte Endpunkt aufgeklappt und ueber **Try it out** und
**Execute** gestartet.

Die vom Banking-Service zurueckgegebene Benutzer-ID muss in den nachfolgenden
Requests eingesetzt werden. In diesem Beispiel wird angenommen, dass der neue
Benutzer die ID `1` erhaelt.

### 1. Benutzer erstellen

1. Banking-Swagger unter <http://localhost:8080/swagger-ui.html> oeffnen.
2. `POST /casino/bank/api/user` auswaehlen.
3. Folgenden Request-Body eintragen und ausfuehren:

```json
{
  "first_name": "Max",
  "last_name": "Mustermann"
}
```

Die zurueckgegebene `id` fuer die weiteren Schritte merken.

### 2. Guthaben einzahlen

Im Banking-Swagger den Endpunkt
`POST /casino/bank/api/user/{user_id}/deposit/{amount}/{decimals}` ausfuehren.

Fuer eine Einzahlung von `100.00` auf Benutzer `1` werden folgende Werte
eingetragen:

| Parameter | Wert |
|---|---:|
| `user_id` | `1` |
| `amount` | `100` |
| `decimals` | `0` |

Danach sollte die Response fuer den Benutzer einen Kontostand von `100.00`
anzeigen.

### 3. Slot-Runde spielen

1. Slots-Swagger unter <http://localhost:8082/swagger-ui.html> oeffnen.
2. `POST /casino/slots/api/play` auswaehlen.
3. Folgenden Request-Body eintragen und ausfuehren:

```json
{
  "user": 1,
  "betAmount": 10.00
}
```

Die Response enthaelt unter anderem die Symbole, den Multiplikator, den
Netto-Betrag und die gespeicherte `gameId`.

### 4. Ergebnis kontrollieren

Im Banking-Swagger werden anschliessend folgende Endpunkte geprueft:

- `GET /casino/bank/api/user/{id}` fuer den neuen Kontostand
- `GET /casino/bank/api/transactions/user/{id}` fuer die Slots-Transaktion

Im Slots-Swagger werden folgende Endpunkte geprueft:

- `GET /casino/slots/api/stats/user/{user_id}` fuer die Benutzerstatistik
- `GET /casino/slots/api/stats/games` fuer die gespeicherte Spielrunde

Der gleiche Ablauf kann mit dem Roulette-Service ueber dessen Swagger UI
durchgefuehrt werden.

## Banking-Service

Der Banking-Service ist die zentrale Datenquelle des Casino-Systems. Er
verwaltet Benutzer, Guthaben und Transaktionen. Roulette- und Slots-Service
kommunizieren ausschliesslich ueber HTTP mit ihm; ein direkter Datenbankzugriff
von aussen findet nicht statt.

### Architektur- und Designentscheidungen

Der Service folgt der **Vertikal-Slice-Architektur** mit den Sub-Domainen
`User` und `Transaction`. Controller, Handler und Views sind pro Sub-Domaine
getrennt. Controller und Handler haengen von Interfaces ab
(`IUserHandler`, `ITransactionHandler` usw.), nicht von konkreten
Implementierungen — das folgt dem **Dependency-Inversion-Prinzip** und erlaubt
das isolierte Testen mit Mockito.

Da Sub-Domainen laut Vertikal-Slice-Architektur nicht direkt miteinander
kommunizieren duerfen, bucht der `TransactionHandler` Kontostandsaenderungen
ueber einen **HTTP-Self-Call** an den eigenen User-Endpunkt (`RestTemplate`).

### Banking-Endpunkte

| Methode | Pfad | Beschreibung |
|---|---|---|
| `GET` | `/casino/bank/api/user/{id}` | Einen Benutzer abrufen |
| `GET` | `/casino/bank/api/users` | Alle Benutzer abrufen |
| `POST` | `/casino/bank/api/user` | Einen Benutzer anlegen |
| `PUT` | `/casino/bank/api/user/{user_id}` | Einen Benutzer aktualisieren |
| `DELETE` | `/casino/bank/api/user/{user_id}` | Einen Benutzer loeschen |
| `POST` | `/casino/bank/api/user/{user_id}/deposit/{amount}/{decimals}` | Guthaben einzahlen |
| `GET` | `/casino/bank/api/transactions` | Alle Transaktionen abrufen |
| `GET` | `/casino/bank/api/transactions/user/{id}` | Transaktionen eines Benutzers abrufen |
| `POST` | `/casino/bank/api/transaction/user/{user_id}` | Transaktion anlegen und Kontostand anpassen |
| `PUT` | `/casino/bank/api/transaction/{transaction_id}` | Transaktion aktualisieren |
| `DELETE` | `/casino/bank/api/transaction/{transaction_id}` | Transaktion loeschen |

### Teststrategie

| Schicht | Testart | Abhaengigkeiten |
|---|---|---|
| `User`, `Transaction` | Unit-Test ohne Mock | keine |
| `UserHandler`, `TransactionHandler` | Unit-Test mit Mockito | Repository und RestTemplate werden gemockt |
| `UserController`, `TransactionController` | Unit-Test mit Mockito | Handler-Interface wird gemockt |

Eingabewerte werden mit `Random` variiert. Zusaetzlich werden `null`, leere
Strings, negative Betraege und nicht vorhandene IDs explizit geprueft.

## Roulette-Service

> Dieser Abschnitt wird vom verantwortlichen Teammitglied ergaenzt. Er soll
> insbesondere den Verantwortungsbereich, den internen Aufbau, die
> Roulette-Regeln, fachliche Entscheidungen und die Teststrategie des
> Roulette-Service erklaeren.

## Slots-Service

### Verantwortungsbereich

Der Slots-Service ist fuer folgende Aufgaben verantwortlich:

- Benutzer und Guthaben ueber den Banking-Service pruefen
- drei zufaellige Walzensymbole erzeugen
- Gewinn, Auszahlungsmultiplikator und Netto-Betrag berechnen
- das Netto-Ergebnis beim Banking-Service buchen
- die gespielte Runde in der Slots-Datenbank speichern
- Regeln und mathematische Gewinnchancen bereitstellen
- Gesamtstatistiken, Benutzerstatistiken und Spielhistorien bereitstellen

### Architektur- und Designentscheidungen

Der Slots-Service folgt einer geschichteten Architektur. Die Klassen sind nach
ihrer Verantwortung getrennt und kommunizieren nur ueber klar definierte
Schnittstellen:

```text
Controller
    |
    v
Play-, Game-, Info- und Stats-Handler
    |                 |
    v                 v
Banking-Client     Factory und Repository
    |                 |
    v                 v
Banking-API       SlotGame-Entity und Slots-Datenbank
```

#### Duenne Controller

Die Controller nehmen HTTP-Requests entgegen, rufen den passenden Handler auf
und uebersetzen Erfolg oder Fehler in HTTP-Statuscodes. Sie enthalten keine
Spiel-, Banking- oder Datenbanklogik.

Der komplette Anwendungsfall einer Slot-Runde liegt im `PlaySlotsHandler`.
Dadurch bleibt der Ablauf auch ohne Webserver testbar und eine Aenderung der
REST-Darstellung beeinflusst die Spiellogik nicht.

#### Handler mit klaren Verantwortungen

Die Logik wurde nicht in einer einzigen grossen Klasse gesammelt:

| Komponente | Verantwortung |
|---|---|
| `PlaySlotsHandler` | Vollstaendigen Spielablauf koordinieren |
| `SlotGameHandler` | Symbole erzeugen und eine Runde fachlich auswerten |
| `SlotGameHistoryHandler` | Spielhistorie speichern, lesen und loeschen |
| `SlotInfoHandler` | Regeln und mathematische Chancen bereitstellen |
| `SlotStatsHandler` | Gesamt- und Benutzerstatistiken berechnen |

Diese Aufteilung folgt dem **Single-Responsibility-Prinzip**: Jede Klasse hat
einen klaren Grund, geaendert zu werden. Eine neue Auszahlungsregel betrifft
beispielsweise den Game-Handler, waehrend eine neue Statistik den Stats-Handler
betrifft.

Die Handler sind mit Spring `@Service` annotiert. Sie sind damit technisch die
Service-Komponenten des Slots-Service, auch wenn das Package fachlich
`handler` heisst und kein zusaetzliches `service`-Package existiert.

#### Interfaces und Dependency Injection

Fuer Klassen mit Logik oder externen Abhaengigkeiten existieren Interfaces, zum
Beispiel `IPlaySlotsHandler`, `ISlotGameHandler`, `ISlotGameHistoryHandler`,
`ISlotInfoHandler`, `ISlotStatsHandler`, `IBankingClient` und
`ISlotGameFactory`.

Controller und Handler haengen von diesen Vertraegen ab und nicht direkt von
einer konkreten Implementierung. Spring stellt die passende `@Service`-
Implementierung ueber Constructor Injection bereit. Das folgt dem
**Dependency-Inversion-Prinzip** und hat drei praktische Vorteile:

- Implementierungen koennen ausgetauscht werden, ohne Aufrufer umzubauen.
- Abhaengigkeiten sind im Konstruktor sichtbar und koennen nicht vergessen
  werden.
- Unit-Tests koennen die Interfaces mit Mockito ersetzen und eine Klasse
  isoliert testen.

Das `I`-Praefix ist eine bewusste Projektkonvention, damit Interfaces sofort am
Dateinamen erkennbar sind. In vielen Java-Projekten wird auf dieses Praefix
verzichtet; fuer dieses Projekt wurde die explizite Kennzeichnung im Team und
nach Ruecksprache mit dem Dozenten bevorzugt.

Records wie `PlaySlotsRequest` und `SlotsPlayView` erhalten dagegen kein
Interface. Sie transportieren Daten und besitzen keine austauschbare
Implementierung. Auch die JPA-Entity `SlotGame` ist ein konkretes Domain-Objekt.
Ein Interface ohne unterschiedliches Verhalten wuerde dort nur zusaetzliche
Komplexitaet erzeugen.

#### Bezug zu den SOLID-Prinzipien

| Prinzip | Umsetzung im Slots-Service |
|---|---|
| **S - Single Responsibility** | Game-, Play-, History-, Info- und Statistiklogik liegen in getrennten Klassen. |
| **O - Open/Closed** | Neue Implementierungen koennen hinter vorhandenen Interfaces ergaenzt werden, ohne Controller oder Aufrufer umzubauen. |
| **L - Liskov Substitution** | Aufrufer arbeiten nur mit dem jeweiligen Interface-Vertrag; Implementierungen und Test-Doubles muessen diesen Vertrag erfuellen. |
| **I - Interface Segregation** | Es existieren mehrere kleine, fachlich fokussierte Interfaces statt einer grossen Gesamtschnittstelle. |
| **D - Dependency Inversion** | High-Level-Ablaufe wie `PlaySlotsHandler` haengen von Interfaces fuer Game, Banking und History ab. |

#### Banking-Client als Systemgrenze

Der `BankingClient` kapselt die gesamte HTTP-Kommunikation mit dem
Banking-Service. Die restliche Slots-Logik muss weder URLs noch HTTP-Details
oder das interne Banking-Datenmodell kennen.

`BankingUserView` und `BankingTransactionRequest` liegen bewusst im
Slots-Service. Microservices teilen keine Java-Klassen miteinander, sondern nur
ihren HTTP-Vertrag. Dadurch kann der Banking-Service intern geaendert werden,
solange sein JSON-Vertrag stabil bleibt.

Die Basis-URL wird ueber `BANKING_SERVICE_URL` konfiguriert. Lokal zeigt sie auf
`localhost:8080`, innerhalb von Docker auf `http://banking-service:8080`.
Dadurch steht keine umgebungsabhaengige URL fest im Java-Code.

#### Factory zwischen Simulator und Entity

`SlotGameResult` ist das kurzfristige Ergebnis des Game-Simulators.
`SlotGame` ist dagegen die persistierbare JPA-Entity fuer die Datenbank. Die
`SlotGameFactory` uebersetzt das Simulator-Ergebnis in die einzelnen Felder der
Entity und setzt den Spielzeitpunkt.

Diese Trennung verhindert, dass die Entity den Game-Simulator oder dessen
Ergebnisformat kennen muss. Wenn sich das Simulator-Ergebnis spaeter aendert,
muss vor allem die Factory angepasst werden und nicht automatisch die
Persistenzklasse.

Die fachliche Erzeugung von `SlotGame` geschieht kontrolliert ueber
`SlotGame.create(...)`; der fachliche Konstruktor ist privat. Dort wird die
Domain-Integritaet geprueft. Der zusaetzliche parameterlose `protected`-
Konstruktor existiert ausschliesslich, weil JPA ihn zum Laden gespeicherter
Entities benoetigt.

#### Repository als Persistenzgrenze

`IGameResultRepository` erweitert `JpaRepository<SlotGame, Long>` und kapselt
den direkten Datenbankzugriff. Controller und Spiellogik verwenden das
Repository nicht direkt. Der `SlotGameHistoryHandler` bildet die fachliche
Grenze zwischen Anwendungslogik und Persistenz.

Die Statistiken werden aus der gespeicherten Spielhistorie berechnet. Dadurch
existiert nur eine Quelle der Wahrheit und keine zweite Statistik-Tabelle, die
mit der Historie synchron gehalten werden muesste. Das Einlesen aller Runden
ist fuer den Umfang dieses Belegprojekts ausreichend; bei sehr grossen
Datenmengen waeren aggregierende Datenbankabfragen die naechste Optimierung.

#### DTOs statt Entities in der REST-API

Request- und Response-Records im Package `view` trennen den aeusseren
JSON-Vertrag von den internen Domain- und Datenbankklassen. Die JPA-Entity wird
nicht direkt an Clients ausgegeben. Damit koennen Datenbankfelder geaendert
werden, ohne unabsichtlich die REST-API zu veraendern oder interne Informationen
zu veroeffentlichen.

#### Robuste Domain-Daten

- IDs werden entsprechend der Belegvorgabe als `Long` gespeichert.
- Geldbetraege verwenden `BigDecimal`, damit keine binaeren
  Gleitkomma-Rundungsfehler wie bei `double` entstehen.
- Slot-Symbole werden als `enum` modelliert und mit `EnumType.STRING`
  gespeichert. Dadurch sind nur gueltige Werte moeglich und die
  Datenbankeintraege bleiben lesbar.
- Request, Ergebnis, Factory und Entity validieren ihre jeweils verantworteten
  Eingaben, sodass keine unvollstaendige Spielrunde gespeichert werden soll.

#### Netto-Buchung und Konsistenz zwischen Services

Pro Runde wird genau eine Banking-Transaktion mit dem fertigen Netto-Ergebnis
erstellt. Das reduziert die Anzahl verteilter HTTP-Aufrufe und verhindert einen
Zwischenzustand, in dem der Einsatz bereits abgebucht, eine Auszahlung aber
noch nicht gebucht wurde.

Banking und Slots besitzen getrennte Datenbanken und koennen deshalb keine
gemeinsame Datenbanktransaktion verwenden. Zuerst wird das Ergebnis beim
Banking-Service gebucht. Lehnt Banking ab, wird keine Slot-Runde gespeichert.
Schlaegt danach das Speichern der Runde fehl, versucht der Slots-Service die
Banking-Buchung durch den exakten Gegenbetrag zu kompensieren. Dieses Vorgehen
entspricht einer einfachen **Compensating Transaction** aus dem Saga-Pattern.

#### Zufall und Testbarkeit

Im echten Spielbetrieb erzeugt `SecureRandom` die Walzensymbole. Die Methode
`evaluate(...)` kann dieselben Regeln mit vorgegebenen Symbolen auswerten.
Dadurch lassen sich alle Gewinn- und Verlustpfade deterministisch testen, ohne
dass ein Unit-Test auf ein zufaelliges Ergebnis hoffen muss.

### Test- und Coverage-Entscheidungen

#### Warum JUnit und Mockito?

JUnit prueft Eingaben und beobachtbare Ergebnisse der einzelnen Klassen.
Mockito ersetzt abstrakte Abhaengigkeiten wie Banking-Client, Factory,
Repository und andere Handler. Ein Unit-Test fuer den Play-Handler startet
dadurch weder einen echten Banking-Service noch eine echte Datenbank. Fehler
lassen sich gezielt ausloesen und jeder Test bleibt schnell und reproduzierbar.

#### Warum JaCoCo?

JaCoCo misst, welche Codezeilen und Verzweigungen waehrend der Tests tatsaechlich
ausgefuehrt werden. Das passt zur geforderten Path-Coverage und macht bisher
ungetestete Fehlerpfade sichtbar.

Der Maven-Build bricht automatisch ab, wenn der Slots-Service weniger als
`90 %` Line Coverage oder `80 %` Branch Coverage erreicht. Coverage allein
beweist jedoch noch keine fachlich guten Tests. Deshalb pruefen die Tests
zusaetzlich konkrete Blackbox-Ergebnisse, Fehlerfaelle, Randwerte und
Extremwerte.

Der HTML-Bericht wird nur lokal unter `target/site/jacoco` erzeugt. Generierte
Build-Artefakte werden nicht committed, weil jedes Teammitglied denselben
Bericht reproduzierbar mit `./mvnw -pl slots-service verify` erstellen kann.

#### Randomisierte und reproduzierbare Tests

Wo Zufallswerte verwendet werden, kommen feste Seeds zum Einsatz. So werden
unterschiedliche Wertebereiche geprueft, waehrend ein fehlgeschlagener Test mit
denselben Daten wiederholt und untersucht werden kann. Zusaetzliche explizite
Tests decken Werte wie `null`, `0`, negative Betraege, sehr kleine Geldbetraege
und maximale IDs ab.

#### Designierter Test pro Klasse

Konkrete Produktionsklassen besitzen passend benannte Testklassen. Interfaces
werden ueber ihre Implementierungen und als gemockte Abhaengigkeiten geprueft,
weil ein Interface selbst keinen ausfuehrbaren Code enthaelt. Dieses Vorgehen
verbindet die Vorgabe designierter Tests mit dem tatsaechlich testbaren
Verhalten.

### Bewusst akzeptierte Kompromisse

| Entscheidung | Vorteil | Bewusster Nachteil |
|---|---|---|
| Interfaces auch bei nur einer Implementierung | Klare Vertraege, Dependency Injection und einfaches Mocking | Mehr Dateien und Struktur fuer einen kleinen Service |
| `I`-Praefix fuer Interfaces | Interface ist im Projekt sofort erkennbar | Entspricht nicht der Konvention jedes Java-Projekts |
| Eine Netto-Transaktion pro Runde | Weniger verteilte Aufrufe und kein Zustand zwischen Einsatz und Auszahlung | Einsatz und Brutto-Auszahlung erscheinen nicht als zwei getrennte Banking-Buchungen |
| Statistiken beim Abruf aus der Historie berechnen | Eine Quelle der Wahrheit, keine Synchronisation einer zweiten Tabelle | Bei sehr grossen Datenmengen weniger effizient als Datenbank-Aggregationen |
| Kompensierende Banking-Transaktion | Praktische Konsistenz ohne gemeinsame Datenbanktransaktion | Auch die Kompensation kann bei einem Netzwerkausfall fehlschlagen; produktiv waeren Retry oder Outbox sinnvoll |
| JaCoCo-Grenzen im Maven-Build | Sinkende Testabdeckung wird automatisch erkannt | Hohe Coverage garantiert allein noch keine guten fachlichen Assertions |

### Symbole und Spielregeln

Jede Runde besitzt genau drei Walzen. Auf jeder Walze erscheint eines von fuenf
gleich wahrscheinlichen Symbolen:

`CHERRY`, `LEMON`, `BELL`, `BAR`, `SEVEN`

Der Response-Wert `amount` ist der **Netto-Betrag**, der beim Banking-Service
gebucht wird. Der Einsatz ist darin bereits beruecksichtigt.

| Ergebnis | Kombinationen | Chance | Auszahlung | Netto-Betrag bei Einsatz 10 |
|---|---:|---:|---:|---:|
| Jackpot | 3 x `SEVEN` | `1/125 = 0.80 %` | Einsatz x 10 | `+90` |
| Drei gleiche, ohne Jackpot | 4 | `4/125 = 3.20 %` | Einsatz x 3 | `+20` |
| Genau zwei gleiche | 60 | `60/125 = 48.00 %` | Einsatz x 1 | `0` |
| Drei unterschiedliche | 60 | `60/125 = 48.00 %` | keine Auszahlung | `-10` |

Die Summe aller gleich wahrscheinlichen Ergebnisse betraegt
`5 x 5 x 5 = 125`.

### Ablauf einer Slot-Runde

1. Der Request und der Einsatz werden validiert.
2. Der Slots-Service fragt den Benutzer beim Banking-Service ab.
3. Ein Einsatz ueber dem aktuellen Guthaben wird abgelehnt.
4. Der Game-Handler erzeugt und bewertet die drei Symbole.
5. Der Netto-Betrag wird als eine Banking-Transaktion mit dem Rechnungssteller
   `SLOTS` gebucht.
6. Die Factory uebertraegt das Simulator-Ergebnis in eine persistierbare
   `SlotGame`-Entity.
7. Das Repository speichert die Runde in der Slots-Datenbank.
8. Der Controller liefert das gespeicherte Ergebnis als JSON zurueck.

Schlaegt die Speicherung nach einer erfolgreichen Banking-Buchung fehl, sendet
der Slots-Service den exakten Gegenbetrag als kompensierende Transaktion an das
Banking. Dadurch sollen Banking-Kontostand und Slots-Historie konsistent bleiben.

### Slots-Endpunkte

| Methode | Pfad | Beschreibung |
|---|---|---|
| `POST` | `/casino/slots/api/play` | Eine Slot-Runde spielen |
| `GET` | `/casino/slots/api/info/rules` | Regeln und Auszahlungen abrufen |
| `GET` | `/casino/slots/api/info/chances` | Gewinnchancen abrufen |
| `GET` | `/casino/slots/api/stats` | Zusammengefasste Gesamtstatistik |
| `GET` | `/casino/slots/api/stats/user/{user_id}` | Statistik eines Benutzers |
| `GET` | `/casino/slots/api/stats/games` | Alle gespeicherten Runden |
| `GET` | `/casino/slots/api/stat/{game_id}` | Eine gespeicherte Runde |
| `DELETE` | `/casino/slots/api/stat/{game_id}` | Eine gespeicherte Runde loeschen |

Beispielrequest fuer `POST /casino/slots/api/play`:

```json
{
  "user": 1,
  "betAmount": 10.00
}
```

Beispielresponse:

```json
{
  "gameId": 15,
  "user": 1,
  "betAmount": 10.00,
  "winning": false,
  "amount": -10.00,
  "slotStates": ["CHERRY", "LEMON", "BAR"],
  "payoutMultiplier": 0,
  "playedAt": "2026-08-02T20:30:00"
}
```

Wichtige Fehlercodes des Play-Endpunkts:

| Status | Bedeutung |
|---:|---|
| `200 OK` | Runde wurde gebucht und gespeichert |
| `400 Bad Request` | Request ungueltig, Einsatz ungueltig oder Guthaben nicht ausreichend |
| `404 Not Found` | Benutzer existiert im Banking-Service nicht |
| `503 Service Unavailable` | Banking-Service ist nicht erreichbar oder antwortet unerwartet |

### Slots-Statistiken

`GET /casino/slots/api/stats` liefert die in der Belegbeschreibung geforderte
Gesamtansicht:

- `total_client_count`: Anzahl unterschiedlicher Spieler
- `total_games_count`: Anzahl gespeicherter Runden
- `total_turnover`: Summe aller Einsaetze
- `total_cash_out`: Summe aller Brutto-Auszahlungen
- `total_profit`: Hausgewinn, berechnet als Umsatz minus Auszahlungen

`GET /casino/slots/api/stats/user/{user_id}` liefert die entsprechende
Zusammenfassung fuer einen einzelnen Benutzer. Fuer einen Benutzer ohne
gespeicherte Slot-Runden wird `404 Not Found` zurueckgegeben.

## Tests

Alle Module bauen und alle vorhandenen Tests ausfuehren:

```bash
./mvnw verify
```

Nur den Slots-Service pruefen:

```bash
./mvnw -pl slots-service verify
```

Der Slots-Service verwendet JUnit, Mockito und JaCoCo. Getestet werden unter
anderem:

- alle Gewinn- und Verlustpfade der Spiellogik
- ungueltige Eingaben sowie Rand- und Extremwerte
- Factory und Domain-Integritaet der Entity
- Repository- und Handler-Ablaeufe mit gemockten Abhaengigkeiten
- Banking- und Play-Ablauf einschliesslich Fehler- und Kompensationspfaden
- Controller-Statuscodes
- Informations-, Historien- und Statistik-Endpunkte

Der Maven-Build erzwingt fuer den Slots-Service mindestens:

- `90 %` Line Coverage
- `80 %` Branch Coverage

Der zuletzt lokal verifizierte Stand erreicht `97.37 %` Line Coverage und
`92.37 %` Branch Coverage bei `108` erfolgreichen Tests.

Nach `verify` wird der lokale HTML-Bericht erzeugt:

```text
slots-service/target/site/jacoco/index.html
```

Der `target`-Ordner wird nicht versioniert. Jedes Teammitglied erzeugt den
Bericht deshalb lokal durch den Maven-Befehl.

## Konfiguration

Docker Compose setzt die Datenbankverbindungen und die Banking-URL ueber
Umgebungsvariablen. Die wichtigsten Variablen sind:

| Variable | Aufgabe |
|---|---|
| `SPRING_DATASOURCE_URL` | JDBC-Verbindung zur jeweiligen Service-Datenbank |
| `SPRING_DATASOURCE_USERNAME` | Datenbankbenutzer |
| `SPRING_DATASOURCE_PASSWORD` | Datenbankpasswort |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | Hibernate-Schema-Verhalten |
| `SPRING_JPA_SHOW_SQL` | Ausgabe der SQL-Befehle |
| `BANKING_SERVICE_URL` | Interne URL des Banking-Service fuer die Spielservices |

Die in `docker-compose.yml` enthaltenen Zugangsdaten sind ausschliesslich fuer
die lokale Entwicklung und nicht fuer einen Produktivbetrieb vorgesehen.

## Projektstruktur

```text
casino-beleg/
|-- pom.xml
|-- docker-compose.yml
|-- banking-service/
|   |-- Dockerfile
|   `-- src/
|-- roulette-service/
|   |-- Dockerfile
|   `-- src/
|-- slots-service/
|   |-- Dockerfile
|   `-- src/
|       |-- main/java/beleg/slotsservice/
|       |   |-- client/       Banking-HTTP-Client und DTOs
|       |   |-- controller/   REST-Endpunkte
|       |   |-- exception/    Fachliche Fehler
|       |   |-- factory/      Erzeugung persistierbarer SlotGame-Entities
|       |   |-- handler/      Play-, Game-, Info- und Statistiklogik
|       |   |-- model/        Domain-Modelle und JPA-Entity
|       |   |-- repository/   Datenbankzugriff
|       |   `-- view/         Request- und Response-DTOs
|       `-- test/java/beleg/slotsservice/
|-- Belegvorstellung.pdf
`-- README.md
```



## Autoren und Zustaendigkeiten

- **Slots-Service** : Phu Dat Tran
- **Banking-Service**: Anton Eckey
- **Roulette-Service**: Name des zustaendigen Teammitglieds ergaenzen
- **Gemeinsame Architektur und Dokumentation**: Teamangaben ergaenzen


## Lizenz

Dieses Projekt steht unter der
[Creative Commons Attribution 4.0 International (CC BY 4.0)](https://creativecommons.org/licenses/by/4.0/).


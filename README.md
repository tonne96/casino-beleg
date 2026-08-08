# Casino Microservices

## Projektbeschreibung

Dieses Projekt modelliert ein risikofreies Online-Casino als RESTful
Microservice-Anwendung. Benutzer können ein Konto anlegen, Guthaben verwalten
und einzelne Runden Roulette oder Slots spielen. Spielhistorien, Statistiken,
Regeln und Gewinnchancen machen die Ergebnisse nachvollziehbar.

Im Mittelpunkt stehen eine saubere Architektur,
SOLID-Prinzipien, robuste Domain-Modelle, automatisierte Tests und die
Kommunikation zwischen unabhängigen Services.

## Architektur

Die Anwendung besteht aus drei Spring-Boot-Services und drei getrennten
PostgreSQL-Datenbanken:

| Service | Aufgabe | HTTP-Port | Datenbank-Port |
|---|---|---:|---:|
| Banking-Service | Benutzer, Guthaben und Transaktionen | `8080` | `5432` |
| Roulette-Service | Roulette-Runden, Regeln, Historie und Statistiken | `8081` | `5433` |
| Slots-Service | Slot-Runden, Regeln, Historie und Statistiken | `8082` | `5434` |

Jeder Service greift ausschließlich auf seine eigene Datenbank zu. Roulette
und Slots verifizieren Benutzer und buchen Spielergebnisse über die HTTP-API
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
Belegbeschreibung ist für Roulette und Slots Port `8081` angegeben; zwei
gleichzeitig laufende Services können jedoch nicht denselben Host-Port
verwenden.

## Technologie-Stack

- Java 25
- Spring Boot 4
- Maven-Multi-Module-Projekt mit Maven Wrapper
- PostgreSQL 17
- Docker und Docker Compose
- Swagger UI / OpenAPI
- JUnit und Mockito
- PlantUML für die Architekturdokumentation

## Voraussetzungen

Für den empfohlenen Start der Gesamtanwendung werden nur folgende Programme
benötigt:

- Git
- Docker mit Docker Compose

Maven, Java und PostgreSQL müssen für den Docker-Betrieb nicht separat
installiert werden. Für einen lokalen Maven-Build außerhalb von Docker wird
zusätzlich ein Java-25-JDK benötigt. Maven selbst wird über `./mvnw`
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
einem zweiten Terminal geprüft werden:

```bash
docker compose ps
```

Die Anwendung beenden:

```bash
docker compose down
```

Anwendungscontainer, Datenbankcontainer und gespeicherte Daten vollständig
löschen:

```bash
docker compose down -v
```

**Achtung:** Durch `-v` werden die drei Docker-Datenbank-Volumes und damit alle
Benutzer, Transaktionen und Spielhistorien gelöscht.

## Swagger und OpenAPI

Nach dem Start stehen drei getrennte Swagger-Oberflächen zur Verfügung:

| Service | Swagger UI | OpenAPI-Dokument |
|---|---|---|
| Banking | <http://localhost:8080/swagger-ui.html> | <http://localhost:8080/api-docs> |
| Roulette | <http://localhost:8081/swagger-ui.html> | <http://localhost:8081/api-docs> |
| Slots | <http://localhost:8082/swagger-ui.html> | <http://localhost:8082/api-docs> |

Swagger ist die vollständige technische API-Dokumentation. Dort können alle
Requests direkt ausgeführt und die jeweiligen Response-Bodies sowie
HTTP-Statuscodes geprüft werden.

## Beispielhafter End-to-End-Ablauf mit Swagger

Die folgenden Schritte zeigen das Zusammenspiel von Banking- und Slots-Service.
Alle Requests werden direkt über die jeweilige Swagger UI ausgeführt. Dazu
wird der gewünschte Endpunkt aufgeklappt und über **Try it out** und
**Execute** gestartet.

Die vom Banking-Service zurückgegebene Benutzer-ID muss in den nachfolgenden
Requests eingesetzt werden. In diesem Beispiel wird angenommen, dass der neue
Benutzer die ID `1` erhält.

### 1. Benutzer erstellen

1. Banking-Swagger unter <http://localhost:8080/swagger-ui.html> öffnen.
2. `POST /casino/bank/api/user` auswählen.
3. Folgenden Request-Body eintragen und ausführen:

```json
{
  "first_name": "Max",
  "last_name": "Mustermann"
}
```

Die zurückgegebene `id` für die weiteren Schritte merken.

### 2. Guthaben einzahlen

Im Banking-Swagger den Endpunkt
`POST /casino/bank/api/user/{user_id}/deposit/{amount}/{decimals}` ausführen.

Für eine Einzahlung von `100.00` auf Benutzer `1` werden folgende Werte
eingetragen:

| Parameter | Wert |
|---|---:|
| `user_id` | `1` |
| `amount` | `100` |
| `decimals` | `0` |

Danach sollte die Response für den Benutzer einen Kontostand von `100.00`
anzeigen.

### 3. Slot-Runde spielen

1. Slots-Swagger unter <http://localhost:8082/swagger-ui.html> öffnen.
2. `POST /casino/slots/api/play` auswählen.
3. Folgenden Request-Body eintragen und ausführen:

```json
{
  "user": 1,
  "betAmount": 10.00
}
```

Die Response enthält unter anderem die Symbole, den Multiplikator, den
Netto-Betrag und die gespeicherte `gameId`.

### 4. Ergebnis kontrollieren

Im Banking-Swagger werden anschließend folgende Endpunkte geprüft:

- `GET /casino/bank/api/user/{id}` für den neuen Kontostand
- `GET /casino/bank/api/transactions/user/{id}` für die Slots-Transaktion

Im Slots-Swagger werden folgende Endpunkte geprüft:

- `GET /casino/slots/api/stats/user/{user_id}` für die Benutzerstatistik
- `GET /casino/slots/api/stats/games` für die gespeicherte Spielrunde

Der gleiche Ablauf kann mit dem Roulette-Service über dessen Swagger UI
durchgeführt werden.

## Banking-Service

Der Banking-Service ist die zentrale Datenquelle des Casino-Systems. Er
verwaltet Benutzer, Guthaben und Transaktionen. Roulette- und Slots-Service
kommunizieren ausschließlich über HTTP mit ihm; ein direkter Datenbankzugriff
von außen findet nicht statt.

### Architektur- und Designentscheidungen

Der Service folgt der **Vertikal-Slice-Architektur** mit den Subdomänen
`User` und `Transaction`. Controller, Handler und Views sind pro Subdomäne
getrennt. Controller und Handler hängen von Interfaces ab
(`IUserHandler`, `ITransactionHandler` usw.), nicht von konkreten
Implementierungen. Das folgt dem **Dependency-Inversion-Prinzip** und erlaubt
das isolierte Testen mit Mockito.

Da Subdomänen laut Vertikal-Slice-Architektur nicht direkt miteinander
kommunizieren dürfen, bucht der `TransactionHandler` Kontostandsänderungen
über einen **HTTP-Self-Call** an den eigenen User-Endpunkt (`RestTemplate`).

### Banking-Endpunkte

| Methode | Pfad | Beschreibung |
|---|---|---|
| `GET` | `/casino/bank/api/user/{id}` | Einen Benutzer abrufen |
| `GET` | `/casino/bank/api/users` | Alle Benutzer abrufen |
| `POST` | `/casino/bank/api/user` | Einen Benutzer anlegen |
| `PUT` | `/casino/bank/api/user/{user_id}` | Einen Benutzer aktualisieren |
| `DELETE` | `/casino/bank/api/user/{user_id}` | Einen Benutzer löschen |
| `POST` | `/casino/bank/api/user/{user_id}/deposit/{amount}/{decimals}` | Guthaben einzahlen |
| `GET` | `/casino/bank/api/transactions` | Alle Transaktionen abrufen |
| `GET` | `/casino/bank/api/transactions/user/{id}` | Transaktionen eines Benutzers abrufen |
| `POST` | `/casino/bank/api/transaction/user/{user_id}` | Transaktion anlegen und Kontostand anpassen |
| `PUT` | `/casino/bank/api/transaction/{transaction_id}` | Transaktion aktualisieren |
| `DELETE` | `/casino/bank/api/transaction/{transaction_id}` | Transaktion löschen |

### Teststrategie

| Schicht | Testart | Abhängigkeiten |
|---|---|---|
| `User`, `Transaction` | Unit-Test ohne Mock | keine |
| `UserHandler`, `TransactionHandler` | Unit-Test mit Mockito | Repository und RestTemplate werden gemockt |
| `UserController`, `TransactionController` | Unit-Test mit Mockito | Handler-Interface wird gemockt |

Eingabewerte werden mit `Random` variiert. Zusätzlich werden `null`, leere
Strings, negative Beträge und nicht vorhandene IDs explizit geprüft.

## Roulette-Service

> Dieser Abschnitt wird vom verantwortlichen Teammitglied ergänzt. Er soll
> insbesondere den Verantwortungsbereich, den internen Aufbau, die
> Roulette-Regeln, fachliche Entscheidungen und die Teststrategie des
> Roulette-Service erklären.

## Slots-Service

Der Slots-Service simuliert einzelne Runden eines Spielautomaten. Er prüft den
Benutzer und dessen Guthaben über den Banking-Service, berechnet das
Spielergebnis und speichert jede erfolgreich gebuchte Runde in seiner eigenen
Datenbank. Zusätzlich stellt er Regeln, Gewinnchancen, Spielhistorien und
Statistiken bereit.

### Architektur- und Designentscheidungen

Der Service folgt einer **geschichteten Architektur**. Controller behandeln nur
HTTP-Anfragen und Statuscodes. Die Handler trennen den vollständigen
Spielablauf, die reine Spiellogik, die Historie, Informationen und Statistiken
voneinander. Das folgt dem Single-Responsibility-Prinzip und hält die
fachliche Logik unabhängig von HTTP und Datenbankzugriffen.

Controller und Handler verwenden kleine Interfaces wie
`IPlaySlotsHandler`, `ISlotGameHandler`, `IBankingClient` und
`ISlotGameFactory`. Spring injiziert die Implementierungen über die
Konstruktoren. Dadurch hängt die Ablaufsteuerung von Verträgen statt von
konkreten Klassen ab und abstrakte Abhängigkeiten können in Unit-Tests mit
Mockito ersetzt werden.

Der `BankingClient` kapselt die HTTP-Kommunikation mit dem Banking-Service.
Die konfigurierbare Variable `BANKING_SERVICE_URL` unterscheidet zwischen
lokalem Betrieb und Docker, ohne eine feste URL im Java-Code zu hinterlegen.

`SlotGameResult` ist das Ergebnis der Simulation, während `SlotGame` die
persistierbare JPA-Entity darstellt. Die `SlotGameFactory` überträgt die
berechneten Werte in die Entity. Dadurch kennt die Entity das Ergebnisformat
des Simulators nicht direkt und ihre Domain-Integrität bleibt in
`SlotGame.create(...)` gekapselt.

Pro Runde wird eine Banking-Transaktion mit dem fertigen **Netto-Ergebnis**
erstellt. Lehnt Banking die Buchung ab, wird keine Runde gespeichert. Scheitert
die Speicherung erst nach erfolgreicher Buchung, versucht der Slots-Service
den Betrag durch eine kompensierende Gegenbuchung auszugleichen.

### Symbole und Spielregeln

Eine Runde besitzt drei Walzen. Jede Walze zeigt eines von fünf gleich
wahrscheinlichen Symbolen:

`CHERRY`, `LEMON`, `BELL`, `BAR`, `SEVEN`

Der Response-Wert `amount` ist der Netto-Betrag nach Berücksichtigung des
Einsatzes und wird in dieser Form beim Banking-Service gebucht.

| Ergebnis | Kombinationen | Chance | Auszahlung | Netto bei Einsatz 10 |
|---|---:|---:|---:|---:|
| Drei `SEVEN` | 1 | `1/125 = 0.80 %` | Einsatz x 10 | `+90` |
| Drei gleiche, ohne `SEVEN` | 4 | `4/125 = 3.20 %` | Einsatz x 3 | `+20` |
| Genau zwei gleiche | 60 | `60/125 = 48.00 %` | Einsatz x 1 | `0` |
| Drei unterschiedliche | 60 | `60/125 = 48.00 %` | keine Auszahlung | `-10` |

Insgesamt existieren `5 x 5 x 5 = 125` gleich wahrscheinliche Ergebnisse.
Zwei gleiche Symbole gelten als Gewinnkombination, geben aber nur den Einsatz
zurück und verändern den Kontostand daher netto nicht.

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
| `DELETE` | `/casino/slots/api/stat/{game_id}` | Eine gespeicherte Runde löschen |

### Teststrategie

| Bereich | Testart | Abhängigkeiten |
|---|---|---|
| Spiellogik und Domain-Modelle | Unit-Test ohne externe Systeme | keine |
| Play-, History- und Stats-Handler | Unit-Test mit Mockito | Interfaces für Client, Factory und Repository werden gemockt |
| Banking-Client | isolierter HTTP-Test | simulierter Banking-Server |
| Controller | Unit-Test mit Mockito | Handler-Interfaces werden gemockt |

Die Tests prüfen Gewinn- und Verlustpfade, ungültige Eingaben, Randwerte,
Extremwerte sowie Fehler in der Kommunikation und Speicherung.

## Konfiguration

Docker Compose setzt die Datenbankverbindungen und die Banking-URL über
Umgebungsvariablen. Die wichtigsten Variablen sind:

| Variable | Aufgabe |
|---|---|
| `SPRING_DATASOURCE_URL` | JDBC-Verbindung zur jeweiligen Service-Datenbank |
| `SPRING_DATASOURCE_USERNAME` | Datenbankbenutzer |
| `SPRING_DATASOURCE_PASSWORD` | Datenbankpasswort |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | Hibernate-Schema-Verhalten |
| `SPRING_JPA_SHOW_SQL` | Ausgabe der SQL-Befehle |
| `BANKING_SERVICE_URL` | Interne URL des Banking-Service für die Spielservices |

Die in `docker-compose.yml` enthaltenen Zugangsdaten sind ausschließlich für
die lokale Entwicklung und nicht für einen Produktivbetrieb vorgesehen.

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

## Autoren und Zuständigkeiten

- **Slots-Service**: Phu Dat Tran
- **Banking-Service**: Anton Eckey
- **Roulette-Service**: Name des zuständigen Teammitglieds ergänzen
- **Gemeinsame Architektur und Dokumentation**: Teamangaben ergänzen

## Lizenz

Dieses Projekt steht unter der
[Creative Commons Attribution 4.0 International (CC BY 4.0)](https://creativecommons.org/licenses/by/4.0/).

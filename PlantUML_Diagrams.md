# PlantUML Component Diagrams

## Entire Application

### Rendered Image

![UML Application](/PlantUML_Entire-Application.svg)

### PlantUML Code

> [!NOTE]
> If you have a Markdown extension installed in your code editor/IDE or your browser, the extension may render an image from the PlantUML code automatically.

```plantuml
@startuml

left to right direction

actor "Client" as Client

package "Casino Application" {

    frame "Services" {
        component "Banking Service\n" as Banking
        component "Roulette Service\n" as Roulette
        component "  Slots Service  \n" as Slots
    }
    
    frame "PostgreSQL\nDatabases" {    
        database "\nBanking DB\n" as BankingDB
        database "\nRoulette DB\n" as RouletteDB
        database "\n  Slots DB  \n" as SlotsDB
    }
}

' Zugriff des Clients
Client --> Banking : REST
Client --> Roulette : REST
Client --> Slots : REST

' Inter-Service-Kommunikation
Roulette -> Banking : REST
Slots -> Banking : REST

' Datenbanken
Banking --> BankingDB
Roulette --> RouletteDB
Slots --> SlotsDB

@enduml
```

___

## BankingService

### Rendered Image

![UML Banking](/PlantUML_Banking.svg)

### PlantUML Code

> [!NOTE]
> If you have a Markdown extension installed in your code editor/IDE or your browser, the extension may render an image from the PlantUML code automatically.

```plantuml
@startuml

package "Banking Service" {

    together {
        folder "Transaction Slice" as TS {
            component "Transaction Controller" as TransactionController
            component "Transaction Handler" as TransactionHandler
            component "Transaction Repository" as TransactionRepository
        }
    
        folder "User Slice" as US {
            component "User Controller" as UserController
            component "User Handler" as UserHandler
            component "User Repository" as UserRepository
            
            interface "REST API" as UserApi
            UserApi - UserController
        }
    }

    together {
        folder "Models" as M {
            component "Transaction Model"
            component "User Model"
        }
        
        folder "Views" as V {
            component "Transaction Views" as TV
            component "User Views" as UV
        }
        
        folder "Spring\nInfrastructure" as INF {
            component "RestTemplate" as RestTemplate
        }
    }
    
    ' Hidden Connections for formatting
    TS -[hidden]down-> INF
    US -[hidden]down-> INF
    V -[hidden]up-> TS
    M -[hidden]up-> US
}

' TRANSACTION
TransactionController --> TransactionHandler
TransactionHandler --> TransactionRepository

' USER
UserController --> UserHandler
UserHandler --> UserRepository

' TransactionHandler prüft User über REST
TransactionHandler --> RestTemplate : "REST"
RestTemplate ..> UserApi : "GET /user/{id}"

@enduml
```

___

## RouletteService

### Rendered Image

![UML Roulette](/PlantUML_Roulette.svg)

### PlantUML Code

> [!NOTE]
> If you have a Markdown extension installed in your code editor/IDE or your browser, the extension may render an image from the PlantUML code automatically.

```plantuml
@startuml
left to right direction

package "Roulette Service" {

    frame "Controller Layer" {
        component "RouletteController" as RouletteController
        component "RouletteInfoController" as InfoController
        component "RouletteStatsController" as StatsController
    }
    
    frame "Handler Layer" {
        component "RouletteGameHandler" as GameHandler
        component "RouletteInfoHandler" as InfoHandler
        component "RouletteStatsHandler" as StatsHandler
    }
    
    frame "External Client" {
        interface "IBankingClient" as BankingClient
        component "BankingClient\nImplementation" as BankingImpl
    }
    
    frame "Persistence Layer" {
        component "RouletteGame Repository\n(Spring JPA)" as Repository
    }
    
    frame "Domain Model" {
        component "RouletteGame" as RouletteGame
        component "RouletteGameResult" as GameResult
        component "RouletteBetType" as BetType
    }
    
    frame "Views / DTOs" {
        component "Roulette Views" as View
    }
}

' Controller -> Handler
RouletteController --> GameHandler
InfoController --> InfoHandler
StatsController --> StatsHandler

' Handler -> Persistence
GameHandler -up-> Repository
StatsHandler -right-> Repository

' Handler -> Domain
GameHandler --> RouletteGame
GameHandler --> GameResult

' Handler -> External Service
GameHandler --> BankingClient
StatsHandler --> BankingClient

' Banking Implementation
BankingImpl ..|> BankingClient

' Repository speichert Domain
Repository -right-> RouletteGame

@enduml
```

---

## SlotsService

### Rendered Image

![UML Slots](/PlantUML_Slots.svg)

### PlantUML Code

> [!NOTE]
> If you have a Markdown extension installed in your code editor/IDE or your browser, the extension may render an image from the PlantUML code automatically.

```plantuml
@startuml
left to right direction

package "Slots Service" {

    frame "Controller Layer" {
        component "SlotsController" as SlotsController
        component "SlotsInfoController" as InfoController
        component "SlotsStatsController" as StatsController
    }

    frame "Handler Layer" {
        component "PlaySlotsHandler" as PlayHandler
        component "SlotGameHandler" as GameHandler
        component "SlotGameHistoryHandler" as HistoryHandler
        component "SlotInfoHandler" as InfoHandler
        component "SlotStatsHandler" as StatsHandler
    }

    frame "Persistence Layer" {
        component "Gameresult Repository\n(Spring JPA)" as Repository
    }

    frame "Domain Model" {
        component "SlotGame" as SlotGame
        component "SlotSymbol" as SlotSymbol
        component "SlotGameResult" as Result
    }

    frame "Views/DTOs" {
        component "Slot Views" as Views
    }

    frame "External Client" {
        interface "IBankingClient" as BankingClient
        component "BankingClient\nImplementation" as BankingImpl
    }
}

' Controller -> Handler
SlotsController --> PlayHandler
InfoController --> InfoHandler
StatsController --> StatsHandler
StatsController --> HistoryHandler

' Handler intern
PlayHandler --> GameHandler
PlayHandler --> HistoryHandler
PlayHandler --> BankingClient

StatsHandler --> Repository
HistoryHandler --> Repository

GameHandler --> Result

Repository --> SlotGame

' Banking Client
BankingImpl ..|> BankingClient

@enduml
```

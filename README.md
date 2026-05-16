# SoftwareengineeringBeleg

# Ordnerstruktur

```
SoftwareengineeringBeleg/
├── pom.xml                          ← Parent POM (gemeinsame Dependencies)
├── banking-service/
│   ├── pom.xml                      
│   ├── Dockerfile
│   └── src/
│       ├── main/java/.../bankservice/
│       │   ├── BankingServiceApplication.java
│       │   ├── controller/
│       │   │   ├── user/
│       │   │   ├── transaction/
│       │   │   └── stat/
│       │   ├── handler/
│       │   │   ├── user/
│       │   │   ├── transaction/
│       │   │   └── stat/
│       │   ├── model/
│       │   │   ├── entity/          ← (User, Transaction)
│       │   │   └── dto/             ← Request/Response DTOs
│       │   ├── repository/
│       │   └── view/                ← Response-Objekte (JSON)
│       └── main/resources/
│           └── application.properties 
├── roulette-service/
│   ├── pom.xml
│   ├── Dockerfile
│   └── src/... (controller, handler, model, repository)
├── slots-service/
│   ├── pom.xml
│   ├── Dockerfile
│   └── src/... (controller, handler, model, repository)
├── docker-compose.yml
└── README.md
```
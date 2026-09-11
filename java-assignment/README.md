# HCL Java Code Assignment

A Quarkus 3.13.3 REST API built with hexagonal (ports-and-adapters) architecture, covering Product, Store, Warehouse, and Allocation domains. Includes full test coverage, JaCoCo reporting, and a GitHub Actions CI/CD pipeline.

## Index Page

![Index Page](../docs/screenshots/index-page.png)

## Quarkus Dev UI (`/q/dev-ui`)

![Quarkus Dev UI](../docs/screenshots/dev-ui.png)

---

## Architecture

The project follows **hexagonal (ports-and-adapters) architecture** across all four business domains:

```
src/main/java/com/fulfilment/application/monolith/
│
├── products/
│   ├── domain/
│   │   └── models/                   # Product (JPA entity)
│   └── adapters/
│       ├── database/                 # ProductRepository (PanacheRepository)
│       └── restapi/                  # ProductResource
│
├── stores/
│   ├── domain/
│   │   ├── models/                   # Store (PanacheEntity — Active Record)
│   │   ├── events/                   # StoreCreatedEvent, StoreUpdatedEvent, StoreDeletedEvent
│   │   └── usecases/                 # StoreService (@Transactional), StoreEventObserver
│   └── adapters/
│       ├── restapi/                  # StoreResource
│       └── gateway/                  # LegacyStoreManagerGateway (CDI AFTER_SUCCESS observer)
│
├── location/
│   └── adapters/
│       └── gateway/                  # LocationGateway (implements LocationResolver port)
│
├── warehouses/
│   ├── domain/
│   │   ├── models/                   # Warehouse, Location
│   │   ├── events/                   # WarehouseCreatedEvent, WarehouseArchivedEvent, WarehouseReplacedEvent
│   │   ├── ports/                    # CreateWarehouseOperation, ArchiveWarehouseOperation,
│   │   │                             # ReplaceWarehouseOperation, WarehouseStore, LocationResolver
│   │   └── usecases/                 # CreateWarehouseUseCase, ArchiveWarehouseUseCase,
│   │                                 # ReplaceWarehouseUseCase, WarehouseEventObserver
│   └── adapters/
│       ├── database/                 # DbWarehouse (JPA entity), WarehouseRepository (implements WarehouseStore)
│       ├── restapi/                  # WarehouseResourceImpl
│       └── gateway/                  # LegacyWarehouseGateway (CDI AFTER_SUCCESS observer)
│
└── allocation/
    ├── domain/
    │   ├── models/                   # WarehouseAllocation (PanacheEntity)
    │   ├── events/                   # AllocationCreatedEvent
    │   ├── ports/                    # AllocationStore (repository interface)
    │   └── usecases/                 # AllocateWarehouseUseCase (3 business constraints)
    └── adapters/
        ├── database/                 # AllocationRepository (implements AllocationStore)
        └── restapi/                  # AllocationResource, AllocationRequest
```

### Key Design Decisions

| Concern | Solution |
|---|---|
| Persistence — Store | Active Record (`PanacheEntity`) — entity manages its own lifecycle |
| Persistence — Product, Warehouse, Allocation | Repository pattern (`PanacheRepository`) — separation of data access |
| Port interfaces | `WarehouseStore`, `AllocationStore`, `LocationResolver` decouple domain from infrastructure |
| Event-driven side effects | CDI `Event<T>` fired inside use cases; `@Observes(during = AFTER_SUCCESS)` in gateway adapters |
| Location validation | `LocationResolver` port implemented by `LocationGateway` (8 Dutch locations) |
| Allocation constraints | Max 2 warehouses per product/store · Max 3 warehouses per store · Max 5 products per warehouse |
| Test isolation | H2 in-memory (`%test` / `%dev` profile); PostgreSQL for production only |
| Coverage | `quarkus-jacoco` + `jacoco-maven-plugin` merge → **96.2%** instruction coverage |

---

## Requirements

- **JDK 17+** (`JAVA_HOME` must be set)
- **Maven wrapper** (`./mvnw`) included — no separate Maven install needed
- **PostgreSQL** for production only (H2 is used automatically in dev and test)

---

## Starting the Application

### Prerequisites

Ensure the following are installed and configured before running:

| Tool | Version | Check |
|---|---|---|
| JDK | 17+ | `java -version` |
| Maven wrapper | bundled | `./mvnw --version` |
| PostgreSQL | any (prod only) | `psql --version` |

> **Dev and test modes use H2 in-memory — no database setup required.**

---

### Step 1 — Clone the repository

```sh
git clone <repo-url>
cd hcl-case-study/java-assignment
```

---

### Step 2 — Run in Dev Mode (recommended)

```sh
./mvnw quarkus:dev
```

- Starts on **http://localhost:8080**
- Uses **H2 in-memory** database automatically (`%dev` profile)
- **Live reload** enabled — code changes apply instantly on next request
- **Dev UI** available at **http://localhost:8080/q/dev-ui**

Expected output:
```
__  ____  __  _____   ___  __ ____  ______
 --/ __ \/ / / / _ | / _ \/ //_/ / / / __/
 -/ /_/ / /_/ / __ |/ , _/ ,< / /_/ /\ \
--\___\_\____/_/ |_/_/|_/_/|_|\____/___/
...
INFO  [io.quarkus] java-code-assignment 1.0.0-SNAPSHOT started in X.XXXs
INFO  [io.quarkus] Profile dev activated. Live Coding activated.
```

---

### Step 3 — Verify the application is running

```sh
# List products (should return seeded data)
curl http://localhost:8080/product

# List stores
curl http://localhost:8080/store

# List warehouses
curl http://localhost:8080/warehouse
```

---

### Step 4 — Run Tests

```sh
./mvnw verify
```

Runs all 62 `@QuarkusTest` integration tests against H2. JaCoCo coverage report is generated at:

```
target/site/jacoco/index.html
```

---

### Step 5 — Build & Run as JAR (Production)

**Requires PostgreSQL.** Configure the datasource first:

```properties
# src/main/resources/application.properties
quarkus.datasource.jdbc.url=jdbc:postgresql://localhost:5432/mydb
quarkus.datasource.username=myuser
quarkus.datasource.password=mypassword
```

Then package and run:

```sh
# Build the fast-jar
./mvnw package -DskipTests

# Run
java -jar target/quarkus-app/quarkus-run.jar
```

---

## API Endpoints

| Domain | Base Path | Methods |
|---|---|---|
| Product | `/product` | GET, POST, PUT, DELETE |
| Store | `/store` | GET, POST, PUT, PATCH, DELETE |
| Warehouse | `/warehouse` | GET, POST, DELETE, POST `/{buCode}/replacement` |
| Allocation *(bonus)* | `/allocation` | GET, POST |

See [`api-report.md`](api-report.md) for the full validated endpoint reference (43 endpoints).

### Quick test

```sh
# List products
curl http://localhost:8080/product

# Create a store
curl -X POST http://localhost:8080/store \
  -H "Content-Type: application/json" \
  -d '{"name":"AMSTERDAM-STORE","quantityProductsInStock":10}'
```

---

## Coverage

| Metric | Result |
|---|---|
| Tests | 62 / 62 passing |
| Instruction coverage | **96.2%** |
| Branch coverage | **87.5%** |

See [`coverage.md`](coverage.md) for the full per-class breakdown.

---

## CI/CD Pipeline

GitHub Actions workflow at [`.github/workflows/ci.yml`](../.github/workflows/ci.yml):

**Triggers:** push or PR to `main` touching `java-assignment/**`

```
build-and-test  →  package (main branch only)
    │                   │
    ├─ ./mvnw verify    ├─ ./mvnw package -DskipTests
    ├─ Upload JaCoCo    └─ Upload quarkus-fast-jar artifact
    └─ Print coverage %
```

| Job | Runs on | Artifact |
|---|---|---|
| `build-and-test` | every push / PR | JaCoCo HTML report (14 days) |
| `package` | main branch push only | `quarkus-app/` fast-jar (7 days) |

---

## Assignment Tasks

See [CODE_ASSIGNMENT.md](CODE_ASSIGNMENT.md) for the original task descriptions and [QUESTIONS.md](QUESTIONS.md) for design questions and answers.

---

## Based On

[Quarkus Quickstarts](https://github.com/quarkusio/quarkus-quickstarts) · Quarkus 3.13.3 · Java 17

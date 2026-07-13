# Design Document — SolidToxic CRUD

## Overview

SolidToxic CRUD is an enterprise-grade data management system designed to maintain toxic waste tracking records. The application transitions from a monolithic desktop script to a **distributed, containerized architecture** using **Java**, **Docker**, and **PostgreSQL** 

The system presents a classic two-panel maintenance layout to the Operator: an upper tabular grid displaying active records (**Grid_Data**) and a lower input form for record inspection and modification (**Register_Form**). All destructive operations are logical—records are never physically deleted from the database, preserving historical integrity through a dedicated state record field.
---

## Architecture & Infrastructure

The application follows a decoupled client-server architecture. The backend services and database are containerized using **Docker**, while the graphical user interface runs as a native desktop client.

```
               Native Desktop Client
             ┌───────────────────────┐
             │     JavaFX (GUI)      │
             └───────────┬───────────┘
                         │ HTTP / REST (JSON)
                         ▼
             ┌───────────────────────┐
             │ Java Backend (Docker) │
             └───────────┬───────────┘
                         │ JDBC / SQL
                         ▼
             ┌───────────────────────┐
             │  PostgreSQL (Docker)  │
             └───────────────────────┘

```

### 1. Containerization (`docker-compose.yml`)

* **`db` Container (PostgreSQL):** Hosts the relational database engine and initializes the schema automatically on startup using `postresiduos.sql`.
* **`backend` Container (Java Web API):** Exposes stateless REST endpoints over HTTP, manages JDBC connections to the PostgreSQL container, enforces business validation, and executes SQL commands.

### 2. Module Layout

```
solidtoxic-system/
├── docker-compose.yml             # Container orchestration for DB and Backend
├── db/
│   └── postresiduos.sql           # Database schema and initial DDL
├── backend/                       # Java Backend Application (Dockerized)
│   ├── src/main/java/com/solidtoxic/
│   │   ├── controller/            # REST API Endpoints (HTTP Handlers)
│   │   ├── service/               # Business logic, validation, and state rules
│   │   ├── repository/            # JDBC data access objects (DAOs)
│   │   ├── model/                 # Domain entities and Data Transfer Objects (DTOs)
│   │   └── exception/             # Global exception handling and error mapping
│   └── Dockerfile
└── gui/                           # JavaFX Desktop Client (Native / Non-Dockerized)
    ├── src/main/java/com/solidtoxic/gui/
    │   ├── MainWindow.java        # Layout coordinator and main application stage
    │   ├── component/
    │   │   ├── GridData.java      # TableView displaying active records
    │   │   └── RegisterForm.java  # Form fields and action buttons
    │   ├── client/                # HTTP REST client for backend communication
    │   └── util/                  # UI state validators and alert dialogs
    └── pom.xml / build.gradle

```

---

## Data Model & Schema Mapping

The database layer is governed by the `postresiduos.sql` schemao satisfy general CRUD maintenance requirements while respecting the relational structure, the architecture applies uniform state tracking across all tables.

postresiduos.sql is described like this:

## TR_Nivel_Toxicidad

Stores the predefined toxicity levels that classify hazardous waste according to its degree of danger. This table serves as a reference catalog to ensure consistent toxicity classification throughout the system.

| Column | Description |
|---------|-------------|
| ID_Toxicidad | Primary key identifying the toxicity level. |
| Nivel | Name of the toxicity level. |
| Descripcion | Detailed explanation of the toxicity level. |
| estReg | Record status (e.g., Active or Inactive). |

### Relationships

- One toxicity level can classify many **Residuo** records (1:N).

## TR_Tipo_Envase

Stores the different packaging types used for transporting hazardous waste. Using a reference catalog guarantees standardized packaging information across all shipment records.

| Column | Description |
|---------|-------------|
| ID_Envase | Primary key identifying the packaging type. |
| Tipo_Envase | Name of the packaging type. |
| Descripcion | Description of the packaging characteristics or intended use. |
| estReg | Record status (e.g., Active or Inactive). |

### Relationships

- One packaging type can be referenced by many **Traslado** records (1:N).

## TR_Tipo_Tratamiento

Stores the available treatment methods that can be applied to hazardous waste before or after transportation. This table standardizes treatment classifications used by the system.

| Column | Description |
|---------|-------------|
| ID_Tratamiento | Primary key identifying the treatment method. |
| Tipo_Tratamiento | Name of the treatment method. |
| Descripcion | Detailed description of the treatment process. |
| estReg | Record status (e.g., Active or Inactive). |

### Relationships

- One treatment type can be referenced by many **Traslado** records (1:N).

## TR_Tipo_Transporte

Stores the available transportation methods used by transport companies when moving hazardous waste.

| Column | Description |
|---------|-------------|
| ID_Tipo_Transporte | Primary key identifying the transportation type. (**actual column name**) |
| Nombre_Transporte | Name of the transportation method. (**actual column name**) |
| Descripcion | Description of the transportation method. |
| estReg | Record status (e.g., Active or Inactive). |

### Relationships

- One transportation type can be referenced by many **Traslado_Transportista** records (1:N).

## Residuo_Estandarizado

Stores the standardized classification of hazardous waste according to regulatory or organizational standards. This catalog ensures that all waste records use consistent classifications.

| Column | Description |
|---------|-------------|
| Cod_Estandar | Primary key identifying the standardized waste code. |
| Nombre_Estandar | Standardized waste classification name. (**actual column name; no Descripcion column**) |
| estReg | Record status (e.g., Active or Inactive). |

### Relationships

- One standardized waste category can classify many **Residuo** records (1:N).

## Constituyente

Stores the chemical constituents that may be present in hazardous waste. Since a waste can contain multiple constituents and a constituent can appear in many wastes, this table participates in a many-to-many relationship.

| Column | Description |
|---------|-------------|
| Cod_Constituyente | Primary key identifying the constituent. (**VARCHAR(20), not INT**) |
| Nombre_Constituyente | Chemical constituent name. (**actual column name**) |
| Otros_Datos | Additional data about the constituent. (**TEXT; replaces Descripcion**) |
| estReg | Record status (e.g., Active or Inactive). |

### Relationships

- Participates in a many-to-many relationship with **Residuo** through the **Residuo_Constituyente** table.

## Region

Stores the geographical regions where authorized waste destination facilities are located.

| Column | Description |
|---------|-------------|
| ID_Region | Primary key identifying the region. |
| Nombre_Region | Name of the geographical region. |
| estReg | Record status (e.g., Active or Inactive). |

### Relationships

- One region can contain many **Destino** facilities (1:N).

## Empresa_Productora

Stores the companies responsible for generating hazardous waste. Each producer company can generate multiple waste records throughout its operations.

| Column | Description |
|---------|-------------|
| NIF_Empresa | Primary key identifying the producer company. |
| Nombre_Empresa | Official name of the company. |
| Ciudad_Empresa | City where the company is located. |
| Actividad | Primary economic activity of the company. |
| Otros_Datos | Additional information about the company. |
| estReg | Record status (e.g., Active or Inactive). |

### Relationships

- One producer company can generate many **Residuo** records (1:N).

## Empresa_Transportista

Stores the companies authorized to transport hazardous waste between producer companies and destination facilities.

| Column | Description |
|---------|-------------|
| NIF_Transportista | Primary key identifying the transport company. |
| Nombre_Transportista | Official name of the transport company. |
| Ciudad_Transportista | City where the company is located. |
| Otros_Datos | Additional information about the company. |
| estReg | Record status (e.g., Active or Inactive). |

### Relationships

- Participates in a many-to-many relationship with **Traslado** through **Traslado_Transportista**.

## Destino

Stores the authorized facilities where hazardous waste is delivered for storage, treatment, recycling, or disposal.

| Column | Description |
|---------|-------------|
| Cod_Destino | Primary key identifying the destination facility. |
| ID_Region | Foreign key referencing the region where the facility is located. |
| Nombre_Destino | Name of the destination facility. |
| Ciudad_Destino | City where the facility is located. |
| Capacidad_Maxima | Maximum waste capacity supported by the facility. |
| Capacidad_Actual | Current occupied capacity. |
| estReg | Record status (e.g., Active or Inactive). |

### Relationships

- Belongs to one **Region** (N:1).
- One destination can receive many **Traslado** records (1:N).

## Residuo

Stores hazardous waste generated by producer companies. Each record contains information about its classification, toxicity level, and total quantity generated.

| Column | Description |
|---------|-------------|
| Cod_Residuo | Primary key identifying the waste record. |
| NIF_Empresa | Foreign key referencing the producer company. |
| Cod_Estandar | Foreign key referencing the standardized waste classification. |
| ID_Toxicidad | Foreign key referencing the toxicity level. |
| Cantidad_Total | Total quantity of waste generated. |
| Otros_Datos | Additional data. (**TEXT; no Nombre_Residuo or Descripcion columns**) |
| estReg | Record status (e.g., Active or Inactive). |

### Relationships

- Belongs to one **Empresa_Productora** (N:1).
- Belongs to one **Residuo_Estandarizado** (N:1).
- Belongs to one **TR_Nivel_Toxicidad** (N:1).
- Can contain multiple **Constituyente** records through **Residuo_Constituyente** (N:M).
- Can participate in multiple **Traslado** records (1:N).

# Associative and Transactional Tables

## Residuo_Constituyente

Associative table that represents the chemical composition of hazardous waste. Since one waste may contain several constituents and one constituent may appear in multiple wastes, this table resolves the many-to-many relationship.

| Column | Description |
|---------|-------------|
| Cod_Residuo | Foreign key referencing the waste. |
| ID_Constituyente | Foreign key referencing the chemical constituent. |
| Cantidad | Quantity of the constituent contained in the waste. |
| estReg | Record status (e.g., Active or Inactive). |

### Relationships

- References one **Residuo** (N:1).
- References one **Constituyente** (N:1).
- Implements the many-to-many relationship between **Residuo** and **Constituyente**.

## Traslado

Stores every shipment of hazardous waste from a producer company to an authorized destination facility. It records the shipment date, quantity transported, packaging type, treatment method, and arrival information.

| Column | Description |
|---------|-------------|
| ID_Traslado | Primary key identifying the shipment. |
| Cod_Residuo | Foreign key referencing the transported waste. |
| Cod_Destino | Foreign key referencing the destination facility. |
| Fecha_Envio | Date when the shipment leaves the producer company. |
| Cantidad_Trasladada | Quantity of waste transported. |
| ID_Envase | Foreign key referencing the packaging type. |
| ID_Tratamiento | Foreign key referencing the treatment method. |
| Fecha_Llegada | Date when the shipment arrives at the destination. |
| estReg | Record status (e.g., Active or Inactive). |

### Relationships

- References one **Residuo** (N:1).
- References one **Destino** (N:1).
- References one **TR_Tipo_Envase** (N:1).
- References one **TR_Tipo_Tratamiento** (N:1).
- Can involve multiple transport companies through **Traslado_Transportista** (1:N).

### Constraints

- The combination **(Cod_Residuo, Fecha_Envio, Cod_Destino)** must be unique, preventing duplicate shipment records for the same waste, destination, and shipping date.

## Traslado_Transportista

Associative table that records which transport companies participate in each shipment. It also stores transportation-specific information such as the transportation method, distance traveled, and transportation cost.

| Column | Description |
|---------|-------------|
| ID_Traslado | Foreign key referencing the shipment. |
| NIF_Transportista | Foreign key referencing the transport company. |
| ID_Tipo_Transporte | Foreign key referencing the transportation type. |
| Kms_Recorridos | Distance traveled during the shipment. (**actual column name**) |
| Costo | Transportation cost associated with the shipment. |
| estReg | Record status (e.g., Active or Inactive). |

### Relationships

- References one **Traslado** (N:1).
- References one **Empresa_Transportista** (N:1).
- References one **TR_Tipo_Transporte** (N:1).
- Implements the many-to-many relationship between **Traslado** and **Empresa_Transportista**.

---

### Standard Entity State (`estReg`)

Every table in the database includes an operational state field named `estReg`, which acts as the `State_Record`:

| Constant | Value | Database Representation | Meaning |
| --- | --- | --- | --- |
| `STATE_ACTIVE` | `'A'` | `CHAR(1) DEFAULT 'A'`Record is active and operational
| `STATE_INACTIVE` | `'I'` | `CHAR(1)` | Record is temporarily inactivated
| `STATE_DELETED` | `'*'` | `CHAR(1)` | Record is logically eliminated. |

### Schema Categories (from `postresiduos.sql`)

* **Reference Tables:** `TR_Nivel_Toxicidad`, `TR_Tipo_Envase`, `TR_Tipo_Tratamiento`, `TR_Tipo_Transporte`, `Residuo_Estandarizado`, `Constituyente`, `Region` **Master Tables:** `Empresa_Productora`, `Empresa_Transportista`, `Destino`, `Residuo` **Transactional & Associative Tables:** `Residuo_Constituyente`, `Traslado`, `Traslado_Transportista`---

## Backend Design (Java API)

### 1. Layered Responsibilities

* **Controller Layer:** Receives HTTP requests from the JavaFX client, deserializes JSON payloads into DTOs, and returns standardized HTTP response codes (e.g., `200 OK`, `201 Created`, `400 Bad Request`, `500 Internal Server Error`).
* **Service Layer:** Enforces business rules and validation before invoking database transactions **Repository Layer:** Uses raw JDBC or lightweight mapping (e.g., Spring JDBC / MyBatis) to execute parameterized SQL sentences against the PostgreSQL container, preventing SQL injection.

### 2. Core REST API Specification

To support generalized maintenance operations across any table (e.g., `TR_Nivel_Toxicidad` or `Residuo`), the backend exposes standardized REST endpoints:

| HTTP Method | Endpoint Pattern | Description | Corresponding Requirement |
| --- | --- | --- | --- |
| **GET** | `/api/v1/{resource}?state=A` | Fetches all active records for the grid Req 2-1| **GET** | `/api/v1/{resource}/{id}` | Fetches a specific record by ID Req 2-2, Req 3-1| **POST** | `/api/v1/{resource}` | Validates and inserts a new active record (`estReg='A'`). | Req 1-1, Req 7| **PUT** | `/api/v1/{resource}/{id}` | Updates permitted editable fields of an existing record Req 3-2, Req 3-5| **PATCH** | `/api/v1/{resource}/{id}/state` | Modifies only the `estReg` value (`*`, `I`, or `A`). | Req 1-3 to 1-5, Req 4
### 3. Backend Validation Rules (`Req 7`)

Before committing any transaction, the Service layer verifies:

1. **Mandatory Fields:** Rejects requests if required fields (e.g., `Nombre_Envase` or `NIF_Empresa`) are null or empty.
2. **Primary Key Duplication:** Queries the repository to ensure unique identifiers (such as `Cod_Residuo` or `ID_Toxicidad`) do not already exist during creation.
3. **Data Length Limits:** Enforces SQL column constraints (e.g., rejecting strings exceeding `VARCHAR(100)` or `VARCHAR(250)`).
4. **Field Protection:** Ignores any attempts to modify record primary keys or manually alter the `estReg` field through standard update endpoints.

---

## GUI Design (JavaFX Desktop Client)

The desktop client is composed of custom JavaFX components coordinated by a central controller (`MainWindow.java`).

### 1. `GridData` Component (`Req 2`)

* Implemented using JavaFX `TableView`.
* On application startup, it calls the GET endpoint to populate the table exclusively with active records (`estReg = 'A'`).
* Selecting a row triggers an event listener that passes the selected record's data to the `RegisterForm` Displays an empty state message when no records are returned by the API### 2. `RegisterForm` Component (`Req 1, 3, 5, 6`)

* Contains JavaFX `TextField`, `TextArea`, and `ComboBox` controls matching the columns of the active entity.
* Includes a read-only visual badge/label indicating the current `State_Record` (`Active`, `Inactive`, or `Deleted`) Controls button availability dynamically based on application mode:

| Button Command | Enabled When... | Action / Behavior |
| --- | --- | --- |
| **Add** | Always enabled | Clears input fields, sets state to `A`, enters `ADD` mode
| **Modify** | Row selected in grid | Loads row data, locks Primary Key field, enters `MODIFY` mode
| **Delete** | Row selected in grid | Sends `PATCH` request setting `estReg = '*'` (Logical Elimination). |
| **Inactivate** | Row selected (`estReg = 'A'`) | Sends `PATCH` request setting `estReg = 'I'`. |
| **Reactivate** | Row selected (`estReg = 'I'`) | Sends `PATCH` request setting `estReg = 'A'`. |
| **Update** | `ADD` or `MODIFY` mode active | Verifies pending operation, validates input, sends `POST` or `PUT`, refreshes grid
| **Cancel** | `ADD` or `MODIFY` mode active | Discards unsaved changes, clears form, exits active operation mode

---

## Operation Flows

### 1. Record Creation (Add Flow)

1. **Operator** clicks **Add**. `RegisterForm` clears all inputs, defaults the State indicator to `A`, and sets internal state to `ADD` mode. **Operator** inputs data and clicks **Update**. JavaFX client validates field presence and length locally, then sends a `POST` request with JSON payload. Backend performs business validation and executes `INSERT INTO...` via JDBC. Upon a `201 Created` response, JavaFX refreshes `GridData`, clears `RegisterForm`, and resets operation mode### 2. Record Modification (Modify Flow)

1. **Operator** selects a row in `GridData` and clicks **Modify**. `RegisterForm` populates with row data, locks the ID field and State indicator, and transitions to `MODIFY` mode. **Operator** updates editable values and clicks **Update**. Client sends a `PUT` request to `/api/v1/{resource}/{id}`. Backend updates the record in PostgreSQL, leaving `estReg` and the ID untouched.
6. Upon `200 OK`, `GridData` refreshes and the form resets### 3. State Management (Delete / Inactivate / Reactivate)

1. **Operator** selects a row in `GridData` and clicks **Delete**, **Inactivate**, or **Reactivate**. Client immediately sends a `PATCH` request with the new state target (`*`, `I`, or `A`) without requiring an **Update** confirmation step. Backend executes `UPDATE table SET estReg = ? WHERE id = ?` via JDBC. Client refreshes `GridData` immediately; if inactivated or deleted, the record disappears from the active grid view---

## Error Handling & Connectivity Strategy

To satisfy database connectivity (`Req 8`) and error handling (`Req 9`)| Scenario | System Behavior & Mitigation |
| --- | --- |
| **Backend / DB Unreachable on Startup** | JavaFX client attempts an initial health check. If unreachable, it displays an error modal notifying the Operator and disables all maintenance buttons
| **Connection Lost Mid-Session** | If an HTTP request fails due to network or timeout issues, the client catches the `ConnectException`, displays an informative warning, and prevents data mutation until connectivity is restored
| **Validation Failure (Backend / Frontend)** | The UI catches `400 Bad Request` payloads containing validation error lists and highlights every invalid field in `RegisterForm` using visual error badgeshe database is never touched
| **SQL / Database Transaction Error** | The Java backend catches JDBC exceptions, rolls back the transaction to preserve consistency, and returns a clean error message without exposing database schema or SQL traces to the GUI
| **Update Without Pending Operation** | If the Operator clicks **Update** without being in `ADD` or `MODIFY` mode, the GUI blocks the action and displays an informative alert ("No pending operation to confirm")

---

## Key Design Decisions

1. **Decoupled GUI and Database:** By placing a Java REST backend between JavaFX and PostgreSQL, the application prevents direct database exposure to client machines and enforces validation logic centrally.
2. **Containerized Data Layer:** Using Docker for PostgreSQL and the backend ensures reproducible environments where `postresiduos.sql` can be deployed identically across developer and production setups.
3. **Uniform State Architecture:** Relying exclusively on `estReg` across all tables simplifies backend DAO design and guarantees that logical deletions (`*`) never cause relational foreign-key violations.
4. **Stateless HTTP Backend:** Facilitates easy scaling and testing of the business logic independently of the JavaFX desktop interface.


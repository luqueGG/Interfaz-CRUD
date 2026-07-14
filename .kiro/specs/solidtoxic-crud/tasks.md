# Implementation Tasks — SolidToxic CRUD

## Overview

Tasks are organized in dependency order: project scaffolding → database container → backend (Spring Boot + Spring JDBC) → GUI (JavaFX + Maven). Each group must be completed before the next can be wired together.

---

## Phase 0 — Project Scaffold & Repository Layout

- [ ] **0.1** Create the root directory `solidtoxic-system/` with the following skeleton:
  ```
  solidtoxic-system/
  ├── docker-compose.yml
  ├── db/
  │   └── postresiduos.sql        ← copy from h:\Universidad\Database\Ressol\db\
  ├── backend/
  │   ├── Dockerfile
  │   └── pom.xml
  └── gui/
      └── pom.xml
  ```

- [ ] **0.2** Write `docker-compose.yml` defining two services:
  - `db`: image `postgres:16`, mounts `./db/postresiduos.sql` into `/docker-entrypoint-initdb.d/`, exposes port `5432`, sets env vars `POSTGRES_DB`, `POSTGRES_USER`, `POSTGRES_PASSWORD`.
  - `backend`: built from `./backend/Dockerfile`, depends on `db`, exposes port `8080`, receives DB connection env vars.

---

## Phase 1 — Database Container

- [ ] **1.1** Copy `postresiduos.sql` (already written) into `solidtoxic-system/db/`. Verify the file is picked up by the PostgreSQL init entrypoint on `docker compose up`.

- [ ] **1.2** Validate schema initialization: run `docker compose up db`, connect with `psql` or a DB client, and confirm all 12 tables exist with the correct columns and constraints:
  - Reference: `TR_Nivel_Toxicidad`, `TR_Tipo_Envase`, `TR_Tipo_Tratamiento`, `TR_Tipo_Transporte`, `Residuo_Estandarizado`, `Constituyente`, `Region`
  - Master: `Empresa_Productora`, `Empresa_Transportista`, `Destino`, `Residuo`
  - Transactional/Associative: `Residuo_Constituyente`, `Traslado`, `Traslado_Transportista`

---

## Phase 2 — Backend: Spring Boot Project Setup

- [ ] **2.1** Configure `backend/pom.xml` with the following dependencies:
  - `spring-boot-starter-web` (REST controllers, Jackson JSON)
  - `spring-boot-starter-jdbc` (Spring JDBC / `JdbcTemplate`)
  - `postgresql` JDBC driver
  - `spring-boot-starter-validation` (Bean Validation for DTO constraints)
  - `spring-boot-maven-plugin` for fat-JAR packaging

- [ ] **2.2** Create `backend/src/main/resources/application.properties`:
  - `spring.datasource.url`, `spring.datasource.username`, `spring.datasource.password` reading from environment variables (to match Docker Compose injection).
  - `server.port=8080`

- [ ] **2.3** Write `backend/Dockerfile`:
  - Multi-stage: stage 1 uses `maven:3.9-eclipse-temurin-21` to build the fat JAR; stage 2 uses `eclipse-temurin:21-jre` to run it.
  - Copies the fat JAR and exposes port `8080`.

- [ ] **2.4** Create the base package structure `com.solidtoxic`:
  - `controller/`, `service/`, `repository/`, `model/dto/`, `exception/`

- [ ] **2.5** Create a `GlobalExceptionHandler` (`@RestControllerAdvice`) in `exception/` that:
  - Maps `ValidationException` / `MethodArgumentNotValidException` → `400 Bad Request` with a list of field errors (Req 7-4).
  - Maps `DuplicateKeyException` (Spring JDBC) → `409 Conflict`.
  - Maps `DataAccessException` → `500 Internal Server Error` without exposing SQL or schema details (Req 9-2).
  - Maps `NoSuchElementException` → `404 Not Found`.

- [ ] **2.6** Create a `HealthController` with `GET /api/v1/health` returning `200 OK` — used by the JavaFX client on startup to verify backend reachability (Req 8-1, 8-2).

---

## Phase 3 — Backend: Reference Tables (7 tables)

For each of the 7 reference tables, implement the full stack: Model → DTO → Repository → Service → Controller.  
Column names must match the SQL schema exactly.

> **Repository contract (applies to all tables):**  
> Every repository must implement these methods:
> - `findAll()` → returns **all records** with no state filter (used by the grid).
> - `findByState(String state)` → returns records matching the given `estReg` value (used by FK ComboBox loaders to fetch selectable options).
> - `findById(id)`, `insert(entity)`, `update(entity)` (never touches `estReg` or PK), `updateState(id, newState)`.
>
> **Controller contract (applies to all tables):**  
> Every controller must expose:
> - `GET /api/v1/{resource}` — all records (no filter).
> - `GET /api/v1/{resource}?state={state}` — filtered by `estReg`.
> - `GET /api/v1/{resource}/{id}`, `POST`, `PUT /{id}`, `PATCH /{id}/state`.

### 3.A — TR_Nivel_Toxicidad (`/api/v1/toxicidad`)
Columns: `ID_Toxicidad INT PK`, `Nivel VARCHAR(20)`, `Descripcion VARCHAR(250)`, `estReg CHAR(1)`

- [ ] **3.A.1** Create `NivelToxicidad` model and `NivelToxicidadDTO` (with Bean Validation: `@NotBlank` on `Nivel`, `@Size(max=250)` on `Descripcion`).
- [ ] **3.A.2** Create `NivelToxicidadRepository` using `JdbcTemplate`:
  - `findAll()` → `SELECT ... ORDER BY ID_Toxicidad` (all records, no state filter)
  - `findByState(String state)` → `SELECT ... WHERE estReg = ?` (used by FK ComboBox loaders)
  - `findById(int id)`
  - `insert(NivelToxicidad)`
  - `update(NivelToxicidad)` — never touches `estReg` or PK
  - `updateState(int id, String newState)`
- [ ] **3.A.3** Create `NivelToxicidadService` enforcing:
  - Mandatory fields check (Req 7-1).
  - Duplicate PK check on insert (Req 7-2).
  - Field length validation (Req 7-3).
- [ ] **3.A.4** Create `NivelToxicidadController` exposing:
  - `GET /api/v1/toxicidad` — returns all records (no state filter)
  - `GET /api/v1/toxicidad?state={state}` — returns records filtered by `estReg` value
  - `GET /api/v1/toxicidad/{id}`
  - `POST /api/v1/toxicidad`
  - `PUT /api/v1/toxicidad/{id}`
  - `PATCH /api/v1/toxicidad/{id}/state`

### 3.B — TR_Tipo_Envase (`/api/v1/envase`)
Columns: `ID_Envase INT PK`, `Nombre_Envase VARCHAR(50)`, `Descripcion VARCHAR(250)`, `estReg CHAR(1)`

- [ ] **3.B.1** Model + DTO (`@NotBlank` on `Nombre_Envase`, `@Size(max=50)`, `@Size(max=250)` on `Descripcion`).
- [ ] **3.B.2** `TipoEnvaseRepository` (same 6 methods as 3.A.2: `findAll`, `findByState`, `findById`, `insert`, `update`, `updateState`).
- [ ] **3.B.3** `TipoEnvaseService` (same validation rules as 3.A.3).
- [ ] **3.B.4** `TipoEnvaseController` at `/api/v1/envase` (same 6 endpoints as 3.A.4).

### 3.C — TR_Tipo_Tratamiento (`/api/v1/tratamiento`)
Columns: `ID_Tratamiento INT PK`, `Nombre_Tratamiento VARCHAR(50)`, `Descripcion VARCHAR(250)`, `estReg CHAR(1)`

- [ ] **3.C.1** Model + DTO.
- [ ] **3.C.2** `TipoTratamientoRepository`.
- [ ] **3.C.3** `TipoTratamientoService`.
- [ ] **3.C.4** `TipoTratamientoController` at `/api/v1/tratamiento`.

### 3.D — TR_Tipo_Transporte (`/api/v1/transporte`)
Columns: `ID_Tipo_Transporte INT PK`, `Nombre_Transporte VARCHAR(50)`, `Descripcion VARCHAR(250)`, `estReg CHAR(1)`

- [ ] **3.D.1** Model + DTO. Note: PK column is `ID_Tipo_Transporte` (not `ID_Transporte`).
- [ ] **3.D.2** `TipoTransporteRepository`.
- [ ] **3.D.3** `TipoTransporteService`.
- [ ] **3.D.4** `TipoTransporteController` at `/api/v1/transporte`.

### 3.E — Residuo_Estandarizado (`/api/v1/estandar`)
Columns: `Cod_Estandar INT PK`, `Nombre_Estandar VARCHAR(100)`, `estReg CHAR(1)`
Note: No `Descripcion` column in the actual schema.

- [ ] **3.E.1** Model + DTO (`@NotBlank` on `Nombre_Estandar`, `@Size(max=100)`).
- [ ] **3.E.2** `ResiduoEstandarizadoRepository`.
- [ ] **3.E.3** `ResiduoEstandarizadoService`.
- [ ] **3.E.4** `ResiduoEstandarizadoController` at `/api/v1/estandar`.

### 3.F — Constituyente (`/api/v1/constituyente`)
Columns: `Cod_Constituyente VARCHAR(20) PK`, `Nombre_Constituyente VARCHAR(100)`, `Otros_Datos TEXT`, `estReg CHAR(1)`
Note: PK is `VARCHAR(20)`, not an integer. Text field is `Otros_Datos`, not `Descripcion`.

- [ ] **3.F.1** Model + DTO (`@NotBlank` and `@Size(max=20)` on PK for insert, `@Size(max=100)` on `Nombre_Constituyente`).
- [ ] **3.F.2** `ConstituyenteRepository` (PK is String).
- [ ] **3.F.3** `ConstituyenteService`.
- [ ] **3.F.4** `ConstituyenteController` at `/api/v1/constituyente`.

### 3.G — Region (`/api/v1/region`)
Columns: `ID_Region INT PK`, `Nombre_Region VARCHAR(50)`, `estReg CHAR(1)`

- [ ] **3.G.1** Model + DTO.
- [ ] **3.G.2** `RegionRepository`.
- [ ] **3.G.3** `RegionService`.
- [ ] **3.G.4** `RegionController` at `/api/v1/region`.

---

## Phase 4 — Backend: Master Tables (4 tables)

### 4.A — Empresa_Productora (`/api/v1/productora`)
Columns: `NIF_Empresa VARCHAR(20) PK`, `Nombre_Empresa VARCHAR(100)`, `Ciudad_Empresa VARCHAR(50)`, `Actividad VARCHAR(100)`, `Otros_Datos TEXT`, `estReg CHAR(1)`

- [ ] **4.A.1** Model + DTO (`@NotBlank` on `NIF_Empresa`, `Nombre_Empresa`, `Ciudad_Empresa`, `Actividad`; size constraints per column).
- [ ] **4.A.2** `EmpresaProductoraRepository` (PK is String).
- [ ] **4.A.3** `EmpresaProductoraService`.
- [ ] **4.A.4** `EmpresaProductoraController` at `/api/v1/productora`.

### 4.B — Empresa_Transportista (`/api/v1/transportista`)
Columns: `NIF_Transportista VARCHAR(20) PK`, `Nombre_Transportista VARCHAR(100)`, `Ciudad_Transportista VARCHAR(50)`, `Otros_Datos TEXT`, `estReg CHAR(1)`

- [ ] **4.B.1** Model + DTO.
- [ ] **4.B.2** `EmpresaTransportistaRepository`.
- [ ] **4.B.3** `EmpresaTransportistaService`.
- [ ] **4.B.4** `EmpresaTransportistaController` at `/api/v1/transportista`.

### 4.C — Destino (`/api/v1/destino`)
Columns: `Cod_Destino VARCHAR(20) PK`, `ID_Region INT FK→Region`, `Nombre_Destino VARCHAR(100)`, `Ciudad_Destino VARCHAR(50)`, `Capacidad_Maxima DECIMAL(12,2)`, `Capacidad_Actual DECIMAL(12,2)`, `Otros_Datos TEXT`, `estReg CHAR(1)`

- [ ] **4.C.1** Model + DTO (`@NotBlank` on `Cod_Destino`, `Nombre_Destino`, `Ciudad_Destino`; `@NotNull` on `ID_Region`, `Capacidad_Maxima`, `Capacidad_Actual`; `@Positive` on capacities).
- [ ] **4.C.2** `DestinoRepository` — FK to `Region` means service must validate `ID_Region` exists and is active before insert/update.
- [ ] **4.C.3** `DestinoService` (includes FK existence check).
- [ ] **4.C.4** `DestinoController` at `/api/v1/destino`.

### 4.D — Residuo (`/api/v1/residuo`)
Columns: `Cod_Residuo VARCHAR(20) PK`, `NIF_Empresa VARCHAR(20) FK→Empresa_Productora`, `Cod_Estandar INT FK→Residuo_Estandarizado`, `ID_Toxicidad INT FK→TR_Nivel_Toxicidad`, `Cantidad_Total DECIMAL(12,2)`, `Otros_Datos TEXT`, `estReg CHAR(1)`
Note: No `Nombre_Residuo` column in the actual schema.

- [ ] **4.D.1** Model + DTO (`@NotBlank` on `Cod_Residuo`; `@NotNull` on all FKs and `Cantidad_Total`; `@Positive` on `Cantidad_Total`).
- [ ] **4.D.2** `ResiduoRepository` — 3 FK dependencies.
- [ ] **4.D.3** `ResiduoService` (validates all 3 FK references are active before insert/update).
- [ ] **4.D.4** `ResiduoController` at `/api/v1/residuo`.

---

## Phase 5 — Backend: Transactional & Associative Tables (3 tables)

### 5.A — Residuo_Constituyente (`/api/v1/residuo-constituyente`)
Columns: `Cod_Residuo VARCHAR(20) FK PK`, `Cod_Constituyente VARCHAR(20) FK PK`, `Cantidad DECIMAL(10,2)`, `estReg CHAR(1)`
Composite PK.

- [ ] **5.A.1** Model + DTO (`@NotBlank` on both PK parts; `@NotNull @Positive` on `Cantidad`).
- [ ] **5.A.2** `ResiduoConstituyenteRepository`:
  - `findByResiduo(String codResiduo)` — lists all constituents for a waste record.
  - `findByCompositeKey(String codResiduo, String codConstituyente)`
  - `insert(...)`, `update(...)` (only `Cantidad`), `updateState(...)`
- [ ] **5.A.3** `ResiduoConstituyenteService` (validates both FK references active).
- [ ] **5.A.4** `ResiduoConstituyenteController` at `/api/v1/residuo-constituyente`.

### 5.B — Traslado (`/api/v1/traslado`)
Columns: `ID_Traslado INT PK`, `Cod_Residuo VARCHAR(20) FK`, `Cod_Destino VARCHAR(20) FK`, `Fecha_Envio DATE`, `Cantidad_Trasladada DECIMAL(12,2)`, `ID_Envase INT FK`, `ID_Tratamiento INT FK`, `Fecha_Llegada DATE`, `Otros_Datos TEXT`, `estReg CHAR(1)`
Unique constraint: `(Cod_Residuo, Fecha_Envio, Cod_Destino)`

- [ ] **5.B.1** Model + DTO (`@NotNull` on all FK fields, `Fecha_Envio`, `Cantidad_Trasladada`; `@Positive` on `Cantidad_Trasladada`).
- [ ] **5.B.2** `TrasladoRepository` (4 FK dependencies; handle `DataIntegrityViolationException` from the unique constraint).
- [ ] **5.B.3** `TrasladoService` (validates all 4 FK references; catches duplicate unique constraint violation and re-throws as a readable `ValidationException`).
- [ ] **5.B.4** `TrasladoController` at `/api/v1/traslado`.

### 5.C — Traslado_Transportista (`/api/v1/traslado-transportista`)
Columns: `ID_Traslado INT FK PK`, `NIF_Transportista VARCHAR(20) FK PK`, `ID_Tipo_Transporte INT FK`, `Kms_Recorridos DECIMAL(8,2)`, `Costo DECIMAL(10,2)`, `estReg CHAR(1)`
Composite PK. Note: column is `Kms_Recorridos` (not `Distancia`).

- [ ] **5.C.1** Model + DTO (`@NotNull` on all fields; `@Positive` on `Kms_Recorridos` and `Costo`).
- [ ] **5.C.2** `TrasladoTransportistaRepository` (3 FK dependencies; composite key lookup).
- [ ] **5.C.3** `TrasladoTransportistaService`.
- [ ] **5.C.4** `TrasladoTransportistaController` at `/api/v1/traslado-transportista`.

---

## Phase 6 — GUI: JavaFX Maven Project Setup

- [ ] **6.1** Configure `gui/pom.xml`:
  - Parent: `spring-boot-starter-parent` or standalone Maven project.
  - Dependencies: `javafx-controls`, `javafx-fxml` (OpenJFX 21); `jackson-databind` (JSON); `java-http-client` (JDK built-in `java.net.http.HttpClient` — no extra dep needed).
  - Plugin: `javafx-maven-plugin` for `mvn javafx:run`.

- [ ] **6.2** Create the main application entry point `MainApp.java` (`Application` subclass) and `MainWindow.java` as the root layout coordinator:
  - Initializes the `HttpClient` singleton used across all REST calls.
  - On startup, calls `GET /api/v1/health`; if unreachable, shows an error modal and disables all buttons (Req 8-2, 8-3).

- [ ] **6.3** Create `client/ApiClient.java`:
  - Wraps `java.net.http.HttpClient`.
  - Methods: `get(String path)`, `post(String path, Object body)`, `put(String path, Object body)`, `patch(String path, Object body)`.
  - Catches `ConnectException` / `IOException` and throws a `BackendUnavailableException` (Req 8-4, 9-1).
  - Base URL configured via a property file or constant pointing to `http://localhost:8080`.

- [ ] **6.4** Create `util/AlertUtil.java` — static helpers for `showError(String msg)`, `showWarning(String msg)`, `showInfo(String msg)` using JavaFX `Alert`.

- [ ] **6.5** Create `util/FormValidator.java` — client-side field presence and length checks that mirror backend rules, providing instant feedback before sending any HTTP request (Req 7-1 to 7-3 on the client side).

---

## Phase 7 — GUI: Shared Components

- [ ] **7.1** Implement `component/GridData.java` (generic `TableView<Map<String, Object>>`):
  - Accepts a list of column descriptors and a list of row maps.
  - Fires a `RowSelectedEvent` with the full row data when the operator selects a row (Req 2-2).
  - Displays an empty-state label when the list is empty (Req 2-5).
  - Applies row-level CSS styling based on the `estReg` value: default style for `A`, muted/grey style for `I`, strikethrough or red-tinted style for `*` (Req 2-1).

- [ ] **7.2** Implement `component/RegisterForm.java` (generic form):
  - Accepts a list of field descriptors (name, label, type, editable, maxLength, **fkEndpoint** optional).
  - Renders `TextField` / `TextArea` controls for plain fields and `ComboBox` for foreign key fields (Req 10-1).
  - FK `ComboBox` controls are populated by calling `GET /api/v1/{fkEndpoint}` (all records) at form initialization and on refresh, and display a human-readable label alongside the stored ID (Req 10-2, 10-3).
  - If a FK endpoint cannot be reached, the ComboBox is disabled and a warning is shown (Req 10-4).
  - Includes a read-only `State_Record` badge showing `Active`, `Inactive`, or `Deleted` (Req 1-1).
  - Exposes `getData()` → `Map<String, String>` and `setData(Map<String, String>)` methods.

- [ ] **7.3** Implement the **7 action buttons** in `RegisterForm` with their enable/disable rules (Req 1 through 6):

  | Button | Enabled When |
  |--------|--------------|
  | Add | Always |
  | Modify | Row selected |
  | Delete | Row selected |
  | Inactivate | Row selected AND `estReg = 'A'` |
  | Reactivate | Row selected AND `estReg = 'I'` |
  | Update | ADD or MODIFY mode active |
  | Cancel | ADD or MODIFY mode active |

- [ ] **7.4** Implement internal operation mode state machine:
  - States: `IDLE`, `ADD`, `MODIFY`.
  - Transitions trigger button enable/disable and form lock/unlock (PK field and `estReg` badge locked in MODIFY mode, Req 3-3, 3-4).

---

## Phase 8 — GUI: Per-Table Maintenance Panels (12 tables)

For each table, create a dedicated panel class extending or composing `MainWindow`. Each panel wires `GridData` and `RegisterForm` to the specific API endpoint. The grid always loads **all records** (no state filter). FK fields are always declared with `fkEndpoint` so `RegisterForm` renders them as `ComboBox` controls.

- [ ] **8.1** `panel/NivelToxicidadPanel.java` — endpoint `/api/v1/toxicidad`, columns: `ID_Toxicidad`, `Nivel`, `Descripcion`, `estReg`. No FK fields.
- [ ] **8.2** `panel/TipoEnvasePanel.java` — endpoint `/api/v1/envase`, columns: `ID_Envase`, `Nombre_Envase`, `Descripcion`, `estReg`. No FK fields.
- [ ] **8.3** `panel/TipoTratamientoPanel.java` — endpoint `/api/v1/tratamiento`, columns: `ID_Tratamiento`, `Nombre_Tratamiento`, `Descripcion`, `estReg`. No FK fields.
- [ ] **8.4** `panel/TipoTransportePanel.java` — endpoint `/api/v1/transporte`, columns: `ID_Tipo_Transporte`, `Nombre_Transporte`, `Descripcion`, `estReg`. No FK fields.
- [ ] **8.5** `panel/ResiduoEstandarizadoPanel.java` — endpoint `/api/v1/estandar`, columns: `Cod_Estandar`, `Nombre_Estandar`, `estReg`. No FK fields.
- [ ] **8.6** `panel/ConstituyentePanel.java` — endpoint `/api/v1/constituyente`, columns: `Cod_Constituyente`, `Nombre_Constituyente`, `Otros_Datos`, `estReg`. No FK fields.
- [ ] **8.7** `panel/RegionPanel.java` — endpoint `/api/v1/region`, columns: `ID_Region`, `Nombre_Region`, `estReg`. No FK fields.
- [ ] **8.8** `panel/EmpresaProductoraPanel.java` — endpoint `/api/v1/productora`, columns: `NIF_Empresa`, `Nombre_Empresa`, `Ciudad_Empresa`, `Actividad`, `Otros_Datos`, `estReg`. No FK fields.
- [ ] **8.9** `panel/EmpresaTransportistaPanel.java` — endpoint `/api/v1/transportista`, columns: `NIF_Transportista`, `Nombre_Transportista`, `Ciudad_Transportista`, `Otros_Datos`, `estReg`. No FK fields.
- [ ] **8.10** `panel/DestinoPanel.java` — endpoint `/api/v1/destino`, FK field `ID_Region` rendered as a `ComboBox` populated from `GET /api/v1/region` (all records); label shows `Nombre_Region`.
- [ ] **8.11** `panel/ResiduoPanel.java` — endpoint `/api/v1/residuo`, FK fields:
  - `NIF_Empresa` → `ComboBox` from `GET /api/v1/productora`, label shows `Nombre_Empresa`.
  - `Cod_Estandar` → `ComboBox` from `GET /api/v1/estandar`, label shows `Nombre_Estandar`.
  - `ID_Toxicidad` → `ComboBox` from `GET /api/v1/toxicidad`, label shows `Nivel`.
- [ ] **8.12** `panel/ResiduoConstituyentePanel.java` — endpoint `/api/v1/residuo-constituyente`, FK fields:
  - `Cod_Residuo` → `ComboBox` from `GET /api/v1/residuo`, label shows `Cod_Residuo`.
  - `Cod_Constituyente` (column name `ID_Constituyente` in table) → `ComboBox` from `GET /api/v1/constituyente`, label shows `Nombre_Constituyente`.
- [ ] **8.13** `panel/TrasladoPanel.java` — endpoint `/api/v1/traslado`, FK fields:
  - `Cod_Residuo` → `ComboBox` from `GET /api/v1/residuo`.
  - `Cod_Destino` → `ComboBox` from `GET /api/v1/destino`, label shows `Nombre_Destino`.
  - `ID_Envase` → `ComboBox` from `GET /api/v1/envase`, label shows `Nombre_Envase`.
  - `ID_Tratamiento` → `ComboBox` from `GET /api/v1/tratamiento`, label shows `Nombre_Tratamiento`.
- [ ] **8.14** `panel/TrasladoTransportistaPanel.java` — endpoint `/api/v1/traslado-transportista`, FK fields:
  - `ID_Traslado` → `ComboBox` from `GET /api/v1/traslado`.
  - `NIF_Transportista` → `ComboBox` from `GET /api/v1/transportista`, label shows `Nombre_Transportista`.
  - `ID_Tipo_Transporte` → `ComboBox` from `GET /api/v1/transporte`, label shows `Nombre_Transporte`.

---

## Phase 9 — Navigation & Main Window

- [ ] **9.1** Add a navigation mechanism in `MainWindow.java` (e.g., a `MenuBar` or sidebar `ListView`) that lets the Operator switch between the 14 maintenance panels.

- [ ] **9.2** Ensure panel switching clears the active `RegisterForm` state and resets operation mode to `IDLE` to prevent state leaks between panels.

---

## Phase 10 — Integration & End-to-End Validation

- [ ] **10.1** Run `docker compose up --build` and verify both containers start cleanly: DB initializes schema from `postresiduos.sql`, backend connects and health endpoint responds.

- [ ] **10.2** Run the JavaFX client against the live stack and verify end-to-end for at least one reference table (e.g., `TR_Nivel_Toxicidad`):
  - Add a record → appears in grid with `estReg = 'A'` (active style).
  - Modify a record → grid refreshes with new values.
  - Inactivate → record stays in grid, row style changes to muted/inactive.
  - Reactivate → record row style returns to active.
  - Delete → record stays in grid, row style changes to strikethrough/deleted; `estReg = '*'` confirmed in DB.
  - Cancel mid-operation → form clears, no DB change.
  - FK ComboBox fields → populated with all records from the referenced table, correct label shown.

- [ ] **10.3** Verify error scenarios:
  - Submit with blank required field → `400` returned, invalid fields highlighted (Req 7-4).
  - Submit duplicate PK → `409` returned, informative message shown (Req 7-2).
  - Stop the backend container mid-session → client shows connectivity warning and disables buttons (Req 8-3).
  - Restart backend container → client recovers on next action (Req 8-4).

- [ ] **10.4** Update `design.md` with any schema corrections discovered during implementation (the 5 discrepancies noted above: `ID_Tipo_Transporte`, `Cod_Constituyente`, `Residuo_Estandarizado` columns, `Residuo` columns, `Kms_Recorridos`).

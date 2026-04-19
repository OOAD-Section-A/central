# SCM Integration Guidelines

This document details the exact architectural approach we are using to unify the disparate Supply Chain Management subsystems into the `final-scm-application` root.

## 1. Architectural Philosophy: Hexagonal Architecture
When integrating isolated subsystems (like Packing or Warehouse Management), we must ensure that they do NOT become tightly coupled with the monolithic SCM codebase, while still being able to connect to the central MySQL `database-module` and the `scm-exception-handler` registry.

We use the **Adapter Pattern** (often referred to as Ports and Adapters) to achieve this.

![Hexagonal Diagram](https://upload.wikimedia.org/wikipedia/commons/thumb/7/75/Hexagonal_Architecture.svg/500px-Hexagonal_Architecture.svg.png)

## 2. The Integration Package
Every subsystem must contain a dedicated `integration` package at its root level (e.g., `com.scm.packing.integration` or `wms.integration`). No core business logic should ever live here—this package acts as translation layer.

Inside `integration`, create two subdirectories:
- `database`
- `exceptions`

### A. Database Integration
1. **Target Interface (The Port)**: Do not delete the subsystem's original mock or legacy interfaces. Instead, define an interface like `IWMSDatabaseLayer` or `IDatabaseLayer` that exactly matches the subsystem's database needs.
2. **The SCM Adapter**: Create a `SCMDatabaseAdapter` that implements your target interface. This class is allowed to import the `SupplyChainDatabaseFacade` from `database-module` and map the local calls to the SCM standard `createStockMovement(...)`, etc.
3. **The Fallback**: Often, another team's module might not compile on your laptop. Keep a `FallbackDatabaseAdapter` wrapping the old in-memory mock or flat-file persistence.
4. **The Factory**: Create a `DatabaseLayerFactory`. It should use `Class.forName()` to detect if the `SupplyChainDatabaseFacade` is on the classpath. If it is, return the `SCMDatabaseAdapter`; otherwise, return the `Fallback`.

### B. Exception Handling Integration
1. **Target Interface**: Create `IExceptionDispatcher` to abstract how errors are logged.
2. **The SCM Adapter**: Create a `SCMExceptionAdapter` mapping your interface to the master `SCMExceptionHandler.INSTANCE.handle(...)` or subsystem categorised events e.g. `WarehouseMgmtSubsystem.INSTANCE.onInvalidProduct(...)`. Use proper numerical exception mapping (e.g., `159` for Packing, `154` for Warehouse).
3. **The Factory**: Create an `ExceptionDispatcherFactory` matching the exact auto-detection fallback pattern as the database.

## 3. UI and Startup
- Subsystems should function as completely standalone `.jar` dependencies.
- The `scm-dashboard` module will depend on all subsystems.
- For subsystems with GUIs (like Packing), the `scm-dashboard` will launch them.
- Submodules shouldn't attempt to load configuration files via absolute paths—ensure `lib/` paths or `src/main/resources/` paths are correctly relative.

## 4. Multi-Module Standardisation
To integrate a new subsystem:
1. Move the subsystem source code into the `2. Final` directory.
2. Modify its `pom.xml` to define the `<parent>` as `final-scm-application`.
3. Add it to the root `<modules>` list.
4. Add it to `scm-dashboard/pom.xml` under `<dependencies>`.
5. Run `mvn clean install` from the root directory to verify it compiles correctly across the reactor.

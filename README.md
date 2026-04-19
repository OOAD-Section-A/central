# Final SCM Application Hub

This repository contains the integrated, multi-module Maven project for the final Supply Chain Management (SCM) application. 
Multiple standalone subsystems align here under a unified architecture, sharing a centralized database facade and exception handling framework.

## Project Structure

The project adopts a Maven multi-module architecture:

```
2. Final/
├── pom.xml                     # Parent POM managing dependencies and module build order
├── scm-exception-handler/      # Core exception registry and dispatching (External Dependency)
├── database-module/            # Centralized database schema and JDBC/Facade layer
├── packing/                    # Subsystem 1: Packing & Box Management
└── warehouse-management/       # Subsystem 3: Warehouse, Inventory, & Procurement
```

## Integration Principles

All subsystems adhere to the following strict integration guidelines:

1. **Hexagonal Architecture via Adapters:**
   - Subsystems must NOT contain direct business logic relying on MySQL or System.err exceptions.
   - Interfaces (`IDatabaseLayer`, `IWMSExceptionDispatcher`) define subsystem needs.
   - Adapters (`SCMDatabaseAdapter`, `SCMExceptionAdapter`) connect these interfaces to the monolithic application facade.
   - Factory patterns auto-fallback to mock or in-memory layers if the SCM components are absent from the classpath.

2. **Database Integration:**
   - Subsystems use the `SupplyChainDatabaseFacade` exported by the `database-module`.

3. **Exception Handling Integration:**
   - Exception routing runs through `SCMExceptionHandler.INSTANCE.handle()`.
   - Appropriate Category Interfaces (`IWarehouseMgmtSubsystem`, `IPackingRepairsReceiptSubsystem`, etc.) are utilized for typed dispatching.

## How to Build and Run

1. **Build all modules:**
   Open a terminal in the root directory and run:
   ```bash
   mvn clean compile package
   ```
   *Note: This will build the shared database-module, followed by packing and warehouse-management.*

2. **Running the Unified Dashboard:**
   To run the complete interface across platforms reliably, compile the overarching application first, then navigate into the dashboard module:
   ```bash
   mvn clean install
   cd scm-dashboard
   mvn exec:java
   ```

3. **Activating the Packing License Server:**
   The Packing UI Subsystem evaluates a commercial activation check upon boot. If the GUI hangs or fails with a license exception, you must run the server manually in a separate terminal:
   ```bash
   cd packing
   mvn compile exec:java "-Dexec.mainClass=com.scm.packing.licensing.LicenseServer"
   ```
   *When prompted in the terminal, enter the secret activation key:* `SCM-PACK-2026-XRAY`
   
   Once the server says it's running on port 15151, the Packing dashboard will correctly initialize!

4. **Running Subsystems Independently:**
   While the unified UI is being developed, subsystems can still test their `Main` methods.
   For example, to run the Warehouse test phase:
   ```bash
   cd warehouse-management
   mvn exec:java -Dexec.mainClass="Main"
   ```

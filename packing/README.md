# SCM Packing Subsystem

The Packing Subsystem for the Supply Chain Management (SCM) project. Built in **Java** following the **Model-View-Controller (MVC)** architecture with a **Swing-based dark-themed GUI**.

---

## Features

### Core Packing Workflow
- **Multi-Order Consolidation**: Select multiple orders for the **same customer**, and the system automatically merges them into a **single packing job**. Different customers still receive individual packages in parallel.
- **Concurrent Packing**: Every unique customer job spawns its own background `SwingWorker` thread, allowing parallel throughput with live progress monitoring.
- **Strategy-Based Packing**: Truly fragile items (crystal glassware, electronics monitors) trigger the `FragilePackingStrategy` via the **Factory Method** pattern, while standard items use the basic strategy.

### Labeling & Traceability
- **Barcode Generation**: Every packed job receives a unique barcode label encoding `SCM|JobID|OrderIDs|Timestamp`.
- **Traceability View**: View graphical barcodes (Java2D) for any packed job via the "View Barcode" dialog.

### Unitization (Shipping Preparation)
- **Pallet Management**: Group multiple packed jobs onto **Pallets** (default capacity 20) for bulk shipping.
- **Pallet Editing**: Add more jobs to an existing pallet or remove jobs from one via a dedicated management interface in the **"Pallets"** tab.
- **Weight Tracking**: Units automatically calculate total weight across all merged orders and jobs.

### Integration
- **SCM Database Module**: Uses the shared `SupplyChainDatabaseFacade` when the `database-module` JAR is on the classpath.
- **SCM Exception Handler**: Fires structured exceptions (IDs 9, 157–159, 208–210, 359–363) via the SCM handler when available.
- **Automatic Fallback**: If external JARs are missing, the system automatically degrades to a flat-file database (session-only, wiped on exit) and a console-based exception logger.

### Software Licensing
- A separate **License Server** process must be running before the main app starts. The server validates an activation key and listens on TCP port `15151`.
- The main app performs a TCP handshake at startup to verify the license.

---

## Architecture & Design Patterns

| Category | Pattern | Where Used |
|---|---|---|
| **Creational** | Factory Method | `PackingStrategyFactory` selects Standard/Fragile strategy; `DatabaseLayerFactory` and `ExceptionDispatcherFactory` choose real vs. fallback adapters |
| **Structural** | Adapter | `SCMDatabaseAdapter` / `FlatFileDatabaseAdapter` adapt different persistence backends to `IDatabaseLayer`; `SCMExceptionAdapter` / `FallbackConsoleLogger` adapt error dispatching to `IExceptionDispatcher` |
| **Behavioral** | Observer | `PackingModel` (subject) notifies `PackingMainFrame` (observer) of all state changes |
| **Behavioral** | Strategy | `IPackingStrategy` with `StandardPackingStrategy` and `FragilePackingStrategy` implementations |

### SOLID Principles
- **SRP**: Each class has a single focused responsibility (model, view, controller, adapter, strategy, worker).
- **OCP**: New strategies, adapters, or observers can be added without modifying existing code.
- **LSP**: All adapter implementations are interchangeable with their interfaces.
- **ISP**: Interfaces are small and focused (`IDatabaseLayer`, `IExceptionDispatcher`, `IPackingStrategy`, `PackingObserver`).
- **DIP**: All dependencies are injected via constructors — no Singletons.

### GRASP Principles
- **Information Expert**: `PackingModel` manages job state; `PackingJob` knows its own items and progress.
- **Controller**: `PackingController` is the first non-UI object handling user requests.
- **Low Coupling**: Adapters and factories isolate external subsystem dependencies.
- **High Cohesion**: Each class has a well-defined, focused role.
- **Creator**: Factories create the objects they have the information to configure.
- **Pure Fabrication**: DAO/adapter classes are invented to support persistence without polluting domain models.

### Multithreading
- **`SwingWorker`**: Each packing job runs on a pooled background thread with EDT-safe progress updates via `publish()`/`process()`.
- **`ConcurrentHashMap`**: Thread-safe maps for jobs and orders — workers write, EDT reads.
- **`CopyOnWriteArrayList`**: Safe observer iteration during notification.
- **`AtomicInteger`**: Lock-free unique ID generation for jobs and units.
- **`volatile` fields**: Job status and progress are visible across threads without synchronization.
- **EDT discipline**: All Swing mutations in observer callbacks are wrapped in `SwingUtilities.invokeLater()`.

---

## Requirements
- **Java Development Kit (JDK)** 11 or higher (tested with JDK 21)
- **Apache Maven** 3.9+

---

## Setup and Running

### 1. Open Packing Folder
```powershell
Set-Location "c:\Users\Advaith\Documents\PES\Sem 6\OOAD\Project\packing-repairs-receiptmanagement\packing"
```

### 2. Configure Database Credentials
Edit both files and set the correct local MySQL password:

- `src/main/resources/database.properties`
- `lib/database.properties`

Required keys:

```properties
db.url=jdbc:mysql://localhost:3306/OOAD
db.username=root
db.user=root
db.password=your_mysql_password
db.pool.size=5
```

### 3. Initialize SQL Schema (MySQL CLI)
Open MySQL CLI:

```powershell
mysql -u root -p
```

Then run:

```sql
SOURCE C:\\Users\\Advaith\\Documents\\PES\\Sem 6\\OOAD\\Project\\schema.sql;
SOURCE C:\\Users\\Advaith\\Documents\\PES\\Sem 6\\OOAD\\Project\\packing-repairs-receiptmanagement\\packing\\sql\\exception-handler-compat.sql;
SOURCE C:\\Users\\Advaith\\Documents\\PES\\Sem 6\\OOAD\\Project\\packing-repairs-receiptmanagement\\packing\\sql\\seed-orders-for-packing.sql;
```

Quick verification:

```sql
USE OOAD;
SELECT order_id, customer_id, order_status FROM orders WHERE order_id LIKE 'ORD-PACK-%';
SELECT order_item_id, order_id, product_id, ordered_quantity FROM order_items WHERE order_id LIKE 'ORD-PACK-%';
SHOW TABLES LIKE 'SCM_EXCEPTION_LOG';
```

### 4. Build and Test
```powershell
mvn clean test
```

### 5. Start License Server (Terminal 1)
```powershell
java -cp "target/classes" com.scm.packing.licensing.LicenseServer
```

When prompted, enter activation key:

`SCM-PACK-2026-XRAY`

Keep this terminal open.

If you get `Address already in use: bind`, another license server is already running on port `15151`.

### 6. Start Packing App (Terminal 2)
```powershell
mvn exec:java "-Dexec.mainClass=com.scm.packing.Main"
```

At startup you should see integration logs similar to:

- `[DatabaseLayerFactory] SCM database-module found — using SCMDatabaseAdapter.`
- `[ExceptionDispatcherFactory] SCM Exception Handler found — using SCMExceptionAdapter.`

### 7. Start Exception Viewer GUI (Terminal 3, optional but recommended)
```powershell
Set-Location "c:\Users\Advaith\Documents\PES\Sem 6\OOAD\Project\packing-repairs-receiptmanagement\packing\lib"
& "$env:JAVA_HOME/bin/java.exe" -cp ".;scm-exception-handler-v3.jar;scm-exception-viewer-gui.jar;database-module-1.0.0-SNAPSHOT-standalone.jar" com.scm.gui.ExceptionViewerGUI
```

### 8. Use the Application
1. Select orders from the left panel.
2. Click **Pack Selected**.
3. Track progress in Jobs table.
4. Use **View Barcode** for packed jobs.
5. Use pallet actions in the **Pallets** tab.

### 9. Verify Database Writes
In MySQL:

```sql
USE OOAD;
SELECT package_id, order_id, packaging_status, created_at
FROM packaging_jobs
ORDER BY created_at DESC
LIMIT 20;
```

## Demonstrating Exception Integration in Packing

Use one of the two flows below.

### Option 1: Fast Probe (Easiest)

This does not require running the packing UI and is the fastest way to prove
exception integration is wired correctly.

```powershell
Set-Location "c:\Users\Advaith\Documents\PES\Sem 6\OOAD\Project\packing-repairs-receiptmanagement\packing"
mvn -DskipTests compile exec:java "-Dexec.mainClass=com.scm.packing.integration.exceptions.ExceptionIntegrationProbe"
```

What this probe does:

1. Dispatches known exception ID `159`.
2. Dispatches known exception ID `359`.
3. Dispatches one unregistered exception path.

Verify results:

```sql
USE OOAD;
SELECT id, exception_id, exception_name, severity, subsystem, error_message, logged_at
FROM SCM_EXCEPTION_LOG
ORDER BY logged_at DESC
LIMIT 20;
```

If the Exception Viewer GUI is open, click **Refresh Now** and confirm rows appear.

### Option 2: In-App Trigger (UI Demo)

Use this during project presentation to show exception behavior from the actual packing app.

```powershell
Set-Location "c:\Users\Advaith\Documents\PES\Sem 6\OOAD\Project\packing-repairs-receiptmanagement\packing"
mvn exec:java "-Dexec.mainClass=com.scm.packing.Main"
```

Then trigger exceptions with one of these methods:

1. **Database interruption during packing**
    - Start packing a seeded order.
    - Temporarily stop MySQL while the job is processing.
    - This triggers DB-backed error paths and exception dispatch calls.

2. **Bad DB credentials before app start**
    - Set incorrect `db.password` in:
      - `src/main/resources/database.properties`
      - `lib/database.properties`
    - Start app and perform a packing action.
    - Restore valid credentials after the demo.

3. **Use unknown order handling path**
    - In UI flow, attempts involving unavailable items/orders route through registered exception dispatch points.

After triggering, verify in DB:

```sql
USE OOAD;
SELECT id, exception_id, exception_name, severity, subsystem, error_message, logged_at
FROM SCM_EXCEPTION_LOG
ORDER BY logged_at DESC
LIMIT 20;
```

---

Use this sequence during demo/presentation:

### A. Confirm Exception Subsystem Is Wired
Start app and ensure log contains:

- `[ExceptionDispatcherFactory] SCM Exception Handler found — using SCMExceptionAdapter.`

### B. Trigger a Packing Exception
Fastest practical demo path:

1. Start app and begin packing one of the seeded orders.
2. While packing is running, temporarily stop MySQL service (or make DB unreachable).
3. This will force persistence/dispatched error paths and route through the SCM exception subsystem.

### C. Confirm Visible Effects

1. **Popup appears** from exception handler.
2. **Viewer GUI** shows new row after refresh.
3. **DB row exists** in exception log table:

```sql
USE OOAD;
SELECT id, exception_id, exception_name, severity, subsystem, error_message, logged_at
FROM SCM_EXCEPTION_LOG
ORDER BY logged_at DESC
LIMIT 20;
```

### D. Restore Environment
Restart MySQL service and continue normal packing flow.

---

## Folder Structure
```
packing/
├── pom.xml
├── README.md
├── .gitignore
└── src/main/java/com/scm/packing/
    ├── Main.java                              # Entry point
    ├── licensing/
    │   ├── LicenseServer.java                 # Standalone license server
    │   └── LicenseChecker.java                # Client-side license check
    ├── mvc/
    │   ├── model/
    │   │   ├── PackingJobStatus.java          # Status enum
    │   │   ├── PackingItem.java               # Item domain model
    │   │   ├── PackingJob.java                # Job domain model
    │   │   ├── Order.java                     # Order domain model
    │   │   ├── BarcodeLabel.java              # Barcode for traceability
    │   │   ├── PackingUnit.java               # Unitization model
    │   │   └── PackingModel.java              # MVC Model (Observer subject)
    │   ├── view/
    │   │   └── PackingMainFrame.java          # Swing dashboard (Observer)
    │   └── controller/
    │       └── PackingController.java         # MVC Controller (GRASP)
    ├── worker/
    │   └── PackingWorker.java                 # SwingWorker for background packing
    ├── strategy/
    │   ├── IPackingStrategy.java              # Strategy interface
    │   ├── StandardPackingStrategy.java       # Standard packing algorithm
    │   ├── FragilePackingStrategy.java        # Fragile packing algorithm
    │   └── PackingStrategyFactory.java        # Factory Method
    ├── observer/
    │   ├── PackingObserver.java               # Observer interface
    │   └── PackingEventType.java              # Event type enum
    └── integration/
        ├── database/
        │   ├── IDatabaseLayer.java            # Adapter target interface
        │   ├── FlatFileDatabaseAdapter.java   # Fallback (flat file + seed data)
        │   ├── SCMDatabaseAdapter.java        # Real SCM DB adapter (stub)
        │   └── DatabaseLayerFactory.java      # Factory with auto-detection
        └── exceptions/
            ├── IExceptionDispatcher.java      # Adapter target interface
            ├── FallbackConsoleLogger.java      # Fallback (console)
            ├── SCMExceptionAdapter.java        # Real SCM handler (stub)
            └── ExceptionDispatcherFactory.java # Factory with auto-detection
```

---

## SCM Exception IDs Used

| ID | Name | Severity | When Fired |
|----|------|----------|------------|
| 159 | ITEM_NOT_AVAILABLE_FOR_PACKING | MAJOR | Item fails during packing |
| 209 | REPAIR_EXECUTION_FAILED | MAJOR | Unexpected packing job failure |
| 359 | PACKAGE_CREATION_FAILED | MINOR | Cannot persist a new packing job |
| 0 | UNREGISTERED_EXCEPTION | MINOR | Any unmatched exception condition |

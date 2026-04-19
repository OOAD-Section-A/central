# Database Module JAR Integration

## Purpose

The database module is packaged as a JAR so other Java subsystems can reuse the
same database access code instead of writing SQL connection logic again.

The main entry point is:

```java
com.jackfruit.scm.database.facade.SupplyChainDatabaseFacade
```

Subsystems can either call the facade directly or use the subsystem-specific
adapter classes in:

```java
com.jackfruit.scm.database.adapter
```

## JAR Location

After building the project, the integration JAR is created at:

```text
dist/database-module-1.0.0-SNAPSHOT-standalone.jar
```

This standalone JAR includes the database module and its required runtime
dependencies, so it is the correct file to share with non-Maven subsystems.
It also packages the canonical `schema.sql` and supporting SQL resources.

Build it with:

```bash
mvn clean package
```

To install it into the local Maven repository:

```bash
mvn install
```

## Schema Setup

The standalone JAR now contains the canonical runtime schema:

```text
schema.sql
```

This is the single source of truth for runtime tables and views. On a fresh
setup, teams should:

1. Create the `OOAD` database in MySQL.
2. Run the module normally.

On first run, the module bootstraps the schema from the embedded `schema.sql`.
Teams do not need a separate external copy of `schema.sql` in the working
directory anymore.

## Migration File

For teams that already created a local `OOAD` database from an older version of
the project, use the migration file instead of dropping and recreating the
database:

```text
src/main/resources/sql/migration-to-canonical-schema.sql
```

Why use it:

1. It upgrades older local schemas in place.
2. It preserves existing local data where possible.
3. It adds newer tables and columns expected by the current JAR.
4. It aligns older databases with the canonical `schema.sql`.

Run it with:

```bash
mysql -u <user> -p OOAD < src/main/resources/sql/migration-to-canonical-schema.sql
```

Use the migration file when:

1. A team already has an `OOAD` database from a previous iteration.
2. They are missing newer tables such as `forecast_timeseries` or
   `SCM_EXCEPTION_LOG`.
3. They want to avoid deleting local test data.

Use a fresh bootstrap instead when:

1. The team is setting up from scratch.
2. They are fine recreating the database cleanly.

## Maven Integration

If another subsystem is a Maven project, add this dependency to its `pom.xml`:

```xml
<dependency>
    <groupId>com.jackfruit.scm</groupId>
    <artifactId>database-module</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

This is the recommended method because Maven also brings required dependencies
like MySQL Connector/J.

**Important**: The database module also integrates with the exception handler subsystem.
If you want automatic exception handling and notifications, you must also add:

```xml
<dependency>
    <groupId>com.scm</groupId>
    <artifactId>scm-exception-handler</artifactId>
    <version>3.0</version>
</dependency>
```

See [EXCEPTION_HANDLING_IMPLEMENTATION.md](EXCEPTION_HANDLING_IMPLEMENTATION.md) for details.

## Manual JAR Integration

If the subsystem is not using Maven:

1. Add `dist/database-module-1.0.0-SNAPSHOT-standalone.jar` to the subsystem classpath.
2. Add `dist/scm-exception-handler-v3.jar` to the subsystem classpath (for automatic exception handling).
3. Set database connection values with environment variables such as `DB_URL`,
   `DB_USERNAME`, and `DB_PASSWORD`, or provide matching JVM system properties.
4. Import and call the required adapter class.

Example compile/run commands:

```bash
javac -cp "lib/database-module-1.0.0-SNAPSHOT-standalone.jar;lib/scm-exception-handler-v3.jar" MySubsystem.java
java -cp "lib/database-module-1.0.0-SNAPSHOT-standalone.jar;lib/scm-exception-handler-v3.jar;." MySubsystem
```

On Windows, use `;` in the classpath. On Linux/macOS, use `:`.

## Using Adapters

Yes, other subsystems can integrate through adapters. The JAR makes the adapter
classes available, and each adapter gives a subsystem-friendly API.

Example:

```java
import com.jackfruit.scm.database.adapter.InventoryAdapter;
import com.jackfruit.scm.database.facade.SupplyChainDatabaseFacade;

public class InventorySubsystem {
    public static void main(String[] args) {
        try (SupplyChainDatabaseFacade facade = new SupplyChainDatabaseFacade()) {
            InventoryAdapter inventoryAdapter = new InventoryAdapter(facade);

            inventoryAdapter.listProducts()
                    .forEach(product -> System.out.println(product.productName()));
        }
    }
}
```

Other available adapters include:

- `PricingAdapter`
- `OrderAdapter`
- `InventoryAdapter`
- `WarehouseManagementAdapter`
- `LogisticsAdapter`
- `ReportingAdapter`
- `BarcodeTrackingAdapter`
- `ExceptionHandlingAdapter`

## Direct Facade Usage

Adapters are useful, but not mandatory. A subsystem can also use the facade
directly:

```java
try (SupplyChainDatabaseFacade db = new SupplyChainDatabaseFacade()) {
    db.inventory().listProducts();
    db.pricing().listPrices();
    db.orders().listOrders();
}
```

## Important Note

The JAR does not automatically connect subsystems by itself. It provides shared
Java classes. The actual integration happens when each subsystem imports the JAR
and calls the adapter or facade methods.

## Exception Handling

The database module integrates with the exception handler subsystem to provide automatic
error handling, logging, and notifications. When database operations encounter errors,
the database module automatically delegates to the exception handler.

**To enable exception handling**:
- Include both `database-module-1.0.0-SNAPSHOT-standalone.jar` AND `scm-exception-handler-v3.jar` in your classpath
- No code changes needed - exceptions are handled automatically

For detailed information on exception handling architecture and all 32 supported exception
types, see [EXCEPTION_HANDLING_IMPLEMENTATION.md](EXCEPTION_HANDLING_IMPLEMENTATION.md).

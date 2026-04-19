# Fixing the Transitive System-Scope Dependency Problem

This document details a non-obvious Maven dependency bug we hit during the `scm-dashboard` integration, and exactly how it was diagnosed and fixed. Any new subsystem that is integrated into the parent project should follow this guide to avoid the same silent failure.

---

## Background

The `database-module` provides the central `SupplyChainDatabaseFacade` that all subsystems use to persist records to the shared MySQL database. Internally, `SupplyChainDatabaseFacade` depends on the `scm-exception-handler-v3.jar` (provided by the Exception Handling subsystem team) to dispatch database connection errors.

Before this fix, the `database-module/pom.xml` declared this JAR using Maven's **`system` scope**:

```xml
<!-- database-module/pom.xml — BEFORE (broken) -->
<dependency>
    <groupId>com.scm</groupId>
    <artifactId>scm-exception-handler</artifactId>
    <version>3.0</version>
    <scope>system</scope>
    <systemPath>${project.basedir}/dist/scm-exception-handler-v3.jar</systemPath>
</dependency>
```

Similarly, the `packing` subsystem also had both the database module AND the exception handler declared as system-scoped JARs pointing at files in its own `lib/` folder:

```xml
<!-- packing/pom.xml — BEFORE (broken) -->
<dependency>
    <groupId>com.jackfruit.scm</groupId>
    <artifactId>database-module-standalone</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <scope>system</scope>
    <systemPath>${project.basedir}/lib/database-module-1.0.0-SNAPSHOT-standalone.jar</systemPath>
</dependency>
<dependency>
    <groupId>com.scm</groupId>
    <artifactId>scm-exception-handler</artifactId>
    <version>3.0</version>
    <scope>system</scope>
    <systemPath>${project.basedir}/lib/scm-exception-handler-v3.jar</systemPath>
</dependency>
```

This worked fine when each subsystem was run in isolation from its own directory. However, it caused a **silent, hard-to-diagnose fallback** when run from the unified dashboard.

---

## The Problem: `system` Scope Is NOT Transitive

This is the core of the bug. Maven's `system` scope is **explicitly non-transitive** by design. This means:

- When `scm-dashboard` depends on `packing-subsystem`, Maven resolves all of packing's `compile`-scoped deps transitively.
- However, packing's `system`-scoped deps are **private to packing's own build context**. Maven will NOT add them to any downstream classpath.
- The same applies to `database-module`: the `scm-exception-handler` JAR it declared as `system`-scoped was never placed on the `scm-dashboard` runtime classpath.

### What Actually Happened at Runtime

The symptom was that the Packing subsystem silently fell back to its offline `FlatFileDatabaseAdapter` instead of the real database. The `DatabaseLayerFactory` used `Class.forName` to check if the facade was present:

```java
// DatabaseLayerFactory.java
try {
    Class.forName("com.jackfruit.scm.database.facade.SupplyChainDatabaseFacade");
    // Class found! Trying to instantiate...
    return new SCMDatabaseAdapter(); // <-- CRASHES HERE
} catch (ClassNotFoundException | LinkageError | RuntimeException e) {
    // Caught the crash silently, fell back to flat file
    return new FlatFileDatabaseAdapter();
}
```

The `Class.forName` **succeeded** (the `database-module` JAR itself was on the classpath). But the moment `new SCMDatabaseAdapter()` tried to instantiate `SupplyChainDatabaseFacade`, the JVM attempted to load `DatabaseConnectionManager`, which had a static field of type `DatabaseDesignSubsystem` from `scm-exception-handler-v3.jar`. That class was **not on the classpath**, causing:

```
java.lang.NoClassDefFoundError: com/scm/subsystems/DatabaseDesignSubsystem
    at com.jackfruit.scm.database.config.DatabaseConnectionManager.<init>(...)
    ...
```

This is a `RuntimeException` subclass, so the `catch` in `DatabaseLayerFactory` swallowed it and returned the flat-file adapter with no visible error.

---

## The Fix

### Step 1: Install the JAR into the Local Maven Repository

The correct way to use a third-party JAR that doesn't have a Maven POM is to install it into the local `.m2` repository once. Run this from the **root project directory**:

```powershell
mvn install:install-file `
  "-Dfile=database-module/dist/scm-exception-handler-v3.jar" `
  "-DgroupId=com.scm" `
  "-DartifactId=scm-exception-handler" `
  "-Dversion=3.0" `
  "-Dpackaging=jar"
```

This registers the JAR as a first-class Maven artifact at `com.scm:scm-exception-handler:3.0` so any module in the reactor can depend on it without using `system` scope.

> **Note:** This step only needs to be done once per machine. Any other developer cloning the repo who wants to build must run this command themselves, since the `.m2` repository is not committed to Git.
> Consider documenting this in the root `README.md` as a one-time setup step.

### Step 2: Replace `system` Scope with `compile` Scope

After the JAR is in `.m2`, update all POMs that previously used `system` scope for either the exception handler or the standalone database module JAR.

#### `database-module/pom.xml`

```xml
<!-- AFTER (fixed) -->
<dependency>
    <groupId>com.scm</groupId>
    <artifactId>scm-exception-handler</artifactId>
    <version>3.0</version>
    <!-- No scope = default compile scope. Now transitively available. -->
</dependency>
```

#### `packing/pom.xml`

Replace both the standalone database JAR and the exception handler:

```xml
<!-- AFTER (fixed) -->

<!-- Use the proper multi-module database-module, not the standalone JAR -->
<dependency>
    <groupId>com.jackfruit.scm</groupId>
    <artifactId>database-module</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>

<!-- Exception handler is now in .m2 -->
<dependency>
    <groupId>com.scm</groupId>
    <artifactId>scm-exception-handler</artifactId>
    <version>3.0</version>
</dependency>
```

#### `scm-dashboard/pom.xml`

No explicit exception handler dependency is needed here. Once `database-module` declares it as `compile`-scoped, Maven propagates it transitively to the dashboard automatically.

---

## Rule for Future Subsystem Integrations

When integrating a new subsystem into `final-scm-application`, follow this checklist:

1. **Never use `system` scope in a multi-module build.** If a team gives you a JAR file, install it into `.m2` via `mvn install:install-file` and declare it as a normal `compile` dependency.

2. **Replace `standalone` JAR dependencies with the proper Maven module reference.** If a subsystem previously depended on `database-module-1.0.0-SNAPSHOT-standalone.jar` (the fat-JAR variant), switch it to depend on `com.jackfruit.scm:database-module:1.0.0-SNAPSHOT` instead. The multi-module build handles packaging.

3. **Make the `DatabaseLayerFactory` (or equivalent factory) print the full stack trace on fallback** during development so silent failures are visible immediately:
   ```java
   } catch (ClassNotFoundException | LinkageError | RuntimeException e) {
       System.out.println("[Factory] Falling back: " + e.getClass().getSimpleName());
       e.printStackTrace(); // Remove before final submission
       return new FallbackAdapter();
   }
   ```

4. **Verify with a probe before running the full UI.** A quick test from the root directory:
   ```powershell
   java -cp ".;packing/target/classes;database-module/target/classes;scm-dashboard/src/main/resources" TestProbe
   ```
   Where `TestProbe.java` calls `DatabaseLayerFactory.create()` and prints the result. If you see `SCM database-module found`, the wiring is correct.

---

## Summary of Files Changed

| File | Change |
|---|---|
| `database-module/pom.xml` | Removed `system`/`systemPath` from `scm-exception-handler` dep; now plain `compile` scope |
| `packing/pom.xml` | Replaced `system`-scoped `database-module-standalone` + `scm-exception-handler` with proper `compile`-scoped deps pointing at the multi-module artifacts |
| `scm-dashboard/pom.xml` | Removed redundant `system`-scoped exception handler dep (now flows transitively) |
| Local `.m2` repository | `scm-exception-handler-v3.jar` installed as `com.scm:scm-exception-handler:3.0` |

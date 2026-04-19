package com.scm.packing.integration.database;

/**
 * Factory that decides which {@link IDatabaseLayer} implementation to use.
 *
 * <p><b>Design Pattern – Factory Method (Creational):</b> The selection
 * between the real SCM adapter and the flat-file fallback is centralised
 * here.  The rest of the application simply asks for an
 * {@code IDatabaseLayer} and is unaware of the concrete class.</p>
 *
 * <p><b>Fallback strategy:</b> We first attempt to load the SCM database
 * adapter's dependency class via reflection.  If it is not on the
 * classpath (i.e. the database-module JAR is missing), we fall back to
 * the flat-file adapter automatically.</p>
 *
 * <p><b>GRASP – Creator:</b> This factory possesses the knowledge needed
 * to choose the correct implementation (classpath availability).</p>
 */
public class DatabaseLayerFactory {

    private static final String MODE_PROPERTY = "packing.integration.database.mode";

    /**
     * Creates the most capable available {@link IDatabaseLayer}.
     *
     * @return an {@link SCMDatabaseAdapter} if the SCM database-module
     *         is available, or a {@link FlatFileDatabaseAdapter} otherwise
     */
    public static IDatabaseLayer create() {
        String forcedMode = System.getProperty(MODE_PROPERTY, "auto").trim().toLowerCase();
        if ("flat".equals(forcedMode)) {
            System.out.println("[DatabaseLayerFactory] Forced mode=flat — using FlatFileDatabaseAdapter.");
            return new FlatFileDatabaseAdapter();
        }
        if ("scm".equals(forcedMode)) {
            try {
                System.out.println("[DatabaseLayerFactory] Forced mode=scm — using SCMDatabaseAdapter.");
                return new SCMDatabaseAdapter();
            } catch (LinkageError | RuntimeException e) {
                System.out.println("[DatabaseLayerFactory] Forced SCM mode failed; falling back to FlatFileDatabaseAdapter.");
                return new FlatFileDatabaseAdapter();
            }
        }

        // -----------------------------------------------------------
        // FALLBACK LOGIC: Try to load the SCM facade class.  If it is
        // not on the classpath, Class.forName will throw, and we
        // gracefully fall back to the flat-file adapter.
        // -----------------------------------------------------------
        try {
            Class.forName("com.jackfruit.scm.database.facade.SupplyChainDatabaseFacade");
            System.out.println("[DatabaseLayerFactory] SCM database-module found — using SCMDatabaseAdapter.");
            return new SCMDatabaseAdapter();
        } catch (ClassNotFoundException | LinkageError | RuntimeException e) {
            System.out.println("[DatabaseLayerFactory] SCM database-module NOT found on classpath or initialization failed.");
            e.printStackTrace();
            System.out.println("[DatabaseLayerFactory] Falling back to FlatFileDatabaseAdapter (session-only persistence).");
            return new FlatFileDatabaseAdapter();
        }
    }
}

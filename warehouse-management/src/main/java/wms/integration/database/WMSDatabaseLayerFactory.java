package wms.integration.database;

import wms.contracts.IWMSRepository;

/**
 * Factory that decides which {@link IWMSDatabaseLayer} implementation to use.
 *
 * <p><b>Design Pattern – Factory Method (Creational):</b> The selection
 * between the real SCM database adapter and the fallback legacy repository
 * is centralised here. The rest of the WMS simply asks for an
 * {@code IWMSDatabaseLayer} and is unaware of the concrete class.</p>
 *
 * <p><b>Fallback strategy:</b> We first attempt to load the SCM database
 * facade class via reflection. If it is not on the classpath, we fall back
 * to the {@link FallbackRepositoryAdapter} automatically.</p>
 */
public class WMSDatabaseLayerFactory {

    private static final String SCM_FACADE_CLASS =
            "com.jackfruit.scm.database.facade.SupplyChainDatabaseFacade";

    /**
     * Creates the most capable available {@link IWMSDatabaseLayer}.
     *
     * @param legacyRepository the existing IWMSRepository mock to wrap as fallback
     * @return an {@link SCMDatabaseAdapter} if the SCM database-module is
     *         available, or a {@link FallbackRepositoryAdapter} wrapping the
     *         legacy repository otherwise
     */
    public static IWMSDatabaseLayer create(IWMSRepository legacyRepository) {
        try {
            Class.forName(SCM_FACADE_CLASS);
            System.out.println("[WMSDatabaseLayerFactory] SCM database-module found — using SCMDatabaseAdapter.");
            return new SCMDatabaseAdapter();
        } catch (ClassNotFoundException | LinkageError | RuntimeException e) {
            System.out.println("[WMSDatabaseLayerFactory] SCM database-module NOT found on classpath.");
            System.out.println("[WMSDatabaseLayerFactory] Falling back to FallbackRepositoryAdapter (in-memory).");
            return new FallbackRepositoryAdapter(legacyRepository);
        }
    }
}

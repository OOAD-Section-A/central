package wms.integration.exceptions;

/**
 * Factory that decides which {@link IWMSExceptionDispatcher} to use.
 *
 * <p><b>Design Pattern – Factory Method (Creational):</b> The selection
 * between the real SCM exception adapter and the console fallback is
 * centralised here.</p>
 */
public class WMSExceptionDispatcherFactory {

    private static final String SCM_SUBSYSTEM_CLASS =
            "com.scm.subsystems.WarehouseMgmtSubsystem";

    /**
     * Creates the most capable available {@link IWMSExceptionDispatcher}.
     *
     * @return an {@link SCMExceptionAdapter} if the SCM exception handler
     *         JARs are available, or a {@link FallbackConsoleLogger} otherwise
     */
    public static IWMSExceptionDispatcher create() {
        try {
            Class.forName(SCM_SUBSYSTEM_CLASS);
            System.out.println("[WMSExceptionDispatcherFactory] SCM Exception Handler found — using SCMExceptionAdapter.");
            return new SCMExceptionAdapter();
        } catch (ClassNotFoundException | LinkageError | RuntimeException e) {
            System.out.println("[WMSExceptionDispatcherFactory] SCM Exception Handler NOT found on classpath.");
            System.out.println("[WMSExceptionDispatcherFactory] Falling back to FallbackConsoleLogger.");
            return new FallbackConsoleLogger();
        }
    }
}

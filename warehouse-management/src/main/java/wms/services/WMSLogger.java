package wms.services;

import wms.integration.exceptions.IWMSExceptionDispatcher;
import wms.integration.exceptions.WMSExceptionDispatcherFactory;

/**
 * Bridge to the SCM Exception Handler subsystem (formerly Subsystem 17).
 *
 * <p>In the integrated application, this class routes all WMS error events
 * through the {@link IWMSExceptionDispatcher}. If the SCM Exception Handler
 * JAR is not present on the classpath, the factory automatically selects the
 * console fallback, so no exception is ever silently dropped.</p>
 *
 * <p><b>SOLID – Single Responsibility:</b> This class is the single
 * point that WMS services use to surface errors — no business logic here.</p>
 */
public class WMSLogger {

    // Lazily initialised dispatcher — resolved once at first use.
    private static volatile IWMSExceptionDispatcher dispatcher;

    private static IWMSExceptionDispatcher getDispatcher() {
        if (dispatcher == null) {
            synchronized (WMSLogger.class) {
                if (dispatcher == null) {
                    dispatcher = WMSExceptionDispatcherFactory.create();
                }
            }
        }
        return dispatcher;
    }

    /**
     * Logs a named, contextual error with exception ID lookup.
     * Uses exception ID 0 (UNREGISTERED) as the default for unknown errors.
     *
     * @param context      human-readable location (e.g. "WarehouseFacade.dispatchOrder")
     * @param errorMessage the error detail message
     */
    public static void logError(String context, String errorMessage) {
        String detail = context + ": " + errorMessage;
        getDispatcher().dispatchUnregistered(detail);
    }

    /**
     * Logs a registered WMS exception by its SCM master-register ID.
     *
     * @param exceptionId the official SCM exception ID (e.g. 152 for INSUFFICIENT_STOCK_FOR_PICK)
     * @param severity    "MINOR", "MAJOR", or "WARNING"
     * @param detail      traceable context string
     */
    public static void logRegistered(int exceptionId, String severity, String detail) {
        getDispatcher().dispatch(exceptionId, severity, "Warehouse Mgmt", detail);
    }
}

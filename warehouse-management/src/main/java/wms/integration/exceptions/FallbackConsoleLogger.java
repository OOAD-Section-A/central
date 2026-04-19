package wms.integration.exceptions;

import java.time.Instant;

/**
 * Console-based fallback exception dispatcher for when the SCM
 * Exception Handler JAR is not on the classpath.
 *
 * <p><b>SOLID – Liskov Substitution:</b> Any code expecting an
 * {@link IWMSExceptionDispatcher} works identically regardless of whether
 * it receives this fallback or the real SCM adapter.</p>
 */
public class FallbackConsoleLogger implements IWMSExceptionDispatcher {

    @Override
    public void dispatch(int exceptionId, String severity, String subsystem, String detail) {
        System.err.printf(
                "[WMS FALLBACK EXCEPTION] %s | ID: %d | Severity: %s | Subsystem: %s | Detail: %s%n",
                Instant.now(), exceptionId, severity, subsystem, detail);
    }

    @Override
    public void dispatchUnregistered(String detail) {
        dispatch(0, "MINOR", "Warehouse Mgmt", "UNREGISTERED_EXCEPTION — " + detail);
    }
}

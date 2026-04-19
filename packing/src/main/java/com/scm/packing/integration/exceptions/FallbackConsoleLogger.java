package com.scm.packing.integration.exceptions;

import java.time.Instant;

/**
 * Console-based fallback exception dispatcher.
 *
 * <p><b>Design Pattern – Adapter (Structural):</b> When the SCM Exception
 * Handler subsystem JARs are not on the classpath, this class provides
 * a drop-in replacement that prints structured error messages to
 * {@code System.err} so that exception information is never silently
 * lost.</p>
 *
 * <p><b>SOLID – Liskov Substitution:</b> Any code expecting an
 * {@link IExceptionDispatcher} works identically regardless of whether
 * it receives this fallback or the real SCM adapter.</p>
 */
public class FallbackConsoleLogger implements IExceptionDispatcher {

    /**
     * Prints a formatted exception record to {@code System.err}.
     */
    @Override
    public void dispatch(int exceptionId, String severity, String subsystem, String detail) {
        // -----------------------------------------------------------
        // FALLBACK: No SCM Exception Handler available.
        // We mirror the popup format specified in the SCM Interface
        // Specification so the log output is still useful for debugging.
        // -----------------------------------------------------------
        System.err.printf(
                "[FALLBACK EXCEPTION] %s | ID: %d | Severity: %s | Subsystem: %s | Detail: %s%n",
                Instant.now(), exceptionId, severity, subsystem, detail);
    }

    /**
     * Handles exceptions that are not in the master register (ID 0).
     */
    @Override
    public void dispatchUnregistered(String detail) {
        dispatch(0, "MINOR", "Packing", "UNREGISTERED_EXCEPTION — " + detail);
    }
}

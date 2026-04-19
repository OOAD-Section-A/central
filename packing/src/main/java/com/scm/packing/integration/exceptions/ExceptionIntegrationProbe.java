package com.scm.packing.integration.exceptions;

/**
 * Lightweight CLI probe to test exception integration quickly.
 *
 * <p>Usage:</p>
 * <pre>
 *   mvn exec:java "-Dexec.mainClass=com.scm.packing.integration.exceptions.ExceptionIntegrationProbe"
 * </pre>
 */
public final class ExceptionIntegrationProbe {

    private ExceptionIntegrationProbe() {
    }

    public static void main(String[] args) {
        IExceptionDispatcher dispatcher = ExceptionDispatcherFactory.create();

        System.out.println("[ExceptionIntegrationProbe] Dispatching sample exceptions...");

        // Known packing exception ID mapped by SCMExceptionAdapter.
        dispatcher.dispatch(159, "MAJOR", "Packing", "Probe: item not available for packing");

        // Another known ID used in packing flows.
        dispatcher.dispatch(359, "MINOR", "Packing", "Probe: package creation failed");

        // Unregistered catch-all path.
        dispatcher.dispatchUnregistered("Probe: unregistered exception path");

        System.out.println("[ExceptionIntegrationProbe] Done.");
    }
}

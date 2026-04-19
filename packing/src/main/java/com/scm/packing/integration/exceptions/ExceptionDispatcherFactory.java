package com.scm.packing.integration.exceptions;

/**
 * Factory that decides which {@link IExceptionDispatcher} to use.
 *
 * <p><b>Design Pattern – Factory Method (Creational):</b> The selection
 * between the real SCM exception adapter and the console fallback is
 * centralised here.</p>
 *
 * <p><b>Fallback strategy:</b> We try to load the SCM packing/repairs
 * subsystem class via reflection. If it is not on the classpath, we
 * return the console logger instead.</p>
 */
public class ExceptionDispatcherFactory {

    private static final String MODE_PROPERTY = "packing.integration.exceptions.mode";

    /**
     * Creates the most capable available {@link IExceptionDispatcher}.
     *
     * @return an {@link SCMExceptionAdapter} if the SCM exception handler
     *         JARs are available, or a {@link FallbackConsoleLogger} otherwise
     */
    public static IExceptionDispatcher create() {
        String forcedMode = System.getProperty(MODE_PROPERTY, "auto").trim().toLowerCase();
        if ("flat".equals(forcedMode)) {
            System.out.println("[ExceptionDispatcherFactory] Forced mode=flat — using FallbackConsoleLogger.");
            return new FallbackConsoleLogger();
        }
        if ("scm".equals(forcedMode)) {
            try {
                System.out.println("[ExceptionDispatcherFactory] Forced mode=scm — using SCMExceptionAdapter.");
                return new SCMExceptionAdapter();
            } catch (LinkageError | RuntimeException e) {
                System.out.println("[ExceptionDispatcherFactory] Forced SCM mode failed; falling back to FallbackConsoleLogger.");
                return new FallbackConsoleLogger();
            }
        }

        try {
            Class.forName("com.scm.subsystems.PackingRepairsReceiptSubsystem");
            System.out.println("[ExceptionDispatcherFactory] SCM Exception Handler found — using SCMExceptionAdapter.");
            return new SCMExceptionAdapter();
        } catch (ClassNotFoundException | LinkageError | RuntimeException e) {
            System.out.println("[ExceptionDispatcherFactory] SCM Exception Handler NOT found on classpath.");
            System.out.println("[ExceptionDispatcherFactory] Falling back to FallbackConsoleLogger.");
            return new FallbackConsoleLogger();
        }
    }
}

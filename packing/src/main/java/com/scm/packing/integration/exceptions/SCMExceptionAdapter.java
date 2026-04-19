package com.scm.packing.integration.exceptions;

import com.scm.core.Severity;
import com.scm.factory.SCMExceptionFactory;
import com.scm.handler.SCMExceptionHandler;
import com.scm.subsystems.PackingRepairsReceiptSubsystem;

/**
 * Adapter that connects to the real SCM Exception Handler subsystem.
 *
 * <p><b>Design Pattern – Adapter (Structural):</b> Bridges the
 * {@link IExceptionDispatcher} interface expected by the Packing
 * subsystem with the concrete singleton API exposed by the Exception
 * team JAR ({@code com.scm.subsystems.PackingRepairsReceiptSubsystem}).</p>
 *
 * <p><b>Integration note:</b> This class depends on the SCM exception
 * handler JARs being on the classpath (packages under
 * {@code com.scm.*}). If they are absent, the
 * {@link ExceptionDispatcherFactory} falls back to
 * {@link FallbackConsoleLogger}.</p>
 *
 * <p><b>SOLID – Single Responsibility:</b> Exception forwarding only —
 * no business logic.</p>
 */
public class SCMExceptionAdapter implements IExceptionDispatcher {

    private static final String DEFAULT_SUBSYSTEM = "Packing, Repairs, Receipt Management";
    private final PackingRepairsReceiptSubsystem subsystem;

    public SCMExceptionAdapter() {
        this.subsystem = PackingRepairsReceiptSubsystem.INSTANCE;
        System.out.println("[SCMExceptionAdapter] Initialised with PackingRepairsReceiptSubsystem.");
    }

    @Override
    public void dispatch(int exceptionId, String severity, String subsystem, String detail) {
        String resolvedSubsystem = normalizeSubsystem(subsystem);
        String resolvedDetail = detail == null ? "(no detail provided)" : detail;

        try {
            switch (exceptionId) {
                case 9:
                    this.subsystem.onInvalidRepairRequest(resolvedSubsystem, resolvedDetail);
                    return;
                case 157:
                    this.subsystem.onSparePartNotAvailable(resolvedDetail);
                    return;
                case 158:
                    this.subsystem.onInventoryReservationFailed(resolvedSubsystem, resolvedDetail);
                    return;
                case 159:
                    this.subsystem.onItemNotAvailableForPacking(resolvedSubsystem, resolvedDetail);
                    return;
                case 208:
                    this.subsystem.onWarrantyValidationFailed(resolvedSubsystem, resolvedDetail);
                    return;
                case 209:
                    this.subsystem.onRepairExecutionFailed(resolvedSubsystem, resolvedDetail);
                    return;
                case 210:
                    this.subsystem.onRepairDelayDetected(resolvedSubsystem, 0L, 0L);
                    return;
                case 359:
                    this.subsystem.onPackageCreationFailed(resolvedDetail);
                    return;
                case 360:
                    this.subsystem.onReceiptStorageFailed(resolvedDetail);
                    return;
                case 361:
                    this.subsystem.onReceiptGenerationFailed(resolvedDetail);
                    return;
                case 362:
                    this.subsystem.onPaymentProcessingFailed(resolvedSubsystem, resolvedDetail);
                    return;
                case 363:
                    this.subsystem.onCostCalculationFailed(resolvedSubsystem, resolvedDetail);
                    return;
                default:
                    SCMExceptionHandler.INSTANCE.handle(
                            SCMExceptionFactory.create(
                                    exceptionId,
                                    "PACKING_EXCEPTION_" + exceptionId,
                                    resolvedDetail,
                                    resolvedSubsystem,
                                    mapSeverity(severity)));
            }
        } catch (Exception e) {
            System.err.println("[SCMExceptionAdapter] Failed to dispatch to SCM handler: " + e.getMessage());
            SCMExceptionHandler.INSTANCE.handle(
                    SCMExceptionFactory.createUnregistered(
                            resolvedSubsystem,
                            "Dispatch fallback triggered: " + resolvedDetail));
        }
    }

    @Override
    public void dispatchUnregistered(String detail) {
        String resolvedDetail = detail == null ? "(no detail provided)" : detail;
        SCMExceptionHandler.INSTANCE.handle(
                SCMExceptionFactory.createUnregistered(
                        DEFAULT_SUBSYSTEM,
                        "UNREGISTERED_EXCEPTION - " + resolvedDetail));
    }

    private static String normalizeSubsystem(String subsystem) {
        return (subsystem == null || subsystem.isBlank()) ? DEFAULT_SUBSYSTEM : subsystem;
    }

    private static Severity mapSeverity(String severity) {
        if (severity == null) {
            return Severity.MINOR;
        }
        try {
            return Severity.valueOf(severity.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return Severity.MINOR;
        }
    }
}

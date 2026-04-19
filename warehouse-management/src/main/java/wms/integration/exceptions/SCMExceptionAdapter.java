package wms.integration.exceptions;

import com.scm.core.Severity;
import com.scm.factory.SCMExceptionFactory;
import com.scm.handler.SCMExceptionHandler;
import com.scm.subsystems.WarehouseMgmtSubsystem;

/**
 * Adapter that connects to the real SCM Exception Handler subsystem.
 *
 * <p><b>Design Pattern – Adapter (Structural):</b> Bridges the
 * {@link IWMSExceptionDispatcher} interface expected by the WMS
 * with the concrete singleton API exposed by the exception team JAR
 * ({@code com.scm.subsystems.WarehouseMgmtSubsystem}).</p>
 *
 * <p><b>SOLID – Single Responsibility:</b> Exception forwarding only —
 * no business logic.</p>
 */
public class SCMExceptionAdapter implements IWMSExceptionDispatcher {

    private static final String DEFAULT_SUBSYSTEM = "Warehouse Mgmt";
    private final WarehouseMgmtSubsystem subsystem;

    public SCMExceptionAdapter() {
        this.subsystem = WarehouseMgmtSubsystem.INSTANCE;
        System.out.println("[WMS SCMExceptionAdapter] Initialised with WarehouseMgmtSubsystem.");
    }

    @Override
    public void dispatch(int exceptionId, String severity, String subsystem, String detail) {
        String resolved = normalizeSubsystem(subsystem);
        String resolvedDetail = detail == null ? "(no detail provided)" : detail;

        try {
            switch (exceptionId) {
                case 13:
                    this.subsystem.onInvalidPurchaseOrderReference(resolvedDetail);
                    return;
                case 14:
                    this.subsystem.onInvalidProductReference(resolvedDetail);
                    return;
                case 19:
                    this.subsystem.onReturnConditionInvalid(resolvedDetail, "(unknown)");
                    return;
                case 107:
                    this.subsystem.onConcurrentUpdateConflict(resolvedDetail);
                    return;
                case 152:
                    this.subsystem.onInsufficientStockForPick(resolvedDetail, 0, 0);
                    return;
                case 153:
                    this.subsystem.onStockUnderflow(resolvedDetail, 0, 0);
                    return;
                case 154:
                    this.subsystem.onBinCapacityExceeded(resolvedDetail, 0);
                    return;
                case 155:
                    this.subsystem.onBinNotFoundOrBlocked(resolvedDetail);
                    return;
                case 156:
                    this.subsystem.onDockDoubleBooking(resolvedDetail, "(unknown)", "(unknown)");
                    return;
                case 313:
                    this.subsystem.onCycleCountDiscrepancy(resolvedDetail, 0, 0);
                    return;
                case 314:
                    this.subsystem.onGrnQtyMismatch(resolvedDetail, 0, 0);
                    return;
                case 407:
                    this.subsystem.onDamagedGoodsDetected(resolvedDetail, "(unknown)");
                    return;
                default:
                    SCMExceptionHandler.INSTANCE.handle(
                            SCMExceptionFactory.create(
                                    exceptionId,
                                    "WMS_EXCEPTION_" + exceptionId,
                                    resolvedDetail,
                                    resolved,
                                    mapSeverity(severity)));
            }
        } catch (Exception e) {
            System.err.println("[WMS SCMExceptionAdapter] Failed to dispatch to SCM handler: " + e.getMessage());
            SCMExceptionHandler.INSTANCE.handle(
                    SCMExceptionFactory.createUnregistered(
                            resolved, "Dispatch fallback triggered: " + resolvedDetail));
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
        if (severity == null) return Severity.MINOR;
        try {
            return Severity.valueOf(severity.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return Severity.MINOR;
        }
    }
}

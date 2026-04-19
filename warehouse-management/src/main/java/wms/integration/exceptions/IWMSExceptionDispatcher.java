package wms.integration.exceptions;

/**
 * Abstraction for exception dispatching in the WMS subsystem.
 *
 * <p><b>Design Pattern – Adapter (Structural):</b> This interface is the
 * "target" that the WMS subsystem codes against. Concrete adapters bridge
 * either the real SCM Exception Handler subsystem or a simple
 * console-based fallback.</p>
 *
 * <p><b>SOLID – Dependency Inversion:</b> Services and controllers depend
 * on this interface, never on a concrete exception handler class.</p>
 *
 * <p>Relevant SCM Exception IDs for the WMS subsystem (from the master register):
 * <ul>
 *   <li>13  — INVALID_PURCHASE_ORDER_REFERENCE</li>
 *   <li>14  — INVALID_PRODUCT_REFERENCE</li>
 *   <li>19  — RETURN_CONDITION_INVALID</li>
 *   <li>107 — CONCURRENT_UPDATE_CONFLICT</li>
 *   <li>152 — INSUFFICIENT_STOCK_FOR_PICK</li>
 *   <li>153 — STOCK_UNDERFLOW</li>
 *   <li>154 — BIN_CAPACITY_EXCEEDED</li>
 *   <li>155 — BIN_NOT_FOUND_OR_BLOCKED</li>
 *   <li>156 — DOCK_DBL_BOOKING</li>
 *   <li>313 — CYCLE_COUNT_DISCREPANCY</li>
 *   <li>314 — GRN_QTY_MISMATCH</li>
 *   <li>407 — DAMAGED_GOODS_DETECTED</li>
 * </ul></p>
 */
public interface IWMSExceptionDispatcher {

    /**
     * Dispatches an exception event to the SCM Exception Handler.
     *
     * @param exceptionId  the numeric ID from the SCM master register
     * @param severity     "MINOR", "MAJOR", or "WARNING"
     * @param subsystem    subsystem name (always "Warehouse Mgmt" here)
     * @param detail       human-readable context about the failure
     */
    void dispatch(int exceptionId, String severity, String subsystem, String detail);

    /**
     * Dispatches an unregistered exception (ID 0) as per the SCM spec.
     *
     * @param detail context about the unregistered exception
     */
    void dispatchUnregistered(String detail);
}

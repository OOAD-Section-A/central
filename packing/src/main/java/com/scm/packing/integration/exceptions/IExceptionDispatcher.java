package com.scm.packing.integration.exceptions;

/**
 * Abstraction for exception dispatching in the Packing subsystem.
 *
 * <p><b>Design Pattern – Adapter (Structural):</b> This interface is the
 * "target" that the Packing subsystem codes against.  Concrete adapters
 * bridge either the real SCM Exception Handler subsystem or a simple
 * console-based fallback.</p>
 *
 * <p><b>SOLID – Dependency Inversion:</b> Controllers and workers depend
 * on this interface, never on a concrete exception handler class.</p>
 *
 * <p><b>SOLID – Interface Segregation:</b> Only packing-relevant exception
 * methods are declared — the full 10-category SCM exception surface is
 * not exposed to callers that do not need it.</p>
 *
 * <p>Relevant SCM Exception IDs for the Packing subsystem (from the
 * master register):</p>
 * <ul>
 *   <li>9   — INVALID_REPAIR_REQUEST</li>
 *   <li>157 — SPARE_PART_NOT_AVAILABLE</li>
 *   <li>158 — INVENTORY_RESERVATION_FAILED</li>
 *   <li>159 — ITEM_NOT_AVAILABLE_FOR_PACKING</li>
 *   <li>208 — WARRANTY_VALIDATION_FAILED</li>
 *   <li>209 — REPAIR_EXECUTION_FAILED</li>
 *   <li>210 — REPAIR_DELAY_DETECTED</li>
 *   <li>359 — PACKAGE_CREATION_FAILED</li>
 *   <li>360 — RECEIPT_STORAGE_FAILED</li>
 *   <li>361 — RECEIPT_GENERATION_FAILED</li>
 *   <li>362 — PAYMENT_PROCESSING_FAILED</li>
 *   <li>363 — COST_CALCULATION_FAILED</li>
 * </ul>
 */
public interface IExceptionDispatcher {

    /**
     * Dispatches an exception event.  Implementations either call the
     * SCM handler's {@code fire*} methods or log to the console.
     *
     * @param exceptionId  the numeric ID from the SCM master register
     * @param severity     "MINOR", "MAJOR", or "WARNING"
     * @param subsystem    subsystem name (always "Packing" in our case)
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

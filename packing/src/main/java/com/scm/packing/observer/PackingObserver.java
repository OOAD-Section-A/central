package com.scm.packing.observer;

import com.scm.packing.mvc.model.PackingJob;

/**
 * Observer interface for the <b>Observer (Behavioral) design pattern</b>.
 *
 * <p>Any component that wants to react to changes in the
 * {@link com.scm.packing.mvc.model.PackingModel} implements this interface
 * and registers itself.  The model (subject) calls
 * {@link #onPackingEvent(PackingEventType, PackingJob, String)} whenever
 * something noteworthy happens.</p>
 *
 * <p><b>SOLID – Dependency Inversion:</b> The model depends on this
 * abstraction, not on concrete view classes.  Views depend on this
 * abstraction, not on the concrete model.</p>
 *
 * <p><b>SOLID – Interface Segregation:</b> This is a single-method
 * functional interface — observers are not forced to implement methods
 * they do not need.</p>
 */
@FunctionalInterface
public interface PackingObserver {

    /**
     * Called by the subject whenever a packing event occurs.
     *
     * @param eventType the kind of event (see {@link PackingEventType})
     * @param job       the affected job, or {@code null} for STATUS_MESSAGE events
     * @param message   optional human-readable detail; may be {@code null}
     */
    void onPackingEvent(PackingEventType eventType, PackingJob job, String message);
}

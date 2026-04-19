package com.scm.packing.observer;

/**
 * Enum listing every kind of event the {@link PackingObserver} can be
 * notified about.
 *
 * <p><b>Design Pattern – Observer (Behavioral):</b> Event types are
 * decoupled from both the subject ({@code PackingModel}) and the
 * observers (views, loggers).  Adding a new event type here does not
 * require modifying existing observers — they can simply ignore types
 * they do not care about.</p>
 */
public enum PackingEventType {

    /** A new job has been added to the model. */
    JOB_ADDED,

    /** A job's status or progress has been updated. */
    JOB_UPDATED,

    /** A job has been removed from the model. */
    JOB_REMOVED,

    /** A general status / log message for the status bar. */
    STATUS_MESSAGE
}

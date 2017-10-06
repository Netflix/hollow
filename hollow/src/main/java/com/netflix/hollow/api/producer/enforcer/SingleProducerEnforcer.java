package com.netflix.hollow.api.producer.enforcer;

/*
 * Allow implementations for restricting a single producer in a distributed system.
 */
public interface SingleProducerEnforcer {

    /**
     * Mark producer to enable processing cycles. Default implementation (BasicSingleProducerEnforcer) is enabled by default.
     */
    void enable();

    /**
     * Relinquish the primary producer status. disable() can be invoked any time, but takes affect only after
     * current cycle is completed (or if no cycle is running).
     */
    void disable();

    /**
     * runCycle() is executed only if isPrimary() is true
     * @return boolean
     */
    boolean isPrimary();

}

package com.netflix.hollow.api.producer.enforcer;

public class BasicSingleProducerEnforcer extends AbstractSingleProducerEnforcer {
    private boolean isPrimary = true;

    @Override
    void _enable() {
        isPrimary = true;
    }

    @Override
    void _disable() {
        isPrimary = false;
    }

    @Override
    boolean _isPrimary() {
        return isPrimary;
    }

}

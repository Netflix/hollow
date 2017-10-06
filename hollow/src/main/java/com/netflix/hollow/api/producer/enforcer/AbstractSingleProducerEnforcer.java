package com.netflix.hollow.api.producer.enforcer;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.netflix.hollow.api.producer.AbstractHollowProducerListener;

public abstract class AbstractSingleProducerEnforcer extends AbstractHollowProducerListener implements SingleProducerEnforcer {
    private boolean hasCycleStarted = false;
    private boolean doStopUponCycleComplete = false;
    private boolean wasPrimary = false;
    private final Logger logger = Logger.getLogger(AbstractSingleProducerEnforcer.class.getName());

    abstract void _enable();

    abstract void _disable();

    abstract boolean _isPrimary();

    @Override
    public void enable() {
        if (_isPrimary()) {
            return;
        }
        _enable();
    }

    @Override
    public void disable() {
        if (!hasCycleStarted) {
            disableNow();
        } else {
            doStopUponCycleComplete = true;
        }
    }

    @Override
    public boolean isPrimary() {
        final boolean primary = _isPrimary();
        if (!primary) {
            if (wasPrimary) {
                logger.log(Level.WARNING, "SingleProducerEnforcer: lost primary producer status");
            }
        } else {
            wasPrimary = true;
        }
        return primary;
    }

    @Override
    public void onCycleStart(long version) {
        hasCycleStarted = true;
    }

    @Override
    public void onCycleComplete(ProducerStatus status, long elapsed, TimeUnit unit) {
        hasCycleStarted = false;
        if (doStopUponCycleComplete) {
            disableNow();
        }
    }

    private void disableNow() {
        if (_isPrimary()) {
            _disable();
        }
        doStopUponCycleComplete = false;
        wasPrimary = false;
    }

    // visible for testing
    boolean getWasPrimary() {
        return wasPrimary;
    }

}

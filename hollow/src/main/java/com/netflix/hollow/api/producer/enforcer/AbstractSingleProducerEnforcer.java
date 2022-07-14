/*
 *  Copyright 2016-2019 Netflix, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */
package com.netflix.hollow.api.producer.enforcer;

import com.netflix.hollow.api.producer.AbstractHollowProducerListener;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractSingleProducerEnforcer extends AbstractHollowProducerListener implements SingleProducerEnforcer {
    private boolean hasCycleStarted = false;
    private boolean doStopUponCycleComplete = false;
    private boolean wasPrimary = false;
    private final Logger logger = Logger.getLogger(AbstractSingleProducerEnforcer.class.getName());

    private final Lock lock = new ReentrantLock();

    protected abstract void _enable();

    protected abstract void _disable();

    protected abstract boolean _isPrimary();

    protected void _force() {
        throw new UnsupportedOperationException("forcing a producer to become primary is not supported");
    }

    @Override
    public void enable() {
        if(_isPrimary()) {
            return;
        }
        lock.lock();
        try {
            _enable();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void disable() {
        if(!hasCycleStarted) {
            disableNow();
        } else {
            doStopUponCycleComplete = true;
        }
    }

    @Override
    public boolean isPrimary() {
        final boolean primary = _isPrimary();
        if(!primary) {
            if(wasPrimary) {
                logger.log(Level.WARNING, "SingleProducerEnforcer: lost primary producer status");
            }
        } else {
            wasPrimary = true;
        }
        return primary;
    }

    @Override
    public void force() {
        if(_isPrimary()) {
            return;
        }
        _force();
    }

    @Override
    public void lock() {
        lock.lock();
    }

    @Override
    public void unlock() {
        lock.unlock();

    }

    @Override
    public void onCycleStart(long version) {
        hasCycleStarted = true;
    }

    @Override
    public void onCycleComplete(ProducerStatus status, long elapsed, TimeUnit unit) {
        hasCycleStarted = false;
        if(doStopUponCycleComplete) {
            disableNow();
        }
    }

    private void disableNow() {
        if(_isPrimary()) {
            lock.lock();
            try {
                _disable();
            } finally {
                lock.unlock();
            }
        }
        doStopUponCycleComplete = false;
        wasPrimary = false;
    }

    // visible for testing
    protected boolean getWasPrimary() {
        return wasPrimary;
    }

}

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

/*
 * Allow implementations for restricting a single producer in a distributed system.
 */
public interface SingleProducerEnforcer {

    /**
     * Mark producer to enable processing cycles. Default implementation (BasicSingleProducerEnforcer) is enabled by default.
     */
    void enable();

    /**
     * Relinquish the primary producer status. disable() can be invoked any time, and it gives up producer primary
     * status thereby allowing a different producer to acquire primary status. If the current producer is mid-cycle
     * then the cycle continues on but will result in an announcement failure.
     */
    void disable();

    /**
     * runCycle() is executed only if isPrimary() is true
     * @return boolean
     */
    boolean isPrimary();

    /**
     * Force marking producer to enable processing cycles.
     */
    void force();

    /**
     * Lock local changes to primary status i.e. block enable or disable until unlock is called
     */
    default void lock() {
    }

    /**
     * Unlock local changes to producer primary status i.e. enable/disable are unblocked
     */
    default void unlock() {
    }
}

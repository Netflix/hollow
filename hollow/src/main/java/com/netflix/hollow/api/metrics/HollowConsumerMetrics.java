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
package com.netflix.hollow.api.metrics;

import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

import java.util.concurrent.atomic.AtomicLong;

public class HollowConsumerMetrics extends HollowMetrics {
    private int refreshFailed;      // TODO: Move these metrics over to com.netflix.hollow.api.consumer.metrics.AbstractRefreshMetricsListener
    private int refreshSucceeded;
    private final AtomicLong lastRefreshStartNs = new AtomicLong(); //last time a refresh started
    private final AtomicLong lastRefreshEndNs = new AtomicLong();

    public void setLastRefreshStartNs(long startNs) {
        lastRefreshStartNs.set(startNs);
    }

    public AtomicLong getLastRefreshStartNs() {
        return lastRefreshStartNs;
    }

    public void setLastRefreshEndNs(long endNs) {
        lastRefreshEndNs.set(endNs);
    }

    public AtomicLong getLastRefreshEndNs() {
       return lastRefreshEndNs;
    }



    /**
     * Updates the consumer metrics:
     * refresh succeeded, version and type's footprint and ordinals.
     * @param hollowReadStateEngine the state engine
     * @param version the version
     */
    public void updateTypeStateMetrics(HollowReadStateEngine hollowReadStateEngine, long version) {
        this.refreshSucceeded++;
        super.update(hollowReadStateEngine, version);
    }

    public void updateRefreshFailed() {
        this.refreshFailed++;
    }

    public int getRefreshFailed() {
        return this.refreshFailed;
    }

    public int getRefreshSucceded() {
        return this.refreshSucceeded;
    }

    /**
     * Records that a repair transition was triggered due to checksum mismatch.
     *
     * @param version the version being repaired
     */
    public void recordRepairTriggered(long version) {
        // Default implementation (subclasses override)
    }

    /**
     * Records the duration of a repair operation.
     *
     * @param version the version being repaired
     * @param durationMs duration in milliseconds
     */
    public void recordRepairDuration(long version, long durationMs) {
        // Default implementation
    }

    /**
     * Records the number of ordinals repaired for a specific type.
     *
     * @param typeName the type name
     * @param ordinalsRepaired count of ordinals repaired
     */
    public void recordRepairOrdinals(String typeName, int ordinalsRepaired) {
        // Default implementation
    }

    /**
     * Records that checksum validation failed.
     *
     * @param version the version at which checksum mismatch occurred
     * @param producerChecksum the producer's checksum
     * @param consumerChecksum the consumer's checksum
     */
    public void recordChecksumMismatch(long version, long producerChecksum, long consumerChecksum) {
        // Default implementation
    }
}

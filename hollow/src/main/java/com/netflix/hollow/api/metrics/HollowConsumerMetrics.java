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

public class HollowConsumerMetrics extends HollowMetrics {
    private int refreshFailed;      // TODO: Move these metrics over to com.netflix.hollow.api.consumer.metrics.AbstractRefreshMetricsListener
    private int refreshSucceeded;

    private long durationNs;

    public void setDurationNs(long durationNs) {
        this.durationNs = durationNs;
    }

    public long getDurationNs() {
        return durationNs;
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
}

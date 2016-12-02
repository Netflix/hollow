/*
 *
 *  Copyright 2016 Netflix, Inc.
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
package com.netflix.hollow.api.client;

import com.netflix.hollow.api.custom.HollowAPI;

import com.netflix.hollow.tools.history.HollowHistory;

/**
 * Defines various aspects of data access guarantees and update behavior which impact the heap footprint/GC behavior of hollow.
 * 
 * Implementations are often a {@link SpecifiedConfig}.
 * 
 * @author dkoszewnik
 *
 */
public interface HollowClientMemoryConfig {

    public static final long ONE_HOUR = 60 * 60 * 1000;

    public static final HollowClientMemoryConfig DEFAULT_CONFIG = new SpecifiedConfig(false, false, ONE_HOUR, ONE_HOUR);

    /**
     * Whether or not long-lived object support is enabled.
     * 
     * Because Hollow reuses pooled memory, if references to Hollow records are held too long, the underlying data may
     * be overwritten.  When long-lived object support is enabled, Hollow records referenced via a {@link HollowAPI} will,
     * after an update, be backed by a reserved copy of the data at the time the reference was created.  This guarantees
     * that even if a reference is held for a long time, it will continue to return the same data when interrogated.
     * 
     * These reserved copies are backed by the {@link HollowHistory} data structure.
     */
    public boolean enableLongLivedObjectSupport();

    public boolean enableExpiredUsageStackTraces();

    /**
     * If long-lived object support is enabled, this returns the number of milliseconds before the {@link StaleHollowReferenceDetector}
     * will begin flagging usage of stale objects.
     * 
     * @return
     */
    public long gracePeriodMillis();

    /**
     * If long-lived object support is enabled, this defines the number of milliseconds, after the grace period, during which
     * data is still available in stale references, but usage will be flagged by the {@link StaleHollowReferenceDetector}.
     * 
     * After the grace period + usage detection period have expired, the data from stale references will become inaccessible if
     * dropDataAutomatically() is enabled.
     * 
     * @return
     */
    public long usageDetectionPeriodMillis();

    /**
     * Whether or not to drop data behind stale references after the grace period + usage detection period has elapsed, assuming
     * that no usage was detected during the usage detection period. 
     * 
     * @return
     */
    public boolean dropDataAutomatically();

    /**
     * Drop data even if flagged during the usage detection period.
     * @return
     */
    public boolean forceDropData();
    
    /**
     * Whether or not a double snapshot will ever be attempted by the {@link HollowClient}
     * @return
     */
    public boolean allowDoubleSnapshot();


    public static class SpecifiedConfig implements HollowClientMemoryConfig {

        private final boolean enableLongLivedObjectSupport;
        private final boolean dropDataAutomatically;
        private final long gracePeriodMillis;
        private final long usageDetectionPeriodMillis;

        public SpecifiedConfig(boolean enableLongLivedObjectSupport, boolean dropDataAutomatically,
                                           long gracePeriodMillis, long usageDetectionPeriodMillis) {
            this.enableLongLivedObjectSupport = enableLongLivedObjectSupport;
            this.dropDataAutomatically = dropDataAutomatically;
            this.gracePeriodMillis = gracePeriodMillis;
            this.usageDetectionPeriodMillis = usageDetectionPeriodMillis;
        }

        public boolean enableLongLivedObjectSupport() { return enableLongLivedObjectSupport; }

        public boolean dropDataAutomatically() { return dropDataAutomatically; }

        public long gracePeriodMillis() { return gracePeriodMillis; }

        public long usageDetectionPeriodMillis() { return usageDetectionPeriodMillis; }

        public boolean enableExpiredUsageStackTraces() { return false; }

        public boolean forceDropData() { return false; }
        
        public boolean allowDoubleSnapshot() { return true; }

    }
}

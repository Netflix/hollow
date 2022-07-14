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
package com.netflix.hollow.api.client;

import com.netflix.hollow.api.consumer.HollowConsumer;

/**
 * Defines various aspects of data access guarantees and update behavior which impact the heap footprint/GC behavior of hollow.
 * 
 * Implementations are often a {@link SpecifiedConfig}.
 * 
 * @deprecated Implement the {@link HollowConsumer.ObjectLongevityConfig} and/or {@link HollowConsumer.DoubleSnapshotConfig} for use 
 * with the {@link HollowConsumer} instead.
 * 
 */
public interface HollowClientMemoryConfig extends HollowConsumer.ObjectLongevityConfig {

    long ONE_HOUR = 60 * 60 * 1000;

    HollowClientMemoryConfig DEFAULT_CONFIG = new SpecifiedConfig(false, false, ONE_HOUR, ONE_HOUR);

    /**
     * @return whether or not a double snapshot will ever be attempted by the {@link HollowClient}
     */
    boolean allowDoubleSnapshot();


    class SpecifiedConfig implements HollowClientMemoryConfig {

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

        public boolean enableLongLivedObjectSupport() {
            return enableLongLivedObjectSupport;
        }

        public boolean dropDataAutomatically() {
            return dropDataAutomatically;
        }

        public long gracePeriodMillis() {
            return gracePeriodMillis;
        }

        public long usageDetectionPeriodMillis() {
            return usageDetectionPeriodMillis;
        }

        public boolean enableExpiredUsageStackTraces() {
            return false;
        }

        public boolean forceDropData() {
            return false;
        }

        public boolean allowDoubleSnapshot() {
            return true;
        }

    }
}

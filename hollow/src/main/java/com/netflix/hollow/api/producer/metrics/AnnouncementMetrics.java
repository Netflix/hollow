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
package com.netflix.hollow.api.producer.metrics;

import java.util.Map;
import java.util.OptionalLong;

public class AnnouncementMetrics {

    private long dataSizeBytes;                             // Heap footprint of announced blob in bytes
    private Map<String, Integer> numShardsPerType;
    private Map<String, Long> shardSizePerType;
    private long announcementDurationMillis;                // Announcement duration in ms, only applicable to completed cycles (skipped cycles dont announce)
    private boolean isAnnouncementSuccess;                  // true if announcement was successful, false if announcement failed
    private OptionalLong lastAnnouncementSuccessTimeNano;   // monotonic time of last successful announcement (no relation to wall clock), N/A until first successful announcement
    private OptionalLong deltaChainVersionCounter;

    public long getDataSizeBytes() {
        return dataSizeBytes;
    }
    public Map<String, Integer> getNumShardsPerType() {
        return numShardsPerType;
    }
    public Map<String, Long> getShardSizePerType() {
        return shardSizePerType;
    }
    public long getAnnouncementDurationMillis() {
        return announcementDurationMillis;
    }
    public boolean getIsAnnouncementSuccess() {
        return isAnnouncementSuccess;
    }
    public OptionalLong getLastAnnouncementSuccessTimeNano() {
        return lastAnnouncementSuccessTimeNano;
    }
    public OptionalLong getDeltaChainVersionCounter() {
        return deltaChainVersionCounter;
    }


    private AnnouncementMetrics(Builder builder) {
        this.dataSizeBytes = builder.dataSizeBytes;
        this.numShardsPerType = builder.numShardsPerType;
        this.shardSizePerType = builder.shardSizePerType;
        this.announcementDurationMillis = builder.announcementDurationMillis;
        this.isAnnouncementSuccess = builder.isAnnouncementSuccess;
        this.lastAnnouncementSuccessTimeNano = builder.lastAnnouncementSuccessTimeNano;
        this.deltaChainVersionCounter = builder.deltaChainVersionCounter;
    }

    public static final class Builder {
        private long dataSizeBytes;
        private long announcementDurationMillis;
        private boolean isAnnouncementSuccess;
        private OptionalLong lastAnnouncementSuccessTimeNano;
        private OptionalLong deltaChainVersionCounter;
        private Map<String, Integer> numShardsPerType;
        private Map<String, Long> shardSizePerType;

        public Builder() {
            lastAnnouncementSuccessTimeNano = OptionalLong.empty();
            deltaChainVersionCounter = OptionalLong.empty();
        }

        public Builder setDataSizeBytes(long dataSizeBytes) {
            this.dataSizeBytes = dataSizeBytes;
            return this;
        }
        public Builder setNumShardsPerType(Map<String, Integer> numShardsPerType) {
            this.numShardsPerType = numShardsPerType;
            return this;
        }
        public Builder setShardSizePerType(Map<String, Long> shardSizePerType) {
            this.shardSizePerType = shardSizePerType;
            return this;
        }
        public Builder setAnnouncementDurationMillis(long announcementDurationMillis) {
            this.announcementDurationMillis = announcementDurationMillis;
            return this;
        }
        public Builder setIsAnnouncementSuccess(boolean isAnnouncementSuccess) {
            this.isAnnouncementSuccess = isAnnouncementSuccess;
            return this;
        }
        public Builder setLastAnnouncementSuccessTimeNano(long lastAnnouncementSuccessTimeNano) {
            this.lastAnnouncementSuccessTimeNano = OptionalLong.of(lastAnnouncementSuccessTimeNano);
            return this;
        }
        public Builder setDeltaChainVersionCounter(long deltaChainVersionCounter) {
            this.deltaChainVersionCounter = OptionalLong.of(deltaChainVersionCounter);
            return this;
        }

        public AnnouncementMetrics build() {
            return new AnnouncementMetrics(this);
        }
    }
}

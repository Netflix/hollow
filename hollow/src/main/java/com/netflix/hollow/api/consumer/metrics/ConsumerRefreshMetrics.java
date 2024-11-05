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
package com.netflix.hollow.api.consumer.metrics;

import com.netflix.hollow.api.consumer.HollowConsumer.Blob.BlobType;
import java.util.List;
import java.util.OptionalLong;

public class ConsumerRefreshMetrics {

    private long durationMillis;
    private boolean isRefreshSuccess;               // true if refresh was successful, false if refresh failed
    private boolean isInitialLoad;                  // true if initial load, false if subsequent refresh
    private BlobType overallRefreshType;            // snapshot, delta, or reverse delta
    private UpdatePlanDetails updatePlanDetails;    // details about the update plan such as no. and types of transitions and no. of successful transitions
    private long consecutiveFailures;
    private OptionalLong refreshSuccessAgeMillisOptional; // time elapsed since the previous successful refresh
    private long refreshEndTimeNano;                // monotonic system time when refresh ended
    private OptionalLong cycleStartTimestamp;       // timestamp in millis of when cycle started for the loaded data version
    private OptionalLong announcementTimestamp; // timestamp in milliseconds to mark announcement for the loaded data version
    private OptionalLong deltaChainVersionCounter;  // the sequence number of a version in a delta chain

    /**
     * A class that contains details of the consumer refresh update plan that may be useful to report as metrics or logs.
     * These details are computed in {@code AbstractRefreshMetricsListener} during execution of the update plan.
     */
    public static class UpdatePlanDetails {
        long beforeVersion;
        long desiredVersion;
        List<BlobType> transitionSequence;
        int numSuccessfulTransitions;

        public long getBeforeVersion() {
            return beforeVersion;
        }
        public long getDesiredVersion() {
            return desiredVersion;
        }
        public List<BlobType> getTransitionSequence() {
            return transitionSequence;
        }
        public int getNumSuccessfulTransitions() {
            return numSuccessfulTransitions;
        }
    }

    public long getDurationMillis() {
        return durationMillis;
    }
    public boolean getIsRefreshSuccess() {
        return isRefreshSuccess;
    }
    public boolean getIsInitialLoad() {
        return isInitialLoad;
    }
    public BlobType getOverallRefreshType() {
        return overallRefreshType;
    }
    public UpdatePlanDetails getUpdatePlanDetails() {
        return updatePlanDetails;
    }
    public long getConsecutiveFailures() {
        return consecutiveFailures;
    }
    public OptionalLong getRefreshSuccessAgeMillisOptional() {
        return refreshSuccessAgeMillisOptional;
    }
    public long getRefreshEndTimeNano() {
        return refreshEndTimeNano;
    }
    public OptionalLong getCycleStartTimestamp() {
        return cycleStartTimestamp;
    }
    public OptionalLong getAnnouncementTimestamp() {
        return announcementTimestamp;
    }
    public OptionalLong getDeltaChainVersionCounter() {
        return deltaChainVersionCounter;
    }

    private ConsumerRefreshMetrics(Builder builder) {
        this.durationMillis = builder.durationMillis;
        this.isRefreshSuccess = builder.isRefreshSuccess;
        this.isInitialLoad = builder.isInitialLoad;
        this.overallRefreshType = builder.overallRefreshType;
        this.updatePlanDetails = builder.updatePlanDetails;
        this.consecutiveFailures = builder.consecutiveFailures;
        this.refreshSuccessAgeMillisOptional = builder.refreshSuccessAgeMillisOptional;
        this.refreshEndTimeNano = builder.refreshEndTimeNano;
        this.cycleStartTimestamp = builder.cycleStartTimestamp;
        this.announcementTimestamp = builder.announcementTimestamp;
        this.deltaChainVersionCounter = builder.deltaChainVersionCounter;
    }

    public static final class Builder {
        private long durationMillis;
        private boolean isRefreshSuccess;
        private boolean isInitialLoad;
        private BlobType overallRefreshType;
        private UpdatePlanDetails updatePlanDetails;
        private long consecutiveFailures;
        private OptionalLong refreshSuccessAgeMillisOptional;
        private long refreshEndTimeNano;
        private OptionalLong cycleStartTimestamp;
        private OptionalLong announcementTimestamp;
        private OptionalLong deltaChainVersionCounter;

        public Builder() {
            refreshSuccessAgeMillisOptional = OptionalLong.empty();
            cycleStartTimestamp = OptionalLong.empty();
            announcementTimestamp = OptionalLong.empty();
            deltaChainVersionCounter = OptionalLong.empty();
        }

        public Builder setDurationMillis(long durationMillis) {
            this.durationMillis = durationMillis;
            return this;
        }
        public Builder setIsRefreshSuccess(boolean isRefreshSuccess) {
            this.isRefreshSuccess = isRefreshSuccess;
            return this;
        }
        public Builder setIsInitialLoad(boolean isInitialLoad) {
            this.isInitialLoad = isInitialLoad;
            return this;
        }
        public Builder setOverallRefreshType(BlobType overallRefreshType) {
            this.overallRefreshType = overallRefreshType;
            return this;
        }
        public Builder setUpdatePlanDetails(
                UpdatePlanDetails updatePlanDetails) {
            this.updatePlanDetails = updatePlanDetails;
            return this;
        }
        public Builder setConsecutiveFailures(long consecutiveFailures) {
            this.consecutiveFailures = consecutiveFailures;
            return this;
        }
        public Builder setRefreshSuccessAgeMillisOptional(long refreshSuccessAgeMillis) {
            this.refreshSuccessAgeMillisOptional = OptionalLong.of(refreshSuccessAgeMillis);
            return this;
        }
        public Builder setRefreshEndTimeNano(long refreshEndTimeNano) {
            this.refreshEndTimeNano = refreshEndTimeNano;
            return this;
        }
        public Builder setCycleStartTimestamp(long cycleStartTimestamp) {
            this.cycleStartTimestamp = OptionalLong.of(cycleStartTimestamp);
            return this;
        }

        public Builder setAnnouncementTimestamp(long announcementTimestamp) {
            this.announcementTimestamp = OptionalLong.of(announcementTimestamp);
            return this;
        }
        public Builder setDeltaChainVersionCounter(long deltaChainVersionCounter) {
            this.deltaChainVersionCounter = OptionalLong.of(deltaChainVersionCounter);
            return this;
        }

        public ConsumerRefreshMetrics build() {
            return new ConsumerRefreshMetrics(this);
        }
    }
}
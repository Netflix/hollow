package com.netflix.hollow.api.consumer.metrics;

import com.netflix.hollow.api.consumer.HollowConsumer.Blob.BlobType;
import java.util.Optional;

public class ConsumerRefreshMetrics {

    private long durationMillis;
    private boolean isRefreshSuccess;               // true if refresh was successful, false if refresh failed
    private boolean isInitialLoad;                  // true if initial load, false if subsequent refresh
    private BlobType overallRefreshType;            // snapshot, delta, or reverse delta
    private UpdatePlanDetails updatePlanDetails;    // details about the update plan such as no. and types of transitions and no. of successful transitions
    private long consecutiveFailures;
    private Optional<Long> refreshSuccessAgeMillisOptional; // time elapsed since the previous successful refresh

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
    public Optional<Long> getRefreshSuccessAgeMillisOptional() {
        return refreshSuccessAgeMillisOptional;
    }

    private ConsumerRefreshMetrics(long durationMillis, boolean isRefreshSuccess, boolean isInitialLoad, BlobType overallRefreshType,
            UpdatePlanDetails updatePlanDetails, long consecutiveFailures, Optional<Long> refreshSuccessAgeMillisOptional) {
        this.durationMillis = durationMillis;
        this.isRefreshSuccess = isRefreshSuccess;
        this.isInitialLoad = isInitialLoad;
        this.overallRefreshType = overallRefreshType;
        this.updatePlanDetails = updatePlanDetails;
        this.consecutiveFailures = consecutiveFailures;
        this.refreshSuccessAgeMillisOptional = refreshSuccessAgeMillisOptional;
    }

    public static final class Builder {
        private long durationMillis;
        private boolean isRefreshSuccess;
        private boolean isInitialLoad;
        private BlobType overallRefreshType;
        private UpdatePlanDetails updatePlanDetails;
        private long consecutiveFailures;
        private Optional<Long> refreshSuccessAgeMillisOptional;

        public Builder() {
            refreshSuccessAgeMillisOptional = Optional.empty();
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
        public Builder setOverallRefreshType(
                BlobType overallRefreshType) {
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
            this.refreshSuccessAgeMillisOptional = Optional.of(refreshSuccessAgeMillis);
            return this;
        }

        public ConsumerRefreshMetrics build() {
            return new ConsumerRefreshMetrics(durationMillis, isRefreshSuccess, isInitialLoad, overallRefreshType,
                    updatePlanDetails, consecutiveFailures, refreshSuccessAgeMillisOptional);
        }
    }
}
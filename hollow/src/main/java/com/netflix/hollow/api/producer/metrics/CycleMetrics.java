package com.netflix.hollow.api.producer.metrics;

import java.util.Optional;
import java.util.OptionalLong;

public class CycleMetrics {

    private long consecutiveFailures;
    private OptionalLong cycleDurationMillisOptional;               // Cycle start to end duration, only applicable to completed cycles
    private Optional<Boolean> isCycleSuccessOptional;               // true if cycle was successful, false if cycle failed, N/A if cycle was skipped
    private OptionalLong lastCycleSuccessTimeNanoOptional;          // monotonic time of last successful cycle (no relation to wall clock), N/A until first successful cycle

    public long getConsecutiveFailures() {
        return consecutiveFailures;
    }
    public OptionalLong getCycleDurationMillisOptional() {
        return cycleDurationMillisOptional;
    }
    public Optional<Boolean> getIsCycleSuccessOptional() {
        return isCycleSuccessOptional;
    }
    public OptionalLong getLastCycleSuccessTimeNanoOptional() {
        return lastCycleSuccessTimeNanoOptional;
    }

    private CycleMetrics(Builder builder) {
        this.consecutiveFailures = builder.consecutiveFailures;
        this.cycleDurationMillisOptional = builder.cycleDurationMillisOptional;
        this.isCycleSuccessOptional = builder.isCycleSuccessOptional;
        this.lastCycleSuccessTimeNanoOptional = builder.lastCycleSuccessTimeNanoOptional;
    }

    public static final class Builder {
        private long consecutiveFailures;
        private OptionalLong cycleDurationMillisOptional;
        private Optional<Boolean> isCycleSuccessOptional;
        private OptionalLong lastCycleSuccessTimeNanoOptional;

        public Builder() {
            isCycleSuccessOptional = Optional.empty();
            cycleDurationMillisOptional = OptionalLong.empty();
            lastCycleSuccessTimeNanoOptional = OptionalLong.empty();
        }

        public Builder setConsecutiveFailures(long consecutiveFailures) {
            this.consecutiveFailures = consecutiveFailures;
            return this;
        }
        public Builder setCycleDurationMillisOptional(long cycleDurationMillis) {
            this.cycleDurationMillisOptional = OptionalLong.of(cycleDurationMillis);
            return this;
        }
        public Builder setIsCycleSuccessOptional(boolean isCycleSuccessOptional) {
            this.isCycleSuccessOptional = Optional.of(isCycleSuccessOptional);
            return this;
        }
        public Builder setLastCycleSuccessTimeNanoOptional(long lastCycleSuccessTimeNano) {
            this.lastCycleSuccessTimeNanoOptional = OptionalLong.of(lastCycleSuccessTimeNano);
            return this;
        }

        public CycleMetrics build() {
            return new CycleMetrics(this);
        }
    }
}

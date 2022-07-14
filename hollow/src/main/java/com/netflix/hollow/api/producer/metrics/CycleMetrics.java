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

import java.util.Optional;
import java.util.OptionalLong;

public class CycleMetrics {

    private long consecutiveFailures;
    private OptionalLong cycleDurationMillis;               // Cycle start to end duration, only applicable to completed cycles
    private Optional<Boolean> isCycleSuccess;               // true if cycle was successful, false if cycle failed, N/A if cycle was skipped
    private OptionalLong lastCycleSuccessTimeNano;          // monotonic time of last successful cycle (no relation to wall clock), N/A until first successful cycle

    public long getConsecutiveFailures() {
        return consecutiveFailures;
    }

    public OptionalLong getCycleDurationMillis() {
        return cycleDurationMillis;
    }

    public Optional<Boolean> getIsCycleSuccess() {
        return isCycleSuccess;
    }

    public OptionalLong getLastCycleSuccessTimeNano() {
        return lastCycleSuccessTimeNano;
    }

    private CycleMetrics(Builder builder) {
        this.consecutiveFailures = builder.consecutiveFailures;
        this.cycleDurationMillis = builder.cycleDurationMillis;
        this.isCycleSuccess = builder.isCycleSuccess;
        this.lastCycleSuccessTimeNano = builder.lastCycleSuccessTimeNano;
    }

    public static final class Builder {
        private long consecutiveFailures;
        private OptionalLong cycleDurationMillis;
        private Optional<Boolean> isCycleSuccess;
        private OptionalLong lastCycleSuccessTimeNano;

        public Builder() {
            isCycleSuccess = Optional.empty();
            cycleDurationMillis = OptionalLong.empty();
            lastCycleSuccessTimeNano = OptionalLong.empty();
        }

        public Builder setConsecutiveFailures(long consecutiveFailures) {
            this.consecutiveFailures = consecutiveFailures;
            return this;
        }

        public Builder setCycleDurationMillis(long cycleDurationMillis) {
            this.cycleDurationMillis = OptionalLong.of(cycleDurationMillis);
            return this;
        }

        public Builder setIsCycleSuccess(boolean isCycleSuccess) {
            this.isCycleSuccess = Optional.of(isCycleSuccess);
            return this;
        }

        public Builder setLastCycleSuccessTimeNano(long lastCycleSuccessTimeNano) {
            this.lastCycleSuccessTimeNano = OptionalLong.of(lastCycleSuccessTimeNano);
            return this;
        }

        public CycleMetrics build() {
            return new CycleMetrics(this);
        }
    }
}

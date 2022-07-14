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
package com.netflix.hollow.api.producer;

import static com.netflix.hollow.api.producer.IncrementalCycleListener.Status.FAIL;
import static com.netflix.hollow.api.producer.IncrementalCycleListener.Status.SUCCESS;

import java.util.EventListener;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Beta API subject to change.
 *
 * A class should implement {@code IncrementalCycleListener}, if it wants to be notified on start/completion of various stages of the {@link HollowIncrementalProducer}.
 *
 * @deprecated see {@link com.netflix.hollow.api.producer.listener.IncrementalPopulateListener}
 * @see com.netflix.hollow.api.producer.listener.IncrementalPopulateListener
 */
@Deprecated
public interface IncrementalCycleListener extends EventListener {

    /**
     * Called after {@code HollowIncrementalProducer} has completed a cycle normally or abnormally. A {@code SUCCESS} status indicates that the
     * entire cycle was successful and the producer is available to begin another cycle.
     *
     * @param status IncrementalCycleStatus of this cycle. {@link IncrementalCycleListener.IncrementalCycleStatus#getStatus()} will return {@code SUCCESS}
     *   when the a new data state has been announced to consumers or when there were no changes to the data.
     * @param elapsed duration of the cycle in {@code unit} units
     * @param unit units of the {@code elapsed} duration
     */
    public void onCycleComplete(IncrementalCycleStatus status, long elapsed, TimeUnit unit);

    /**
     * Called after {@code HollowIncrementalProducer} has completed a cycle normally or abnormally. A {@code FAIL} status indicates that the
     * entire cycle failed.
     *
     * @param status IncrementalCycleStatus of this cycle. {@link IncrementalCycleListener.IncrementalCycleStatus#getStatus()} will return {@code FAIL}
     * @param elapsed duration of the cycle in {@code unit} units
     * @param unit units of the {@code elapsed} duration
     */
    public void onCycleFail(IncrementalCycleStatus status, long elapsed, TimeUnit unit);

    public class IncrementalCycleStatus {
        private final Status status;
        private final long version;
        private final long recordsAddedOrModified;
        private final long recordsRemoved;
        private final Map<String, Object> cycleMetadata;
        private final Throwable throwable;

        public IncrementalCycleStatus(Status status, long version, Throwable throwable, long recordsAddedOrModified, long recordsRemoved, Map<String, Object> cycleMetadata) {
            this.status = status;
            this.version = version;
            this.recordsAddedOrModified = recordsAddedOrModified;
            this.recordsRemoved = recordsRemoved;
            this.cycleMetadata = cycleMetadata;
            this.throwable = throwable;
        }

        /**
         * This version is currently under process by {@code HollowIncrementalProducer} which matches with version of {@code HollowProducer}.
         *
         * @return Current version of the {@code HollowIncrementalProducer}.
         */
        public long getVersion() {
            return version;
        }

        /**
         * Status of the latest stage completed by {@code HollowIncrementalProducer}.
         *
         * @return SUCCESS or FAIL.
         */
        public Status getStatus() {
            return status;
        }

        /**
         *
         * @return the number of records that potentially were added/modified from the dataset.
         */
        public long getRecordsAddedOrModified() {
            return recordsAddedOrModified;
        }

        /**
         * @return the number of records that potentially were removed from the dataset.
         */
        public long getRecordsRemoved() {
            return recordsRemoved;
        }

        /**
         * @return cycle metadata attached before runCycle in {@code HollowIncrementalProducer}
         */
        public Map<String, Object> getCycleMetadata() {
            return cycleMetadata;
        }

        /**
         * Returns the failure cause if this status represents a {@code HollowProducer} failure that was caused by an exception.
         *
         * @return Throwable if {@code Status.equals(FAIL)} else null.
         */
        public Throwable getCause() {
            return throwable;
        }

        public static final class Builder {
            private final long start;
            private long end;

            private long version = Long.MIN_VALUE;
            private Status status = FAIL;
            private Throwable cause = null;
            private long recordsAddedOrModified;
            private long recordsRemoved;
            private Map<String, Object> cycleMetadata;

            Builder() {
                start = System.currentTimeMillis();
            }

            Builder success(long version, long recordsAddedOrModified, long recordsRemoved, Map<String, Object> cycleMetadata) {
                this.status = SUCCESS;
                this.version = version;
                this.recordsAddedOrModified = recordsAddedOrModified;
                this.recordsRemoved = recordsRemoved;
                this.cycleMetadata = cycleMetadata;
                return this;
            }

            Builder fail(Throwable cause, long recordsAddedOrModified, long recordsRemoved, Map<String, Object> cycleMetadata) {
                this.status = FAIL;
                this.cause = cause;
                this.recordsAddedOrModified = recordsAddedOrModified;
                this.recordsRemoved = recordsRemoved;
                this.cycleMetadata = cycleMetadata;
                return this;
            }

            IncrementalCycleStatus build() {
                end = System.currentTimeMillis();
                return new IncrementalCycleStatus(status, version, cause, recordsAddedOrModified, recordsRemoved, cycleMetadata);
            }

            long elapsed() {
                return end - start;
            }

        }
    }

    public enum Status {
        SUCCESS, FAIL
    }
}

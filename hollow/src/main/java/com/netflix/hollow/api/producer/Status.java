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

import java.time.Duration;

/**
 * A status representing success or failure for a particular producer action.
 */
public final class Status {
    final StatusType type;
    final Throwable cause;

    public Status(StatusType type, Throwable cause) {
        this.type = type;
        this.cause = cause;
    }

    /**
     * The status type.
     */
    public enum StatusType {
        /**
         * If the producer action was successful.
         */
        SUCCESS,

        /**
         * If the producer action failed.
         */
        FAIL;
    }

    /**
     * Gets the status type
     *
     * @return the status type
     */
    public StatusType getType() {
        return type;
    }

    /**
     * Returns the cause of producer action failure.
     *
     * @return the cause of producer action failure
     */
    public Throwable getCause() {
        return cause;
    }

    abstract static class AbstractStatusBuilder<T extends AbstractStatusBuilder<T>> {
        StatusType type;
        Throwable cause;
        long start;
        long end;

        AbstractStatusBuilder() {
            start = System.currentTimeMillis();
        }

        @SuppressWarnings("unchecked")
        T success() {
            this.type = StatusType.SUCCESS;
            return (T) this;
        }

        @SuppressWarnings("unchecked")
        T fail(Throwable cause) {
            this.type = StatusType.FAIL;
            this.cause = cause;
            return (T) this;
        }

        Status build() {
            end = System.currentTimeMillis();
            return new Status(type, cause);
        }

        Duration elapsed() {
            return Duration.ofMillis(end - start);
        }
    }

    static final class StageBuilder extends AbstractStatusBuilder<StageBuilder> {
        long version;

        StageBuilder version(long version) {
            this.version = version;
            return this;
        }
    }

    static final class StageWithStateBuilder extends AbstractStatusBuilder<StageWithStateBuilder> {
        HollowProducer.ReadState readState;
        long version;

        StageWithStateBuilder readState(HollowProducer.ReadState readState) {
            this.readState = readState;
            return version(readState.getVersion());
        }

        StageWithStateBuilder version(long version) {
            this.version = version;
            return this;
        }
    }

    static final class IncrementalPopulateBuilder extends AbstractStatusBuilder<PublishBuilder> {
        long version;
        long removed;
        long addedOrModified;

        IncrementalPopulateBuilder version(long version) {
            this.version = version;
            return this;
        }

        IncrementalPopulateBuilder changes(long removed, long addedOrModified) {
            this.removed = removed;
            this.addedOrModified = addedOrModified;
            return this;
        }
    }

    static final class PublishBuilder extends AbstractStatusBuilder<PublishBuilder> {
        HollowProducer.Blob blob;

        PublishBuilder blob(HollowProducer.Blob blob) {
            this.blob = blob;
            return this;
        }
    }

    static final class PublishHeaderBuilder extends AbstractStatusBuilder<PublishHeaderBuilder> {
        HollowProducer.HeaderBlob headerBlob;

        PublishHeaderBuilder headerBlob(HollowProducer.HeaderBlob headerBlob) {
            this.headerBlob = headerBlob;
            return this;
        }
    }

    static final class RestoreStageBuilder extends AbstractStatusBuilder<RestoreStageBuilder> {
        long versionDesired;
        long versionReached;

        RestoreStageBuilder versions(long versionDesired, long versionReached) {
            this.versionDesired = versionDesired;
            this.versionReached = versionReached;
            return this;
        }
    }
}

/*
 *
 *  Copyright 2017 Netflix, Inc.
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

import static com.netflix.hollow.api.producer.HollowProducerListener.Status.FAIL;
import static com.netflix.hollow.api.producer.HollowProducerListener.Status.SUCCESS;

import com.netflix.hollow.api.producer.HollowProducer.ReadState;
import com.netflix.hollow.api.producer.HollowProducer.WriteState;
import java.util.EventListener;
import java.util.concurrent.TimeUnit;

/**
 * Beta API subject to change.
 *
 * A class should implement {@code HollowProducerListener}, if it wants to be notified on start/completion of various stages of the {@link HollowProducer}.
 *
 * @author Kinesh Satiya {@literal kineshsatiya@gmail.com}.
 */
public interface HollowProducerListener extends EventListener {

    /**
     * Called after the {@code HollowProducer} has initialized its data model.
     */
    public void onProducerInit(long elapsed, TimeUnit unit);

    /**
     * Called after the {@code HollowProducer} has restored its data state to the indicated version.
     * If previous state is not available to restore from, then this callback will not be called.
     *
     * @param restoreVersion Version from which the state for {@code HollowProducer} was restored.
     */
    public void onProducerRestoreStart(long restoreVersion);

    /**
     * Called after the {@code HollowProducer} has restored its data state to the indicated version.
     * If previous state is not available to restore from, then this callback will not be called.
     *
     * @param status of the restore. {@link RestoreStatus#getStatus()} will return {@code SUCCESS} when
     *   the desired version was reached during restore, otheriwse {@code FAIL} will be returned.
     * @param elapsed duration of the restore in {@code unit} units
     * @param unit units of the {@code elapsed} duration
     */
    public void onProducerRestoreComplete(RestoreStatus status, long elapsed, TimeUnit unit);

    /**
     * Indicates that the next state produced will begin a new delta chain.
     * This will be called prior to the next state being produced either if
     * {@link HollowProducer#restore(long, com.netflix.hollow.api.consumer.HollowConsumer.BlobRetriever)}
     * hasn't been called or the restore failed.
     *
     * @param version the version of the state that will become the first of a new delta chain
     */
    public void onNewDeltaChain(long version);

    /**
     * Called when the {@code HollowProducer} has begun a new cycle.
     *
     * @param version Version produced by the {@code HollowProducer} for new cycle about to start.
     */
    public void onCycleStart(long version);

    /**
     * Called after {@code HollowProducer} has completed a cycle normally or abnormally. A {@code SUCCESS} status indicates that the
     * entire cycle was successful and the producer is available to begin another cycle.
     *
     * @param status ProducerStatus of this cycle. {@link ProducerStatus#getStatus()} will return {@code SUCCESS}
     *   when the a new data state has been announced to consumers or when there were no changes to the data; it will return @{code FAIL}
     *   when any stage failed or any other failure occured during the cycle.
     * @param elapsed duration of the cycle in {@code unit} units
     * @param unit units of the {@code elapsed} duration
     */
    public void onCycleComplete(ProducerStatus status, long elapsed, TimeUnit unit);

    /**
     * Called after the new state has been populated if the {@code HollowProducer} detects that no data has changed, thus no snapshot nor delta should be produced.<p>
     *
     * @param version Current version of the cycle.
     */
    public void onNoDeltaAvailable(long version);

    /**
     * Called before starting to execute the task to populate data into Hollow.
     *
     * @param version
     */
    public void onPopulateStart(long version);

    /**
     * Called once populating task stage has finished successfully or failed. Use {@code ProducerStatus#getStatus()} to get status of the task.
     *
     * @param status A value of {@code Success} indicates that all data was successfully populated. {@code Fail} status indicates populating hollow with data failed.
     * @param elapsed Time taken to populate hollow.
     * @param unit unit of {@code elapsed} duration.
     */
    public void onPopulateComplete(ProducerStatus status, long elapsed, TimeUnit unit);

    /**
     * Called when the {@code HollowProducer} has begun publishing the {@code HollowBlob}s produced this cycle.
     *
     * @param version Version to be published.
     */
    public void onPublishStart(long version);

    /**
     * Called after the publish stage finishes normally or abnormally. A {@code SUCCESS} status indicates that
     * the {@code HollowBlob}s produced this cycle has been published to the blob store.
     *
     * @param status CycleStatus of the publish stage. {@link ProducerStatus#getStatus()} will return {@code SUCCESS}
     *   when the publish was successful; @{code FAIL} otherwise.
     * @param elapsed duration of the publish stage in {@code unit} units
     * @param unit units of the {@code elapsed} duration
     */
    public void onPublishComplete(ProducerStatus status, long elapsed, TimeUnit unit);

    /**
     * Called once a blob has been published successfully or failed to published. Use {@link PublishStatus#getBlob()} to get more details on blob type and size.
     * This method is called for every {@link com.netflix.hollow.api.producer.HollowProducer.Blob.Type} that was published.
     *
     * @param publishStatus Status of publishing. {@link PublishStatus#getStatus()} returns {@code SUCCESS} or {@code FAIL}.
     * @param elapsed       time taken to publish the blob
     * @param unit          unit of elapsed.
     */
    public void onArtifactPublish(PublishStatus publishStatus, long elapsed, TimeUnit unit);

    /**
     * Called when the {@code HollowProducer} has begun checking the integrity of the {@code HollowBlob}s produced this cycle.
     *
     * @param version Version to be checked
     */
    public void onIntegrityCheckStart(long version);

    /**
     * Called after the integrity check stage finishes normally or abnormally. A {@code SUCCESS} status indicates that
     * the previous snapshot, current snapshot, delta, and reverse-delta {@code HollowBlob}s are all internally consistent.
     *
     * @param status CycleStatus of the integrity check stage. {@link ProducerStatus#getStatus()} will return {@code SUCCESS}
     *   when the blobs are internally consistent; @{code FAIL} otherwise.
     * @param elapsed duration of the integrity check stage in {@code unit} units
     * @param unit units of the {@code elapsed} duration
     */
    public void onIntegrityCheckComplete(ProducerStatus status, long elapsed, TimeUnit unit);

    /**
     * Called when the {@code HollowProducer} has begun validating the new data state produced this cycle.
     *
     * @param version Version to be validated
     */
    public void onValidationStart(long version);

    /**
     * Called after the validation stage finishes normally or abnormally. A {@code SUCCESS} status indicates that
     * the newly published data state is considered valid.
     *
     * @param status CycleStatus of the publish stage. {@link ProducerStatus#getStatus()} will return {@code SUCCESS}
     *   when the publish was successful; @{code FAIL} otherwise.
     * @param elapsed duration of the validation stage in {@code unit} units
     * @param unit units of the {@code elapsed} duration
     */
    public void onValidationComplete(ProducerStatus status, long elapsed, TimeUnit unit);

    /**
     * Called when the {@code HollowProducer} has begun announcing the {@code HollowBlob} published this cycle.
     *
     * @param version of {@code HollowBlob} that will be announced.
     */
    public void onAnnouncementStart(long version);

    /**
     * Called after the announcement stage finishes normally or abnormally. A {@code SUCCESS} status indicates
     * that the {@code HollowBlob} published this cycle has been announced to consumers.
     *
     * @param status CycleStatus of the announcement stage. {@link ProducerStatus#getStatus()} will return {@code SUCCESS}
     *   when the announce was successful; @{code FAIL} otherwise.
     * @param elapsed duration of the announcement stage in {@code unit} units
     * @param unit units of the {@code elapsed} duration
     */
    public void onAnnouncementComplete(ProducerStatus status, long elapsed, TimeUnit unit);

    /**
     * This class represents information on details when {@link HollowProducer} has finished executing a particular stage.
     * An instance of this class is provided on different events of {@link HollowProducerListener}.
     *
     * @author Kinesh Satiya {@literal kineshsatiya@gmail.com}
     */
    public class ProducerStatus {

        private final long version;
        private final Status status;
        private final Throwable throwable;
        private final HollowProducer.ReadState readState;


        static ProducerStatus success(long version) {
            return new ProducerStatus(Status.SUCCESS, version, null, null);
        }

        static ProducerStatus success(HollowProducer.ReadState readState) {
            return new ProducerStatus(Status.SUCCESS, readState.getVersion(), readState, null);
        }

        static ProducerStatus unknownFailure() {
            return new ProducerStatus(Status.FAIL, Long.MIN_VALUE, null, null);
        }

        static ProducerStatus fail(long version) {
            return new ProducerStatus(Status.FAIL, version, null, null);
        }

        static ProducerStatus fail(long version, Throwable th) {
            return new ProducerStatus(Status.FAIL, version, null, th);
        }

        static ProducerStatus fail(HollowProducer.ReadState readState, Throwable th) {
            return new ProducerStatus(Status.FAIL, readState.getVersion(), readState, th);
        }

        private ProducerStatus(Status status, long version, HollowProducer.ReadState readState, Throwable throwable) {
            this.status = status;
            this.version = version;
            this.readState = readState;
            this.throwable = throwable;
        }

        /**
         * This version is currently under process by {@code HollowProducer}.
         *
         * @return Current version of the {@code HollowProducer}.
         */
        public long getVersion() {
            return version;
        }

        /**
         * Status of the latest stage completed by {@code HollowProducer}.
         *
         * @return SUCCESS or FAIL.
         */
        public Status getStatus() {
            return status;
        }

        /**
         * Returns the failure cause if this status represents a {@code HollowProducer} failure that was caused by an exception.
         *
         * @return Throwable if {@code Status.equals(FAIL)} else null.
         */
        public Throwable getCause() {
            return throwable;
        }

        /**
         * Returns the resulting read state engine after adding new data into write state engine.
         *
         * @return Resulting read state engine only if data is added successfully else null.
         */
        public ReadState getReadState() {
            return readState;
        }

        public static final class Builder {
            private final long start;
            private long end;

            private long version = Long.MIN_VALUE;
            private Status status = FAIL;
            private Throwable cause = null;
            private ReadState readState = null;

            Builder() {
                start = System.currentTimeMillis();
            }

            ProducerStatus.Builder version(long version) {
                this.version = version;
                return this;
            }

            ProducerStatus.Builder version(WriteState writeState) {
                return version(writeState.getVersion());
            }

            ProducerStatus.Builder version(ReadState readState) {
                this.readState = readState;
                return version(readState.getVersion());
            }

            ProducerStatus.Builder success() {
                this.status = SUCCESS;
                return this;
            }

            ProducerStatus.Builder fail() {
                return this;
            }

            ProducerStatus.Builder fail(Throwable cause) {
                this.status = FAIL;
                this.cause = cause;
                return this;
            }

            ProducerStatus build() {
                end = System.currentTimeMillis();
                return new ProducerStatus(status, version, readState, cause);
            }

            long elapsed() {
                return end - start;
            }

            long version() {
                return version;
            }
        }
    }

    /**
     * This class represents information on details when {@link HollowProducer} has finished executing a particular stage.
     * An instance of this class is provided on different events of {@link HollowProducerListener}.
     *
     * @author Tim Taylor {@literal tim@toolbear.io}
     */
    public class RestoreStatus {
        private final Status status;
        private final long versionDesired;
        private final long versionReached;
        private final Throwable throwable;

        static RestoreStatus success(long versionDesired, long versionReached) {
            return new RestoreStatus(Status.SUCCESS, versionDesired, versionReached, null);
        }

        static RestoreStatus unknownFailure() {
            return new RestoreStatus(Status.FAIL, Long.MIN_VALUE, Long.MIN_VALUE, null);
        }

        static RestoreStatus fail(long versionDesired, long versionReached, Throwable cause) {
            return new RestoreStatus(Status.FAIL, versionDesired, versionReached, cause);
        }

        private RestoreStatus(Status status, long versionDesired, long versionReached, Throwable throwable) {
            this.status = status;
            this.versionDesired = versionDesired;
            this.versionReached = versionReached;
            this.throwable = throwable;
        }

        /**
         * The version desired to restore to when calling
         * {@link HollowProducer#restore(long, com.netflix.hollow.api.consumer.HollowConsumer.BlobRetriever)}
         *
         * @return the latest announced version or {@code Long.MIN_VALUE} if latest announced version couldn't be
         * retrieved
         */
        public long getDesiredVersion() {
            return versionDesired;
        }

        /**
         * The version reached when restoring.
         * When {@link HollowProducer#restore(long, com.netflix.hollow.api.consumer.HollowConsumer.BlobRetriever)}
         * succeeds then {@code versionDesired == versionReached} is always true. Can be {@code Long.MIN_VALUE}
         * indicating restore failed to reach any state, or the version of an intermediate state reached.
         *
         * @return the version restored to when successful, otherwise {@code Long.MIN_VALUE} if no version was
         * reached or the version of an intermediate state reached before restore completed unsuccessfully.
         */
        public long getVersionReached() {
            return versionReached;
        }

        /**
         * Status of the restore
         *
         * @return SUCCESS or FAIL.
         */
        public Status getStatus() {
            return status;
        }

        /**
         * Returns the failure cause if this status represents a {@code HollowProducer} failure that was caused by an exception.
         *
         * @return the {@code Throwable} cause of a failure, otherwise {@code null} if restore succeeded or it failed
         * without an exception.
         */
        public Throwable getCause() {
            return throwable;
        }
    }

    public class PublishStatus {
        private final Status status;
        private final HollowProducer.Blob blob;
        private final Throwable throwable;

        private PublishStatus(Status status, HollowProducer.Blob blob, Throwable throwable) {
            this.status = status;
            this.blob = blob;
            this.throwable = throwable;
        }

        static class Builder {
            private final long start;
            private long elapsed;
            private Status status;
            private HollowProducer.Blob blob;
            private Throwable throwable;

            Builder() {
                this.start = System.currentTimeMillis();
            }

            PublishStatus.Builder blob(final HollowProducer.Blob blob) {
                this.blob = blob;
                return this;
            }

            PublishStatus.Builder success() {
                this.status = SUCCESS;
                return this;
            }

            PublishStatus.Builder fail(Throwable throwable) {
                this.status = FAIL;
                this.throwable = throwable;
                return this;
            }

            long elapsed() {
                return elapsed;
            }

            PublishStatus build() {
                this.elapsed = System.currentTimeMillis() - start;
                return new PublishStatus(status, blob, throwable);
            }

        }

        /**
         * Status to indicate if publishing was successful or failed.
         *
         * @return {@code Success} or {@code Fail}
         */
        public Status getStatus() {
            return status;
        }

        /**
         * An instance of {@code Blob} has methods to get details on type of blob, size, from and to version.
         *
         * @return Blob that was published.
         */
        public HollowProducer.Blob getBlob() {
            return blob;
        }

        /**
         * @return Throwable that contains the error cause if publishing failed.
         */
        public Throwable getCause() {
            return throwable;
        }

    }

    public enum Status {
        SUCCESS, FAIL, SKIP
    }
}

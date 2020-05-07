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

import com.netflix.hollow.api.producer.HollowProducer.ReadState;
import com.netflix.hollow.api.producer.listener.AnnouncementListener;
import com.netflix.hollow.api.producer.listener.CycleListener;
import com.netflix.hollow.api.producer.listener.DataModelInitializationListener;
import com.netflix.hollow.api.producer.listener.IntegrityCheckListener;
import com.netflix.hollow.api.producer.listener.PopulateListener;
import com.netflix.hollow.api.producer.listener.PublishListener;
import com.netflix.hollow.api.producer.listener.RestoreListener;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * A class should implement {@code HollowProducerListener}, if it wants to be notified on
 * start/completion of various stages of the {@link HollowProducer}.
 * Subclasses are encouraged to extend {@link AbstractHollowProducerListener}
 *
 * @author Kinesh Satiya {@literal kineshsatiya@gmail.com}.
 */
public interface HollowProducerListener extends
        DataModelInitializationListener,
        RestoreListener,
        CycleListener,
        PopulateListener,
        PublishListener,
        IntegrityCheckListener,
        AnnouncementListener {


    ///////////////////////////
    // DataModelInitializationListener
    ///////////////////////////

    // Called when HollowProducer.initializeDataModel is called

    @Override
    default void onProducerInit(Duration elapsed) {
        onProducerInit(elapsed.toMillis(), TimeUnit.MILLISECONDS);
    }

    /**
     * Called after the {@code HollowProducer} has initialized its data model.
     * @param elapsed the elapsed duration
     * @param unit the units of duration
     */
    void onProducerInit(long elapsed, TimeUnit unit);


    ///////////////////////////
    // RestoreListener
    ///////////////////////////

    /**
     * Called after the {@code HollowProducer} has restored its data state to the indicated version.
     * If previous state is not available to restore from, then this callback will not be called.
     *
     * @param restoreVersion Version from which the state for {@code HollowProducer} was restored.
     */
    @Override
    void onProducerRestoreStart(long restoreVersion);

    @Override
    default void onProducerRestoreComplete(com.netflix.hollow.api.producer.Status status, long versionDesired, long versionReached, Duration elapsed) {
        onProducerRestoreComplete(new RestoreStatus(status, versionDesired, versionReached),
                elapsed.toMillis(), TimeUnit.MILLISECONDS);
    }

    /**
     * Called after the {@code HollowProducer} has restored its data state to the indicated version.
     * If previous state is not available to restore from, then this callback will not be called.
     *
     * @param status of the restore. {@link RestoreStatus#getStatus()} will return {@code SUCCESS} when
     * the desired version was reached during restore, otheriwse {@code FAIL} will be returned.
     * @param elapsed duration of the restore in {@code unit} units
     * @param unit units of the {@code elapsed} duration
     */
    void onProducerRestoreComplete(RestoreStatus status, long elapsed, TimeUnit unit);


    ///////////////////////////
    // CycleListener
    ///////////////////////////

    // See also HollowProducerListenerV2.onCycleSkip

    @Override
    default void onCycleSkip(CycleSkipReason reason) {
    }

    // This is called just before onCycleStart, can the two be merged with additional arguments

    /**
     * Indicates that the next state produced will begin a new delta chain.
     * This will be called prior to the next state being produced either if
     * {@link HollowProducer#restore(long, com.netflix.hollow.api.consumer.HollowConsumer.BlobRetriever)}
     * hasn't been called or the restore failed.
     *
     * @param version the version of the state that will become the first of a new delta chain
     */
    @Override
    void onNewDeltaChain(long version);

    /**
     * Called when the {@code HollowProducer} has begun a new cycle.
     *
     * @param version Version produced by the {@code HollowProducer} for new cycle about to start.
     */
    @Override
    void onCycleStart(long version);

    @Override
    default void onCycleComplete(com.netflix.hollow.api.producer.Status status, ReadState readState, long version, Duration elapsed) {
        onCycleComplete(new ProducerStatus(status, readState, version),
                elapsed.toMillis(), TimeUnit.MILLISECONDS);
    }

    /**
     * Called after {@code HollowProducer} has completed a cycle normally or abnormally. A {@code SUCCESS} status indicates that the
     * entire cycle was successful and the producer is available to begin another cycle.
     *
     * @param status ProducerStatus of this cycle. {@link ProducerStatus#getStatus()} will return {@code SUCCESS}
     * when the a new data state has been announced to consumers or when there were no changes to the data; it will return @{code FAIL}
     * when any stage failed or any other failure occured during the cycle.
     * @param elapsed duration of the cycle in {@code unit} units
     * @param unit units of the {@code elapsed} duration
     */
    void onCycleComplete(ProducerStatus status, long elapsed, TimeUnit unit);


    ///////////////////////////
    // PopulateListener
    ///////////////////////////

    /**
     * Called before starting to execute the task to populate data into Hollow.
     *
     * @param version Current version of the cycle
     */
    @Override
    void onPopulateStart(long version);

    @Override
    default void onPopulateComplete(com.netflix.hollow.api.producer.Status status, long version, Duration elapsed) {
        onPopulateComplete(new ProducerStatus(status, version),
                elapsed.toMillis(), TimeUnit.MILLISECONDS);
    }

    /**
     * Called once populating task stage has finished successfully or failed. Use {@code ProducerStatus#getStatus()} to deserializeFrom status of the task.
     *
     * @param status A value of {@code Success} indicates that all data was successfully populated. {@code Fail} status indicates populating hollow with data failed.
     * @param elapsed Time taken to populate hollow.
     * @param unit unit of {@code elapsed} duration.
     */
    void onPopulateComplete(ProducerStatus status, long elapsed, TimeUnit unit);


    ///////////////////////////
    // PublishListener
    ///////////////////////////

    // Called after populate

    // Called after populateComplete and instead of publish
    // Can be merged in to PublishListener?

    /**
     * Called after the new state has been populated if the {@code HollowProducer} detects that no data has changed, thus no snapshot nor delta should be produced.<p>
     *
     * @param version Current version of the cycle.
     */
    @Override
    void onNoDeltaAvailable(long version);

    /**
     * Called when the {@code HollowProducer} has begun publishing the {@code HollowBlob}s produced this cycle.
     *
     * @param version Version to be published.
     */
    @Override
    void onPublishStart(long version);

    // Called during publish start-complete cycle for each blob
    // Can be merged in to PublishListener?

    @Override
    default void onBlobPublish(com.netflix.hollow.api.producer.Status status, HollowProducer.Blob blob, Duration elapsed) {
        onArtifactPublish(new PublishStatus(status, blob),
                elapsed.toMillis(), TimeUnit.MILLISECONDS);
    }

    /**
     * Called once a blob has been published successfully or failed to published. Use {@link PublishStatus#getBlob()} to deserializeFrom more details on blob type and size.
     * This method is called for every {@link com.netflix.hollow.api.producer.HollowProducer.Blob.Type} that was published.
     *
     * @param publishStatus Status of publishing. {@link PublishStatus#getStatus()} returns {@code SUCCESS} or {@code FAIL}.
     * @param elapsed time taken to publish the blob
     * @param unit unit of elapsed.
     */
    // TODO(hollow3): "artifact" as a term is redundant with "blob", probably don't need both. #onBlobPublish(...)?
    void onArtifactPublish(PublishStatus publishStatus, long elapsed, TimeUnit unit);

    @Override
    default void onPublishComplete(com.netflix.hollow.api.producer.Status status, long version, Duration elapsed) {
        onPublishComplete(new ProducerStatus(status, version), elapsed.toMillis(), TimeUnit.MILLISECONDS);
    }

    /**
     * Called after the publish stage finishes normally or abnormally. A {@code SUCCESS} status indicates that
     * the {@code HollowBlob}s produced this cycle has been published to the blob store.
     *
     * @param status CycleStatus of the publish stage. {@link ProducerStatus#getStatus()} will return {@code SUCCESS}
     * when the publish was successful; @{code FAIL} otherwise.
     * @param elapsed duration of the publish stage in {@code unit} units
     * @param unit units of the {@code elapsed} duration
     */
    void onPublishComplete(ProducerStatus status, long elapsed, TimeUnit unit);


    ///////////////////////////
    // IntegrityCheckListener
    ///////////////////////////

    // This is effectively a validator that is executed before any other validators
    // Called after publish start-complete cycle

    /**
     * Called when the {@code HollowProducer} has begun checking the integrity of the {@code HollowBlob}s produced this cycle.
     *
     * @param version Version to be checked
     */
    @Override
    void onIntegrityCheckStart(long version);

    @Override
    default void onIntegrityCheckComplete(com.netflix.hollow.api.producer.Status status, ReadState readState, long version, Duration elapsed) {
        onIntegrityCheckComplete(new ProducerStatus(status, readState, version),
                elapsed.toMillis(), TimeUnit.MILLISECONDS);
    }

    /**
     * Called after the integrity check stage finishes normally or abnormally. A {@code SUCCESS} status indicates that
     * the previous snapshot, current snapshot, delta, and reverse-delta {@code HollowBlob}s are all internally consistent.
     *
     * @param status CycleStatus of the integrity check stage. {@link ProducerStatus#getStatus()} will return {@code SUCCESS}
     * when the blobs are internally consistent; @{code FAIL} otherwise.
     * @param elapsed duration of the integrity check stage in {@code unit} units
     * @param unit units of the {@code elapsed} duration
     */
    void onIntegrityCheckComplete(ProducerStatus status, long elapsed, TimeUnit unit);


    ///////////////////////////
    ///////////////////////////

    // See also HollowValidationListener (deprecated) and Validators.ValidationStatusListener

    // Called after integrity check cycle
    // Validators will be called during the validation start-complete cycle

    /**
     * Called when the {@code HollowProducer} has begun validating the new data state produced this cycle.
     *
     * @param version Version to be validated
     */
    void onValidationStart(long version);

    /**
     * Called after the validation stage finishes normally or abnormally. A {@code SUCCESS} status indicates that
     * the newly published data state is considered valid.
     *
     * @param status CycleStatus of the publish stage. {@link ProducerStatus#getStatus()} will return {@code SUCCESS}
     * when the publish was successful; @{code FAIL} otherwise.
     * @param elapsed duration of the validation stage in {@code unit} units
     * @param unit units of the {@code elapsed} duration
     */
    void onValidationComplete(ProducerStatus status, long elapsed, TimeUnit unit);


    ///////////////////////////
    // AnnouncementListener
    ///////////////////////////

    // Called after a successful validation start-complete cycle

    /**
     * Called when the {@code HollowProducer} has begun announcing the {@code HollowBlob} published this cycle.
     *
     * @param version of {@code HollowBlob} that will be announced.
     */
    @Override
    void onAnnouncementStart(long version);

    @Override
    default void onAnnouncementComplete(com.netflix.hollow.api.producer.Status status, ReadState readState, long version, Duration elapsed) {
        onAnnouncementComplete(new ProducerStatus(status, readState, version),
                elapsed.toMillis(), TimeUnit.MILLISECONDS);
    }

    /**
     * Called after the announcement stage finishes normally or abnormally. A {@code SUCCESS} status indicates
     * that the {@code HollowBlob} published this cycle has been announced to consumers.
     *
     * @param status CycleStatus of the announcement stage. {@link ProducerStatus#getStatus()} will return {@code SUCCESS}
     * when the announce was successful; @{code FAIL} otherwise.
     * @param elapsed duration of the announcement stage in {@code unit} units
     * @param unit units of the {@code elapsed} duration
     */
    void onAnnouncementComplete(ProducerStatus status, long elapsed, TimeUnit unit);


    /**
     * This class represents information on details when {@link HollowProducer} has finished executing a particular stage.
     * An instance of this class is provided on different events of {@link HollowProducerListener}.
     *
     * @author Kinesh Satiya {@literal kineshsatiya@gmail.com}
     */
    class ProducerStatus {
        private final long version;
        private final Status status;
        private final Throwable throwable;
        private final HollowProducer.ReadState readState;

        ProducerStatus(com.netflix.hollow.api.producer.Status s, long version) {
            this(s, null, version);
        }

        ProducerStatus(com.netflix.hollow.api.producer.Status s, ReadState readState, long version) {
            this.status = Status.of(s.getType());
            this.throwable = s.getCause();
            this.readState = readState;
            this.version = version;
        }

        ProducerStatus(Status status, Throwable throwable, long version, HollowProducer.ReadState readState) {
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
    }

    /**
     * This class represents information on details when {@link HollowProducer} has finished executing a particular stage.
     * An instance of this class is provided on different events of {@link HollowProducerListener}.
     *
     * @author Tim Taylor {@literal tim@toolbear.io}
     */
    class RestoreStatus {
        private final Status status;
        private final long versionDesired;
        private final long versionReached;
        private final Throwable throwable;

        RestoreStatus(com.netflix.hollow.api.producer.Status s, long versionDesired, long versionReached) {
            this.status = Status.of(s.getType());
            this.throwable = s.getCause();
            this.versionDesired = versionDesired;
            this.versionReached = versionReached;
        }

        /**
         * The version desired to restore to when calling
         * {@link HollowProducer#restore(long, com.netflix.hollow.api.consumer.HollowConsumer.BlobRetriever)}
         *
         * @return the latest announced version or {@code HollowConstants.VERSION_NONE} if latest announced version couldn't be
         * retrieved
         */
        public long getDesiredVersion() {
            return versionDesired;
        }

        /**
         * The version reached when restoring.
         * When {@link HollowProducer#restore(long, com.netflix.hollow.api.consumer.HollowConsumer.BlobRetriever)}
         * succeeds then {@code versionDesired == versionReached} is always true. Can be {@code HollowConstants.VERSION_NONE}
         * indicating restore failed to reach any state, or the version of an intermediate state reached.
         *
         * @return the version restored to when successful, otherwise {@code HollowConstants.VERSION_NONE} if no version was
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

    class PublishStatus {
        private final Status status;
        private final HollowProducer.Blob blob;
        private final Throwable throwable;

        PublishStatus(com.netflix.hollow.api.producer.Status s, HollowProducer.Blob blob) {
            this.status = Status.of(s.getType());
            this.throwable = s.getCause();
            this.blob = blob;
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
         * An instance of {@code Blob} has methods to deserializeFrom details on type of blob, size, from and to version.
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

    enum Status {
        SUCCESS, FAIL,
        // This is currently only used to report skipping of a validator
        // with SingleValidationStatusBuilder and SingleValidationStatus
        @Deprecated
        SKIP;

        static Status of(com.netflix.hollow.api.producer.Status.StatusType st) {
            return st == com.netflix.hollow.api.producer.Status.StatusType.SUCCESS
                    ? Status.SUCCESS
                    : Status.FAIL;
        }

        static com.netflix.hollow.api.producer.Status.StatusType from(Status s) {
            return s == Status.SUCCESS
                    ? com.netflix.hollow.api.producer.Status.StatusType.SUCCESS
                    : com.netflix.hollow.api.producer.Status.StatusType.FAIL;
        }
    }
}
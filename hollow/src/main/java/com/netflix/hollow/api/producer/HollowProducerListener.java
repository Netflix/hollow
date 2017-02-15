package com.netflix.hollow.api.producer;

import java.util.EventListener;

import com.netflix.hollow.api.HollowStateTransition;
import com.netflix.hollow.api.consumer.HollowConsumer;

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
    public void onProducerInit();

    /**
     * Called after the {@code HollowProducer} has restored its data state to the indicated version.
     * If previous state is not available to restore from, then this callback will not be called.
     *
     * @param restoreVersion Version from which the state for {@code HollowProducer} was restored.
     */
    public void onProducerRestore(long restoreVersion);

    /**
     * Called when the {@code HollowProducer} has begun a new cycle.
     *
     * @param version Version produced by the {@code HollowProducer} for new cycle about to start.
     */
    public void onCycleStart(long version);

    /**
     * Called after the new state has been populated if the {@code HollowProducer} detects that no data has changed, thus no snapshot nor delta should be produced.<p>
     *
     * This is a terminal cycle stage; no other stages notifications will be sent for this cycle; the {@link #onCycleComplete(CycleStatus)} will be
     * notified with @{code SUCCESS}.
     *
     * @param version Current version of the cycle.
     */
    public void onNoDeltaAvailable(long version);

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
     * @param status CycleStatus of the publish stage. {@link com.netflix.hollow.api.producer.CycleStatus#getStatus()} will return {@code SUCCESS}
     *   when the publish was successful; @{code FAIL} otherwise.
     */
    public void onPublishComplete(ProducerStatus status);

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
     * @param status CycleStatus of the integrity check stage. {@link com.netflix.hollow.api.producer.CycleStatus#getStatus()} will return {@code SUCCESS}
     *   when the blobs are internally consistent; @{code FAIL} otherwise.
     */
    public void onIntegrityCheckComplete(ProducerStatus status);

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
     * @param status CycleStatus of the publish stage. {@link com.netflix.hollow.api.producer.CycleStatus#getStatus()} will return {@code SUCCESS}
     *   when the publish was successful; @{code FAIL} otherwise.
     */
    public void onValidationComplete(ProducerStatus status);

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
     * @param status CycleStatus of the announcement stage. {@link com.netflix.hollow.api.producer.CycleStatus#getStatus()} will return {@code SUCCESS}
     *   when the announce was successful; @{code FAIL} otherwise.
     */
    public void onAnnouncementComplete(ProducerStatus status);

    /**
     * Called after {@code HollowProducer} has completed a cycle normally or abnormally. A {@code SUCCESS} status indicates that the
     * entire cycle was successful and the producer is available to begin another cycle.
     *
     * @param status CycleStatus of this cycle. {@link com.netflix.hollow.api.producer.CycleStatus#getStatus()} will return {@code SUCCESS}
     *   when the a new data state has been announced to consumers or when there were no changes to the data; it will return @{code FAIL}
     *   when any stage failed or any other failure occured during the cycle.
     */
    public void onCycleComplete(ProducerStatus status);

    /**
     * Beta API subject to change.
     *
     * This class represents information on details when {@link HollowProducer} has finished executing a particular stage.
     * An instance of this class is provided on different events of {@link HollowProducerListener}.
     *
     * @author Kinesh Satiya {@literal kineshsatiya@gmail.com}
     */
    public class ProducerStatus {

        public enum Status {
            SUCCESS, FAIL
        }

        private final long version;
        private final Status status;
        private final Throwable throwable;
        private final HollowConsumer.ReadState readState;

        static ProducerStatus success(HollowStateTransition transition) {
            return success(transition, null);
        }

        static ProducerStatus success(HollowStateTransition transition, HollowConsumer.ReadState readState) {
            return success(transition.getToVersion(), readState);
        }

        static ProducerStatus success(long version, HollowConsumer.ReadState readState) {
            return new ProducerStatus(version, Status.SUCCESS, null, readState);
        }

        static ProducerStatus unknownFailure() {
            return fail(Long.MIN_VALUE, null);
        }

        static ProducerStatus fail(HollowStateTransition transition, Throwable th) {
            return fail(transition.getToVersion(), th);
        }

        static ProducerStatus fail(long version, Throwable th) {
            return new ProducerStatus(version, Status.FAIL, th, null);
        }

        private ProducerStatus(long version, Status status, Throwable throwable, HollowConsumer.ReadState readState) {
            this.version = version;
            this.status = status;
            this.throwable = throwable;
            this.readState = readState;
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
        public Throwable getThrowable() {
            return throwable;
        }

        /**
         * Returns the resulting read state engine after adding new data into write state engine.
         *
         * @return Resulting read state engine only if data is added successfully else null.
         */
        public HollowConsumer.ReadState getReadState() {
            return readState;
        }

    }
}
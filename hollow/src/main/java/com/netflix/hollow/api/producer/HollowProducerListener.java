package com.netflix.hollow.api.producer;

import java.util.EventListener;
import java.util.concurrent.TimeUnit;

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
    public void onProducerInit(long elapsed, TimeUnit unit);

    /**
     * Called after the {@code HollowProducer} has restored its data state to the indicated version.
     * If previous state is not available to restore from, then this callback will not be called.
     *
     * @param restoreVersion Version from which the state for {@code HollowProducer} was restored.
     */
    public void onProducerRestore(long restoreVersion, long elapsed, TimeUnit unit);

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
     * @param status CycleStatus of this cycle. {@link ProducerStatus#getStatus()} will return {@code SUCCESS}
     *   when the a new data state has been announced to consumers or when there were no changes to the data; it will return @{code FAIL}
     *   when any stage failed or any other failure occured during the cycle.
     */
    public void onCycleComplete(ProducerStatus status, long elapsed, TimeUnit unit);

    /**
     * Called after the new state has been populated if the {@code HollowProducer} detects that no data has changed, thus no snapshot nor delta should be produced.<p>
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
     * @param status CycleStatus of the publish stage. {@link ProducerStatus#getStatus()} will return {@code SUCCESS}
     *   when the publish was successful; @{code FAIL} otherwise.
     */
    public void onPublishComplete(ProducerStatus status, long elapsed, TimeUnit unit);

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
     */
    public void onAnnouncementComplete(ProducerStatus status, long elapsed, TimeUnit unit);

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


        static ProducerStatus success(long version) {
            return new ProducerStatus(Status.SUCCESS, version, null, null);
        }

        static ProducerStatus success(HollowConsumer.ReadState readState) {
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

        static ProducerStatus fail(HollowConsumer.ReadState readState, Throwable th) {
            return new ProducerStatus(Status.FAIL, readState.getVersion(), readState, th);
        }

        private ProducerStatus(Status status, long version, HollowConsumer.ReadState readState, Throwable throwable) {
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
        public HollowConsumer.ReadState getReadState() {
            return readState;
        }

    }
}
package com.netflix.hollow.api.producer;

/**
 * Beta API subject to change.
 *
 * A class should implement {@code HollowProducerListener}, if it wants to be notified on start/completion of various stages of the {@link HollowProducer}.
 *
 * @author Kinesh Satiya {@literal kineshsatiya@gmail.com}.
 */
public interface HollowProducerListener {

    /**
     * This method is called once {@code HollowProducer} has been initialized with the expected schemas.
     */
    public void onProducerInit();

    /**
     * This method is called once {@code HollowProducer} has restored from previously produced state.
     * If previous state is not available or its the first state, then this callback will not be called.
     *
     * @param restoreVersion Version from which the state for {@code HollowProducer} was restored.
     */
    public void onProducerRestore(long restoreVersion);

    /**
     * This method is called before starting to populating the data into {@code HollowWriteStateEngine}.
     *
     * @param version Version produced by the {@code HollowProducer} for new cycle about to start.
     */
    public void onCycleStart(long version);

    /**
     * This method is called when there is no change observed in write state engine, in the current cycle. This would also mean, no delta can be produced for publishing.
     *
     * @param version Current version of the cycle.
     */
    public void onNoDeltaAvailable(long version);

    /**
     * This method is called before starting to published the {@code HollowBlob} produced by the {@code HollowProducer}.
     *
     * @param version Version to be published.
     */
    public void onPublishStart(long version);

    /**
     * This method is called upon successful publishing of the resulting {@code HollowBlob} produced by the {@code HollowProducer.}
     *
     * @param status CycleStatus of the publish stage.
     */
    public void onPublishComplete(CycleStatus status);

    /**
     * This method is called before announcing the availability of new {@code HollowBlob}.
     *
     * @param version of {@code HollowBlob} that was announced.
     */
    public void onAnnouncementStart(long version);

    /**
     * This method is called when availability of new {@code HollowBlob} is announced.
     *
     * @param status CycleStatus of the announcement stage.
     */
    public void onAnnouncementComplete(CycleStatus status);

    /**
     * This method is called when {@code HollowProducer} has successfully completed a cycle.
     *
     * @param status CycleStatus when cycle completed.
     */
    public void onCycleComplete(CycleStatus status);

}
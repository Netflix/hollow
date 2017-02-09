package com.netflix.hollow.api.producer;

import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

/**
 * This class represents information on details when {@link HollowProducer} has finished executing a particular stage.
 * An instance of this class is provided on different events of {@link HollowProducerListener}.
 *
 * @author Kinesh Satiya {@literal kineshsatiya@gmail.com}
 */
public class CycleStatus {

    public enum Status {
        SUCCESS, FAIL
    }

    private long version;
    private Status status;
    private Throwable throwable;
    private HollowReadStateEngine readStateEngine;

    public static CycleStatus getSuccessInstance(long version) {
        return new CycleStatus(version, Status.SUCCESS, null, null);
    }

    public static CycleStatus getSuccessInstance(long version, HollowReadStateEngine readStateEngine) {
        return new CycleStatus(version, Status.SUCCESS, null, readStateEngine);
    }

    public static CycleStatus getFailInstance(long version, Throwable th) {
        return new CycleStatus(version, Status.FAIL, th, null);
    }

    CycleStatus(long version, Status status, Throwable throwable, HollowReadStateEngine readStateEngine) {
        this.version = version;
        this.status = status;
        this.throwable = throwable;
        this.readStateEngine = readStateEngine;
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
     * This method returns the exception if the latest state completed by {@code HollowProducer} failed because of an exception.
     *
     * @return Throwable if {@code Status.equals(FAIL)} else null.
     */
    public Throwable getThrowable() {
        return throwable;
    }

    /**
     * This method returns the resulting read state engine after adding new data into write state engine.
     *
     * @return Resulting read state engine only if data is added successfully else null.
     */
    public HollowReadStateEngine getReadStateEngine() {
        return readStateEngine;
    }

}
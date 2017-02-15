/*
 *
 *  Copyright 2016 Netflix, Inc.
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

import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

/**
 * Beta API subject to change.
 *
 * This class represents information on details when {@link HollowProducer} has finished executing a particular stage.
 * An instance of this class is provided on different events of {@link HollowProducerListener}.
 *
 * @author Kinesh Satiya {@literal kineshsatiya@gmail.com}
 */
public class CycleStatus {

    public enum Status {
        SUCCESS, FAIL
    }

    private final HollowProducer producer;
    private long version;
    private Status status;
    private Throwable throwable;
    private HollowReadStateEngine readStateEngine;

    public static CycleStatus success(HollowProducer producer, long version) {
        return new CycleStatus(producer, version, Status.SUCCESS, null, null);
    }

    public static CycleStatus success(HollowProducer producer, long version, HollowReadStateEngine readStateEngine) {
        return new CycleStatus(producer, version, Status.SUCCESS, null, readStateEngine);
    }

    public static CycleStatus fail(HollowProducer producer, long version, Throwable th) {
        return new CycleStatus(producer, version, Status.FAIL, th, null);
    }

    CycleStatus(HollowProducer producer, long version, Status status, Throwable throwable, HollowReadStateEngine readStateEngine) {
        this.producer = producer;
        this.version = version;
        this.status = status;
        this.throwable = throwable;
        this.readStateEngine = readStateEngine;
    }

    /**
     * The producer whose status is being described.
     *
     * @return the producer
     */
    public HollowProducer getProducer() {
        return producer;
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
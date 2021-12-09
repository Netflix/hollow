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
package com.netflix.hollow.api.metrics;

import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.api.producer.HollowProducerListener;
import com.netflix.hollow.api.producer.Status;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import java.util.concurrent.atomic.AtomicInteger;

public class HollowProducerMetrics extends HollowMetrics {
    private int cyclesCompleted = 0;
    private int cyclesSucceeded = 0;
    private int cycleFailed = 0;
    // Snapshots can be published asynchronously resulting concurrent
    // access to the snapshot metrics
    private AtomicInteger snapshotsCompleted = new AtomicInteger();
    private AtomicInteger snapshotsFailed = new AtomicInteger();
    private int deltasCompleted = 0;
    private int deltasFailed = 0;
    private int reverseDeltasCompleted = 0;
    private int reverseDeltasFailed = 0;
    private int headersCompleted = 0;
    private int headersFailed = 0;

    /**
     * Updates the producer metrics:
     * cycles completed, version and type's footprint and ordinals.
     * @param producerStatus the producer status
     */
    public void updateCycleMetrics(HollowProducerListener.ProducerStatus producerStatus) {
        Status.StatusType st = producerStatus.getStatus() == HollowProducerListener.Status.SUCCESS
                ? Status.StatusType.SUCCESS
                : Status.StatusType.FAIL;

        updateCycleMetrics(new Status(st, producerStatus.getCause()), producerStatus.getReadState(), producerStatus.getVersion());
    }

    /**
     * Updates the producer metrics:
     * cycles completed, version and type's footprint and ordinals.
     * @param status the status
     * @param readState the read state
     * @param version the version
     */
    public void updateCycleMetrics(Status status, HollowProducer.ReadState readState, long version) {
        cyclesCompleted++;
        if(status.getType() == Status.StatusType.FAIL) {
            cycleFailed++;
            return;
        }
        cyclesSucceeded++;

        if(readState != null) {
            HollowReadStateEngine hollowReadStateEngine = readState.getStateEngine();
            super.update(hollowReadStateEngine, version);
        } else {
            super.update(version);
        }
    }

    public void updateBlobTypeMetrics(HollowProducerListener.PublishStatus publishStatus) {
        Status.StatusType st = publishStatus.getStatus() == HollowProducerListener.Status.SUCCESS
                ? Status.StatusType.SUCCESS
                : Status.StatusType.FAIL;

        updateBlobTypeMetrics(new Status(st, publishStatus.getCause()), publishStatus.getBlob());
    }

    public void updateBlobTypeMetrics(Status status, HollowProducer.Blob blob) {
        HollowProducer.Blob.Type blobType = blob.getType();
        switch (blobType) {
            case SNAPSHOT:
                if(status.getType() == Status.StatusType.SUCCESS)
                    snapshotsCompleted.incrementAndGet();
                else
                    snapshotsFailed.incrementAndGet();
                break;
            case DELTA:
                if(status.getType() == Status.StatusType.SUCCESS)
                    deltasCompleted++;
                else
                    deltasFailed++;
                break;
            case REVERSE_DELTA:
                if(status.getType() == Status.StatusType.SUCCESS)
                    reverseDeltasCompleted++;
                else
                    reverseDeltasFailed++;
                break;
        }
    }

    public int getCyclesCompleted() {
        return this.cyclesCompleted;
    }

    public int getCyclesSucceeded() {
        return this.cyclesSucceeded;
    }

    public int getCycleFailed() {
        return this.cycleFailed;
    }

    public int getSnapshotsCompleted() {
        return snapshotsCompleted.get();
    }

    public int getSnapshotsFailed() {
        return snapshotsFailed.get();
    }

    public int getDeltasCompleted() {
        return deltasCompleted;
    }

    public int getDeltasFailed() {
        return deltasFailed;
    }

    public int getReverseDeltasCompleted() {
        return reverseDeltasCompleted;
    }

    public int getReverseDeltasFailed() {
        return reverseDeltasFailed;
    }

    public int getHeadersCompleted() {
        return headersCompleted;
    }

    public int getHeadersFailed() {
        return headersFailed;
    }
}

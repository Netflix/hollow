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
package com.netflix.hollow.api.metrics;

import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.api.producer.HollowProducerListener;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import static com.netflix.hollow.api.producer.HollowProducer.Blob.Type.*;

public class HollowProducerMetrics extends HollowMetrics {
    private int cyclesCompleted = 0;
    private int cyclesSucceeded = 0;
    private int cycleFailed = 0;
    private int snapshotsCompleted = 0;
    private int snapshotsFailed = 0;
    private int deltasCompleted = 0;
    private int deltasFailed = 0;
    private int reverseDeltasCompleted = 0;
    private int reverseDeltasFailed = 0;

    /**
     * Updates the producer metrics:
     * cycles completed, version and type's footprint and ordinals.
     * @param producerStatus
     */
    public void updateCycleMetrics(HollowProducerListener.ProducerStatus producerStatus) {
        cyclesCompleted++;
        if(producerStatus.getStatus() == HollowProducerListener.Status.FAIL) {
            cycleFailed++;
            return;
        }
        cyclesSucceeded++;

        if(producerStatus.getReadState() != null) {
            HollowReadStateEngine hollowReadStateEngine = producerStatus.getReadState().getStateEngine();
            super.update(hollowReadStateEngine, producerStatus.getVersion());
        } else {
            super.update(producerStatus.getVersion());
        }
    }

    public void updateBlobTypeMetrics(HollowProducerListener.PublishStatus publishStatus) {
        HollowProducer.Blob.Type blobType = publishStatus.getBlob().getType();
        switch (blobType) {
            case SNAPSHOT:
                if(publishStatus.getStatus() == HollowProducerListener.Status.SUCCESS)
                    snapshotsCompleted++;
                else
                    snapshotsFailed++;
                break;
            case DELTA:
                if(publishStatus.getStatus() == HollowProducerListener.Status.SUCCESS)
                    deltasCompleted++;
                else
                    deltasFailed++;
                break;
            case REVERSE_DELTA:
                if(publishStatus.getStatus() == HollowProducerListener.Status.SUCCESS)
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
        return snapshotsCompleted;
    }

    public int getSnapshotsFailed() {
        return snapshotsFailed;
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
}

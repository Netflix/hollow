/*
 *  Copyright 2021 Netflix, Inc.
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
package com.netflix.hollow.api.testdata;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.util.StateEngineRoundTripper;
import com.netflix.hollow.core.write.HollowBlobWriter;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class HollowTestDataset {

    private final HollowWriteStateEngine writeEngine;
    private final List<HollowTestRecord<Void>> recsToAdd;

    private HollowTestBlobRetriever blobRetriever;

    private long currentState = 0L;


    public HollowTestDataset() {
        this.writeEngine = new HollowWriteStateEngine();
        this.recsToAdd = new ArrayList<>();
    }

    public void add(HollowTestRecord<Void> rec) {
        recsToAdd.add(rec);
    }

    public HollowConsumer.Builder<?> newConsumerBuilder() {
        blobRetriever = new HollowTestBlobRetriever();

        return HollowConsumer
                .newHollowConsumer()
                .withBlobRetriever(blobRetriever);
    }

    public void buildHeader(HollowConsumer consumer) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            HollowBlobWriter writer = new HollowBlobWriter(writeEngine);
            writer.writeHeader(baos, null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void buildSnapshot(HollowConsumer consumer) {
        for(HollowTestRecord<Void> rec : recsToAdd) {
            rec.addTo(writeEngine);
        }

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            HollowBlobWriter writer = new HollowBlobWriter(writeEngine);
            writer.writeSnapshot(baos);
            writeEngine.prepareForNextCycle();
            blobRetriever.addSnapshot(currentState, new HollowTestBlobRetriever.TestBlob(currentState, baos.toByteArray()));

            consumer.triggerRefreshTo(currentState);
        } catch (IOException rethrow) {
            throw new RuntimeException(rethrow);
        }
    }

    public void buildDelta(HollowConsumer consumer) {
        for(HollowTestRecord<Void> rec : recsToAdd) {
            rec.addTo(writeEngine);
        }

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            HollowBlobWriter writer = new HollowBlobWriter(writeEngine);
            writer.writeDelta(baos);
            writeEngine.prepareForNextCycle();
            long nextState = currentState + 1;
            blobRetriever.addDelta(currentState, new HollowTestBlobRetriever.TestBlob(currentState, nextState, baos.toByteArray()));
            consumer.triggerRefreshTo(nextState);
            currentState = nextState;
        } catch (IOException rethrow) {
            throw new RuntimeException(rethrow);
        }
    }

    public HollowReadStateEngine buildSnapshot() {
        for(HollowTestRecord<Void> rec : recsToAdd) {
            rec.addTo(writeEngine);
        }

        try {
            return StateEngineRoundTripper.roundTripSnapshot(writeEngine);
        } catch (IOException rethrow) {
            throw new RuntimeException(rethrow);
        }
    }

    public void buildDelta(HollowReadStateEngine readEngine) {
        for(HollowTestRecord<Void> rec : recsToAdd) {
            rec.addTo(writeEngine);
        }

        try {
            StateEngineRoundTripper.roundTripDelta(writeEngine, readEngine);
        } catch (IOException rethrow) {
            throw new RuntimeException(rethrow);
        }
    }

}

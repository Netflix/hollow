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
package com.netflix.hollow.api.consumer.index;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.InMemoryBlobStore;
import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.api.producer.fs.HollowInMemoryBlobStager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class HashIndexUpdatesTest {
    InMemoryBlobStore blobStore;

    @Before
    public void setUp() {
        blobStore = new InMemoryBlobStore();
    }

    void updates(boolean doubleSnapshot) {
        HollowProducer producer = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .build();

        long v1 = producer.runCycle(ws -> {
            ws.add(new DataModel.Producer.TypeA(1, "1"));
        });
        HollowConsumer consumer = HollowConsumer.withBlobRetriever(blobStore)
                .withGeneratedAPIClass(DataModel.Consumer.Api.class)
                .build();
        consumer.triggerRefreshTo(v1);

        HashIndex<DataModel.Consumer.TypeA, Integer> hi = HashIndex.from(consumer, DataModel.Consumer.TypeA.class)
                .usingPath("i", int.class);
        consumer.addRefreshListener(hi);

        Assert.assertEquals(1L, hi.findMatches(1).count());


        long v2 = producer.runCycle(ws -> {
            ws.add(new DataModel.Producer.TypeA(1, "1"));
            ws.add(new DataModel.Producer.TypeA(1, "2"));
        });
        if(doubleSnapshot) {
            consumer.forceDoubleSnapshotNextUpdate();
        }
        consumer.triggerRefreshTo(v2);

        Assert.assertEquals(2L, hi.findMatches(1).count());


        consumer.removeRefreshListener(hi);
        long v3 = producer.runCycle(ws -> {
            ws.add(new DataModel.Producer.TypeA(1, "1"));
            ws.add(new DataModel.Producer.TypeA(1, "2"));
            ws.add(new DataModel.Producer.TypeA(1, "3"));
        });
        if(doubleSnapshot) {
            consumer.forceDoubleSnapshotNextUpdate();
        }
        consumer.triggerRefreshTo(v3);

        Assert.assertEquals(2L, hi.findMatches(1).count());
    }

    @Test
    public void deltaUpdates() {
        updates(false);
    }

    @Test
    public void snapshotUpdates() {
        updates(true);
    }
}

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
package com.netflix.hollow.core.read.object;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.InMemoryBlobStore;
import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.api.producer.fs.HollowInMemoryBlobStager;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import org.junit.Assert;
import org.junit.Test;

public class HollowObjectLargeFieldSizeTest {

    @Test
    public void preserveNullValueWhenFieldSizeIsLargeInSnapshot() {

        InMemoryBlobStore blobStore = new InMemoryBlobStore();
        HollowProducer producer = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .build();

        long v1 = producer.runCycle(state -> {
            state.add(new Long(1L));
            state.add(new Long(0L));
            state.add(new Long(2L));
            state.add(new Long(-1L));
            state.add(new Long(3L));
            state.add(new Long(Long.MIN_VALUE));
            state.add(new Long(4L));
            state.add(new Long(Long.MAX_VALUE));
            state.add(new Long(5L));
        });

        HollowConsumer consumer = HollowConsumer.withBlobRetriever(blobStore).build();
        consumer.triggerRefreshTo(v1);

        assertValues(consumer, 1L, 0L, 2L, -1L, 3L, Long.MIN_VALUE, 4L, Long.MAX_VALUE, 5L);
    }

    @Test
    public void preserveNullValueWhenFieldSizeBecomesLargeInDelta() {

        InMemoryBlobStore blobStore = new InMemoryBlobStore();
        HollowProducer producer = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .noIntegrityCheck()
                .build();

        long v1 = producer.runCycle(state -> {
            state.add(new Long(1L));
            state.add(new Long(0L));
            state.add(new Long(2L));
            state.add(new Long(-1L));
            state.add(new Long(3L));
            state.add(new Long(4L));
            state.add(new Long(5L));
        });

        HollowConsumer consumer = HollowConsumer.withBlobRetriever(blobStore).build();
        consumer.triggerRefreshTo(v1);

        assertValues(consumer, 1L, 0L, 2L, -1L, 3L, 4L, 5L);

        long v2 = producer.runCycle(state -> {
            state.add(new Long(1L));
            state.add(new Long(0L));
            state.add(new Long(2L));
            state.add(new Long(-1L));
            state.add(new Long(3L));
            state.add(new Long(4L));
            state.add(new Long(5L));
            state.add(new Long(Long.MIN_VALUE));
            state.add(new Long(Long.MAX_VALUE));
        });

        consumer.triggerRefreshTo(v2);

        assertValues(consumer, 1L, 0L, 2L, -1L, 3L, 4L, 5L, Long.MIN_VALUE, Long.MAX_VALUE);
    }


    private void assertValues(HollowConsumer consumer, long... values) {

        HollowObjectTypeReadState typeState = (HollowObjectTypeReadState) consumer.getStateEngine().getTypeState("Long");

        for(int i = 0; i < values.length; i++) {
            Assert.assertEquals(values[i], typeState.readLong(i, 0));
        }


    }

}

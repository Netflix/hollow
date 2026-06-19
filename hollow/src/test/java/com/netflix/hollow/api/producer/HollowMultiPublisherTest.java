/*
 *
 *  Copyright 2018 Netflix, Inc.
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

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.InMemoryBlobStore;
import com.netflix.hollow.api.objects.generic.GenericHollowObject;
import com.netflix.hollow.api.producer.HollowProducer.Populator;
import com.netflix.hollow.api.producer.HollowProducer.WriteState;
import com.netflix.hollow.api.producer.experimental.AbstractHollowMultiPublisher;
import com.netflix.hollow.api.producer.experimental.ParallelHollowMultiPublisher;
import com.netflix.hollow.api.producer.experimental.SingleHollowMultiPublisher;
import com.netflix.hollow.api.producer.fs.HollowInMemoryBlobStager;
import com.netflix.hollow.core.index.HollowPrimaryKeyIndex;
import com.netflix.hollow.core.write.objectmapper.HollowPrimaryKey;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class HollowMultiPublisherTest {

    private InMemoryBlobStore blobStorage1;
    private InMemoryBlobStore blobStorage2;

    @Before
    public void setUp() {
        blobStorage1 = new InMemoryBlobStore();
        blobStorage2 = new InMemoryBlobStore();
    }


    @Test
    public void publishToMultipleStoragesInParallel() {
        List<HollowProducer.Publisher> publishers = new ArrayList<>();
        publishers.add(blobStorage1);
        publishers.add(blobStorage2);

        ParallelHollowMultiPublisher parallelHollowMultiPublisher = new ParallelHollowMultiPublisher(publishers, 2.0d);

        HollowProducer producer = createInMemoryProducer(parallelHollowMultiPublisher);

        /// initialize the data -- classic producer creates the first state in the delta chain.
        long version = initializeData(producer);

        /// now we read the changes on each storage
        verifyStorages(blobStorage1, blobStorage2, version);
    }

    @Test
    public void publishToMultipleStorages() {
        List<HollowProducer.Publisher> publishers = new ArrayList<>();
        publishers.add(blobStorage1);
        publishers.add(blobStorage2);

        SingleHollowMultiPublisher parallelHollowMultiPublisher = new SingleHollowMultiPublisher(publishers);

        HollowProducer producer = createInMemoryProducer(parallelHollowMultiPublisher);

        /// initialize the data -- classic producer creates the first state in the delta chain.
        long version = initializeData(producer);

        /// now we read the changes on each storage
        verifyStorages(blobStorage1, blobStorage2, version);
    }

    private void verifyStorages(InMemoryBlobStore blobStorage1, InMemoryBlobStore blobStorage2, long version) {
        HollowConsumer consumerBlobStorage1 = HollowConsumer.withBlobRetriever(blobStorage1).build();
        consumerBlobStorage1.triggerRefreshTo(version);

        HollowPrimaryKeyIndex idxBlobStorage1 = new HollowPrimaryKeyIndex(consumerBlobStorage1.getStateEngine(), "TypeA", "id1", "id2");
        Assert.assertFalse(idxBlobStorage1.containsDuplicates());

        assertTypeA(idxBlobStorage1, 1, "one", 1L);
        assertTypeA(idxBlobStorage1, 2, "two", 2L);
        assertTypeA(idxBlobStorage1, 3, "three", 3L);
        assertTypeA(idxBlobStorage1, 4, "four", 4L);
        assertTypeA(idxBlobStorage1, 5, "five", 5L);

        HollowConsumer consumerBlobStorage2 = HollowConsumer.withBlobRetriever(blobStorage2).build();
        consumerBlobStorage2.triggerRefreshTo(version);

        HollowPrimaryKeyIndex idxBlobStorage2 = new HollowPrimaryKeyIndex(consumerBlobStorage2.getStateEngine(), "TypeA", "id1", "id2");
        Assert.assertFalse(idxBlobStorage2.containsDuplicates());

        assertTypeA(idxBlobStorage2, 1, "one", 1L);
        assertTypeA(idxBlobStorage2, 2, "two", 2L);
        assertTypeA(idxBlobStorage2, 3, "three", 3L);
        assertTypeA(idxBlobStorage2, 4, "four", 4L);
        assertTypeA(idxBlobStorage2, 5, "five", 5L);
    }

    @SuppressWarnings("unused")
    @HollowPrimaryKey(fields = {"id1", "id2"})
    private static class TypeA {
        int id1;
        String id2;
        long value;

        public TypeA(int id1, String id2, long value) {
            this.id1 = id1;
            this.id2 = id2;
            this.value = value;
        }
    }

    private long initializeData(HollowProducer producer) {
        return producer.runCycle(new Populator() {
            public void populate(WriteState state) throws Exception {
                state.add(new TypeA(1, "one", 1));
                state.add(new TypeA(2, "two", 2));
                state.add(new TypeA(3, "three", 3));
                state.add(new TypeA(4, "four", 4));
                state.add(new TypeA(5, "five", 5));
            }
        });
    }

    private void assertTypeA(HollowPrimaryKeyIndex typeAIdx, int id1,
                             String id2, Long expectedValue) {
        int ordinal = typeAIdx.getMatchingOrdinal(id1, id2);

        if (expectedValue == null) {
            Assert.assertEquals(-1, ordinal);
        } else {
            Assert.assertNotEquals(-1, ordinal);
            GenericHollowObject obj = new GenericHollowObject(
                    typeAIdx.getTypeState(), ordinal);
            Assert.assertEquals(expectedValue.longValue(), obj.getLong("value"));
        }
    }

    private HollowProducer createInMemoryProducer(AbstractHollowMultiPublisher multiPublisher) {
        return HollowProducer.withPublisher(multiPublisher)
                .withBlobStager(new HollowInMemoryBlobStager())
                .build();
    }

}

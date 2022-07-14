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

public class UniqueKeyUpdatesTest {
    InMemoryBlobStore blobStore;

    @Before
    public void setUp() {
        blobStore = new InMemoryBlobStore();
    }

    static class Key {
        @FieldPath("i")
        int i;
        @FieldPath("sub1.s")
        String sub1_s;
        @FieldPath("sub2.i")
        int sub2_i;

        Key(int i, String sub1_s, int sub2_i) {
            this.i = i;
            this.sub1_s = sub1_s;
            this.sub2_i = sub2_i;
        }
    }

    void updates(boolean doubleSnapshot) {
        HollowProducer producer = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .build();

        long v1 = producer.runCycle(ws -> {
            ws.add(new DataModel.Producer.TypeWithPrimaryKey(
                    1,
                    new DataModel.Producer.SubTypeOfTypeWithPrimaryKey("1", 1),
                    new DataModel.Producer.SubTypeOfTypeWithPrimaryKey("2", 2)));
        });
        HollowConsumer consumer = HollowConsumer.withBlobRetriever(blobStore)
                .withGeneratedAPIClass(DataModel.Consumer.Api.class)
                .build();
        consumer.triggerRefreshTo(v1);

        UniqueKeyIndex<DataModel.Consumer.TypeWithPrimaryKey, Key> uki = UniqueKeyIndex.from(consumer,
                DataModel.Consumer.TypeWithPrimaryKey.class)
                .bindToPrimaryKey()
                .usingBean(Key.class);
        consumer.addRefreshListener(uki);

        Assert.assertNotNull(uki.findMatch(new Key(1, "1", 2)));


        long v2 = producer.runCycle(ws -> {
            ws.add(new DataModel.Producer.TypeWithPrimaryKey(
                    1,
                    new DataModel.Producer.SubTypeOfTypeWithPrimaryKey("1", 1),
                    new DataModel.Producer.SubTypeOfTypeWithPrimaryKey("2", 2)));

            ws.add(new DataModel.Producer.TypeWithPrimaryKey(
                    2,
                    new DataModel.Producer.SubTypeOfTypeWithPrimaryKey("1", 1),
                    new DataModel.Producer.SubTypeOfTypeWithPrimaryKey("2", 2)));
        });
        if(doubleSnapshot) {
            consumer.forceDoubleSnapshotNextUpdate();
        }
        consumer.triggerRefreshTo(v2);

        Assert.assertNotNull(uki.findMatch(new Key(1, "1", 2)));
        Assert.assertNotNull(uki.findMatch(new Key(2, "1", 2)));


        consumer.removeRefreshListener(uki);
        long v3 = producer.runCycle(ws -> {
            ws.add(new DataModel.Producer.TypeWithPrimaryKey(
                    1,
                    new DataModel.Producer.SubTypeOfTypeWithPrimaryKey("1", 1),
                    new DataModel.Producer.SubTypeOfTypeWithPrimaryKey("2", 2)));

            ws.add(new DataModel.Producer.TypeWithPrimaryKey(
                    2,
                    new DataModel.Producer.SubTypeOfTypeWithPrimaryKey("1", 1),
                    new DataModel.Producer.SubTypeOfTypeWithPrimaryKey("2", 2)));

            ws.add(new DataModel.Producer.TypeWithPrimaryKey(
                    3,
                    new DataModel.Producer.SubTypeOfTypeWithPrimaryKey("1", 1),
                    new DataModel.Producer.SubTypeOfTypeWithPrimaryKey("2", 2)));
        });
        if(doubleSnapshot) {
            consumer.forceDoubleSnapshotNextUpdate();
        }
        consumer.triggerRefreshTo(v3);

        Assert.assertNotNull(uki.findMatch(new Key(1, "1", 2)));
        Assert.assertNotNull(uki.findMatch(new Key(2, "1", 2)));
        Assert.assertNull(uki.findMatch(new Key(3, "1", 2)));
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

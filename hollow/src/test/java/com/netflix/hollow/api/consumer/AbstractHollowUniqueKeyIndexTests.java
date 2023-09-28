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
package com.netflix.hollow.api.consumer;

import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.api.producer.fs.HollowInMemoryBlobStager;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.write.objectmapper.HollowPrimaryKey;
import com.netflix.hollow.test.InMemoryBlobStore;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class AbstractHollowUniqueKeyIndexTests {

    private InMemoryBlobStore blobStore;

    @Before
    public void setUp() {
        blobStore = new InMemoryBlobStore();
    }

    static class TypeAPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<HollowAPI, Object> {

        public TypeAPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
            this(consumer, isListenToDataRefresh,
                    ((HollowObjectSchema) consumer.getStateEngine().getNonNullSchema("TypeA")).getPrimaryKey()
                            .getFieldPaths());
        }

        private TypeAPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
            this(consumer, false, fieldPaths);
        }

        private TypeAPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
            super(consumer, "TypeA", isListenToDataRefresh, fieldPaths);
        }

        public int findMatch(Object... keys) {
            return idx.getMatchingOrdinal(keys);
        }

        public Object[] getRecordKey(int ordinal) {
            return idx.getRecordKey(ordinal);
        }

    }


    @Test
    public void deltaUpdates() {
        HollowProducer producer = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .withNumStatesBetweenSnapshots(2) /// do not produce snapshots for v2 or v3
                .build();

        long v1 = producer.runCycle(ws -> {
            ws.add(new TypeA(1, 1.1d, new TypeB("one")));
            ws.add(new TypeA(1, 1.1d, new TypeB("1")));
            ws.add(new TypeA(2, 2.2d, new TypeB("two")));

        });
        HollowConsumer consumer = HollowConsumer.withBlobRetriever(blobStore).build();
        consumer.triggerRefreshTo(v1);

        TypeAPrimaryKeyIndex indexer = new TypeAPrimaryKeyIndex(consumer, true);

        int ord1 = indexer.findMatch(1, 1.1d, "1");
        int ord0 = indexer.findMatch(1, 1.1d, "one");
        int ord2 = indexer.findMatch(2, 2.2d, "two");

        Assert.assertEquals(0, ord0);
        Assert.assertEquals(1, ord1);
        Assert.assertEquals(2, ord2);
        assertArrayEquals(indexer.getRecordKey(0), 1, 1.1d, "one");
        assertArrayEquals(indexer.getRecordKey(1), 1, 1.1d, "1");
        assertArrayEquals(indexer.getRecordKey(2), 2, 2.2d, "two");


        long v2 = producer.runCycle(ws -> {
            ws.add(new TypeA(1, 1.1d, new TypeB("one")));
            // mapper.add(new TypeA(1, 1.1d, new TypeB("1")));
            ws.add(new TypeA(2, 2.2d, new TypeB("two")));
            ws.add(new TypeA(3, 3.3d, new TypeB("three")));

        });
        consumer.triggerRefreshTo(v2);

        ord0 = indexer.findMatch(1, 1.1d, "one");
        ord1 = indexer.findMatch(1, 1.1d, "1");
        ord2 = indexer.findMatch(2, 2.2d, "two");
        int ord3 = indexer.findMatch(3, 3.3d, "three");

        Assert.assertEquals(0, ord0);
        Assert.assertEquals(-1, ord1);
        Assert.assertEquals(2, ord2);
        Assert.assertEquals(3, ord3);
        assertArrayEquals(indexer.getRecordKey(0), 1, 1.1d, "one");
        assertArrayEquals(indexer.getRecordKey(1), 1, 1.1d,
                "1"); // it is a ghost record (marked deleted but it is available)
        assertArrayEquals(indexer.getRecordKey(2), 2, 2.2d, "two");
        assertArrayEquals(indexer.getRecordKey(3), 3, 3.3d, "three");

    }

    @Test
    public void snapshotUpdates() {
        HollowProducer producer = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .withNumStatesBetweenSnapshots(2) /// do not produce snapshots for v2 or v3
                .build();

        long v1 = producer.runCycle(ws -> {
            ws.add(new TypeA(1, 1.1d, new TypeB("one")));
            ws.add(new TypeA(1, 1.1d, new TypeB("1")));
            ws.add(new TypeA(2, 2.2d, new TypeB("two")));
        });

        HollowConsumer consumer = HollowConsumer.withBlobRetriever(blobStore).build();
        consumer.triggerRefreshTo(v1);
        long cv1 = consumer.getCurrentVersionId();

        TypeAPrimaryKeyIndex indexer = new TypeAPrimaryKeyIndex(consumer, true);

        int ord1 = indexer.findMatch(1, 1.1d, "1");
        int ord0 = indexer.findMatch(1, 1.1d, "one");
        int ord2 = indexer.findMatch(2, 2.2d, "two");

        Assert.assertEquals(0, ord0);
        Assert.assertEquals(1, ord1);
        Assert.assertEquals(2, ord2);
        assertArrayEquals(indexer.getRecordKey(0), 1, 1.1d, "one");
        assertArrayEquals(indexer.getRecordKey(1), 1, 1.1d, "1");
        assertArrayEquals(indexer.getRecordKey(2), 2, 2.2d, "two");


        long v2 = producer.runCycle(ws -> {
            ws.add(new TypeA(1, 1.1d, new TypeB("one")));
            // mapper.add(new TypeA(1, 1.1d, new TypeB("1")));
            ws.add(new TypeA(2, 2.2d, new TypeB("two")));
            ws.add(new TypeA(3, 3.3d, new TypeB("three")));

        });
        consumer.forceDoubleSnapshotNextUpdate();
        consumer.triggerRefreshTo(v2);

        ord0 = indexer.findMatch(1, 1.1d, "one");
        ord1 = indexer.findMatch(1, 1.1d, "1");
        ord2 = indexer.findMatch(2, 2.2d, "two");
        int ord3 = indexer.findMatch(3, 3.3d, "three");

        Assert.assertEquals(0, ord0);
        Assert.assertEquals(-1, ord1);
        Assert.assertEquals(2, ord2);
        Assert.assertEquals(3, ord3);
        assertArrayEquals(indexer.getRecordKey(0), 1, 1.1d, "one");
        // it is a ghost record (marked deleted but it is available)
        assertArrayEquals(indexer.getRecordKey(1), 1, 1.1d, "1");
        assertArrayEquals(indexer.getRecordKey(2), 2, 2.2d, "two");
        assertArrayEquals(indexer.getRecordKey(3), 3, 3.3d, "three");


        long v3 = producer.runCycle(ws -> {
            ws.add(new TypeA(1, 1.1d, new TypeB("one")));
            ws.add(new TypeA(1, 1.1d, new TypeB("1")));
            ws.add(new TypeA(2, 2.2d, new TypeB("two")));
        });
        consumer.triggerRefreshTo(v3);

        ord0 = indexer.findMatch(1, 1.1d, "one");
        ord1 = indexer.findMatch(1, 1.1d, "1");
        ord2 = indexer.findMatch(2, 2.2d, "two");
        ord3 = indexer.findMatch(3, 3.3d, "three");

        Assert.assertEquals(0, ord0);
        Assert.assertEquals(1, ord1);
        Assert.assertEquals(2, ord2);
        Assert.assertEquals(-1, ord3);

        assertArrayEquals(indexer.getRecordKey(0), 1, 1.1d, "one");
        assertArrayEquals(indexer.getRecordKey(1), 1, 1.1d, "1");
        assertArrayEquals(indexer.getRecordKey(2), 2, 2.2d, "two");
        // it is a ghost record (marked deleted but it is available)
        assertArrayEquals(indexer.getRecordKey(3), 3, 3.3d, "three");
    }


    private static void assertArrayEquals(Object[] actual, Object... expected) {
        Assert.assertArrayEquals(expected, actual);
    }


    @HollowPrimaryKey(fields = {"a1", "a2", "ab.b1"})
    private static class TypeA {
        private final int a1;
        private final double a2;
        private final TypeB ab;

        public TypeA(int a1, double a2, TypeB ab) {
            this.a1 = a1;
            this.a2 = a2;
            this.ab = ab;
        }
    }

    private static class TypeB {
        private final String b1;
        private final boolean isDuplicate;

        public TypeB(String b1) {
            this(b1, false);
        }

        public TypeB(String b1, boolean isDuplicate) {
            this.b1 = b1;
            this.isDuplicate = isDuplicate;
        }
    }
}

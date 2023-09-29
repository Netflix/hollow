package com.netflix.hollow.api.consumer;

import com.netflix.hollow.api.consumer.index.AbstractHollowHashIndex;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.api.producer.fs.HollowInMemoryBlobStager;
import com.netflix.hollow.core.index.HollowHashIndexResult;
import com.netflix.hollow.core.read.iterator.EmptyOrdinalIterator;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.netflix.hollow.test.InMemoryBlobStore;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class AbstractHollowHashIndexTests {
    private InMemoryBlobStore blobStore;

    @Before
    public void setUp() {
        blobStore = new InMemoryBlobStore();
    }

    static class TypeHashIndex extends AbstractHollowHashIndex<HollowAPI> {

        public TypeHashIndex(
                HollowConsumer consumer, String queryType, String selectFieldPath, String... matchFieldPaths) {
            super(consumer, false, queryType, selectFieldPath, matchFieldPaths);
        }

        public TypeHashIndex(
                HollowConsumer consumer, boolean isListenToDataRefresh, String queryType, String selectFieldPath,
                String... matchFieldPaths) {
            super(consumer, isListenToDataRefresh, queryType, selectFieldPath, matchFieldPaths);
        }

        public HollowOrdinalIterator findMatches(Object... keys) {
            HollowHashIndexResult matches = idx.findMatches(keys);
            return matches == null ? EmptyOrdinalIterator.INSTANCE : matches.iterator();
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
            ws.add(new TypeA(2, 2.2d, new TypeB("two"), new TypeB("twenty"), new TypeB("two hundred")));
            ws.add(new TypeA(3, 3.3d, new TypeB("three"), new TypeB("thirty"), new TypeB("three hundred")));
            ws.add(new TypeA(4, 4.4d, new TypeB("four")));
            ws.add(new TypeA(4, 4.5d, new TypeB("four"), new TypeB("forty")));
        });
        HollowConsumer consumer = HollowConsumer.withBlobRetriever(blobStore).build();
        consumer.triggerRefreshTo(v1);

        TypeHashIndex index = new TypeHashIndex(consumer, true, "TypeA", "", "a1");

        assertIteratorContainsAll(index.findMatches(0));
        assertIteratorContainsAll(index.findMatches(1), 1, 0);
        assertIteratorContainsAll(index.findMatches(2), 2);
        assertIteratorContainsAll(index.findMatches(3), 3);
        assertIteratorContainsAll(index.findMatches(4), 4, 5);


        long v2 = producer.runCycle(ws -> {
            ws.add(new TypeA(1, 1.1d, new TypeB("one")));
            ws.add(new TypeA(1, 1.1d, new TypeB("1")));
            ws.add(new TypeA(3, 3.3d, new TypeB("three"), new TypeB("thirty"), new TypeB("three hundred")));
            ws.add(new TypeA(4, 4.4d, new TypeB("four"), new TypeB("fore")));
            ws.add(new TypeA(4, 4.5d, new TypeB("four"), new TypeB("fourfour")));
            ws.add(new TypeA(4, 4.5d, new TypeB("four"), new TypeB("forty")));
        });
        consumer.triggerRefreshTo(v2);

        // verify the ordinals we get from the index match our new expected ones.
        assertIteratorContainsAll(index.findMatches(1), 1, 0);
        assertIteratorContainsAll(index.findMatches(2));
        assertIteratorContainsAll(index.findMatches(3), 3);
        assertIteratorContainsAll(index.findMatches(4), 5, 6, 7);


        long v3 = producer.runCycle(ws -> {
            ws.add(new TypeA(1, 1.1d, new TypeB("one")));
            ws.add(new TypeA(1, 1.1d, new TypeB("1")));
            ws.add(new TypeA(2, 2.2d, new TypeB("two"), new TypeB("twenty"), new TypeB("two hundred")));
            ws.add(new TypeA(3, 3.3d, new TypeB("three"), new TypeB("thirty"), new TypeB("three hundred")));
            ws.add(new TypeA(4, 4.4d, new TypeB("four")));
            ws.add(new TypeA(4, 4.5d, new TypeB("four"), new TypeB("forty")));
        });
        consumer.triggerRefreshTo(v3);

        assertIteratorContainsAll(index.findMatches(0));
        assertIteratorContainsAll(index.findMatches(1), 1, 0);
        assertIteratorContainsAll(index.findMatches(2), 2);
        assertIteratorContainsAll(index.findMatches(3), 3);
        assertIteratorContainsAll(index.findMatches(4), 4, 5);
    }

    @Test
    public void deltaSnapshotUpdates() {
        HollowProducer producer = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .withNumStatesBetweenSnapshots(2) /// do not produce snapshots for v2 or v3
                .build();

        long v1 = producer.runCycle(ws -> {
            ws.add(new TypeA(1, 1.1d, new TypeB("one")));
            ws.add(new TypeA(1, 1.1d, new TypeB("1")));
            ws.add(new TypeA(2, 2.2d, new TypeB("two"), new TypeB("twenty"), new TypeB("two hundred")));
            ws.add(new TypeA(3, 3.3d, new TypeB("three"), new TypeB("thirty"), new TypeB("three hundred")));
            ws.add(new TypeA(4, 4.4d, new TypeB("four")));
            ws.add(new TypeA(4, 4.5d, new TypeB("four"), new TypeB("forty")));
        });
        HollowConsumer consumer = HollowConsumer.withBlobRetriever(blobStore).build();
        consumer.triggerRefreshTo(v1);

        TypeHashIndex index = new TypeHashIndex(consumer, true, "TypeA", "", "a1");

        assertIteratorContainsAll(index.findMatches(0));
        assertIteratorContainsAll(index.findMatches(1), 1, 0);
        assertIteratorContainsAll(index.findMatches(2), 2);
        assertIteratorContainsAll(index.findMatches(3), 3);
        assertIteratorContainsAll(index.findMatches(4), 4, 5);


        long v2 = producer.runCycle(ws -> {
            ws.add(new TypeA(1, 1.1d, new TypeB("one")));
            ws.add(new TypeA(1, 1.1d, new TypeB("1")));
            ws.add(new TypeA(3, 3.3d, new TypeB("three"), new TypeB("thirty"), new TypeB("three hundred")));
            ws.add(new TypeA(4, 4.4d, new TypeB("four"), new TypeB("fore")));
            ws.add(new TypeA(4, 4.5d, new TypeB("four"), new TypeB("fourfour")));
            ws.add(new TypeA(4, 4.5d, new TypeB("four"), new TypeB("forty")));
        });
        consumer.triggerRefreshTo(v2);

        // verify the ordinals we get from the index match our new expected ones.
        assertIteratorContainsAll(index.findMatches(1), 1, 0);
        assertIteratorContainsAll(index.findMatches(2));
        assertIteratorContainsAll(index.findMatches(3), 3);
        assertIteratorContainsAll(index.findMatches(4), 5, 6, 7);


        long v3 = producer.runCycle(ws -> {
            ws.add(new TypeA(1, 1.1d, new TypeB("one")));
            ws.add(new TypeA(1, 1.1d, new TypeB("1")));
            ws.add(new TypeA(2, 2.2d, new TypeB("two"), new TypeB("twenty"), new TypeB("two hundred")));
            ws.add(new TypeA(3, 3.3d, new TypeB("three"), new TypeB("thirty"), new TypeB("three hundred")));
            ws.add(new TypeA(4, 4.4d, new TypeB("four")));
            ws.add(new TypeA(4, 4.5d, new TypeB("four"), new TypeB("forty")));
        });
        consumer.triggerRefreshTo(v3);

        assertIteratorContainsAll(index.findMatches(0));
        assertIteratorContainsAll(index.findMatches(1), 1, 0);
        assertIteratorContainsAll(index.findMatches(2), 2);
        assertIteratorContainsAll(index.findMatches(3), 3);
        assertIteratorContainsAll(index.findMatches(4), 4, 5);


        long v4 = producer.runCycle(ws -> {
            ws.add(new TypeA(1, 1.1d, new TypeB("one")));
        });
        consumer.forceDoubleSnapshotNextUpdate();
        consumer.triggerRefreshTo(v4);

        assertIteratorContainsAll(index.findMatches(1), 0);
    }

    @Test
    public void snapshotDeltaUpdates() {
        HollowProducer producer = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .withNumStatesBetweenSnapshots(2) /// do not produce snapshots for v2 or v3
                .build();

        long v1 = producer.runCycle(ws -> {
            ws.add(new TypeA(1, 1.1d, new TypeB("one")));
            ws.add(new TypeA(1, 1.1d, new TypeB("1")));
            ws.add(new TypeA(2, 2.2d, new TypeB("two"), new TypeB("twenty"), new TypeB("two hundred")));
            ws.add(new TypeA(3, 3.3d, new TypeB("three"), new TypeB("thirty"), new TypeB("three hundred")));
            ws.add(new TypeA(4, 4.4d, new TypeB("four")));
            ws.add(new TypeA(4, 4.5d, new TypeB("four"), new TypeB("forty")));
        });
        HollowConsumer consumer = HollowConsumer.withBlobRetriever(blobStore).build();
        consumer.triggerRefreshTo(v1);

        TypeHashIndex index = new TypeHashIndex(consumer, true, "TypeA", "", "a1");

        assertIteratorContainsAll(index.findMatches(0));
        assertIteratorContainsAll(index.findMatches(1), 1, 0);
        assertIteratorContainsAll(index.findMatches(2), 2);
        assertIteratorContainsAll(index.findMatches(3), 3);
        assertIteratorContainsAll(index.findMatches(4), 4, 5);


        long v2 = producer.runCycle(ws -> {
            ws.add(new TypeA(1, 1.1d, new TypeB("one")));
            ws.add(new TypeA(1, 1.1d, new TypeB("1")));
            ws.add(new TypeA(3, 3.3d, new TypeB("three"), new TypeB("thirty"), new TypeB("three hundred")));
            ws.add(new TypeA(4, 4.4d, new TypeB("four"), new TypeB("fore")));
            ws.add(new TypeA(4, 4.5d, new TypeB("four"), new TypeB("fourfour")));
            ws.add(new TypeA(4, 4.5d, new TypeB("four"), new TypeB("forty")));
        });
        consumer.forceDoubleSnapshotNextUpdate();
        consumer.triggerRefreshTo(v2);

        // verify the ordinals we get from the index match our new expected ones.
        assertIteratorContainsAll(index.findMatches(1), 1, 0);
        assertIteratorContainsAll(index.findMatches(2));
        assertIteratorContainsAll(index.findMatches(3), 3);
        assertIteratorContainsAll(index.findMatches(4), 5, 6, 7);


        long v3 = producer.runCycle(ws -> {
            ws.add(new TypeA(1, 1.1d, new TypeB("one")));
            ws.add(new TypeA(1, 1.1d, new TypeB("1")));
            ws.add(new TypeA(2, 2.2d, new TypeB("two"), new TypeB("twenty"), new TypeB("two hundred")));
            ws.add(new TypeA(3, 3.3d, new TypeB("three"), new TypeB("thirty"), new TypeB("three hundred")));
            ws.add(new TypeA(4, 4.4d, new TypeB("four")));
            ws.add(new TypeA(4, 4.5d, new TypeB("four"), new TypeB("forty")));
        });
        consumer.triggerRefreshTo(v3);

        assertIteratorContainsAll(index.findMatches(0));
        assertIteratorContainsAll(index.findMatches(1), 1, 0);
        assertIteratorContainsAll(index.findMatches(2), 2);
        assertIteratorContainsAll(index.findMatches(3), 3);
        assertIteratorContainsAll(index.findMatches(4), 4, 5);
    }


    private void assertIteratorContainsAll(HollowOrdinalIterator iter, int... expectedOrdinals) {
        Set<Integer> ordinalSet = new HashSet<Integer>();
        int ordinal = iter.next();
        while (ordinal != HollowOrdinalIterator.NO_MORE_ORDINALS) {
            ordinalSet.add(ordinal);
            ordinal = iter.next();
        }

        for (int ord : expectedOrdinals) {
            Assert.assertTrue(ordinalSet.contains(ord));
        }
        Assert.assertEquals(expectedOrdinals.length, ordinalSet.size());
    }


    private static class TypeA {
        private final int a1;
        private final double a2;
        private final List<TypeB> ab;

        public TypeA(int a1, double a2, TypeB... ab) {
            this.a1 = a1;
            this.a2 = a2;
            this.ab = Arrays.asList(ab);
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

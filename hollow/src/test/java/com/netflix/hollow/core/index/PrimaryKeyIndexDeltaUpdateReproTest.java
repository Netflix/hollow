package com.netflix.hollow.core.index;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.api.producer.fs.HollowInMemoryBlobStager;
import com.netflix.hollow.core.write.objectmapper.HollowPrimaryKey;
import com.netflix.hollow.test.InMemoryBlobStore;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Reproduces the delta update corruption bug in HollowPrimaryKeyIndex.
 * <p>
 * The delta update feature was disabled in 2019 (commit 20ec2864c) due to
 * "matches unexpectedly returning no result," attributed to a possible C2 JIT issue.
 * <p>
 * The actual root cause: {@link HollowPrimaryKeyIndex} registers as a
 * {@code HollowTypeStateListener}, whose {@code endUpdate()} fires after both delta
 * and snapshot transitions. After a snapshot (double snapshot), the
 * {@code PopulatedOrdinalListener}'s {@code previousOrdinals} is empty, so
 * {@code deltaUpdate()} copies the stale pre-snapshot hash table and only inserts
 * entries — it never removes the now-stale entries from the old state.
 * <p>
 * These tests use {@code consumer.forceDoubleSnapshotNextUpdate()} to trigger this
 * deterministically.
 */
public class PrimaryKeyIndexDeltaUpdateReproTest {

    @HollowPrimaryKey(fields = "id")
    static class Rec {
        final int id;
        Rec(int id) { this.id = id; }
    }

    @Before
    public void enableDeltaUpdate() {
        System.setProperty(
                "com.netflix.hollow.core.index.HollowPrimaryKeyIndex.allowDeltaUpdate", "true");
    }

    @After
    public void disableDeltaUpdate() {
        System.clearProperty(
                "com.netflix.hollow.core.index.HollowPrimaryKeyIndex.allowDeltaUpdate");
    }

    /**
     * A double snapshot corrupts the index immediately.
     * <p>
     * 1. Create an index, run a few delta cycles (index is correct)
     * 2. Force a double snapshot
     * 3. Run one more cycle
     * 4. Compare the delta-updated index against a freshly built index — they diverge
     */
    @Test
    public void testDoubleSnapshotCorruptsIndex() {
        InMemoryBlobStore blobStore = new InMemoryBlobStore();
        HollowProducer producer = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .build();
        producer.initializeDataModel(Rec.class);

        int size = 512;
        Set<Integer> activeIds = new HashSet<>();
        for (int i = 0; i < size; i++) activeIds.add(i);
        int nextId = size;

        // Snapshot: ids {0..511}
        Set<Integer> snap0 = new HashSet<>(activeIds);
        long version = producer.runCycle(ws -> {
            for (int id : snap0) ws.add(new Rec(id));
        });

        HollowConsumer consumer = HollowConsumer.withBlobRetriever(blobStore).build();
        consumer.triggerRefreshTo(version);

        HollowPrimaryKeyIndex index = new HollowPrimaryKeyIndex(consumer.getStateEngine(), "Rec", "id");
        index.listenForDeltaUpdates();

        // A few normal delta cycles — index stays correct
        for (int cycle = 0; cycle < 5; cycle++) {
            activeIds.remove(Collections.min(activeIds));
            activeIds.add(nextId++);
            Set<Integer> snap = new HashSet<>(activeIds);
            version = producer.runCycle(ws -> { for (int id : snap) ws.add(new Rec(id)); });
            consumer.triggerRefreshTo(version);
        }

        // Sanity check: index is correct before the double snapshot
        assertIndexMatchesFresh(index, consumer, activeIds);

        // Force a double snapshot on the next consumer update
        consumer.forceDoubleSnapshotNextUpdate();

        // One more delta cycle — consumer loads a snapshot, then endUpdate() runs
        // deltaUpdate() against empty previousOrdinals
        activeIds.remove(Collections.min(activeIds));
        activeIds.add(nextId++);
        Set<Integer> snap = new HashSet<>(activeIds);
        version = producer.runCycle(ws -> { for (int id : snap) ws.add(new Rec(id)); });
        consumer.triggerRefreshTo(version);

        // The index is now corrupt
        assertIndexMatchesFresh(index, consumer, activeIds);
    }

    /**
     * After a double snapshot, corruption accumulates with every subsequent delta cycle
     * and never self-heals.
     */
    @Test
    public void testCorruptionAccumulatesAfterDoubleSnapshot() {
        InMemoryBlobStore blobStore = new InMemoryBlobStore();
        HollowProducer producer = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .build();
        producer.initializeDataModel(Rec.class);

        int size = 512;
        Set<Integer> activeIds = new HashSet<>();
        for (int i = 0; i < size; i++) activeIds.add(i);
        int nextId = size;

        // Snapshot
        Set<Integer> snap0 = new HashSet<>(activeIds);
        long version = producer.runCycle(ws -> {
            for (int id : snap0) ws.add(new Rec(id));
        });

        HollowConsumer consumer = HollowConsumer.withBlobRetriever(blobStore).build();
        consumer.triggerRefreshTo(version);

        HollowPrimaryKeyIndex index = new HollowPrimaryKeyIndex(consumer.getStateEngine(), "Rec", "id");
        index.listenForDeltaUpdates();

        // 10 clean delta cycles
        for (int cycle = 0; cycle < 10; cycle++) {
            activeIds.remove(Collections.min(activeIds));
            activeIds.add(nextId++);
            Set<Integer> snap = new HashSet<>(activeIds);
            version = producer.runCycle(ws -> { for (int id : snap) ws.add(new Rec(id)); });
            consumer.triggerRefreshTo(version);
        }
        assertIndexMatchesFresh(index, consumer, activeIds);

        // Double snapshot
        consumer.forceDoubleSnapshotNextUpdate();

        // 50 more delta cycles — track mismatch growth
        Random rng = new Random(42);
        int corruptCycles = 0;
        int prevMismatches = 0;

        for (int cycle = 0; cycle < 50; cycle++) {
            List<Integer> sorted = new ArrayList<>(activeIds);
            Collections.sort(sorted);
            activeIds.remove(sorted.get(rng.nextInt(sorted.size())));
            activeIds.add(nextId++);

            Set<Integer> snap = new HashSet<>(activeIds);
            version = producer.runCycle(ws -> { for (int id : snap) ws.add(new Rec(id)); });
            consumer.triggerRefreshTo(version);

            int mismatches = countMismatches(index, consumer, activeIds);
            if (mismatches > 0) {
                corruptCycles++;
                // Each cycle can only add mismatches, never remove them — the corruption
                // is permanent because deltaUpdate() builds on the corrupted hash table
                Assert.assertTrue(
                        "Corruption should not decrease: cycle " + cycle
                                + " has " + mismatches + " but prev had " + prevMismatches,
                        mismatches >= prevMismatches);
                prevMismatches = mismatches;
            }
        }

        Assert.assertTrue(
                "Expected corruption after double snapshot but found none",
                corruptCycles > 0);
    }

    // ------------------------------------------------------------------

    private void assertIndexMatchesFresh(HollowPrimaryKeyIndex index,
                                         HollowConsumer consumer,
                                         Set<Integer> expectedIds) {
        HollowPrimaryKeyIndex fresh =
                new HollowPrimaryKeyIndex(consumer.getStateEngine(), "Rec", "id");

        StringBuilder details = new StringBuilder();
        int mismatches = 0;
        for (int id : expectedIds) {
            int deltaOrd = index.getMatchingOrdinal(id);
            int freshOrd = fresh.getMatchingOrdinal(id);
            if (deltaOrd != freshOrd) {
                mismatches++;
                if (details.length() < 2000) {
                    details.append(String.format(
                            "  id=%d: deltaIndex=%d, freshIndex=%d%n", id, deltaOrd, freshOrd));
                }
            }
        }

        Assert.assertEquals(
                "Delta-updated index diverges from fresh index (" + mismatches + " mismatches):\n" + details,
                0, mismatches);
    }

    private int countMismatches(HollowPrimaryKeyIndex index,
                                HollowConsumer consumer,
                                Set<Integer> expectedIds) {
        HollowPrimaryKeyIndex fresh =
                new HollowPrimaryKeyIndex(consumer.getStateEngine(), "Rec", "id");

        int mismatches = 0;
        for (int id : expectedIds) {
            if (index.getMatchingOrdinal(id) != fresh.getMatchingOrdinal(id))
                mismatches++;
        }
        return mismatches;
    }
}

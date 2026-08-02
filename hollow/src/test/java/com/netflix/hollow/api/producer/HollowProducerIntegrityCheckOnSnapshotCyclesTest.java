/*
 *
 *  Copyright 2026 Netflix, Inc.
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.objects.generic.GenericHollowObject;
import com.netflix.hollow.api.producer.fs.HollowInMemoryBlobStager;
import com.netflix.hollow.api.producer.listener.IntegrityCheckListener;
import com.netflix.hollow.api.producer.listener.PublishListener;
import com.netflix.hollow.core.index.HollowPrimaryKeyIndex;
import com.netflix.hollow.core.write.objectmapper.HollowPrimaryKey;
import com.netflix.hollow.test.InMemoryBlobStore;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Before;
import org.junit.Test;

public class HollowProducerIntegrityCheckOnSnapshotCyclesTest {

    private InMemoryBlobStore blobStore;

    @Before
    public void setUp() {
        blobStore = new InMemoryBlobStore();
    }

    static class Counters implements IntegrityCheckListener, PublishListener {
        final AtomicInteger integrityChecks = new AtomicInteger();
        final AtomicInteger snapshotStages = new AtomicInteger();
        final AtomicInteger snapshotPublishes = new AtomicInteger();

        @Override public void onIntegrityCheckStart(long version) {
            integrityChecks.incrementAndGet();
        }
        @Override public void onIntegrityCheckComplete(Status status, HollowProducer.ReadState readState, long version, Duration elapsed) {}

        @Override public void onBlobStage(Status status, HollowProducer.Blob blob, Duration elapsed) {
            if (blob.getType() == HollowProducer.Blob.Type.SNAPSHOT) snapshotStages.incrementAndGet();
        }
        @Override public void onBlobPublish(Status status, HollowProducer.Blob blob, Duration elapsed) {
            if (blob.getType() == HollowProducer.Blob.Type.SNAPSHOT) snapshotPublishes.incrementAndGet();
        }
        @Override public void onNoDeltaAvailable(long version) {}
        @Override public void onPublishStart(long version) {}
        @Override public void onPublishComplete(Status status, long version, Duration elapsed) {}
    }

    private HollowProducer buildProducer(Counters counters, boolean snapshotOnly, int numStatesBetweenSnapshots) {
        return buildProducer(counters, snapshotOnly, numStatesBetweenSnapshots, null);
    }

    private HollowProducer buildProducer(Counters counters, boolean snapshotOnly, int numStatesBetweenSnapshots,
            ExecutorService snapshotPublishExecutor) {
        HollowProducer.Builder<?> b = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .withAnnouncer((HollowProducer.Announcer) v -> {})
                .withNumStatesBetweenSnapshots(numStatesBetweenSnapshots)
                .withListener(counters);
        if (snapshotPublishExecutor != null) {
            b.withSnapshotPublishExecutor(snapshotPublishExecutor);
        }
        if (snapshotOnly) {
            b.checkIntegrityOnSnapshotCyclesOnly();
        }
        return b.build();
    }

    // ---------- default behavior (opt-in off) ----------

    @Test
    public void integrityCheckRunsEveryCycleByDefault() {
        Counters counters = new Counters();
        HollowProducer producer = buildProducer(counters, false, 2);

        int cycles = 5;
        for (int i = 0; i < cycles; i++) {
            final int id = i;
            producer.runCycle(ws -> ws.add(new Rec(id, id)));
        }

        assertEquals(cycles, counters.integrityChecks.get());
        // integrity ON forces a snapshot to be staged every cycle
        assertEquals(cycles, counters.snapshotStages.get());
    }

    // ---------- opt-in: integrity only on snapshot cycles ----------

    @Test
    public void integrityCheckRunsOnlyOnSnapshotCyclesWhenOptedIn() {
        Counters counters = new Counters();
        HollowProducer producer = buildProducer(counters, true, 2);

        int cycles = 6;
        for (int i = 0; i < cycles; i++) {
            final int id = i;
            producer.runCycle(ws -> ws.add(new Rec(id, id)));
        }

        // With numStatesBetweenSnapshots=2, snapshots fall on cycles 1 and 4.
        assertEquals(2, counters.integrityChecks.get());
        assertEquals(counters.snapshotStages.get(), counters.integrityChecks.get());
        assertEquals(counters.snapshotPublishes.get(), counters.snapshotStages.get());
        assertTrue(counters.integrityChecks.get() < cycles);
    }

    @Test
    public void integrityRunsOnFirstCycleInSnapshotOnlyMode() {
        Counters counters = new Counters();
        HollowProducer producer = buildProducer(counters, true, 5);

        producer.runCycle(ws -> ws.add(new Rec(1, 1)));

        assertEquals(1, counters.integrityChecks.get());
        assertEquals(1, counters.snapshotStages.get());
    }

    @Test
    public void zeroStatesBetweenSnapshotsChecksEveryCycleInSnapshotOnlyMode() {
        Counters counters = new Counters();
        HollowProducer producer = buildProducer(counters, true, 0);

        int cycles = 4;
        for (int i = 0; i < cycles; i++) {
            final int id = i;
            producer.runCycle(ws -> ws.add(new Rec(id, id)));
        }

        // Every cycle is a snapshot cycle when numStatesBetweenSnapshots=0.
        assertEquals(cycles, counters.integrityChecks.get());
        assertEquals(cycles, counters.snapshotStages.get());
    }

    @Test
    public void noDeltaCycleSkipsIntegrityInSnapshotOnlyMode() {
        Counters counters = new Counters();
        HollowProducer producer = buildProducer(counters, true, 5);

        producer.runCycle(ws -> ws.add(new Rec(1, 1)));   // snapshot cycle
        producer.runCycle(ws -> ws.add(new Rec(1, 1)));   // no change -> no-delta cycle
        long v3 = producer.runCycle(ws -> ws.add(new Rec(1, 2))); // delta cycle

        // Only the first (snapshot) cycle staged a snapshot / ran integrity.
        assertEquals(1, counters.integrityChecks.get());
        assertEquals(1, counters.snapshotStages.get());

        HollowConsumer consumer = HollowConsumer.withBlobRetriever(blobStore).build();
        consumer.triggerRefreshTo(v3);
        assertValue(consumer, "Rec", 1, 2L);
    }

    @Test
    public void noIntegrityCheckOverridesSnapshotOnly() {
        Counters counters = new Counters();
        HollowProducer producer = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .withAnnouncer((HollowProducer.Announcer) v -> {})
                .withNumStatesBetweenSnapshots(2)
                .withListener(counters)
                .noIntegrityCheck()
                .checkIntegrityOnSnapshotCyclesOnly()
                .build();

        for (int i = 0; i < 4; i++) {
            final int id = i;
            producer.runCycle(ws -> ws.add(new Rec(id, id)));
        }

        assertEquals(0, counters.integrityChecks.get());
    }

    // ---------- equivalence with default mode (snapshot-only must not change consumer-visible data) ----------

    @Test
    public void snapshotOnlyMatchesDefaultForAddsModifiesDeletes() {
        InMemoryBlobStore defStore = new InMemoryBlobStore();
        InMemoryBlobStore snapStore = new InMemoryBlobStore();
        HollowProducer def = producerFor(defStore, false, 3);
        HollowProducer snap = producerFor(snapStore, true, 3);

        HollowProducer.Populator[] seq = new HollowProducer.Populator[] {
            ws -> { ws.add(new Rec(1, 1)); ws.add(new Rec(2, 2)); },
            ws -> { ws.add(new Rec(1, 1)); ws.add(new Rec(2, 20)); ws.add(new Rec(3, 3)); }, // modify 2, add 3
            ws -> { ws.add(new Rec(2, 20)); ws.add(new Rec(3, 3)); },                        // delete 1
            ws -> { ws.add(new Rec(2, 20)); ws.add(new Rec(3, 3)); ws.add(new Rec(4, 4)); }, // add 4
            ws -> { ws.add(new Rec(2, 200)); ws.add(new Rec(4, 4)); }                        // delete 3, modify 2
        };

        long[] vdef = new long[seq.length];
        long[] vsnap = new long[seq.length];
        for (int i = 0; i < seq.length; i++) {
            vdef[i] = def.runCycle(seq[i]);
            vsnap[i] = snap.runCycle(seq[i]);
        }

        HollowConsumer cdef = HollowConsumer.withBlobRetriever(defStore).build();
        HollowConsumer csnap = HollowConsumer.withBlobRetriever(snapStore).build();
        for (int i = 0; i < seq.length; i++) {
            cdef.triggerRefreshTo(vdef[i]);
            csnap.triggerRefreshTo(vsnap[i]);
            assertSameRecData(cdef, csnap);
        }
    }

    @Test
    public void snapshotOnlyMatchesDefaultAcrossNewTypeSchemaChange() {
        InMemoryBlobStore defStore = new InMemoryBlobStore();
        InMemoryBlobStore snapStore = new InMemoryBlobStore();
        HollowProducer def = producerFor(defStore, false, 5);
        HollowProducer snap = producerFor(snapStore, true, 5);

        HollowProducer.Populator c1 = ws -> ws.add(new Rec(1, 10));
        HollowProducer.Populator c2 = ws -> { ws.add(new Rec(1, 10)); ws.add(new Other(1, "n")); };

        def.runCycle(c1);
        long vdef = def.runCycle(c2);
        snap.runCycle(c1);
        long vsnap = snap.runCycle(c2);

        HollowConsumer cdef = HollowConsumer.withBlobRetriever(defStore).build();
        cdef.triggerRefreshTo(vdef);
        HollowConsumer csnap = HollowConsumer.withBlobRetriever(snapStore).build();
        csnap.triggerRefreshTo(vsnap);

        assertValue(cdef, "Rec", 1, 10L);
        assertValue(csnap, "Rec", 1, 10L);

        // Whatever Hollow does with a new type introduced on a delta cycle, snapshot-only mode matches default.
        boolean defHasOther = cdef.getStateEngine().getTypeState("Other") != null;
        boolean snapHasOther = csnap.getStateEngine().getTypeState("Other") != null;
        assertEquals(defHasOther, snapHasOther);
    }

    // ---------- async snapshot publish (RawHollow's configuration) ----------

    @Test
    public void snapshotOnlyModeWithAsyncSnapshotPublishExecutor() throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Counters counters = new Counters();
        HollowProducer producer = buildProducer(counters, true, 2, executor);

        long lastVersion = 0;
        int cycles = 6;
        for (int i = 0; i < cycles; i++) {
            final int id = i;
            lastVersion = producer.runCycle(ws -> ws.add(new Rec(id, id)));
        }

        executor.shutdown();
        assertTrue(executor.awaitTermination(30, TimeUnit.SECONDS));

        assertEquals(counters.snapshotStages.get(), counters.integrityChecks.get());
        assertTrue(counters.integrityChecks.get() < cycles);

        HollowConsumer consumer = HollowConsumer.withBlobRetriever(blobStore).build();
        consumer.triggerRefreshTo(lastVersion);
        assertValue(consumer, "Rec", cycles - 1, cycles - 1);
    }

    // ---------- consumer corner cases ----------

    @Test
    public void freshConsumerBootstrapsToDeltaCycleVersion() {
        Counters counters = new Counters();
        HollowProducer producer = buildProducer(counters, true, 5);

        producer.runCycle(ws -> ws.add(new Rec(1, 1)));               // snapshot cycle
        producer.runCycle(ws -> { ws.add(new Rec(1, 1)); ws.add(new Rec(2, 2)); }); // delta
        long deltaVersion = producer.runCycle(ws -> {                 // delta cycle (bootstrap target)
            ws.add(new Rec(1, 100));
            ws.add(new Rec(2, 2));
            ws.add(new Rec(3, 3));
        });

        // Fresh consumer must find the nearest snapshot and roll deltas forward to a delta-cycle version.
        HollowConsumer consumer = HollowConsumer.withBlobRetriever(blobStore).build();
        consumer.triggerRefreshTo(deltaVersion);

        assertValue(consumer, "Rec", 1, 100L);
        assertValue(consumer, "Rec", 2, 2L);
        assertValue(consumer, "Rec", 3, 3L);
    }

    @Test
    public void consumerFollowsEveryVersionInSnapshotOnlyMode() {
        Counters counters = new Counters();
        HollowProducer producer = buildProducer(counters, true, 2);

        int cycles = 6;
        long[] versions = new long[cycles];
        for (int i = 0; i < cycles; i++) {
            final int id = i;
            versions[i] = producer.runCycle(ws -> ws.add(new Rec(id, id * 10L)));
        }

        HollowConsumer consumer = HollowConsumer.withBlobRetriever(blobStore).build();
        for (int i = 0; i < cycles; i++) {
            consumer.triggerRefreshTo(versions[i]);
            assertEquals(versions[i], consumer.getCurrentVersionId());
            assertValue(consumer, "Rec", i, i * 10L);
        }
    }

    // ---------- helpers ----------

    private HollowProducer producerFor(InMemoryBlobStore store, boolean snapshotOnly, int numStatesBetweenSnapshots) {
        HollowProducer.Builder<?> b = HollowProducer.withPublisher(store)
                .withBlobStager(new HollowInMemoryBlobStager())
                .withAnnouncer((HollowProducer.Announcer) v -> {})
                .withNumStatesBetweenSnapshots(numStatesBetweenSnapshots);
        if (snapshotOnly) {
            b.checkIntegrityOnSnapshotCyclesOnly();
        }
        return b.build();
    }

    private static int ordinalOf(HollowConsumer consumer, String type, int id) {
        if (consumer.getStateEngine().getTypeState(type) == null) {
            return -1;
        }
        return new HollowPrimaryKeyIndex(consumer.getStateEngine(), type, "id").getMatchingOrdinal(id);
    }

    private static void assertValue(HollowConsumer consumer, String type, int id, long expected) {
        int ordinal = ordinalOf(consumer, type, id);
        assertNotEquals(-1, ordinal);
        GenericHollowObject obj = new GenericHollowObject(consumer.getStateEngine(), type, ordinal);
        assertEquals(expected, obj.getLong("value"));
    }

    private static void assertSameRecData(HollowConsumer a, HollowConsumer b) {
        for (int id = 0; id <= 5; id++) {
            int oa = ordinalOf(a, "Rec", id);
            int ob = ordinalOf(b, "Rec", id);
            assertEquals("presence mismatch for Rec " + id, oa == -1, ob == -1);
            if (oa != -1) {
                long va = new GenericHollowObject(a.getStateEngine(), "Rec", oa).getLong("value");
                long vb = new GenericHollowObject(b.getStateEngine(), "Rec", ob).getLong("value");
                assertEquals("value mismatch for Rec " + id, va, vb);
            }
        }
    }

    @HollowPrimaryKey(fields = "id")
    private static class Rec {
        private final int id;
        private final long value;
        Rec(int id, long value) {
            this.id = id;
            this.value = value;
        }
    }

    @HollowPrimaryKey(fields = "id")
    private static class Other {
        private final int id;
        private final String name;
        Other(int id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}

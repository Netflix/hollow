/*
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

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.objects.generic.GenericHollowObject;
import com.netflix.hollow.api.producer.fs.HollowInMemoryBlobStager;
import com.netflix.hollow.core.index.HollowPrimaryKeyIndex;
import com.netflix.hollow.core.write.objectmapper.HollowPrimaryKey;
import com.netflix.hollow.test.InMemoryBlobStore;
import org.junit.Test;

public class HollowProducerCompactSkipTest {

    private static HollowProducer producer(InMemoryBlobStore store, boolean partitionedOrdinalMap, boolean opportunisticCompact) {
        HollowProducer.Builder<?> b = HollowProducer.withPublisher(store)
                .withBlobStager(new HollowInMemoryBlobStager())
                .withAnnouncer((HollowProducer.Announcer) v -> {})
                .withOpportunisticCompact(opportunisticCompact);
        if (partitionedOrdinalMap) {
            b.withPartitionedOrdinalMap(true);
        }
        return b.build();
    }

    // Every cycle produces a delta: add-only cycles exercise the skip path, delete/replace the full path.
    private static HollowProducer.Populator[] mixedChurnSequence() {
        return new HollowProducer.Populator[] {
            ws -> { ws.add(new Rec(1, 10)); ws.add(new Rec(2, 20)); ws.add(new Rec(3, 30)); },
            ws -> { ws.add(new Rec(1, 10)); ws.add(new Rec(2, 20)); ws.add(new Rec(3, 30)); ws.add(new Rec(4, 40)); }, // add-only
            ws -> { ws.add(new Rec(1, 10)); ws.add(new Rec(3, 30)); ws.add(new Rec(4, 40)); },                          // delete 2
            ws -> { ws.add(new Rec(1, 100)); ws.add(new Rec(3, 30)); ws.add(new Rec(4, 40)); },                         // replace 1
            ws -> { ws.add(new Rec(1, 100)); ws.add(new Rec(3, 30)); ws.add(new Rec(4, 40)); ws.add(new Rec(5, 50)); }, // add-only
            ws -> { ws.add(new Rec(3, 30)); ws.add(new Rec(5, 50)); }                                                   // delete 1 & 4
        };
    }

    @Test
    public void addOnlyCyclesAreReadable() {
        InMemoryBlobStore store = new InMemoryBlobStore();
        HollowProducer producer = producer(store, false, true);

        long v1 = producer.runCycle(ws -> ws.add(new Rec(1, 1)));
        long v2 = producer.runCycle(ws -> { ws.add(new Rec(1, 1)); ws.add(new Rec(2, 2)); });
        long v3 = producer.runCycle(ws -> { ws.add(new Rec(1, 1)); ws.add(new Rec(2, 2)); ws.add(new Rec(3, 3)); });

        HollowConsumer consumer = HollowConsumer.withBlobRetriever(store).build();
        consumer.triggerRefreshTo(v1);
        assertValue(consumer, 1, 1L);
        consumer.triggerRefreshTo(v2);
        assertValue(consumer, 1, 1L);
        assertValue(consumer, 2, 2L);
        consumer.triggerRefreshTo(v3);
        assertValue(consumer, 1, 1L);
        assertValue(consumer, 2, 2L);
        assertValue(consumer, 3, 3L);
    }

    @Test
    public void deleteAndReplaceCyclesAreCorrect() {
        InMemoryBlobStore store = new InMemoryBlobStore();
        HollowProducer producer = producer(store, false, true);

        producer.runCycle(ws -> { ws.add(new Rec(1, 10)); ws.add(new Rec(2, 20)); ws.add(new Rec(3, 30)); });
        long vDel = producer.runCycle(ws -> { ws.add(new Rec(1, 10)); ws.add(new Rec(3, 30)); });   // delete 2
        long vRep = producer.runCycle(ws -> { ws.add(new Rec(1, 100)); ws.add(new Rec(3, 30)); });  // replace 1

        HollowConsumer consumer = HollowConsumer.withBlobRetriever(store).build();
        consumer.triggerRefreshTo(vDel);
        assertValue(consumer, 1, 10L);
        assertMissing(consumer, 2);
        assertValue(consumer, 3, 30L);

        consumer.triggerRefreshTo(vRep);
        assertValue(consumer, 1, 100L);   // replaced, no duplicate
        assertMissing(consumer, 2);
        assertValue(consumer, 3, 30L);
    }

    @Test
    public void noOpCyclesDoNotCorruptState() {
        InMemoryBlobStore store = new InMemoryBlobStore();
        HollowProducer producer = producer(store, false, true);

        long v1 = producer.runCycle(ws -> ws.add(new Rec(1, 1)));
        producer.runCycle(ws -> ws.add(new Rec(1, 1)));                          // no-op (unchanged)
        long v3 = producer.runCycle(ws -> { ws.add(new Rec(1, 1)); ws.add(new Rec(2, 2)); });

        HollowConsumer consumer = HollowConsumer.withBlobRetriever(store).build();
        consumer.triggerRefreshTo(v1);
        assertValue(consumer, 1, 1L);
        consumer.triggerRefreshTo(v3);
        assertValue(consumer, 1, 1L);
        assertValue(consumer, 2, 2L);
    }

    @Test
    public void partitionedOrdinalMapMixedChurnIsCorrect() {
        InMemoryBlobStore store = new InMemoryBlobStore();
        HollowProducer producer = producer(store, true, true);

        HollowProducer.Populator[] seq = mixedChurnSequence();
        long last = 0;
        for (HollowProducer.Populator p : seq) {
            last = producer.runCycle(p);
        }

        HollowConsumer consumer = HollowConsumer.withBlobRetriever(store).build();
        consumer.triggerRefreshTo(last);
        assertMissing(consumer, 1);
        assertValue(consumer, 3, 30L);
        assertMissing(consumer, 4);
        assertValue(consumer, 5, 50L);
    }

    @Test
    public void skipOnMatchesSkipOffAcrossMixedChurn() {
        long[] vOn = runSequenceWithSkip(true);
        long[] vOff = runSequenceWithSkip(false);
        assertEquals(vOn.length, vOff.length);

        HollowConsumer cOn = HollowConsumer.withBlobRetriever(onStore).build();
        HollowConsumer cOff = HollowConsumer.withBlobRetriever(offStore).build();
        for (int i = 0; i < vOn.length; i++) {
            cOn.triggerRefreshTo(vOn[i]);
            cOff.triggerRefreshTo(vOff[i]);
            assertSameRecData(cOn, cOff);
        }
    }

    private InMemoryBlobStore onStore;
    private InMemoryBlobStore offStore;

    private long[] runSequenceWithSkip(boolean skip) {
        InMemoryBlobStore store = new InMemoryBlobStore();
        if (skip) {
            onStore = store;
        } else {
            offStore = store;
        }
        HollowProducer producer = producer(store, false, skip);
        HollowProducer.Populator[] seq = mixedChurnSequence();
        long[] versions = new long[seq.length];
        for (int i = 0; i < seq.length; i++) {
            versions[i] = producer.runCycle(seq[i]);
        }
        return versions;
    }

    // ---------- helpers ----------

    private static int ordinalOf(HollowConsumer consumer, int id) {
        if (consumer.getStateEngine().getTypeState("Rec") == null) {
            return -1;
        }
        return new HollowPrimaryKeyIndex(consumer.getStateEngine(), "Rec", "id").getMatchingOrdinal(id);
    }

    private static void assertValue(HollowConsumer consumer, int id, long expected) {
        int ordinal = ordinalOf(consumer, id);
        assertNotEquals("record " + id + " should be present", -1, ordinal);
        assertEquals(expected, new GenericHollowObject(consumer.getStateEngine(), "Rec", ordinal).getLong("value"));
    }

    private static void assertMissing(HollowConsumer consumer, int id) {
        assertEquals("record " + id + " should be absent", -1, ordinalOf(consumer, id));
    }

    private static void assertSameRecData(HollowConsumer a, HollowConsumer b) {
        for (int id = 0; id <= 6; id++) {
            int oa = ordinalOf(a, id);
            int ob = ordinalOf(b, id);
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
}

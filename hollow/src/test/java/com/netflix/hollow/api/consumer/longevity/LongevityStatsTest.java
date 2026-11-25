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
package com.netflix.hollow.api.consumer.longevity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.HollowConsumer.HeldVersionsConfig;
import com.netflix.hollow.api.consumer.HollowConsumer.ObjectLongevity;
import com.netflix.hollow.core.HollowConstants;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;

public class LongevityStatsTest {

    private final HeldVersionsFixture fixture = new HeldVersionsFixture();
    private final AtomicInteger transitions = new AtomicInteger();

    @Test
    public void nothingIsHeldBeforeTheFirstHold() {
        long v0 = fixture.cycle();
        HollowConsumer consumer = fixture.heldConsumer();
        assertNothingHeld(consumer.getLongevityStats()); // not loaded yet
        assertFalse(consumer.getLongevityStats().isDirectLookupActive());

        consumer.triggerRefreshTo(v0);

        assertNothingHeld(consumer.getLongevityStats());
        assertTrue(consumer.getLongevityStats().isDirectLookupActive());
    }

    @Test
    public void theLeadCountsEveryDeltaPastTheOldestOpenHold() {
        List<Long> versions = fixture.cycles(6);
        HollowConsumer consumer = fixture.heldConsumer();
        AtomicInteger leadAtRefresh = new AtomicInteger(-1);
        consumer.addRefreshListener(new HollowConsumer.AbstractRefreshListener() {
            @Override
            public void refreshSuccessful(long beforeVersion, long afterVersion, long requestedVersion) {
                leadAtRefresh.set(consumer.getLongevityStats().getDeltasPastOldestHold());
            }
        });
        consumer.triggerRefreshTo(versions.get(0));
        HollowVersionHold served = consumer.holdVersion(versions.get(0));

        for (int i = 1; i <= 4; i++) {
            consumer.triggerRefreshTo(versions.get(i)); // a background refresh
            LongevityStats stats = consumer.getLongevityStats();
            assertEquals(i, stats.getDeltasPastOldestHold());
            assertEquals(i, leadAtRefresh.get());
            assertEquals((long) versions.get(0), stats.getOldestHoldVersion());
            assertEquals(1, stats.getOpenHolds());
        }

        HollowVersionHold next = consumer.holdVersion(versions.get(2));
        assertEquals(2, consumer.getLongevityStats().getOpenHolds());
        served.close(); // the oldest open hold is the next one at once
        assertEquals((long) versions.get(2), consumer.getLongevityStats().getOldestHoldVersion());
        assertEquals(2, consumer.getLongevityStats().getDeltasPastOldestHold());
        consumer.triggerRefreshTo(versions.get(3)); // a reverse delta counts too
        assertEquals(3, consumer.getLongevityStats().getDeltasPastOldestHold());
        assertEquals(1, consumer.getLongevityStats().getOpenHolds());

        next.close();
        assertEquals(0, consumer.getLongevityStats().getOpenHolds());
        assertEquals(HollowConstants.VERSION_NONE, consumer.getLongevityStats().getOldestHoldVersion());
        assertEquals(0, consumer.getLongevityStats().getDeltasPastOldestHold());
    }

    @Test
    public void copiesAreCountedWhileAHoldKeepsThem() {
        List<Long> versions = fixture.cycles(6);
        HollowConsumer consumer = fixture.heldConsumer();
        consumer.triggerRefreshTo(versions.get(0));
        HollowVersionHold hold = consumer.holdVersion(versions.get(0));

        consumer.triggerRefreshTo(versions.get(1));
        LongevityStats one = consumer.getLongevityStats();
        assertTrue(one.getRetainedCopyBytes() > 0);
        assertEquals(removedOrdinals(versions.subList(0, 2)), one.getDirectLookupOrdinals());
        consumer.triggerRefreshTo(versions.get(3));
        LongevityStats three = consumer.getLongevityStats();
        assertTrue(three.getRetainedCopyBytes() > one.getRetainedCopyBytes());
        assertEquals(removedOrdinals(versions.subList(0, 4)), three.getDirectLookupOrdinals());

        hold.close();
        consumer.triggerRefreshTo(versions.get(4)); // the closed hold still reads its copies
        assertEquals(three.getRetainedCopyBytes(), consumer.getLongevityStats().getRetainedCopyBytes());
        assertEquals(three.getDirectLookupOrdinals(), consumer.getLongevityStats().getDirectLookupOrdinals());
        consumer.triggerRefreshTo(versions.get(5)); // the second delta after the close releases it

        assertNothingHeld(consumer.getLongevityStats());
    }

    @Test
    public void directLookupIsReportedAsTheLastRefreshReadItsGuard() {
        List<Long> versions = fixture.cycles(4);
        AtomicBoolean directLookup = new AtomicBoolean(true);
        HollowConsumer consumer = fixture.heldConsumer(new HeldVersionsConfig() {
            @Override
            public boolean enableDirectLookup() {
                return directLookup.get();
            }
        });
        consumer.triggerRefreshTo(versions.get(0));
        consumer.holdVersion(versions.get(0));
        consumer.triggerRefreshTo(versions.get(2));

        directLookup.set(false);
        assertTrue(consumer.getLongevityStats().isDirectLookupActive()); // read at the next refresh
        consumer.triggerRefreshTo(versions.get(2)); // which may apply nothing
        LongevityStats off = consumer.getLongevityStats();
        assertFalse(off.isDirectLookupActive());
        assertEquals(0, off.getDirectLookupOrdinals());
        assertTrue(off.getRetainedCopyBytes() > 0); // the chain keeps the copies

        directLookup.set(true);
        consumer.triggerRefreshTo(versions.get(3)); // indexes the deltas from this refresh on
        LongevityStats on = consumer.getLongevityStats();
        assertTrue(on.isDirectLookupActive());
        assertEquals(removedOrdinals(versions.subList(2, 4)), on.getDirectLookupOrdinals());
    }

    @Test
    public void openHoldsAreCountedOnEveryStateEngine() {
        List<Long> versions = fixture.cycles(4);
        HollowConsumer consumer = fixture.heldConsumer();
        consumer.triggerRefreshTo(versions.get(0));
        HollowVersionHold replaced = consumer.holdVersion(versions.get(0));
        consumer.forceDoubleSnapshotNextUpdate();
        HollowVersionHold current = consumer.holdVersion(versions.get(2)); // takes the double snapshot
        consumer.triggerRefreshTo(versions.get(3));

        LongevityStats stats = consumer.getLongevityStats();
        assertEquals(2, stats.getOpenHolds());
        assertEquals((long) versions.get(2), stats.getOldestHoldVersion()); // the lead is the current engine's
        assertEquals(1, stats.getDeltasPastOldestHold());

        replaced.close();
        replaced.close();
        assertEquals(1, consumer.getLongevityStats().getOpenHolds());
        current.close();
        assertEquals(0, consumer.getLongevityStats().getOpenHolds());
    }

    @Test
    public void everyReadThroughAReleasedHoldIsCounted() {
        List<Long> versions = fixture.cycles(4);
        HollowConsumer consumer = fixture.heldConsumer();
        consumer.triggerRefreshTo(versions.get(0));
        HollowVersionHold hold = consumer.holdVersion(versions.get(0));
        HollowObjectTypeDataAccess movies =
                (HollowObjectTypeDataAccess) hold.getDataAccess().getTypeDataAccess("Movie");
        int ordinal = fixture.reference(versions.get(0)).getTypeState("Movie").getPopulatedOrdinals().nextSetBit(0);
        int id = movies.getSchema().getPosition("id");

        hold.close();
        consumer.triggerRefreshTo(versions.get(1));
        movies.readLong(ordinal, id); // still exact
        assertEquals(0, consumer.getLongevityStats().getReadsAfterRelease());
        consumer.triggerRefreshTo(versions.get(2)); // released

        for (int i = 0; i < 3; i++)
            movies.readLong(ordinal, id);
        assertEquals(3, consumer.getLongevityStats().getReadsAfterRelease());

        consumer.forceDoubleSnapshotNextUpdate(); // counted for the consumer, not the state engine
        consumer.holdVersion(versions.get(3));
        movies.readLong(ordinal, id);
        assertEquals(4, consumer.getLongevityStats().getReadsAfterRelease());
    }

    @Test
    public void aHoldReportsHowFarItsStateEngineIsPastIt() {
        List<Long> versions = fixture.cycles(6);
        HollowConsumer consumer = fixture.heldConsumer();
        consumer.triggerRefreshTo(versions.get(0));
        HollowVersionHold served = consumer.holdVersion(versions.get(0));
        assertEquals(0, served.getDeltasBehindEngine());

        consumer.triggerRefreshTo(versions.get(3));
        HollowVersionHold next = consumer.holdVersion(versions.get(2));
        assertEquals(3, served.getDeltasBehindEngine());
        assertEquals(1, next.getDeltasBehindEngine());
        consumer.triggerRefreshTo(versions.get(1)); // two reverse deltas
        assertEquals(5, served.getDeltasBehindEngine());
        assertEquals(3, next.getDeltasBehindEngine());

        consumer.forceDoubleSnapshotNextUpdate();
        HollowVersionHold replacing = consumer.holdVersion(versions.get(4));
        consumer.triggerRefreshTo(versions.get(5));
        assertEquals(5, served.getDeltasBehindEngine()); // a replaced state engine applies no more deltas
        assertEquals(1, replacing.getDeltasBehindEngine());
    }

    @Test
    public void aHoldReportsTheTransitionsHoldingItApplied() {
        List<Long> versions = fixture.cycles(6);
        HollowConsumer consumer = fixture.consumer()
                .withObjectLongevity(ObjectLongevity.held())
                .withRefreshListener(new HollowConsumer.AbstractRefreshListener() {
                    @Override
                    public void blobLoaded(HollowConsumer.Blob transition) {
                        transitions.incrementAndGet();
                    }
                })
                .build();

        assertTransitions(1, consumer, versions.get(0)); // loads the consumer
        assertTransitions(3, consumer, versions.get(3));
        assertTransitions(0, consumer, versions.get(1)); // from copies
        assertTransitions(0, consumer, versions.get(3)); // the engine's
        consumer.forceDoubleSnapshotNextUpdate();
        assertTransitions(1, consumer, versions.get(5));
    }

    @Test
    public void statsNeedHeldMode() {
        for (HollowConsumer consumer : Arrays.asList(fixture.consumer().build(),
                fixture.consumer().withObjectLongevity(ObjectLongevity.off()).build())) {
            try {
                consumer.getLongevityStats();
                fail("stats outside held mode");
            } catch (IllegalStateException expected) {
            }
        }
    }

    private void assertTransitions(int expected, HollowConsumer consumer, long version) {
        transitions.set(0);
        HollowVersionHold hold = consumer.holdVersion(version);
        assertEquals(expected, transitions.get());
        assertEquals(expected, hold.getTransitionsApplied());
    }

    private static void assertNothingHeld(LongevityStats stats) {
        assertEquals(0, stats.getRetainedCopyBytes());
        assertEquals(0, stats.getDirectLookupOrdinals());
        assertEquals(0, stats.getOpenHolds());
        assertEquals(HollowConstants.VERSION_NONE, stats.getOldestHoldVersion());
        assertEquals(0, stats.getDeltasPastOldestHold());
        assertEquals(0, stats.getReadsAfterRelease());
    }

    /** The ordinals, across types, that the deltas along {@code path} removed: those direct lookup maps to copies. */
    private int removedOrdinals(List<Long> path) {
        Map<String, BitSet> removed = new HashMap<>();
        for (int i = 1; i < path.size(); i++) {
            HollowReadStateEngine before = fixture.reference(path.get(i - 1));
            HollowReadStateEngine after = fixture.reference(path.get(i));
            for (HollowTypeReadState type : before.getTypeStates()) {
                String name = type.getSchema().getName();
                BitSet gone = (BitSet) type.getPopulatedOrdinals().clone();
                gone.andNot(after.getTypeState(name).getPopulatedOrdinals());
                removed.computeIfAbsent(name, t -> new BitSet()).or(gone);
            }
        }
        return removed.values().stream().mapToInt(BitSet::cardinality).sum();
    }
}

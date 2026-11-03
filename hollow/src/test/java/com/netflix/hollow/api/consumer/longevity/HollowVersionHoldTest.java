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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.HollowConsumer.ObjectLongevity;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.core.HollowConstants;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.read.engine.PopulatedOrdinalListener;
import com.netflix.hollow.test.generated.AwardsAPI;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicLongArray;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class HollowVersionHoldTest {

    private final HeldVersionsFixture fixture = new HeldVersionsFixture();
    private final AtomicInteger transitions = new AtomicInteger();
    private final List<LogRecord> problems = new CopyOnWriteArrayList<>(); // warnings and errors
    private final Logger clientLogger = Logger.getLogger("com.netflix.hollow.api.client");
    private final Handler errorCapture = new Handler() {
        @Override
        public void publish(LogRecord record) {
            if (record.getLevel().intValue() >= Level.WARNING.intValue())
                problems.add(record);
        }

        @Override
        public void flush() {
        }

        @Override
        public void close() {
        }
    };

    @Before
    public void captureErrors() {
        clientLogger.addHandler(errorCapture);
    }

    @After
    public void releaseErrors() {
        clientLogger.removeHandler(errorCapture);
    }

    @Test
    public void everyHeldVersionReadsExactlyAfterEveryDelta() {
        List<Long> versions = fixture.cycles(10);
        HollowConsumer consumer = fixture.heldConsumer();
        List<HollowVersionHold> holds = new ArrayList<>();

        for (long version : versions) {
            consumer.triggerRefreshTo(version);
            holds.add(consumer.holdVersion(version));
            for (HollowVersionHold hold : holds)
                fixture.assertReads(hold.getVersion(), hold.getDataAccess());
        }
    }

    @Test
    public void aVersionAppliedSinceTheOldestOpenHoldIsHeldWithoutTransitions() {
        List<Long> versions = fixture.cycles(7);
        HollowConsumer consumer = countingHeldConsumer();
        consumer.triggerRefreshTo(versions.get(0));
        HollowVersionHold oldest = consumer.holdVersion(versions.get(0));
        consumer.triggerRefreshTo(versions.get(4));
        transitions.set(0);

        HollowVersionHold behind = consumer.holdVersion(versions.get(2));

        assertEquals(0, transitions.get());
        assertEquals((long) versions.get(4), consumer.getCurrentVersionId());
        assertEquals((long) versions.get(2), behind.getVersion());
        consumer.triggerRefreshTo(versions.get(6));
        fixture.assertReads(versions.get(2), behind.getDataAccess());
        fixture.assertReads(versions.get(0), oldest.getDataAccess());
    }

    @Test
    public void aLaterVersionIsRefreshedToOnTheCallingThread() {
        List<Long> versions = fixture.cycles(4);
        HollowConsumer consumer = countingHeldConsumer();
        consumer.triggerRefreshTo(versions.get(0));
        HollowVersionHold served = consumer.holdVersion(versions.get(0));
        transitions.set(0);

        HollowVersionHold next = consumer.holdVersion(versions.get(3));

        assertEquals(3, transitions.get());
        assertEquals((long) versions.get(3), consumer.getCurrentVersionId());
        fixture.assertReads(versions.get(3), next.getDataAccess());
        fixture.assertReads(versions.get(0), served.getDataAccess());
    }

    @Test
    public void versionsAppliedWhileNoHoldIsOpenAreNotRetained() {
        List<Long> versions = fixture.cycles(3);
        HollowConsumer consumer = countingHeldConsumer();
        consumer.triggerRefreshTo(versions.get(0));
        consumer.triggerRefreshTo(versions.get(2));
        transitions.set(0);

        HollowVersionHold hold = consumer.holdVersion(versions.get(1));

        assertEquals(1, transitions.get()); // a reverse delta: nothing kept the version
        assertEquals((long) versions.get(1), consumer.getCurrentVersionId());
        fixture.assertReads(versions.get(1), hold.getDataAccess());
    }

    @Test
    public void reverseDeltasKeepOpenHoldsExact() {
        List<Long> versions = fixture.cycles(7);
        HollowConsumer consumer = fixture.heldConsumer();
        consumer.triggerRefreshTo(versions.get(4));
        HollowVersionHold served = consumer.holdVersion(versions.get(4));

        HollowVersionHold rollback = consumer.holdVersion(versions.get(1));

        assertEquals((long) versions.get(1), consumer.getCurrentVersionId());
        fixture.assertReads(versions.get(4), served.getDataAccess());
        fixture.assertReads(versions.get(1), rollback.getDataAccess());
        consumer.triggerRefreshTo(versions.get(6));
        fixture.assertReads(versions.get(4), served.getDataAccess());
        fixture.assertReads(versions.get(1), rollback.getDataAccess());
    }

    @Test
    public void aVersionPassedBeforeAReverseWalkIsHeldWithoutTransitions() {
        List<Long> versions = fixture.cycles(6);
        HollowConsumer consumer = countingHeldConsumer();
        consumer.triggerRefreshTo(versions.get(2));
        HollowVersionHold oldest = consumer.holdVersion(versions.get(2));
        consumer.triggerRefreshTo(versions.get(5));
        HollowVersionHold rollback = consumer.holdVersion(versions.get(0)); // reverse deltas back past the oldest hold
        transitions.set(0);

        HollowVersionHold passed = consumer.holdVersion(versions.get(4));

        assertEquals(0, transitions.get());
        assertEquals((long) versions.get(0), consumer.getCurrentVersionId());
        fixture.assertReads(versions.get(4), passed.getDataAccess());
        fixture.assertReads(versions.get(2), oldest.getDataAccess());
        fixture.assertReads(versions.get(0), rollback.getDataAccess());
    }

    @Test
    public void aClosedHoldReadsExactlyUntilTheSecondDeltaAfterTheClose() {
        List<Long> versions = fixture.cycles(6);
        HollowConsumer consumer = fixture.heldConsumer();
        consumer.triggerRefreshTo(versions.get(0));
        HollowVersionHold served = consumer.holdVersion(versions.get(0));
        consumer.triggerRefreshTo(versions.get(3));
        HollowVersionHold next = consumer.holdVersion(versions.get(2));

        served.close();
        fixture.assertReads(versions.get(0), served.getDataAccess());
        consumer.triggerRefreshTo(versions.get(4));
        fixture.assertReads(versions.get(0), served.getDataAccess());
        fixture.assertReads(versions.get(0), served.getAPI(HollowAPI.class).getDataAccess());
        assertTrue(problems.isEmpty());

        consumer.triggerRefreshTo(versions.get(5));

        fixture.assertReads(versions.get(5), served.getDataAccess()); // the engine, not the held version
        fixture.assertReads(versions.get(5), served.getDataAccess());
        assertEquals(1, problems.size());
        assertEquals(Level.SEVERE, problems.get(0).getLevel());
        assertNotNull(problems.get(0).getThrown());
        fixture.assertReads(versions.get(2), next.getDataAccess());
    }

    @Test
    public void closingTheOnlyHoldLeavesTheNextDeltasRemovalsReadableInTheEngine() {
        List<Long> versions = fixture.cycles(3);
        HollowConsumer consumer = fixture.heldConsumer();
        consumer.triggerRefreshTo(versions.get(0));
        HollowVersionHold hold = consumer.holdVersion(versions.get(0));

        hold.close();
        consumer.triggerRefreshTo(versions.get(1)); // no open hold, so no copies: its removals are still in the engine

        fixture.assertReads(versions.get(0), hold.getDataAccess());
        consumer.triggerRefreshTo(versions.get(2));
        fixture.assertReads(versions.get(2), hold.getDataAccess());
        assertEquals(1, problems.size());
        assertEquals(Level.SEVERE, problems.get(0).getLevel());
    }

    @Test
    public void aClosedHoldNoLongerWidensWhatIsHeldWithoutTransitions() {
        List<Long> versions = fixture.cycles(4);
        HollowConsumer consumer = countingHeldConsumer();
        consumer.triggerRefreshTo(versions.get(0));
        HollowVersionHold served = consumer.holdVersion(versions.get(0));
        consumer.triggerRefreshTo(versions.get(3));
        HollowVersionHold next = consumer.holdVersion(versions.get(2));
        served.close();
        transitions.set(0);

        HollowVersionHold older = consumer.holdVersion(versions.get(1));

        assertEquals(2, transitions.get());
        assertEquals((long) versions.get(1), consumer.getCurrentVersionId());
        fixture.assertReads(versions.get(1), older.getDataAccess());
        fixture.assertReads(versions.get(2), next.getDataAccess());
    }

    @Test
    public void aHoldKeepsItsCopiesWhenAnOlderHoldIsReleased() {
        List<Long> versions = fixture.cycles(7);
        HollowConsumer consumer = fixture.heldConsumer();
        consumer.triggerRefreshTo(versions.get(0));
        HollowVersionHold served = consumer.holdVersion(versions.get(0));
        consumer.triggerRefreshTo(versions.get(3));
        HollowVersionHold next = consumer.holdVersion(versions.get(1));

        served.close();
        for (int i = 4; i < versions.size(); i++)
            consumer.triggerRefreshTo(versions.get(i));

        fixture.assertReads(versions.get(1), next.getDataAccess());
        assertTrue(problems.isEmpty());
    }

    @Test
    public void closeIsIdempotent() {
        List<Long> versions = fixture.cycles(4);
        HollowConsumer consumer = fixture.heldConsumer();
        consumer.triggerRefreshTo(versions.get(0));
        HollowVersionHold hold = consumer.holdVersion(versions.get(0));

        hold.close();
        consumer.triggerRefreshTo(versions.get(1));
        hold.close();

        fixture.assertReads(versions.get(0), hold.getDataAccess());
    }

    @Test
    public void latestAndNoneAreNotVersions() {
        long v1 = fixture.cycle();
        HollowConsumer consumer = fixture.heldConsumer();
        consumer.triggerRefreshTo(v1);

        for (long version : new long[] {HollowConstants.VERSION_LATEST, HollowConstants.VERSION_NONE}) {
            try {
                consumer.holdVersion(version);
                fail();
            } catch (IllegalArgumentException expected) {
            }
        }
        assertEquals(v1, consumer.getCurrentVersionId());
    }

    @Test
    public void aVersionNoTransitionLandsOnIsNotReachable() {
        List<Long> versions = fixture.cycles(3);
        HollowConsumer consumer = fixture.heldConsumer();
        consumer.triggerRefreshTo(versions.get(0));

        try {
            consumer.holdVersion(versions.get(2) - 1);
            fail();
        } catch (VersionNotReachableException expected) {
        }
        assertEquals((long) versions.get(1), consumer.getCurrentVersionId());
    }

    @Test
    public void theFirstHoldMayLoadTheConsumer() {
        long v1 = fixture.cycle();
        HollowConsumer consumer = fixture.heldConsumer();

        HollowVersionHold hold = consumer.holdVersion(v1);

        assertEquals(v1, consumer.getCurrentVersionId());
        fixture.assertReads(v1, hold.getDataAccess());
    }

    @Test
    public void aHoldsApiIsOfTheRequestedClassOverItsDataAccess() {
        long v1 = fixture.cycle();
        HollowConsumer consumer = fixture.heldConsumer();
        HollowVersionHold hold = consumer.holdVersion(v1);

        AwardsAPI api = hold.getAPI(AwardsAPI.class);

        assertSame(hold.getDataAccess(), api.getDataAccess());
        assertSame(api, hold.getAPI(AwardsAPI.class));
        assertSame(hold.getDataAccess(), hold.getAPI(HollowAPI.class).getDataAccess());
    }

    @Test
    public void aDeltaWhoseCopiesFailIsTheLastTheEngineApplies() {
        List<Long> versions = fixture.cycles(3);
        HollowConsumer consumer = fixture.consumer()
                .withObjectLongevity(ObjectLongevity.held())
                .withDoubleSnapshotConfig(new NoDoubleSnapshots())
                .build();
        consumer.triggerRefreshTo(versions.get(0));
        HollowVersionHold hold = consumer.holdVersion(versions.get(0));
        breakCopies(consumer);

        assertRefreshFails(consumer, versions.get(1));
        assertRefreshFails(consumer, versions.get(2)); // the next delta could reuse ordinals nothing copied

        assertStuckLoggedOnce(Level.SEVERE, "double snapshots are disallowed");
        assertEquals((long) versions.get(1), consumer.getCurrentVersionId());
        fixture.assertReads(versions.get(0), hold.getDataAccess());
        fixture.assertReads(versions.get(1), consumer.holdVersion(versions.get(1)).getDataAccess());
    }

    @Test
    public void aDoubleSnapshotFollowsADeltaWhoseCopiesFailed() {
        List<Long> versions = fixture.cycles(3);
        HollowConsumer consumer = fixture.heldConsumer();
        consumer.triggerRefreshTo(versions.get(0));
        HollowVersionHold hold = consumer.holdVersion(versions.get(0));
        breakCopies(consumer);
        assertRefreshFails(consumer, versions.get(1));
        assertStuckLoggedOnce(Level.WARNING, "next double snapshot");

        consumer.triggerRefreshTo(versions.get(2));

        assertEquals((long) versions.get(2), consumer.getCurrentVersionId());
        fixture.assertReads(versions.get(0), hold.getDataAccess());
        fixture.assertReads(versions.get(2), consumer.holdVersion(versions.get(2)).getDataAccess());
    }

    @Test(timeout = 120_000)
    public void holdsReadExactlyWhileDeltasHoldsAndReleasesRace() throws Exception {
        List<Long> versions = fixture.cycles(24);
        versions.forEach(fixture::reference);
        AtomicLong transitionEvents = new AtomicLong();
        HollowConsumer consumer = fixture.consumer()
                .withBlobRetriever(countingTransitions(fixture.blobStore, transitionEvents))
                .withRefreshListener(new HollowConsumer.AbstractRefreshListener() {
                    @Override
                    public void deltaApplied(HollowAPI api, HollowReadStateEngine stateEngine, long version) {
                        transitionEvents.incrementAndGet();
                    }

                    @Override
                    public void snapshotApplied(HollowAPI api, HollowReadStateEngine stateEngine, long version) {
                        transitionEvents.incrementAndGet();
                    }
                })
                .withObjectLongevity(ObjectLongevity.held())
                .build();
        consumer.triggerRefreshTo(versions.get(0));
        CopyOnWriteArrayList<HollowVersionHold> readable = new CopyOnWriteArrayList<>();
        readable.add(consumer.holdVersion(versions.get(0)));

        int readerCount = 6;
        AtomicBoolean done = new AtomicBoolean();
        AtomicLongArray passes = new AtomicLongArray(readerCount);
        ConcurrentLinkedQueue<String> failures = new ConcurrentLinkedQueue<>();
        ExecutorService threads = Executors.newFixedThreadPool(readerCount + 1);
        for (int r = 0; r < readerCount; r++) {
            int reader = r;
            threads.submit(() -> {
                while (!done.get()) {
                    for (HollowVersionHold hold : readable) {
                        if (fixture.mismatches(hold.getVersion(), hold.getDataAccess(), transitionEvents) > 0)
                            failures.add("reader read " + hold.getVersion());
                    }
                    passes.incrementAndGet(reader);
                }
            });
        }
        threads.submit(() -> { // holds and releases versions at random, refreshing when one isn't retained
            Random random = new Random(3);
            while (!done.get()) {
                long version = versions.get(random.nextInt(versions.size()));
                try (HollowVersionHold hold = consumer.holdVersion(version)) {
                    if (fixture.mismatches(version, hold.getDataAccess(), transitionEvents) > 0)
                        failures.add("holder read " + version);
                }
            }
        });

        List<Integer> path = new ArrayList<>(); // forward, back by reverse deltas, and forward again
        for (int round = 0; round < 3; round++) {
            for (int i = 1; i < versions.size(); i++)
                path.add(round % 2 == 0 ? i : versions.size() - 1 - i);
        }
        Random random = new Random(5);
        for (int step = 0; step < path.size(); step++) {
            consumer.triggerRefreshTo(versions.get(path.get(step)));
            readable.add(consumer.holdVersion(versions.get(path.get(Math.max(0, step - random.nextInt(3))))));
            if (readable.size() > 3) {
                HollowVersionHold released = readable.remove(random.nextInt(readable.size()));
                awaitFullPasses(passes, done);
                released.close();
            }
        }
        done.set(true);
        threads.shutdown();
        assertTrue(threads.awaitTermination(60, TimeUnit.SECONDS));
        assertTrue(failures.toString(), failures.isEmpty());
    }

    private HollowConsumer countingHeldConsumer() {
        return fixture.consumer()
                .withObjectLongevity(ObjectLongevity.held())
                .withRefreshListener(new HollowConsumer.AbstractRefreshListener() {
                    @Override
                    public void blobLoaded(HollowConsumer.Blob transition) {
                        transitions.incrementAndGet();
                    }
                })
                .build();
    }

    /** Counts each transition as its blob is opened; a listener counts it again once holds read its copies. */
    private static HollowConsumer.BlobRetriever countingTransitions(HollowConsumer.BlobRetriever retriever,
            AtomicLong transitionEvents) {
        return new HollowConsumer.BlobRetriever() {
            @Override
            public HollowConsumer.Blob retrieveSnapshotBlob(long desiredVersion) {
                return counting(retriever.retrieveSnapshotBlob(desiredVersion));
            }

            @Override
            public HollowConsumer.Blob retrieveDeltaBlob(long currentVersion) {
                return counting(retriever.retrieveDeltaBlob(currentVersion));
            }

            @Override
            public HollowConsumer.Blob retrieveReverseDeltaBlob(long currentVersion) {
                return counting(retriever.retrieveReverseDeltaBlob(currentVersion));
            }

            private HollowConsumer.Blob counting(HollowConsumer.Blob blob) {
                if (blob == null)
                    return null;
                return new HollowConsumer.Blob(blob.getFromVersion(), blob.getToVersion()) {
                    @Override
                    public InputStream getInputStream() throws IOException {
                        transitionEvents.incrementAndGet();
                        return blob.getInputStream();
                    }
                };
            }
        };
    }

    /** Removes a type's populated-ordinal listener, which building a delta's copies needs. */
    private static void breakCopies(HollowConsumer consumer) {
        HollowTypeReadState typeState = consumer.getStateEngine().getTypeState("Movie");
        typeState.removeListener(typeState.getListener(PopulatedOrdinalListener.class));
    }

    /** A warning when a double snapshot recovers the consumer, an error when it is stuck. */
    private void assertStuckLoggedOnce(Level level, String recovery) {
        assertEquals(1, problems.size());
        LogRecord record = problems.get(0);
        assertEquals(level, record.getLevel());
        assertTrue(record.getMessage(), record.getMessage().contains("applies no more deltas"));
        assertTrue(record.getMessage(), record.getMessage().contains(recovery));
        assertNotNull(record.getThrown()); // the copy failure
    }

    private static void assertRefreshFails(HollowConsumer consumer, long version) {
        try {
            consumer.triggerRefreshTo(version);
            fail("refresh to " + version);
        } catch (RuntimeException expected) {
        }
    }

    /** Waits until every reader has finished the pass it was in, so none still reads a hold removed before the call. */
    private static void awaitFullPasses(AtomicLongArray passes, AtomicBoolean done) throws InterruptedException {
        long[] started = new long[passes.length()];
        for (int i = 0; i < started.length; i++)
            started[i] = passes.get(i);
        for (int i = 0; i < started.length; i++) {
            while (passes.get(i) <= started[i] && !done.get())
                Thread.sleep(1);
        }
    }

    private static final class NoDoubleSnapshots implements HollowConsumer.DoubleSnapshotConfig {
        @Override
        public boolean allowDoubleSnapshot() {
            return false;
        }

        @Override
        public int maxDeltasBeforeDoubleSnapshot() {
            return 32;
        }
    }
}

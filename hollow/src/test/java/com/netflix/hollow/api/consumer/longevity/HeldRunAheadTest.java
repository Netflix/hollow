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

import static com.netflix.hollow.core.HollowStateEngine.HEADER_TAG_SCHEMA_HASH;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.HollowConsumer.ObjectLongevity;
import com.netflix.hollow.core.HollowConstants;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.test.consumer.TestAnnouncementWatcher;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;

/**
 * Background refreshes: in held mode with a hold open, every refresh that isn't a hold request runs under Hollow's
 * normal plan, except that it never applies a snapshot, which it leaves to the next hold, and forces none when it fails
 * before applying anything. It stops at the lead limit.
 */
public class HeldRunAheadTest {

    private final HeldVersionsFixture fixture = new HeldVersionsFixture();
    private final Blobs blobs = new Blobs(fixture.blobStore);
    private final Callbacks callbacks = new Callbacks();

    @Test
    public void aRefreshBehindTheEngineMovesItBackByReverseDeltas() {
        long[] v = cycles(5);
        HollowConsumer consumer = consumer().build();
        HollowVersionHold served = serve(consumer, v[4]);
        HollowReadStateEngine engine = consumer.getStateEngine();

        consumer.triggerRefreshTo(v[1]);

        assertEquals(v[1], consumer.getCurrentVersionId());
        assertSame(engine, consumer.getStateEngine());
        callbacks.assertEvents("detected " + v[1], "started " + v[4] + " " + v[1],
                "successful " + v[4] + " " + v[1] + " " + v[1]);
        fixture.assertReads(v[4], served.getDataAccess());
    }

    @Test
    public void aRefreshPast32ReverseDeltasMovesTheEngineBackWithoutASnapshot() {
        long[] v = cycles(35);
        HollowConsumer consumer = consumer().build();
        HollowVersionHold served = serve(consumer, v[34]);
        HollowReadStateEngine engine = consumer.getStateEngine();

        consumer.triggerRefreshTo(v[1]);
        assertEquals(v[2], consumer.getCurrentVersionId()); // the rest at the next refresh
        consumer.triggerRefreshTo(v[1]);

        assertEquals(v[1], consumer.getCurrentVersionId());
        assertSame(engine, consumer.getStateEngine());
        fixture.assertReads(v[34], served.getDataAccess());
    }

    @Test
    public void refreshesBeforeTheFirstHoldAndAfterTheLastIsClosedTakeSnapshotsAsToday() {
        long[] v = cycles(4);
        HollowConsumer consumer = consumer().build();
        consumer.triggerRefreshTo(v[0]);
        HollowReadStateEngine first = consumer.getStateEngine();
        consumer.forceDoubleSnapshotNextUpdate();
        consumer.triggerRefreshTo(v[1]);
        HollowReadStateEngine second = consumer.getStateEngine();
        assertNotSame(first, second);

        HollowVersionHold hold = consumer.holdVersion(v[1]);
        consumer.forceDoubleSnapshotNextUpdate();
        assertRefreshFails(consumer, v[2]); // left to the next hold
        assertSame(second, consumer.getStateEngine());
        assertEquals(v[1], consumer.getCurrentVersionId());

        hold.close();
        consumer.triggerRefreshTo(v[3]);
        assertNotSame(second, consumer.getStateEngine());
        assertEquals(v[3], consumer.getCurrentVersionId());
    }

    @Test
    public void announcedRefreshesLeaveAForcedDoubleSnapshotToTheNextHold() throws InterruptedException {
        long[] v = cycles(3);
        TestAnnouncementWatcher watcher = new TestAnnouncementWatcher().setLatestVersion(v[0]);
        HollowConsumer consumer = consumer().withAnnouncementWatcher(watcher).build();
        consumer.triggerRefresh();
        HollowVersionHold served = consumer.holdVersion(v[0]);
        HollowReadStateEngine engine = consumer.getStateEngine();
        consumer.forceDoubleSnapshotNextUpdate();
        watcher.setLatestVersion(v[2]);
        callbacks.events.clear();
        callbacks.finished.drainPermits();

        assertRefreshFails(consumer::triggerRefresh);
        consumer.triggerAsyncRefresh();
        assertTrue(callbacks.finished.tryAcquire(2, 10, TimeUnit.SECONDS));

        assertSame(engine, consumer.getStateEngine());
        String failed = "failed " + v[0] + " " + v[0] + " " + v[2];
        callbacks.assertEvents("detected " + v[2], "started " + v[0] + " " + v[2], failed,
                "detected " + v[2], "started " + v[0] + " " + v[2], failed);

        HollowVersionHold next = consumer.holdVersion(v[2]);
        assertNotSame(engine, consumer.getStateEngine());
        fixture.assertReads(v[0], served.getDataAccess());
        fixture.assertReads(v[2], next.getDataAccess());
    }

    @Test
    public void backgroundRefreshesCatchUpPast32DeltasWithoutASnapshot() {
        long[] v = cycles(41);
        HollowConsumer consumer = consumer().build();
        HollowVersionHold served = serve(consumer, v[0]);
        HollowReadStateEngine engine = consumer.getStateEngine();

        consumer.triggerRefreshTo(v[40]);

        assertEquals(v[32], consumer.getCurrentVersionId()); // the rest at the next refresh
        callbacks.assertEvents("detected " + v[40], "started " + v[0] + " " + v[40],
                "successful " + v[0] + " " + v[32] + " " + v[40]);
        consumer.triggerRefreshTo(v[40]);
        assertEquals(v[40], consumer.getCurrentVersionId());
        assertSame(engine, consumer.getStateEngine());
        fixture.assertReads(v[0], served.getDataAccess());
    }

    @Test
    public void aHoldPast32DeltasFromTheEngineTakesADoubleSnapshot() {
        long[] v = cycles(35);
        HollowConsumer consumer = consumer().build();
        serve(consumer, v[34]);
        HollowReadStateEngine engine = consumer.getStateEngine();

        HollowVersionHold rollback = consumer.holdVersion(v[1]);

        assertNotSame(engine, consumer.getStateEngine());
        assertEquals(v[1], consumer.getCurrentVersionId());
        fixture.assertReads(v[1], rollback.getDataAccess());
    }

    @Test
    public void aDeltaNotVisibleYetIsRetriedWithoutASnapshot() {
        long[] v = cycles(5);
        HollowConsumer consumer = consumer().build();
        HollowVersionHold served = serve(consumer, v[0]);
        HollowReadStateEngine engine = consumer.getStateEngine();
        blobs.hidden.add(v[2]);

        consumer.triggerRefreshTo(v[4]); // the deltas before it apply
        assertEquals(v[2], consumer.getCurrentVersionId());
        callbacks.assertEvents("detected " + v[4], "started " + v[0] + " " + v[4],
                "successful " + v[0] + " " + v[2] + " " + v[4]);

        assertRefreshFails(consumer, v[4]); // still not visible: no plan
        callbacks.assertEvents("detected " + v[4], "started " + v[2] + " " + v[4],
                "failed " + v[2] + " " + v[2] + " " + v[4]);

        blobs.hidden.clear();
        consumer.triggerRefreshTo(v[4]);
        assertEquals(v[4], consumer.getCurrentVersionId());
        assertSame(engine, consumer.getStateEngine()); // no double snapshot forced
        fixture.assertReads(v[0], served.getDataAccess());
    }

    @Test
    public void aDeltaLookupThatThrowsFailsTheRefreshButForcesNothing() {
        long[] v = cycles(4);
        HollowConsumer consumer = consumer().build();
        serve(consumer, v[0]);
        HollowReadStateEngine engine = consumer.getStateEngine();
        blobs.lookupFails.add(v[1]);

        assertRefreshFails(consumer, v[3]);
        assertEquals(v[0], consumer.getCurrentVersionId()); // planning failed, so nothing applied
        callbacks.assertEvents("detected " + v[3], "started " + v[0] + " " + v[3],
                "failed " + v[0] + " " + v[0] + " " + v[3]);

        blobs.lookupFails.clear();
        consumer.triggerRefreshTo(v[3]);
        assertEquals(v[3], consumer.getCurrentVersionId());
        assertSame(engine, consumer.getStateEngine()); // no double snapshot forced
    }

    @Test
    public void aDeltaThatFailsToDownloadLeavesItsDoubleSnapshotToTheNextHold() {
        long[] v = cycles(4);
        HollowConsumer consumer = consumer().build();
        HollowVersionHold served = serve(consumer, v[0]);
        HollowReadStateEngine engine = consumer.getStateEngine();
        blobs.downloadFails.add(v[1]);

        assertRefreshFails(consumer, v[3]);
        assertEquals(v[1], consumer.getCurrentVersionId()); // the deltas before it apply
        callbacks.assertEvents("detected " + v[3], "started " + v[0] + " " + v[3],
                "failed " + v[0] + " " + v[1] + " " + v[3]);
        assertEquals(1, consumer.getNumFailedDeltaTransitions()); // as today
        blobs.downloadFails.clear();

        assertRefreshFails(consumer, v[3]);
        callbacks.assertEvents("detected " + v[3], "started " + v[1] + " " + v[3],
                "failed " + v[1] + " " + v[1] + " " + v[3]);
        assertSame(engine, consumer.getStateEngine());

        HollowVersionHold next = consumer.holdVersion(v[3]);
        assertNotSame(engine, consumer.getStateEngine());
        fixture.assertReads(v[0], served.getDataAccess());
        fixture.assertReads(v[3], next.getDataAccess());
    }

    @Test
    public void aFailedHoldRequestLeavesItsDoubleSnapshotToTheNextHold() {
        long[] v = cycles(5);
        HollowConsumer consumer = consumer().build();
        serve(consumer, v[0]);
        blobs.downloadFails.add(v[1]);
        try {
            consumer.holdVersion(v[3]);
            fail();
        } catch (RuntimeException expected) {
        }
        assertEquals(1, consumer.getNumFailedDeltaTransitions()); // as today
        blobs.downloadFails.clear();
        callbacks.events.clear();
        HollowReadStateEngine engine = consumer.getStateEngine();

        assertRefreshFails(consumer, v[4]);
        callbacks.assertEvents("detected " + v[4], "started " + v[1] + " " + v[4],
                "failed " + v[1] + " " + v[1] + " " + v[4]);

        HollowVersionHold next = consumer.holdVersion(v[3]);
        assertNotSame(engine, consumer.getStateEngine());
        assertEquals(v[3], consumer.getCurrentVersionId());
        fixture.assertReads(v[3], next.getDataAccess());
        consumer.triggerRefreshTo(v[4]);
        assertEquals(v[4], consumer.getCurrentVersionId());
    }

    @Test
    public void aRequestedVersionNoDeltaLandsOnEndsTheRefreshBeforeIt() {
        long[] v = cycles(4);
        HollowConsumer consumer = consumer().build();
        serve(consumer, v[0]);
        long between = v[3] - 1; // the delta to v[3] skips past it

        consumer.triggerRefreshTo(between);

        assertEquals(v[2], consumer.getCurrentVersionId());
        callbacks.assertEvents("detected " + between, "started " + v[0] + " " + between,
                "successful " + v[0] + " " + v[2] + " " + between);
    }

    @Test
    public void aRefreshToTheLatestVersionAppliesEveryVisibleDelta() {
        long[] v = cycles(3);
        HollowConsumer consumer = consumer().build();
        serve(consumer, v[0]);

        consumer.triggerRefresh(); // without an announcement watcher, the latest version

        long latest = HollowConstants.VERSION_LATEST;
        assertEquals(v[2], consumer.getCurrentVersionId());
        callbacks.assertEvents("detected " + latest, "started " + v[0] + " " + latest,
                "successful " + v[0] + " " + v[2] + " " + latest);
    }

    @Test
    public void backgroundRefreshesRunAheadWithDirectLookupOff() {
        long[] v = cycles(3);
        HollowConsumer consumer = consumer()
                .withObjectLongevity(ObjectLongevity.held(new HollowConsumer.HeldVersionsConfig() {
                    @Override
                    public boolean enableDirectLookup() {
                        return false;
                    }
                }))
                .build();
        HollowVersionHold served = serve(consumer, v[0]);

        consumer.triggerRefreshTo(v[2]);

        assertEquals(v[2], consumer.getCurrentVersionId());
        callbacks.assertEvents("detected " + v[2], "started " + v[0] + " " + v[2],
                "successful " + v[0] + " " + v[2] + " " + v[2]);
        fixture.assertReads(v[0], served.getDataAccess()); // through the chain of copies
    }

    @Test
    public void aSchemaChangeIsLeftToTheNextHold() {
        long[] v = cycles(4);
        HollowConsumer consumer = consumer().withDoubleSnapshotConfig(new HollowConsumer.DoubleSnapshotConfig() {
            @Override
            public boolean allowDoubleSnapshot() {
                return true;
            }

            @Override
            public int maxDeltasBeforeDoubleSnapshot() {
                return 32;
            }

            @Override
            public boolean doubleSnapshotOnSchemaChange() {
                return true;
            }
        }).build();
        HollowVersionHold served = serve(consumer, v[0]);
        HollowReadStateEngine engine = consumer.getStateEngine();

        assertRefreshFails(() -> consumer.triggerRefreshTo(withAnotherSchema(v[2])));
        assertEquals(v[0], consumer.getCurrentVersionId());
        assertSame(engine, consumer.getStateEngine());

        HollowVersionHold next = consumer.holdVersion(v[2]); // carries no metadata, but takes the snapshot
        assertNotSame(engine, consumer.getStateEngine());
        fixture.assertReads(v[0], served.getDataAccess());
        fixture.assertReads(v[2], next.getDataAccess());
        consumer.triggerRefreshTo(v[3]);
        assertEquals(v[3], consumer.getCurrentVersionId());
    }

    @Test
    public void afterAForkBackgroundRefreshesApplyTheAbandonedChainAndTheNextHoldTakesTheNewOne() {
        long[] v = cycles(5);
        HollowConsumer consumer = consumer().build();
        serve(consumer, v[1]);
        consumer.triggerRefreshTo(v[3]);
        long forked = fixture.forkFrom(v[1]); // the producer restores the held version and publishes from it
        callbacks.events.clear();

        consumer.triggerRefreshTo(forked);
        assertEquals(v[4], consumer.getCurrentVersionId()); // what was left of the abandoned chain
        assertRefreshFails(consumer, forked); // and then nothing, without forcing a snapshot

        HollowReadStateEngine engine = consumer.getStateEngine();
        HollowVersionHold next = consumer.holdVersion(forked);
        assertNotSame(engine, consumer.getStateEngine());
        fixture.assertReads(forked, next.getDataAccess());
    }

    @Test
    public void backgroundRefreshesStop120DeltasPastTheOldestHoldByDefault() {
        long[] v = cycles(122);
        HollowConsumer consumer = consumer().build();
        serve(consumer, v[0]);

        for (int i = 1; i <= 121; i++)
            consumer.triggerRefreshTo(v[i]);

        assertEquals(v[120], consumer.getCurrentVersionId());
    }

    @Test
    public void aRefreshHeldBackAtTheLeadLimitCallsNoListener() {
        long[] v = cycles(4);
        HollowConsumer consumer = consumer(new AtomicInteger(2)).build();
        serve(consumer, v[0]);
        consumer.triggerRefreshTo(v[2]);
        callbacks.events.clear();

        consumer.triggerRefreshTo(v[3]);
        consumer.triggerRefreshTo(v[1]); // behind the engine too

        assertEquals(v[2], consumer.getCurrentVersionId());
        callbacks.assertEvents(); // the engine didn't move: listeners hear at the refresh that moves it
    }

    @Test
    public void aRefreshThatStartsUnderTheLeadLimitAppliesItsWholePlan() {
        long[] v = cycles(6);
        HollowConsumer consumer = consumer(new AtomicInteger(2)).build();
        serve(consumer, v[0]);
        consumer.triggerRefreshTo(v[1]);

        consumer.triggerRefreshTo(v[4]); // one under the limit, so it ends two past it

        assertEquals(v[4], consumer.getCurrentVersionId());
        consumer.triggerRefreshTo(v[5]);
        assertEquals(v[4], consumer.getCurrentVersionId());
    }

    @Test
    public void closingTheOldestHoldResumesBackgroundRefreshes() {
        long[] v = cycles(4);
        HollowConsumer consumer = consumer(new AtomicInteger(2)).build();
        HollowVersionHold served = serve(consumer, v[0]);
        consumer.triggerRefreshTo(v[2]);
        HollowVersionHold next = consumer.holdVersion(v[1]); // from copies
        consumer.triggerRefreshTo(v[3]);
        assertEquals(v[2], consumer.getCurrentVersionId());

        served.close();
        callbacks.events.clear();
        consumer.triggerRefreshTo(v[3]);

        assertEquals(v[3], consumer.getCurrentVersionId());
        callbacks.assertEvents("detected " + v[3], "started " + v[2] + " " + v[3],
                "successful " + v[2] + " " + v[3] + " " + v[3]);
        fixture.assertReads(v[1], next.getDataAccess());
    }

    @Test
    public void theLeadLimitIsReadBeforeEveryBackgroundRefresh() {
        long[] v = cycles(5);
        AtomicInteger limit = new AtomicInteger(10);
        HollowConsumer consumer = consumer(limit).build();
        serve(consumer, v[0]);
        consumer.triggerRefreshTo(v[3]);

        limit.set(2); // below the lead: run-ahead pauses where it is
        consumer.triggerRefreshTo(v[4]);
        assertEquals(v[3], consumer.getCurrentVersionId());

        limit.set(10);
        consumer.triggerRefreshTo(v[4]);
        assertEquals(v[4], consumer.getCurrentVersionId());
    }

    @Test
    public void holdRequestsIgnoreTheLeadLimit() {
        long[] v = cycles(4);
        HollowConsumer consumer = consumer(new AtomicInteger(1)).build();
        serve(consumer, v[0]);
        consumer.triggerRefreshTo(v[1]);
        consumer.triggerRefreshTo(v[3]);
        assertEquals(v[1], consumer.getCurrentVersionId());

        HollowVersionHold next = consumer.holdVersion(v[3]);

        assertEquals(v[3], consumer.getCurrentVersionId());
        fixture.assertReads(v[3], next.getDataAccess());
    }

    @Test
    public void refreshesWithNoHoldOpenIgnoreTheLeadLimit() {
        long[] v = cycles(4);
        HollowConsumer consumer = consumer(new AtomicInteger(0)).build();
        consumer.triggerRefreshTo(v[0]);
        consumer.triggerRefreshTo(v[1]);
        assertEquals(v[1], consumer.getCurrentVersionId());

        HollowVersionHold hold = consumer.holdVersion(v[1]);
        consumer.triggerRefreshTo(v[2]); // a limit of 0 stops every background refresh
        assertEquals(v[1], consumer.getCurrentVersionId());

        hold.close();
        consumer.triggerRefreshTo(v[3]);
        assertEquals(v[3], consumer.getCurrentVersionId());
    }

    private long[] cycles(int count) {
        return fixture.cycles(count).stream().mapToLong(Long::longValue).toArray();
    }

    private HollowConsumer.Builder<?> consumer() {
        return HollowConsumer.withBlobRetriever(blobs)
                .withObjectLongevity(ObjectLongevity.held())
                .withRefreshListener(callbacks);
    }

    private HollowConsumer.Builder<?> consumer(AtomicInteger leadLimit) {
        return consumer().withObjectLongevity(ObjectLongevity.held(new HollowConsumer.HeldVersionsConfig() {
            @Override
            public int maxDeltasPastOldestHold() {
                return leadLimit.get();
            }
        }));
    }

    /** Refreshes to {@code version} and holds it, so the refreshes after it are background refreshes. */
    private HollowVersionHold serve(HollowConsumer consumer, long version) {
        consumer.triggerRefreshTo(version);
        HollowVersionHold hold = consumer.holdVersion(version);
        callbacks.events.clear();
        return hold;
    }

    private static HollowConsumer.VersionInfo withAnotherSchema(long version) {
        return new HollowConsumer.VersionInfo(version,
                Optional.of(Collections.singletonMap(HEADER_TAG_SCHEMA_HASH, "another schema")), Optional.empty());
    }

    private static void assertRefreshFails(HollowConsumer consumer, long version) {
        assertRefreshFails(() -> consumer.triggerRefreshTo(version));
    }

    private static void assertRefreshFails(Runnable refresh) {
        try {
            refresh.run();
            fail();
        } catch (RuntimeException expected) {
        }
    }

    /** The fixture's blobs, but a delta from a listed version is hidden, its lookup throws, or its download fails. */
    private static final class Blobs implements HollowConsumer.BlobRetriever {
        final Set<Long> hidden = ConcurrentHashMap.newKeySet();
        final Set<Long> lookupFails = ConcurrentHashMap.newKeySet();
        final Set<Long> downloadFails = ConcurrentHashMap.newKeySet();
        private final HollowConsumer.BlobRetriever blobs;

        Blobs(HollowConsumer.BlobRetriever blobs) {
            this.blobs = blobs;
        }

        @Override
        public HollowConsumer.Blob retrieveSnapshotBlob(long desiredVersion) {
            return blobs.retrieveSnapshotBlob(desiredVersion);
        }

        @Override
        public HollowConsumer.Blob retrieveDeltaBlob(long currentVersion) {
            if (lookupFails.contains(currentVersion))
                throw new IllegalStateException("lookup of the delta from " + currentVersion);
            HollowConsumer.Blob delta = blobs.retrieveDeltaBlob(currentVersion);
            if (delta == null || hidden.contains(currentVersion))
                return null;
            if (!downloadFails.contains(currentVersion))
                return delta;
            return new HollowConsumer.Blob(delta.getFromVersion(), delta.getToVersion()) {
                @Override
                public InputStream getInputStream() throws IOException {
                    throw new IOException("download of the delta from " + currentVersion);
                }
            };
        }

        @Override
        public HollowConsumer.Blob retrieveReverseDeltaBlob(long currentVersion) {
            return blobs.retrieveReverseDeltaBlob(currentVersion);
        }
    }

    /** Records the refresh callbacks, in order. */
    private static final class Callbacks extends HollowConsumer.AbstractRefreshListener {
        final List<String> events = new CopyOnWriteArrayList<>();
        final Semaphore finished = new Semaphore(0);

        @Override
        public void versionDetected(HollowConsumer.VersionInfo requestedVersionInfo) {
            events.add("detected " + requestedVersionInfo.getVersion());
        }

        @Override
        public void refreshStarted(long currentVersion, long requestedVersion) {
            events.add("started " + currentVersion + " " + requestedVersion);
        }

        @Override
        public void refreshSuccessful(long beforeVersion, long afterVersion, long requestedVersion) {
            events.add("successful " + beforeVersion + " " + afterVersion + " " + requestedVersion);
            finished.release();
        }

        @Override
        public void refreshFailed(long beforeVersion, long afterVersion, long requestedVersion, Throwable cause) {
            events.add("failed " + beforeVersion + " " + afterVersion + " " + requestedVersion);
            finished.release();
        }

        /** Asserts the callbacks since the last check, and clears them. */
        void assertEvents(String... expected) {
            List<String> actual = new ArrayList<>(events);
            events.clear();
            assertEquals(Arrays.asList(expected), actual);
        }
    }
}

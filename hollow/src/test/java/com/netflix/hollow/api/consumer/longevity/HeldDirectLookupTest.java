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
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.HollowConsumer.HeldVersionsConfig;
import com.netflix.hollow.api.consumer.HollowConsumer.ObjectLongevity;
import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.api.producer.fs.HollowInMemoryBlobStager;
import com.netflix.hollow.core.read.dataaccess.HollowDataAccess;
import com.netflix.hollow.core.read.dataaccess.proxy.HollowProxyDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.write.objectmapper.HollowPrimaryKey;
import com.netflix.hollow.core.write.objectmapper.HollowTypeName;
import com.netflix.hollow.test.InMemoryBlobStore;
import com.netflix.hollow.tools.history.HollowHistoricalStateDataAccess;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.Test;

public class HeldDirectLookupTest {

    private final AtomicBoolean directLookup = new AtomicBoolean(true);
    private final HeldVersionsConfig config = new HeldVersionsConfig() {
        @Override
        public boolean enableDirectLookup() {
            return directLookup.get();
        }
    };

    @Test
    public void directLookupIsOnByDefault() {
        assertTrue(HeldVersionsConfig.DEFAULT.enableDirectLookup());
    }

    /** Ordinals reused within the range, records changed several times, keyed sets and maps, and lists. */
    @Test
    public void heldVersionsBehindTheEngineReadExactlyThroughDirectLookup() {
        readEveryHoldAfterEveryTransition(new HeldVersionsFixture(), true);
    }

    @Test
    public void shardedTypesReadExactlyThroughDirectLookup() {
        HeldVersionsFixture fixture = new HeldVersionsFixture(16);
        HollowConsumer consumer = readEveryHoldAfterEveryTransition(fixture, true);
        assertTrue(consumer.getStateEngine().getTypeState("Movie").numShards() > 1);
        assertTrue(consumer.getStateEngine().getTypeState("SetOfString").numShards() > 1);
    }

    @Test
    public void withDirectLookupOffHeldVersionsReadThroughTheChain() {
        directLookup.set(false);
        readEveryHoldAfterEveryTransition(new HeldVersionsFixture(), false);
    }

    @Test
    public void turnedOffEveryHoldReadsThroughTheChainFromTheNextRefresh() {
        HeldVersionsFixture fixture = new HeldVersionsFixture();
        List<Long> versions = fixture.cycles(4);
        HollowConsumer consumer = fixture.heldConsumer(config);
        consumer.triggerRefreshTo(versions.get(0));
        HollowVersionHold first = consumer.holdVersion(versions.get(0));
        consumer.triggerRefreshTo(versions.get(1));
        HollowVersionHold second = consumer.holdVersion(versions.get(1));
        consumer.triggerRefreshTo(versions.get(2));
        assertDirect(first, second);

        directLookup.set(false);
        assertDirect(first, second); // read at refreshes only
        consumer.triggerRefreshTo(versions.get(2)); // a refresh that applies nothing

        assertChain(first, second);
        assertReads(fixture, first, second);
        consumer.triggerRefreshTo(versions.get(3));
        assertEquals((long) versions.get(3), consumer.getCurrentVersionId()); // run-ahead goes on with the guard off
        assertChain(first, second);
        assertReads(fixture, first, second);
    }

    @Test
    public void aRefreshHeldBackAtTheLeadLimitStillReadsTheGuard() {
        HeldVersionsFixture fixture = new HeldVersionsFixture();
        List<Long> versions = fixture.cycles(3);
        HollowConsumer consumer = fixture.heldConsumer(new HeldVersionsConfig() {
            @Override
            public boolean enableDirectLookup() {
                return directLookup.get();
            }

            @Override
            public int maxDeltasPastOldestHold() {
                return 1;
            }
        });
        consumer.triggerRefreshTo(versions.get(0));
        HollowVersionHold first = consumer.holdVersion(versions.get(0));
        consumer.triggerRefreshTo(versions.get(1));
        assertDirect(first);

        directLookup.set(false);
        consumer.triggerRefreshTo(versions.get(2)); // held back at the limit

        assertEquals((long) versions.get(1), consumer.getCurrentVersionId());
        assertChain(first);
        assertReads(fixture, first);
    }

    @Test
    public void turnedOnDirectLookupAppliesToVersionsFromTheFlip() {
        HeldVersionsFixture fixture = new HeldVersionsFixture();
        List<Long> versions = fixture.cycles(4);
        directLookup.set(false);
        HollowConsumer consumer = fixture.heldConsumer(config);
        consumer.triggerRefreshTo(versions.get(0));
        HollowVersionHold before = consumer.holdVersion(versions.get(0));
        consumer.triggerRefreshTo(versions.get(1));
        HollowVersionHold atTheFlip = consumer.holdVersion(versions.get(1));

        directLookup.set(true);
        consumer.triggerRefreshTo(versions.get(1)); // a refresh that applies nothing
        consumer.triggerRefreshTo(versions.get(2));
        HollowVersionHold after = consumer.holdVersion(versions.get(2));
        consumer.triggerRefreshTo(versions.get(3));

        assertChain(before);
        assertDirect(atTheFlip, after);
        assertReads(fixture, before, atTheFlip, after);
        assertDirect(consumer.holdVersion(versions.get(1)), consumer.holdVersion(versions.get(2))); // held at once
    }

    @Test
    public void flippedBothWaysHoldsStayExact() {
        HeldVersionsFixture fixture = new HeldVersionsFixture();
        List<Long> versions = fixture.cycles(12);
        HollowConsumer consumer = fixture.heldConsumer(config);
        List<HollowVersionHold> holds = new ArrayList<>();
        for (int i = 0; i < versions.size(); i++) {
            directLookup.set(i % 3 != 2);
            consumer.triggerRefreshTo(versions.get(i));
            holds.add(consumer.holdVersion(versions.get(i)));
            if (i % 4 == 3)
                holds.remove(0).close();
            for (HollowVersionHold hold : holds)
                fixture.assertReads(hold.getVersion(), hold.getDataAccess());
        }
    }

    @Test
    public void aSchemaChangeInADeltaKeepsHeldVersionsExact() {
        InMemoryBlobStore blobStore = new InMemoryBlobStore();
        AtomicLong lastVersion = new AtomicLong();
        HollowProducer before = producer(blobStore, lastVersion);
        before.initializeDataModel(Movie.class);
        long v1 = before.runCycle(ws -> {
            for (int id = 1; id <= 4; id++)
                ws.add(new Movie(id, "title " + id));
        });
        long v2 = before.runCycle(ws -> {
            ws.add(new Movie(1, "title 1"));
            ws.add(new Movie(2, "retitled 2"));
            ws.add(new Movie(4, "title 4"));
            ws.add(new Movie(5, "title 5"));
        });
        HollowProducer after = producer(blobStore, lastVersion); // restarted with a field and a type more
        after.initializeDataModel(MovieWithYear.class, Award.class);
        after.restore(v2, blobStore);
        long v3 = after.runCycle(ws -> {
            ws.add(new MovieWithYear(1, "title 1", 1999));
            ws.add(new MovieWithYear(2, "retitled 2", 2001));
            ws.add(new MovieWithYear(5, "title 5", 2005));
            ws.add(new Award(7));
        });
        long v4 = after.runCycle(ws -> {
            ws.add(new MovieWithYear(1, "retitled 1", 1999));
            ws.add(new MovieWithYear(5, "title 5", 2005));
            ws.add(new MovieWithYear(6, "title 6", 2006));
        });

        HollowConsumer reference = HollowConsumer.withBlobRetriever(blobStore).build(); // the same deltas, no holds
        HollowConsumer consumer = HollowConsumer.withBlobRetriever(blobStore)
                .withObjectLongevity(ObjectLongevity.held(config))
                .build();
        Map<Long, Map<String, Map<Integer, String>>> expected = new HashMap<>();
        List<HollowVersionHold> holds = new ArrayList<>();
        for (long version : Arrays.asList(v1, v2, v3, v4)) {
            reference.triggerRefreshTo(version);
            expected.put(version, HeldVersionsFixture.recordsOf(reference.getStateEngine()));
            consumer.triggerRefreshTo(version);
            holds.add(consumer.holdVersion(version));
            for (HollowVersionHold hold : holds)
                HeldVersionsFixture.assertReads("in " + hold.getVersion() + " at " + version,
                        expected.get(hold.getVersion()), hold.getDataAccess());
        }
        HollowObjectSchema movie = (HollowObjectSchema) consumer.getStateEngine().getSchema("Movie");
        assertEquals(2, movie.numFields()); // the snapshot's schema
        assertFalse(consumer.getStateEngine().getAllTypes().contains("Award"));
        assertDirect(holds.get(0), holds.get(1), holds.get(2));
    }

    /**
     * Holds every version on a path forward, back by reverse deltas and forward again, and after each transition reads
     * every hold: the newest from the engine, the others through direct lookup or the chain.
     */
    private HollowConsumer readEveryHoldAfterEveryTransition(HeldVersionsFixture fixture, boolean direct) {
        List<Long> versions = fixture.cycles(10);
        HollowConsumer consumer = fixture.heldConsumer(config);
        List<HollowVersionHold> holds = new ArrayList<>();
        for (int i : HeldVersionsFixture.forwardBackForward(versions.size(), 3)) {
            consumer.triggerRefreshTo(versions.get(i));
            holds.add(consumer.holdVersion(versions.get(i)));
            for (HollowVersionHold hold : holds)
                fixture.assertReads(hold.getVersion(), hold.getDataAccess());
            assertSame(consumer.getStateEngine(), proxied(holds.get(holds.size() - 1)));
            for (HollowVersionHold hold : holds.subList(0, holds.size() - 1)) {
                if (direct)
                    assertDirect(hold);
                else
                    assertChain(hold);
            }
        }
        return consumer;
    }

    private static void assertReads(HeldVersionsFixture fixture, HollowVersionHold... holds) {
        for (HollowVersionHold hold : holds)
            fixture.assertReads(hold.getVersion(), hold.getDataAccess());
    }

    /** Reads through direct lookup, whose data access is internal to Hollow's client package. */
    private static void assertDirect(HollowVersionHold... holds) {
        for (HollowVersionHold hold : holds) {
            assertEquals("version " + hold.getVersion(), "HollowHeldStateDataAccess",
                    proxied(hold).getClass().getSimpleName());
        }
    }

    private static void assertChain(HollowVersionHold... holds) {
        for (HollowVersionHold hold : holds)
            assertTrue(hold.getVersion() + ": " + proxied(hold),
                    proxied(hold) instanceof HollowHistoricalStateDataAccess);
    }

    private static HollowDataAccess proxied(HollowVersionHold hold) {
        return ((HollowProxyDataAccess) hold.getDataAccess()).getProxiedDataAccess();
    }

    private static HollowProducer producer(InMemoryBlobStore blobStore, AtomicLong lastVersion) {
        return HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .withVersionMinter(() -> lastVersion.addAndGet(10))
                .build();
    }

    @HollowPrimaryKey(fields = "id")
    static final class Movie {
        final long id;
        final String title;

        Movie(long id, String title) {
            this.id = id;
            this.title = title;
        }
    }

    @HollowTypeName(name = "Movie")
    @HollowPrimaryKey(fields = "id")
    static final class MovieWithYear {
        final long id;
        final String title;
        final int year;

        MovieWithYear(long id, String title, int year) {
            this.id = id;
            this.title = title;
            this.year = year;
        }
    }

    static final class Award {
        final long id;

        Award(long id) {
            this.id = id;
        }
    }
}

package com.netflix.hollow.api.consumer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.netflix.hollow.api.consumer.index.HashIndex;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.core.read.dataaccess.HollowDataAccess;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.read.engine.PopulatedOrdinalListener;
import java.util.BitSet;
import com.netflix.hollow.test.HollowWriteStateEngineBuilder;
import com.netflix.hollow.test.consumer.TestBlobRetriever;
import com.netflix.hollow.test.consumer.TestHollowConsumer;
import com.netflix.hollow.test.generated.Award;
import com.netflix.hollow.test.generated.AwardsAPI;
import com.netflix.hollow.test.generated.Movie;
import com.netflix.hollow.test.generated.MoviePrimaryKeyIndex;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class HollowConsumerBuilderTest {
    @Test
    public void testCachedTypes() throws IOException {
        TestHollowConsumer consumer = new TestHollowConsumer.Builder()
                .withBlobRetriever(new TestBlobRetriever())
                .withGeneratedAPIClass(AwardsAPI.class, "Movie")
                .build();
        testConsumerCache(true, false, consumer);
    }

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();
    @Test
    public void testCachedTypes_inputSanitization() {
        expectedEx.expect(NullPointerException.class);
        expectedEx.expectMessage("null detected for varargs parameter additionalCachedTypes");
        TestHollowConsumer consumer = new TestHollowConsumer.Builder()
                .withBlobRetriever(new TestBlobRetriever())
                .withGeneratedAPIClass(AwardsAPI.class, "Movie", null)
                .build();
    }

    @Test(expected = IllegalStateException.class)
    public void testCachedTypesDetached() throws IOException {
        TestHollowConsumer consumer = new TestHollowConsumer.Builder()
                .withBlobRetriever(new TestBlobRetriever())
                .withGeneratedAPIClass(AwardsAPI.class, "Movie")
                .build();
        testConsumerCache(true, true, consumer);
    }

    @Test
    public void testNoCacheConsumer() throws IOException {
        TestHollowConsumer consumer = new TestHollowConsumer.Builder()
                .withBlobRetriever(new TestBlobRetriever())
                .withGeneratedAPIClass(AwardsAPI.class)
                .build();
        testConsumerCache(false, false, consumer);
    }

    @Test
    public void retainRemovedOrdinals_wiresFlagThroughToGeneratedApi() throws IOException {
        TestHollowConsumer consumer = longevityBuilder()
                .withGeneratedAPIClass(RetentionRecordingAPI.class, true, "Movie")
                .build();

        RetentionRecordingAPI api = refreshThroughDelta(consumer);

        // the delta refresh builds the api via createAPI(dataAccess, previous), which must reach the 5-arg
        // retain-aware constructor with the flag set
        assertEquals(5, api.constructorArgCount);
        assertTrue(api.retainRemovedOrdinalsReceived);
    }

    @Test
    public void withoutRetainRemovedOrdinals_usesStandardConstructor() throws IOException {
        TestHollowConsumer consumer = longevityBuilder()
                .withGeneratedAPIClass(RetentionRecordingAPI.class, "Movie")
                .build();

        RetentionRecordingAPI api = refreshThroughDelta(consumer);

        assertEquals(4, api.constructorArgCount);
        assertFalse(api.retainRemovedOrdinalsReceived);
    }

    @Test
    public void retainedBeforeImage_referenceTraversal_resolvesToBeforeImagesWithoutError() throws IOException {
        TestHollowConsumer consumer = longevityBuilder()
                .withGeneratedAPIClass(AwardsAPI.class, true, "Award", "Movie", "SetOfMovie")
                .build();

        com.netflix.hollow.test.model.Movie winner = new com.netflix.hollow.test.model.Movie(10, "winner", 2023);
        com.netflix.hollow.test.model.Movie nominee = new com.netflix.hollow.test.model.Movie(20, "nominee", 2023);
        com.netflix.hollow.test.model.Award a1 = new com.netflix.hollow.test.model.Award(1, winner,
                new HashSet<com.netflix.hollow.test.model.Movie>() {{ add(nominee); }});

        // v1: the award (ordinal 0) and its referenced movies are present
        consumer.addSnapshot(1l, new HollowWriteStateEngineBuilder().add(a1).build());
        consumer.triggerRefreshTo(1l);

        // v2: the award and its movies are removed; a different award takes fresh ordinals (the one-cycle
        // reuse gap keeps a1 at ordinal 0), so a1 becomes a removed-only "before image"
        com.netflix.hollow.test.model.Movie w2 = new com.netflix.hollow.test.model.Movie(30, "w2", 2024);
        com.netflix.hollow.test.model.Award a2 = new com.netflix.hollow.test.model.Award(2, w2,
                new HashSet<com.netflix.hollow.test.model.Movie>());
        consumer.addDelta(1l, 2l, new HollowWriteStateEngineBuilder().add(a2).build());
        consumer.triggerRefreshTo(2l);

        AwardsAPI api = (AwardsAPI) consumer.getAPI();

        // the removed award is still queryable as a before image
        Award beforeImage = api.getAward(0);
        assertNotNull(beforeImage);
        assertEquals(1L, beforeImage.getId());

        // traversing its reference fields must not error and must resolve to the referenced before images
        Movie beforeWinner = beforeImage.getWinner();
        assertNotNull(beforeWinner);
        assertEquals(10L, beforeWinner.getId());

        assertEquals(1L, beforeImage.getNominees().stream().count());
        assertEquals(20L, beforeImage.getNominees().stream().findFirst().get().getId());
    }

    @Test
    public void withoutRetain_removedAwardBeforeImageIsHoledOut() throws IOException {
        TestHollowConsumer consumer = longevityBuilder()
                .withGeneratedAPIClass(AwardsAPI.class, "Award", "Movie", "SetOfMovie")
                .build();

        com.netflix.hollow.test.model.Movie winner = new com.netflix.hollow.test.model.Movie(10, "winner", 2023);
        com.netflix.hollow.test.model.Award a1 = new com.netflix.hollow.test.model.Award(1, winner,
                new HashSet<com.netflix.hollow.test.model.Movie>());
        consumer.addSnapshot(1l, new HollowWriteStateEngineBuilder().add(a1).build());
        consumer.triggerRefreshTo(1l);

        com.netflix.hollow.test.model.Movie w2 = new com.netflix.hollow.test.model.Movie(30, "w2", 2024);
        com.netflix.hollow.test.model.Award a2 = new com.netflix.hollow.test.model.Award(2, w2,
                new HashSet<com.netflix.hollow.test.model.Movie>());
        consumer.addDelta(1l, 2l, new HollowWriteStateEngineBuilder().add(a2).build());
        consumer.triggerRefreshTo(2l);

        AwardsAPI api = (AwardsAPI) consumer.getAPI();
        // without the flag, the removed record's slot is holed out
        assertNull(api.getAward(0));
    }

    // An updated record removes its old ordinal and adds a new one. With retention enabled, the before image at
    // the removed ordinal and the after image at the added ordinal are both queryable in the same refresh.
    @Test
    public void updatedRecord_beforeAndAfterImagesQueryableViaOrdinalDiff() throws IOException {
        TestHollowConsumer consumer = longevityBuilder()
                .withGeneratedAPIClass(AwardsAPI.class, true, "Movie")
                .build();

        // v1: record with id=100 at ordinal 0
        consumer.addSnapshot(1l, new HollowWriteStateEngineBuilder()
                .add(new com.netflix.hollow.test.model.Movie(100, "old title", 2020)).build());
        consumer.triggerRefreshTo(1l);

        // v2: the same record is UPDATED (title changes) -> new ordinal added, old ordinal removed
        consumer.addDelta(1l, 2l, new HollowWriteStateEngineBuilder()
                .add(new com.netflix.hollow.test.model.Movie(100, "new title", 2020)).build());
        consumer.triggerRefreshTo(2l);

        AwardsAPI api = (AwardsAPI) consumer.getAPI();

        // diff ordinals exactly as a change listener would
        PopulatedOrdinalListener listener = movieOrdinalListener(consumer);
        BitSet removed = (BitSet) listener.getPreviousOrdinals().clone();
        removed.andNot(listener.getPopulatedOrdinals());
        BitSet added = (BitSet) listener.getPopulatedOrdinals().clone();
        added.andNot(listener.getPreviousOrdinals());

        int removedOrdinal = removed.nextSetBit(0);
        int addedOrdinal = added.nextSetBit(0);
        assertTrue("expected an updated record to remove one ordinal", removedOrdinal >= 0);
        assertTrue("expected an updated record to add one ordinal", addedOrdinal >= 0);

        // before image (removed ordinal) is still queryable and holds the prior value
        Movie before = api.getMovie(removedOrdinal);
        assertNotNull(before);
        assertEquals(100L, before.getId());
        assertEquals("old title", before.getTitle());

        // after image (added ordinal) holds the new value
        Movie after = api.getMovie(addedOrdinal);
        assertNotNull(after);
        assertEquals(100L, after.getId());
        assertEquals("new title", after.getTitle());
    }

    @Test
    public void updatedRecord_withoutRetain_beforeImageIsLost() throws IOException {
        TestHollowConsumer consumer = longevityBuilder()
                .withGeneratedAPIClass(AwardsAPI.class, "Movie")
                .build();

        consumer.addSnapshot(1l, new HollowWriteStateEngineBuilder()
                .add(new com.netflix.hollow.test.model.Movie(100, "old title", 2020)).build());
        consumer.triggerRefreshTo(1l);
        consumer.addDelta(1l, 2l, new HollowWriteStateEngineBuilder()
                .add(new com.netflix.hollow.test.model.Movie(100, "new title", 2020)).build());
        consumer.triggerRefreshTo(2l);

        AwardsAPI api = (AwardsAPI) consumer.getAPI();
        PopulatedOrdinalListener listener = movieOrdinalListener(consumer);
        BitSet removed = (BitSet) listener.getPreviousOrdinals().clone();
        removed.andNot(listener.getPopulatedOrdinals());
        int removedOrdinal = removed.nextSetBit(0);
        assertTrue(removedOrdinal >= 0);

        // without the flag, the before image of the updated record is holed out
        assertNull(api.getMovie(removedOrdinal));
    }

    // A before image is retained for exactly one cycle: after a second, unrelated delta it is no longer queryable.
    // A stable record keeps the low ordinal populated so the checked slot stays in bounds regardless of reuse.
    @Test
    public void updatedRecord_beforeImageRetainedForOnlyOneCycle() throws IOException {
        TestHollowConsumer consumer = longevityBuilder()
                .withGeneratedAPIClass(AwardsAPI.class, true, "Movie")
                .build();

        com.netflix.hollow.test.model.Movie stable = new com.netflix.hollow.test.model.Movie(1, "stable", 2020);
        consumer.addSnapshot(1l, new HollowWriteStateEngineBuilder()
                .add(stable).add(new com.netflix.hollow.test.model.Movie(2, "t1", 2020)).build());
        consumer.triggerRefreshTo(1l);

        // cycle 1: update record id=2 -> its old ordinal becomes a removed-only before image
        consumer.addDelta(1l, 2l, new HollowWriteStateEngineBuilder()
                .add(stable).add(new com.netflix.hollow.test.model.Movie(2, "t2", 2020)).build());
        consumer.triggerRefreshTo(2l);

        PopulatedOrdinalListener listener = movieOrdinalListener(consumer);
        BitSet removed = (BitSet) listener.getPreviousOrdinals().clone();
        removed.andNot(listener.getPopulatedOrdinals());
        int t1Ordinal = removed.nextSetBit(0);
        assertTrue(t1Ordinal >= 0);
        assertEquals("t1", ((AwardsAPI) consumer.getAPI()).getMovie(t1Ordinal).getTitle());

        // cycle 2: an unrelated add -> the previous before image must no longer be retained
        consumer.addDelta(2l, 3l, new HollowWriteStateEngineBuilder()
                .add(stable).add(new com.netflix.hollow.test.model.Movie(2, "t2", 2020))
                .add(new com.netflix.hollow.test.model.Movie(3, "filler", 2020)).build());
        consumer.triggerRefreshTo(3l);

        Movie stale = ((AwardsAPI) consumer.getAPI()).getMovie(t1Ordinal);
        assertTrue("before image should be dropped after one cycle", stale == null || !"t1".equals(stale.getTitle()));
    }

    private static PopulatedOrdinalListener movieOrdinalListener(TestHollowConsumer consumer) {
        HollowTypeReadState movieState = consumer.getStateEngine().getTypeState("Movie");
        return movieState.getListener(PopulatedOrdinalListener.class);
    }

    @Test
    public void retainedBeforeImage_traversalToNonCachedReference_resolvesGhost() throws IOException {
        // Only the parent (Award) is cached/retained; the referenced Movie is NOT cached, so getWinner()
        // resolves through the factory provider against the current read state. This probes the "ghost read"
        // path for a removed reference of a non-cached type.
        TestHollowConsumer consumer = longevityBuilder()
                .withGeneratedAPIClass(AwardsAPI.class, true, "Award")
                .build();

        com.netflix.hollow.test.model.Movie winner = new com.netflix.hollow.test.model.Movie(10, "winner", 2023);
        com.netflix.hollow.test.model.Award a1 = new com.netflix.hollow.test.model.Award(1, winner,
                new HashSet<com.netflix.hollow.test.model.Movie>());
        consumer.addSnapshot(1l, new HollowWriteStateEngineBuilder().add(a1).build());
        consumer.triggerRefreshTo(1l);

        com.netflix.hollow.test.model.Movie w2 = new com.netflix.hollow.test.model.Movie(30, "w2", 2024);
        com.netflix.hollow.test.model.Award a2 = new com.netflix.hollow.test.model.Award(2, w2,
                new HashSet<com.netflix.hollow.test.model.Movie>());
        consumer.addDelta(1l, 2l, new HollowWriteStateEngineBuilder().add(a2).build());
        consumer.triggerRefreshTo(2l);

        AwardsAPI api = (AwardsAPI) consumer.getAPI();
        Award beforeImage = api.getAward(0);
        assertNotNull(beforeImage);
        assertEquals(1L, beforeImage.getId());

        // traverse to the removed, non-cached Movie reference -- must not error
        Movie beforeWinner = beforeImage.getWinner();
        assertNotNull(beforeWinner);
        assertEquals(10L, beforeWinner.getId());
    }

    // Object longevity forces a fresh API instance per cycle via createAPI(dataAccess, previous) -- the rotation
    // path that the retainRemovedOrdinals flag targets. Without it, deltas are applied in place and no new API is built.
    private static TestHollowConsumer.Builder longevityBuilder() {
        return new TestHollowConsumer.Builder()
                .withBlobRetriever(new TestBlobRetriever())
                .withObjectLongevityConfig(new HollowConsumer.ObjectLongevityConfig() {
                    public long usageDetectionPeriodMillis() { return 1_000L; }
                    public long gracePeriodMillis() { return 2L * 60 * 60 * 1000; }
                    public boolean forceDropData() { return true; }
                    public boolean enableLongLivedObjectSupport() { return true; }
                    public boolean enableExpiredUsageStackTraces() { return false; }
                    public boolean dropDataAutomatically() { return true; }
                });
    }

    private RetentionRecordingAPI refreshThroughDelta(TestHollowConsumer consumer) throws IOException {
        com.netflix.hollow.test.model.Movie m1 = new com.netflix.hollow.test.model.Movie(1, "test movie 1", 2023);
        com.netflix.hollow.test.model.Award a1 = new com.netflix.hollow.test.model.Award(1, m1,
                new HashSet<com.netflix.hollow.test.model.Movie>());

        consumer.addSnapshot(1l, new HollowWriteStateEngineBuilder().add(a1).build());
        consumer.triggerRefreshTo(1l);
        consumer.addDelta(1l, 2l, new HollowWriteStateEngineBuilder().add(a1).build());
        consumer.triggerRefreshTo(2l);

        return (RetentionRecordingAPI) consumer.getAPI();
    }

    /** Stub generated API that records which constructor the factory invoked. */
    public static class RetentionRecordingAPI extends HollowAPI {
        final int constructorArgCount;
        final boolean retainRemovedOrdinalsReceived;

        public RetentionRecordingAPI(HollowDataAccess dataAccess) {
            super(dataAccess);
            this.constructorArgCount = 1;
            this.retainRemovedOrdinalsReceived = false;
        }

        public RetentionRecordingAPI(HollowDataAccess dataAccess, Set<String> cachedTypes) {
            super(dataAccess);
            this.constructorArgCount = 2;
            this.retainRemovedOrdinalsReceived = false;
        }

        public RetentionRecordingAPI(HollowDataAccess dataAccess, Set<String> cachedTypes,
                Map<String, Object> factoryOverrides, RetentionRecordingAPI previousCycleAPI) {
            super(dataAccess);
            this.constructorArgCount = 4;
            this.retainRemovedOrdinalsReceived = false;
        }

        public RetentionRecordingAPI(HollowDataAccess dataAccess, Set<String> cachedTypes,
                Map<String, Object> factoryOverrides, RetentionRecordingAPI previousCycleAPI,
                boolean retainRemovedOrdinals) {
            super(dataAccess);
            this.constructorArgCount = 5;
            this.retainRemovedOrdinalsReceived = retainRemovedOrdinals;
        }
    }

    private void testConsumerCache(boolean hasCache, boolean detachCache, TestHollowConsumer consumer) throws IOException {
        com.netflix.hollow.test.model.Movie m1 = new com.netflix.hollow.test.model.Movie(1, "test movie 1", 2023);
        com.netflix.hollow.test.model.Movie m2 = new com.netflix.hollow.test.model.Movie(2, "test movie 2", 2023);
        com.netflix.hollow.test.model.Award a1 = new com.netflix.hollow.test.model.Award(1, m1, new HashSet<com.netflix.hollow.test.model.Movie>() {{ add(m2); }});
        com.netflix.hollow.test.model.Award a2 = new com.netflix.hollow.test.model.Award(2, m2, new HashSet<com.netflix.hollow.test.model.Movie>() {{ add(m1); }});

        // v1
        consumer.addSnapshot(1l, new HollowWriteStateEngineBuilder()
                .add(a1).build());
        consumer.triggerRefreshTo(1l);
        AwardsAPI awardsAPI = (AwardsAPI) consumer.getAPI();
        Movie movieFromV1 = awardsAPI.getMovie(0);

        // v2
        consumer.addDelta(1l, 2l, new HollowWriteStateEngineBuilder()
                .add(a1, a2).build());
        consumer.triggerRefreshTo(2l);
        assertTrue(consumer.getCurrentVersionId() == 2l);
        awardsAPI = (AwardsAPI) consumer.getAPI();

        if (detachCache) {
            awardsAPI.detachCaches();
        }
        Movie movie = awardsAPI.getMovie(0);

        // primary key index
        MoviePrimaryKeyIndex primaryKeyIndex = new MoviePrimaryKeyIndex(consumer, false);
        Movie movieFromPrimaryKeyIndex = primaryKeyIndex.findMatch(1l);

        // hash index
        HashIndex<Movie, Long> hashIndex = HashIndex.from(consumer, Movie.class)
                .usingPath("id", Long.class); // note ".value" suffix
        Movie movieFromHashIndex = hashIndex.findMatches(1l).findFirst().get();

        // getAll
        Movie movieFromGetAllMovie = awardsAPI.getAllMovie().stream().findFirst().get();

        // movie from award.getWinner()
        Award award1 = awardsAPI.getAward(0);
        Movie movieFromAward = award1.getWinner();

        // movie from award.getNominees()
        Award award2 = awardsAPI.getAward(1);
        Movie movieFromAwardNominees = award2.getNominees().stream().findFirst().get();


        if (hasCache) {
            assertTrue(movie == movieFromV1);
            assertTrue(movie == movieFromPrimaryKeyIndex);
            assertTrue(movie == movieFromHashIndex);
            assertTrue(movie == movieFromGetAllMovie);
            assertTrue(movie == movieFromAward);
            assertTrue(movie == movieFromAwardNominees);
        } else {
            assertFalse(movie == movieFromV1);
            assertFalse(movie == movieFromPrimaryKeyIndex);
            assertFalse(movie == movieFromHashIndex);
            assertFalse(movie == movieFromGetAllMovie);
            assertFalse(movie == movieFromAward);
            assertFalse(movie == movieFromAwardNominees);
        }
    }

}

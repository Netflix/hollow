package com.netflix.hollow.api.consumer;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.netflix.hollow.api.consumer.index.HashIndex;
import com.netflix.hollow.test.HollowWriteStateEngineBuilder;
import com.netflix.hollow.test.consumer.TestBlobRetriever;
import com.netflix.hollow.test.consumer.TestHollowConsumer;
import com.netflix.hollow.test.generated.Award;
import com.netflix.hollow.test.generated.AwardsAPI;
import com.netflix.hollow.test.generated.Movie;
import com.netflix.hollow.test.generated.MoviePrimaryKeyIndex;
import java.io.IOException;
import java.util.HashSet;
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

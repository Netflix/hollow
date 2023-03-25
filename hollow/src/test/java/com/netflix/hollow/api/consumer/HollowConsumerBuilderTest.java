package com.netflix.hollow.api.consumer;

import com.netflix.hollow.test.HollowWriteStateEngineBuilder;
import com.netflix.hollow.test.consumer.TestBlobRetriever;
import com.netflix.hollow.test.consumer.TestHollowConsumer;
import com.netflix.hollow.test.generated.MovieAPI;
import com.netflix.hollow.test.model.Movie;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


public class HollowConsumerBuilderTest {
    @Test
    public void testCachedTypes() throws IOException {
        TestHollowConsumer consumer = new TestHollowConsumer.Builder()
                .withBlobRetriever(new TestBlobRetriever())
                .withGeneratedAPIClass(MovieAPI.class, "Movie")
                .build();
        testConsumerCache(true, false, consumer);
    }

    @Test(expected = IllegalStateException.class)
    public void testCachedTypesDetached() throws IOException {
        TestHollowConsumer consumer = new TestHollowConsumer.Builder()
                .withBlobRetriever(new TestBlobRetriever())
                .withGeneratedAPIClass(MovieAPI.class, "Movie")
                .build();
        testConsumerCache(true, true, consumer);
    }

    @Test
    public void testNoCacheConsumer() throws IOException {
        TestHollowConsumer consumer = new TestHollowConsumer.Builder()
                .withBlobRetriever(new TestBlobRetriever())
                .withGeneratedAPIClass(MovieAPI.class)
                .build();
        testConsumerCache(false, false, consumer);
    }

    private void testConsumerCache(boolean hasCache, boolean detachCache, TestHollowConsumer consumer) throws IOException {
        consumer.addSnapshot(1l, new HollowWriteStateEngineBuilder().add(
                new Movie(1, "test movie 1", 2023)).build());

        consumer.triggerRefreshTo(1l);

        consumer.addDelta(1l, 2l, new HollowWriteStateEngineBuilder().add(
                new Movie(1, "test movie 1", 2023),
                new Movie(2, "test movie 2", 2023)).build());

        consumer.triggerRefreshTo(2l);

        Assert.assertTrue(consumer.getCurrentVersionId() == 2l);

        MovieAPI movieAPI = (MovieAPI) consumer.getAPI();
        if (detachCache) {
            movieAPI.detachCaches();
        }
        com.netflix.hollow.test.generated.Movie movie1 = movieAPI.getMovie(0);
        com.netflix.hollow.test.generated.Movie movie2 = movieAPI.getMovie(0);

        if (hasCache) {
            Assert.assertTrue(movie1 == movie2);
        } else {
            Assert.assertFalse(movie1 == movie2);
        }
    }

}

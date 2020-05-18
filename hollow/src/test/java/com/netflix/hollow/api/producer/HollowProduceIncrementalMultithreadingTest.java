package com.netflix.hollow.api.producer;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.InMemoryBlobStore;
import com.netflix.hollow.api.objects.generic.GenericHollowObject;
import com.netflix.hollow.api.producer.fs.HollowInMemoryBlobStager;
import com.netflix.hollow.core.index.HollowPrimaryKeyIndex;
import com.netflix.hollow.core.write.objectmapper.HollowPrimaryKey;
import java.util.Arrays;
import java.util.stream.IntStream;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class HollowProduceIncrementalMultithreadingTest {

    private static final int ELEMENTS = 20000;
    private static final int ITERATIONS = 10;

    private InMemoryBlobStore blobStore;

    @Before
    public void setUp() {
        blobStore = new InMemoryBlobStore();
    }

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void updateAndPublishUsingMultithreading() {
        // run within a loop to increase the likelihood of a race condition to occur
        for (int iterationCounter = 0; iterationCounter < ITERATIONS; ++iterationCounter) {
            HollowProducer.Incremental producer = createInMemoryIncrementalProducer();
            initializeData(producer);

            int[] notModifiedElementIds = IntStream.range(0, ELEMENTS / 2).toArray();
            int[] modifiedElementIds = IntStream.range(ELEMENTS / 2, ELEMENTS).toArray();

            long versionAfterUpdate = producer.runIncrementalCycle(iws -> {
                Arrays.stream(modifiedElementIds).parallel()
                        .mapToObj(i -> new SimpleType(i, i + 1))
                        .forEach(iws::addOrModify);
            });

            /// now we read the changes and assert
            HollowPrimaryKeyIndex idx = createPrimaryKeyIndex(versionAfterUpdate);
            Assert.assertFalse(idx.containsDuplicates());
            Assert.assertTrue(Arrays.stream(notModifiedElementIds)
                    .boxed()
                    .map(elementId -> getHollowObject(idx, elementId))
                    .allMatch(obj -> obj.getInt("value") == obj.getInt("id")));
            Assert.assertTrue(Arrays.stream(modifiedElementIds)
                    .boxed()
                    .map(elementId -> getHollowObject(idx, elementId))
                    .allMatch(obj -> obj.getInt("value") != obj.getInt("id")));
        }
    }

    @Test
    public void removeAndPublishUsingMultithreading() {
        // run within a loop to increase the likelihood of a race condition to occur
        for (int iterationCounter = 0; iterationCounter < ITERATIONS; ++iterationCounter) {
            HollowProducer.Incremental producer = createInMemoryIncrementalProducer();
            initializeData(producer);

            int[] notModifiedElementIds = IntStream.range(0, ELEMENTS / 2).toArray();
            int[] deletedElementIds = IntStream.range(ELEMENTS / 2, ELEMENTS).toArray();

            long versionAfterDelete = producer.runIncrementalCycle(iws -> {
                Arrays.stream(deletedElementIds).parallel()
                        .mapToObj(i -> new SimpleType(i, i))
                        .forEach(iws::delete);
            });

            /// now we read the changes and assert
            HollowPrimaryKeyIndex idx = createPrimaryKeyIndex(versionAfterDelete);
            Assert.assertTrue(Arrays.stream(notModifiedElementIds)
                    .boxed()
                    .map(elementId -> getHollowObject(idx, elementId))
                    .allMatch(obj -> obj.getInt("value") == obj.getInt("id")));
            Assert.assertTrue(Arrays.stream(deletedElementIds)
                    .boxed()
                    .map(elementId -> getOrdinal(idx, elementId))
                    .allMatch(ordinal -> ordinal == -1));
        }
    }

    private HollowProducer.Incremental createInMemoryIncrementalProducer() {
        return new HollowProducer.Builder<>()
                .withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .buildIncremental();
    }

    private void initializeData(HollowProducer.Incremental producer) {
        producer.runIncrementalCycle(iws -> {
            for (int i = 0; i < ELEMENTS; ++i) {
                iws.addOrModify(new SimpleType(i, i));
            }
        });
    }

    private HollowPrimaryKeyIndex createPrimaryKeyIndex(long version) {
        HollowConsumer consumer = HollowConsumer.withBlobRetriever(blobStore).build();
        consumer.triggerRefreshTo(version);

        return new HollowPrimaryKeyIndex(consumer.getStateEngine(), "SimpleType", "id");
    }

    private GenericHollowObject getHollowObject(HollowPrimaryKeyIndex idx, Object... keys) {
        int ordinal = getOrdinal(idx, keys);
        return new GenericHollowObject(idx.getTypeState(), ordinal);
    }

    private int getOrdinal(HollowPrimaryKeyIndex idx, Object... keys) {
        return idx.getMatchingOrdinal(keys);
    }

    @HollowPrimaryKey(fields = "id")
    private static class SimpleType {

        int id;
        int value;

        SimpleType(int id, int value) {
            this.id = id;
            this.value = value;
        }
    }
}

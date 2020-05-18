package com.netflix.hollow.core.index;

import static java.util.stream.Collectors.toList;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.InMemoryBlobStore;
import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.api.producer.fs.HollowInMemoryBlobStager;
import com.netflix.hollow.core.write.objectmapper.HollowPrimaryKey;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.Assert;
import org.junit.Test;

public class PrimaryKeyIndexDeltaIndexTest {
    @HollowPrimaryKey(fields = "id")
    static class X {
        final int id;

        X(int id) {
            this.id = id;
        }
    }

    @Test
    public void testRemoval() {
        InMemoryBlobStore blobStore = new InMemoryBlobStore();
        HollowProducer p = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .build();

        p.initializeDataModel(X.class);

        // Add all ordinals for first cycle
        // Size appropriately so the addition and removal keep within the same hash code table size
        int upper = (1 << 11) + 512;
        long version = p.runCycle(ws -> {
            IntStream.range(0, upper).parallel().
                    forEach(i -> {
                        ws.add(new X(i));
                    });
        });

        HollowConsumer consumer = HollowConsumer.withBlobRetriever(blobStore).build();
        consumer.triggerRefreshTo(version);

        HollowPrimaryKeyIndex index = new HollowPrimaryKeyIndex(consumer.getStateEngine(), "X", "id");
        index.listenForDeltaUpdates();

        List<Integer> indexes = IntStream.range(0, upper).boxed().collect(toList());
        for (int j = 0; j < 10; j++) {
            Collections.shuffle(indexes);

            // Remove 8% of ordinals, selected randomly, to keep within the threshold for a delta update
            int lower = upper * 92 / 100;
            int[] ordinalsToKeep = indexes.stream().limit(lower).mapToInt(i -> i).toArray();
            version = p.runCycle(ws -> {
                IntStream.of(ordinalsToKeep).parallel().
                        forEach(i -> {
                            ws.add(new X(i));
                        });
            });
            consumer.triggerRefreshTo(version);

            int[] matches = IntStream.range(0, upper)
                    .filter(i -> index.getMatchingOrdinal(i) != -1)
                    .sorted()
                    .toArray();

            Arrays.sort(ordinalsToKeep);
            Assert.assertArrayEquals(ordinalsToKeep, matches);
            Assert.assertFalse(index.containsDuplicates());


            // Add all ordinals back
            version = p.runCycle(ws -> {
                IntStream.range(0, upper).parallel().
                        forEach(i -> {
                            ws.add(new X(i));
                        });
            });
            consumer.triggerRefreshTo(version);

            matches = IntStream.range(0, upper)
                    .filter(i -> index.getMatchingOrdinal(i) != -1)
                    .sorted()
                    .toArray();

            Assert.assertEquals(upper, matches.length);
            Assert.assertFalse(index.containsDuplicates());
        }
    }
}

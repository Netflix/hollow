package com.netflix.hollow.core.write;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.api.producer.fs.HollowInMemoryBlobStager;
import com.netflix.hollow.test.InMemoryBlobStore;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Test;

public class HollowTypeWriteStateTest {

    // SNAP: TODO: already has collections
    @Test
    public void testReverseDeltaNumShardsWhenNewTypes() {
        InMemoryBlobStore blobStore = new InMemoryBlobStore();
        HollowInMemoryBlobStager blobStager = new HollowInMemoryBlobStager();

        HollowProducer p1 = HollowProducer.withPublisher(blobStore).withBlobStager(blobStager).build();
        long v1 = p1.runCycle(ws -> ws.add("s1"));

        // add a new object type and all collection types to data model
        HollowProducer p2 = HollowProducer.withPublisher(blobStore).withBlobStager(blobStager).build();
        p2.initializeDataModel(HasAllTypes.class);
        p2.restore(v1, blobStore);
        long v2 = p2.runCycle(state -> {
            HasAllTypes o1 = new HasAllTypes(
                    new CustomReferenceType(5l),
                    new HashSet<>(Arrays.asList("e1")),
                    Arrays.asList(1, 2, 3),
                    new HashMap<String, Long>(){{put("k1", 1L);}}
            );
            state.add(o1);
        });

        HollowConsumer consumer = HollowConsumer.withBlobRetriever(blobStore)
                .withDoubleSnapshotConfig(new HollowConsumer.DoubleSnapshotConfig() {
                    @Override
                    public boolean allowDoubleSnapshot() {
                        return false;
                    }
                    @Override
                    public int maxDeltasBeforeDoubleSnapshot() {
                        return Integer.MAX_VALUE;
                    }
                })
                .build();
        consumer.triggerRefreshTo(v2);

        int numShardsObject = consumer.getStateEngine().getTypeState("CustomReferenceType").numShards();
        assertTrue(numShardsObject > 0);
        int numShardsList = consumer.getStateEngine().getTypeState("ListOfInteger").numShards();
        assertTrue(numShardsList > 0);
        int numShardsSet = consumer.getStateEngine().getTypeState("SetOfString").numShards();
        assertTrue(numShardsSet > 0);
        int numShardsMap = consumer.getStateEngine().getTypeState("MapOfStringToLong").numShards();
        assertTrue(numShardsMap > 0);

        consumer.triggerRefreshTo(v1);
        assertEquals(v1, consumer.getCurrentVersionId());
        assertEquals(numShardsObject, consumer.getStateEngine().getTypeState("CustomReferenceType").numShards());
        assertEquals(numShardsList, consumer.getStateEngine().getTypeState("ListOfInteger").numShards());
        assertEquals(numShardsSet, consumer.getStateEngine().getTypeState("SetOfString").numShards());
        assertEquals(numShardsMap, consumer.getStateEngine().getTypeState("MapOfStringToLong").numShards());
    }

    private class HasAllTypes {
        CustomReferenceType customReferenceType;
        Set<String> setOfStrings;
        List<Integer> listOfInt;
        Map<String, Long> mapOfStringToLong;

        private HasAllTypes(CustomReferenceType customReferenceType, Set<String> setOfStrings, List<Integer> listOfInt, Map<String, Long> mapOfStringToLong) {
            this.customReferenceType = customReferenceType;
            this.setOfStrings = setOfStrings;
            this.listOfInt = listOfInt;
            this.mapOfStringToLong = mapOfStringToLong;
        }
    }

    private class CustomReferenceType {
        long id;
        private CustomReferenceType(long id) {
            this.id = id;
        }
    }
}

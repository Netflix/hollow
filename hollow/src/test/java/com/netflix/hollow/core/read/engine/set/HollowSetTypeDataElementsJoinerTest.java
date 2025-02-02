package com.netflix.hollow.core.read.engine.set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.api.producer.fs.HollowInMemoryBlobStager;
import com.netflix.hollow.core.read.engine.HollowBlobReader;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.write.HollowObjectWriteRecord;
import com.netflix.hollow.core.write.HollowSetWriteRecord;
import com.netflix.hollow.test.InMemoryBlobStore;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.Assert;
import org.junit.Test;

public class HollowSetTypeDataElementsJoinerTest extends AbstractHollowSetTypeDataElementsSplitJoinTest {
    @Override
    protected void initializeTypeStates() {
        super.initializeTypeStates();
        writeStateEngine.setTargetMaxTypeShardSize(16);
    }

    @Test
    public void testJoin() throws IOException {
        int[][] setContents = new int[][] {
                {0, 1, 2}
        };
        HollowSetTypeReadState typeReadState = populateTypeStateWith(setContents);
        assertEquals(1, typeReadState.numShards());

        setContents = new int[][] {
                {0, 1, 2},
                {3},
                {}
        };
        int setSize = 50;
        setContents[2] = new int[setSize];
        for (int i=4; i<setSize; i++) {
            setContents[2][i] = i;
        }
        HollowSetTypeReadState typeReadStateSharded = populateTypeStateWith(setContents);
        assertDataUnchanged(typeReadStateSharded, setContents);
        assertEquals(8, typeReadStateSharded.numShards());

        HollowSetTypeDataElementsJoiner joiner = new HollowSetTypeDataElementsJoiner(typeReadStateSharded.currentDataElements());
        HollowSetTypeDataElements joinedDataElements = joiner.join();

        typeReadState = new HollowSetTypeReadState(typeReadState.getSchema(), joinedDataElements);
        assertDataUnchanged(typeReadState, setContents);

        try {
            joiner = new HollowSetTypeDataElementsJoiner(mockSetTypeState.currentDataElements());
            joiner.join();
            Assert.fail();
        } catch (IllegalStateException e) {
            // expected, numSplits should be a power of 2
        }
    }

    // tests data integrity and delta chain traversal when re-sharding in the presence of lopsided shards (different maxOrdinals)
    @Test
    public void testJoinDifferentFieldWidths() throws IOException {
        HollowSetTypeReadState typeReadStateSmall = populateTypeStateWith(new int[][] {{1}});
        assertEquals(1, typeReadStateSmall.numShards());
        HollowSetTypeDataElements dataElementsSmall = typeReadStateSmall.currentDataElements()[0];
        int widthSmall = dataElementsSmall.bitsPerElement;
        long valSmall = dataElementsSmall.elementData.getElementValue(0, widthSmall);

        int[] bigVals = new int[] {1000, 2000};
        HollowSetTypeReadState typeReadStateBig = populateTypeStateWith(new int[][] {bigVals});
        Set<Integer> setOfBigVals =  IntStream.of(bigVals).boxed().collect(Collectors.toSet());
        assertEquals(1, typeReadStateBig.numShards());
        HollowSetTypeDataElements dataElementsBig = typeReadStateBig.currentDataElements()[0];
        int widthBig = dataElementsBig.bitsPerElement;
        long bucketStart = 0;
        long valBig = dataElementsBig.elementData.getElementValue(bucketStart, widthBig);
        while (valBig == dataElementsBig.emptyBucketValue) {
            bucketStart += widthBig;
            valBig = dataElementsBig.elementData.getElementValue(bucketStart, widthBig);
        }

        HollowSetTypeDataElementsJoiner joiner = new HollowSetTypeDataElementsJoiner(new HollowSetTypeDataElements[]
                {dataElementsSmall, dataElementsBig});
        HollowSetTypeDataElements dataElementsJoined = joiner.join();
        int widthJoined = dataElementsJoined.bitsPerElement;

        long valSmallJoined = dataElementsJoined.elementData.getElementValue(0, widthJoined);
        bucketStart = dataElementsJoined.getStartBucket(1);
        long bucketEnd = dataElementsJoined.getEndBucket(1);
        Set<Integer> bigValsJoined = new HashSet<>();
        for (long bucket=bucketStart;bucket<bucketEnd;bucket++)  {
            int val = dataElementsJoined.getBucketValue(bucket);
            if (val != dataElementsJoined.emptyBucketValue) {
                bigValsJoined.add(val);
            }
        }

        assertTrue(widthBig > widthSmall);
        assertEquals(widthBig, widthJoined);
        assertEquals(valSmall, valSmallJoined);
        assertEquals(setOfBigVals, bigValsJoined);
    }

    // tests data integrity and delta chain traversal when re-sharding in the presence of lopsided shards (different maxOrdinals)
    @Test
    public void testLopsidedMaxOrdinalShards() throws IOException {
        InMemoryBlobStore blobStore = new InMemoryBlobStore();
        HollowProducer p = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .withTypeResharding(true)
                .build();

        p.initializeDataModel(setSchema, schema);
        int targetSize = 16;
        p.getWriteEngine().setTargetMaxTypeShardSize(targetSize);
        long v1 = oneRunCycle(p, new int[][] {{0}, {1}, {2}, {3}, {4}, {5}, {6}, {7}, {8}, {9}, {10}});

        HollowConsumer c = HollowConsumer
                .withBlobRetriever(blobStore)
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
                .withSkipTypeShardUpdateWithNoAdditions()
                .build();
        c.triggerRefreshTo(v1);

        assertEquals(2, c.getStateEngine().getTypeState("TestSet").numShards()); // shard[0] maxOrdinal 5, shard[1] maxOrdinal 4
        assertEquals(5, c.getStateEngine().getTypeState("TestSet").getShardsVolatile().getShards()[0].getDataElements().maxOrdinal);
        assertEquals(4, c.getStateEngine().getTypeState("TestSet").getShardsVolatile().getShards()[1].getDataElements().maxOrdinal);
        assertEquals(true, c.getStateEngine().isSkipTypeShardUpdateWithNoAdditions());

        long v2 = oneRunCycle(p, new int[][] {{0}, {1}, {3}, {5}, {7}, {9}});
        c.triggerRefreshTo(v2);
        assertEquals(5, c.getStateEngine().getTypeState("TestSet").getShardsVolatile().getShards()[0].getDataElements().maxOrdinal);
        assertEquals(4, c.getStateEngine().getTypeState("TestSet").getShardsVolatile().getShards()[1].getDataElements().maxOrdinal);
        assertEquals(6, c.getStateEngine().getTypeState("TestSet").getPopulatedOrdinals().cardinality());
        assertEquals(2, c.getStateEngine().getTypeState("TestSet").numShards()); // ghost records accounted for in shard size, shards not joined yet

        // v2 snapshot was also serialized with same numShards as delta
        HollowReadStateEngine testSnapshot = new HollowReadStateEngine();
        HollowBlobReader reader = new HollowBlobReader(testSnapshot);
        reader.readSnapshot(blobStore.retrieveSnapshotBlob(v2).getInputStream());
        assertEquals(c.getStateEngine().getTypeState("TestSet").numShards(), testSnapshot.getTypeState("TestSet").numShards());

        long v3 = oneRunCycle(p, new int[][] {{0}, {1}, {3}, {5}, {7}, {9}, {11}}); // {2} is a ghost
        c.triggerRefreshTo(v3);
        assertEquals(1, c.getStateEngine().getTypeState("TestSet").numShards());
        assertDataUnchanged((HollowSetTypeReadState) c.getStateEngine().getTypeState("TestSet"), new int[][] {{0}, {1}, {3}, {5}, {7}, {9}, {11}});

        long v4 = oneRunCycle(p, new int[][] {{0}, {1}, {3}});
        c.triggerRefreshTo(v4);
        assertDataUnchanged((HollowSetTypeReadState) c.getStateEngine().getTypeState("TestSet"), new int[][] {{0}, {1}, {3}});

        c.triggerRefreshTo(v3);
        assertDataUnchanged((HollowSetTypeReadState) c.getStateEngine().getTypeState("TestSet"), new int[][] {{0}, {1}, {3}, {5}, {7}, {9}, {11}});

        c.triggerRefreshTo(v2);
        assertDataUnchanged((HollowSetTypeReadState) c.getStateEngine().getTypeState("TestSet"), new int[][] {{0}, {1}, {3}, {5}, {7}, {9}});
        assertEquals(2, c.getStateEngine().getTypeState("TestSet").numShards());

        c.triggerRefreshTo(v4);
        assertDataUnchanged((HollowSetTypeReadState) c.getStateEngine().getTypeState("TestSet"), new int[][] {{0}, {1}, {3}});
    }

    @Test
    public void testLopsidedStatsShards() {
        InMemoryBlobStore blobStore = new InMemoryBlobStore();
        HollowProducer p = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .withTypeResharding(true)
                .build();

        p.initializeDataModel(setSchema, schema);
        int targetSize = 16;
        p.getWriteEngine().setTargetMaxTypeShardSize(targetSize);
        long v1 = oneRunCycle(p, new int[][] {{0}, {1}, {2}, {3}, {4}, {5}, {6}, {7}, {8}, {9}});

        HollowConsumer c = HollowConsumer
                .withBlobRetriever(blobStore)
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
                .withSkipTypeShardUpdateWithNoAdditions()
                .build();
        c.triggerRefreshTo(v1);

        assertEquals(2, c.getStateEngine().getTypeState("TestSet").numShards());
        assertEquals(true, c.getStateEngine().isSkipTypeShardUpdateWithNoAdditions());
        HollowSetTypeDataElements dataElements0 = (HollowSetTypeDataElements) c.getStateEngine().getTypeState("TestSet").getShardsVolatile().getShards()[0].getDataElements();
        HollowSetTypeDataElements dataElements1 = (HollowSetTypeDataElements) c.getStateEngine().getTypeState("TestSet").getShardsVolatile().getShards()[1].getDataElements();
        assertEquals(4, dataElements0.bitsPerElement);
        assertEquals(4, dataElements0.bitsPerSetPointer);
        assertEquals(1, dataElements0.bitsPerSetSizeValue);
        assertEquals(5, dataElements0.bitsPerFixedLengthSetPortion);
        assertEquals(15, dataElements0.emptyBucketValue);
        assertEquals(4, dataElements1.bitsPerElement); // shards have similar stats
        assertEquals(4, dataElements1.bitsPerSetPointer);
        assertEquals(1, dataElements1.bitsPerSetSizeValue);
        assertEquals(5, dataElements1.bitsPerFixedLengthSetPortion);
        assertEquals(15, dataElements1.emptyBucketValue);

        long v2 = oneRunCycle(p, new int[][] {{0}, {1}, {2}, {3}, {4}, {5}, {6}, {7}, {8}, {9}, {1, 2, 3, 16}});
        c.triggerRefreshTo(v2);
        assertEquals(2, c.getStateEngine().getTypeState("TestSet").numShards());
        dataElements0 = (HollowSetTypeDataElements) c.getStateEngine().getTypeState("TestSet").getShardsVolatile().getShards()[0].getDataElements();
        dataElements1 = (HollowSetTypeDataElements) c.getStateEngine().getTypeState("TestSet").getShardsVolatile().getShards()[1].getDataElements();

        assertEquals(5, dataElements0.bitsPerElement);
        assertEquals(5, dataElements0.bitsPerSetPointer);
        assertEquals(3, dataElements0.bitsPerSetSizeValue);
        assertEquals(8, dataElements0.bitsPerFixedLengthSetPortion);
        assertEquals(31, dataElements0.emptyBucketValue);
        assertEquals(4, dataElements1.bitsPerElement); // shards have non-similar stats, thanks to withSkipTypeShardUpdateWithNoAdditions
        assertEquals(4, dataElements1.bitsPerSetPointer);
        assertEquals(1, dataElements1.bitsPerSetSizeValue);
        assertEquals(5, dataElements1.bitsPerFixedLengthSetPortion);
        assertEquals(15, dataElements1.emptyBucketValue);

        long v3 = oneRunCycle(p, new int[][] {{0}, {1}, {1, 2, 3, 16}});
        c.triggerRefreshTo(v3);
        assertEquals(2, c.getStateEngine().getTypeState("TestSet").numShards());

        long v4 = oneRunCycle(p, new int[][] {{0}});
        c.triggerRefreshTo(v4);
        assertEquals(1, c.getStateEngine().getTypeState("TestSet").numShards());
        assertDataUnchanged((HollowSetTypeReadState) c.getStateEngine().getTypeState("TestSet"), new int[][] {{0}});

        long v5 = oneRunCycle(p, new int[][] {{0}, {1}, {3}, {1, 2, 3, 16}});
        c.triggerRefreshTo(v5);
        assertDataUnchanged((HollowSetTypeReadState) c.getStateEngine().getTypeState("TestSet"), new int[][] {{0}, {1}, {3}, {1, 2, 3, 16}});

        c.triggerRefreshTo(v1);
        assertDataUnchanged((HollowSetTypeReadState) c.getStateEngine().getTypeState("TestSet"), new int[][] {{0}, {1}, {2}, {3}, {4}, {5}, {6}, {7}, {8}, {9}});

        c.triggerRefreshTo(v2);
        assertDataUnchanged((HollowSetTypeReadState) c.getStateEngine().getTypeState("TestSet"), new int[][] {{0}, {1}, {2}, {3}, {4}, {5}, {6}, {7}, {8}, {9}, {1, 2, 3, 16}});

        c.triggerRefreshTo(v5);
        assertDataUnchanged((HollowSetTypeReadState) c.getStateEngine().getTypeState("TestSet"), new int[][] {{0}, {1}, {3}, {1, 2, 3, 16}});
    }

    private long oneRunCycle(HollowProducer p, int setContents[][]) {
        return p.runCycle(state -> {
            int maxElement = -1;
            for(int[] set : setContents) {
                for(int element : set) {
                    if (element > maxElement) {
                        maxElement = element;
                    }
                }
            }
            for(int i=0; i<=maxElement; i++) {
                HollowObjectWriteRecord rec = new HollowObjectWriteRecord(schema);
                rec.setLong("longField", i);
                rec.setString("stringField", "Value" + i);
                rec.setInt("intField", i);
                rec.setDouble("doubleField", i);

                state.getStateEngine().add("TestObject", rec);
            }
            for(int[] set : setContents) {
                HollowSetWriteRecord rec = new HollowSetWriteRecord();
                for (int ordinal : set) {
                    rec.addElement(ordinal);
                }
                state.getStateEngine().add("TestSet", rec);
            }
        });
    }
}

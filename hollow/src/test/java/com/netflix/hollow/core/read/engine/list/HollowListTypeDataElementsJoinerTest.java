package com.netflix.hollow.core.read.engine.list;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.api.producer.fs.HollowInMemoryBlobStager;
import com.netflix.hollow.core.read.engine.PopulatedOrdinalListener;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;
import com.netflix.hollow.core.write.HollowObjectWriteRecord;
import com.netflix.hollow.core.write.HollowSetWriteRecord;
import com.netflix.hollow.test.InMemoryBlobStore;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Test;

public class HollowListTypeDataElementsJoinerTest extends AbstractHollowListTypeDataElementsSplitJoinTest {
    @Override
    protected void initializeTypeStates() {
        super.initializeTypeStates();
        writeStateEngine.setTargetMaxTypeShardSize(16);
    }

    @Test
    public void testJoin() throws IOException {
        int[][] listContents = new int[][] {
                {1000, 2000, 3000}};
        HollowListTypeReadState typeReadState = populateTypeStateWith(listContents);
        assertEquals(1, typeReadState.numShards());

        listContents = new int[][] {
                {1000, 2000, 3000},
                {0},
                {}
        };
        int listLen = 50;
        listContents[2] = new int[listLen];
        for (int i=0; i<listLen; i++) {
            listContents[2][i] = (int) Math.pow(2,7) - i;
        }
        HollowListTypeReadState typeReadStateSharded = populateTypeStateWith(listContents);
        assertDataUnchanged(typeReadStateSharded, listContents);
        assertEquals(8, typeReadStateSharded.numShards());

        HollowListTypeDataElementsJoiner joiner = new HollowListTypeDataElementsJoiner(typeReadStateSharded.currentDataElements());
        HollowListTypeDataElements joinedDataElements = joiner.join();

        typeReadState = new HollowListTypeReadState(typeReadState.getSchema(), joinedDataElements);
        assertDataUnchanged(typeReadState, listContents);

        try {
            joiner = new HollowListTypeDataElementsJoiner(mockListTypeState.currentDataElements());
            joiner.join();
            Assert.fail();
        } catch (IllegalStateException e) {
            // expected, numSplits should be a power of 2
        }
    }

    @Test
    public void testJoinDifferentFieldWidths() throws IOException {
        HollowListTypeReadState typeReadStateSmall = populateTypeStateWith(new int[][] {{1}});
        assertEquals(1, typeReadStateSmall.numShards());
        HollowListTypeDataElements dataElementsSmall = typeReadStateSmall.currentDataElements()[0];
        int widthSmall = dataElementsSmall.bitsPerElement;
        long valSmall = dataElementsSmall.elementData.getElementValue(0, widthSmall);


        int bigListLen = 5;
        int[][] bigListContents = new int[3][bigListLen];
        for (int i=0; i<bigListLen; i++) {
            bigListContents[2][i] = (int) Math.pow(2,7) - i;
        }

        HollowListTypeReadState typeReadStateBig = populateTypeStateWith(bigListContents);
        assertEquals(1, typeReadStateBig.numShards());
        HollowListTypeDataElements dataElementsBig = typeReadStateBig.currentDataElements()[0];
        int widthBig = dataElementsBig.bitsPerElement;

        assertTrue(widthBig > widthSmall);

        HollowListTypeDataElementsJoiner joiner = new HollowListTypeDataElementsJoiner(new HollowListTypeDataElements[]
                {dataElementsSmall, dataElementsBig});
        HollowListTypeDataElements dataElementsJoined = joiner.join();
        int widthJoined = dataElementsJoined.bitsPerElement;

        long val0 = dataElementsJoined.elementData.getElementValue(0, widthJoined);

        assertEquals(widthBig, widthJoined);
        assertEquals(valSmall, val0);
    }

    @Test
    public void testLopsidedStatsShards() {
        InMemoryBlobStore blobStore = new InMemoryBlobStore();
        HollowProducer p = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .withTypeResharding(true)
                .build();

        p.initializeDataModel(listSchema, schema);
        int targetSize = 16;
        p.getWriteEngine().setTargetMaxTypeShardSize(targetSize);
        long v1 = oneRunCycle(p, new int[][] {{0}, {1}, {2}, {3}, {4}, {5}, {6}, {7}, {8}, {9}, {10}, {11}, {12}, {13}, {14}, {15}});

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

        assertEquals(2, c.getStateEngine().getTypeState("TestList").numShards());
        assertEquals(true, c.getStateEngine().isSkipTypeShardUpdateWithNoAdditions());
        HollowListTypeDataElements dataElements0 = (HollowListTypeDataElements) c.getStateEngine().getTypeState("TestList").getShardsVolatile().getShards()[0].getDataElements();
        HollowListTypeDataElements dataElements1 = (HollowListTypeDataElements) c.getStateEngine().getTypeState("TestList").getShardsVolatile().getShards()[1].getDataElements();
        assertEquals(4, dataElements0.bitsPerElement);
        assertEquals(4, dataElements0.bitsPerListPointer);
        assertEquals(8, dataElements0.totalNumberOfElements);
        assertEquals(7, dataElements0.maxOrdinal);
        assertEquals(4, dataElements1.bitsPerElement); // shards have similar stats
        assertEquals(4, dataElements1.bitsPerListPointer);
        assertEquals(8, dataElements1.totalNumberOfElements);
        assertEquals(7, dataElements1.maxOrdinal);

        long v2 = oneRunCycle(p, new int[][] {{0}, {1}, {2}, {3}, {4}, {5}, {6}, {7}, {8}, {9}, {10}, {11}, {12}, {13}, {14}, {15}, {16}});
        c.triggerRefreshTo(v2);
        assertEquals(2, c.getStateEngine().getTypeState("TestList").numShards());
        dataElements0 = (HollowListTypeDataElements) c.getStateEngine().getTypeState("TestList").getShardsVolatile().getShards()[0].getDataElements();
        dataElements1 = (HollowListTypeDataElements) c.getStateEngine().getTypeState("TestList").getShardsVolatile().getShards()[1].getDataElements();
        assertEquals(5, dataElements0.bitsPerElement);
        assertEquals(4, dataElements0.bitsPerListPointer);
        assertEquals(9, dataElements0.totalNumberOfElements);
        assertEquals(8, dataElements0.maxOrdinal);
        assertEquals(4, dataElements1.bitsPerElement); // shards have non-similar stats
        assertEquals(4, dataElements1.bitsPerListPointer);
        assertEquals(8, dataElements1.totalNumberOfElements);
        assertEquals(7, dataElements1.maxOrdinal);

        long v3 = oneRunCycle(p, new int[][] {{0}, {16}});
        c.triggerRefreshTo(v3);
        assertEquals(2, c.getStateEngine().getTypeState("TestList").numShards());

        long v4 = oneRunCycle(p, new int[][] {{0}});
        c.triggerRefreshTo(v4);
        assertEquals(1, c.getStateEngine().getTypeState("TestList").numShards());
        assertValuesUnchanged((HollowListTypeReadState) c.getStateEngine().getTypeState("TestList"), new int[][] {{0}});

        long v5 = oneRunCycle(p, new int[][] {{0}, {1}, {16}});
        c.triggerRefreshTo(v5);
        assertValuesUnchanged((HollowListTypeReadState) c.getStateEngine().getTypeState("TestList"), new int[][] {{0}, {1}, {16}});

        c.triggerRefreshTo(v1);
        assertValuesUnchanged((HollowListTypeReadState) c.getStateEngine().getTypeState("TestList"), new int[][] {{0}, {1}, {2}, {3}, {4}, {5}, {6}, {7}, {8}, {9}, {10}, {11}, {12}, {13}, {14}, {15}});

        c.triggerRefreshTo(v2);
        assertValuesUnchanged((HollowListTypeReadState) c.getStateEngine().getTypeState("TestList"), new int[][] {{0}, {1}, {2}, {3}, {4}, {5}, {6}, {7}, {8}, {9}, {10}, {11}, {12}, {13}, {14}, {15}, {16}});

        c.triggerRefreshTo(v5);
        assertValuesUnchanged((HollowListTypeReadState) c.getStateEngine().getTypeState("TestList"), new int[][] {{0}, {1}, {16}});
    }

    private long oneRunCycle(HollowProducer p, int listContents[][]) {
        return p.runCycle(state -> {
            int maxElement = -1;
            for(int[] list : listContents) {
                for(int element : list) {
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
            for(int[] list : listContents) {
                HollowSetWriteRecord rec = new HollowSetWriteRecord();
                for (int ordinal : list) {
                    rec.addElement(ordinal);
                }
                state.getStateEngine().add("TestList", rec);
            }
        });
    }

    protected void assertValuesUnchanged(HollowListTypeReadState typeState, int[][] listContents) {
        int numListRecords = listContents.length;
        if (typeState.getListener(PopulatedOrdinalListener.class) != null) {
            assertEquals(listContents.length, typeState.getPopulatedOrdinals().cardinality());
        }
        for(int i=0;i<numListRecords;i++) {
            List<Integer> expected = Arrays.stream(listContents[i]).boxed().collect(Collectors.toList());
            boolean matched = false;
            List<Integer> actual = null;
            for (int listRecordOridnal=0; listRecordOridnal<=typeState.maxOrdinal(); listRecordOridnal++) {
                HollowOrdinalIterator iter = typeState.ordinalIterator(listRecordOridnal);
                actual = new ArrayList<>();
                int o = iter.next();
                while (o != HollowOrdinalIterator.NO_MORE_ORDINALS) {
                    actual.add(((HollowObjectTypeReadState) typeState.getStateEngine().getTypeState("TestObject")).readInt(o, 2));
                    o = iter.next();
                }
                if (actual.equals(expected)) {
                    matched = true;
                    break;
                }
            }
            if (!actual.equals(expected)) {
                System.out.println("// SNAP: TODO: Remove this and similar");
            }

            assertTrue(matched);
        }
    }
}

package com.netflix.hollow.core.read.engine.map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.api.producer.fs.HollowInMemoryBlobStager;
import com.netflix.hollow.core.write.HollowMapWriteRecord;
import com.netflix.hollow.core.write.HollowObjectWriteRecord;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.test.InMemoryBlobStore;
import com.netflix.hollow.tools.checksum.HollowChecksum;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;

public class HollowMapTypeDataElementsJoinerTest extends AbstractHollowMapTypeDataElementsSplitJoinTest {
    @Override
    protected void initializeTypeStates() {
        super.initializeTypeStates();
        writeStateEngine.allowTypeResharding(true);
        writeStateEngine.setTargetMaxTypeShardSize(16);
    }

    @Test
    public void testJoin() throws IOException {
        int[][][] maps = new int[][][] { { {1, 1}, {2, 2}, {3, 3} } };
        HollowMapTypeReadState typeReadState = populateTypeStateWith(maps);
        assertEquals(1, typeReadState.numShards());

        maps = new int[][][] {
                { {1, 1}, {2, 2}, {3, 3} },
                { {1, 3}, {2, 1}, {3, 2} },
                {},
            };
        int entryLen = 20;
        maps[2] = new int[entryLen][2];
        for (int i=0; i<entryLen; i++) {
            maps[2][i][0] = (int) Math.pow(2,7) - i;
            maps[2][i][1] = (int) Math.pow(2,7) - i;
        }

        HollowMapTypeReadState typeReadStateSharded = populateTypeStateWith(maps);
        assertDataUnchanged(typeReadStateSharded, maps);
        assertEquals(2, typeReadStateSharded.numShards());

        HollowMapTypeDataElementsJoiner joiner = new HollowMapTypeDataElementsJoiner(typeReadStateSharded.currentDataElements());
        HollowMapTypeDataElements joinedDataElements = joiner.join();

        typeReadState = new HollowMapTypeReadState(typeReadState.getSchema(), joinedDataElements);
        assertDataUnchanged(typeReadState, maps);

        try {
            joiner = new HollowMapTypeDataElementsJoiner(mockMapTypeState.currentDataElements());
            joiner.join();
            Assert.fail();
        } catch (IllegalStateException e) {
            // expected, numSplits should be a power of 2
        }
    }

    @Test
    public void testJoinDifferentFieldWidths() throws IOException {
        writeStateEngine.setTargetMaxTypeShardSize(32);

        HollowMapTypeReadState typeReadStateSmall = populateTypeStateWith(new int[][][] {{{1,2}}});
        assertEquals(1, typeReadStateSmall.numShards());
        HollowMapTypeDataElements dataElementsSmall = typeReadStateSmall.currentDataElements()[0];
        int widthSmall = dataElementsSmall.bitsPerMapEntry;
        long keySmall = dataElementsSmall.entryData.getElementValue(0, dataElementsSmall.bitsPerKeyElement);
        long valSmall = dataElementsSmall.entryData.getElementValue(0 + dataElementsSmall.bitsPerKeyElement, dataElementsSmall.bitsPerValueElement);
        assertEquals(1, keySmall);
        assertEquals(2, valSmall);

        writeStateEngine = new HollowWriteStateEngine();
        super.initializeTypeStates();
        writeStateEngine.allowTypeResharding(true);
        writeStateEngine.setTargetMaxTypeShardSize(32);
        int[][][] bigValueMapOriginal = new int[][][] {
                { {1000, 2000} },
                { {1000, 4000} }
        };
        HollowMapTypeReadState typeReadStateBig = populateTypeStateWith(bigValueMapOriginal);
        assertEquals(1, typeReadStateBig.numShards());
        HollowMapTypeDataElements dataElementsBig = typeReadStateBig.currentDataElements()[0];
        int widthBig = dataElementsBig.bitsPerMapEntry;

        assertTrue(widthBig > widthSmall);

        HollowMapTypeDataElementsJoiner joiner = new HollowMapTypeDataElementsJoiner(new HollowMapTypeDataElements[]
                {dataElementsSmall, dataElementsBig});
        HollowMapTypeDataElements dataElementsJoined = joiner.join();
        int widthJoined = dataElementsJoined.bitsPerMapEntry;

        long keyJoined = dataElementsJoined.entryData.getElementValue(0, dataElementsJoined.bitsPerKeyElement);
        long valJoined = dataElementsJoined.entryData.getElementValue(0 + dataElementsJoined.bitsPerKeyElement, dataElementsJoined.bitsPerValueElement);

        assertEquals(widthBig, widthJoined);
        assertEquals(keySmall, keyJoined);
        assertEquals(valSmall, valJoined);

        int ordinalFirstBig = 1;
        long startBucketFirstBig = dataElementsJoined.getStartBucket(ordinalFirstBig);
        long endBucketFirstBig = dataElementsJoined.getEndBucket(ordinalFirstBig);
        Map<Integer, Integer> bigValueMapJoined = new HashMap<>();
        for (long bucket=startBucketFirstBig;bucket<endBucketFirstBig;bucket++) {
            long key = dataElementsJoined.entryData.getElementValue(bucket * dataElementsJoined.bitsPerMapEntry, dataElementsJoined.bitsPerKeyElement);
            if (key == dataElementsJoined.emptyBucketKeyValue) {
                continue;
            }
            long value = dataElementsJoined.entryData.getElementValue((bucket * dataElementsJoined.bitsPerMapEntry) + dataElementsJoined.bitsPerKeyElement, dataElementsJoined.bitsPerValueElement);
            bigValueMapJoined.put((int) key, (int) value);
        }

        Map<Integer, Integer> expected = new HashMap<Integer, Integer>() {{ put(bigValueMapOriginal[0][0][0], bigValueMapOriginal[0][0][1]); }};
        assertEquals(expected, bigValueMapJoined);
    }

    @Test
    public void testLopsidedStatsShards() {
        InMemoryBlobStore blobStore = new InMemoryBlobStore();
        HollowProducer p = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .withTypeResharding(true)
                .build();

        p.initializeDataModel(mapSchema, schema);
        int targetSize = 16;
        p.getWriteEngine().setTargetMaxTypeShardSize(targetSize);
        long v1 = oneRunCycle(p, new int[][][] {
                { {1, 1}, {2, 2}, {3, 3} },
                { {1, 3}, {2, 1}, {3, 2} },
        });

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

        assertEquals(1, c.getStateEngine().getTypeState("TestMap").numShards());
        assertEquals(true, c.getStateEngine().isSkipTypeShardUpdateWithNoAdditions());

        HollowMapTypeDataElements dataElementsOriginal = (HollowMapTypeDataElements) c.getStateEngine().getTypeState("TestMap").getShardsVolatile().getShards()[0].getDataElements();
        assertEquals(1, dataElementsOriginal.maxOrdinal);
        assertEquals(4, dataElementsOriginal.bitsPerMapPointer);
        assertEquals(2, dataElementsOriginal.bitsPerMapSizeValue);
        assertEquals(6, dataElementsOriginal.bitsPerFixedLengthMapPortion);
        assertEquals(3, dataElementsOriginal.bitsPerKeyElement);
        assertEquals(2, dataElementsOriginal.bitsPerValueElement);
        assertEquals(5, dataElementsOriginal.bitsPerMapEntry);
        assertEquals(7, dataElementsOriginal.emptyBucketKeyValue);
        assertEquals(8, dataElementsOriginal.totalNumberOfBuckets);

        long v2 = oneRunCycle(p, new int[][][] {
                { {1, 1}, {2, 2}, {3, 3} },
                { {1, 3}, {2, 1}, {3, 2} },
                { {1000, 1001}},
        });
        c.triggerRefreshTo(v2);
        assertEquals(2, c.getStateEngine().getTypeState("TestMap").numShards());

        HollowMapTypeDataElements dataElements0 = (HollowMapTypeDataElements) c.getStateEngine().getTypeState("TestMap").getShardsVolatile().getShards()[0].getDataElements();
        HollowMapTypeDataElements dataElements1 = (HollowMapTypeDataElements) c.getStateEngine().getTypeState("TestMap").getShardsVolatile().getShards()[1].getDataElements();
        assertEquals(1, dataElements0.maxOrdinal);  // non-similar stats thanks to withSkipTypeShardUpdateWithNoAdditions
        assertEquals(3, dataElements0.bitsPerMapPointer);
        assertEquals(2, dataElements0.bitsPerMapSizeValue);
        assertEquals(5, dataElements0.bitsPerFixedLengthMapPortion);
        assertEquals(10, dataElements0.bitsPerKeyElement);
        assertEquals(10, dataElements0.bitsPerValueElement);
        assertEquals(20, dataElements0.bitsPerMapEntry);
        assertEquals(1023, dataElements0.emptyBucketKeyValue);
        assertEquals(6, dataElements0.totalNumberOfBuckets);

        assertEquals(0, dataElements1.maxOrdinal);
        assertEquals(3, dataElements1.bitsPerMapPointer);
        assertEquals(dataElementsOriginal.bitsPerMapSizeValue, dataElements1.bitsPerMapSizeValue);
        assertEquals(5, dataElements1.bitsPerFixedLengthMapPortion);
        assertEquals(dataElementsOriginal.bitsPerKeyElement, dataElements1.bitsPerKeyElement);
        assertEquals(dataElementsOriginal.bitsPerValueElement, dataElements1.bitsPerValueElement);
        assertEquals(dataElementsOriginal.bitsPerMapEntry, dataElements1.bitsPerMapEntry);
        assertEquals(dataElementsOriginal.emptyBucketKeyValue, dataElements1.emptyBucketKeyValue);
        assertEquals(4, dataElements1.totalNumberOfBuckets);

        long v3 = oneRunCycle(p, new int[][][] {
                { {1, 1}, {2, 2}, {3, 3} }
        });
        c.triggerRefreshTo(v3);
        assertEquals(2, c.getStateEngine().getTypeState("TestMap").numShards());
        HollowMapTypeReadState typeReadState = (HollowMapTypeReadState) c.getStateEngine().getTypeState("TestMap");
        assertDataUnchanged(typeReadState, new int[][][] {
                { {1, 1}, {2, 2}, {3, 3} }
        });

        long v4 = oneRunCycle(p, new int[][][] {
                { {1, 1}, {2, 2} }
        });
        c.triggerRefreshTo(v4);
        typeReadState = (HollowMapTypeReadState) c.getStateEngine().getTypeState("TestMap");
        assertEquals(1, typeReadState.numShards());
        assertDataUnchanged(typeReadState, new int[][][] {
                { {1, 1}, {2, 2} }
        });

        long v5 = oneRunCycle(p, new int[][][] {
            { {1, 1}, {2, 2}, {3, 3} },
            { {1, 4}, {5, 1}, {13, 2} }
        });
        c.triggerRefreshTo(v5);
        typeReadState = (HollowMapTypeReadState) c.getStateEngine().getTypeState("TestMap");
        assertDataUnchanged(typeReadState, new int[][][] {
                { {1, 1}, {2, 2}, {3, 3} },
                { {1, 4}, {5, 1}, {13, 2} }
        });

        c.triggerRefreshTo(v1);
        assertDataUnchanged((HollowMapTypeReadState) c.getStateEngine().getTypeState("TestMap"), new int[][][] {
                { {1, 1}, {2, 2}, {3, 3} },
                { {1, 3}, {2, 1}, {3, 2} },
        });

        c.triggerRefreshTo(v2);
        assertDataUnchanged((HollowMapTypeReadState) c.getStateEngine().getTypeState("TestMap"), new int[][][] {
                { {1, 1}, {2, 2}, {3, 3} },
                { {1, 3}, {2, 1}, {3, 2} },
                { {1000, 1001}},
        });

        c.triggerRefreshTo(v5);
        assertDataUnchanged((HollowMapTypeReadState) c.getStateEngine().getTypeState("TestMap"), new int[][][] {
                { {1, 1}, {2, 2}, {3, 3} },
                { {1, 4}, {5, 1}, {13, 2} }
        });
    }

    private long oneRunCycle(HollowProducer p, int maps[][][]) {
        return p.runCycle(state -> {
            if (maps.length > 0) {
                int numKeyValueOrdinals = 1 + Arrays.stream(maps)
                        .flatMap(Arrays::stream)
                        .flatMapToInt(Arrays::stream)
                        .max()
                        .orElseThrow(() -> new IllegalArgumentException("Array is empty"));
                // populate write state with that many ordinals
                HollowObjectWriteRecord rec = new HollowObjectWriteRecord(schema);
                for(int i=0;i<numKeyValueOrdinals;i++) {
                    rec.reset();
                    rec.setLong("longField", i);
                    rec.setString("stringField", "Value" + i);
                    rec.setInt("intField", i);
                    rec.setDouble("doubleField", i);
                    state.getStateEngine().add("TestObject", rec);
                }
            }
            for(int[][] map : maps) {
                HollowMapWriteRecord rec = new HollowMapWriteRecord();
                for (int[] entry : map) {
                    // assertEquals(2, entry.length);    // key value pair  // SNAP: TODO: drop this
                    //  empty map is supported
                    if (entry.length == 2) {
                        rec.addEntry(entry[0], entry[1]);
                    }
                }
                state.getStateEngine().add("TestMap", rec);
            }
        });
    }

    @Test
    public void testReshardingWithEmptyMap() {
        InMemoryBlobStore blobStore = new InMemoryBlobStore();
        HollowInMemoryBlobStager inMemoryBlobStager = new HollowInMemoryBlobStager();
        HollowProducer p = HollowProducer.withPublisher(blobStore)
                .withBlobStager(inMemoryBlobStager)
                .withTypeResharding(true)
                .build();

        p.initializeDataModel(mapSchema, schema);
        int targetSize = 16;
        p.getWriteEngine().setTargetMaxTypeShardSize(targetSize);
        long v1 = oneRunCycle(p, new int[][][] {
                { {1, 1}, {2, 2}, {3, 3} },
                { {1, 3}, {2, 1}, {3, 2} },
                { {} }
        });

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
                .build();
        c.triggerRefreshTo(v1);

        assertEquals(1, c.getStateEngine().getTypeState("TestMap").numShards());
        assertEquals(2, c.getStateEngine().getTypeState("TestMap").maxOrdinal());

        HollowMapTypeDataElements dataElementsOriginal = (HollowMapTypeDataElements) c.getStateEngine().getTypeState("TestMap").getShardsVolatile().getShards()[0].getDataElements();
        assertEquals(4, dataElementsOriginal.bitsPerMapPointer);
        assertEquals(2, dataElementsOriginal.bitsPerMapSizeValue);
        assertEquals(6, dataElementsOriginal.bitsPerFixedLengthMapPortion);
        assertEquals(3, dataElementsOriginal.bitsPerKeyElement);
        assertEquals(2, dataElementsOriginal.bitsPerValueElement);
        assertEquals(5, dataElementsOriginal.bitsPerMapEntry);
        assertEquals(7, dataElementsOriginal.emptyBucketKeyValue);
        assertEquals(9, dataElementsOriginal.totalNumberOfBuckets);

        long v2 = oneRunCycle(p, new int[][][] {
                { {1, 1}, {2, 2}, {3, 3} },
                { {1, 3}, {2, 1}, {3, 2} },
                { {} },
                { {1000, 1001}},
        });
        c.triggerRefreshTo(v2);
        assertEquals(2, c.getStateEngine().getTypeState("TestMap").numShards());

        HollowMapTypeDataElements dataElements0 = (HollowMapTypeDataElements) c.getStateEngine().getTypeState("TestMap").getShardsVolatile().getShards()[0].getDataElements();
        HollowMapTypeDataElements dataElements1 = (HollowMapTypeDataElements) c.getStateEngine().getTypeState("TestMap").getShardsVolatile().getShards()[1].getDataElements();
        assertEquals(1, dataElements0.maxOrdinal);  // non-similar stats thanks to withSkipTypeShardUpdateWithNoAdditions
        assertEquals(3, dataElements0.bitsPerMapPointer);
        assertEquals(2, dataElements0.bitsPerMapSizeValue);
        assertEquals(5, dataElements0.bitsPerFixedLengthMapPortion);
        assertEquals(10, dataElements0.bitsPerKeyElement);
        assertEquals(10, dataElements0.bitsPerValueElement);
        assertEquals(20, dataElements0.bitsPerMapEntry);
        assertEquals(1023, dataElements0.emptyBucketKeyValue);
        assertEquals(5, dataElements0.totalNumberOfBuckets);// SNAP: REMEOVE: thumbs up- split carried over the 1 bucket for empty map 4 + 1

        assertEquals(1, dataElements1.maxOrdinal);
        assertEquals(3, dataElements1.bitsPerMapPointer);
        assertEquals(dataElementsOriginal.bitsPerMapSizeValue, dataElements1.bitsPerMapSizeValue);
        assertEquals(5, dataElements1.bitsPerFixedLengthMapPortion);
        assertEquals(10, dataElements1.bitsPerKeyElement);
        assertEquals(10, dataElements1.bitsPerValueElement);
        assertEquals(20, dataElements1.bitsPerMapEntry);
        assertEquals(1023, dataElements1.emptyBucketKeyValue);
        assertEquals(6, dataElements1.totalNumberOfBuckets);

        //  SNAP: TODO: later also test with joining shards, but for now simplified to reuse same producer
//        HollowProducer p2 = HollowProducer.withPublisher(blobStore)
//                .withBlobStager(inMemoryBlobStager)
//                .withTypeResharding(true)
//                .build();
//
//        p2.initializeDataModel(mapSchema, schema);
//        p2.restore(v2, blobStore);
//        p2.getWriteEngine().setTargetMaxTypeShardSize(1024);
        HollowProducer p2 = p;

        long v3 = oneRunCycle(p2, new int[][][] {// SNAP: TODO: INTEGRIY CHECK FAILURE HERE, how can we investigate delta checksum invalid here?
                { {1, 1}, {2, 2}, {3, 3} },
                { {1, 3}, {2, 1}, {3, 2} },
                { {} },
                { {1000, 1001}},
                { {1, 2}, {1, 3} }
        });
        c.triggerRefreshTo(v3);
        assertEquals(1, c.getStateEngine().getTypeState("TestMap").numShards());
        assertEquals(11, dataElements0.totalNumberOfBuckets);// 4, 4, 1, 2, 4
//
//        long v3 = oneRunCycle(p, new int[][][] {
//                { {1, 1}, {2, 2}, {3, 3} }
//        });
//        c.triggerRefreshTo(v3);
//        assertEquals(2, c.getStateEngine().getTypeState("TestMap").numShards());
//        HollowMapTypeReadState typeReadState = (HollowMapTypeReadState) c.getStateEngine().getTypeState("TestMap");
//        assertDataUnchanged(typeReadState, new int[][][] {
//                { {1, 1}, {2, 2}, {3, 3} }
//        });
//
//        long v4 = oneRunCycle(p, new int[][][] {
//                { {1, 1}, {2, 2} }
//        });
//        c.triggerRefreshTo(v4);
//        typeReadState = (HollowMapTypeReadState) c.getStateEngine().getTypeState("TestMap");
//        assertEquals(1, typeReadState.numShards());
//        assertDataUnchanged(typeReadState, new int[][][] {
//                { {1, 1}, {2, 2} }
//        });
//
//        long v5 = oneRunCycle(p, new int[][][] {
//                { {1, 1}, {2, 2}, {3, 3} },
//                { {1, 4}, {5, 1}, {13, 2} }
//        });
//        c.triggerRefreshTo(v5);
//        typeReadState = (HollowMapTypeReadState) c.getStateEngine().getTypeState("TestMap");
//        assertDataUnchanged(typeReadState, new int[][][] {
//                { {1, 1}, {2, 2}, {3, 3} },
//                { {1, 4}, {5, 1}, {13, 2} }
//        });
//
//        c.triggerRefreshTo(v1);
//        assertDataUnchanged((HollowMapTypeReadState) c.getStateEngine().getTypeState("TestMap"), new int[][][] {
//                { {1, 1}, {2, 2}, {3, 3} },
//                { {1, 3}, {2, 1}, {3, 2} },
//        });
//
//        c.triggerRefreshTo(v2);
//        assertDataUnchanged((HollowMapTypeReadState) c.getStateEngine().getTypeState("TestMap"), new int[][][] {
//                { {1, 1}, {2, 2}, {3, 3} },
//                { {1, 3}, {2, 1}, {3, 2} },
//                { {1000, 1001}},
//        });
//
//        c.triggerRefreshTo(v5);
//        assertDataUnchanged((HollowMapTypeReadState) c.getStateEngine().getTypeState("TestMap"), new int[][][] {
//                { {1, 1}, {2, 2}, {3, 3} },
//                { {1, 4}, {5, 1}, {13, 2} }
//        });
    }

    @Test
    public void testEmptyMap_NoResharding() {
        InMemoryBlobStore blobStore = new InMemoryBlobStore();
        HollowInMemoryBlobStager inMemoryBlobStager = new HollowInMemoryBlobStager();
        HollowProducer p = HollowProducer.withPublisher(blobStore)
                .withBlobStager(inMemoryBlobStager)
                .build();

        p.initializeDataModel(mapSchema, schema);
        long v1 = oneRunCycle(p, new int[][][] {
                { {1, 1}, {2, 2}, {3, 3} },
                { {1, 3}, {2, 1}, {3, 2} },
                { {} }
        });

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
                .build();
        c.triggerRefreshTo(v1);

        long v2 = oneRunCycle(p, new int[][][] {
                { {1, 1}, {2, 2}, {3, 3} },
                { {1, 3}, {2, 1}, {3, 2} },
                { {} },
                { {1000, 1001}},
        });
        c.triggerRefreshTo(v2);

        HollowProducer p2 = HollowProducer.withPublisher(blobStore)
                .withBlobStager(inMemoryBlobStager)
                .build();

        p2.initializeDataModel(mapSchema, schema);
        p2.restore(v2, blobStore);

        long v3 = oneRunCycle(p2, new int[][][] {
                { {1, 1}, {2, 2}, {3, 3} },
                { {1, 3}, {2, 1}, {3, 2} },
                { {} },
                { {1000, 1001}},
                { {1, 2}, {1, 3} }
        });
        c.triggerRefreshTo(v3);

        assertEquals(v3, c.getCurrentVersionId());
    }

    private long oneRunCycleRepro(HollowProducer p, int maps[][][]) {
        return p.runCycle(state -> {
            if (maps.length > 0) {
                int numKeyValueOrdinals = 1 + Arrays.stream(maps)
                        .flatMap(Arrays::stream)
                        .flatMapToInt(Arrays::stream)
                        .max()
                        .orElseThrow(() -> new IllegalArgumentException("Array is empty"));
                // populate write state with that many ordinals
                for(int i=0;i<numKeyValueOrdinals;i++) {
                    HollowObjectWriteRecord rec = new HollowObjectWriteRecord(schemaRepro);
                    // rec.setLong("longField", i);
                    rec.setInt("intField", i);

                    state.getStateEngine().add("TestObject", rec);
                }
            }
            for(int[][] map : maps) {
                HollowMapWriteRecord rec = new HollowMapWriteRecord();
                for (int[] entry : map) {
                    // assertEquals(2, entry.length);    // key value pair  // SNAP: TODO: drop this
                    //  empty map is supported
                    if (entry.length == 2) {
                        rec.addEntry(entry[0], entry[1]);
                    }
                }
                state.getStateEngine().add("TestMap", rec);
            }
        });
    }

    @Test
    public void testSimplestRepro() {
        InMemoryBlobStore blobStore = new InMemoryBlobStore();
        HollowInMemoryBlobStager inMemoryBlobStager = new HollowInMemoryBlobStager();
        HollowProducer p = HollowProducer.withPublisher(blobStore)
                .withBlobStager(inMemoryBlobStager)
                .withTypeResharding(true)
                .build();

        p.initializeDataModel(schemaRepro, mapSchema);
        int targetSize = 16;
        p.getWriteEngine().setTargetMaxTypeShardSize(targetSize);
        long v1 = oneRunCycleRepro(p, new int[][][] {
                { {1, 1} }
        });

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
                .build();
        c.triggerRefreshTo(v1);

        assertEquals(1, c.getStateEngine().getTypeState("TestMap").numShards());
        // assertEquals(2, c.getStateEngine().getTypeState("TestObject").numShards());

        long v2 = oneRunCycleRepro(p, new int[][][] {
                { {1, 1} },
                { {1000, 1001}},
        });
        c.triggerRefreshTo(v2);

        HollowChecksum c1 = c.getStateEngine().getTypeState("TestObject").getChecksum(schema);
        c.triggerRefreshTo(v1);
        c.triggerRefreshTo(v2);
        HollowChecksum c2 = c.getStateEngine().getTypeState("TestObject").getChecksum(schema);
        assertEquals(c1, c2);

        System.out.println("Here for snapshot load");
        HollowConsumer cSnapshot = HollowConsumer
                .withBlobRetriever(blobStore)
                .build();
        cSnapshot.triggerRefreshTo(v2);
        HollowChecksum cSnapshotCheckSum = cSnapshot.getStateEngine().getTypeState("TestObject").getChecksum(schema);
        assertEquals(c1, cSnapshotCheckSum);
        cSnapshot.triggerRefreshTo(v1);
        cSnapshot.triggerRefreshTo(v2);
        cSnapshotCheckSum = cSnapshot.getStateEngine().getTypeState("TestObject").getChecksum(schema);
        assertEquals(c1, cSnapshotCheckSum);
        System.out.println("Done snapshot checksum comparison");


        assertEquals(1, c.getStateEngine().getTypeState("TestMap").numShards());
        assertEquals(2, c.getStateEngine().getTypeState("TestObject").numShards());

        long v3 = oneRunCycleRepro(p, new int[][][] { // SNAP: TODO: no shards being compared should be identical! INTEGRIY CHECK FAILURE HERE, how can we investigate delta checksum invalid here?
                { {1, 1} },
                { {1000, 1001}},
                { {1, 2} }
        });
        c.triggerRefreshTo(v3);
    }
}

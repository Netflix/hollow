package com.netflix.hollow.core.read.engine.list;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.netflix.hollow.core.memory.pool.WastefulRecycler;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.HollowTypeReshardingStrategy;
import com.netflix.hollow.core.read.iterator.HollowListOrdinalIterator;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;
import com.netflix.hollow.core.util.StateEngineRoundTripper;
import com.netflix.hollow.core.write.HollowListTypeWriteState;
import com.netflix.hollow.core.write.HollowObjectTypeWriteState;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import java.util.Random;
import org.junit.Test;

public class HollowListTypeReadStateTest extends AbstractHollowListTypeDataElementsSplitJoinTest {

    @Test
    public void usesExistingIteratorByDefault() throws Exception {
        populateWriteStateEngine(3);
        populateWriteStateEngineWithListRecords(new int[][] {{0, 1, 2}});

        readStateEngine = new HollowReadStateEngine(WastefulRecycler.DEFAULT_INSTANCE);
        StateEngineRoundTripper.roundTripSnapshot(writeStateEngine, readStateEngine, null);

        HollowListTypeReadState typeState =
                (HollowListTypeReadState) readStateEngine.getTypeState("TestList");
        HollowOrdinalIterator iterator = typeState.ordinalIterator(0);

        assertTrue(iterator instanceof HollowListOrdinalIterator);
        assertFalse(iterator instanceof HollowListSnapshotOrdinalIterator);
    }

    @Test
    public void usesCompatibleSnapshotIteratorWhenEnabled() throws Exception {
        populateWriteStateEngine(3);
        populateWriteStateEngineWithListRecords(new int[][] {{0, 1, 2}});

        readStateEngine = new HollowReadStateEngine(WastefulRecycler.DEFAULT_INSTANCE);
        readStateEngine.setSnapshotCollectionIterators(true);
        StateEngineRoundTripper.roundTripSnapshot(writeStateEngine, readStateEngine, null);

        HollowListTypeReadState typeState =
                (HollowListTypeReadState) readStateEngine.getTypeState("TestList");
        HollowOrdinalIterator iterator = typeState.ordinalIterator(0);

        assertTrue(iterator instanceof HollowListSnapshotOrdinalIterator);
        assertTrue(iterator instanceof HollowListOrdinalIterator);
    }

    @Test
    public void iteratorRetainsImmutableShardAcrossRefresh() throws Exception {
        populateWriteStateEngine(3);
        populateWriteStateEngineWithListRecords(new int[][] {{0, 1, 2}});

        readStateEngine = new HollowReadStateEngine(WastefulRecycler.DEFAULT_INSTANCE);
        readStateEngine.setSnapshotCollectionIterators(true);
        StateEngineRoundTripper.roundTripSnapshot(writeStateEngine, readStateEngine, null);

        HollowListTypeReadState typeState =
                (HollowListTypeReadState) readStateEngine.getTypeState("TestList");
        HollowOrdinalIterator iterator = typeState.ordinalIterator(0);
        assertEquals(0, iterator.next());

        populateWriteStateEngine(writeStateEngine, schema, 3);
        populateWriteStateEngineWithListRecords(new int[][] {{2}});
        roundTripDelta();

        assertEquals(1, iterator.next());
        assertEquals(2, iterator.next());
        assertEquals(HollowOrdinalIterator.NO_MORE_ORDINALS, iterator.next());
    }

    @Test
    public void iteratorRefreshesRecycledShardAcrossRefresh() throws Exception {
        populateWriteStateEngine(3);
        populateWriteStateEngineWithListRecords(new int[][] {{0, 1, 2}, {0}});
        roundTripSnapshot();
        readStateEngine.setSnapshotCollectionIterators(true);

        HollowListTypeReadState typeState =
                (HollowListTypeReadState) readStateEngine.getTypeState("TestList");
        HollowOrdinalIterator iterator = typeState.ordinalIterator(0);
        assertEquals(0, iterator.next());

        populateWriteStateEngine(writeStateEngine, schema, 3);
        populateWriteStateEngineWithListRecords(new int[][] {{0, 1, 2}, {1}});
        roundTripDelta();

        assertEquals(1, iterator.next());
        assertEquals(2, iterator.next());
        assertEquals(HollowOrdinalIterator.NO_MORE_ORDINALS, iterator.next());
    }

    @Test
    public void testApproximateHoleCostWithShards() throws Exception {
        // Setup with 2 shards
        writeStateEngine = new HollowWriteStateEngine();
        writeStateEngine.addTypeState(new HollowObjectTypeWriteState(schema, 2));
        writeStateEngine.addTypeState(new HollowListTypeWriteState(listSchema, 2));

        // Cycle 1
        // populate TestObject with ordinal values 0 - 7
        populateWriteStateEngine(writeStateEngine, schema, 8);
        // populate TestList with ordinal values 0 - 7
        populateWriteStateEngineWithListRecords(new int[][]{
                {0}, {1}, {2}, {3}, {4}, {5}, {6}, {7}
        });
        roundTripSnapshot();

        // Cycle 2
        // readded all the TestObject records.
        // for TestList:
        // Shard 0 (even: 0=hole, 2=pop, 4=pop, 6=hole) -> 2 hole
        // Shard 1 (odd: 1=hole, 3=hole, 5=hole, 7=hole) -> 4 holes
        populateWriteStateEngine(writeStateEngine, schema, 8);
        populateWriteStateEngineWithListRecords(new int[][]{
                {2}, {4}
        });
        roundTripDelta();

        HollowListTypeReadState typeState = (HollowListTypeReadState) readStateEngine.getTypeState("TestList");
        assertEquals(2, typeState.numShards());
        assertEquals(2, typeState.getPopulatedOrdinals().cardinality());
        HollowListTypeReadStateShard[] shards = (HollowListTypeReadStateShard[])typeState.getShardsVolatile().getShards();
        long holeCost = (shards[0].dataElements.bitsPerListPointer*2L +
                shards[1].dataElements.bitsPerListPointer*4L) >>> 3;
        assertEquals(holeCost, typeState.getApproximateHoleCostInBytes());
    }

    @Test
    public void testResharding() throws Exception {

        for (int shardingFactor : new int[]{2}) { // , 4, 8, 16, 32, 64, 128, 256, 512, 1024
            for(int numRecords=1;numRecords<=1000;numRecords+=new Random().nextInt(100)) {
                int[][] listContents = generateListContents(numRecords);
                HollowListTypeReadState listTypeReadState = populateTypeStateWith(listContents);
                assertDataUnchanged(listTypeReadState, listContents);
                HollowTypeReshardingStrategy reshardingStrategy = HollowTypeReshardingStrategy.getInstance(listTypeReadState);

                // Splitting shards
                {
                    int prevShardCount = listTypeReadState.numShards();
                    int newShardCount = shardingFactor * prevShardCount;
                    reshardingStrategy.reshard(listTypeReadState, listTypeReadState.numShards(), newShardCount);

                    assertEquals(newShardCount, listTypeReadState.numShards());
                    assertEquals(newShardCount, shardingFactor * prevShardCount);
                }
                assertDataUnchanged(listTypeReadState, listContents);

                // Joining shards
                {
                    int prevShardCount = listTypeReadState.numShards();
                    int newShardCount = prevShardCount / shardingFactor;
                    reshardingStrategy.reshard(listTypeReadState, listTypeReadState.numShards(), newShardCount);

                    assertEquals(newShardCount, listTypeReadState.numShards());
                    assertEquals(shardingFactor * newShardCount, prevShardCount);
                }
                assertDataUnchanged(listTypeReadState, listContents);
            }
        }
    }
}

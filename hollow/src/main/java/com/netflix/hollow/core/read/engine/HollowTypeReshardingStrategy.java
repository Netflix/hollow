package com.netflix.hollow.core.read.engine;

import com.netflix.hollow.core.read.engine.list.HollowListTypeReadState;
import com.netflix.hollow.core.read.engine.list.HollowListTypeReshardingStrategy;
import com.netflix.hollow.core.read.engine.map.HollowMapTypeReadState;
import com.netflix.hollow.core.read.engine.map.HollowMapTypeReshardingStrategy;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReshardingStrategy;
import com.netflix.hollow.core.read.engine.set.HollowSetTypeReadState;
import com.netflix.hollow.core.read.engine.set.HollowSetTypeReshardingStrategy;
import java.util.Arrays;

public abstract class HollowTypeReshardingStrategy {
    private final static HollowTypeReshardingStrategy OBJECT_RESHARDING_STRATEGY = new HollowObjectTypeReshardingStrategy();
    private final static HollowTypeReshardingStrategy LIST_RESHARDING_STRATEGY = new HollowListTypeReshardingStrategy();
    private final static HollowTypeReshardingStrategy SET_RESHARDING_STRATEGY = new HollowSetTypeReshardingStrategy();
    private final static HollowTypeReshardingStrategy MAP_RESHARDING_STRATEGY = new HollowMapTypeReshardingStrategy();

    public abstract HollowTypeDataElementsSplitter createDataElementsSplitter(HollowTypeDataElements from, int shardingFactor);

    public abstract HollowTypeDataElementsJoiner createDataElementsJoiner(HollowTypeDataElements[] from);

    public static HollowTypeReshardingStrategy getInstance(HollowTypeReadState typeState) {
        if (typeState instanceof HollowObjectTypeReadState) {
            return OBJECT_RESHARDING_STRATEGY;
        } else if (typeState instanceof HollowListTypeReadState) {
            return LIST_RESHARDING_STRATEGY;
        } else if (typeState instanceof HollowSetTypeReadState) {
            return SET_RESHARDING_STRATEGY;
        } else if (typeState instanceof HollowMapTypeReadState) {
            return MAP_RESHARDING_STRATEGY;
        } else {
            throw new IllegalArgumentException("Unsupported type state: " + typeState.getClass().getName());
        }
    }

    /**
     * Reshards this type state to the desired shard count using O(shard size) space while supporting concurrent reads
     * into the underlying data elements.
     *
     * @param typeState The type state to reshard
     * @param prevNumShards The current number of shards in typeState
     * @param newNumShards The desired number of shards for typeState
     */
    public void reshard(HollowTypeReadState typeState, int prevNumShards, int newNumShards) {
        int shardingFactor = shardingFactor(prevNumShards, newNumShards);
        HollowTypeDataElements[] newDataElements;
        int[] shardOrdinalShifts;

        try {
            if (newNumShards > prevNumShards) { // split existing shards
                // Step 1:  Grow the number of shards. Each original shard will result in N child shards where N is the sharding factor.
                // The child shards will reference into the existing data elements as-is, and reuse existing shardOrdinalShift.
                // However since the shards array is resized, a read will map into the new shard index, as a result a subset of
                // ordinals in each shard will be accessed. In the next "splitting" step, the data elements in these new shards
                // will be filtered to only retain the subset of ordinals that are actually accessed.
                //
                // This is an atomic update to shardsVolatile: full construction happens-before the store to shardsVolatile,
                // in other words a fully constructed object as visible to this thread will be visible to other threads that
                // load the new shardsVolatile.
                typeState.updateShardsVolatile(expandWithOriginalDataElements(typeState.getShardsVolatile(), shardingFactor));

                // Step 2: Split each original data element into N child data elements where N is the sharding factor.
                // Then update each of the N child shards with the respective split of data element, this will be
                // sufficient to serve all reads into this shard. Once all child shards for a pre-split parent
                // shard have been assigned the split data elements, the parent data elements can be discarded.
                for (int i = 0; i < prevNumShards; i++) {
                    HollowTypeDataElements originalDataElements = typeState.getShardsVolatile().getShards()[i].getDataElements();

                    typeState.updateShardsVolatile(splitDataElementsForOneShard(typeState, i, prevNumShards, shardingFactor));

                    typeState.destroyOriginalDataElements(originalDataElements);
                }
                // Re-sharding done.
                // shardsVolatile now contains newNumShards shards where each shard contains
                // a split of original data elements.

            } else { // join existing shards
                // Step 1: Join N data elements to create one, where N is the sharding factor. Then update each of the
                //         N shards to reference the joined result, but with a new shardOrdinalShift.
                //         Reads will continue to reference the same shard index as before, but the new shardOrdinalShift
                //         will help these reads land at the right ordinal in the joined shard. When all N old shards
                //         corresponding to one new shard have been updated, the N pre-join data elements can be destroyed.
                for (int i = 0; i < newNumShards; i++) {
                    HollowTypeDataElements destroyCandidates[] = joinCandidates(typeState, i, shardingFactor);

                    typeState.updateShardsVolatile(joinDataElementsForOneShard(typeState, i, shardingFactor));  // atomic update to shardsVolatile

                    for (int j = 0; j < shardingFactor; j++) {
                        typeState.destroyOriginalDataElements(destroyCandidates[j]);
                    }
                }

                // Step 2: Resize the shards array to only keep the first newNumShards shards.
                newDataElements = typeState.createTypeDataElements(typeState.getShardsVolatile().getShards().length);
                shardOrdinalShifts = new int[typeState.getShardsVolatile().getShards().length];
                copyShardDataElements(typeState.getShardsVolatile(), newDataElements, shardOrdinalShifts);

                HollowTypeReadStateShard[] newShards = Arrays.copyOfRange(typeState.getShardsVolatile().getShards(), 0, newNumShards);
                typeState.updateShardsVolatile(newShards);

                // Re-sharding done.
                // shardsVolatile now contains newNumShards shards where each shard contains
                // a join of original data elements.
            }
        } catch (Exception e) {
            throw new RuntimeException("Error in re-sharding", e);
        }
    }

    /**
     * Given old and new numShards, this method returns the shard resizing multiplier.
     */
    public static int shardingFactor(int oldNumShards, int newNumShards) {
        if (newNumShards <= 0 || oldNumShards <= 0 || newNumShards == oldNumShards) {
            throw new IllegalStateException("Invalid shard resizing, oldNumShards=" + oldNumShards + ", newNumShards=" + newNumShards);
        }

        boolean isNewGreater = newNumShards > oldNumShards;
        int dividend = isNewGreater ? newNumShards : oldNumShards;
        int divisor = isNewGreater ? oldNumShards : newNumShards;

        if (dividend % divisor != 0) {
            throw new IllegalStateException("Invalid shard resizing, oldNumShards=" + oldNumShards + ", newNumShards=" + newNumShards);
        }
        return dividend / divisor;
    }

    private void copyShardDataElements(ShardsHolder from, HollowTypeDataElements[] newDataElements, int[] shardOrdinalShifts) {
        for (int i=0; i<from.getShards().length; i++) {
            newDataElements[i] = from.getShards()[i].getDataElements();
            shardOrdinalShifts[i] = from.getShards()[i].getShardOrdinalShift();
        }
    }

    private HollowTypeDataElements[] joinCandidates(HollowTypeReadState typeState, int indexIntoShards, int shardingFactor) {
        HollowTypeReadStateShard[] shards = typeState.getShardsVolatile().getShards();
        HollowTypeDataElements[] result = typeState.createTypeDataElements(shardingFactor);
        int newNumShards = shards.length / shardingFactor;
        for (int i=0; i<shardingFactor; i++) {
            result[i] = shards[indexIntoShards + (newNumShards*i)].getDataElements();
        }
        return result;
    }

    public HollowTypeReadStateShard[] joinDataElementsForOneShard(HollowTypeReadState typeState, int currentIndex, int shardingFactor) {
        ShardsHolder shardsHolder = typeState.getShardsVolatile();
        int newNumShards = shardsHolder.getShards().length / shardingFactor;
        int newShardOrdinalShift = 31 - Integer.numberOfLeadingZeros(newNumShards);

        HollowTypeDataElements[] joinCandidates = joinCandidates(typeState, currentIndex, shardingFactor);
        HollowTypeDataElementsJoiner joiner = createDataElementsJoiner(joinCandidates);
        HollowTypeDataElements joined = joiner.join();

        HollowTypeReadStateShard[] newShards = Arrays.copyOf(shardsHolder.getShards(), shardsHolder.getShards().length);
        for (int i=0; i<shardingFactor; i++) {
            newShards[currentIndex + (newNumShards*i)] = typeState.createTypeReadStateShard(typeState.getSchema(), joined, newShardOrdinalShift);
        }

        return newShards;
    }

    public HollowTypeReadStateShard[] expandWithOriginalDataElements(ShardsHolder shardsHolder, int shardingFactor) {
        int prevNumShards = shardsHolder.getShards().length;
        int newNumShards = prevNumShards * shardingFactor;
        HollowTypeReadStateShard[] newShards = new HollowTypeReadStateShard[newNumShards];

        for(int i=0; i<prevNumShards; i++) {
            for (int j=0; j<shardingFactor; j++) {
                newShards[i+(prevNumShards*j)] = shardsHolder.getShards()[i];
            }
        }
        return newShards;
        // return shardsHolder.getClass().getConstructor(HollowTypeReadStateShard[].class).newInstance((Object) newShards);
    }

    public HollowTypeReadStateShard[] splitDataElementsForOneShard(HollowTypeReadState typeState, int currentIndex, int prevNumShards, int shardingFactor) {
        ShardsHolder shardsHolder = typeState.getShardsVolatile();
        int newNumShards = shardsHolder.getShards().length;
        int newShardOrdinalShift = 31 - Integer.numberOfLeadingZeros(newNumShards);

        HollowTypeDataElements dataElementsToSplit = shardsHolder.getShards()[currentIndex].getDataElements();
        HollowTypeDataElementsSplitter splitter = createDataElementsSplitter(dataElementsToSplit, shardingFactor);
        HollowTypeDataElements[] splits = splitter.split();

        HollowTypeReadStateShard[] newShards = Arrays.copyOf(shardsHolder.getShards(), shardsHolder.getShards().length);
        for (int i = 0; i < shardingFactor; i ++) {
            newShards[currentIndex + (prevNumShards*i)] = typeState.createTypeReadStateShard(typeState.getSchema(), splits[i], newShardOrdinalShift);
        }
        return newShards;
    }
}

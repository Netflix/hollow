/*
 *  Copyright 2016-2019 Netflix, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */
package com.netflix.hollow.core.read.engine;

import com.netflix.hollow.api.sampling.HollowSampler;
import com.netflix.hollow.core.memory.MemoryMode;
import com.netflix.hollow.core.memory.encoding.GapEncodedVariableLengthIntegerReader;
import com.netflix.hollow.core.memory.pool.ArraySegmentRecycler;
import com.netflix.hollow.core.read.HollowBlobInput;
import com.netflix.hollow.core.read.dataaccess.HollowDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.core.read.engine.list.HollowListTypeReadState;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeDataElements;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeDataElementsJoiner;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeDataElementsSplitter;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadStateShard;
import com.netflix.hollow.core.read.engine.object.ObjectTypeShardsHolder;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.tools.checksum.HollowChecksum;
import java.io.IOException;
import java.util.Arrays;
import java.util.BitSet;
import java.util.stream.Stream;

/**
 * A HollowTypeReadState contains and is the root handle to all the records of a specific type in
 * a {@link HollowReadStateEngine}.
 */
public abstract class HollowTypeReadState implements HollowTypeDataAccess {  // SNAP: Cant add generic here

    protected static final HollowTypeStateListener[] EMPTY_LISTENERS = new HollowTypeStateListener[0];

    protected final HollowReadStateEngine stateEngine;
    protected final MemoryMode memoryMode;
    protected final HollowSchema schema;
    protected HollowTypeStateListener[] stateListeners;

    public HollowTypeReadState(HollowReadStateEngine stateEngine, MemoryMode memoryMode, HollowSchema schema) {
        this.stateEngine = stateEngine;
        this.memoryMode = memoryMode;
        this.schema = schema;
        this.stateListeners = EMPTY_LISTENERS;
    }

    /**
     * Add a {@link HollowTypeStateListener} to this type.
     * @param listener the listener to add
     */
    public void addListener(HollowTypeStateListener listener) {
        HollowTypeStateListener[] newListeners = Arrays.copyOf(stateListeners, stateListeners.length + 1);
        newListeners[newListeners.length - 1] = listener;
        stateListeners = newListeners;
    }

    /**
     * Remove a specific {@link HollowTypeStateListener} from this type.
     * @param listener the listener to remove
     */
    public void removeListener(HollowTypeStateListener listener) {
        if (stateListeners.length == 0)
            return;

        stateListeners = Stream.of(stateListeners)
                .filter(l -> l != listener)
                .toArray(HollowTypeStateListener[]::new);
    }

    /**
     * @return all {@link HollowTypeStateListener}s currently associated with this type.
     */
    public HollowTypeStateListener[] getListeners() {
        return stateListeners;
    }

    /**
     * @param listenerClazz the listener class
     * @return a {@link HollowTypeStateListener} of the specified class currently associated with this type, or
     * null if none is currently attached.
     * @param <L> the type of the listener
     */
    @SuppressWarnings("unchecked")
    public <L extends HollowTypeStateListener> L getListener(Class<L> listenerClazz) {
        for (HollowTypeStateListener listener : stateListeners) {
            if (listenerClazz.isAssignableFrom(listener.getClass())) {
                return (L) listener;
            }
        }
        return null;
    }
    
    /**
     * Returns the BitSet containing the currently populated ordinals in this type state.
     * <p>
     * WARNING: Do not modify the returned BitSet.
     * @return the bit containing the currently populated ordinals
     */
    public BitSet getPopulatedOrdinals() {
        return getListener(PopulatedOrdinalListener.class).getPopulatedOrdinals();
    }
    
    /**
     * Returns the BitSet containing the populated ordinals in this type state prior to the previous delta transition.
     * <p>
     * WARNING: Do not modify the returned BitSet.
     * @return the bit containing the previously populated ordinals
     */
    public BitSet getPreviousOrdinals() {
        return getListener(PopulatedOrdinalListener.class).getPreviousOrdinals();
    }

    /**
     * @return The maximum ordinal currently populated in this type state.
     */
    public abstract int maxOrdinal();

    public abstract void readSnapshot(HollowBlobInput in, ArraySegmentRecycler recycler) throws IOException;

    public abstract void readSnapshot(HollowBlobInput in, ArraySegmentRecycler recycler, int numShards) throws IOException;

    public abstract void applyDelta(HollowBlobInput in, HollowSchema deltaSchema, ArraySegmentRecycler memoryRecycler, int deltaNumShards) throws IOException;

    protected boolean shouldReshard(int currNumShards, int deltaNumShards) {
        return currNumShards!=0 && deltaNumShards!=0 && currNumShards!=deltaNumShards;
    }

    public HollowSchema getSchema() {
        return schema;
    }

    @Override
    public HollowDataAccess getDataAccess() {
        return stateEngine;
    }

    /**
     * @return the {@link HollowReadStateEngine} which this type state belongs to.
     */
    public HollowReadStateEngine getStateEngine() {
        return stateEngine;
    }

    protected void notifyListenerAboutDeltaChanges(GapEncodedVariableLengthIntegerReader removals, GapEncodedVariableLengthIntegerReader additions, int shardNumber, int numShards) {
        for(HollowTypeStateListener stateListener : stateListeners) {
            removals.reset();
            int removedOrdinal = removals.nextElement();
            while(removedOrdinal < Integer.MAX_VALUE) {
                stateListener.removedOrdinal((removedOrdinal * numShards) + shardNumber);
                removals.advance();
                removedOrdinal = removals.nextElement();
            }

            additions.reset();
            int addedOrdinal = additions.nextElement();
            while(addedOrdinal < Integer.MAX_VALUE) {
                stateListener.addedOrdinal((addedOrdinal * numShards) + shardNumber);
                additions.advance();
                addedOrdinal = additions.nextElement();
            }
        }
    }

    public abstract HollowSampler getSampler();

    protected abstract void invalidate();

    public HollowChecksum getChecksum(HollowSchema withSchema) {
        HollowChecksum cksum = new HollowChecksum();
        applyToChecksum(cksum, withSchema);
        return cksum;
    }

    protected abstract void applyToChecksum(HollowChecksum checksum, HollowSchema withSchema);

    @Override
    public HollowTypeReadState getTypeState() {
        return this;
    }
    
    /**
     * @return an approximate accounting of the current heap footprint occupied by this type state.
     */
    public abstract long getApproximateHeapFootprintInBytes();
    
    /**
     * @return an approximate accounting of the current cost of the "ordinal holes" in this type state.
     */
    public abstract long getApproximateHoleCostInBytes();

    /**
     * @return an approximate accounting of the current heap footprint occupied by each shard of this type state.
     */
    public long getApproximateShardSizeInBytes() {
        return getApproximateHeapFootprintInBytes() / numShards();
    }

    /**
     * @return The number of shards into which this type is split.  Sharding is transparent, so this has no effect on normal usage.
     */
    public abstract int numShards();

    public abstract <T extends ShardsHolder> T getShardsVolatile();

    public abstract void setShardsVolatile(ShardsHolder newShardsVolatile);

    /**
     * Reshards this type state to the desired shard count using O(shard size) space while supporting concurrent reads
     * into the underlying data elements.
     *
     * @param newNumShards The desired number of shards
     */
    public void reshard(int newNumShards) {
        int prevNumShards = getShardsVolatile().getShards().length;    // SNAP: TODO: or .shards.length
        int shardingFactor = shardingFactor(prevNumShards, newNumShards);
        HollowObjectTypeDataElements[] newDataElements;
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
                setShardsVolatile(expandWithOriginalDataElements(getShardsVolatile(), shardingFactor));

                // Step 2: Split each original data element into N child data elements where N is the sharding factor.
                // Then update each of the N child shards with the respective split of data element, this will be
                // sufficient to serve all reads into this shard. Once all child shards for a pre-split parent
                // shard have been assigned the split data elements, the parent data elements can be discarded.
                for (int i = 0; i < prevNumShards; i++) {
                    AbstractHollowTypeDataElements originalDataElements = getShardsVolatile().getShards()[i].getDataElements();

                    setShardsVolatile(splitDataElementsForOneShard(getShardsVolatile(), i, prevNumShards, shardingFactor));

                    destroyOriginalDataElements(originalDataElements);
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
                    AbstractHollowTypeDataElements destroyCandidates[] = joinCandidates(getShardsVolatile().getShards(), i, shardingFactor);

                    setShardsVolatile(joinDataElementsForOneShard(getShardsVolatile(), i, shardingFactor));  // atomic update to shardsVolatile

                    for (int j = 0; j < shardingFactor; j++) {
                        destroyOriginalDataElements(destroyCandidates[j]);
                    }
                }

                // Step 2: Resize the shards array to only keep the first newNumShards shards.
                newDataElements = new HollowObjectTypeDataElements[getShardsVolatile().getShards().length];
                shardOrdinalShifts = new int[getShardsVolatile().getShards().length];
                copyShardElements(getShardsVolatile(), newDataElements, shardOrdinalShifts);

                HollowTypeReadStateShard[] newShards = Arrays.copyOfRange(getShardsVolatile().getShards(), 0, newNumShards);
                if (this instanceof HollowObjectTypeReadState) {
                    setShardsVolatile(new ObjectTypeShardsHolder((HollowObjectTypeReadStateShard[]) newShards));
                } else if (this instanceof HollowListTypeReadState) {
                    // newShards[currentIndex + (newNumShards*i)] = new HollowListTypeReadStateShard(joined, newShardOrdinalShift);
                    throw new UnsupportedOperationException("Not yet implemented");
                }
                // setShardsVolatile(   // SNAP: TODO: constructor has HollowObjectTypeReadStateShard[] parameter
                //         getShardsVolatile().getClass().getConstructor(HollowTypeReadStateShard[].class).newInstance(
                //                 newShards
                //         )
                //); // SNAP: TODO: is this better addressed with a factor method in the interface, or by switching over to an abstract class?
                // SNAP: TODO: can do generics with reflective constructor invocation here, or create an abstract method reassignShardsHolder(), or check instanceOf or assignableFrom

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

    private void copyShardElements(ShardsHolder from, AbstractHollowTypeDataElements[] newDataElements, int[] shardOrdinalShifts) {
        for (int i=0; i<from.getShards().length; i++) {
            newDataElements[i] = from.getShards()[i].getDataElements();
            shardOrdinalShifts[i] = from.getShards()[i].getShardOrdinalShift();
        }
    }

    private AbstractHollowTypeDataElements[] joinCandidates(HollowTypeReadStateShard[] shards, int indexIntoShards, int shardingFactor) {
        AbstractHollowTypeDataElements[] result = new HollowObjectTypeDataElements[shardingFactor];
        int newNumShards = shards.length / shardingFactor;
        for (int i=0; i<shardingFactor; i++) {
            result[i] = shards[indexIntoShards + (newNumShards*i)].getDataElements();
        };
        return result;
    }

    public ShardsHolder joinDataElementsForOneShard(ShardsHolder shardsHolder, int currentIndex, int shardingFactor) throws Exception {
        int newNumShards = shardsHolder.getShards().length / shardingFactor;
        int newShardOrdinalShift = 31 - Integer.numberOfLeadingZeros(newNumShards);

        AbstractHollowTypeDataElements[] joinCandidates = joinCandidates(shardsHolder.getShards(), currentIndex, shardingFactor);

        // SNAP: TODO: a better way
        AbstractHollowTypeDataElementsJoiner joiner = null;
        if (this instanceof HollowObjectTypeReadState) {
            joiner = new HollowObjectTypeDataElementsJoiner((HollowObjectTypeDataElements[]) joinCandidates);
        } else if (this instanceof HollowListTypeReadState) {
            // newShards[currentIndex + (newNumShards*i)] = new HollowListTypeReadStateShard(joined, newShardOrdinalShift);
            throw new UnsupportedOperationException("Not yet implemented");
        }
        AbstractHollowTypeDataElements joined = joiner.join();

        HollowTypeReadStateShard[] newShards = Arrays.copyOf(shardsHolder.getShards(), shardsHolder.getShards().length);
        for (int i=0; i<shardingFactor; i++) {
            if (this instanceof HollowObjectTypeReadState) {
                newShards[currentIndex + (newNumShards*i)] = new HollowObjectTypeReadStateShard((HollowObjectSchema) getSchema(), (HollowObjectTypeDataElements) joined, newShardOrdinalShift);
            } else if (this instanceof HollowListTypeReadState) {
                // newShards[currentIndex + (newNumShards*i)] = new HollowListTypeReadStateShard(joined, newShardOrdinalShift);
                throw new UnsupportedOperationException("Not yet implemented");
            }

        }

        return new ObjectTypeShardsHolder((HollowObjectTypeReadStateShard[]) newShards);
        // SNAP: TODO: update enclosing class? may have to move shardsHolder out
        // return shardsHolder.getClass().getConstructor(HollowTypeReadStateShard[].class).newInstance(newShards);
    }

    public ShardsHolder expandWithOriginalDataElements(ShardsHolder shardsHolder, int shardingFactor) throws Exception {
        int prevNumShards = shardsHolder.getShards().length;
        int newNumShards = prevNumShards * shardingFactor;
        HollowTypeReadStateShard[] newShards = new HollowTypeReadStateShard[newNumShards];

        for(int i=0; i<prevNumShards; i++) {
            for (int j=0; j<shardingFactor; j++) {
                newShards[i+(prevNumShards*j)] = shardsHolder.getShards()[i];
            }
        }
        return shardsHolder.getClass().getConstructor(HollowTypeReadStateShard[].class).newInstance((Object) newShards);
    }

    public ShardsHolder splitDataElementsForOneShard(ShardsHolder shardsHolder, int currentIndex, int prevNumShards, int shardingFactor) throws Exception {
        int newNumShards = shardsHolder.getShards().length;
        int newShardOrdinalShift = 31 - Integer.numberOfLeadingZeros(newNumShards);

        AbstractHollowTypeDataElements dataElementsToSplit = shardsHolder.getShards()[currentIndex].getDataElements();
        AbstractHollowTypeDataElementsSplitter splitter = null;
        if (this instanceof HollowObjectTypeReadState) {
            splitter = new HollowObjectTypeDataElementsSplitter((HollowObjectTypeDataElements) dataElementsToSplit, shardingFactor);
        } else if (this instanceof HollowListTypeReadState) {
            // newShards[currentIndex + (newNumShards*i)] = new HollowListTypeReadStateShard(joined, newShardOrdinalShift);
            throw new UnsupportedOperationException("Not yet implemented");
        }
        AbstractHollowTypeDataElements[] splits = splitter.split();

        HollowTypeReadStateShard[] newShards = Arrays.copyOf(shardsHolder.getShards(), shardsHolder.getShards().length);
        for (int i = 0; i < shardingFactor; i ++) {
            if (this instanceof HollowObjectTypeReadState) {
                newShards[currentIndex + (prevNumShards*i)] = new HollowObjectTypeReadStateShard((HollowObjectSchema) getSchema(), (HollowObjectTypeDataElements) splits[i], newShardOrdinalShift);
            } else if (this instanceof HollowListTypeReadState) {
                // newShards[currentIndex + (newNumShards*i)] = new HollowListTypeReadStateShard(joined, newShardOrdinalShift);
                throw new UnsupportedOperationException("Not yet implemented");
            }
        }
        // return shardsHolder.getClass().getConstructor(HollowTypeReadStateShard[].class).newInstance(newShards);
        return new ObjectTypeShardsHolder(newShards);
    }

    private void destroyOriginalDataElements(AbstractHollowTypeDataElements dataElements) {
        dataElements.destroy();
        if (dataElements.encodedRemovals != null) {
            dataElements.encodedRemovals.destroy();
        }
    }
}

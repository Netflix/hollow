/*
 *  Copyright 2016-2021 Netflix, Inc.
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
package com.netflix.hollow.core.write;

import static com.netflix.hollow.core.write.HollowHashableWriteRecord.HashBehavior.IGNORED_HASHES;
import static com.netflix.hollow.core.write.HollowHashableWriteRecord.HashBehavior.UNMIXED_HASHES;

import com.netflix.hollow.core.HollowStateEngine;
import com.netflix.hollow.core.memory.ByteArrayOrdinalMap;
import com.netflix.hollow.core.memory.ByteDataArray;
import com.netflix.hollow.core.memory.ThreadSafeBitSet;
import com.netflix.hollow.core.memory.pool.WastefulRecycler;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.read.engine.PopulatedOrdinalListener;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.write.HollowHashableWriteRecord.HashBehavior;
import com.netflix.hollow.core.write.copy.HollowRecordCopier;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.BitSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The {@link HollowTypeWriteState} contains and is the root handle to all of the records of a specific type in
 * a {@link HollowWriteStateEngine}.
 */
public abstract class HollowTypeWriteState {
    private static final Logger LOG = Logger.getLogger(HollowTypeWriteState.class.getName());

    protected final HollowSchema schema;

    protected final ByteArrayOrdinalMap ordinalMap;
    protected int maxOrdinal;

    protected int numShards;
    protected int revNumShards;
    private int resetToLastNumShards;

    protected HollowSchema restoredSchema;
    protected ByteArrayOrdinalMap restoredMap;
    protected HollowTypeReadState restoredReadState;

    protected ThreadSafeBitSet currentCyclePopulated;
    protected ThreadSafeBitSet previousCyclePopulated;

    private final ThreadLocal<ByteDataArray> serializedScratchSpace;

    protected HollowWriteStateEngine stateEngine;

    private boolean wroteData = false;

    private final boolean isNumShardsPinned;  // if numShards is pinned in data model
    protected int maxShardOrdinal[];
    protected int revMaxShardOrdinal[];


    public HollowTypeWriteState(HollowSchema schema, int numShards) {
        this.schema = schema;
        this.ordinalMap = new ByteArrayOrdinalMap();
        this.serializedScratchSpace = new ThreadLocal<ByteDataArray>();
        this.currentCyclePopulated = new ThreadSafeBitSet();
        this.previousCyclePopulated = new ThreadSafeBitSet();
        this.numShards = numShards;
        this.isNumShardsPinned = (numShards != -1);
        this.resetToLastNumShards = numShards;

        if(numShards != -1 && ((numShards & (numShards - 1)) != 0 || numShards <= 0))
            throw new IllegalArgumentException("Number of shards must be a power of 2!  Check configuration for type " + schema.getName());
    }
    
    /**
     * Add an object to this state.  We will create a serialized representation of this object, then
     * assign or retrieve the ordinal for this serialized representation in our {@link ByteArrayOrdinalMap}.
     * @param rec the record to add to this state
     * @return the ordinal of the added record
     */
    public int add(HollowWriteRecord rec) {
        if(!ordinalMap.isReadyForAddingObjects())
            throw new RuntimeException("The HollowWriteStateEngine is not ready to add more Objects.  Did you remember to call stateEngine.prepareForNextCycle()?");

        int ordinal;

        if(restoredMap == null) {
            ordinal = assignOrdinal(rec);
        } else {
            ordinal = reuseOrdinalFromRestoredState(rec);
        }

        currentCyclePopulated.set(ordinal);

        return ordinal;
    }

    private int assignOrdinal(HollowWriteRecord rec) {
        ByteDataArray scratch = scratch();
        rec.writeDataTo(scratch);
        int ordinal = ordinalMap.getOrAssignOrdinal(scratch);
        scratch.reset();
        return ordinal;
    }


    private int reuseOrdinalFromRestoredState(HollowWriteRecord rec) {
        ByteDataArray scratch = scratch();

        int ordinal;

        if(restoredSchema instanceof HollowObjectSchema) {
            ((HollowObjectWriteRecord)rec).writeDataTo(scratch, (HollowObjectSchema)restoredSchema);
            int preferredOrdinal = restoredMap.get(scratch);
            scratch.reset();
            rec.writeDataTo(scratch);
            ordinal = ordinalMap.getOrAssignOrdinal(scratch, preferredOrdinal);
        } else {
            if(rec instanceof HollowHashableWriteRecord) {
                ((HollowHashableWriteRecord) rec).writeDataTo(scratch, IGNORED_HASHES);
                int preferredOrdinal = restoredMap.get(scratch);
                scratch.reset();
                rec.writeDataTo(scratch);
                ordinal = ordinalMap.getOrAssignOrdinal(scratch, preferredOrdinal);
            } else {
                rec.writeDataTo(scratch);
                int preferredOrdinal = restoredMap.get(scratch);
                ordinal = ordinalMap.getOrAssignOrdinal(scratch, preferredOrdinal);
            }
        }

        scratch.reset();

        return ordinal;
    }

    /**
     * Resets this write state to empty (i.e. as if prepareForNextCycle() had just been called)
     */
    public void resetToLastPrepareForNextCycle() {
        numShards = resetToLastNumShards;
        if(restoredReadState == null) {
            currentCyclePopulated.clearAll();
            ordinalMap.compact(previousCyclePopulated, numShards, stateEngine.isFocusHoleFillInFewestShards());
        } else {
            /// this state engine began the cycle as a restored state engine
            currentCyclePopulated.clearAll();
            previousCyclePopulated.clearAll();
            ordinalMap.compact(previousCyclePopulated, numShards, stateEngine.isFocusHoleFillInFewestShards());
            restoreFrom(restoredReadState);
            wroteData = false;
        }
    }

    public void addAllObjectsFromPreviousCycle() {
        if(!ordinalMap.isReadyForAddingObjects())
            throw new RuntimeException("The HollowWriteStateEngine is not ready to add more Objects.  Did you remember to call stateEngine.prepareForNextCycle()?");

        currentCyclePopulated = ThreadSafeBitSet.orAll(previousCyclePopulated, currentCyclePopulated);
    }
    
    public void addOrdinalFromPreviousCycle(int ordinal) {
        if(!ordinalMap.isReadyForAddingObjects())
            throw new RuntimeException("The HollowWriteStateEngine is not ready to add more Objects.  Did you remember to call stateEngine.prepareForNextCycle()?");

        if(!previousCyclePopulated.get(ordinal))
            throw new IllegalArgumentException("Ordinal " + ordinal + " was not present in the previous cycle");
        
        currentCyclePopulated.set(ordinal);
    }

    public void removeOrdinalFromThisCycle(int ordinalToRemove) {
        if(!ordinalMap.isReadyForAddingObjects())
            throw new RuntimeException("The HollowWriteStateEngine is not ready to add more Objects.  Did you remember to call stateEngine.prepareForNextCycle()?");

        currentCyclePopulated.clear(ordinalToRemove);
    }
    
    public void removeAllOrdinalsFromThisCycle() {
        if(!ordinalMap.isReadyForAddingObjects())
            throw new RuntimeException("The HollowWriteStateEngine is not ready to add more Objects.  Did you remember to call stateEngine.prepareForNextCycle()?");

        currentCyclePopulated.clearAll();
    }
    
    /**
     * Put an object in this state with a specific ordinal, and update the currentCyclePopulated bitset.  
     * 
     * WARNING: This method is not thread safe.
     * WARNING: This method may result in duplicate records getting added into the state engine.  Do not add
     * records using this method which have already been added to this write state in the current cycle.
     * WARNING: This method will not automatically update the ByteArrayOrdinalMap's free ordinals.  This will corrupt 
     * the state unless all remapped ordinals are *also* removed from the free ordinal list using recalculateFreeOrdinals()
     * after all calls to mapOrdinal() are complete.
     *
     * @param rec the record
     * @param newOrdinal the new ordinal
     * @param markPreviousCycle true if the previous populated cycle should be updated
     * @param markCurrentCycle true if the current populated cycle should be updated
     */
    public void mapOrdinal(HollowWriteRecord rec, int newOrdinal, boolean markPreviousCycle, boolean markCurrentCycle) {
        if(!ordinalMap.isReadyForAddingObjects())
            throw new RuntimeException("The HollowWriteStateEngine is not ready to add more Objects.  Did you remember to call stateEngine.prepareForNextCycle()?");

        ByteDataArray scratch = scratch();
        rec.writeDataTo(scratch);
        ordinalMap.put(scratch, newOrdinal);
        if(markPreviousCycle)
            previousCyclePopulated.set(newOrdinal);
        if(markCurrentCycle)
            currentCyclePopulated.set(newOrdinal);
        scratch.reset();
    }
    
    /**
     * Correct the free ordinal list after using mapOrdinal()
     */
    public void recalculateFreeOrdinals() {
        ordinalMap.recalculateFreeOrdinals();
    }

    public ThreadSafeBitSet getPopulatedBitSet() {
        return currentCyclePopulated;
    }

    public ThreadSafeBitSet getPreviousCyclePopulatedBitSet() {
        return previousCyclePopulated;
    }
    
    public HollowSchema getSchema() {
        return schema;
    }
    
    public int getNumShards() {
        return numShards;
    }

    boolean isNumShardsPinned() {
        return isNumShardsPinned;
    }

    int getRevNumShards() {
        return revNumShards;
    }

    public void setNumShards(int numShards) {
        if(this.numShards == -1) {
            this.numShards = numShards;
            this.resetToLastNumShards = numShards;
        } else if(this.numShards != numShards) {
            throw new IllegalStateException("The number of shards for type " + schema.getName() + " is already fixed to " + this.numShards + ".  Cannot reset to " + numShards + ".");
        }
    }

    public void resizeOrdinalMap(int size) {
        ordinalMap.resize(size);
    }

    /**
     * Called to perform a state transition.<p>
     *
     * Precondition: We are writing the previously added objects to a FastBlob.<br>
     * Postcondition: We are ready to add objects to this state engine for the next server cycle.
     */
    public void prepareForNextCycle() {
        ordinalMap.compact(currentCyclePopulated, numShards, stateEngine.isFocusHoleFillInFewestShards());

        ThreadSafeBitSet temp = previousCyclePopulated;
        previousCyclePopulated = currentCyclePopulated;
        currentCyclePopulated = temp;

        currentCyclePopulated.clearAll();

        restoredMap = null;
        restoredSchema = null;
        restoredReadState = null;

        resetToLastNumShards = numShards; // -1 if first cycle else previous numShards. See {@code testNumShardsMaintainedWhenNoResharding}
    }

    public void prepareForWrite(boolean canReshard) {
        /// write all of the unused objects to the current ordinalMap, without updating the current cycle bitset,
        /// this way we can do a reverse delta.
        if(isRestored() && !wroteData) {
            HollowRecordCopier copier = HollowRecordCopier.createCopier(restoredReadState, schema);

            BitSet unusedPreviousOrdinals = ordinalMap.getUnusedPreviousOrdinals();
            int ordinal = unusedPreviousOrdinals.nextSetBit(0);

            while(ordinal != -1) {
                restoreOrdinal(ordinal, copier, ordinalMap, UNMIXED_HASHES);
                ordinal = unusedPreviousOrdinals.nextSetBit(ordinal + 1);
            }
        }

        ordinalMap.prepareForWrite();
        wroteData = true;
    }

    public boolean hasChangedSinceLastCycle() {
        if (!currentCyclePopulated.equals(previousCyclePopulated)) {
            return true;
        }
        if (numShards != revNumShards // see {@code testChangingNumShardsWithoutChangesInPopulatedOrdinals}
            && revNumShards != 0) {
            return true;
        }
        return false;

    }
    
    public boolean isRestored() {
        return ordinalMap.getUnusedPreviousOrdinals() != null;
    }

    public abstract void calculateSnapshot();

    public abstract void writeSnapshot(DataOutputStream dos) throws IOException;

    public void calculateDelta() {
        calculateDelta(previousCyclePopulated, currentCyclePopulated, false);
    }

    public void calculateReverseDelta() {
        calculateDelta(currentCyclePopulated, previousCyclePopulated, true);
    }

    public void writeDelta(DataOutputStream dos) throws IOException {
        LOG.log(Level.FINE, String.format("Writing delta with num shards = %s, max shard ordinals = %s", numShards, Arrays.toString(maxShardOrdinal)));
        writeCalculatedDelta(dos, false, maxShardOrdinal);
    }

    public void writeReverseDelta(DataOutputStream dos) throws IOException {
        LOG.log(Level.FINE, String.format("Writing reversedelta with num shards = %s, max shard ordinals = %s", revNumShards, Arrays.toString(revMaxShardOrdinal)));
        writeCalculatedDelta(dos, true, revMaxShardOrdinal);
    }

    public abstract void calculateDelta(ThreadSafeBitSet fromCyclePopulated, ThreadSafeBitSet toCyclePopulated, boolean isReverse);

    public abstract void writeCalculatedDelta(DataOutputStream os, boolean isReverse, int[] maxShardOrdinal) throws IOException;

    protected void restoreFrom(HollowTypeReadState readState) {
        if(previousCyclePopulated.cardinality() != 0 || currentCyclePopulated.cardinality() != 0)
            throw new IllegalStateException("Attempting to restore into a non-empty state (type " + schema.getName() + ")");
        
        PopulatedOrdinalListener listener = readState.getListener(PopulatedOrdinalListener.class);
        BitSet populatedOrdinals = listener.getPopulatedOrdinals();

        restoredReadState = readState;
        if(schema instanceof HollowObjectSchema)
            restoredSchema = ((HollowObjectSchema)schema).findCommonSchema((HollowObjectSchema)readState.getSchema());
        else
            restoredSchema = readState.getSchema();
        HollowRecordCopier copier = HollowRecordCopier.createCopier(restoredReadState, restoredSchema);

        // Size the restore ordinal map to avoid resizing when adding ordinals
        int size = populatedOrdinals.cardinality();
        restoredMap = new ByteArrayOrdinalMap(size);
        int ordinal = populatedOrdinals.nextSetBit(0);
        while(ordinal != -1) {
            previousCyclePopulated.set(ordinal);
            restoreOrdinal(ordinal, copier, restoredMap, IGNORED_HASHES);
            ordinal = populatedOrdinals.nextSetBit(ordinal + 1);
        }

        // Resize the ordinal map to avoid resizing when populating
        ordinalMap.resize(size);
        ordinalMap.reservePreviouslyPopulatedOrdinals(populatedOrdinals);
    }

    protected void restoreOrdinal(int ordinal, HollowRecordCopier copier, ByteArrayOrdinalMap destinationMap, HashBehavior hashBehavior) {
        HollowWriteRecord rec = copier.copy(ordinal);

        ByteDataArray scratch = scratch();
        if(rec instanceof HollowHashableWriteRecord)
            ((HollowHashableWriteRecord)rec).writeDataTo(scratch, hashBehavior);
        else
            rec.writeDataTo(scratch);

        destinationMap.put(scratch, ordinal);
        scratch.reset();
    }

    /**
     * Get or create a scratch byte array.  Each thread will need its own array, so these
     * are referenced via a ThreadLocal variable.
     * @return the scratch byte array
     */
    protected ByteDataArray scratch() {
        ByteDataArray scratch = serializedScratchSpace.get();
        if(scratch == null) {
            scratch = new ByteDataArray(WastefulRecycler.DEFAULT_INSTANCE);
            serializedScratchSpace.set(scratch);
        }
        return scratch;
    }
    
    void setStateEngine(HollowWriteStateEngine writeEngine) {
        this.stateEngine = writeEngine;
    }
    
    public HollowWriteStateEngine getStateEngine() {
        return stateEngine;
    }

    protected static int[] calcMaxShardOrdinal(int maxOrdinal, int numShards) {
        int[] maxShardOrdinal = new int[numShards];
        int minRecordLocationsPerShard = (maxOrdinal + 1) / numShards;
        for(int i=0;i<numShards;i++)
            maxShardOrdinal[i] = (i < ((maxOrdinal + 1) & (numShards - 1))) ? minRecordLocationsPerShard : minRecordLocationsPerShard - 1;
        return maxShardOrdinal;
    }

    public boolean allowTypeResharding() {
        boolean isAllowed = stateEngine.allowTypeResharding();
        if (isAllowed) {
            if (isNumShardsPinned()) {
                LOG.warning(String.format("The num shards for type %s is pinned (likely using the @HollowShardLargeType annotation " +
                        "in the data model) but this producer is also configured for dynamically adjusting the num shards based on " +
                        "data size during the course of the delta chain, so the pin for num shards will not be honored and it can be dropped",
                        schema.getName()));
            }
        }
        return isAllowed;
    }

    public void gatherShardingStats(int maxOrdinal, boolean canReshard) {
        if(numShards == -1) {
            numShards = typeStateNumShards(maxOrdinal);
            revNumShards = numShards;
        } else {
            revNumShards = numShards;
            if (canReshard && allowTypeResharding()) {
                numShards = typeStateNumShards(maxOrdinal);
                if (numShards != revNumShards) {    // re-sharding
                    // limit numShards to 2x or .5x of prevShards per producer cycle
                    numShards = numShards > revNumShards ? revNumShards * 2 : revNumShards / 2;

                    LOG.info(String.format("Num shards for type %s changing from %s to %s", schema.getName(), revNumShards, numShards));
                    addReshardingHeader(revNumShards, numShards);
                }
            }
        }
        maxShardOrdinal = calcMaxShardOrdinal(maxOrdinal, numShards);
        if (revNumShards > 0) {
            revMaxShardOrdinal = calcMaxShardOrdinal(maxOrdinal, revNumShards);
        }
    }

    protected abstract int typeStateNumShards(int maxOrdinal);

    /**
     * A header tag indicating that num shards for a type has changed since the prior version. Its value encodes
     * the type(s) that were re-sharded along with the before and after num shards in the fwd delta direction.
     * For e.g. Movie:(2,4) Actor:(8,4)
     */
    protected void addReshardingHeader(int prevNumShards, int newNumShards) {
        String existing = stateEngine.getHeaderTag(HollowStateEngine.HEADER_TAG_TYPE_RESHARDING_INVOKED);
        String appendTo = "";
        if (existing != null) {
            appendTo = existing + " ";
        }
        stateEngine.addHeaderTag(HollowStateEngine.HEADER_TAG_TYPE_RESHARDING_INVOKED, appendTo + schema.getName() + ":(" + prevNumShards + "," + newNumShards + ")");
    }
}

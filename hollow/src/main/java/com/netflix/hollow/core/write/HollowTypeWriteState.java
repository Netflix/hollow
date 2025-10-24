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

import static com.netflix.hollow.core.write.HollowHashableWriteRecord.HashBehavior.IGNORED_HASHES;
import static com.netflix.hollow.core.write.HollowHashableWriteRecord.HashBehavior.UNMIXED_HASHES;

/**
 * The {@link HollowTypeWriteState} contains and is the root handle to all of the records of a specific type in
 * a {@link HollowWriteStateEngine}.
 */
public abstract class HollowTypeWriteState {
    private static final Logger LOG = Logger.getLogger(HollowTypeWriteState.class.getName());

    public static final int MAX_PARTITIONS = 8;

    protected final HollowSchema schema;

    // Partition support: up to 8 partitions per type
    protected final HollowTypeWriteStatePartition[] partitions;
    protected int numPartitions;

    // Primary key for consistent hashing across partitions (optional)
    protected com.netflix.hollow.core.index.key.PrimaryKey primaryKey;

    protected HollowSchema restoredSchema;
    protected HollowTypeReadState restoredReadState;

    private final ThreadLocal<ByteDataArray> serializedScratchSpace;

    protected HollowWriteStateEngine stateEngine;

    private boolean wroteData = false;

    private final boolean isNumShardsPinned;  // if numShards is pinned in data model
    protected int maxShardOrdinal[];
    protected int revMaxShardOrdinal[];

    // Convenience accessors for backward compatibility (delegate to partition 0)
    protected ByteArrayOrdinalMap ordinalMap;
    protected int maxOrdinal;
    protected int numShards;
    protected int revNumShards;
    protected ByteArrayOrdinalMap restoredMap;
    protected ThreadSafeBitSet currentCyclePopulated;
    protected ThreadSafeBitSet previousCyclePopulated;


    public HollowTypeWriteState(HollowSchema schema, int numShards) {
        this(schema, numShards, 1);  // Default to 1 partition for backward compatibility
    }

    public HollowTypeWriteState(HollowSchema schema, int numShards, int numPartitions) {
        if(numPartitions < 1 || numPartitions > MAX_PARTITIONS)
            throw new IllegalArgumentException("Number of partitions must be between 1 and " + MAX_PARTITIONS);

        this.schema = schema;
        this.serializedScratchSpace = new ThreadLocal<ByteDataArray>();
        this.isNumShardsPinned = (numShards != -1);

        if(numShards != -1 && ((numShards & (numShards - 1)) != 0 || numShards <= 0))
            throw new IllegalArgumentException("Number of shards must be a power of 2!  Check configuration for type " + schema.getName());

        // Initialize partitions
        this.numPartitions = numPartitions;
        this.partitions = new HollowTypeWriteStatePartition[MAX_PARTITIONS];
        for(int i = 0; i < numPartitions; i++) {
            this.partitions[i] = new HollowTypeWriteStatePartition(numShards);
        }

        // Set up backward compatibility references to partition 0
        this.ordinalMap = this.partitions[0].getOrdinalMap();
        this.currentCyclePopulated = this.partitions[0].getCurrentCyclePopulated();
        this.previousCyclePopulated = this.partitions[0].getPreviousCyclePopulated();
        this.numShards = numShards;
    }
    
    /**
     * Gets the number of partitions in this type write state.
     *
     * @return the number of partitions
     */
    public int getNumPartitions() {
        return numPartitions;
    }

    /**
     * Gets a specific partition by index.
     *
     * @param partitionIndex the partition index (0 to numPartitions-1)
     * @return the partition at the specified index
     */
    public HollowTypeWriteStatePartition getPartition(int partitionIndex) {
        if(partitionIndex < 0 || partitionIndex >= numPartitions)
            throw new IllegalArgumentException("Partition index " + partitionIndex + " is out of bounds. Valid range: 0 to " + (numPartitions - 1));
        return partitions[partitionIndex];
    }

    /**
     * Sets the primary key for this type. This is used for consistent hashing across partitions.
     * When a primary key is set and numPartitions > 1, records with the same primary key value
     * will always be assigned to the same partition.
     *
     * @param primaryKey the primary key definition, or null to use hash of full record
     */
    public void setPrimaryKey(com.netflix.hollow.core.index.key.PrimaryKey primaryKey) {
        this.primaryKey = primaryKey;
    }

    /**
     * Gets the primary key for this type.
     *
     * @return the primary key, or null if not set
     */
    public com.netflix.hollow.core.index.key.PrimaryKey getPrimaryKey() {
        return primaryKey;
    }

    /**
     * Syncs backward compatibility fields with partition 0.
     * This should be called after operations that modify partition 0's state.
     */
    protected void syncBackwardCompatibilityFields() {
        HollowTypeWriteStatePartition partition0 = partitions[0];
        this.maxOrdinal = partition0.getMaxOrdinal();
        this.numShards = partition0.getNumShards();
        this.revNumShards = partition0.getRevNumShards();
        this.restoredMap = partition0.getRestoredMap();
    }

    /**
     * Computes a hash code from primary key field values directly.
     * This is more efficient than extracting from a record.
     *
     * @param primaryKeyFieldValues the values of the primary key fields in order
     * @return hash code based on primary key field values
     */
    protected int computePrimaryKeyHash(Object... primaryKeyFieldValues) {
        return HollowTypeWriteStatePartitionSelector.computePrimaryKeyHash(primaryKeyFieldValues);
    }

    /**
     * Computes a hash code for primary key fields of an Object record.
     * Falls back to 0 if primary key is not set or record is not an Object type.
     *
     * @param rec the write record
     * @return hash code based on primary key fields, or 0 if not applicable
     */
    protected int computePrimaryKeyHash(HollowWriteRecord rec) {
        // Fast path: no primary key configured or single partition
        if(primaryKey == null || numPartitions == 1) {
            return 0;
        }

        // Only support primary key hashing for Object types
        if(!(rec instanceof HollowObjectWriteRecord)) {
            return 0;
        }

        HollowObjectWriteRecord objRec = (HollowObjectWriteRecord) rec;
        HollowObjectSchema objSchema = objRec.getSchema();

        int hash = 0;

        // Hash each primary key field
        for(int i = 0; i < primaryKey.numFields(); i++) {
            String fieldPath = primaryKey.getFieldPath(i);

            // For now, only support simple (non-hierarchical) primary key fields
            // Hierarchical fields (with dots) would require following references
            if(fieldPath.contains(".")) {
                // Fall back to partition 0 for complex primary keys
                return 0;
            }

            int fieldPosition = objSchema.getPosition(fieldPath);
            if(fieldPosition == -1) {
                // Field not found, fall back to partition 0
                return 0;
            }

            // Hash this field's data
            hash = hash * 31 + hashField(objRec, fieldPosition, objSchema.getFieldType(fieldPosition));
        }

        return hash;
    }

    /**
     * Computes a hash code for a single field in an Object record.
     *
     * @param rec the write record
     * @param fieldPosition the position of the field in the schema
     * @param fieldType the type of the field
     * @return hash code for this field
     */
    private int hashField(HollowObjectWriteRecord rec, int fieldPosition, HollowObjectSchema.FieldType fieldType) {
        // Access the field data through reflection or by serializing just that field
        // For now, we'll serialize just this field to a temp buffer and hash it
        ByteDataArray scratch = scratch();
        long startPos = scratch.length();

        // Use package-private access to fieldData if possible, otherwise serialize
        // For simplicity, hash the field by writing it and hashing the bytes
        try {
            java.lang.reflect.Field fieldDataField = HollowObjectWriteRecord.class.getDeclaredField("fieldData");
            java.lang.reflect.Field isNonNullField = HollowObjectWriteRecord.class.getDeclaredField("isNonNull");
            fieldDataField.setAccessible(true);
            isNonNullField.setAccessible(true);

            ByteDataArray[] fieldData = (ByteDataArray[]) fieldDataField.get(rec);
            boolean[] isNonNull = (boolean[]) isNonNullField.get(rec);

            if(!isNonNull[fieldPosition]) {
                return 0; // null hash
            }

            ByteDataArray fieldBytes = fieldData[fieldPosition];
            int hash = 0;
            for(long i = 0; i < fieldBytes.length(); i++) {
                hash = hash * 31 + fieldBytes.get(i);
            }
            return hash;

        } catch(Exception e) {
            // Fall back to 0 if reflection fails
            return 0;
        }
    }

    /**
     * Selects which partition a record should be added to based on primary key hash.
     *
     * @param rec the record to add
     * @return the partition index (0 to numPartitions-1)
     */
    protected int selectPartitionForRecord(HollowWriteRecord rec) {
        // Fast path: single partition
        if(numPartitions == 1) {
            return 0;
        }

        int hash = computePrimaryKeyHash(rec);

        // If hash is 0 (no primary key or unsupported case), always use partition 0
        if(hash == 0) {
            return 0;
        }

        // Use modulo to map to partition index
        // Convert to positive value and modulo by numPartitions
        return (hash & Integer.MAX_VALUE) % numPartitions;
    }

    /**
     * Add an object to this state with explicit primary key values for optimized partition selection.
     * This method is more efficient when partitioning is enabled, as it avoids extracting the primary
     * key from the serialized record.
     *
     * @param rec the record to add to this state
     * @param primaryKeyFieldValues the values of the primary key fields in order (for partition selection)
     * @return the ordinal of the added record
     */
    public int add(HollowWriteRecord rec, Object... primaryKeyFieldValues) {
        // Fast path: single partition (backward compatibility)
        if(numPartitions == 1) {
            return add(rec);
        }

        // Multi-partition path: use provided primary key for partition selection
        int partitionIndex = HollowTypeWriteStatePartitionSelector.selectPartition(numPartitions, primaryKeyFieldValues);

        HollowTypeWriteStatePartition partition = partitions[partitionIndex];

        if(!partition.getOrdinalMap().isReadyForAddingObjects())
            throw new RuntimeException("The HollowWriteStateEngine is not ready to add more Objects.  Did you remember to call stateEngine.prepareForNextCycle()?");

        // Serialize the record
        ByteDataArray scratch = scratch();
        rec.writeDataTo(scratch);

        int ordinal;
        if(partition.getRestoredMap() == null) {
            ordinal = partition.getOrdinalMap().getOrAssignOrdinal(scratch);
        } else {
            // Handle restored state: try to reuse ordinals if possible
            if(schema instanceof HollowObjectSchema && rec instanceof HollowObjectWriteRecord) {
                ((HollowObjectWriteRecord)rec).writeDataTo(scratch, (HollowObjectSchema)restoredSchema);
                int preferredOrdinal = partition.getRestoredMap().get(scratch);
                scratch.reset();
                rec.writeDataTo(scratch);
                ordinal = partition.getOrdinalMap().getOrAssignOrdinal(scratch, preferredOrdinal);
            } else {
                int preferredOrdinal = partition.getRestoredMap().get(scratch);
                ordinal = partition.getOrdinalMap().getOrAssignOrdinal(scratch, preferredOrdinal);
            }
        }

        partition.getCurrentCyclePopulated().set(ordinal);
        scratch.reset();

        return ordinal;
    }

    /**
     * Add an object to this state.  We will create a serialized representation of this object, then
     * assign or retrieve the ordinal for this serialized representation in our {@link ByteArrayOrdinalMap}.
     * @param rec the record to add to this state
     * @return the ordinal of the added record
     */
    public int add(HollowWriteRecord rec) {
        // Fast path: single partition (backward compatibility)
        if(numPartitions == 1) {
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

        // Multi-partition path: select partition based on primary key hash
        int partitionIndex = selectPartitionForRecord(rec);
        HollowTypeWriteStatePartition partition = partitions[partitionIndex];

        if(!partition.getOrdinalMap().isReadyForAddingObjects())
            throw new RuntimeException("The HollowWriteStateEngine is not ready to add more Objects.  Did you remember to call stateEngine.prepareForNextCycle()?");

        // Serialize the record
        ByteDataArray scratch = scratch();
        rec.writeDataTo(scratch);

        int ordinal;
        if(partition.getRestoredMap() == null) {
            ordinal = partition.getOrdinalMap().getOrAssignOrdinal(scratch);
        } else {
            // Handle restored state: try to reuse ordinals if possible
            if(schema instanceof HollowObjectSchema && rec instanceof HollowObjectWriteRecord) {
                ((HollowObjectWriteRecord)rec).writeDataTo(scratch, (HollowObjectSchema)restoredSchema);
                int preferredOrdinal = partition.getRestoredMap().get(scratch);
                scratch.reset();
                rec.writeDataTo(scratch);
                ordinal = partition.getOrdinalMap().getOrAssignOrdinal(scratch, preferredOrdinal);
            } else {
                int preferredOrdinal = partition.getRestoredMap().get(scratch);
                ordinal = partition.getOrdinalMap().getOrAssignOrdinal(scratch, preferredOrdinal);
            }
        }

        partition.getCurrentCyclePopulated().set(ordinal);
        scratch.reset();

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
        boolean focusHoleFillInFewestShards = (stateEngine != null) && stateEngine.isFocusHoleFillInFewestShards();

        for(int i = 0; i < numPartitions; i++) {
            HollowTypeWriteStatePartition partition = partitions[i];
            if(restoredReadState == null) {
                partition.resetToLastPrepareForNextCycle(focusHoleFillInFewestShards);
            } else {
                /// this state engine began the cycle as a restored state engine
                partition.getCurrentCyclePopulated().clearAll();
                partition.getPreviousCyclePopulated().clearAll();
                partition.getOrdinalMap().compact(partition.getPreviousCyclePopulated(),
                    partition.getNumShards(), focusHoleFillInFewestShards);
            }
        }

        if(restoredReadState != null) {
            restoreFrom(restoredReadState);
            wroteData = false;
        }

        syncBackwardCompatibilityFields();
    }

    public void addAllObjectsFromPreviousCycle() {
        for(int i = 0; i < numPartitions; i++) {
            partitions[i].addAllObjectsFromPreviousCycle();
        }
        // Update backward compatibility reference
        currentCyclePopulated = partitions[0].getCurrentCyclePopulated();
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
        for(int i = 0; i < numPartitions; i++) {
            partitions[i].recalculateFreeOrdinals();
        }
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
        HollowTypeWriteStatePartition partition0 = partitions[0];
        if(partition0.getNumShards() == -1) {
            for(int i = 0; i < numPartitions; i++) {
                partitions[i].setNumShards(numShards);
                partitions[i].setResetToLastNumShards(numShards);
            }
            this.numShards = numShards;
        } else if(partition0.getNumShards() != numShards) {
            throw new IllegalStateException("The number of shards for type " + schema.getName() + " is already fixed to " + partition0.getNumShards() + ".  Cannot reset to " + numShards + ".");
        }
    }

    public void resizeOrdinalMap(int size) {
        for(int i = 0; i < numPartitions; i++) {
            partitions[i].resizeOrdinalMap(size);
        }
    }

    /**
     * Called to perform a state transition.<p>
     *
     * Precondition: We are writing the previously added objects to a FastBlob.<br>
     * Postcondition: We are ready to add objects to this state engine for the next server cycle.
     */
    public void prepareForNextCycle() {
        boolean focusHoleFillInFewestShards = (stateEngine != null) && stateEngine.isFocusHoleFillInFewestShards();

        for(int i = 0; i < numPartitions; i++) {
            partitions[i].prepareForNextCycle(focusHoleFillInFewestShards);
        }

        restoredSchema = null;
        restoredReadState = null;

        // Update backward compatibility references to partition 0
        syncBackwardCompatibilityFields();
        currentCyclePopulated = partitions[0].getCurrentCyclePopulated();
        previousCyclePopulated = partitions[0].getPreviousCyclePopulated();
    }

    public void prepareForWrite(boolean canReshard) {
        /// write all of the unused objects to the current ordinalMap, without updating the current cycle bitset,
        /// this way we can do a reverse delta.
        for(int i = 0; i < numPartitions; i++) {
            HollowTypeWriteStatePartition partition = partitions[i];
            if(partition.isRestored() && !wroteData) {
                HollowRecordCopier copier = HollowRecordCopier.createCopier(restoredReadState, schema);

                BitSet unusedPreviousOrdinals = partition.getOrdinalMap().getUnusedPreviousOrdinals();
                int ordinal = unusedPreviousOrdinals.nextSetBit(0);

                while(ordinal != -1) {
                    restoreOrdinal(ordinal, copier, partition.getOrdinalMap(), UNMIXED_HASHES);
                    ordinal = unusedPreviousOrdinals.nextSetBit(ordinal + 1);
                }
            }

            partition.prepareForWrite();
        }

        wroteData = true;
    }

    public boolean hasChangedSinceLastCycle() {
        for(int i = 0; i < numPartitions; i++) {
            HollowTypeWriteStatePartition partition = partitions[i];
            if (!partition.getCurrentCyclePopulated().equals(partition.getPreviousCyclePopulated())) {
                return true;
            }
            if (partition.getNumShards() != partition.getRevNumShards() // see {@code testChangingNumShardsWithoutChangesInPopulatedOrdinals}
                && partition.getRevNumShards() != 0) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isRestored() {
        for(int i = 0; i < numPartitions; i++) {
            if(partitions[i].isRestored()) {
                return true;
            }
        }
        return false;
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
        // Check that all partitions are empty
        for(int i = 0; i < numPartitions; i++) {
            HollowTypeWriteStatePartition partition = partitions[i];
            if(partition.getPreviousCyclePopulated().cardinality() != 0 || partition.getCurrentCyclePopulated().cardinality() != 0)
                throw new IllegalStateException("Attempting to restore into a non-empty state (type " + schema.getName() + ")");
        }

        PopulatedOrdinalListener listener = readState.getListener(PopulatedOrdinalListener.class);
        BitSet populatedOrdinals = listener.getPopulatedOrdinals();

        restoredReadState = readState;
        if(schema instanceof HollowObjectSchema)
            restoredSchema = ((HollowObjectSchema)schema).findCommonSchema((HollowObjectSchema)readState.getSchema());
        else
            restoredSchema = readState.getSchema();
        HollowRecordCopier copier = HollowRecordCopier.createCopier(restoredReadState, restoredSchema);

        // SNAP: TODO: fix restore when read state supports partitions
        // Restore to all partitions - for now, restore to partition 0 only
        // In the future, this could distribute ordinals across partitions
        HollowTypeWriteStatePartition partition0 = partitions[0];

        // Size the restore ordinal map to avoid resizing when adding ordinals
        int size = populatedOrdinals.cardinality();
        ByteArrayOrdinalMap restoredMap0 = new ByteArrayOrdinalMap(size);
        partition0.setRestoredMap(restoredMap0);

        int ordinal = populatedOrdinals.nextSetBit(0);
        while(ordinal != -1) {
            partition0.getPreviousCyclePopulated().set(ordinal);
            restoreOrdinal(ordinal, copier, restoredMap0, IGNORED_HASHES);
            ordinal = populatedOrdinals.nextSetBit(ordinal + 1);
        }

        // Resize the ordinal map to avoid resizing when populating
        partition0.resizeOrdinalMap(size);
        partition0.getOrdinalMap().reservePreviouslyPopulatedOrdinals(populatedOrdinals);

        // Update backward compatibility fields
        syncBackwardCompatibilityFields();
        previousCyclePopulated = partition0.getPreviousCyclePopulated();
        currentCyclePopulated = partition0.getCurrentCyclePopulated();
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
        // Update sharding stats for all partitions
        for(int i = 0; i < numPartitions; i++) {
            HollowTypeWriteStatePartition partition = partitions[i];
            int partitionNumShards = partition.getNumShards();
            int partitionRevNumShards;

            if(partitionNumShards == -1) {
                partitionNumShards = typeStateNumShards(maxOrdinal);
                partitionRevNumShards = partitionNumShards;
            } else {
                partitionRevNumShards = partitionNumShards;
                if (canReshard && allowTypeResharding()) {
                    int newNumShards = typeStateNumShards(maxOrdinal);
                    if (newNumShards != partitionRevNumShards) {    // re-sharding
                        // limit numShards to 2x or .5x of prevShards per producer cycle
                        partitionNumShards = newNumShards > partitionRevNumShards ? partitionRevNumShards * 2 : partitionRevNumShards / 2;

                        if(i == 0) {  // Only log once for partition 0
                            LOG.info(String.format("Num shards for type %s changing from %s to %s", schema.getName(), partitionRevNumShards, partitionNumShards));
                            addReshardingHeader(partitionRevNumShards, partitionNumShards);
                        }
                    }
                }
            }

            partition.setNumShards(partitionNumShards);
            partition.setRevNumShards(partitionRevNumShards);
        }

        // Use partition 0 values for backward compatibility
        syncBackwardCompatibilityFields();

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

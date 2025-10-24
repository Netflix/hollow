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

import com.netflix.hollow.core.memory.ByteArrayOrdinalMap;
import com.netflix.hollow.core.memory.ThreadSafeBitSet;

/**
 * Represents a partition of a {@link HollowTypeWriteState}. Each partition maintains its own
 * ordinal map, populated ordinal bitsets, and sharding configuration.
 * <p>
 * A {@link HollowTypeWriteState} can have up to 8 partitions, allowing for better memory
 * management and parallel processing of large types.
 */
public class HollowTypeWriteStatePartition {

    protected final ByteArrayOrdinalMap ordinalMap;
    protected int maxOrdinal;

    protected int numShards;
    protected int revNumShards;
    protected int resetToLastNumShards;

    protected ByteArrayOrdinalMap restoredMap;

    protected ThreadSafeBitSet currentCyclePopulated;
    protected ThreadSafeBitSet previousCyclePopulated;

    /**
     * Creates a new partition with the specified number of shards.
     *
     * @param numShards the number of shards for this partition, or -1 for dynamic sharding
     */
    public HollowTypeWriteStatePartition(int numShards) {
        this.ordinalMap = new ByteArrayOrdinalMap();
        this.currentCyclePopulated = new ThreadSafeBitSet();
        this.previousCyclePopulated = new ThreadSafeBitSet();
        this.numShards = numShards;
        this.resetToLastNumShards = numShards;
    }

    /**
     * Gets the ordinal map for this partition.
     *
     * @return the ordinal map
     */
    public ByteArrayOrdinalMap getOrdinalMap() {
        return ordinalMap;
    }

    /**
     * Gets the maximum ordinal in this partition.
     *
     * @return the maximum ordinal
     */
    public int getMaxOrdinal() {
        return maxOrdinal;
    }

    /**
     * Sets the maximum ordinal in this partition.
     *
     * @param maxOrdinal the maximum ordinal
     */
    public void setMaxOrdinal(int maxOrdinal) {
        this.maxOrdinal = maxOrdinal;
    }

    /**
     * Gets the number of shards for this partition.
     *
     * @return the number of shards
     */
    public int getNumShards() {
        return numShards;
    }

    /**
     * Sets the number of shards for this partition.
     *
     * @param numShards the number of shards
     */
    public void setNumShards(int numShards) {
        this.numShards = numShards;
    }

    /**
     * Gets the previous number of shards (for reverse delta).
     *
     * @return the previous number of shards
     */
    public int getRevNumShards() {
        return revNumShards;
    }

    /**
     * Sets the previous number of shards (for reverse delta).
     *
     * @param revNumShards the previous number of shards
     */
    public void setRevNumShards(int revNumShards) {
        this.revNumShards = revNumShards;
    }

    /**
     * Gets the number of shards to reset to after a cycle.
     *
     * @return the reset number of shards
     */
    public int getResetToLastNumShards() {
        return resetToLastNumShards;
    }

    /**
     * Sets the number of shards to reset to after a cycle.
     *
     * @param resetToLastNumShards the reset number of shards
     */
    public void setResetToLastNumShards(int resetToLastNumShards) {
        this.resetToLastNumShards = resetToLastNumShards;
    }

    /**
     * Gets the restored ordinal map for this partition.
     *
     * @return the restored ordinal map, or null if not restored
     */
    public ByteArrayOrdinalMap getRestoredMap() {
        return restoredMap;
    }

    /**
     * Sets the restored ordinal map for this partition.
     *
     * @param restoredMap the restored ordinal map
     */
    public void setRestoredMap(ByteArrayOrdinalMap restoredMap) {
        this.restoredMap = restoredMap;
    }

    /**
     * Gets the populated ordinal bitset for the current cycle.
     *
     * @return the current cycle populated bitset
     */
    public ThreadSafeBitSet getCurrentCyclePopulated() {
        return currentCyclePopulated;
    }

    /**
     * Gets the populated ordinal bitset for the previous cycle.
     *
     * @return the previous cycle populated bitset
     */
    public ThreadSafeBitSet getPreviousCyclePopulated() {
        return previousCyclePopulated;
    }

    /**
     * Checks if this partition is in a restored state.
     *
     * @return true if the partition has been restored from a previous state
     */
    public boolean isRestored() {
        return ordinalMap.getUnusedPreviousOrdinals() != null;
    }

    /**
     * Resizes the ordinal map to the specified size.
     *
     * @param size the new size
     */
    public void resizeOrdinalMap(int size) {
        ordinalMap.resize(size);
    }

    /**
     * Prepares this partition for the next cycle by compacting the ordinal map
     * and swapping the populated bitsets.
     *
     * @param focusHoleFillInFewestShards whether to focus hole filling in fewest shards
     */
    public void prepareForNextCycle(boolean focusHoleFillInFewestShards) {
        ordinalMap.compact(currentCyclePopulated, numShards, focusHoleFillInFewestShards);

        ThreadSafeBitSet temp = previousCyclePopulated;
        previousCyclePopulated = currentCyclePopulated;
        currentCyclePopulated = temp;

        currentCyclePopulated.clearAll();

        restoredMap = null;
        resetToLastNumShards = numShards;
    }

    /**
     * Prepares the ordinal map for writing.
     */
    public void prepareForWrite() {
        ordinalMap.prepareForWrite();
    }

    /**
     * Resets this partition to the state after the last prepareForNextCycle() call.
     *
     * @param focusHoleFillInFewestShards whether to focus hole filling in fewest shards
     */
    public void resetToLastPrepareForNextCycle(boolean focusHoleFillInFewestShards) {
        numShards = resetToLastNumShards;
        currentCyclePopulated.clearAll();
        ordinalMap.compact(previousCyclePopulated, numShards, focusHoleFillInFewestShards);
    }

    /**
     * Adds all objects from the previous cycle to the current cycle.
     */
    public void addAllObjectsFromPreviousCycle() {
        if(!ordinalMap.isReadyForAddingObjects())
            throw new RuntimeException("The HollowWriteStateEngine is not ready to add more Objects.  Did you remember to call stateEngine.prepareForNextCycle()?");

        currentCyclePopulated = ThreadSafeBitSet.orAll(previousCyclePopulated, currentCyclePopulated);
    }

    /**
     * Recalculates the free ordinals in the ordinal map.
     */
    public void recalculateFreeOrdinals() {
        ordinalMap.recalculateFreeOrdinals();
    }
}

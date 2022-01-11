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
package com.netflix.hollow.tools.history;

import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.dataaccess.HollowDataAccess;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.PopulatedOrdinalListener;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.util.RemovedOrdinalIterator;
import com.netflix.hollow.core.util.SimultaneousExecutor;
import com.netflix.hollow.tools.diff.exact.DiffEqualOrdinalMap;
import com.netflix.hollow.tools.diff.exact.DiffEqualOrdinalMap.OrdinalIdentityTranslator;
import com.netflix.hollow.tools.diff.exact.DiffEqualityMapping;
import com.netflix.hollow.tools.history.keyindex.HollowHistoricalStateKeyOrdinalMapping;
import com.netflix.hollow.tools.history.keyindex.HollowHistoricalStateTypeKeyOrdinalMapping;
import com.netflix.hollow.tools.history.keyindex.HollowHistoryKeyIndex;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Retains, in memory, the changes in a dataset over many states.  Indexes data for efficient retrieval from any
 * point in time.
 * <p>
 * This historical data is maintained by retaining and indexing all of the changes for the delta chain in memory.
 * Because only changes over time are retained, rather than complete states, a great length of history can often
 * be held in memory.  Additionally, because the data is held indexed in memory, it can be accessed very quickly,
 * so that changes to specific records over time can be precisely investigated quickly.
 * <p>
 * Each retained state is accessible via a {@link HollowHistoricalState}, from which a {@link HollowDataAccess} can
 * be obtained and used interchangeably with a (current) {@link HollowReadStateEngine} for many operations.
 *
 */
public class HollowHistory {

    private final HollowHistoryKeyIndex keyIndex;
    private final HollowHistoricalStateCreator creator;
    private final int maxHistoricalStatesToKeep;

    private HollowReadStateEngine latestHollowReadStateEngine;
    private Map<String, String> latestHeaderEntries;
    private final List<HollowHistoricalState> historicalStates;

    private final Map<Long, HollowHistoricalState> historicalStateLookupMap;

    private long latestVersion;

    private boolean ignoreListOrderingOnDoubleSnapshot = false;

    /**
     * @param initialHollowStateEngine The HollowReadStateEngine at an initial (earliest) state.
     * @param initialVersion The initial version of the HollowReadStateEngine
     * @param maxHistoricalStatesToKeep The number of historical states to keep in memory
     */
    public HollowHistory(HollowReadStateEngine initialHollowStateEngine, long initialVersion, int maxHistoricalStatesToKeep) {
        this(initialHollowStateEngine, initialVersion, maxHistoricalStatesToKeep, true);
    }

    /**
     * @param initialHollowStateEngine The HollowReadStateEngine at an initial (earliest) state.
     * @param initialVersion The initial version of the HollowReadStateEngine
     * @param maxHistoricalStatesToKeep The number of historical states to keep in memory
     * @param isAutoDiscoverTypeIndex true if scheme types are auto-discovered from the initiate state engine
     */
    public HollowHistory(HollowReadStateEngine initialHollowStateEngine, long initialVersion, int maxHistoricalStatesToKeep, boolean isAutoDiscoverTypeIndex) {
        this.keyIndex = new HollowHistoryKeyIndex(this);
        this.creator = new HollowHistoricalStateCreator(this);
        this.latestHollowReadStateEngine = initialHollowStateEngine;
        this.latestHeaderEntries = latestHollowReadStateEngine.getHeaderTags();
        this.historicalStates = new ArrayList<HollowHistoricalState>();
        this.historicalStateLookupMap = new HashMap<Long, HollowHistoricalState>();
        this.maxHistoricalStatesToKeep = maxHistoricalStatesToKeep;
        this.latestVersion = initialVersion;

        if (isAutoDiscoverTypeIndex) {
            for (HollowSchema schema : initialHollowStateEngine.getSchemas()) {
                if (schema instanceof HollowObjectSchema) {
                    PrimaryKey pKey = ((HollowObjectSchema) schema).getPrimaryKey();
                    if (pKey == null) continue;

                    keyIndex.addTypeIndex(pKey);
                    keyIndex.indexTypeField(pKey);
                }
            }
        }
    }

    /**
     * Call this method to indicate that list ordering changes should be identified as modified records when
     * a double snapshot occurs.  By default, these will not be identified as updates.
     */
    public void ignoreListOrderingOnDoubleSnapshot() {
        this.ignoreListOrderingOnDoubleSnapshot = true;
    }

    /**
     * @return The {@link HollowHistoryKeyIndex}, responsible for identifying keyOrdinals.
     */
    public HollowHistoryKeyIndex getKeyIndex() {
        return keyIndex;
    }

    /**
     * @return The {@link HollowReadStateEngine} backing the latest state.
     */
    public HollowReadStateEngine getLatestState() {
        return latestHollowReadStateEngine;
    }

    /**
     * @return An array of each historical state.
     */
    public HollowHistoricalState[] getHistoricalStates() {
        return historicalStates.toArray(new HollowHistoricalState[historicalStates.size()]);
    }

    /**
     * @return the number of historical states
     */
    public int getNumberOfHistoricalStates() {
        return historicalStates.size();
    }

    /**
     * @param version A version in the past
     * @return The {@link HollowHistoricalState} for the specified version, if it exists.
     */
    public HollowHistoricalState getHistoricalState(long version) {
        if(latestVersion == version)
            return historicalStates.get(0);
        return historicalStateLookupMap.get(version);
    }

    /**
     * Call this method after each time a delta occurs in the backing {@link HollowReadStateEngine}.  This
     * is how the HollowHistory knows how to create a new {@link HollowHistoricalState}.
     *
     * @param newVersion The version of the new state
     */
    public void deltaOccurred(long newVersion) {
        keyIndex.update(latestHollowReadStateEngine, true);

        HollowHistoricalStateDataAccess historicalDataAccess = creator.createBasedOnNewDelta(latestVersion, latestHollowReadStateEngine);
        historicalDataAccess.setNextState(latestHollowReadStateEngine);

        HollowHistoricalStateKeyOrdinalMapping keyOrdinalMapping = createKeyOrdinalMappingFromDelta();
        HollowHistoricalState historicalState = new HollowHistoricalState(newVersion, keyOrdinalMapping, historicalDataAccess, latestHeaderEntries);

        addHistoricalState(historicalState);
        this.latestVersion = newVersion;
        this.latestHeaderEntries = latestHollowReadStateEngine.getHeaderTags();
    }

    /**
     * Call this method after each time a delta occurs in the backing {@link HollowReadStateEngine}.  This
     * is how the HollowHistory knows how to create a new {@link HollowHistoricalState}.
     *
     * @param newVersion The version of the new state
     */
    public void reverseDeltaOccurred(long newVersion) {
        keyIndex.update(latestHollowReadStateEngine, true);
        long version = 0;
        if(historicalStates.size()>0){
            version = historicalStates.get(historicalStates.size()-1).getVersion();
        }
        HollowHistoricalStateDataAccess historicalDataAccess = creator.createBasedOnNewDelta(version, latestHollowReadStateEngine);
        historicalDataAccess.setNextState(latestHollowReadStateEngine);

        HollowHistoricalStateKeyOrdinalMapping keyOrdinalMapping = createKeyOrdinalMappingFromReverseDelta();
        HollowHistoricalState historicalState = new HollowHistoricalState(newVersion, keyOrdinalMapping, historicalDataAccess, latestHeaderEntries);

        addReverseHistoricalState(historicalState);
        this.latestVersion = historicalStates.get(0).getVersion();
        this.latestHeaderEntries = latestHollowReadStateEngine.getHeaderTags();
    }
    /**
     * Call this method after each time a double snapshot occurs.
     * <p>
     * This method will replace the previous backing {@link HollowReadStateEngine} with the newly
     * supplied one, stitch together all of the existing history with the new state currently in the
     * new {@link HollowReadStateEngine}, and create a new {@link HollowHistoricalState} to represent
     * the transition.
     *
     * @param newHollowStateEngine the new state engine
     * @param newVersion the new version
     */
    public void doubleSnapshotOccurred(HollowReadStateEngine newHollowStateEngine, long newVersion) {
        if(!keyIndex.isInitialized())
            keyIndex.update(latestHollowReadStateEngine, false);

        keyIndex.update(newHollowStateEngine, false);

        HollowHistoricalStateDataAccess historicalDataAccess;

        DiffEqualityMapping mapping = new DiffEqualityMapping(latestHollowReadStateEngine, newHollowStateEngine, true, !ignoreListOrderingOnDoubleSnapshot);
        DiffEqualityMappingOrdinalRemapper remapper = new DiffEqualityMappingOrdinalRemapper(mapping);

        historicalDataAccess = creator.createHistoricalStateFromDoubleSnapshot(latestVersion, latestHollowReadStateEngine, newHollowStateEngine, remapper);

        HollowHistoricalStateDataAccess nextRemappedDataAccess = historicalDataAccess;
        HollowHistoricalState nextRemappedState = null;

        HollowHistoricalStateDataAccess[] remappedDataAccesses = new HollowHistoricalStateDataAccess[historicalStates.size()];
        HollowHistoricalStateKeyOrdinalMapping[] remappedKeyOrdinalMappings = new HollowHistoricalStateKeyOrdinalMapping[historicalStates.size()];

        remapHistoricalStateOrdinals(remapper, remappedDataAccesses, remappedKeyOrdinalMappings);

        for(int i=0;i<historicalStates.size();i++) {
            HollowHistoricalState historicalStateToRemap = historicalStates.get(i);
            HollowHistoricalStateDataAccess remappedDataAccess = remappedDataAccesses[i];
            HollowHistoricalStateKeyOrdinalMapping remappedKeyOrdinalMapping = remappedKeyOrdinalMappings[i];

            remappedDataAccess.setNextState(nextRemappedDataAccess);
            nextRemappedDataAccess = remappedDataAccess;
            HollowHistoricalState remappedState = new HollowHistoricalState(historicalStateToRemap.getVersion(), remappedKeyOrdinalMapping, remappedDataAccess, historicalStateToRemap.getHeaderEntries());
            remappedState.setNextState(nextRemappedState);
            nextRemappedState = remappedState;
            historicalStates.set(i, remappedState);
            historicalStateLookupMap.put(remappedState.getVersion(), remappedState);
        }

        historicalDataAccess.setNextState(newHollowStateEngine);

        HollowHistoricalStateKeyOrdinalMapping keyOrdinalMapping = createKeyOrdinalMappingFromDoubleSnapshot(newHollowStateEngine, remapper);
        HollowHistoricalState historicalState = new HollowHistoricalState(newVersion, keyOrdinalMapping, historicalDataAccess, latestHeaderEntries);

        addHistoricalState(historicalState);
        this.latestVersion = newVersion;
        this.latestHollowReadStateEngine = newHollowStateEngine;
        this.latestHeaderEntries = latestHollowReadStateEngine.getHeaderTags();
    }

    private void remapHistoricalStateOrdinals(final DiffEqualityMappingOrdinalRemapper remapper, final HollowHistoricalStateDataAccess[] remappedDataAccesses, final HollowHistoricalStateKeyOrdinalMapping[] remappedKeyOrdinalMappings) {
        SimultaneousExecutor executor = new SimultaneousExecutor(getClass(), "remap");
        final int numThreads = executor.getCorePoolSize();

        for(int i=0;i<executor.getCorePoolSize();i++) {
            final int threadNumber = i;
            executor.execute(() -> {
                for(int t=threadNumber;t<historicalStates.size();t+=numThreads) {
                    HollowHistoricalState historicalStateToRemap = historicalStates.get(t);
                    remappedDataAccesses[t] = creator.copyButRemapOrdinals(historicalStateToRemap.getDataAccess(), remapper);
                    remappedKeyOrdinalMappings[t] = historicalStateToRemap.getKeyOrdinalMapping().remap(remapper);
                }
            });
        }

        try {
            executor.awaitSuccessfulCompletion();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private HollowHistoricalStateKeyOrdinalMapping createKeyOrdinalMappingFromDelta() {
        HollowHistoricalStateKeyOrdinalMapping keyOrdinalMapping = new HollowHistoricalStateKeyOrdinalMapping(keyIndex);

        for(String keyType : keyIndex.getTypeKeyIndexes().keySet()) {
            HollowHistoricalStateTypeKeyOrdinalMapping typeMapping = keyOrdinalMapping.getTypeMapping(keyType);
            HollowObjectTypeReadState typeState = (HollowObjectTypeReadState) latestHollowReadStateEngine.getTypeState(keyType);
            if (typeState==null) {
                // The type is present in the history's primary key index but is not present
                // in the latest read state; ensure the mapping is initialized to the default state
                typeMapping.prepare(0, 0);
                typeMapping.finish();
                continue;
            }

            PopulatedOrdinalListener listener = typeState.getListener(PopulatedOrdinalListener.class);

            RemovedOrdinalIterator removalIterator = new RemovedOrdinalIterator(listener);
            RemovedOrdinalIterator additionsIterator = new RemovedOrdinalIterator(listener.getPopulatedOrdinals(), listener.getPreviousOrdinals());

            typeMapping.prepare(additionsIterator.countTotal(), removalIterator.countTotal());

            int removedOrdinal = removalIterator.next();
            while(removedOrdinal != -1) {
                typeMapping.removed(typeState, removedOrdinal);
                removedOrdinal = removalIterator.next();
            }

            int addedOrdinal = additionsIterator.next();
            while(addedOrdinal != -1) {
                typeMapping.added(typeState, addedOrdinal);
                addedOrdinal = additionsIterator.next();
            }

            typeMapping.finish();
        }

        return keyOrdinalMapping;
    }

    private HollowHistoricalStateKeyOrdinalMapping createKeyOrdinalMappingFromReverseDelta() {
        HollowHistoricalStateKeyOrdinalMapping keyOrdinalMapping = new HollowHistoricalStateKeyOrdinalMapping(keyIndex);

        for(String keyType : keyIndex.getTypeKeyIndexes().keySet()) {
            HollowHistoricalStateTypeKeyOrdinalMapping typeMapping = keyOrdinalMapping.getTypeMapping(keyType);
            HollowObjectTypeReadState typeState = (HollowObjectTypeReadState) latestHollowReadStateEngine.getTypeState(keyType);
            if (typeState==null) {
                // The type is present in the history's primary key index but is not present
                // in the latest read state; ensure the mapping is initialized to the default state
                typeMapping.prepare(0, 0);
                typeMapping.finish();
                continue;
            }

            PopulatedOrdinalListener listener = typeState.getListener(PopulatedOrdinalListener.class);

            RemovedOrdinalIterator removalIterator = new RemovedOrdinalIterator(listener);
            RemovedOrdinalIterator additionsIterator = new RemovedOrdinalIterator(listener.getPopulatedOrdinals(), listener.getPreviousOrdinals());

            typeMapping.prepare(additionsIterator.countTotal(), removalIterator.countTotal());

            int removedOrdinal = removalIterator.next();
            while(removedOrdinal != -1) {
                typeMapping.added(typeState, removedOrdinal);
                removedOrdinal = removalIterator.next();
            }

            int addedOrdinal = additionsIterator.next();
            while(addedOrdinal != -1) {
                typeMapping.removed(typeState, addedOrdinal);
                addedOrdinal = additionsIterator.next();
            }

            typeMapping.finish();
        }

        return keyOrdinalMapping;
    }

    private HollowHistoricalStateKeyOrdinalMapping createKeyOrdinalMappingFromDoubleSnapshot(HollowReadStateEngine newStateEngine, DiffEqualityMappingOrdinalRemapper ordinalRemapper) {
        HollowHistoricalStateKeyOrdinalMapping keyOrdinalMapping = new HollowHistoricalStateKeyOrdinalMapping(keyIndex);
        DiffEqualityMapping mapping = ordinalRemapper.getDiffEqualityMapping();

        for(String keyType : keyIndex.getTypeKeyIndexes().keySet()) {
            HollowHistoricalStateTypeKeyOrdinalMapping typeMapping = keyOrdinalMapping.getTypeMapping(keyType);
            HollowObjectTypeReadState fromTypeState = (HollowObjectTypeReadState) latestHollowReadStateEngine.getTypeState(keyType);
            HollowObjectTypeReadState toTypeState = (HollowObjectTypeReadState) newStateEngine.getTypeState(keyType);
            DiffEqualOrdinalMap equalOrdinalMap = mapping.getEqualOrdinalMap(keyType);

            BitSet fromOrdinals = fromTypeState == null ? new BitSet() : fromTypeState.getListener(PopulatedOrdinalListener.class).getPopulatedOrdinals();
            BitSet toOrdinals = toTypeState == null ? new BitSet() : toTypeState.getListener(PopulatedOrdinalListener.class).getPopulatedOrdinals();

            int removedOrdinalsCount = countUnmatchedOrdinals(fromOrdinals, equalOrdinalMap.getFromOrdinalIdentityTranslator());
            int addedOrdinalsCount = countUnmatchedOrdinals(toOrdinals, equalOrdinalMap.getToOrdinalIdentityTranslator());

            typeMapping.prepare(addedOrdinalsCount, removedOrdinalsCount);

            int fromOrdinal = fromOrdinals.nextSetBit(0);
            while(fromOrdinal != -1) {
                if(equalOrdinalMap.getIdentityFromOrdinal(fromOrdinal) == -1)
                    typeMapping.removed(fromTypeState, fromOrdinal, ordinalRemapper.getMappedOrdinal(keyType, fromOrdinal));

                fromOrdinal = fromOrdinals.nextSetBit(fromOrdinal + 1);
            }

            int toOrdinal = toOrdinals.nextSetBit(0);
            while(toOrdinal != -1) {
                if(equalOrdinalMap.getIdentityToOrdinal(toOrdinal) == -1)
                    typeMapping.added(toTypeState, toOrdinal);

                toOrdinal = toOrdinals.nextSetBit(toOrdinal + 1);
            }

            typeMapping.finish();
        }

        return keyOrdinalMapping;
    }

    private int countUnmatchedOrdinals(BitSet ordinals, OrdinalIdentityTranslator translator) {
        int count = 0;
        int ordinal = ordinals.nextSetBit(0);
        while(ordinal != -1) {
            if(translator.getIdentityOrdinal(ordinal) == -1)
                count++;
            ordinal = ordinals.nextSetBit(ordinal + 1);
        }
        return count;
    }

    private void addHistoricalState(HollowHistoricalState historicalState) {
        if(historicalStates.size() > 0) {
            historicalStates.get(0).getDataAccess().setNextState(historicalState.getDataAccess());
            historicalStates.get(0).setNextState(historicalState);
        }

        historicalStates.add(0, historicalState);
        historicalStateLookupMap.put(historicalState.getVersion(), historicalState);

        if(historicalStates.size() > maxHistoricalStatesToKeep) {
            removeHistoricalStates(1);
        }
    }

    private void addReverseHistoricalState(HollowHistoricalState historicalState) {
        if(historicalStates.size() > 0) {
            historicalState.getDataAccess().setNextState(historicalStates.get(historicalStates.size()-1).getDataAccess());
            historicalState.setNextState(historicalStates.get(historicalStates.size()-1));
        }

        historicalStates.add(historicalState);
        historicalStateLookupMap.put(historicalState.getVersion(), historicalState);

        if(historicalStates.size() > maxHistoricalStatesToKeep) {
            removeHistoricalStatesForReverseDelta(1);
        }
    }

    /**
     * Removes the last {@code n} historical states.
     *
     * @param n the number of historical states to remove
     * @throws IllegalArgumentException if the {@code n} is less than {@code 0} or
     * greater than the {@link #getNumberOfHistoricalStates() number} of historical
     * states.
     */
    public void removeHistoricalStates(int n) {
        if (n < 0) {
            throw new IllegalArgumentException(String.format(
                    "Number of states to remove is negative: %d", n));
        }
        if (n > historicalStates.size()) {
            throw new IllegalArgumentException(String.format(
                    "Number of states to remove, %d, is greater than the number of states. %d",
                    n, historicalStates.size()));
        }

        while (n-- > 0) {
            HollowHistoricalState removedState = historicalStates.remove(historicalStates.size() - 1);
            historicalStateLookupMap.remove(removedState.getVersion());
        }
    }

    /**
     * Removes the last {@code n} historical states.
     *
     * @param n the number of historical states to remove
     * @throws IllegalArgumentException if the {@code n} is less than {@code 0} or
     * greater than the {@link #getNumberOfHistoricalStates() number} of historical
     * states.
     */
    public void removeHistoricalStatesForReverseDelta(int n) {
        if (n < 0) {
            throw new IllegalArgumentException(String.format(
                    "Number of states to remove is negative: %d", n));
        }
        if (n > historicalStates.size()) {
            throw new IllegalArgumentException(String.format(
                    "Number of states to remove, %d, is greater than the number of states. %d",
                    n, historicalStates.size()));
        }

        while (n-- > 0) {
            HollowHistoricalState removedState = historicalStates.remove(0);
            historicalStateLookupMap.remove(removedState.getVersion());
        }
    }
}

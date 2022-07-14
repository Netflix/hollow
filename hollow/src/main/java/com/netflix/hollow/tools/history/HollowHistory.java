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

import static com.netflix.hollow.core.HollowConstants.VERSION_NONE;
import static java.util.Objects.requireNonNull;

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
 * This class is not thread safe.
 *
 */
public class HollowHistory {

    private final HollowHistoryKeyIndex keyIndex;
    private final HollowHistoricalStateCreator creator;
    private final int maxHistoricalStatesToKeep;
    private final long fwdInitialVersion;

    /**
     * A list of historical states in decreasing order of version i.e. index 0 holds the highest version
     *  {@code historicalStates} ordered like: V3 -> V2 -> V1 (as displayed to user)
     *  however internally the states are linked like:
     *      V1.nextState = V2; V2.nextState = V3; etc. whether building using fwd or rev deltas
     */
    private final List<HollowHistoricalState> historicalStates;

    // A map of version to HollowHistoricalState for quick retrieval
    private final Map<Long, HollowHistoricalState> historicalStateLookupMap;

    // StateEngines and versions corresponding to the latest and oldest read states. Two are required when building
    // history in fwd and rev directions simultaneously, then once there is sufficient history built
    // oldestHollowReadStateEngine can be dropped.
    // For history v1->v2->v3,
    //  latestHollowReadStateEngine will be at v3, and
    //  oldestHollowReadStateEngine will be at v0 (since the v1 historical state represents the v0->v1 diff)
    private HollowReadStateEngine latestHollowReadStateEngine;
    private long latestVersion = VERSION_NONE;

    // reverse facing read state is optional at initialization
    private HollowReadStateEngine oldestHollowReadStateEngine;
    private long oldestVersion = VERSION_NONE;

    private Map<String, String> latestHeaderEntries;
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
        this(initialHollowStateEngine, null, initialVersion, VERSION_NONE, maxHistoricalStatesToKeep, isAutoDiscoverTypeIndex);
    }

    /**
     * When building history bi-directionally, 2 state engines moving in opposite directions need to be maintained. They
     * must be at the same version, or {@code revMovingHollowReadStateEngine} can be null now but later initialized with
     * a snapshot for the same version as {@code fwdMovingHollowReadStateEngine} passed here before any reverse deltas are applied.
     *
     * @param fwdMovingHollowReadStateEngine The HollowReadStateEngine that will incur application of fwd deltas
     * @param revMovingHollowReadStateEngine The HollowReadStateEngine that will incur application of reverse deltas, or null
     * @param fwdInitialVersion The version of {@code fwdMovingHollowReadStateEngine}
     * @param revInitialVersion The version of {@code revMovingHollowReadStateEngine}, pass VERSION_NONE if revMovingHollowReadStateEngine is null
     * @param maxHistoricalStatesToKeep The number of historical states to keep in memory
     */
    public HollowHistory(HollowReadStateEngine fwdMovingHollowReadStateEngine,
            HollowReadStateEngine revMovingHollowReadStateEngine,
            long fwdInitialVersion,
            long revInitialVersion,
            int maxHistoricalStatesToKeep) {
        this(fwdMovingHollowReadStateEngine, revMovingHollowReadStateEngine, fwdInitialVersion, revInitialVersion,
                maxHistoricalStatesToKeep, true);
    }

    /**
     * When building history bi-directionally, 2 state engines moving in opposite directions need to be maintained. They
     * must start at the same version for contiguous history. {@code revMovingHollowReadStateEngine} can be null now and
     * initialized later by calling {@code initializeReverseStateEngine} with the same version as
     * {@code fwdMovingHollowReadStateEngine} passed here.
     *
     * @param fwdMovingHollowReadStateEngine The HollowReadStateEngine that will incur application of fwd deltas.
     *                                       This is required to be initialized before calling this constructor.
     * @param revMovingHollowReadStateEngine The HollowReadStateEngine that will incur application of reverse deltas.
     *                                       This can optionally be initialized before calling this constructor, or
     *                                       anytime before applying the first reverse delta.
     * @param fwdInitialVersion The version of {@code fwdMovingHollowReadStateEngine}
     * @param revInitialVersion The version of {@code revMovingHollowReadStateEngine}
     * @param maxHistoricalStatesToKeep The number of historical states to keep in memory
     * @param isAutoDiscoverTypeIndex true if scheme types are auto-discovered from the initiate state engine
     */
    public HollowHistory(HollowReadStateEngine fwdMovingHollowReadStateEngine,
            HollowReadStateEngine revMovingHollowReadStateEngine,
            long fwdInitialVersion,
            long revInitialVersion,
            int maxHistoricalStatesToKeep,
            boolean isAutoDiscoverTypeIndex) {
        this.keyIndex = new HollowHistoryKeyIndex(this);
        this.creator = new HollowHistoricalStateCreator(this);
        this.historicalStates = new ArrayList<>();
        this.historicalStateLookupMap = new HashMap<>();
        this.maxHistoricalStatesToKeep = maxHistoricalStatesToKeep;

        // validate fwd moving state initialization
        requireNonNull(fwdMovingHollowReadStateEngine, "Fwd direction read state engine should be initialized");
        if(fwdInitialVersion == VERSION_NONE) {
            throw new IllegalArgumentException("Valid version corresponding to fwdMovingHollowReadStateEngine should be specified" +
                    "during HollowHistory initialization");
        }
        this.latestHollowReadStateEngine = fwdMovingHollowReadStateEngine;
        this.fwdInitialVersion = fwdInitialVersion;
        this.latestVersion = fwdInitialVersion;
        this.latestHeaderEntries = latestHollowReadStateEngine.getHeaderTags();

        // rev moving state, may or may not be specified at initialization
        if(revMovingHollowReadStateEngine != null || revInitialVersion != VERSION_NONE) {
            initializeReverseStateEngine(revMovingHollowReadStateEngine, revInitialVersion);
        }

        if(isAutoDiscoverTypeIndex) {
            for(HollowSchema schema : fwdMovingHollowReadStateEngine.getSchemas()) {
                if(schema instanceof HollowObjectSchema) {
                    PrimaryKey pKey = ((HollowObjectSchema) schema).getPrimaryKey();
                    if(pKey == null) continue;

                    keyIndex.addTypeIndex(pKey);
                    keyIndex.indexTypeField(pKey);
                }
            }
        }
    }

    public void initializeReverseStateEngine(HollowReadStateEngine revReadStateEngine, long version) {
        requireNonNull(revReadStateEngine, "Non-null revReadStateEngine required");
        if(version == VERSION_NONE) {
            throw new IllegalArgumentException("Valid version corresponding to revReadStateEngine required");
        }
        if(version != fwdInitialVersion) {
            throw new IllegalStateException("Reverse state engine version should correspond to the version that fwd state engine" +
                    "initialized to for a contiguous history chain");
        }
        if(latestHollowReadStateEngine == null) {
            // so that history key index is initialized to latestReadStateEngine, the one we're going to retain forever
            throw new IllegalStateException("Initialize fwd direction read state engine before initializing rev direction read state engine");
        }
        if(oldestHollowReadStateEngine != null || oldestVersion != VERSION_NONE) {
            throw new IllegalStateException("oldestHollowReadStateEngine has already been initialized");
        }
        this.oldestHollowReadStateEngine = revReadStateEngine;
        this.oldestVersion = version;
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
     * @return The {@link HollowReadStateEngine} backing the oldest state.
     */
    public HollowReadStateEngine getOldestState() {
        return oldestHollowReadStateEngine;
    }

    public long getLatestVersion() {
        return latestVersion;
    }

    public long getOldestVersion() {
        return oldestVersion;
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
        // When invoked in a listener the delta update has been already applied to latestHollowReadStateEngine, but
        // {@code latestVersion} is still the version from before the delta transition. {@code latestVersion} is
        // updated in this method.

        // Update the state stored in keyIndex (in its member readStateEngine) with the passed read state engine.
        // The readStateEngine within keyIndex stores an ever-growing state of all keys ever seen by this HollowHistory
        // instance i.e. all keys seen in initial load or a successive double-snapshot and all keys added/removed in
        // deltas and reverse deltas. It doesn't store a copy of the keyed records, instead just the primary key values
        // for each type that has a primary key defined (in schema or custom via history helpers).
        keyIndex.update(latestHollowReadStateEngine, true);

        // A HollowHistoricalStateDataAccess is used to save data that won't exist in future states so it needs to
        // be stashed away for making those records accessible in the history view. It achieves this by copying over
        // data corresponding to ghost records in the "to" state in any state transition (i.e. records corresponding to
        // ordinals were populated in the "from" state but are not populated in the "to" state) into a new state engine
        // where it assigns new ordinals serially(0, 1, 2, etc.) to each such record. A mapping of original ordinal
        // in the read state to its new ordinal position in the historical state data access for all such records in
        // each type is stored in the member typeRemovedOrdinalMapping.
        HollowHistoricalStateDataAccess historicalDataAccess = creator.createBasedOnNewDelta(latestVersion, latestHollowReadStateEngine);
        historicalDataAccess.setNextState(latestHollowReadStateEngine);

        // keyOrdinalMapping tracks, for each primary key in each type, ordinals corresponding to added and removed records
        // in the latest read state engine. Used together with the mapping for original ordinal in the read state engine to
        // assigned ordinal in historic state, this helps power the UI view where given a historic version it lists all
        // keys that were added/removed/modified in that version, and can then retrieve the data in those historic states
        // corresponding to those keys
        HollowHistoricalStateKeyOrdinalMapping keyOrdinalMapping = createKeyOrdinalMappingFromDelta(latestHollowReadStateEngine, false);
        HollowHistoricalState historicalState = new HollowHistoricalState(newVersion, keyOrdinalMapping, historicalDataAccess, latestHeaderEntries);

        addHistoricalState(historicalState);
        this.latestVersion = newVersion;
        this.latestHeaderEntries = latestHollowReadStateEngine.getHeaderTags();
    }

    /**
     * Call this method after each time a reverse delta occurs in the backing {@link HollowReadStateEngine}.  This
     * is how the HollowHistory knows how to create a new {@link HollowHistoricalState}.
     *
     * @param newVersion The version of the new state
     */
    public void reverseDeltaOccurred(long newVersion) {
        if(oldestHollowReadStateEngine == null) {
            throw new IllegalStateException("Read state engine for reverse direction history computation isn't initialized. " +
                    "This can occur if the required hollow history init sequence isn't followed or if oldestHollowReadStateEngine " +
                    "was discarded after history was initialized to max old versions");
        }
        if(historicalStates.size() >= maxHistoricalStatesToKeep) {
            throw new IllegalStateException("No. of history states reached max states capacity. HollowHistory does not " +
                    "support reaching this state when building history in reverse because older states would be evicted " +
                    "and history past here wouldn't be of contiguous versions");
        }

        // keyIndex is an ever-growing stat that maintains all primary key values ever seen, and when a reverse delta
        // update occurs we add any newly seen keys to it
        keyIndex.update(oldestHollowReadStateEngine, true);

        // Applying reverse delta from v2->v1 builds a historical data access state for v2
        //
        // The behavior for building history using reverse deltas is logically flipped i.e. it needs to track data
        // corresponding to ordinals that were added in reverse delta transition instead of removed in the delta direction,
        // because those will not be present in the latestReadStateEngine (oldestReadStateEngine is discarded eventually). So,
        // When building history using fwd deltas for e.g. v1->v2
        //  look up ordinals removed in v2, copy over data at those ordinals from v2's read state into a new data access
        //  under ordinals 0,1,2,etc. - this comprises the historical state corresponding to v2
        // When building history using rev delta for e.g. v2->v1:
        //  look up ordinals added in v1, copy over data at those ordinals from v1 read state a new data access under
        //  ordinals 0,1,2,etc. - this comprises the historical state corresponding to v2
        HollowHistoricalStateDataAccess historicalDataAccess = creator.createBasedOnNewDelta(oldestVersion, oldestHollowReadStateEngine, true);

        // Depending on directionality (delta or reverse delta) the significance of additions and removal is flipped
        HollowHistoricalStateKeyOrdinalMapping keyOrdinalMapping = createKeyOrdinalMappingFromDelta(oldestHollowReadStateEngine, true);
        HollowHistoricalState historicalState = new HollowHistoricalState(oldestVersion, keyOrdinalMapping, historicalDataAccess,
                oldestHollowReadStateEngine.getHeaderTags());

        addReverseHistoricalState(historicalState);
        this.oldestVersion = newVersion;
    }

    /**
     * Call this method after each time a double snapshot occurs that advances the latest version. Note that building
     * history using double snapshot in the reverse direction is not supported.
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
        if(newVersion <= latestVersion) {
            throw new UnsupportedOperationException("Double snapshot only supports advancing the latest version");
        }

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

        for(int i = 0; i < historicalStates.size(); i++) {
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

    // only called when doing a double snapshot
    private void remapHistoricalStateOrdinals(final DiffEqualityMappingOrdinalRemapper remapper, final HollowHistoricalStateDataAccess[] remappedDataAccesses, final HollowHistoricalStateKeyOrdinalMapping[] remappedKeyOrdinalMappings) {
        SimultaneousExecutor executor = new SimultaneousExecutor(getClass(), "remap");
        final int numThreads = executor.getCorePoolSize();

        for(int i = 0; i < executor.getCorePoolSize(); i++) {
            final int threadNumber = i;
            executor.execute(() -> {
                for(int t = threadNumber; t < historicalStates.size(); t += numThreads) {
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

    private HollowHistoricalStateKeyOrdinalMapping createKeyOrdinalMappingFromDelta(HollowReadStateEngine readStateEngine, boolean reverse) {
        HollowHistoricalStateKeyOrdinalMapping keyOrdinalMapping = new HollowHistoricalStateKeyOrdinalMapping(keyIndex);

        for(String keyType : keyIndex.getTypeKeyIndexes().keySet()) {
            HollowHistoricalStateTypeKeyOrdinalMapping typeMapping = keyOrdinalMapping.getTypeMapping(keyType);
            HollowObjectTypeReadState typeState = (HollowObjectTypeReadState) readStateEngine.getTypeState(keyType);
            if(typeState == null) {
                // The type is present in the history's primary key index but is not present
                // in the latest read state; ensure the mapping is initialized to the default state
                typeMapping.prepare(0, 0);
                typeMapping.finish();
                continue;
            }

            PopulatedOrdinalListener listener = typeState.getListener(PopulatedOrdinalListener.class);

            RemovedOrdinalIterator additionsIterator, removalIterator;
            if(reverse) {
                removalIterator = new RemovedOrdinalIterator(listener.getPopulatedOrdinals(), listener.getPreviousOrdinals());
                additionsIterator = new RemovedOrdinalIterator(listener);

            } else {
                removalIterator = new RemovedOrdinalIterator(listener);
                additionsIterator = new RemovedOrdinalIterator(listener.getPopulatedOrdinals(), listener.getPreviousOrdinals());
            }

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

    // place historicalState at the beginning of historicalStates
    //
    // historicalStates is ordered like: V3 -> V2 -> V1
    // however internally the states are linked like: V1.nextState = V2; V2.nextState = V3; etc.
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

    // place historicalState at the end of historicalStates
    //
    // historicalStates is ordered like: V3 -> V2 -> V1
    // however internally the states are linked like: V1.nextState = V2; V2.nextState = V3; etc.
    private void addReverseHistoricalState(HollowHistoricalState historicalState) {
        if(historicalStates.size() > 0) {
            historicalState.getDataAccess().setNextState(historicalStates.get(historicalStates.size() - 1).getDataAccess());
            historicalState.setNextState(historicalStates.get(historicalStates.size() - 1));
        } else { // if reverse delta occurs before any fwd deltas
            historicalState.getDataAccess().setNextState(latestHollowReadStateEngine);
        }

        historicalStates.add(historicalState);
        historicalStateLookupMap.put(historicalState.getVersion(), historicalState);

        if(historicalStates.size() >= maxHistoricalStatesToKeep) {
            // drop old read state because we won't be building history in reverse after we get here
            oldestHollowReadStateEngine = null;
        }
    }

    /**
     * Removes the oldest {@code n} historical states.
     *
     * @param n the number of historical states to remove
     * @throws IllegalArgumentException if the {@code n} is less than {@code 0} or
     * greater than the {@link #getNumberOfHistoricalStates() number} of historical
     * states.
     */
    public void removeHistoricalStates(int n) {
        if(n < 0) {
            throw new IllegalArgumentException(String.format(
                    "Number of states to remove is negative: %d", n));
        }
        if(n > historicalStates.size()) {
            throw new IllegalArgumentException(String.format(
                    "Number of states to remove, %d, is greater than the number of states. %d",
                    n, historicalStates.size()));
        }

        // drop oldest HollowReadStateEngine if it hasn't already been
        oldestHollowReadStateEngine = null;

        while(n-- > 0) {
            HollowHistoricalState removedState;
            removedState = historicalStates.remove(historicalStates.size() - 1);
            historicalStateLookupMap.remove(removedState.getVersion());
        }
    }
}

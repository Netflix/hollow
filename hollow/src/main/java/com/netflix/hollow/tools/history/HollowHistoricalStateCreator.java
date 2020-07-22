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

import com.netflix.hollow.core.read.HollowBlobInput;
import com.netflix.hollow.core.read.dataaccess.HollowDataAccess;
import com.netflix.hollow.core.read.engine.HollowBlobReader;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.read.engine.PopulatedOrdinalListener;
import com.netflix.hollow.core.read.engine.list.HollowListDeltaHistoricalStateCreator;
import com.netflix.hollow.core.read.engine.list.HollowListTypeReadState;
import com.netflix.hollow.core.read.engine.map.HollowMapDeltaHistoricalStateCreator;
import com.netflix.hollow.core.read.engine.map.HollowMapTypeReadState;
import com.netflix.hollow.core.read.engine.object.HollowObjectDeltaHistoricalStateCreator;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.core.read.engine.set.HollowSetDeltaHistoricalStateCreator;
import com.netflix.hollow.core.read.engine.set.HollowSetTypeReadState;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.schema.HollowSchemaSorter;
import com.netflix.hollow.core.util.HollowWriteStateCreator;
import com.netflix.hollow.core.util.IntMap;
import com.netflix.hollow.core.util.IntMap.IntMapEntryIterator;
import com.netflix.hollow.core.util.SimultaneousExecutor;
import com.netflix.hollow.core.write.HollowBlobWriter;
import com.netflix.hollow.core.write.HollowTypeWriteState;
import com.netflix.hollow.core.write.HollowWriteRecord;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.copy.HollowRecordCopier;
import com.netflix.hollow.tools.combine.IdentityOrdinalRemapper;
import com.netflix.hollow.tools.combine.OrdinalRemapper;
import com.netflix.hollow.tools.diff.exact.DiffEqualOrdinalMap;
import com.netflix.hollow.tools.diff.exact.DiffEqualityMapping;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Used to create a historical {@link HollowDataAccess}, even in the absence of a {@link HollowHistory}.
 *
 */
public class HollowHistoricalStateCreator {

    private final HollowHistory totalHistory;

    public HollowHistoricalStateCreator() {
        this(null);
    }

    public HollowHistoricalStateCreator(HollowHistory totalHistory) {
        this.totalHistory = totalHistory;
    }

    /**
     * Create a {@link HollowDataAccess} for the prior state of the supplied {@link HollowReadStateEngine} after a delta
     * has been applied.
     * 
     * @param version The state's version
     * @param stateEngine The current {@link HollowReadStateEngine} to which a delta has been applied.
     * @return a data access for history
     */
    public HollowHistoricalStateDataAccess createBasedOnNewDelta(long version, HollowReadStateEngine stateEngine) {
        IntMapOrdinalRemapper typeRemovedOrdinalMapping = new IntMapOrdinalRemapper();

        List<HollowTypeReadState> historicalTypeStates = new ArrayList<HollowTypeReadState>(stateEngine.getTypeStates().size());

        for(HollowTypeReadState typeState : stateEngine.getTypeStates()) {
            createDeltaHistoricalTypeState(typeRemovedOrdinalMapping, historicalTypeStates, typeState);
        }

        HollowHistoricalStateDataAccess dataAccess = new HollowHistoricalStateDataAccess(totalHistory, version, stateEngine, historicalTypeStates, typeRemovedOrdinalMapping, Collections.<String, HollowHistoricalSchemaChange>emptyMap());
        dataAccess.setNextState(stateEngine);

        return dataAccess;
    }

    private void createDeltaHistoricalTypeState(IntMapOrdinalRemapper typeRemovedOrdinalMapping, List<HollowTypeReadState> historicalTypeStates, HollowTypeReadState typeState) {
        if(typeState instanceof HollowObjectTypeReadState) {
            HollowObjectDeltaHistoricalStateCreator deltaHistoryCreator = new HollowObjectDeltaHistoricalStateCreator((HollowObjectTypeReadState)typeState);
            deltaHistoryCreator.populateHistory();
            typeRemovedOrdinalMapping.addOrdinalRemapping(typeState.getSchema().getName(), deltaHistoryCreator.getOrdinalMapping());
            historicalTypeStates.add(deltaHistoryCreator.createHistoricalTypeReadState());
        } else if(typeState instanceof HollowListTypeReadState) {
            HollowListDeltaHistoricalStateCreator deltaHistoryCreator = new HollowListDeltaHistoricalStateCreator((HollowListTypeReadState)typeState);
            deltaHistoryCreator.populateHistory();
            typeRemovedOrdinalMapping.addOrdinalRemapping(typeState.getSchema().getName(), deltaHistoryCreator.getOrdinalMapping());
            historicalTypeStates.add(deltaHistoryCreator.createHistoricalTypeReadState());
        } else if(typeState instanceof HollowSetTypeReadState) {
            HollowSetDeltaHistoricalStateCreator deltaHistoryCreator = new HollowSetDeltaHistoricalStateCreator((HollowSetTypeReadState)typeState);
            deltaHistoryCreator.populateHistory();
            typeRemovedOrdinalMapping.addOrdinalRemapping(typeState.getSchema().getName(), deltaHistoryCreator.getOrdinalMapping());
            historicalTypeStates.add(deltaHistoryCreator.createHistoricalTypeReadState());
        } else if(typeState instanceof HollowMapTypeReadState) {
            HollowMapDeltaHistoricalStateCreator deltaHistoryCreator = new HollowMapDeltaHistoricalStateCreator((HollowMapTypeReadState)typeState);
            deltaHistoryCreator.populateHistory();
            typeRemovedOrdinalMapping.addOrdinalRemapping(typeState.getSchema().getName(), deltaHistoryCreator.getOrdinalMapping());
            historicalTypeStates.add(deltaHistoryCreator.createHistoricalTypeReadState());
        }
    }

    /**
     * Create a {@link HollowDataAccess} for a {@link HollowHistory}.  Remap ordinal spaces for all prior historical
     * versions in the {@link HollowHistory} for consistency.
     *
     * @param version the version
     * @param previous the prior read state
     * @return the data access for a history
     */
    public HollowHistoricalStateDataAccess createConsistentOrdinalHistoricalStateFromDoubleSnapshot(long version, HollowReadStateEngine previous) {
        return new HollowHistoricalStateDataAccess(totalHistory, version, previous, IdentityOrdinalRemapper.INSTANCE, Collections.<String, HollowHistoricalSchemaChange>emptyMap());
    }

    /**
     * Create a {@link HollowDataAccess} for a historical state after a double snapshot occurs, without a {@link HollowHistory}. 
     *
     * @param version the version
     * @param previous the previous state
     * @param current the current state
     * @param ordinalRemapper the ordinal remapper
     * @return the data access for a history
     */
    public HollowHistoricalStateDataAccess createHistoricalStateFromDoubleSnapshot(long version, HollowReadStateEngine previous, HollowReadStateEngine current, DiffEqualityMappingOrdinalRemapper ordinalRemapper) {
        HollowWriteStateEngine writeEngine = HollowWriteStateCreator.createWithSchemas(schemasWithoutKeys(previous.getSchemas()));
        IntMapOrdinalRemapper typeRemovedOrdinalLookupMaps = new IntMapOrdinalRemapper();

        for(HollowSchema previousSchema : HollowSchemaSorter.dependencyOrderedSchemaList(previous)) {
            HollowTypeReadState previousTypeState = previous.getTypeState(previousSchema.getName());
            String typeName = previousTypeState.getSchema().getName();
            IntMap ordinalLookupMap;

            if(current.getTypeState(typeName) == null) {
                ordinalLookupMap = copyAllRecords(previousTypeState, ordinalRemapper, writeEngine);
            } else {
                HollowTypeReadState currentTypeState = current.getTypeState(typeName);
                BitSet currentlyPopulatedOrdinals = currentTypeState.getListener(PopulatedOrdinalListener.class).getPopulatedOrdinals();
                ordinalLookupMap = copyUnmatchedRecords(previousTypeState, ordinalRemapper, currentlyPopulatedOrdinals, writeEngine);
            }
            typeRemovedOrdinalLookupMaps.addOrdinalRemapping(typeName, ordinalLookupMap);
        }

        Map<String, HollowHistoricalSchemaChange> schemaChanges = calculateSchemaChanges(previous, current, ordinalRemapper.getDiffEqualityMapping());

        return new HollowHistoricalStateDataAccess(totalHistory, version, roundTripStateEngine(writeEngine), typeRemovedOrdinalLookupMaps, schemaChanges);
    }
    
    private Map<String, HollowHistoricalSchemaChange> calculateSchemaChanges(HollowReadStateEngine previous, HollowReadStateEngine current, DiffEqualityMapping equalityMapping) {
        Map<String, HollowHistoricalSchemaChange> schemaChanges = new HashMap<String, HollowHistoricalSchemaChange>();
        for(HollowTypeReadState previousTypeState : previous.getTypeStates()) {
            String typeName = previousTypeState.getSchema().getName();
            HollowTypeReadState currentTypeState = current.getTypeState(typeName);
            if(currentTypeState == null) {
                schemaChanges.put(typeName, new HollowHistoricalSchemaChange(previousTypeState.getSchema(), null));
            } else if (equalityMapping.requiresMissingFieldTraversal(typeName)) {
                schemaChanges.put(typeName, new HollowHistoricalSchemaChange(previousTypeState.getSchema(), currentTypeState.getSchema()));
            }
        }

        for(HollowTypeReadState currentTypeState : current.getTypeStates()) {
            String typeName = currentTypeState.getSchema().getName();
            HollowTypeReadState previousTypeState = previous.getTypeState(typeName);
            if(previousTypeState == null) {
                schemaChanges.put(typeName, new HollowHistoricalSchemaChange(null, currentTypeState.getSchema()));
            }
        }

        return schemaChanges;
    }

    private IntMap copyUnmatchedRecords(HollowTypeReadState previousTypeState, DiffEqualityMappingOrdinalRemapper ordinalRemapper, BitSet currentlyPopulatedOrdinals, HollowWriteStateEngine writeEngine) {
        String typeName = previousTypeState.getSchema().getName();
        PopulatedOrdinalListener previousListener = previousTypeState.getListener(PopulatedOrdinalListener.class);
        HollowRecordCopier recordCopier = HollowRecordCopier.createCopier(previousTypeState, ordinalRemapper, false);  ///NOTE: This will invalidate custom hash codes
        DiffEqualOrdinalMap equalityMap = ordinalRemapper.getDiffEqualityMapping().getEqualOrdinalMap(typeName);

        boolean shouldCopyAllRecords = ordinalRemapper.getDiffEqualityMapping().requiresMissingFieldTraversal(typeName);

        BitSet previouslyPopulatedOrdinals = previousListener.getPopulatedOrdinals();

        int ordinalSpaceLength = Math.max(currentlyPopulatedOrdinals.length(), previouslyPopulatedOrdinals.length());
        int unmatchedOrdinalCount = ordinalSpaceLength - countMatchedRecords(previouslyPopulatedOrdinals, equalityMap);
        int unmatchedRecordCount = countUnmatchedRecords(previouslyPopulatedOrdinals, equalityMap);
        int nextFreeOrdinal = ordinalSpaceLength;

        ordinalRemapper.hintUnmatchedOrdinalCount(typeName, unmatchedOrdinalCount * 2);
        IntMap ordinalLookupMap = new IntMap(shouldCopyAllRecords ? previouslyPopulatedOrdinals.cardinality() : unmatchedRecordCount);

        BitSet mappedFromOrdinals = new BitSet(ordinalSpaceLength);
        BitSet mappedToOrdinals = new BitSet(ordinalSpaceLength);

        int fromOrdinal = previouslyPopulatedOrdinals.nextSetBit(0);
        while(fromOrdinal != -1) {
            int matchedToOrdinal = equalityMap.getIdentityFromOrdinal(fromOrdinal);
            if(matchedToOrdinal != -1) {
                mappedFromOrdinals.set(fromOrdinal);
                mappedToOrdinals.set(matchedToOrdinal);

                if(shouldCopyAllRecords) {
                    HollowWriteRecord rec = recordCopier.copy(fromOrdinal);
                    int removedMappedOrdinal = writeEngine.add(typeName, rec);

                    ordinalLookupMap.put(matchedToOrdinal, removedMappedOrdinal);
                }
            }

            fromOrdinal = previouslyPopulatedOrdinals.nextSetBit(fromOrdinal + 1);
        }

        fromOrdinal = mappedFromOrdinals.nextClearBit(0);
        int toOrdinal = mappedToOrdinals.nextClearBit(0);
        while(fromOrdinal < ordinalSpaceLength) {
            ordinalRemapper.remapOrdinal(typeName, fromOrdinal, nextFreeOrdinal);
            ordinalRemapper.remapOrdinal(typeName, nextFreeOrdinal, toOrdinal);

            if(previouslyPopulatedOrdinals.get(fromOrdinal)) {
                HollowWriteRecord rec = recordCopier.copy(fromOrdinal);
                int removedMappedOrdinal = writeEngine.add(typeName, rec);

                ordinalLookupMap.put(nextFreeOrdinal, removedMappedOrdinal);
            }

            fromOrdinal = mappedFromOrdinals.nextClearBit(fromOrdinal + 1);
            toOrdinal = mappedToOrdinals.nextClearBit(toOrdinal + 1);
            nextFreeOrdinal++;
        }

        return ordinalLookupMap;
    }

    private int countMatchedRecords(BitSet populatedOrdinals, DiffEqualOrdinalMap equalityMap) {
        int matchedRecordCount = 0;
        int ordinal = populatedOrdinals.nextSetBit(0);
        while(ordinal != -1) {
            if(equalityMap.getIdentityFromOrdinal(ordinal) != -1)
                matchedRecordCount++;
            ordinal = populatedOrdinals.nextSetBit(ordinal + 1);
        }
        return matchedRecordCount;
    }

    private int countUnmatchedRecords(BitSet populatedOrdinals, DiffEqualOrdinalMap equalityMap) {
        int unmatchedRecordCount = 0;
        int ordinal = populatedOrdinals.nextSetBit(0);
        while(ordinal != -1) {
            if(equalityMap.getIdentityFromOrdinal(ordinal) == -1)
                unmatchedRecordCount++;
            ordinal = populatedOrdinals.nextSetBit(ordinal + 1);
        }
        return unmatchedRecordCount;
    }

    private IntMap copyAllRecords(HollowTypeReadState typeState, DiffEqualityMappingOrdinalRemapper ordinalRemapper, HollowWriteStateEngine writeEngine) {
        String typeName = typeState.getSchema().getName();
        HollowRecordCopier recordCopier = HollowRecordCopier.createCopier(typeState, ordinalRemapper, false);  ///NOTE: This will invalidate custom hash codes
        PopulatedOrdinalListener listener = typeState.getListener(PopulatedOrdinalListener.class);
        IntMap ordinalLookupMap = new IntMap(listener.getPopulatedOrdinals().cardinality());
        int ordinal = listener.getPopulatedOrdinals().nextSetBit(0);
        while(ordinal != -1) {
            HollowWriteRecord rec = recordCopier.copy(ordinal);
            int mappedOrdinal = writeEngine.add(typeName, rec);
            ordinalLookupMap.put(ordinal, mappedOrdinal);
            ordinal = listener.getPopulatedOrdinals().nextSetBit(ordinal + 1);
        }
        return ordinalLookupMap;
    }


    public HollowHistoricalStateDataAccess copyButRemapOrdinals(HollowHistoricalStateDataAccess previous, OrdinalRemapper ordinalRemapper) {
        HollowWriteStateEngine writeEngine = HollowWriteStateCreator.createWithSchemas(schemasWithoutKeys(previous.getSchemas()));

        IntMapOrdinalRemapper typeRemovedOrdinalRemapping = new IntMapOrdinalRemapper();

        for(String typeName : previous.getAllTypes()) {
            HollowHistoricalTypeDataAccess typeDataAccess = (HollowHistoricalTypeDataAccess) previous.getTypeDataAccess(typeName);
            copyRemappedRecords(typeDataAccess.getRemovedRecords(), ordinalRemapper, writeEngine);

            IntMap ordinalLookupMap = remapPreviousOrdinalMapping(typeDataAccess.getOrdinalRemap(), typeName, ordinalRemapper);
            typeRemovedOrdinalRemapping.addOrdinalRemapping(typeName, ordinalLookupMap);
        }

        return new HollowHistoricalStateDataAccess(totalHistory, previous.getVersion(), roundTripStateEngine(writeEngine), typeRemovedOrdinalRemapping, previous.getSchemaChanges());
    }

    private void copyRemappedRecords(HollowTypeReadState readTypeState, OrdinalRemapper ordinalRemapper, HollowWriteStateEngine writeEngine) {
        String typeName = readTypeState.getSchema().getName();
        HollowTypeWriteState typeState = writeEngine.getTypeState(typeName);
        HollowRecordCopier copier = HollowRecordCopier.createCopier(readTypeState, ordinalRemapper, false);  ///NOTE: This will invalidate custom hash codes

        for(int i=0;i<=readTypeState.maxOrdinal();i++) {
            HollowWriteRecord rec = copier.copy(i);
            typeState.add(rec);
        }
    }

    private IntMap remapPreviousOrdinalMapping(IntMap previousOrdinalMapping, String typeName, OrdinalRemapper ordinalRemapper) {
        IntMapEntryIterator ordinalMappingIter = previousOrdinalMapping.iterator();
        IntMap ordinalLookupMap = new IntMap(previousOrdinalMapping.size());

        while(ordinalMappingIter.next())
            ordinalLookupMap.put(ordinalRemapper.getMappedOrdinal(typeName, ordinalMappingIter.getKey()), ordinalMappingIter.getValue());
        return ordinalLookupMap;
    }

    private static HollowReadStateEngine roundTripStateEngine(HollowWriteStateEngine writeEngine) {
        HollowBlobWriter writer = new HollowBlobWriter(writeEngine);
        HollowReadStateEngine removedRecordCopies = new HollowReadStateEngine();
        HollowBlobReader reader = new HollowBlobReader(removedRecordCopies);

        // Use a pipe to write and read concurrently to avoid writing
        // to temporary files or allocating memory
        // @@@ for small states it's more efficient to sequentially write to
        // and read from a byte array but it is tricky to estimate the size
        SimultaneousExecutor executor = new SimultaneousExecutor(1, HollowHistoricalStateCreator.class, "round-trip");
        Exception pipeException = null;
        // Ensure read-side is closed after completion of read
        try (PipedInputStream in = new PipedInputStream(1 << 15)) {
            BufferedOutputStream out = new BufferedOutputStream(new PipedOutputStream(in));
            executor.execute(() -> {
                // Ensure write-side is closed after completion of write
                try (Closeable ac = out) {
                    writer.writeSnapshot(out);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            try (HollowBlobInput hbi = HollowBlobInput.serial(in)) {
                reader.readSnapshot(hbi);
            }
        } catch (Exception e) {
            pipeException = e;
        }

        // Ensure no underlying writer exception is lost due to broken pipe
        try {
            executor.awaitSuccessfulCompletion();
        } catch (InterruptedException | ExecutionException e) {
            if (pipeException == null) {
                throw new RuntimeException(e);
            }

            pipeException.addSuppressed(e);
        }
        if (pipeException != null)
            throw new RuntimeException(pipeException);

        return removedRecordCopies;
    }

    private List<HollowSchema> schemasWithoutKeys(List<HollowSchema> schemas) {
        List<HollowSchema> baldSchemas = new ArrayList<HollowSchema>();
        for(HollowSchema prevSchema : schemas)
            baldSchemas.add(HollowSchema.withoutKeys(prevSchema));
        return baldSchemas;
    }

}

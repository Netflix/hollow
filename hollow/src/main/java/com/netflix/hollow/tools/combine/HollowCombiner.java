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
package com.netflix.hollow.tools.combine;

import com.netflix.hollow.core.index.HollowPrimaryKeyIndex;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.memory.ByteArrayOrdinalMap;
import com.netflix.hollow.core.memory.ByteDataArray;
import com.netflix.hollow.core.memory.pool.WastefulRecycler;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.read.engine.PopulatedOrdinalListener;
import com.netflix.hollow.core.schema.HollowMapSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.schema.HollowSchema.SchemaType;
import com.netflix.hollow.core.schema.HollowSchemaSorter;
import com.netflix.hollow.core.schema.HollowSetSchema;
import com.netflix.hollow.core.util.HollowWriteStateCreator;
import com.netflix.hollow.core.util.SimultaneousExecutor;
import com.netflix.hollow.core.write.HollowHashableWriteRecord;
import com.netflix.hollow.core.write.HollowHashableWriteRecord.HashBehavior;
import com.netflix.hollow.core.write.HollowTypeWriteState;
import com.netflix.hollow.core.write.HollowWriteRecord;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.copy.HollowRecordCopier;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * The HollowCombiner is used to copy data from one or more copies of hollow datasets (a {@link HollowReadStateEngine}) into a single hollow dataset (a {@link HollowWriteStateEngine}).
 * <p>
 * By default, a HollowCombiner will copy all records from each provided {@link HollowReadStateEngine} into the destination.
 * <p>
 * A {@link HollowCombinerCopyDirector} can be provided, which will specify which specific ordinals to include/exclude while copying.
 * <p>
 * A set of {@link PrimaryKey} can be provided, which will ensure that no duplicate records, as defined by any of the provided keys, will be added to the destination state.
 *
 * @author dkoszewnik
 *
 */
public class HollowCombiner {

    private final HollowReadStateEngine[] inputs;
    private final OrdinalRemapper[] ordinalRemappers;

    private final HollowWriteStateEngine output;
    private final Set<String> typeNamesWithDefinedHashCodes;
    private final Set<String> ignoredTypes;

    private final HollowCombinerCopyDirector copyDirector;

    private List<PrimaryKey> primaryKeys;

    private final ThreadLocal<Map<String, HollowCombinerCopier>> copiersPerType;
    private final Map<String, ByteArrayOrdinalMap> hashOrderIndependentOrdinalMaps;

    /**
     * @param inputs the set of {@link HollowReadStateEngine} to combine data from.
     */
    public HollowCombiner(HollowReadStateEngine... inputs) {
        this(HollowWriteStateCreator.createWithSchemas(validateInputs(inputs)[0].getSchemas()), inputs);
    }

    static HollowReadStateEngine[] validateInputs(HollowReadStateEngine... inputs) {
        Objects.requireNonNull(inputs);
        if (inputs.length == 0) {
            throw new IllegalArgumentException("No input read state engines");
        }
        return inputs;
    }

    /**
     * @param director a {@link HollowCombinerCopyDirector} which will specify which specific records to copy from the input(s).
     * @param inputs the set of {@link HollowReadStateEngine} to combine data from.
     */
    public HollowCombiner(HollowCombinerCopyDirector director, HollowReadStateEngine... inputs) {
        this(director, HollowWriteStateCreator.createWithSchemas(inputs[0].getSchemas()), inputs);
    }

    /**
     * @param output the {@link HollowWriteStateEngine} to use as the destination.
     * @param inputs the set of {@link HollowReadStateEngine} to combine data from.
     */
    public HollowCombiner(HollowWriteStateEngine output, HollowReadStateEngine... inputs) {
        this(HollowCombinerCopyDirector.DEFAULT_DIRECTOR, output, inputs);
    }

    /**
     * @param copyDirector a {@link HollowCombinerCopyDirector} which will specify which specific records to copy from the input(s).
     * @param output the {@link HollowWriteStateEngine} to use as the destination.
     * @param inputs the set of {@link HollowReadStateEngine} to combine data from.
     */
    public HollowCombiner(HollowCombinerCopyDirector copyDirector, HollowWriteStateEngine output, HollowReadStateEngine... inputs) {
        Objects.requireNonNull(copyDirector);
        Objects.requireNonNull(output);

        this.inputs = validateInputs(inputs);
        this.output = output;

        this.typeNamesWithDefinedHashCodes = getAllTypesWithDefinedHashCodes();
        this.ordinalRemappers = new OrdinalRemapper[inputs.length];
        this.copiersPerType = new ThreadLocal<>();
        this.hashOrderIndependentOrdinalMaps = new HashMap<>();
        this.ignoredTypes = new HashSet<>();
        this.copyDirector = copyDirector;

        initializePrimaryKeys();
    }

    private Set<String> getAllTypesWithDefinedHashCodes() {
        Set<String> unionOfTypesWithDefinedHashCodes = new HashSet<>();
        for(HollowReadStateEngine input : inputs) {
            unionOfTypesWithDefinedHashCodes.addAll(input.getTypesWithDefinedHashCodes());
        }
        return unionOfTypesWithDefinedHashCodes;
    }

    /**
     * When provided, a set of {@link PrimaryKey} will ensure that no duplicate records are added to the destination state.
     *
     * If multiple records exist in the inputs matching a single value for any of the supplied primary keys, then only one such record
     * will be copied to the destination.  The specific record which is copied will be the record from the input which was supplied first in the constructor
     * of this HollowCombiner.
     *
     * Further, if any record <i>references</i> another record which was omitted because it would have been duplicate based on this rule, then that reference is
     * remapped in the destination state to the matching record which was chosen to be included.
     *
     * @param newKeys the new primary keys
     */
    public void setPrimaryKeys(PrimaryKey... newKeys) {
        Objects.requireNonNull(newKeys);
        if (newKeys.length == 0) {
            return;
        }

        if (inputs.length == 1) {
            return;
        }

        /// deduplicate new keys with existing keys
        //process existing ones first
        Map<String, PrimaryKey> keysByType = new HashMap<>();
        for (PrimaryKey primaryKey : primaryKeys) {
            keysByType.put(primaryKey.getType(), primaryKey);
        }

        // allow override
        for (PrimaryKey primaryKey : newKeys) {
            keysByType.put(primaryKey.getType(), primaryKey);
        }

        this.primaryKeys = sortPrimaryKeys(new ArrayList<>(keysByType.values()));
    }

    public List<PrimaryKey> getPrimaryKeys() {
        return this.primaryKeys;
    }
    
    private void initializePrimaryKeys() {
        if (inputs.length == 1) {
            this.primaryKeys = new ArrayList<>();
            return;
        }

        List<PrimaryKey> keys = new ArrayList<>();
        for (HollowSchema schema : output.getSchemas()) {
            if (schema.getSchemaType() == SchemaType.OBJECT && !ignoredTypes.contains(schema.getName())) {
                PrimaryKey pk = ((HollowObjectSchema) schema).getPrimaryKey();
                if (pk != null)
                    keys.add(pk);
            }
        }
        
        this.primaryKeys = sortPrimaryKeys(keys);
    }
    
    private List<PrimaryKey> sortPrimaryKeys(List<PrimaryKey> primaryKeys) {
        final List<HollowSchema> dependencyOrderedSchemas = HollowSchemaSorter.dependencyOrderedSchemaList(output.getSchemas());
        primaryKeys.sort(new Comparator<PrimaryKey>() {
            public int compare(PrimaryKey o1, PrimaryKey o2) {
                return schemaDependencyIdx(o1) - schemaDependencyIdx(o2);
            }

            private int schemaDependencyIdx(PrimaryKey key) {
                for (int i = 0; i < dependencyOrderedSchemas.size(); i++) {
                    if (dependencyOrderedSchemas.get(i).getName().equals(key.getType()))
                        return i;
                }
                throw new IllegalArgumentException("Primary key defined for non-existent type: " + key.getType());
            }
        });
        
        return primaryKeys;
    }

    /**
     * Specify a set of types not to copy.  Be careful: if any included types reference any of the specified types,
     * behavior is undefined.
     *
     * @param typeNames the type names to be ignored
     */
    public void addIgnoredTypes(String... typeNames) {
        for(String typeName : typeNames)
            ignoredTypes.add(typeName);
    }

    /**
     * Perform the combine operation.
     */
    public void combine() {
        SimultaneousExecutor executor = new SimultaneousExecutor(getClass(), "combine");
        final int numThreads = executor.getCorePoolSize();

        createOrdinalRemappers();
        createHashOrderIndependentOrdinalMaps();

        final Set<String> processedTypes = new HashSet<>();
        final Set<PrimaryKey> processedPrimaryKeys = new HashSet<>();
        final Set<PrimaryKey> selectedPrimaryKeys = new HashSet<>();

        while(processedTypes.size() < output.getOrderedTypeStates().size()){

            /// find the next primary keys
            for(PrimaryKey key : primaryKeys) {
                if (!processedPrimaryKeys.contains(key) && !ignoredTypes.contains(key.getType())) {
                    if(!isAnySelectedPrimaryKeyADependencyOf(key.getType(), selectedPrimaryKeys)) {
                        selectedPrimaryKeys.add(key);
                    }
                }
            }

            final Set<String> typesToProcessThisIteration = new HashSet<>();
            final Map<String, HollowPrimaryKeyIndex[]> primaryKeyIndexes = new HashMap<>();
            final HollowCombinerExcludePrimaryKeysCopyDirector primaryKeyCopyDirector = new HollowCombinerExcludePrimaryKeysCopyDirector(copyDirector);

            for(HollowSchema schema : output.getSchemas()) {
                if(!processedTypes.contains(schema.getName()) && !ignoredTypes.contains(schema.getName())) {
                    if(selectedPrimaryKeys.isEmpty() || isAnySelectedPrimaryKeyDependentOn(schema.getName(), selectedPrimaryKeys)) {
                        for(PrimaryKey pk : selectedPrimaryKeys) {
                            if(pk.getType().equals(schema.getName())) {
                                HollowPrimaryKeyIndex[] indexes = new HollowPrimaryKeyIndex[inputs.length];
                                for(int i=0;i<indexes.length;i++) {
                                    if(inputs[i].getTypeState(pk.getType()) != null)
                                        indexes[i] = new HollowPrimaryKeyIndex(inputs[i], pk);
                                }

                                for(int i=0;i<indexes.length;i++) {
                                    HollowTypeReadState typeState = inputs[i].getTypeState(pk.getType());
                                    if(typeState != null) {
                                        BitSet populatedOrdinals = typeState.getListener(PopulatedOrdinalListener.class).getPopulatedOrdinals();
    
                                        int ordinal = populatedOrdinals.nextSetBit(0);
                                        while(ordinal != -1) {
                                            if(primaryKeyCopyDirector.shouldCopy(typeState, ordinal)) {
                                                Object[] recordKey = indexes[i].getRecordKey(ordinal);
    
                                                for(int j=i+1;j<indexes.length;j++) {
                                                    primaryKeyCopyDirector.excludeKey(indexes[j], recordKey);
                                                }
                                            }
    
                                            ordinal = populatedOrdinals.nextSetBit(ordinal + 1);
                                        }
                                    }
                                }

                                primaryKeyIndexes.put(pk.getType(), indexes);
                            }
                        }

                        typesToProcessThisIteration.add(schema.getName());
                    }
                }
            }

            if(typesToProcessThisIteration.isEmpty())
                break;

            for(int i=0;i<numThreads;i++) {
                final int threadNumber = i;
                executor.execute(() -> {
                    for(int i1 =0; i1 <inputs.length; i1++) {
                        HollowCombinerCopyDirector copyDirector = selectedPrimaryKeys.isEmpty()
                                ? HollowCombiner.this.copyDirector
                                : primaryKeyCopyDirector;

                        HollowReadStateEngine inputEngine = inputs[i1];
                        OrdinalRemapper ordinalRemapper = selectedPrimaryKeys.isEmpty()
                                ? ordinalRemappers[i1]
                                : new HollowCombinerPrimaryKeyOrdinalRemapper(ordinalRemappers, primaryKeyIndexes, i1);

                        Map<String, HollowCombinerCopier> copierMap = new HashMap<>();
                        List<HollowCombinerCopier> copierList = new ArrayList<>();

                        for(String typeName : typesToProcessThisIteration) {
                            HollowTypeReadState readState = inputEngine.getTypeState(typeName);
                            HollowTypeWriteState writeState = output.getTypeState(typeName);
                            if (readState != null && writeState != null) {
                                HollowCombinerCopier copier = new HollowCombinerCopier(readState, writeState, ordinalRemapper);
                                copierList.add(copier);
                                copierMap.put(typeName, copier);
                            }
                        }

                        for(String typeName : processedTypes) {
                            HollowTypeReadState readState = inputEngine.getTypeState(typeName);
                            HollowTypeWriteState writeState = output.getTypeState(typeName);
                            if (readState != null && writeState != null) {
                                HollowCombinerCopier copier = new HollowCombinerCopier(readState, writeState, ordinalRemappers[i1]);
                                copierMap.put(typeName, copier);
                            }
                        }

                        copiersPerType.set(copierMap);

                        int currentOrdinal = threadNumber;

                        while(!copierList.isEmpty()) {
                            copyOrdinalForAllStates(currentOrdinal, copierList, ordinalRemapper, copyDirector);

                            currentOrdinal += numThreads;
                        }

                    }
                });
            }

            try {
                executor.awaitSuccessfulCompletionOfCurrentTasks();
            } catch(Throwable th) {
                throw new RuntimeException(th);
            }

            processedTypes.addAll(typesToProcessThisIteration);
            processedPrimaryKeys.addAll(selectedPrimaryKeys);
            selectedPrimaryKeys.clear();
        }

        executor.shutdown();
    }

    private boolean isAnySelectedPrimaryKeyADependencyOf(String type, Set<PrimaryKey> selectedPrimaryKeys) {
        for(PrimaryKey selectedKey : selectedPrimaryKeys) {
            if(HollowSchemaSorter.typeIsTransitivelyDependent(output, type, selectedKey.getType()))
                return true;
        }
        return false;
    }

    private boolean isAnySelectedPrimaryKeyDependentOn(String type, Set<PrimaryKey> selectedPrimaryKeys) {
        for(PrimaryKey selectedKey : selectedPrimaryKeys) {
            if(HollowSchemaSorter.typeIsTransitivelyDependent(output, selectedKey.getType(), type))
                return true;
        }
        return false;
    }


    /**
     * @return the destination {@link HollowWriteStateEngine}
     */
    public HollowWriteStateEngine getCombinedStateEngine() {
        return output;
    }

    void copyOrdinalForAllStates(int currentOrdinal, List<HollowCombinerCopier> copiers,
            OrdinalRemapper ordinalRemapper, HollowCombinerCopyDirector copyDirector) {
        Iterator<HollowCombinerCopier> iter = copiers.iterator();
        while(iter.hasNext()) {
            HollowCombinerCopier copier = iter.next();
            HollowTypeReadState readTypeState = copier.getReadTypeState();

            if(currentOrdinal <= readTypeState.maxOrdinal() && readTypeState.getPopulatedOrdinals().get(currentOrdinal)) {
                if(copyDirector.shouldCopy(readTypeState, currentOrdinal))
                    copier.copy(currentOrdinal);
            } else {
                iter.remove();
            }
        }
    }

    int copyOrdinal(String typeName, int currentOrdinal) {
        HollowCombinerCopier hollowCombinerCopier = copiersPerType.get().get(typeName);
        return hollowCombinerCopier == null
                ? currentOrdinal
                : hollowCombinerCopier.copy(currentOrdinal);
    }

    private OrdinalRemapper[] createOrdinalRemappers() {
        for(int i=0;i<ordinalRemappers.length;i++)
            ordinalRemappers[i] = new HollowCombinerOrdinalRemapper(this, inputs[i]);

        return ordinalRemappers;
    }

    private void createHashOrderIndependentOrdinalMaps() {
        for(HollowSchema schema : output.getSchemas()) {
            if(isDefinedHashCode(schema)) {
                hashOrderIndependentOrdinalMaps.put(schema.getName(), new ByteArrayOrdinalMap());
            }
        }
    }

    private boolean isDefinedHashCode(HollowSchema schema) {
        if(schema instanceof HollowSetSchema)
            return typeNamesWithDefinedHashCodes.contains(((HollowSetSchema)schema).getElementType());
        if(schema instanceof HollowMapSchema)
            return typeNamesWithDefinedHashCodes.contains(((HollowMapSchema)schema).getKeyType());
        return false;
    }

    private class HollowCombinerCopier {
        private final HollowRecordCopier copier;
        private final BitSet populatedOrdinals;
        private final HollowTypeWriteState writeState;
        private final OrdinalRemapper ordinalRemapper;
        private final ByteArrayOrdinalMap hashOrderIndependentOrdinalMap;
        private final ByteDataArray scratch;

        HollowCombinerCopier(HollowTypeReadState readState, HollowTypeWriteState writeState, OrdinalRemapper ordinalRemapper) {
            this.copier = HollowRecordCopier.createCopier(readState, writeState.getSchema(), ordinalRemapper, isDefinedHashCode(readState.getSchema()));
            this.populatedOrdinals = readState.getListener(PopulatedOrdinalListener.class).getPopulatedOrdinals();
            this.writeState = writeState;
            this.ordinalRemapper = ordinalRemapper;
            this.hashOrderIndependentOrdinalMap = hashOrderIndependentOrdinalMaps.get(readState.getSchema().getName());
            this.scratch = hashOrderIndependentOrdinalMap != null ? new ByteDataArray(WastefulRecycler.SMALL_ARRAY_RECYCLER) : null;
        }

        int copy(int ordinal) {
            if(isOrdinalPopulated(ordinal)) {
                if(!ordinalRemapper.ordinalIsMapped(getType(), ordinal)) {
                    HollowWriteRecord rec = copier.copy(ordinal);

                    if(hashOrderIndependentOrdinalMap == null) {
                        int outputOrdinal = writeState.add(rec);
                        ordinalRemapper.remapOrdinal(getType(), ordinal, outputOrdinal);
                        return outputOrdinal;
                    } else {
                        scratch.reset();
                        ((HollowHashableWriteRecord)rec).writeDataTo(scratch, HashBehavior.IGNORED_HASHES);
                        int outputOrdinal = hashOrderIndependentOrdinalMap.get(scratch);
                        if(outputOrdinal != -1)
                            return outputOrdinal;

                        synchronized(hashOrderIndependentOrdinalMap) {
                            outputOrdinal = hashOrderIndependentOrdinalMap.get(scratch);
                            if(outputOrdinal != -1)
                                return outputOrdinal;

                            outputOrdinal = writeState.add(rec);
                            ordinalRemapper.remapOrdinal(getType(), ordinal, outputOrdinal);
                            hashOrderIndependentOrdinalMap.put(scratch, outputOrdinal);
                        }
                    }
                }

                return ordinalRemapper.getMappedOrdinal(getType(), ordinal);
            }
            return -1;
        }

        boolean isOrdinalPopulated(int ordinal) {
            return populatedOrdinals.get(ordinal);
        }

        String getType() {
            return copier.getReadTypeState().getSchema().getName();
        }

        HollowTypeReadState getReadTypeState() {
            return copier.getReadTypeState();
        }
    }
}

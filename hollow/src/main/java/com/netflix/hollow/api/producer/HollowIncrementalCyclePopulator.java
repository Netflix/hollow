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
package com.netflix.hollow.api.producer;

import com.netflix.hollow.core.index.HollowPrimaryKeyIndex;
import com.netflix.hollow.core.memory.ThreadSafeBitSet;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.util.SimultaneousExecutor;
import com.netflix.hollow.core.write.HollowTypeWriteState;
import com.netflix.hollow.core.write.objectmapper.RecordPrimaryKey;
import com.netflix.hollow.core.write.objectmapper.flatrecords.FlatRecord;
import com.netflix.hollow.core.write.objectmapper.flatrecords.FlatRecordDumper;
import com.netflix.hollow.tools.traverse.TransitiveSetTraverser;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Used by HollowIncrementalProducer for Delta-Based Producer Input
 * @since 2.9.9
 * @deprecated see {@link HollowProducer.Incremental#runIncrementalCycle(HollowProducer.Incremental.IncrementalPopulator)}
 * @see HollowProducer.Incremental#runIncrementalCycle(HollowProducer.Incremental.IncrementalPopulator)
 */
@Deprecated
public class HollowIncrementalCyclePopulator implements HollowProducer.Populator {

    public static final Object DELETE_RECORD = new Object();

    private final double threadsPerCpu;
    private final Map<RecordPrimaryKey, Object> mutations;


    HollowIncrementalCyclePopulator(Map<RecordPrimaryKey, Object> mutations, double threadsPerCpu) {
        this.mutations = mutations;
        this.threadsPerCpu = threadsPerCpu;
    }

    @Override
    public void populate(HollowProducer.WriteState newState) throws Exception {
        newState.getStateEngine().addAllObjectsFromPreviousCycle();
        removeRecords(newState);
        addRecords(newState);
    }

    private void removeRecords(HollowProducer.WriteState newState) {
        if(newState.getPriorState() != null) {
            Collection<String> types = findTypesWithRemovedRecords(newState.getPriorState());
            Map<String, BitSet> recordsToRemove = markRecordsToRemove(newState.getPriorState(), types);
            removeRecordsFromNewState(newState, recordsToRemove);
        }
    }

    private Set<String> findTypesWithRemovedRecords(HollowProducer.ReadState readState) {
        Set<String> typesWithRemovedRecords = new HashSet<>();
        for(RecordPrimaryKey key : mutations.keySet()) {
            if(!typesWithRemovedRecords.contains(key.getType())) {
                HollowTypeReadState typeState = readState.getStateEngine().getTypeState(key.getType());
                if(typeState != null) {
                    typesWithRemovedRecords.add(key.getType());
                }
            }
        }
        return typesWithRemovedRecords;
    }

    private Map<String, BitSet> markRecordsToRemove(HollowProducer.ReadState priorState, Collection<String> types) {
        HollowReadStateEngine priorStateEngine = priorState.getStateEngine();

        Map<String, BitSet> recordsToRemove = new HashMap<>();
        for(String type : types) {
            recordsToRemove.put(type, markTypeRecordsToRemove(priorStateEngine, type));
        }

        TransitiveSetTraverser.addTransitiveMatches(priorStateEngine, recordsToRemove);
        TransitiveSetTraverser.removeReferencedOutsideClosure(priorStateEngine, recordsToRemove);

        return recordsToRemove;
    }

    private BitSet markTypeRecordsToRemove(HollowReadStateEngine priorStateEngine, final String type) {
        HollowTypeReadState priorReadState = priorStateEngine.getTypeState(type);
        HollowSchema schema = priorReadState.getSchema();
        int populatedOrdinals = priorReadState.getPopulatedOrdinals().length();
        if(schema.getSchemaType() == HollowSchema.SchemaType.OBJECT) {
            final HollowPrimaryKeyIndex idx = new HollowPrimaryKeyIndex(priorStateEngine, ((HollowObjectSchema) schema).getPrimaryKey()); ///TODO: Should we scan instead?  Can we create this once and do delta updates?

            ThreadSafeBitSet typeRecordsToRemove = new ThreadSafeBitSet(ThreadSafeBitSet.DEFAULT_LOG2_SEGMENT_SIZE_IN_BITS, populatedOrdinals);
            SimultaneousExecutor executor = new SimultaneousExecutor(threadsPerCpu, getClass(), "mark-type-records-to-remove");
            for(final Map.Entry<RecordPrimaryKey, Object> entry : mutations.entrySet()) {
                executor.execute(() -> {
                    if(entry.getKey().getType().equals(type)) {
                        int priorOrdinal = idx.getMatchingOrdinal(entry.getKey().getKey());

                        if(priorOrdinal != -1) {
                            if(entry.getValue() instanceof AddIfAbsent)
                                ((AddIfAbsent) entry.getValue()).wasFound = true;
                            else
                                typeRecordsToRemove.set(priorOrdinal);
                        }
                    }
                });
            }

            try {
                executor.awaitSuccessfulCompletion();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            return typeRecordsToRemove.toBitSet();
        }

        return new BitSet(populatedOrdinals);
    }

    private void removeRecordsFromNewState(HollowProducer.WriteState newState, Map<String, BitSet> recordsToRemove) {
        for(Map.Entry<String, BitSet> removalEntry : recordsToRemove.entrySet()) {
            HollowTypeWriteState writeState = newState.getStateEngine().getTypeState(removalEntry.getKey());
            BitSet typeRecordsToRemove = removalEntry.getValue();

            int ordinalToRemove = typeRecordsToRemove.nextSetBit(0);
            while(ordinalToRemove != -1) {
                writeState.removeOrdinalFromThisCycle(ordinalToRemove);
                ordinalToRemove = typeRecordsToRemove.nextSetBit(ordinalToRemove + 1);
            }
        }
    }

    private void addRecords(final HollowProducer.WriteState newState) {
        List<Map.Entry<RecordPrimaryKey, Object>> entryList = new ArrayList<>(mutations.entrySet());

        AtomicInteger nextMutation = new AtomicInteger(0);

        // @@@ Use parallel stream
        SimultaneousExecutor executor = new SimultaneousExecutor(threadsPerCpu, getClass(), "add-records");
        for(int i = 0; i < executor.getCorePoolSize(); i++) {
            executor.execute(() -> {
                FlatRecordDumper flatRecordDumper = null;
                int currentMutationIdx = nextMutation.getAndIncrement();

                while(currentMutationIdx < entryList.size()) {
                    Object currentMutation = entryList.get(currentMutationIdx).getValue();

                    if(currentMutation instanceof AddIfAbsent) {
                        AddIfAbsent aia = (AddIfAbsent) currentMutation;
                        if(aia.wasFound)
                            currentMutation = DELETE_RECORD;
                        else
                            currentMutation = aia.obj;
                    }

                    if(currentMutation != DELETE_RECORD) {
                        if(currentMutation instanceof FlatRecord) {
                            if(flatRecordDumper == null)
                                flatRecordDumper = new FlatRecordDumper(newState.getStateEngine());
                            flatRecordDumper.dump((FlatRecord) currentMutation);
                        } else {
                            newState.add(currentMutation);
                        }
                    }

                    currentMutationIdx = nextMutation.getAndIncrement();
                }

            });
        }

        try {
            executor.awaitSuccessfulCompletion();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static final class AddIfAbsent {
        private final Object obj;
        private boolean wasFound;

        public AddIfAbsent(Object obj) {
            this.obj = obj;
            this.wasFound = false;
        }

    }
}

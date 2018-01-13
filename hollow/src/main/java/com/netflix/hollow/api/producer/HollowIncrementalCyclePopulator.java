package com.netflix.hollow.api.producer;

import com.netflix.hollow.core.index.HollowPrimaryKeyIndex;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.util.SimultaneousExecutor;
import com.netflix.hollow.core.write.HollowTypeWriteState;
import com.netflix.hollow.core.write.objectmapper.RecordPrimaryKey;
import com.netflix.hollow.tools.traverse.TransitiveSetTraverser;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Used by HollowIncrementalProducer for Delta-Based Producer Input
 * @since 2.9.9
 */
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
        Map<String, BitSet> recordsToRemove = findTypesWithRemovedRecords(newState.getPriorState());
        markRecordsToRemove(newState.getPriorState(), recordsToRemove);
        removeRecordsFromNewState(newState, recordsToRemove);
    }

    private Map<String, BitSet> findTypesWithRemovedRecords(HollowProducer.ReadState readState) {
        Map<String, BitSet> recordsToRemove = new HashMap<String, BitSet>();
        for(RecordPrimaryKey key : mutations.keySet()) {
            if(!recordsToRemove.containsKey(key.getType())) {
                HollowTypeReadState typeState = readState.getStateEngine().getTypeState(key.getType());
                if(typeState != null) {
                    BitSet bs = new BitSet(typeState.getPopulatedOrdinals().length());
                    recordsToRemove.put(key.getType(), bs);
                }
            }
        }
        return recordsToRemove;
    }

    private void markRecordsToRemove(HollowProducer.ReadState priorState, Map<String, BitSet> recordsToRemove) {
        HollowReadStateEngine priorStateEngine = priorState.getStateEngine();

        for(Map.Entry<String, BitSet> removalEntry : recordsToRemove.entrySet()) {
            markTypeRecordsToRemove(priorStateEngine, removalEntry.getKey(), removalEntry.getValue());
        }

        TransitiveSetTraverser.addTransitiveMatches(priorStateEngine, recordsToRemove);
        TransitiveSetTraverser.removeReferencedOutsideClosure(priorStateEngine, recordsToRemove);
    }

    private void markTypeRecordsToRemove(HollowReadStateEngine priorStateEngine, String type, BitSet typeRecordsToRemove) {
        HollowTypeReadState priorReadState = priorStateEngine.getTypeState(type);
        HollowSchema schema = priorReadState.getSchema();
        if(schema.getSchemaType() == HollowSchema.SchemaType.OBJECT) {
            HollowPrimaryKeyIndex idx = new HollowPrimaryKeyIndex(priorStateEngine, ((HollowObjectSchema) schema).getPrimaryKey()); ///TODO: Should we scan instead?  Can we create this once and do delta updates?

            for(Map.Entry<RecordPrimaryKey, Object> entry : mutations.entrySet()) {
                if(entry.getKey().getType().equals(type)) {
                    int priorOrdinal = idx.getMatchingOrdinal(entry.getKey().getKey());

                    if(priorOrdinal != -1)
                        typeRecordsToRemove.set(priorOrdinal);
                }
            }
        }
    }

    private void removeRecordsFromNewState(HollowProducer.WriteState newState, Map<String, BitSet> recordsToRemove) {
        for(Map.Entry<String, BitSet> removalEntry : recordsToRemove.entrySet()) {
            HollowTypeWriteState writeState = newState.getStateEngine().getTypeState(removalEntry.getKey());
            BitSet typeRecordsToRemove = removalEntry.getValue();

            int ordinalToRemove = typeRecordsToRemove.nextSetBit(0);
            while(ordinalToRemove != -1) {
                writeState.removeOrdinalFromThisCycle(ordinalToRemove);
                ordinalToRemove = typeRecordsToRemove.nextSetBit(ordinalToRemove+1);
            }
        }
    }

    private void addRecords(final HollowProducer.WriteState newState) {
        SimultaneousExecutor executor = new SimultaneousExecutor(threadsPerCpu);
        for(final Map.Entry<RecordPrimaryKey, Object> entry : mutations.entrySet()) {
            executor.execute(new Runnable() {
                public void run() {
                    if(entry.getValue() != DELETE_RECORD)
                        newState.add(entry.getValue());
                }
            });
        }

        try {
            executor.awaitSuccessfulCompletion();
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
}

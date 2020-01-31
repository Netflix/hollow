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
package com.netflix.hollow.tools.patch.delta;

import com.netflix.hollow.core.memory.ThreadSafeBitSet;
import com.netflix.hollow.core.read.HollowReadFieldUtils;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.read.engine.PopulatedOrdinalListener;
import com.netflix.hollow.core.read.engine.list.HollowListTypeReadState;
import com.netflix.hollow.core.read.engine.map.HollowMapTypeReadState;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.core.read.engine.set.HollowSetTypeReadState;
import com.netflix.hollow.core.read.iterator.HollowMapEntryOrdinalIterator;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;
import com.netflix.hollow.core.schema.HollowMapSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.schema.HollowSchema.SchemaType;
import com.netflix.hollow.core.schema.HollowSchemaSorter;
import com.netflix.hollow.core.schema.HollowSetSchema;
import com.netflix.hollow.core.util.HollowWriteStateCreator;
import com.netflix.hollow.core.util.IntList;
import com.netflix.hollow.core.util.IntMap;
import com.netflix.hollow.core.util.LongList;
import com.netflix.hollow.core.util.SimultaneousExecutor;
import com.netflix.hollow.core.write.HollowTypeWriteState;
import com.netflix.hollow.core.write.HollowWriteRecord;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.copy.HollowRecordCopier;
import com.netflix.hollow.tools.combine.IdentityOrdinalRemapper;
import com.netflix.hollow.tools.traverse.TransitiveSetTraverser;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The HollowStateDeltaPatcher will create delta blobs which patch between two arbitrary states in a contiguous delta state chain.
 * <p>
 * This tool can be used in the following scenarios:
 * <ol>
 * <li>If a delta is lost (either published incorrectly or accidentally deleted from a file store), and
 * a replacement must be created.</li>
 * <li>If a long chain of deltas must be followed by clients, and it is desirable to create a shortcut to skip over many states</li>
 * </ol>
 * 
 * <p>
 * The HollowStateDeltaPatcher must create <b>two</b> deltas in order to map from one state to another.  The intermediate state
 * will move all of the records in ordinals which are shared by different records between two non-adjacent states out of the way.
 * This way, we maintain the guarantee that ghost records will continue to be accessible in adjacent states. 
 */
public class HollowStateDeltaPatcher {

    private final HollowReadStateEngine from;
    private final HollowReadStateEngine to;
    
    private final HollowWriteStateEngine writeEngine;
    private final List<HollowSchema> schemas;
    
    private Map<String, BitSet> changedOrdinalsBetweenStates;

    /**
     * Create a delta patcher which will patch between the states contained in the two state engines.
     * 
     * @param from The earlier state
     * @param to The later state.
     */
    public HollowStateDeltaPatcher(HollowReadStateEngine from, HollowReadStateEngine to) {
        this.from = from;
        this.to = to;
        this.schemas = HollowSchemaSorter.dependencyOrderedSchemaList(getCommonSchemas(from, to));
        this.writeEngine = HollowWriteStateCreator.createWithSchemas(schemas);
        this.changedOrdinalsBetweenStates = discoverChangedOrdinalsBetweenStates();
    }
    
    /**
     * Returns the HollowWriteStateEngine containing the state, use this to write the deltas and reverse deltas.
     * @return the HollowWriteStateEngine containing the state
     */
    public HollowWriteStateEngine getStateEngine() {
        return writeEngine;
    }
    
    /**
     * Call this method first.  After this returns, you can write a delta/reversedelta from/to the earlier state to/from the intermediate state.
     */
    public void prepareInitialTransition() {
        writeEngine.overridePreviousStateRandomizedTag(from.getCurrentRandomizedTag());
        copyUnchangedDataToIntermediateState();
        remapTheChangedDataToUnusedOrdinals();
    }
    
    /**
     * Call this method second.  After this returns, you can write a delta/reversedelta from/to the intermediate state to/from the later state.
     */
    public void prepareFinalTransition() {
        writeEngine.prepareForNextCycle();
        writeEngine.overrideNextStateRandomizedTag(to.getCurrentRandomizedTag());
        copyUnchangedDataToDestinationState();
        remapTheChangedDataToDestinationOrdinals();
    }
    
    private void copyUnchangedDataToIntermediateState() {
        SimultaneousExecutor executor = new SimultaneousExecutor(getClass(), "copy-unchanged");
        for(final HollowSchema schema : schemas) {
            executor.execute(new Runnable() {
                public void run() {
                    HollowTypeReadState fromTypeState = from.getTypeState(schema.getName());
                    HollowTypeWriteState writeTypeState = writeEngine.getTypeState(schema.getName());
                    BitSet changedOrdinals = changedOrdinalsBetweenStates.get(schema.getName());
                    HollowRecordCopier copier = HollowRecordCopier.createCopier(fromTypeState, schema, IdentityOrdinalRemapper.INSTANCE, true);
                    
                    BitSet fromOrdinals = fromTypeState.getListener(PopulatedOrdinalListener.class).getPopulatedOrdinals();
                    
                    int ordinal = fromOrdinals.nextSetBit(0);
                    while(ordinal != -1) {
                        boolean markCurrentCycle = !changedOrdinals.get(ordinal);
                        HollowWriteRecord rec = copier.copy(ordinal);
                        writeTypeState.mapOrdinal(rec, ordinal, true, markCurrentCycle);
                        
                        ordinal = fromOrdinals.nextSetBit(ordinal + 1);
                    }
                    
                    writeTypeState.recalculateFreeOrdinals();
                }
            });
        }
        
        try {
            executor.awaitSuccessfulCompletion();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private void remapTheChangedDataToUnusedOrdinals() {
        PartialOrdinalRemapper remapper = new PartialOrdinalRemapper();
        
        for(HollowSchema schema : schemas) {
            BitSet ordinalsToRemap = changedOrdinalsBetweenStates.get(schema.getName());
            
            HollowTypeReadState fromTypeState = from.getTypeState(schema.getName());
            HollowTypeReadState toTypeState = to.getTypeState(schema.getName());
            HollowTypeWriteState typeWriteState = writeEngine.getTypeState(schema.getName());
            
            IntMap ordinalRemapping = new IntMap(ordinalsToRemap.cardinality());
            
            int nextFreeOrdinal = Math.max(fromTypeState.maxOrdinal(), toTypeState.maxOrdinal()) + 1;
            
            boolean preserveHashPositions = shouldPreserveHashPositions(schema);
            HollowRecordCopier copier = HollowRecordCopier.createCopier(fromTypeState, schema, remapper, preserveHashPositions);
            
            int ordinal = ordinalsToRemap.nextSetBit(0);
            while(ordinal != -1 && ordinal <= fromTypeState.maxOrdinal()) {
                HollowWriteRecord copy = copier.copy(ordinal);
                typeWriteState.mapOrdinal(copy, nextFreeOrdinal, false, true);
                ordinalRemapping.put(ordinal, nextFreeOrdinal++);
                ordinal = ordinalsToRemap.nextSetBit(ordinal + 1);
            }
            
            remapper.addOrdinalRemapping(schema.getName(), ordinalRemapping);
            typeWriteState.recalculateFreeOrdinals();
        }
    }
    
    private void copyUnchangedDataToDestinationState() {
        for(HollowSchema schema : schemas) {
            HollowTypeWriteState writeTypeState = writeEngine.getTypeState(schema.getName());
            HollowTypeReadState toTypeState = to.getTypeState(schema.getName());
            HollowTypeReadState fromTypeState = from.getTypeState(schema.getName());
            BitSet toOrdinals = toTypeState.getListener(PopulatedOrdinalListener.class).getPopulatedOrdinals();
            BitSet fromOrdinals = fromTypeState.getListener(PopulatedOrdinalListener.class).getPopulatedOrdinals();
            BitSet changedOrdinals = changedOrdinalsBetweenStates.get(schema.getName());
            
            int ordinal = toOrdinals.nextSetBit(0);
            while(ordinal != -1) {
                if(!changedOrdinals.get(ordinal) && fromOrdinals.get(ordinal))
                    writeTypeState.addOrdinalFromPreviousCycle(ordinal);
                
                ordinal = toOrdinals.nextSetBit(ordinal + 1);
            }
        }
    }
    
    private void remapTheChangedDataToDestinationOrdinals() {
        for(HollowSchema schema : schemas) {
            BitSet changedOrdinals = changedOrdinalsBetweenStates.get(schema.getName());
            
            HollowTypeWriteState typeWriteState = writeEngine.getTypeState(schema.getName());
            
            HollowTypeReadState toReadState = to.getTypeState(schema.getName());
            HollowTypeReadState fromReadState = from.getTypeState(schema.getName());
            BitSet toOrdinals = toReadState.getListener(PopulatedOrdinalListener.class).getPopulatedOrdinals();
            BitSet fromOrdinals = fromReadState.getListener(PopulatedOrdinalListener.class).getPopulatedOrdinals();
            HollowRecordCopier copier = HollowRecordCopier.createCopier(toReadState, schema, IdentityOrdinalRemapper.INSTANCE, true);
            
            int ordinal = toOrdinals.nextSetBit(0);
            while(ordinal != -1) {
                if(!fromOrdinals.get(ordinal) || changedOrdinals.get(ordinal)) {
                    HollowWriteRecord copy = copier.copy(ordinal);
                    typeWriteState.mapOrdinal(copy, ordinal, false, true);
                }
                
                ordinal = toOrdinals.nextSetBit(ordinal + 1);
            }
            
            typeWriteState.recalculateFreeOrdinals();
        }
    }

    private Map<String, BitSet> discoverChangedOrdinalsBetweenStates() {
        SimultaneousExecutor executor = new SimultaneousExecutor(getClass(), "discover-changed");
        Map<String, BitSet> excludeOrdinalsFromCopy = new HashMap<String, BitSet>();
        
        for(HollowSchema schema : schemas) {
            BitSet recordsToExclude = findOrdinalsPopulatedWithDifferentRecords(schema.getName(), executor);

            excludeOrdinalsFromCopy.put(schema.getName(), recordsToExclude);
        }
        
        TransitiveSetTraverser.addReferencingOutsideClosure(from, excludeOrdinalsFromCopy);
        
        return excludeOrdinalsFromCopy;
    }
    
    private BitSet findOrdinalsPopulatedWithDifferentRecords(String typeName, SimultaneousExecutor executor) {
       final HollowTypeReadState fromTypeState = from.getTypeState(typeName);
       final HollowTypeReadState toTypeState = to.getTypeState(typeName);
       
       if(fromTypeState.getSchema().getSchemaType() != SchemaType.OBJECT)
           ensureEqualSchemas(fromTypeState, toTypeState);
       
       final BitSet fromOrdinals = fromTypeState.getListener(PopulatedOrdinalListener.class).getPopulatedOrdinals();
       final BitSet toOrdinals = toTypeState.getListener(PopulatedOrdinalListener.class).getPopulatedOrdinals();
        
       final int maxSharedOrdinal = Math.min(fromTypeState.maxOrdinal(), toTypeState.maxOrdinal());
       
       final ThreadSafeBitSet populatedOrdinalsWithDifferentRecords = new ThreadSafeBitSet();
       
       final int numThreads = executor.getCorePoolSize();

       for(int i=0;i<numThreads;i++) {
           final int threadNum = i;
           executor.execute(new Runnable() {
               public void run() {
                   
                   EqualityCondition equalityCondition = null;
                   
                   switch(fromTypeState.getSchema().getSchemaType()) {
                   case OBJECT:
                       equalityCondition = objectRecordEquality(fromTypeState, toTypeState);
                       break;
                   case LIST:
                       equalityCondition = listRecordEquality(fromTypeState, toTypeState);
                       break;
                   case SET:
                       equalityCondition = setRecordEquality(fromTypeState, toTypeState);
                       break;
                   case MAP:
                       equalityCondition = mapRecordEquality(fromTypeState, toTypeState);
                       break;
                   }
                   
                   for(int i=threadNum;i<=maxSharedOrdinal;i+=numThreads) {
                       if(fromOrdinals.get(i) && toOrdinals.get(i)) {
                           if(!equalityCondition.recordsAreEqual(i)) {
                               populatedOrdinalsWithDifferentRecords.set(i);
                           }
                       }
                   }
               }
           });
           
       }
       
       try {
           executor.awaitSuccessfulCompletionOfCurrentTasks();
       } catch(Exception e) {
           throw new RuntimeException(e);
       }
       
       return toBitSet(populatedOrdinalsWithDifferentRecords);
    }
    
    private BitSet toBitSet(ThreadSafeBitSet tsbs) {
        BitSet bs = new BitSet(tsbs.currentCapacity());
        
        int bit = tsbs.nextSetBit(0);
        while(bit != -1) {
            bs.set(bit);
            bit = tsbs.nextSetBit(bit+1);
        }
        
        return bs;
    }
    
    private static interface EqualityCondition {
        boolean recordsAreEqual(int ordinal);
    }
    
    private EqualityCondition objectRecordEquality(HollowTypeReadState fromState, HollowTypeReadState toState) {
        final HollowObjectTypeReadState fromObjectState = (HollowObjectTypeReadState)fromState;
        final HollowObjectTypeReadState toObjectState = (HollowObjectTypeReadState)toState;

        final HollowObjectSchema commonSchema = fromObjectState.getSchema().findCommonSchema(toObjectState.getSchema());

        return new EqualityCondition() {
            
            public boolean recordsAreEqual(int ordinal) {
                for(int i=0;i<commonSchema.numFields();i++) {
                    int fromFieldPos = fromObjectState.getSchema().getPosition(commonSchema.getFieldName(i));
                    int toFieldPos = toObjectState.getSchema().getPosition(commonSchema.getFieldName(i));
                    
                    if(commonSchema.getFieldType(i) == FieldType.REFERENCE) {
                        if(fromObjectState.readOrdinal(ordinal, fromFieldPos) != toObjectState.readOrdinal(ordinal, toFieldPos))
                            return false;
                    } else if(!HollowReadFieldUtils.fieldsAreEqual(fromObjectState, ordinal, fromFieldPos, toObjectState, ordinal, toFieldPos)) {
                        return false;
                    }
                }
                
                return true;
            }
        };
    }
    
    private EqualityCondition listRecordEquality(HollowTypeReadState fromState, HollowTypeReadState toState) {
        final HollowListTypeReadState fromListState = (HollowListTypeReadState)fromState;
        final HollowListTypeReadState toListState = (HollowListTypeReadState)toState;
        
        return new EqualityCondition() {
            public boolean recordsAreEqual(int ordinal) {
                int size = fromListState.size(ordinal);
                if(toListState.size(ordinal) != size)
                    return false;
                
                for(int i=0;i<size;i++) {
                    if(fromListState.getElementOrdinal(ordinal, i) != toListState.getElementOrdinal(ordinal, i))
                        return false;
                }
                
                return true;
            }
        };
    }

    private EqualityCondition setRecordEquality(HollowTypeReadState fromState, HollowTypeReadState toState) {
        final HollowSetTypeReadState fromSetState = (HollowSetTypeReadState)fromState;
        final HollowSetTypeReadState toSetState = (HollowSetTypeReadState)toState;
        
        return new EqualityCondition() {
            final IntList fromScratch = new IntList();
            final IntList toScratch = new IntList();
            
            public boolean recordsAreEqual(int ordinal) {
                int size = fromSetState.size(ordinal);
                if(toSetState.size(ordinal) != size)
                    return false;
                
                fromScratch.clear();
                toScratch.clear();
                
                HollowOrdinalIterator iter = fromSetState.ordinalIterator(ordinal);
                int next = iter.next();
                while(next != HollowOrdinalIterator.NO_MORE_ORDINALS) {
                    fromScratch.add(next);
                    next = iter.next();
                }
                
                iter = toSetState.ordinalIterator(ordinal);
                next = iter.next();
                while(next != HollowOrdinalIterator.NO_MORE_ORDINALS) {
                    toScratch.add(next);
                    next = iter.next();
                }
                
                fromScratch.sort();
                toScratch.sort();
                
                return fromScratch.equals(toScratch);
            }
        };
    }
    
    private EqualityCondition mapRecordEquality(HollowTypeReadState fromState, HollowTypeReadState toState) {
        final HollowMapTypeReadState fromMapState = (HollowMapTypeReadState) fromState;
        final HollowMapTypeReadState toMapState = (HollowMapTypeReadState) toState;
        
        return new EqualityCondition() {
            final LongList fromScratch = new LongList();
            final LongList toScratch = new LongList();
            
            public boolean recordsAreEqual(int ordinal) {
                int size = fromMapState.size(ordinal);
                if(toMapState.size(ordinal) != size)
                    return false;
                
                fromScratch.clear();
                toScratch.clear();
                
                HollowMapEntryOrdinalIterator iter = fromMapState.ordinalIterator(ordinal);
                while(iter.next())
                    fromScratch.add(((long)iter.getKey() << 32) | iter.getValue());
                
                iter = toMapState.ordinalIterator(ordinal);
                while(iter.next())
                    toScratch.add(((long)iter.getKey() << 32) | iter.getValue());
                
                fromScratch.sort();
                toScratch.sort();
                
                return fromScratch.equals(toScratch);
            }
        };
    }
    
    private void ensureEqualSchemas(HollowTypeReadState fromState, HollowTypeReadState toState) {
        if(!fromState.getSchema().equals(toState.getSchema()))
            throw new IllegalStateException("FROM and TO schemas were not the same: " + fromState.getSchema().getName());
    }
    
    private Set<HollowSchema> getCommonSchemas(HollowReadStateEngine from, HollowReadStateEngine to) {
        Set<HollowSchema> schemas = new HashSet<HollowSchema>();
        
        for(HollowSchema fromSchema : from.getSchemas()) {
            HollowSchema toSchema = to.getTypeState(fromSchema.getName()).getSchema();

            if(toSchema != null) {
                if(fromSchema.getSchemaType() == SchemaType.OBJECT) {
                    HollowObjectSchema commonSchema = ((HollowObjectSchema)fromSchema).findCommonSchema((HollowObjectSchema)toSchema);
                    schemas.add(commonSchema);
                } else {
                    schemas.add(toSchema);
                }
            }
        }
        
        return schemas;
    }
    
    private boolean shouldPreserveHashPositions(HollowSchema schema) {
        switch(schema.getSchemaType()) {
        case MAP:
            return from.getTypesWithDefinedHashCodes().contains(((HollowMapSchema)schema).getKeyType());
        case SET:
            return from.getTypesWithDefinedHashCodes().contains(((HollowSetSchema)schema).getElementType());
        default:
            return false;
        }
    }

}

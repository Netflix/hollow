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
package com.netflix.hollow.tools.compact;

import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.read.engine.PopulatedOrdinalListener;
import com.netflix.hollow.core.schema.HollowMapSchema;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.schema.HollowSchemaSorter;
import com.netflix.hollow.core.schema.HollowSetSchema;
import com.netflix.hollow.core.util.IntMap;
import com.netflix.hollow.core.write.HollowTypeWriteState;
import com.netflix.hollow.core.write.HollowWriteRecord;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.copy.HollowRecordCopier;
import com.netflix.hollow.tools.patch.delta.PartialOrdinalRemapper;
import com.netflix.hollow.tools.traverse.TransitiveSetTraverser;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * During a long delta chain, it's possible that a large number of holes in the ordinal space will exist in some types.
 * <p>
 * The HollowCompactor can reclaim space by moving records off of the high end of the ordinal space into these holes.
 * <p>
 * This is accomplished by producing deltas which <i>only</i> include removals and additions of identical records
 * allocated to more optimal ordinals.
 * <p>
 * This must sometimes be accomplished with a series of deltas, because the remapping of one state will cause some removals/additions
 * in referencing states (since they will point to new ordinals).  In a single delta transition, the HollowCompactor will
 * only attempt to compact a set of types which are not referencing each other (either directly or transitively).
 * 
 */
public class HollowCompactor {

    private final HollowWriteStateEngine writeEngine;
    private final HollowReadStateEngine readEngine;

    private long minCandidateHoleCostInBytes;
    private int minCandidateHolePercentage;

    /**
     * Provide the state engines on which to operate, and the criteria to identify when a compaction is necessary 
     * 
     * @param writeEngine the HollowWriteStateEngine to compact
     * @param readEngine  a HollowReadStateEngine at the same data state as the writeEngine
     * @param config      The criteria to identify when a compaction is necessary. 
     */
    public HollowCompactor(HollowWriteStateEngine writeEngine, HollowReadStateEngine readEngine, CompactionConfig config) {
        this(writeEngine, readEngine, config.getMinCandidateHoleCostInBytes(), config.getMinCandidateHolePercentage());
    }

    /**
     * Provide the state engines on which to operate, and the criteria to identify when a compaction is necessary 
     * 
     * @param writeEngine                 the HollowWriteStateEngine to compact
     * @param readEngine                  a HollowReadStateEngine at the same data state as the writeEngine
     * @param minCandidateHoleCostInBytes identify a type as a candidate for compaction only when the bytes used by ordinal holes exceeds this value 
     * @param minCandidateHolePercentage  identify a type as a candidate for compaction only when the percentage of space used by ordinal holes exceeds this value
     */
    public HollowCompactor(HollowWriteStateEngine writeEngine, HollowReadStateEngine readEngine, long minCandidateHoleCostInBytes, int minCandidateHolePercentage) {
        this.writeEngine = writeEngine;
        this.readEngine = readEngine;
        this.minCandidateHoleCostInBytes = minCandidateHoleCostInBytes;
        this.minCandidateHolePercentage = minCandidateHolePercentage;
    }

    /**
     * Determine whether a compaction is necessary, based on the criteria specified in the constructor.
     * @return {@code true} if compaction is necessary, otherwise {@code false}
     */
    public boolean needsCompaction() {
        return !findCompactionTargets().isEmpty();
    }

    /**
     * Perform a compaction.  It is expected that:
     * 
     * <ul>
     *   <li>the {@link HollowWriteStateEngine} supplied in the constructor is unmodified since the 
     *       last call to {@link HollowWriteStateEngine#prepareForNextCycle()}</li>
     *   <li>the {@link HollowReadStateEngine} supplied in the constructor reflects the same state as 
     *       the HollowWriteStateEngine.</li>
     * </ul>
     *   
     */
    public void compact() {
        Set<String> compactionTargets = findCompactionTargets();

        Map<String, BitSet> relocatedOrdinals = new HashMap<String, BitSet>();
        PartialOrdinalRemapper remapper = new PartialOrdinalRemapper();

        for(String compactionTarget : compactionTargets) {
            HollowTypeReadState typeState = readEngine.getTypeState(compactionTarget);
            HollowTypeWriteState writeState = writeEngine.getTypeState(compactionTarget);
            BitSet populatedOrdinals = typeState.getListener(PopulatedOrdinalListener.class).getPopulatedOrdinals();
            BitSet typeRelocatedOrdinals = new BitSet(populatedOrdinals.length());
            int populatedCardinality = populatedOrdinals.cardinality();

            writeState.addAllObjectsFromPreviousCycle();

            int numRelocations = 0;
            int ordinalToRelocate = populatedOrdinals.nextSetBit(populatedCardinality);
            while(ordinalToRelocate != -1) {
                numRelocations++;
                ordinalToRelocate = populatedOrdinals.nextSetBit(ordinalToRelocate + 1);
            }

            HollowRecordCopier copier = HollowRecordCopier.createCopier(typeState);
            IntMap remappedOrdinals = new IntMap(numRelocations);

            ordinalToRelocate = populatedOrdinals.length();
            int relocatePosition = -1;

            try {

                for(int i = 0; i < numRelocations; i++) {
                    while(!populatedOrdinals.get(--ordinalToRelocate)) ;
                    relocatePosition = populatedOrdinals.nextClearBit(relocatePosition + 1);
                    typeRelocatedOrdinals.set(ordinalToRelocate);
                    writeState.removeOrdinalFromThisCycle(ordinalToRelocate);
                    HollowWriteRecord rec = copier.copy(ordinalToRelocate);
                    writeState.mapOrdinal(rec, relocatePosition, false, true);
                    remappedOrdinals.put(ordinalToRelocate, relocatePosition);
                }

            } finally {
                writeState.recalculateFreeOrdinals();
            }

            remapper.addOrdinalRemapping(compactionTarget, remappedOrdinals);
            relocatedOrdinals.put(compactionTarget, typeRelocatedOrdinals);
        }

        /// find the referencing dependents
        TransitiveSetTraverser.addReferencingOutsideClosure(readEngine, relocatedOrdinals);

        /// copy all forward except remapped and transitive dependents of remapped
        for(HollowSchema schema : HollowSchemaSorter.dependencyOrderedSchemaList(writeEngine.getSchemas())) {
            if(!compactionTargets.contains(schema.getName())) {
                HollowTypeWriteState writeState = writeEngine.getTypeState(schema.getName());

                writeState.addAllObjectsFromPreviousCycle();

                BitSet typeRelocatedOrdinals = relocatedOrdinals.get(schema.getName());
                if(typeRelocatedOrdinals != null) {
                    HollowTypeReadState readState = readEngine.getTypeState(schema.getName());
                    IntMap remappedOrdinals = new IntMap(typeRelocatedOrdinals.cardinality());

                    boolean preserveHashPositions = shouldPreserveHashPositions(schema);
                    HollowRecordCopier copier = HollowRecordCopier.createCopier(readState, remapper, preserveHashPositions);

                    int remapOrdinal = typeRelocatedOrdinals.nextSetBit(0);
                    while(remapOrdinal != -1) {
                        HollowWriteRecord rec = copier.copy(remapOrdinal);
                        int newOrdinal = writeState.add(rec);
                        remappedOrdinals.put(remapOrdinal, newOrdinal);
                        writeState.removeOrdinalFromThisCycle(remapOrdinal);

                        remapOrdinal = typeRelocatedOrdinals.nextSetBit(remapOrdinal + 1);
                    }

                    remapper.addOrdinalRemapping(schema.getName(), remappedOrdinals);
                }
            }
        }
    }

    /**
     * Find candidate types for compaction.  No two types in the returned set will have a dependency relationship, either
     * directly or transitively.  
     */
    private Set<String> findCompactionTargets() {
        List<HollowSchema> schemas = HollowSchemaSorter.dependencyOrderedSchemaList(readEngine.getSchemas());
        Set<String> typesToCompact = new HashSet<String>();

        for(HollowSchema schema : schemas) {
            if(isCompactionCandidate(schema.getName())) {
                if(!candidateIsDependentOnAnyTargetedType(schema.getName(), typesToCompact))
                    typesToCompact.add(schema.getName());
            }
        }

        return typesToCompact;
    }

    private boolean isCompactionCandidate(String typeName) {
        HollowTypeReadState typeState = readEngine.getTypeState(typeName);
        BitSet populatedOrdinals = typeState.getListener(PopulatedOrdinalListener.class).getPopulatedOrdinals();
        double numOrdinals = populatedOrdinals.length();
        double numHoles = populatedOrdinals.length() - populatedOrdinals.cardinality();
        double holePercentage = numHoles / numOrdinals * 100d;
        long approximateHoleCostInBytes = typeState.getApproximateHoleCostInBytes();
        boolean isCompactionCandidate = holePercentage > (double) minCandidateHolePercentage && approximateHoleCostInBytes > minCandidateHoleCostInBytes;
        return isCompactionCandidate;
    }

    private boolean candidateIsDependentOnAnyTargetedType(String type, Set<String> targetedTypes) {
        for(String targetedType : targetedTypes) {
            if(HollowSchemaSorter.typeIsTransitivelyDependent(readEngine, type, targetedType))
                return true;
        }

        return false;
    }

    private boolean shouldPreserveHashPositions(HollowSchema schema) {
        switch(schema.getSchemaType()) {
            case MAP:
                return readEngine.getTypesWithDefinedHashCodes().contains(((HollowMapSchema) schema).getKeyType());
            case SET:
                return readEngine.getTypesWithDefinedHashCodes().contains(((HollowSetSchema) schema).getElementType());
            default:
                return false;
        }
    }

    /**
     * A configuration that specifies when a type is a candidate for compaction.
     */
    public static class CompactionConfig {
        private final long minCandidateHoleCostInBytes;
        private final int minCandidateHolePercentage;

        /**
         * Create a new compaction.  Both of the criteria specified by the following parameters must be met in order for a type
         * to be considered a candidate for compaction.
         * 
         * @param minCandidateHoleCostInBytes identify a type as a candidate for compaction only when the bytes used by ordinal holes exceeds this value 
         * @param minCandidateHolePercentage identify a type as a candidate for compaction only when the percentage of space used by ordinal holes exceeds this value
         */
        public CompactionConfig(long minCandidateHoleCostInBytes, int minCandidateHolePercentage) {
            this.minCandidateHoleCostInBytes = minCandidateHoleCostInBytes;
            this.minCandidateHolePercentage = minCandidateHolePercentage;
        }

        public long getMinCandidateHoleCostInBytes() {
            return minCandidateHoleCostInBytes;
        }

        public int getMinCandidateHolePercentage() {
            return minCandidateHolePercentage;
        }
    }
}

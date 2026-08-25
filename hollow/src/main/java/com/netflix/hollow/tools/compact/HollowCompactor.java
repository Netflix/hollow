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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

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
 * <p>
 * By default, a single call to {@link #compact()} relocates every misplaced record in each targeted type, this could
 * lead to large deltas. Specifying an approxDeltaBytesPerCycle instead bounds the size of the produced delta: each cycle
 * will then compact only a single type (one with the biggest hole footprint), and only as many of its records as fit
 * within the budget. The budget accounts for the referencing closure as well.
 * <p>
 * A budget too small to accommodate even one record together with its closure is reported and no compaction is performed.
 * <p>
 * A compactor must not be retained across cycles, nor reused once its read state has been refreshed; construct a new one per cycle.
 */
public class HollowCompactor {

    private static final Logger log = Logger.getLogger(HollowCompactor.class.getName());

    private final HollowWriteStateEngine writeEngine;
    private final HollowReadStateEngine readEngine;

    private long minCandidateHoleCostInBytes;
    private int minCandidateHolePercentage;
    private long approxDeltaBytesPerCycle;
    private Map<String, Integer> compactionPlan;

    /**
     * Provide the state engines on which to operate, and the criteria to identify when a compaction is necessary 
     * 
     * @param writeEngine the HollowWriteStateEngine to compact
     * @param readEngine  a HollowReadStateEngine at the same data state as the writeEngine
     * @param config      The criteria to identify when a compaction is necessary. 
     */
    public HollowCompactor(HollowWriteStateEngine writeEngine, HollowReadStateEngine readEngine, CompactionConfig config) {
        this(writeEngine, readEngine, config.getMinCandidateHoleCostInBytes(), config.getMinCandidateHolePercentage(), config.getApproxDeltaBytesPerCycle());
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
        this(writeEngine, readEngine, minCandidateHoleCostInBytes, minCandidateHolePercentage, Long.MAX_VALUE);
    }

    /**
     * Provide the state engines on which to operate, and the criteria to identify when a compaction is necessary
     *
     * @param writeEngine                 the HollowWriteStateEngine to compact
     * @param readEngine                  a HollowReadStateEngine at the same data state as the writeEngine
     * @param minCandidateHoleCostInBytes identify a type as a candidate for compaction only when the bytes used by ordinal holes exceeds this value
     * @param minCandidateHolePercentage  identify a type as a candidate for compaction only when the percentage of space used by ordinal holes exceeds this value
     * @param approxDeltaBytesPerCycle    approx max delta size for a compaction cycle, and when specified a cycle compacts only at most one type in per compaction cycle
     */
    public HollowCompactor(HollowWriteStateEngine writeEngine, HollowReadStateEngine readEngine, long minCandidateHoleCostInBytes, int minCandidateHolePercentage, long approxDeltaBytesPerCycle) {
        if(approxDeltaBytesPerCycle < 1)
            throw new IllegalArgumentException("approxDeltaBytesPerCycle must be at least 1, was " + approxDeltaBytesPerCycle);

        this.writeEngine = writeEngine;
        this.readEngine = readEngine;
        this.minCandidateHoleCostInBytes = minCandidateHoleCostInBytes;
        this.minCandidateHolePercentage = minCandidateHolePercentage;
        this.approxDeltaBytesPerCycle = approxDeltaBytesPerCycle;
    }
    
    /**
     * Determine whether a compaction is necessary, based on the criteria specified in the constructor.
     * @return {@code true} if compaction is necessary, otherwise {@code false}
     */
    public boolean needsCompaction() {
        return !calculateCompactionPlan().isEmpty();
    }
    
    /**
     * Perform a compaction.  It is expected that:
     * 
     * <ul>
     *   <li>the {@link HollowWriteStateEngine} supplied in the constructor is unmodified since the 
     *       last call to {@link HollowWriteStateEngine#prepareForNextCycle()}</li>
     *   <li>the {@link HollowReadStateEngine} supplied in the constructor reflects the same state as
     *       the HollowWriteStateEngine.</li>
     *   <li>this HollowCompactor has not already been used for an earlier cycle -- an instance plans its work from
     *       the read state once, so a stale plan would relocate the wrong ordinals.</li>
     * </ul>
     *   
     */
    public void compact() {
        Map<String, Integer> plan = calculateCompactionPlan();

        Map<String, BitSet> relocatedOrdinals = new HashMap<String, BitSet>();
        PartialOrdinalRemapper remapper = new PartialOrdinalRemapper();

        for(Map.Entry<String, Integer> plannedCompaction : plan.entrySet()) {
            String compactionTarget = plannedCompaction.getKey();
            int numRelocations = plannedCompaction.getValue();

            HollowTypeReadState typeState = readEngine.getTypeState(compactionTarget);
            HollowTypeWriteState writeState = writeEngine.getTypeState(compactionTarget);
            BitSet populatedOrdinals = populatedOrdinals(compactionTarget);
            BitSet typeRelocatedOrdinals = new BitSet(populatedOrdinals.length());

            writeState.addAllObjectsFromPreviousCycle();

            HollowRecordCopier copier = HollowRecordCopier.createCopier(typeState);
            IntMap remappedOrdinals = new IntMap(numRelocations);

            int ordinalToRelocate = populatedOrdinals.length();
            int relocatePosition = -1;
            
            try {
                
                for(int i=0;i<numRelocations;i++) {
                    while(!populatedOrdinals.get(--ordinalToRelocate));
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
            if(!plan.containsKey(schema.getName())) {
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
     * The types to compact in this cycle, mapped to the number of records to relocate from each.  A type is omitted
     * when the smallest batch it could produce (record + closure) > approxDeltaBytesPerCycle.
     */
    private Map<String, Integer> calculateCompactionPlan() {
        if(compactionPlan != null)
            return compactionPlan;

        Set<String> compactionTargets = findCompactionTargets();

        if(approxDeltaBytesPerCycle != Long.MAX_VALUE)
            compactionTargets = focusOnCostliestType(compactionTargets);

        Map<String, Integer> plan = new HashMap<String, Integer>();

        for(String compactionTarget : compactionTargets) {
            BitSet populatedOrdinals = populatedOrdinals(compactionTarget);
            int numRelocations = numMisplacedOrdinals(populatedOrdinals);

            /// relocating the highest ordinals
            if(approxDeltaBytesPerCycle != Long.MAX_VALUE)
                numRelocations = relocationsWithinDeltaBudget(compactionTarget, populatedOrdinals, numRelocations);

            if(numRelocations > 0)
                plan.put(compactionTarget, numRelocations);
        }

        compactionPlan = plan;
        return compactionPlan;
    }

    private BitSet populatedOrdinals(String type) {
        return readEngine.getTypeState(type).getListener(PopulatedOrdinalListener.class).getPopulatedOrdinals();
    }

    /**
     * The populated ordinals sitting at or above the cardinality watermark
     */
    private int numMisplacedOrdinals(BitSet populatedOrdinals) {
        int numMisplaced = 0;
        int ordinal = populatedOrdinals.nextSetBit(populatedOrdinals.cardinality());

        while(ordinal != -1) {
            numMisplaced++;
            ordinal = populatedOrdinals.nextSetBit(ordinal + 1);
        }

        return numMisplaced;
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

    /**
     * Narrow the compaction targets to the single type with the biggest hole footprint
     */
    private Set<String> focusOnCostliestType(Set<String> compactionTargets) {
        String costliestType = null;
        long costliestHoleCostInBytes = -1;

        for(String type : compactionTargets) {
            long holeCostInBytes = readEngine.getTypeState(type).getApproximateHoleCostInBytes();
            if(holeCostInBytes > costliestHoleCostInBytes) {
                costliestHoleCostInBytes = holeCostInBytes;
                costliestType = type;
            }
        }

        return costliestType == null ? compactionTargets : Collections.singleton(costliestType);
    }

    /**
     * Determine how many of the highest ordinals in the given type may be relocated within the delta size budget
     */
    private int relocationsWithinDeltaBudget(String compactionTarget, BitSet populatedOrdinals, int numRelocations) {
        long deltaBytes = approximateDeltaBytes(compactionTarget, highestPopulatedOrdinals(populatedOrdinals, numRelocations));

        if(deltaBytes <= approxDeltaBytesPerCycle)
            return numRelocations;

        /// A record costs the delta its own bytes plus those of everything referencing it, so the per-record cost is
        /// uneven and the scaled estimate below cannot be read as evidence that even one record fits.  Price the
        /// smallest possible batch directly rather than inferring it from a batch size the projection happened to land on.
        long singleRecordBytes = approximateDeltaBytes(compactionTarget, highestPopulatedOrdinals(populatedOrdinals, 1));

        if(singleRecordBytes > approxDeltaBytesPerCycle) {
            log.warning("Relocating a single " + compactionTarget + " record is estimated to add " + singleRecordBytes
                    + " bytes to the delta, exceeding the configured approxDeltaBytesPerCycle of " + approxDeltaBytesPerCycle
                    + ".  " + compactionTarget + " will not be compacted; raise the budget to at least " + singleRecordBytes
                    + " bytes to reclaim its ordinal holes.");
            return 0;
        }

        return (int)Math.max(1, numRelocations * approxDeltaBytesPerCycle / deltaBytes);
    }

    private long approximateDeltaBytes(String compactionTarget, BitSet candidateRelocations) {
        Map<String, BitSet> churnedOrdinals = new HashMap<String, BitSet>();
        churnedOrdinals.put(compactionTarget, candidateRelocations);
        TransitiveSetTraverser.addReferencingOutsideClosure(readEngine, churnedOrdinals);

        long deltaBytes = 0;
        for(Map.Entry<String, BitSet> entry : churnedOrdinals.entrySet())
            deltaBytes += (long)entry.getValue().cardinality() * approximateRecordSizeInBytes(entry.getKey());

        return deltaBytes;
    }

    private long approximateRecordSizeInBytes(String type) {
        HollowTypeReadState typeState = readEngine.getTypeState(type);
        int numRecords = typeState.getPopulatedOrdinals().cardinality();

        return numRecords == 0 ? 0 : Math.max(1, typeState.getApproximateHeapFootprintInBytes() / numRecords);
    }

    private BitSet highestPopulatedOrdinals(BitSet populatedOrdinals, int numOrdinals) {
        BitSet highestOrdinals = new BitSet(populatedOrdinals.length());
        int ordinal = populatedOrdinals.length();

        for(int i=0;i<numOrdinals;i++) {
            while(!populatedOrdinals.get(--ordinal));
            highestOrdinals.set(ordinal);
        }

        return highestOrdinals;
    }

    private boolean isCompactionCandidate(String typeName) {
        HollowTypeReadState typeState = readEngine.getTypeState(typeName);
        BitSet populatedOrdinals = typeState.getListener(PopulatedOrdinalListener.class).getPopulatedOrdinals();
        double numOrdinals = populatedOrdinals.length();
        double numHoles = populatedOrdinals.length() - populatedOrdinals.cardinality();
        double holePercentage = numHoles / numOrdinals * 100d;
        long approximateHoleCostInBytes = typeState.getApproximateHoleCostInBytes();
        boolean isCompactionCandidate = holePercentage > (double)minCandidateHolePercentage && approximateHoleCostInBytes > minCandidateHoleCostInBytes;
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
            return readEngine.getTypesWithDefinedHashCodes().contains(((HollowMapSchema)schema).getKeyType());
        case SET:
            return readEngine.getTypesWithDefinedHashCodes().contains(((HollowSetSchema)schema).getElementType());
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
        private final long approxDeltaBytesPerCycle;

        /**
         * Create a new compaction.  Both of the criteria specified by the following parameters must be met in order for a type
         * to be considered a candidate for compaction.
         *
         * @param minCandidateHoleCostInBytes identify a type as a candidate for compaction only when the bytes used by ordinal holes exceeds this value
         * @param minCandidateHolePercentage identify a type as a candidate for compaction only when the percentage of space used by ordinal holes exceeds this value
         */
        public CompactionConfig(long minCandidateHoleCostInBytes, int minCandidateHolePercentage) {
            this(minCandidateHoleCostInBytes, minCandidateHolePercentage, Long.MAX_VALUE);
        }

        /**
         * Create a new compaction which converges gradually, rather than relocating every misplaced record at once.
         *
         * @param minCandidateHoleCostInBytes identify a type as a candidate for compaction only when the bytes used by ordinal holes exceeds this value
         * @param minCandidateHolePercentage identify a type as a candidate for compaction only when the percentage of space used by ordinal holes exceeds this value
         * @param approxDeltaBytesPerCycle approx ceiling on the size of delta
         */
        public CompactionConfig(long minCandidateHoleCostInBytes, int minCandidateHolePercentage, long approxDeltaBytesPerCycle) {
            this.minCandidateHoleCostInBytes = minCandidateHoleCostInBytes;
            this.minCandidateHolePercentage = minCandidateHolePercentage;
            this.approxDeltaBytesPerCycle = approxDeltaBytesPerCycle;
        }

        public long getMinCandidateHoleCostInBytes() {
            return minCandidateHoleCostInBytes;
        }

        public int getMinCandidateHolePercentage() {
            return minCandidateHolePercentage;
        }

        public long getApproxDeltaBytesPerCycle() {
            return approxDeltaBytesPerCycle;
        }
    }
}

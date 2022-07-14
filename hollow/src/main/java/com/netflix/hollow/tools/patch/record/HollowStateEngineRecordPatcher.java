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
package com.netflix.hollow.tools.patch.record;

import com.netflix.hollow.core.index.traversal.HollowIndexerValueTraverser;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.read.engine.PopulatedOrdinalListener;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.tools.combine.HollowCombiner;
import com.netflix.hollow.tools.combine.HollowCombinerCopyDirector;
import com.netflix.hollow.tools.traverse.TransitiveSetTraverser;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This tool can be used to create a state with most records coming from a "base" state, but selected records
 * coming from a "patch" state. 
 */
public class HollowStateEngineRecordPatcher {

    private final HollowReadStateEngine base;
    private final HollowReadStateEngine patchFrom;

    private final List<TypeMatchSpec> matchKeyPaths;

    private String ignoredTypes[] = new String[0];

    public HollowStateEngineRecordPatcher(HollowReadStateEngine base, HollowReadStateEngine patchFrom) {
        this(base, patchFrom, true);
    }

    public HollowStateEngineRecordPatcher(HollowReadStateEngine base, HollowReadStateEngine patchFrom, boolean removeDetachedTransitiveReferences) {
        this.base = base;
        this.patchFrom = patchFrom;
        this.matchKeyPaths = new ArrayList<TypeMatchSpec>();
    }

    public void addTypeMatchSpec(TypeMatchSpec matchSpec) {
        this.matchKeyPaths.add(matchSpec);
    }

    public void setIgnoredTypes(String... ignoredTypes) {
        this.ignoredTypes = ignoredTypes;
    }

    public HollowWriteStateEngine patch() {
        Map<String, BitSet> baseMatches = findMatches(base);

        TransitiveSetTraverser.addTransitiveMatches(base, baseMatches);
        TransitiveSetTraverser.removeReferencedOutsideClosure(base, baseMatches);

        Map<String, BitSet> patchFromMatches = findMatches(patchFrom);

        HollowCombinerCopyDirector combineDirector = new HollowPatcherCombinerCopyDirector(base, baseMatches, patchFrom, patchFromMatches);
        HollowCombiner combiner = new HollowCombiner(combineDirector, base, patchFrom);
        combiner.addIgnoredTypes(ignoredTypes);
        combiner.combine();
        return combiner.getCombinedStateEngine();
    }

    private Map<String, BitSet> findMatches(HollowReadStateEngine stateEngine) {
        Map<String, BitSet> matches = new HashMap<String, BitSet>();
        for(TypeMatchSpec spec : matchKeyPaths) {
            HollowTypeReadState typeState = stateEngine.getTypeState(spec.getTypeName());
            BitSet foundMatches = getOrCreateBitSet(matches, spec.getTypeName(), typeState.maxOrdinal());
            if(typeState != null) {
                BitSet ordinals = getPopulatedOrdinals(typeState);
                HollowIndexerValueTraverser traverser = new HollowIndexerValueTraverser(stateEngine, spec.getTypeName(), spec.getKeyPaths());

                int ordinal = ordinals.nextSetBit(0);
                while(ordinal != -1) {
                    traverser.traverse(ordinal);

                    for(int i = 0; i < traverser.getNumMatches(); i++) {

                        boolean foundMatch = false;

                        for(int j = 0; j < spec.getKeyMatchingValues().size(); j++) {
                            boolean matched = true;

                            for(int k = 0; k < traverser.getNumFieldPaths(); k++) {
                                if(!traverser.isMatchedValueEqual(i, k, spec.getKeyMatchingValues().get(j)[k])) {
                                    matched = false;
                                    break;
                                }
                            }

                            if(matched) {
                                foundMatch = true;
                                break;
                            }
                        }

                        if(foundMatch) {
                            foundMatches.set(ordinal);
                            break;
                        }
                    }

                    ordinal = ordinals.nextSetBit(ordinal + 1);
                }


                if(foundMatches.size() > 0)
                    matches.put(spec.getTypeName(), foundMatches);
            }
        }

        return matches;
    }

    private BitSet getOrCreateBitSet(Map<String, BitSet> bitSets, String typeName, int numBitsRequired) {
        if(numBitsRequired < 0)
            return new BitSet(0);

        BitSet bs = bitSets.get(typeName);
        if(bs == null) {
            bs = new BitSet(numBitsRequired);
            bitSets.put(typeName, bs);
        }
        return bs;
    }

    private BitSet getPopulatedOrdinals(HollowTypeReadState typeState) {
        return typeState.getListener(PopulatedOrdinalListener.class).getPopulatedOrdinals();
    }

}

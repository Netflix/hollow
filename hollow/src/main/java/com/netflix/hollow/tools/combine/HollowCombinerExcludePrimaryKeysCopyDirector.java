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
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.tools.traverse.TransitiveSetTraverser;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Specifies a set of exclusions for a {@link HollowCombiner}'s operation over one or more inputs.
 * <p>
 * Exclusions are specified based on record primary keys.
 * <p>
 * This is likely the most useful implementation of a {@link HollowCombinerCopyDirector}.
 * 
 * 
 * @author dkoszewnik
 *
 */
public class HollowCombinerExcludePrimaryKeysCopyDirector implements HollowCombinerCopyDirector {

    private final HollowCombinerCopyDirector baseDirector;
    private final Map<HollowTypeReadState, BitSet> excludedOrdinals;

    public HollowCombinerExcludePrimaryKeysCopyDirector() {
        this(HollowCombinerCopyDirector.DEFAULT_DIRECTOR);
    }

    /**
     * @param baseDirector if primary keys are not matched, delegate to the provided director for the answer to {@link #shouldCopy(HollowTypeReadState, int) }
     */
    public HollowCombinerExcludePrimaryKeysCopyDirector(HollowCombinerCopyDirector baseDirector) {
        this.excludedOrdinals = new HashMap<HollowTypeReadState, BitSet>();
        this.baseDirector = baseDirector;
    }

    /**
     * Exclude the record which matches the specified key.
     * 
     * @param idx the index in which to query for the key 
     * @param key the key
     */
    public void excludeKey(HollowPrimaryKeyIndex idx, Object... key) {
        int excludeOrdinal = idx.getMatchingOrdinal(key);

        if(excludeOrdinal >= 0) {
            BitSet excludedOrdinals = this.excludedOrdinals.get(idx.getTypeState());

            if(excludedOrdinals == null) {
                excludedOrdinals = new BitSet(idx.getTypeState().maxOrdinal() + 1);
                this.excludedOrdinals.put(idx.getTypeState(), excludedOrdinals);
            }

            excludedOrdinals.set(excludeOrdinal);
        }
    }

    /**
     * Exclude any objects which are referenced by excluded objects.
     */
    public void excludeReferencedObjects() {
        Set<HollowReadStateEngine> stateEngines = new HashSet<HollowReadStateEngine>();
        for(Map.Entry<HollowTypeReadState, BitSet> entry : excludedOrdinals.entrySet())
            stateEngines.add(entry.getKey().getStateEngine());

        for(HollowReadStateEngine stateEngine : stateEngines) {
            Map<String, BitSet> typeBitSetsForStateEngine = new HashMap<String, BitSet>();

            for(Map.Entry<HollowTypeReadState, BitSet> entry : excludedOrdinals.entrySet()) {
                if(entry.getKey().getStateEngine() == stateEngine) {
                    String type = entry.getKey().getSchema().getName();
                    typeBitSetsForStateEngine.put(type, BitSet.valueOf(entry.getValue().toLongArray()));
                }
            }

            TransitiveSetTraverser.addTransitiveMatches(stateEngine, typeBitSetsForStateEngine);

            for(Map.Entry<String, BitSet> entry : typeBitSetsForStateEngine.entrySet())
                excludedOrdinals.put(stateEngine.getTypeState(entry.getKey()), entry.getValue());
        }
    }

    @Override
    public boolean shouldCopy(HollowTypeReadState typeState, int ordinal) {
        BitSet bitSet = excludedOrdinals.get(typeState);
        if(bitSet != null && bitSet.get(ordinal))
            return false;
        return baseDirector.shouldCopy(typeState, ordinal);
    }

}

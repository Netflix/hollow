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

import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Used by the {@link HollowCombiner} to track the mapping between ordinals in the input state and ordinals in the output state.  Not intended for external consumption. 
 * 
 * @author dkoszewnik
 *
 */
public class HollowCombinerOrdinalRemapper implements OrdinalRemapper {

    private final HollowCombiner combiner;
    private final Map<String, int[]> typeMappings;

    public HollowCombinerOrdinalRemapper(HollowCombiner combiner, HollowReadStateEngine inputStateEngine) {
        this.combiner = combiner;
        this.typeMappings = initializeTypeMappings(inputStateEngine);
    }

    @Override
    public int getMappedOrdinal(String type, int originalOrdinal) {
        int typeMapping[] = typeMappings.get(type);

        if(typeMapping == null)
            return originalOrdinal;

        if(typeMapping[originalOrdinal] == -1)
            typeMapping[originalOrdinal] = combiner.copyOrdinal(type, originalOrdinal);

        return typeMapping[originalOrdinal];
    }

    @Override
    public void remapOrdinal(String type, int originalOrdinal, int mappedOrdinal) {
        typeMappings.get(type)[originalOrdinal] = mappedOrdinal;
    }

    @Override
    public boolean ordinalIsMapped(String type, int originalOrdinal) {
        return typeMappings.get(type)[originalOrdinal] != -1;
    }

    private Map<String, int[]> initializeTypeMappings(HollowReadStateEngine inputStateEngine) {
        Map<String, int[]> typeMappings = new HashMap<String, int[]>();
        for(HollowTypeReadState typeState : inputStateEngine.getTypeStates()) {
            int mapping[] = new int[typeState.maxOrdinal() + 1];
            Arrays.fill(mapping, -1);
            typeMappings.put(typeState.getSchema().getName(), mapping);
        }
        return typeMappings;
    }
}

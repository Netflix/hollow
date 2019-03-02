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

import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import java.util.BitSet;
import java.util.Map;

/**
 * An implementation of {@link HollowCombinerCopyDirector} which specifies the ordinals to <i>include</i> in the
 * copy operation for a single state engine.
 * 
 * @author dkoszewnik
 *
 */
public class HollowCombinerExcludeOrdinalsCopyDirector implements HollowCombinerCopyDirector {

    private final Map<String, BitSet> excludedOrdinals;

    public HollowCombinerExcludeOrdinalsCopyDirector(Map<String, BitSet> excludedOrdinals) {
        this.excludedOrdinals = excludedOrdinals;
    }

    @Override
    public boolean shouldCopy(HollowTypeReadState typeState, int ordinal) {
        BitSet typeExcludedOrdinals = excludedOrdinals.get(typeState.getSchema().getName());

        if(typeExcludedOrdinals == null)
            return true;

        return !typeExcludedOrdinals.get(ordinal);
    }

}

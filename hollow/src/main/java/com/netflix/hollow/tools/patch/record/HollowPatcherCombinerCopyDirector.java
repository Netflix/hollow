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

import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.tools.combine.HollowCombinerCopyDirector;
import java.util.BitSet;
import java.util.Map;

public class HollowPatcherCombinerCopyDirector implements HollowCombinerCopyDirector {

    private HollowReadStateEngine base;
    private HollowReadStateEngine patchFrom;

    private final Map<String, BitSet> baseMatchesClosure;
    private final Map<String, BitSet> patchFromMatchesClosure;

    public HollowPatcherCombinerCopyDirector(HollowReadStateEngine base, Map<String, BitSet> baseMatchesClosure, HollowReadStateEngine patchFrom, Map<String, BitSet> patchFromMatchesClosure) {
        this.base = base;
        this.patchFrom = patchFrom;
        this.baseMatchesClosure = baseMatchesClosure;
        this.patchFromMatchesClosure = patchFromMatchesClosure;
    }

    @Override
    public boolean shouldCopy(HollowTypeReadState typeState, int ordinal) {
        if(typeState.getStateEngine() == base) {
            BitSet bitSet = baseMatchesClosure.get(typeState.getSchema().getName());
            if(bitSet == null)
                return true;
            return !bitSet.get(ordinal);
        } else if(typeState.getStateEngine() == patchFrom) {
            BitSet bitSet = patchFromMatchesClosure.get(typeState.getSchema().getName());
            if(bitSet == null)
                return false;
            return bitSet.get(ordinal);
        }

        return false;
    }

}

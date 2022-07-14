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
import java.util.Map;

/**
 * Used by the {@link HollowCombiner} to deduplicate records in the output based on primary keys.  Not intended for external consumption.
 * 
 * @author dkoszewnik
 *
 */
class HollowCombinerPrimaryKeyOrdinalRemapper implements OrdinalRemapper {

    private final Map<String, HollowPrimaryKeyIndex[]> primaryKeyIndexes;
    private final OrdinalRemapper baseRemappers[];
    private final int stateEngineIdx;

    public HollowCombinerPrimaryKeyOrdinalRemapper(OrdinalRemapper[] baseRemappers, Map<String, HollowPrimaryKeyIndex[]> primaryKeyIndexes, int stateEngineIdx) {
        this.primaryKeyIndexes = primaryKeyIndexes;
        this.baseRemappers = baseRemappers;
        this.stateEngineIdx = stateEngineIdx;
    }

    @Override
    public int getMappedOrdinal(String type, int originalOrdinal) {
        return baseRemappers[stateEngineIdx].getMappedOrdinal(type, originalOrdinal);
    }

    @Override
    public void remapOrdinal(String type, int originalOrdinal, int mappedOrdinal) {
        baseRemappers[stateEngineIdx].remapOrdinal(type, originalOrdinal, mappedOrdinal);

        HollowPrimaryKeyIndex[] typeKeyIndexes = this.primaryKeyIndexes.get(type);
        if(typeKeyIndexes != null) {
            Object primaryKey[] = typeKeyIndexes[stateEngineIdx].getRecordKey(originalOrdinal);

            for(int i = 0; i < baseRemappers.length; i++) {
                if(i != stateEngineIdx) {
                    if(typeKeyIndexes[i] != null) {
                        int matchOrdinal = typeKeyIndexes[i].getMatchingOrdinal(primaryKey);
                        if(matchOrdinal != -1) {
                            baseRemappers[i].remapOrdinal(type, matchOrdinal, mappedOrdinal);
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean ordinalIsMapped(String type, int originalOrdinal) {
        return baseRemappers[stateEngineIdx].ordinalIsMapped(type, originalOrdinal);
    }


}

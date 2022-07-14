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

import com.netflix.hollow.core.util.IntMap;
import com.netflix.hollow.tools.combine.OrdinalRemapper;
import com.netflix.hollow.tools.compact.HollowCompactor;
import java.util.HashMap;
import java.util.Map;

/**
 * An {@link OrdinalRemapper} used by the {@link HollowCompactor} and {@link HollowStateDeltaPatcher}.
 * 
 * Not intended for external consumption.
 */
public class PartialOrdinalRemapper implements OrdinalRemapper {

    private final Map<String, IntMap> ordinalMappings;

    public PartialOrdinalRemapper() {
        this.ordinalMappings = new HashMap<String, IntMap>();
    }

    public void addOrdinalRemapping(String typeName, IntMap mapping) {
        ordinalMappings.put(typeName, mapping);
    }

    public IntMap getOrdinalRemapping(String typeName) {
        return ordinalMappings.get(typeName);
    }

    @Override
    public int getMappedOrdinal(String type, int originalOrdinal) {
        IntMap mapping = ordinalMappings.get(type);
        if(mapping != null) {
            int remappedOrdinal = mapping.get(originalOrdinal);
            if(remappedOrdinal != -1)
                return remappedOrdinal;
        }
        return originalOrdinal;
    }

    @Override
    public boolean ordinalIsMapped(String type, int originalOrdinal) {
        return true;
    }

    @Override
    public void remapOrdinal(String type, int originalOrdinal, int mappedOrdinal) {
        throw new UnsupportedOperationException("Cannot explicitly remap an ordinal in an IntMapOrdinalRemapper");
    }


}

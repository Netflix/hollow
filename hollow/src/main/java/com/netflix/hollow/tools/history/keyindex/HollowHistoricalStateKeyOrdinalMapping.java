/*
 *
 *  Copyright 2016 Netflix, Inc.
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
package com.netflix.hollow.tools.history.keyindex;

import com.netflix.hollow.tools.combine.OrdinalRemapper;
import java.util.HashMap;
import java.util.Map;

public class HollowHistoricalStateKeyOrdinalMapping {

    private final Map<String, HollowHistoricalStateTypeKeyOrdinalMapping> typeMappings;

    public HollowHistoricalStateKeyOrdinalMapping(HollowHistoryKeyIndex keyIndex) {
        this.typeMappings = new HashMap<String, HollowHistoricalStateTypeKeyOrdinalMapping>();

        for(Map.Entry<String, HollowHistoryTypeKeyIndex> entry : keyIndex.getTypeKeyIndexes().entrySet()) {
            typeMappings.put(entry.getKey(), new HollowHistoricalStateTypeKeyOrdinalMapping(entry.getKey(), entry.getValue()));
        }
    }

    private HollowHistoricalStateKeyOrdinalMapping(Map<String, HollowHistoricalStateTypeKeyOrdinalMapping> typeMappings) {
        this.typeMappings = typeMappings;
    }

    public HollowHistoricalStateKeyOrdinalMapping remap(OrdinalRemapper remapper) {
        Map<String, HollowHistoricalStateTypeKeyOrdinalMapping> typeMappings = new HashMap<String, HollowHistoricalStateTypeKeyOrdinalMapping>();

        for(Map.Entry<String, HollowHistoricalStateTypeKeyOrdinalMapping> entry : this.typeMappings.entrySet()) {
            typeMappings.put(entry.getKey(), entry.getValue().remap(remapper));
        }

        return new HollowHistoricalStateKeyOrdinalMapping(typeMappings);
    }

    public HollowHistoricalStateTypeKeyOrdinalMapping getTypeMapping(String typeName) {
        return typeMappings.get(typeName);
    }

    public Map<String, HollowHistoricalStateTypeKeyOrdinalMapping> getTypeMappings() {
        return typeMappings;
    }

}

/*
 *  Copyright 2021 Netflix, Inc.
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
package com.netflix.hollow.api.perfapi;

import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.core.HollowDataset;
import com.netflix.hollow.core.read.dataaccess.HollowDataAccess;
import com.netflix.hollow.core.schema.HollowSchema;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HollowPerformanceAPI extends HollowAPI {

    protected final PerfAPITypeIdentifiers types;

    public HollowPerformanceAPI(HollowDataAccess dataAccess) {
        super(dataAccess);

        this.types = new PerfAPITypeIdentifiers(dataAccess);
    }

    public PerfAPITypeIdentifiers getTypeIdentifiers() {
        return types;
    };

    public static class PerfAPITypeIdentifiers {
        private final String[] typeNames;
        private final Map<String, Integer> typeIdxMap;

        public PerfAPITypeIdentifiers(HollowDataset dataset) {
            List<HollowSchema> schemas = dataset.getSchemas();

            this.typeIdxMap = new HashMap<>();
            String[] typeNames = new String[schemas.size()];
            for (int i = 0; i < schemas.size(); i++) {
                typeNames[i] = schemas.get(i).getName();
                typeIdxMap.put(typeNames[i], i);
            }

            this.typeNames = typeNames;
        }

        public int getIdx(String typeName) {
            Integer idx = typeIdxMap.get(typeName);
            if (idx == null) {
                return Ref.TYPE_ABSENT;
            }

            return idx;
        }

        public String getTypeName(int idx) {
            if (idx >= 0 && idx < typeNames.length) {
                return typeNames[idx];
            }
            return "INVALID (" + idx + ")";
        }
    }

}

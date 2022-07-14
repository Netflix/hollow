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
package com.netflix.hollow.diffview.effigy.pairer.exact;

import com.netflix.hollow.core.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.tools.diff.exact.DiffEqualOrdinalMap;
import com.netflix.hollow.tools.diff.exact.DiffEqualOrdinalMap.MatchIterator;
import com.netflix.hollow.tools.diff.exact.DiffEqualityMapping;

public class DiffExactRecordMatcher implements ExactRecordMatcher {

    private final DiffEqualityMapping equalityMapping;

    public DiffExactRecordMatcher(DiffEqualityMapping equalityMapping) {
        this.equalityMapping = equalityMapping;
    }

    @Override
    public boolean isExactMatch(HollowTypeDataAccess fromType, int fromOrdinal, HollowTypeDataAccess toType, int toOrdinal) {
        if(fromType == null || toType == null)
            return false;

        DiffEqualOrdinalMap typeMap = equalityMapping.getEqualOrdinalMap(fromType.getSchema().getName());
        if(typeMap != null) {
            MatchIterator matchingToOrdinals = typeMap.getEqualOrdinals(fromOrdinal);

            while(matchingToOrdinals.hasNext()) {
                if(toOrdinal == matchingToOrdinals.next())
                    return true;
            }
        }

        return false;
    }

}

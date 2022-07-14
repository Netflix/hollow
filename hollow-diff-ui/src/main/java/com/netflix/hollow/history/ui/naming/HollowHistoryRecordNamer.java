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
package com.netflix.hollow.history.ui.naming;

import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.tools.history.HollowHistoricalState;
import com.netflix.hollow.tools.history.keyindex.HollowHistoricalStateTypeKeyOrdinalMapping;

public class HollowHistoryRecordNamer {

    public static final HollowHistoryRecordNamer DEFAULT_RECORD_NAMER = new HollowHistoryRecordNamer();

    public String getRecordName(HollowHistoricalState historicalState, HollowHistoricalStateTypeKeyOrdinalMapping typeKeyMapping, int keyOrdinal, HollowObjectTypeDataAccess dataAccess, int recordOrdinal) {
        String recordName = getRecordName(dataAccess, recordOrdinal);

        if(recordName != null)
            return recordName;

        return typeKeyMapping.getKeyIndex().getKeyDisplayString(keyOrdinal);
    }

    public String getRecordName(HollowObjectTypeDataAccess dataAccess, int recordOrdinal) {
        return null;
    }

    public String getKeyFieldName(HollowHistoricalState historicalState, Object o, int keyFieldIdx) {
        return String.valueOf(o);
    }

}

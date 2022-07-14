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
package com.netflix.hollow.history.ui.model;

import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.util.IntList;
import com.netflix.hollow.history.ui.HollowHistoryUI;
import com.netflix.hollow.history.ui.naming.HollowHistoryRecordNamer;
import com.netflix.hollow.tools.history.HollowHistoricalState;
import com.netflix.hollow.tools.history.keyindex.HollowHistoricalStateTypeKeyOrdinalMapping;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HistoryStateQueryMatches {

    private final long stateVersion;
    private final String dateDisplayString;
    private final List<TypeMatches> typeMatches;

    public HistoryStateQueryMatches(HollowHistoricalState historicalState, HollowHistoryUI ui, String dateDisplayString, Map<String, IntList> perTypeQueryMatchingKeys) {
        this.stateVersion = historicalState.getVersion();
        this.dateDisplayString = dateDisplayString;
        this.typeMatches = new ArrayList<TypeMatches>(historicalState.getKeyOrdinalMapping().getTypeMappings().size());

        for(Map.Entry<String, IntList> entry : perTypeQueryMatchingKeys.entrySet()) {
            HollowHistoryRecordNamer recordNamer = ui.getHistoryRecordNamer(entry.getKey());
            TypeMatches typeMatches = new TypeMatches(historicalState, recordNamer, entry.getKey(), entry.getValue());
            if(typeMatches.hasMatches())
                this.typeMatches.add(typeMatches);
        }
    }

    public boolean hasMatches() {
        return !typeMatches.isEmpty();
    }

    public long getStateVersion() {
        return stateVersion;
    }

    public String getDateDisplayString() {
        return dateDisplayString;
    }

    public List<TypeMatches> getTypeMatches() {
        return typeMatches;
    }

    public static class TypeMatches {

        private final String type;
        private final List<RecordDiff> modifiedRecords;
        private final List<RecordDiff> removedRecords;
        private final List<RecordDiff> addedRecords;

        public TypeMatches(HollowHistoricalState historicalState, HollowHistoryRecordNamer recordNamer, String type, IntList queryMatchingKeys) {
            this.type = type;
            this.modifiedRecords = new ArrayList<RecordDiff>();
            this.removedRecords = new ArrayList<RecordDiff>();
            this.addedRecords = new ArrayList<RecordDiff>();


            HollowHistoricalStateTypeKeyOrdinalMapping typeKeyMapping = historicalState.getKeyOrdinalMapping().getTypeMapping(type);
            if(typeKeyMapping == null) {
                return;
            }
            HollowObjectTypeDataAccess typeDataAccess = (HollowObjectTypeDataAccess) historicalState.getDataAccess().getTypeDataAccess(type);

            for(int i = 0; i < queryMatchingKeys.size(); i++) {
                int matchingKey = queryMatchingKeys.get(i);
                int removedOrdinal = typeKeyMapping.findRemovedOrdinal(matchingKey);
                int addedOrdinal = typeKeyMapping.findAddedOrdinal(matchingKey);

                if(removedOrdinal != -1 && addedOrdinal != -1) {
                    modifiedRecords.add(new RecordDiff(historicalState, recordNamer, typeKeyMapping, typeDataAccess, matchingKey, removedOrdinal, addedOrdinal));
                } else if(removedOrdinal != -1) {
                    removedRecords.add(new RecordDiff(historicalState, recordNamer, typeKeyMapping, typeDataAccess, matchingKey, removedOrdinal, addedOrdinal));
                } else if(addedOrdinal != -1) {
                    addedRecords.add(new RecordDiff(historicalState, recordNamer, typeKeyMapping, typeDataAccess, matchingKey, removedOrdinal, addedOrdinal));
                }
            }
        }

        public boolean hasMatches() {
            return !(modifiedRecords.isEmpty() && addedRecords.isEmpty() && removedRecords.isEmpty());
        }

        public String getType() {
            return type;
        }

        public List<RecordDiff> getModifiedRecords() {
            return modifiedRecords;
        }

        public List<RecordDiff> getAddedRecords() {
            return addedRecords;
        }

        public List<RecordDiff> getRemovedRecords() {
            return removedRecords;
        }
    }

}

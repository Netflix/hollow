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
import com.netflix.hollow.history.ui.naming.HollowHistoryRecordNamer;
import com.netflix.hollow.tools.history.HollowHistoricalState;
import com.netflix.hollow.tools.history.keyindex.HollowHistoricalStateTypeKeyOrdinalMapping;


public class RecordDiff implements Comparable<RecordDiff> {
    private final HollowHistoryRecordNamer recordNamer;
    private final HollowHistoricalState historicalState;
    private final HollowHistoricalStateTypeKeyOrdinalMapping typeKeyOrdinalMapping;
    private final HollowObjectTypeDataAccess typeDataAccess;
    private final int keyOrdinal;
    private final int fromOrdinal;
    private final int toOrdinal;

    public RecordDiff(HollowHistoricalState historicalState, HollowHistoryRecordNamer recordNamer, HollowHistoricalStateTypeKeyOrdinalMapping typeKeyOrdinalMapping, HollowObjectTypeDataAccess typeDataAccess, int keyOrdinal, int fromOrdinal, int toOrdinal) {
        this.historicalState = historicalState;
        this.recordNamer = recordNamer;
        this.typeKeyOrdinalMapping = typeKeyOrdinalMapping;
        this.typeDataAccess = typeDataAccess;
        this.keyOrdinal = keyOrdinal;
        this.fromOrdinal = fromOrdinal;
        this.toOrdinal = toOrdinal;
    }

    public int getKeyOrdinal() {
        return keyOrdinal;
    }

    public String getIdentifierString() {
        return recordNamer.getRecordName(historicalState, typeKeyOrdinalMapping, keyOrdinal, typeDataAccess, toOrdinal != -1 ? toOrdinal : fromOrdinal);
    }

    public int getFromOrdinal() {
        return fromOrdinal;
    }

    public int getToOrdinal() {
        return toOrdinal;
    }

    @Override
    public int compareTo(RecordDiff o) {
        return o.getIdentifierString().compareTo(getIdentifierString());
    }
}


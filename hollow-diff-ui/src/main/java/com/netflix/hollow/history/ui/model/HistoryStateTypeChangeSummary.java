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

import com.netflix.hollow.tools.history.keyindex.HollowHistoricalStateTypeKeyOrdinalMapping;

public class HistoryStateTypeChangeSummary {

    private final long stateVersion;
    private final String typeName;
    private final int modifications;
    private final int additions;
    private final int removals;

    public HistoryStateTypeChangeSummary(long stateVersion, String typeName, HollowHistoricalStateTypeKeyOrdinalMapping mapping) {
        this.stateVersion = stateVersion;
        this.typeName = typeName;
        this.modifications = mapping.getNumberOfModifiedRecords();
        this.additions = mapping.getNumberOfNewRecords();
        this.removals = mapping.getNumberOfRemovedRecords();
    }

    public long getVersion() {
        return stateVersion;
    }

    public String getTypeName() {
        return typeName;
    }

    public int getTotalChanges() {
        return modifications + additions + removals;
    }

    public int getModifications() {
        return modifications;
    }

    public int getAdditions() {
        return additions;
    }

    public int getRemovals() {
        return removals;
    }

    public boolean isEmpty() {
        return modifications == 0 && additions == 0 && removals == 0;
    }

}

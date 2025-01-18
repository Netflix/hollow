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

import com.netflix.hollow.history.ui.pages.HistoryOverviewPage.ChangeBreakdown;
import java.util.Map;

public class HistoryOverviewRow {

    private final String dateDisplayString;
    private final long version;
    private final Map<String, ChangeBreakdown> topLevelChangesByType;
    private final ChangeBreakdown topLevelChanges;
    private final String[] overviewDisplayHeaderValues;
    private final String reshardingInvocationHeader;

    public HistoryOverviewRow(String dateDisplayString, long version, ChangeBreakdown topLevelChanges,
                              Map<String, ChangeBreakdown> topLevelChangesByType, String[] overviewDisplayHeaderValues) {
        this(dateDisplayString, version, topLevelChanges, topLevelChangesByType, overviewDisplayHeaderValues, null);
    }

    public HistoryOverviewRow(String dateDisplayString, long version, ChangeBreakdown topLevelChanges,
                              Map<String, ChangeBreakdown> topLevelChangesByType, String[] overviewDisplayHeaderValues,
                              String reshardingInvocationHeader) {
        this.dateDisplayString = dateDisplayString;
        this.version = version;
        this.topLevelChanges = topLevelChanges;
        this.topLevelChangesByType = topLevelChangesByType;
        this.overviewDisplayHeaderValues = overviewDisplayHeaderValues;
        this.reshardingInvocationHeader = reshardingInvocationHeader;
    }

    public String getDateDisplayString() {
        return dateDisplayString;
    }

    public long getVersion() {
        return version;
    }

    public Map<String, ChangeBreakdown> getTopLevelChangesByType() {
        return topLevelChangesByType;
    }

    public ChangeBreakdown getTopLevelChanges() {
        return topLevelChanges;
    }

    public String[] getOverviewDisplayHeaderValues() {
        return overviewDisplayHeaderValues;
    }

    public String getReshardingInvocationHeader() {
        return reshardingInvocationHeader;
    }
}

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
package com.netflix.hollow.diffview;

import com.netflix.hollow.diffview.effigy.pairer.exact.ExactRecordMatcher;


public class HollowHistoryView extends HollowObjectView {

    private final long historicalVersion;
    private final String type;
    private final int keyOrdinal;
    private final long latestStateEngineRandomizedTag;

    public HollowHistoryView(long historicalVersion, String type, int keyOrdinal, long latestStateEngineRandomizedTag, HollowDiffViewRow rootRow, ExactRecordMatcher exactRecordMatcher) {
        super(rootRow, exactRecordMatcher);
        this.historicalVersion = historicalVersion;
        this.type = type;
        this.keyOrdinal = keyOrdinal;
        this.latestStateEngineRandomizedTag = latestStateEngineRandomizedTag;
    }

    public long getHistoricalVersion() {
        return historicalVersion;
    }

    public String getType() {
        return type;
    }

    public int getKeyOrdinal() {
        return keyOrdinal;
    }

    public long getLatestStateEngineRandomizedTag() {
        return latestStateEngineRandomizedTag;
    }

}

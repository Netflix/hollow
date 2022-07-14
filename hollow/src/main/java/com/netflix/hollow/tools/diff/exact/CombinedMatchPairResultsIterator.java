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
package com.netflix.hollow.tools.diff.exact;

import com.netflix.hollow.core.util.IntList;
import com.netflix.hollow.core.util.LongList;

/**
 * Not intended for external consumption.
 */
public class CombinedMatchPairResultsIterator {

    private final LongList[] shardedResults;

    private int currentShardList;
    private int currentShardListPosition;

    private int currentFromOrdinal;
    private final IntList list;

    public CombinedMatchPairResultsIterator(LongList[] shardedResults) {
        this.shardedResults = shardedResults;
        this.list = new IntList();
    }

    public boolean next() {
        list.clear();

        while(currentShardList < shardedResults.length) {
            if(currentShardListPosition < shardedResults[currentShardList].size()) {
                currentFromOrdinal = (int) (shardedResults[currentShardList].get(currentShardListPosition) >> 32);
                while(currentShardListPosition < shardedResults[currentShardList].size()
                        && (int) (shardedResults[currentShardList].get(currentShardListPosition) >> 32) == currentFromOrdinal) {
                    int toOrdinal = (int) shardedResults[currentShardList].get(currentShardListPosition);
                    list.add(toOrdinal);
                    currentShardListPosition++;
                }
                return true;
            }

            currentShardListPosition = 0;
            currentShardList++;
        }

        return false;
    }

    public int fromOrdinal() {
        return currentFromOrdinal;
    }

    public IntList toOrdinals() {
        return list;
    }

}

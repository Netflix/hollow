/*
 *  Copyright 2026 Netflix, Inc.
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
 */
package com.netflix.hollow.core.read.engine;

import static com.netflix.hollow.core.read.engine.HollowTypeReadStateShard.nextOrdinalInShard;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class HollowTypeReadStateShardTest {

    @Test
    public void nextOrdinalInShardMatchesLinearScan() {
        for(int numShards=1;numShards<=256;numShards<<=1) {
            for(int shardNumber=0;shardNumber<numShards;shardNumber++) {
                for(int ordinal=0;ordinal<1024;ordinal++) {
                    int expected = ordinal + 1;
                    while((expected & (numShards - 1)) != shardNumber)
                        expected++;
                    assertEquals(expected, nextOrdinalInShard(ordinal, shardNumber, numShards));
                }
            }
        }
    }
}

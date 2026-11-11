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
package com.netflix.hollow.core.index;

import static com.netflix.hollow.core.HollowConstants.INDEX_HASH_TABLE_MAX_SIZE;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class HollowHashIndexBuilderTest {

    @Test
    public void sizeBeforeGrowAppliesTheLoadFactor() {
        assertEquals(179, HollowHashIndexBuilder.sizeBeforeGrow(256));
        assertEquals(11744051, HollowHashIndexBuilder.sizeBeforeGrow(1 << 24));
    }

    /// an int multiply overflowed here, turning the threshold negative and forcing a grow on every insert until the
    /// table size itself overflowed into a negative allocation.
    @Test
    public void sizeBeforeGrowStaysPositiveWhereAnIntMultiplyWouldOverflow() {
        assertEquals(375809638, HollowHashIndexBuilder.sizeBeforeGrow(1L << 29));
        assertEquals(751619276, HollowHashIndexBuilder.sizeBeforeGrow(1L << 30));
        assertEquals(INDEX_HASH_TABLE_MAX_SIZE, HollowHashIndexBuilder.sizeBeforeGrow(1L << 31));
    }

    @Test
    public void grownTableSizeDoublesUpToTwoToThe31Buckets() {
        assertEquals(512, HollowHashIndexBuilder.grownTableSize(256));
        assertEquals(1L << 30, HollowHashIndexBuilder.grownTableSize(1L << 29));
        assertEquals(1L << 31, HollowHashIndexBuilder.grownTableSize(1L << 30));
    }

    @Test(expected = IllegalStateException.class)
    public void grownTableSizeRejectsATableThatCannotDouble() {
        HollowHashIndexBuilder.grownTableSize(1L << 31);
    }
}

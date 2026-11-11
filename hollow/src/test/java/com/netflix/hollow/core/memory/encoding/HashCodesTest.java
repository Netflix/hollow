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
package com.netflix.hollow.core.memory.encoding;

import static com.netflix.hollow.core.HollowConstants.HASH_TABLE_MAX_SIZE;
import static com.netflix.hollow.core.HollowConstants.INDEX_HASH_TABLE_MAX_SIZE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Random;
import org.junit.Test;

public class HashCodesTest {

    /// indexHashTableSize() is only safe to use in place of hashTableSize() if the two agree everywhere
    /// hashTableSize() is defined -- an index built with one bucket count and read with the other is corrupt.
    @Test
    public void indexHashTableSizeMatchesHashTableSizeWhereverBothApply() {
        for(int numElements : new int[] {0, 1, 2, 3, 4, 5, 7, 8, 9, 1023, 1024, 1 << 24, HASH_TABLE_MAX_SIZE})
            assertEquals("numElements=" + numElements, HashCodes.hashTableSize(numElements), HashCodes.indexHashTableSize(numElements));

        Random rand = new Random(20260902);
        for(int i=0;i<100000;i++) {
            int numElements = rand.nextInt(HASH_TABLE_MAX_SIZE);
            assertEquals("numElements=" + numElements, HashCodes.hashTableSize(numElements), HashCodes.indexHashTableSize(numElements));
        }
    }

    /// Note the load factor is approximate: rounding in "next power of two at or above numElements * 10 / 7" leaves a
    /// few inputs (numElements=3 among them) slightly above 70%.  That is inherited from hashTableSize and is harmless
    /// -- what open addressing actually requires is a power-of-two table strictly larger than the element count.
    @Test
    public void indexHashTableSizeReturnsAMinimalPowerOfTwoLargerThanTheElementCount() {
        for(long numElements : new long[] {3, 100, 1 << 24, HASH_TABLE_MAX_SIZE, HASH_TABLE_MAX_SIZE + 1L, INDEX_HASH_TABLE_MAX_SIZE}) {
            long size = HashCodes.indexHashTableSize(numElements);
            assertEquals("numElements=" + numElements + " size=" + size + " is not a power of two", 0, size & (size - 1));
            assertTrue("numElements=" + numElements + " does not fit in size=" + size, size > numElements);
            assertTrue("numElements=" + numElements + " leaves size=" + size + " oversized", (size / 2) * 7 / 10 < numElements);
        }
    }

    /// 2^31 buckets is the deliberate stopping point: the mask stays within 31 bits, so sign-extending an int hash
    /// code into a long mask is harmless.
    @Test
    public void indexHashTableSizeStopsAtTwoToThe31Buckets() {
        assertEquals(1L << 30, HashCodes.indexHashTableSize(HASH_TABLE_MAX_SIZE));
        assertEquals(1L << 31, HashCodes.indexHashTableSize(INDEX_HASH_TABLE_MAX_SIZE));
        assertEquals(INDEX_HASH_TABLE_MAX_SIZE, (1L << 31) * 7 / 10);
    }

    @Test(expected = IllegalArgumentException.class)
    public void indexHashTableSizeRejectsMoreElementsThanItCanHold() {
        HashCodes.indexHashTableSize(INDEX_HASH_TABLE_MAX_SIZE + 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void indexHashTableSizeRejectsNegativeSizes() {
        HashCodes.indexHashTableSize(-1);
    }
}

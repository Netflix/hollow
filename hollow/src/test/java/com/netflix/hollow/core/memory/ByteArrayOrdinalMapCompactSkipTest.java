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
 *
 */
package com.netflix.hollow.core.memory;

import static com.netflix.hollow.core.memory.ByteArrayOrdinalTest.createBuffer;
import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ByteArrayOrdinalMapCompactSkipTest {

    private boolean originalSkip;

    @Before
    public void setUp() {
        originalSkip = ByteArrayOrdinalMap.OPPORTUNISTIC_COMPACT;
        ByteArrayOrdinalMap.OPPORTUNISTIC_COMPACT = true;
        ByteArrayOrdinalMap.OPPORTUNISTIC_COMPACT_COUNT.set(0);
    }

    @After
    public void tearDown() {
        ByteArrayOrdinalMap.OPPORTUNISTIC_COMPACT = originalSkip;
    }

    private static ThreadSafeBitSet used(int... globalOrdinals) {
        ThreadSafeBitSet b = new ThreadSafeBitSet();
        for (int o : globalOrdinals) {
            b.set(o);
        }
        return b;
    }

    private static ByteArrayOrdinalMap mapOf(String... records) {
        ByteArrayOrdinalMap m = new ByteArrayOrdinalMap();
        for (String r : records) {
            m.getOrAssignOrdinal(createBuffer(r));
        }
        return m;
    }

    @Test
    public void skipsCompactWhenNothingFreed() {
        ByteArrayOrdinalMap m = new ByteArrayOrdinalMap();
        int a = m.getOrAssignOrdinal(createBuffer("A"));
        int b = m.getOrAssignOrdinal(createBuffer("B"));
        int c = m.getOrAssignOrdinal(createBuffer("C"));
        m.prepareForWrite();

        m.compact(used(a, b, c), 1, false, 0, 0);

        assertEquals(1, ByteArrayOrdinalMap.OPPORTUNISTIC_COMPACT_COUNT.get());
        assertEquals(a, m.get(createBuffer("A")));
        assertEquals(b, m.get(createBuffer("B")));
        assertEquals(c, m.get(createBuffer("C")));
    }

    @Test
    public void doesNotSkipWhenOrdinalFreed() {
        ByteArrayOrdinalMap m = new ByteArrayOrdinalMap();
        int a = m.getOrAssignOrdinal(createBuffer("A"));
        int b = m.getOrAssignOrdinal(createBuffer("B"));
        int c = m.getOrAssignOrdinal(createBuffer("C"));
        m.prepareForWrite();

        m.compact(used(a, c), 1, false, 0, 0); // drop B

        assertEquals(0, ByteArrayOrdinalMap.OPPORTUNISTIC_COMPACT_COUNT.get());
        assertEquals(a, m.get(createBuffer("A")));
        assertEquals(-1, m.get(createBuffer("B")));
        assertEquals(c, m.get(createBuffer("C")));
    }

    @Test
    public void skipDisabledFallsBackToFullCompact() {
        ByteArrayOrdinalMap.OPPORTUNISTIC_COMPACT = false;
        ByteArrayOrdinalMap m = new ByteArrayOrdinalMap();
        int a = m.getOrAssignOrdinal(createBuffer("A"));
        int b = m.getOrAssignOrdinal(createBuffer("B"));
        m.prepareForWrite();

        m.compact(used(a, b), 1, false, 0, 0);

        assertEquals(0, ByteArrayOrdinalMap.OPPORTUNISTIC_COMPACT_COUNT.get());
        assertEquals(a, m.get(createBuffer("A")));
        assertEquals(b, m.get(createBuffer("B")));
    }

    @Test
    public void fastAndFullPathProduceIdenticalLayout() {
        ByteArrayOrdinalMap fast = mapOf("A", "B", "C");
        ByteArrayOrdinalMap full = mapOf("A", "B", "C");
        int a = fast.get(createBuffer("A"));
        int b = fast.get(createBuffer("B"));
        int c = fast.get(createBuffer("C"));
        fast.prepareForWrite();
        full.prepareForWrite();

        ByteArrayOrdinalMap.OPPORTUNISTIC_COMPACT = true;
        fast.compact(used(a, b, c), 1, false, 0, 0);
        ByteArrayOrdinalMap.OPPORTUNISTIC_COMPACT = false;
        full.compact(used(a, b, c), 1, false, 0, 0);

        fast.prepareForWrite();
        full.prepareForWrite();
        for (String s : new String[] {"A", "B", "C"}) {
            int ordFast = fast.get(createBuffer(s));
            int ordFull = full.get(createBuffer(s));
            assertEquals(ordFull, ordFast);
            assertEquals(full.getPointerForData(ordFull), fast.getPointerForData(ordFast));
        }
    }

    @Test
    public void detectsFreesWithPartitionedOrdinalMapIndexBits() {
        int mapIdx = 1;
        int mapIndexBits = 2; // one of 4 partitioned maps
        ByteArrayOrdinalMap m = new ByteArrayOrdinalMap();
        int a = m.getOrAssignOrdinal(createBuffer("A"));
        int b = m.getOrAssignOrdinal(createBuffer("B"));
        m.prepareForWrite();

        m.compact(used((a << mapIndexBits) | mapIdx, (b << mapIndexBits) | mapIdx), 1, false, mapIdx, mapIndexBits);
        assertEquals(1, ByteArrayOrdinalMap.OPPORTUNISTIC_COMPACT_COUNT.get());

        m.prepareForWrite();
        m.compact(used((a << mapIndexBits) | mapIdx), 1, false, mapIdx, mapIndexBits); // drop B
        assertEquals(1, ByteArrayOrdinalMap.OPPORTUNISTIC_COMPACT_COUNT.get()); // full path, unchanged
        assertEquals(a, m.get(createBuffer("A")));
        assertEquals(-1, m.get(createBuffer("B")));
    }

    @Test
    public void reclaimedOrdinalReusedThenSkip() {
        ByteArrayOrdinalMap m = new ByteArrayOrdinalMap();
        int a = m.getOrAssignOrdinal(createBuffer("A"));
        int b = m.getOrAssignOrdinal(createBuffer("B"));
        m.prepareForWrite();

        m.compact(used(a), 1, false, 0, 0); // free B via full compact
        assertEquals(0, ByteArrayOrdinalMap.OPPORTUNISTIC_COMPACT_COUNT.get());
        assertEquals(-1, m.get(createBuffer("B")));

        int d = m.getOrAssignOrdinal(createBuffer("D")); // reuses freed ordinal
        assertEquals(b, d);
        m.prepareForWrite();

        m.compact(used(a, d), 1, false, 0, 0); // nothing freed -> skip
        assertEquals(1, ByteArrayOrdinalMap.OPPORTUNISTIC_COMPACT_COUNT.get());
        assertEquals(a, m.get(createBuffer("A")));
        assertEquals(d, m.get(createBuffer("D")));
        assertEquals(-1, m.get(createBuffer("B")));
    }

    @Test
    public void getterReflectsOpportunisticCompactCount() {
        assertEquals(0, ByteArrayOrdinalMap.getOpportunisticCompactCount());
        ByteArrayOrdinalMap m = new ByteArrayOrdinalMap();
        int a = m.getOrAssignOrdinal(createBuffer("A"));
        int b = m.getOrAssignOrdinal(createBuffer("B"));
        m.prepareForWrite();

        m.compact(used(a, b), 1, false, 0, 0); // nothing freed -> fast path

        assertEquals(1, ByteArrayOrdinalMap.getOpportunisticCompactCount());
        assertEquals(ByteArrayOrdinalMap.OPPORTUNISTIC_COMPACT_COUNT.get(),
                ByteArrayOrdinalMap.getOpportunisticCompactCount());
    }
}

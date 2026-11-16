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
package com.netflix.hollow.api.client.longevity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

/** The table behind direct lookup; held versions read through it in HeldDirectLookupTest. */
public class DirectLookupTest {

    private final DirectLookup.TypeLookup lookup = new DirectLookup.TypeLookup(null);

    @Test
    public void aVersionReadsTheFirstCopyAfterIt() {
        add(5, 2, 4, 7);

        assertCopiedAt(5, 0, 2);
        assertCopiedAt(5, 1, 2);
        assertCopiedAt(5, 2, 4);
        assertCopiedAt(5, 6, 7);
        assertNull(lookup.find(5, 7)); // the engine has it
        assertNull(lookup.find(6, 0));
    }

    @Test
    public void anEntryIsFoundOnlyOnceItsOrdinalIsPublished() {
        lookup.add(new DirectLookup.Entry(3, 1, null, 0));
        assertNull(lookup.find(3, 0));

        lookup.publish(3);

        assertCopiedAt(3, 0, 1);
    }

    @Test
    public void pruningDropsEntriesThroughADeltaAndThenTheBit() {
        add(5, 2, 4);

        lookup.dropThrough(5, 2);
        assertCopiedAt(5, 0, 4); // what an older hold would have read is gone
        assertCopiedAt(5, 3, 4);
        assertEquals(1, lookup.size());

        lookup.dropThrough(5, 4);
        assertNull(lookup.find(5, 0));
        assertEquals(0, lookup.size());

        lookup.add(new DirectLookup.Entry(5, 9, null, 0)); // the ordinal again, removed by a later delta
        assertNull(lookup.find(5, 0)); // its bit was cleared
        lookup.publish(5);
        assertCopiedAt(5, 0, 9);
    }

    @Test
    public void entriesSurviveGrowthAndReusedSlots() {
        for (int ordinal = 0; ordinal < 1000; ordinal++)
            add(ordinal, 1);
        for (int ordinal = 0; ordinal < 1000; ordinal += 2)
            lookup.dropThrough(ordinal, 1);
        for (int ordinal = 0; ordinal < 1000; ordinal++)
            add(ordinal, 2);

        for (int ordinal = 0; ordinal < 1000; ordinal++) {
            assertCopiedAt(ordinal, 0, ordinal % 2 == 0 ? 2 : 1);
            assertCopiedAt(ordinal, 1, 2);
        }
        assertEquals(1000, lookup.size());
        for (int ordinal = 0; ordinal < 1000; ordinal++)
            lookup.dropThrough(ordinal, 2);
        assertEquals(0, lookup.size());
        for (int ordinal = 0; ordinal < 1000; ordinal++)
            assertNull(lookup.find(ordinal, 0));
    }

    private void add(int ordinal, long... positions) {
        for (long position : positions)
            lookup.add(new DirectLookup.Entry(ordinal, position, null, (int) position));
        lookup.publish(ordinal);
    }

    private void assertCopiedAt(int ordinal, long heldAt, long copiedAt) {
        DirectLookup.Entry entry = lookup.find(ordinal, heldAt);
        assertEquals(ordinal + " held at " + heldAt, copiedAt, entry == null ? -1 : entry.position);
        assertEquals(ordinal, entry.ordinal);
    }
}

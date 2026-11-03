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
package com.netflix.hollow.api.consumer.longevity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.netflix.hollow.api.consumer.HollowConsumer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Test;

public class HollowVersionDiffTest {

    private final HeldVersionsFixture fixture = new HeldVersionsFixture();

    @Test
    public void everyPairOfHoldsDiffsExactlyAcrossForwardAndReverseDeltas() {
        List<Long> versions = fixture.cycles(10);
        HollowConsumer consumer = fixture.heldConsumer();
        List<Long> applied = new ArrayList<>(); // the engine's version at each hold
        List<HollowVersionHold> holds = new ArrayList<>();
        for (int i : HeldVersionsFixture.forwardBackForward(versions.size(), 3)) {
            consumer.triggerRefreshTo(versions.get(i));
            applied.add(versions.get(i));
            holds.add(consumer.holdVersion(versions.get(i)));
        }

        for (int from = 0; from < holds.size(); from++) {
            for (int to = 0; to < holds.size(); to++)
                fixture.assertDiff(between(applied, from, to), holds.get(to).diffFrom(holds.get(from)));
        }
        assertTrue(holds.get(1).diffFrom(holds.get(0)).getRemovedOrdinals("NoSuchType").isEmpty());
    }

    @Test
    public void holdsOnDifferentStateEnginesDiffNonIncrementally() {
        List<Long> versions = fixture.cycles(3);
        HollowConsumer consumer = fixture.heldConsumer();
        consumer.triggerRefreshTo(versions.get(0));
        HollowVersionHold before = consumer.holdVersion(versions.get(0));
        consumer.forceDoubleSnapshotNextUpdate();
        consumer.triggerRefreshTo(versions.get(2));
        HollowVersionHold after = consumer.holdVersion(versions.get(2));

        HollowVersionDiff diff = after.diffFrom(before);

        assertEquals((long) versions.get(0), diff.getFromVersion());
        assertEquals((long) versions.get(2), diff.getToVersion());
        assertFalse(diff.isIncremental());
        assertEquals(-1, diff.getDeltaCount());
        try {
            diff.getRemovedOrdinals("Movie");
            fail();
        } catch (IllegalStateException expected) {
        }
        fixture.assertPopulated(before); // what a rebuild reads instead
        fixture.assertPopulated(after);
    }

    @Test
    public void aClosedHoldHasNoDiffs() {
        List<Long> versions = fixture.cycles(2);
        HollowConsumer consumer = fixture.heldConsumer();
        consumer.triggerRefreshTo(versions.get(0));
        HollowVersionHold served = consumer.holdVersion(versions.get(0));
        HollowVersionHold next = consumer.holdVersion(versions.get(1));

        served.close();

        assertDiffFails(next, served);
        assertDiffFails(served, next);
    }

    /** The versions applied from the hold at {@code from} to the one at {@code to}, in that direction. */
    private static List<Long> between(List<Long> applied, int from, int to) {
        List<Long> between = new ArrayList<>(applied.subList(Math.min(from, to), Math.max(from, to) + 1));
        if (from > to)
            Collections.reverse(between);
        return between;
    }

    private static void assertDiffFails(HollowVersionHold hold, HollowVersionHold older) {
        try {
            hold.diffFrom(older);
            fail();
        } catch (IllegalStateException expected) {
        }
    }
}

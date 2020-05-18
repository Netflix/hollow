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
package com.netflix.hollow.api.consumer;

import com.netflix.hollow.api.client.FailedTransitionTracker;
import com.netflix.hollow.api.client.HollowUpdatePlan;
import com.netflix.hollow.api.consumer.HollowConsumer.Blob;
import java.io.IOException;
import java.io.InputStream;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class FailedTransitionTrackerTest {

    private FailedTransitionTracker tracker;

    @Before
    public void setUp() {
        this.tracker = new FailedTransitionTracker();

        tracker.markFailedTransition(new FakeHollowBlob(1));

        HollowUpdatePlan plan = new HollowUpdatePlan();
        plan.add(new FakeHollowBlob(100, 101));
        plan.add(new FakeHollowBlob(101, 102));

        tracker.markAllTransitionsAsFailed(plan);
    }

    @After
    public void tearDown() {
        this.tracker.clear();
    }

    @Test
    public void testNoFailedTransitions() {
        HollowUpdatePlan plan = new HollowUpdatePlan();
        plan.add(new FakeHollowBlob(50));
        plan.add(new FakeHollowBlob(50, 51));
        plan.add(new FakeHollowBlob(51, 52));
        plan.add(new FakeHollowBlob(52, 53));

        Assert.assertFalse(tracker.anyTransitionWasFailed(plan));
    }

    @Test
    public void testFailedDeltaTransition() {
        HollowUpdatePlan plan = new HollowUpdatePlan();
        plan.add(new FakeHollowBlob(99));
        plan.add(new FakeHollowBlob(99, 100));
        plan.add(new FakeHollowBlob(100, 101));

        Assert.assertTrue(tracker.anyTransitionWasFailed(plan));
    }

    @Test
    public void testFailedSnapshotTransition() {
        HollowUpdatePlan plan = new HollowUpdatePlan();
        plan.add(new FakeHollowBlob(1));
        plan.add(new FakeHollowBlob(1, 2));
        plan.add(new FakeHollowBlob(2, 3));

        Assert.assertTrue(tracker.anyTransitionWasFailed(plan));
    }

    @Test
    public void testGetNumFailedSnapshotTransitions() {
        // setUp adds a single failed snapshot transition
        Assert.assertEquals(1, tracker.getNumFailedSnapshotTransitions());
    }

    @Test
    public void testGetNumFailedDeltaTransitions() {
        // setUp adds a two failed delta transitions
        Assert.assertEquals(2, tracker.getNumFailedDeltaTransitions());
    }

    static class FakeHollowBlob extends Blob {
        public FakeHollowBlob(long toVersion) {
            super(toVersion);
        }

        public FakeHollowBlob(long fromVersion, long toVersion) {
            super(fromVersion, toVersion);
        }

        public InputStream getInputStream() throws IOException {
            return null;
        }
    }
}

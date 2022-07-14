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

import com.netflix.hollow.api.client.HollowUpdatePlan;
import com.netflix.hollow.api.client.HollowUpdatePlanner;
import com.netflix.hollow.api.consumer.HollowConsumer.Blob;
import com.netflix.hollow.test.consumer.TestBlob;
import com.netflix.hollow.test.consumer.TestBlobRetriever;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class HollowUpdatePlannerTest {

    TestBlobRetriever mockTransitionCreator;
    HollowUpdatePlanner planner;

    @Before
    public void setUp() {
        mockTransitionCreator = new TestBlobRetriever();
        planner = new HollowUpdatePlanner(mockTransitionCreator, new HollowConsumer.DoubleSnapshotConfig() {
            @Override
            public int maxDeltasBeforeDoubleSnapshot() {
                return 3;
            }

            @Override
            public boolean allowDoubleSnapshot() {
                return true;
            }
        });
    }

    @Test
    public void initiallyIncludesSnapshot() throws Exception {
        addMockSnapshot(5, 1);
        addMockDelta(1, 2);
        addMockDelta(2, 3);
        addMockDelta(3, 4);
        addMockDelta(4, 5);
        addMockDelta(5, 6);

        HollowUpdatePlan plan = planner.planInitializingUpdate(5);

        Assert.assertEquals(plan.numTransitions(), 5);

        assertTransition(plan.getTransition(0), Long.MIN_VALUE, 1);
        assertTransition(plan.getTransition(1), 1, 2);
        assertTransition(plan.getTransition(2), 2, 3);
        assertTransition(plan.getTransition(3), 3, 4);
        assertTransition(plan.getTransition(4), 4, 5);
    }

    @Test
    public void followsDeltaAfterInstantiated() throws Exception {
        addMockSnapshot(5, 1);
        addMockDelta(1, 2);
        addMockDelta(2, 3);
        addMockDelta(3, 4);
        addMockDelta(4, 5);
        addMockDelta(5, 6);

        HollowUpdatePlan plan = planner.planUpdate(3, 5, true);

        Assert.assertEquals(plan.numTransitions(), 2);

        assertTransition(plan.getTransition(0), 3, 4);
        assertTransition(plan.getTransition(1), 4, 5);
    }

    @Test
    public void attemptsDoubleSnapshotIfDeltaChainIsOverThreshold() throws Exception {
        addMockDelta(1, 2);
        addMockDelta(2, 3);
        addMockDelta(3, 4);
        addMockSnapshot(6, 4);
        addMockDelta(4, 5);
        addMockDelta(5, 6);

        HollowUpdatePlan plan = planner.planUpdate(1, 6, true);

        Assert.assertEquals(plan.numTransitions(), 3);

        assertTransition(plan.getTransition(0), Long.MIN_VALUE, 4);
        assertTransition(plan.getTransition(1), 4, 5);
        assertTransition(plan.getTransition(2), 5, 6);
    }

    @Test
    public void doesNotattemptDoubleSnapshotIfNotAllowed() throws Exception {
        addMockDelta(1, 2);
        addMockDelta(2, 3);
        addMockDelta(3, 4);
        addMockSnapshot(6, 4);
        addMockDelta(4, 5);
        addMockDelta(5, 6);

        HollowUpdatePlan plan = planner.planUpdate(1, 6, false);

        Assert.assertEquals(plan.numTransitions(), 3);

        assertTransition(plan.getTransition(0), 1, 2);
        assertTransition(plan.getTransition(1), 2, 3);
        assertTransition(plan.getTransition(2), 3, 4);
    }


    @Test
    public void followsReverseDeltas() throws Exception {
        addMockReverseDelta(5, 4);
        addMockReverseDelta(4, 3);

        HollowUpdatePlan plan = planner.planUpdate(5, 3, true);

        Assert.assertEquals(plan.numTransitions(), 2);

        assertTransition(plan.getTransition(0), 5, 4);
        assertTransition(plan.getTransition(1), 4, 3);
    }

    @Test
    public void followsDoubleSnapshotForLongReverseDeltaChains() throws Exception {
        addMockReverseDelta(5, 4);
        addMockReverseDelta(4, 3);
        addMockReverseDelta(3, 2);
        addMockReverseDelta(2, 1);
        addMockSnapshot(1, 0);
        addMockDelta(0, 1);

        HollowUpdatePlan plan = planner.planUpdate(5, 1, true);

        Assert.assertEquals(plan.numTransitions(), 2);

        assertTransition(plan.getTransition(0), Long.MIN_VALUE, 0);
        assertTransition(plan.getTransition(1), 0, 1);
    }

    @Test
    public void doesNotFollowDoubleSnapshotForLongReverseDeltaChainIfNotAllowed() throws Exception {
        addMockReverseDelta(5, 4);
        addMockReverseDelta(4, 3);
        addMockReverseDelta(3, 2);
        addMockReverseDelta(2, 1);
        addMockSnapshot(1, 0);
        addMockDelta(0, 1);

        HollowUpdatePlan plan = planner.planUpdate(5, 1, false);

        Assert.assertEquals(plan.numTransitions(), 3);

        assertTransition(plan.getTransition(0), 5, 4);
        assertTransition(plan.getTransition(1), 4, 3);
        assertTransition(plan.getTransition(2), 3, 2);
    }


    @Test
    public void deltaChainStopsBeforeDesiredStateIfDesiredStateDoesNotExist() throws Exception {
        addMockSnapshot(5, 0);
        addMockDelta(0, 3);
        addMockDelta(3, 6);

        HollowUpdatePlan plan = planner.planInitializingUpdate(5);

        Assert.assertEquals(plan.numTransitions(), 2);

        assertTransition(plan.getTransition(0), Long.MIN_VALUE, 0);
        assertTransition(plan.getTransition(1), 0, 3);
    }

    @Test
    public void loadsSnapshotIfDeltaChainCannotReachDesiredStateButSnapshotPlanCan() throws Exception {
        addMockDelta(0, 1);
        addMockDelta(1, 2);
        addMockDelta(2, 3);
        addMockSnapshot(7, 6);
        addMockDelta(6, 7);

        HollowUpdatePlan plan = planner.planUpdate(0, 7, true);

        Assert.assertEquals(plan.numTransitions(), 2);

        Assert.assertTrue(plan.isSnapshotPlan());
        assertTransition(plan.getSnapshotTransition(), Long.MIN_VALUE, 6);
        assertTransition(plan.getTransition(1), 6, 7);
    }


    @Test
    public void doesNotPreferSnapshotUnlessItGetsToDesiredState() throws Exception {
        addMockDelta(0, 1);
        addMockDelta(1, 2);
        addMockSnapshot(7, 2);
        addMockDelta(2, 3);

        HollowUpdatePlan plan = planner.planUpdate(0, 7, true);

        Assert.assertEquals(plan.numTransitions(), 3);

        assertTransition(plan.getTransition(0), 0, 1);
        assertTransition(plan.getTransition(1), 1, 2);
        assertTransition(plan.getTransition(2), 2, 3);
    }


    @Test
    public void reverseDeltaChainStopsBeforeDesiredStateIfDesiredStateDoesNotExist() throws Exception {
        addMockReverseDelta(10, 8);
        addMockReverseDelta(8, 6);
        addMockReverseDelta(6, 3);

        HollowUpdatePlan plan = planner.planUpdate(10, 5, true);

        Assert.assertEquals(plan.numTransitions(), 3);

        assertTransition(plan.getTransition(0), 10, 8);
        assertTransition(plan.getTransition(1), 8, 6);
        assertTransition(plan.getTransition(2), 6, 3);
    }

    @Test
    public void unavailableDeltaUpdatesToLatest() throws Exception {
        addMockDelta(1, 2);
        addMockDelta(2, 3);

        HollowUpdatePlan plan = planner.planUpdate(1, 5, true);

        Assert.assertEquals(2, plan.numTransitions());

        assertTransition(plan.getTransition(0), 1, 2);
        assertTransition(plan.getTransition(1), 2, 3);
    }

    @Test
    public void unavailableReverseDeltaAttemptsEarlierSnapshot() throws Exception {
        addMockReverseDelta(5, 4);

        addMockSnapshot(3, 1);
        addMockDelta(1, 2);

        HollowUpdatePlan plan = planner.planUpdate(5, 3, true);

        Assert.assertEquals(2, plan.numTransitions());

        assertTransition(plan.getTransition(0), Long.MIN_VALUE, 1);
        assertTransition(plan.getTransition(1), 1, 2);
    }


    private void assertTransition(HollowConsumer.Blob transition, long expectedFrom, long expectedTo) {
        Assert.assertEquals(transition.getFromVersion(), expectedFrom);
        Assert.assertEquals(transition.getToVersion(), expectedTo);
    }


    private void addMockSnapshot(long desiredVersion, long actualVersion) {
        Blob result = new TestBlob(Long.MIN_VALUE, actualVersion);

        mockTransitionCreator.addSnapshot(desiredVersion, result);
    }

    private void addMockDelta(long fromVersion, long toVersion) {
        Blob result = new TestBlob(fromVersion, toVersion);

        mockTransitionCreator.addDelta(fromVersion, result);
    }

    private void addMockReverseDelta(long fromVersion, long toVersion) {
        Blob result = new TestBlob(fromVersion, toVersion);

        mockTransitionCreator.addReverseDelta(fromVersion, result);
    }

}

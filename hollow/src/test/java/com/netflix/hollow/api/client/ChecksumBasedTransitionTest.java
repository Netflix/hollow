package com.netflix.hollow.api.client;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.test.consumer.TestBlob;
import com.netflix.hollow.test.consumer.TestBlobRetriever;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for checksum-based transition fallback when deltas are unavailable.
 * Verifies that HollowUpdatePlanner falls back to snapshot transitions when
 * delta chain is broken or unavailable.
 */
public class ChecksumBasedTransitionTest {

    private TestBlobRetriever blobRetriever;
    private HollowUpdatePlanner planner;

    @Before
    public void setUp() {
        blobRetriever = new TestBlobRetriever();
        planner = new HollowUpdatePlanner(
            blobRetriever,
            HollowConsumer.DoubleSnapshotConfig.DEFAULT_CONFIG,
            HollowConsumer.UpdatePlanBlobVerifier.DEFAULT_INSTANCE
        );
    }

    /**
     * Test that when a delta in the chain is unavailable, the planner
     * falls back to using a snapshot transition to reach the desired version.
     *
     * Setup: v1 -> (missing delta) -> v2 -> v3, with snapshot at v3 available
     * Expected: Planner should use snapshot to v3 instead of trying delta chain
     */
    @Test
    public void testTransitionFallsBackWhenDeltaUnavailable() throws Exception {
        // Setup: Create delta chain with missing link
        // v1 -> (delta missing) -> v2
        // v2 -> v3 (delta exists)
        addDelta(2, 3);
        // Note: delta from v1->v2 is intentionally NOT added to simulate unavailable delta

        // Add snapshot at v3 for fallback
        addSnapshot(3, 3);

        // Try to plan update from v1 to v3
        // With allowSnapshot=true, should fall back to snapshot
        HollowUpdatePlan plan = planner.planUpdate(1, 3, true);

        // Should have created a snapshot-based plan since delta chain is broken
        Assert.assertNotNull("Plan should not be null", plan);
        Assert.assertTrue("Plan should use snapshot when delta unavailable",
            plan.isSnapshotPlan());
        Assert.assertEquals("Should reach desired version v3",
            3, plan.destinationVersion(1));
    }

    /**
     * Test that the planner prefers direct delta path when available,
     * but can also create valid plans when deltas exist.
     *
     * Setup: Complete delta chain v1 -> v2 -> v3
     * Expected: Planner should use delta transitions
     */
    @Test
    public void testTransitionPrefersDirectDeltaPath() throws Exception {
        // Setup: Create complete delta chain
        addDelta(1, 2);
        addDelta(2, 3);

        // Plan update from v1 to v3 without allowing snapshot
        HollowUpdatePlan plan = planner.planUpdate(1, 3, false);

        Assert.assertNotNull("Plan should not be null", plan);
        Assert.assertEquals("Should have 2 delta transitions", 2, plan.numTransitions());
        Assert.assertEquals("Should reach v3", 3, plan.destinationVersion(1));

        // Verify the plan uses deltas, not snapshot
        Assert.assertFalse("Plan should use deltas when available", plan.isSnapshotPlan());
    }

    /**
     * Test that when delta is unavailable and allowSnapshot=false,
     * the planner returns a plan that cannot reach the desired version.
     *
     * Setup: v1 -> (missing delta) -> v2, with snapshot at v2 available
     * Expected: With allowSnapshot=false, planner should not fall back to snapshot
     *           and plan should not reach desired version
     */
    @Test
    public void testTransitionFailsWhenDeltaUnavailableAndSnapshotDisallowed() throws Exception {
        // Setup: Delta from v1 to v2 is missing
        // Note: delta from v1->v2 is intentionally NOT added

        // Add snapshot at v2 (but it should not be used)
        addSnapshot(2, 2);

        // Try to plan update from v1 to v2 with allowSnapshot=false
        HollowUpdatePlan plan = planner.planUpdate(1, 2, false);

        // Should have created a plan, but it won't reach the desired version
        Assert.assertNotNull("Plan should not be null", plan);
        Assert.assertNotEquals("Plan should not reach desired version when delta unavailable and snapshot disallowed",
            2, plan.destinationVersion(1));
    }

    private void addSnapshot(long desiredVersion, long toVersion) {
        blobRetriever.addSnapshot(desiredVersion, new TestBlob(Long.MIN_VALUE, toVersion));
    }

    private void addDelta(long fromVersion, long toVersion) {
        blobRetriever.addDelta(fromVersion, new TestBlob(fromVersion, toVersion));
    }
}

package com.netflix.hollow.api.client;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.HollowConsumer.Blob.BlobType;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.*;

/**
 * Integration test for repair transition version jumping.
 * Tests the scenario where delta chain is broken (v100->v101 unavailable)
 * and we need to jump directly to v200 using a repair transition.
 */
public class RepairVersionJumpingIntegrationTest {
    private HollowConsumer.BlobRetriever retriever;
    private HollowUpdatePlanner planner;

    @Before
    public void setUp() {
        retriever = new TestBlobRetriever();
        planner = new HollowUpdatePlanner(retriever);
    }

    @Test
    public void testVersionJumpWhenDeltaChainBroken() {
        // Arrange: Consumer at v100, wants to reach v200, but v100->v101 delta is broken
        long currentVersion = 100L;
        long targetVersion = 200L;

        // Act: Plan repair transition to jump versions
        HollowUpdatePlan plan = planner.planUpdateWithRepairJump(currentVersion, targetVersion);

        // Assert: Should create single REPAIR transition from v100 to v200
        assertNotNull("Plan should not be null", plan);
        assertEquals("Should have exactly 1 transition", 1, plan.numTransitions());

        HollowConsumer.Blob transition = plan.getTransition(0);
        assertEquals("Should be REPAIR type", BlobType.REPAIR, transition.getBlobType());
        assertEquals("From version should be 100", 100L, transition.getFromVersion());
        assertEquals("To version should be 200", 200L, transition.getToVersion());
    }

    @Test
    public void testVersionJumpBackward() {
        // Arrange: Consumer at v200, wants to rollback to v100
        long currentVersion = 200L;
        long targetVersion = 100L;

        // Act: Plan repair transition to jump backward
        HollowUpdatePlan plan = planner.planUpdateWithRepairJump(currentVersion, targetVersion);

        // Assert: Should create single REPAIR transition from v200 to v100
        assertNotNull("Plan should not be null", plan);
        assertEquals("Should have exactly 1 transition", 1, plan.numTransitions());

        HollowConsumer.Blob transition = plan.getTransition(0);
        assertEquals("Should be REPAIR type", BlobType.REPAIR, transition.getBlobType());
        assertEquals("From version should be 200", 200L, transition.getFromVersion());
        assertEquals("To version should be 100", 100L, transition.getToVersion());
    }

    @Test
    public void testVersionJumpFallbackWhenSnapshotUnavailable() {
        // Arrange: No snapshot available at target version
        long currentVersion = 100L;
        long targetVersion = 300L; // No snapshot at v300

        // Act: Try to plan repair transition
        HollowUpdatePlan plan = planner.planUpdateWithRepairJump(currentVersion, targetVersion);

        // Assert: Should fall back to regular planning (which will fail gracefully)
        // In this test setup, regular planning returns an empty plan that can't reach v300
        assertNotNull("Plan should not be null", plan);
        // The plan might be empty or contain transitions that don't reach the target version
        // When no snapshot is available and delta chain is broken, it returns an empty delta plan
        assertTrue("Plan should be empty or not contain a REPAIR transition",
            plan.numTransitions() == 0 ||
            (plan.numTransitions() > 0 && !plan.getTransition(plan.numTransitions() - 1).isRepair()));
    }

    /**
     * Test blob retriever that simulates:
     * - Delta chain broken at v100 (no delta at v100)
     * - Snapshots available at v100 and v200
     * - No snapshot at v300
     */
    private static class TestBlobRetriever implements HollowConsumer.BlobRetriever {
        @Override
        public HollowConsumer.Blob retrieveSnapshotBlob(long desiredVersion) {
            if (desiredVersion == 100L || desiredVersion == 200L) {
                return new TestBlob(desiredVersion, desiredVersion, BlobType.SNAPSHOT);
            }
            return null; // No snapshot at v300
        }

        @Override
        public HollowConsumer.Blob retrieveDeltaBlob(long currentVersion) {
            // Delta chain broken at v100
            return null;
        }

        @Override
        public HollowConsumer.Blob retrieveReverseDeltaBlob(long currentVersion) {
            // No reverse deltas available
            return null;
        }
    }

    /**
     * Test blob with empty content (sufficient for planning tests)
     */
    private static class TestBlob extends HollowConsumer.Blob {
        TestBlob(long fromVersion, long toVersion, BlobType type) {
            super(fromVersion, toVersion, type);
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return new ByteArrayInputStream(new byte[0]);
        }
    }
}

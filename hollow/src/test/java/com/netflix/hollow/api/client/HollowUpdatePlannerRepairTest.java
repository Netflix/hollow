package com.netflix.hollow.api.client;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.HollowConsumer.Blob.BlobType;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class HollowUpdatePlannerRepairTest {

    private HollowConsumer.BlobRetriever retriever;
    private HollowUpdatePlanner planner;

    @Before
    public void setUp() {
        retriever = mock(HollowConsumer.BlobRetriever.class);
        planner = new HollowUpdatePlanner(retriever, HollowConsumer.DoubleSnapshotConfig.DEFAULT_CONFIG);
    }

    @Test
    public void testPlanWithRepairTransition() {
        // Arrange
        long currentVersion = 100L;
        long targetVersion = 101L;

        HollowConsumer.Blob deltaBlob = mockDeltaBlob(currentVersion, targetVersion);
        HollowConsumer.Blob snapshotBlob = mockSnapshotBlob(targetVersion);

        when(retriever.retrieveDeltaBlob(currentVersion)).thenReturn(deltaBlob);
        when(retriever.retrieveSnapshotBlob(targetVersion)).thenReturn(snapshotBlob);

        // Act
        HollowUpdatePlan plan = planner.planUpdateWithRepair(currentVersion, targetVersion);

        // Assert
        assertNotNull("Plan should not be null", plan);
        assertEquals("Plan should have 2 transitions", 2, plan.getTransitionSequence().size());
        assertEquals("First transition should be DELTA", BlobType.DELTA,
            plan.getTransitionSequence().get(0));
        assertEquals("Second transition should be REPAIR", BlobType.REPAIR,
            plan.getTransitionSequence().get(1));
    }

    @Test
    public void testPlanWithRepairTransitionWhenSnapshotUnavailable() {
        // Arrange
        long currentVersion = 100L;
        long targetVersion = 101L;

        HollowConsumer.Blob deltaBlob = mockDeltaBlob(currentVersion, targetVersion);

        when(retriever.retrieveDeltaBlob(currentVersion)).thenReturn(deltaBlob);
        when(retriever.retrieveSnapshotBlob(targetVersion)).thenReturn(null); // No snapshot available

        // Act
        HollowUpdatePlan plan = planner.planUpdateWithRepair(currentVersion, targetVersion);

        // Assert - should fall back to just delta plan
        assertNotNull("Plan should not be null", plan);
        assertEquals("Plan should have 1 transition when snapshot unavailable", 1, plan.getTransitionSequence().size());
        assertEquals("Only transition should be DELTA", BlobType.DELTA,
            plan.getTransitionSequence().get(0));
    }

    @Test
    public void testPlanWithRepairTransitionForSnapshot() {
        // Arrange - when doing snapshot from VERSION_NONE
        long currentVersion = -1L; // VERSION_NONE
        long targetVersion = 100L;

        HollowConsumer.Blob snapshotBlob = mockSnapshotBlob(targetVersion);

        when(retriever.retrieveSnapshotBlob(targetVersion)).thenReturn(snapshotBlob);

        // Act
        HollowUpdatePlan plan = planner.planUpdateWithRepair(currentVersion, targetVersion);

        // Assert - snapshot plan should have repair appended
        assertNotNull("Plan should not be null", plan);
        assertTrue("Plan should have at least 2 transitions", plan.getTransitionSequence().size() >= 2);
        assertEquals("First transition should be SNAPSHOT", BlobType.SNAPSHOT,
            plan.getTransitionSequence().get(0));
        assertEquals("Last transition should be REPAIR", BlobType.REPAIR,
            plan.getTransitionSequence().get(plan.getTransitionSequence().size() - 1));
    }

    @Test
    public void testRepairBlobHasCorrectBlobType() {
        // Arrange
        long currentVersion = 100L;
        long targetVersion = 101L;

        HollowConsumer.Blob deltaBlob = mockDeltaBlob(currentVersion, targetVersion);
        HollowConsumer.Blob snapshotBlob = mockSnapshotBlob(targetVersion);

        when(retriever.retrieveDeltaBlob(currentVersion)).thenReturn(deltaBlob);
        when(retriever.retrieveSnapshotBlob(targetVersion)).thenReturn(snapshotBlob);

        // Act
        HollowUpdatePlan plan = planner.planUpdateWithRepair(currentVersion, targetVersion);

        // Assert - verify the actual Blob objects have correct types
        assertNotNull("Plan should not be null", plan);
        assertEquals("Plan should have 2 transitions", 2, plan.numTransitions());

        // Get the actual blob objects
        HollowConsumer.Blob firstBlob = plan.getTransition(0);
        HollowConsumer.Blob secondBlob = plan.getTransition(1);

        assertEquals("First blob should be DELTA type", BlobType.DELTA, firstBlob.getBlobType());
        assertEquals("Second blob should be REPAIR type", BlobType.REPAIR, secondBlob.getBlobType());

        // Verify repair blob characteristics
        assertTrue("Repair blob should have fromVersion == toVersion",
            secondBlob.getFromVersion() == secondBlob.getToVersion());
        assertEquals("Repair blob should be at target version", targetVersion, secondBlob.getToVersion());
        assertTrue("Repair blob should report isRepair() as true", secondBlob.isRepair());
        assertFalse("Repair blob should not be a snapshot", secondBlob.isSnapshot());
        assertFalse("Repair blob should not be a delta", secondBlob.isDelta());
        assertFalse("Repair blob should not be a reverse delta", secondBlob.isReverseDelta());
    }

    private HollowConsumer.Blob mockDeltaBlob(long from, long to) {
        HollowConsumer.Blob blob = mock(HollowConsumer.Blob.class);
        when(blob.getFromVersion()).thenReturn(from);
        when(blob.getToVersion()).thenReturn(to);
        when(blob.getBlobType()).thenReturn(BlobType.DELTA);
        when(blob.isDelta()).thenReturn(true);
        when(blob.isSnapshot()).thenReturn(false);
        when(blob.isReverseDelta()).thenReturn(false);
        when(blob.isRepair()).thenReturn(false);
        return blob;
    }

    private HollowConsumer.Blob mockSnapshotBlob(long version) {
        HollowConsumer.Blob blob = mock(HollowConsumer.Blob.class);
        when(blob.getFromVersion()).thenReturn(-1L);
        when(blob.getToVersion()).thenReturn(version);
        when(blob.getBlobType()).thenReturn(BlobType.SNAPSHOT);
        when(blob.isDelta()).thenReturn(false);
        when(blob.isSnapshot()).thenReturn(true);
        when(blob.isReverseDelta()).thenReturn(false);
        when(blob.isRepair()).thenReturn(false);
        try {
            when(blob.getInputStream()).thenReturn(mock(InputStream.class));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return blob;
    }

    @Test
    public void testRepairTransitionForwardVersionJump() {
        // Arrange - Jump from v100 to v200 (delta chain broken at v101)
        long currentVersion = 100L;
        long targetVersion = 200L;

        // Delta at v100 is broken/unavailable
        when(retriever.retrieveDeltaBlob(currentVersion)).thenReturn(null);

        // Snapshot at v200 is available
        HollowConsumer.Blob snapshotBlob = mockSnapshotBlob(targetVersion);
        when(retriever.retrieveSnapshotBlob(targetVersion)).thenReturn(snapshotBlob);

        // Act - use the new planUpdateWithRepairJump method for version jumping
        HollowUpdatePlan plan = planner.planUpdateWithRepairJump(currentVersion, targetVersion);

        // Assert
        assertNotNull("Plan should not be null", plan);
        assertTrue("Plan should have at least 1 transition", plan.numTransitions() >= 1);

        // Should contain REPAIR transition with fromVersion=100, toVersion=200
        HollowConsumer.Blob repairBlob = plan.getRepairTransition();
        assertNotNull("Repair transition should exist", repairBlob);
        assertEquals("Repair fromVersion should be 100", currentVersion, repairBlob.getFromVersion());
        assertEquals("Repair toVersion should be 200", targetVersion, repairBlob.getToVersion());
        assertEquals("Should be REPAIR type", BlobType.REPAIR, repairBlob.getBlobType());
    }

    @Test
    public void testRepairTransitionReverseVersionJump() {
        // Arrange - Jump backward from v200 to v100 (reverse delta unavailable)
        long currentVersion = 200L;
        long targetVersion = 100L;

        // Reverse delta at v200 is unavailable
        when(retriever.retrieveReverseDeltaBlob(currentVersion)).thenReturn(null);

        // Snapshot at v100 is available
        HollowConsumer.Blob snapshotBlob = mockSnapshotBlob(targetVersion);
        when(retriever.retrieveSnapshotBlob(targetVersion)).thenReturn(snapshotBlob);

        // Act
        HollowUpdatePlan plan = planner.planUpdateWithRepairJump(currentVersion, targetVersion);

        // Assert
        assertNotNull("Plan should not be null", plan);
        assertTrue("Plan should have at least 1 transition", plan.numTransitions() >= 1);

        // Should contain REPAIR transition with fromVersion=200, toVersion=100
        HollowConsumer.Blob repairBlob = plan.getRepairTransition();
        assertNotNull("Repair transition should exist", repairBlob);
        assertEquals("Repair fromVersion should be 200", currentVersion, repairBlob.getFromVersion());
        assertEquals("Repair toVersion should be 100", targetVersion, repairBlob.getToVersion());
        assertEquals("Should be REPAIR type", BlobType.REPAIR, repairBlob.getBlobType());
    }

    @Test
    public void testRepairTransitionSameVersionBackwardCompatibility() {
        // Arrange - Same version repair (original behavior: fix corruption at v100)
        long currentVersion = 100L;
        long targetVersion = 100L;

        HollowConsumer.Blob snapshotBlob = mockSnapshotBlob(targetVersion);
        when(retriever.retrieveSnapshotBlob(targetVersion)).thenReturn(snapshotBlob);

        // Act
        HollowUpdatePlan plan = planner.planUpdateWithRepairJump(currentVersion, targetVersion);

        // Assert
        assertEquals("Should return DO_NOTHING for same version", HollowUpdatePlan.DO_NOTHING, plan);
    }
}

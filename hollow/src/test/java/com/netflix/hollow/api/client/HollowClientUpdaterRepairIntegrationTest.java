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
package com.netflix.hollow.api.client;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.HollowConsumer.Blob;
import com.netflix.hollow.api.consumer.HollowConsumer.Blob.BlobType;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.tools.checksum.HollowChecksum;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class HollowClientUpdaterRepairIntegrationTest {

    /**
     * This test verifies that the hasRepairTransition() and getRepairTransition()
     * methods work correctly on HollowUpdatePlan.
     */
    @Test
    public void testHollowUpdatePlanRepairMethods() {
        // Arrange: Create a plan with a repair transition
        HollowUpdatePlan plan = new HollowUpdatePlan();

        Blob repairBlob = mock(Blob.class);
        when(repairBlob.getBlobType()).thenReturn(BlobType.REPAIR);
        when(repairBlob.getToVersion()).thenReturn(100L);

        plan.add(repairBlob);

        // Act & Assert
        assertTrue("Plan should have repair transition", plan.hasRepairTransition());
        assertNotNull("Should return repair blob", plan.getRepairTransition());
        assertEquals("Should return correct repair blob", repairBlob, plan.getRepairTransition());
    }

    /**
     * This test verifies that hasRepairTransition() returns false when no repair exists.
     */
    @Test
    public void testHollowUpdatePlanWithoutRepair() {
        // Arrange: Create a plan with only delta transitions
        HollowUpdatePlan plan = new HollowUpdatePlan();

        Blob deltaBlob = mock(Blob.class);
        when(deltaBlob.getBlobType()).thenReturn(BlobType.DELTA);
        when(deltaBlob.getToVersion()).thenReturn(100L);

        plan.add(deltaBlob);

        // Act & Assert
        assertFalse("Plan should not have repair transition", plan.hasRepairTransition());
        assertNull("Should return null when no repair", plan.getRepairTransition());
    }

    /**
     * This test verifies the withAdditionalTransition() method works with repair blobs.
     */
    @Test
    public void testWithAdditionalRepairTransition() {
        // Arrange: Create base plan
        HollowUpdatePlan basePlan = new HollowUpdatePlan();

        Blob deltaBlob = mock(Blob.class);
        when(deltaBlob.getBlobType()).thenReturn(BlobType.DELTA);
        basePlan.add(deltaBlob);

        Blob repairBlob = mock(Blob.class);
        when(repairBlob.getBlobType()).thenReturn(BlobType.REPAIR);
        when(repairBlob.getToVersion()).thenReturn(100L);

        // Act: Add repair transition
        HollowUpdatePlan planWithRepair = basePlan.withAdditionalTransition(repairBlob);

        // Assert
        assertFalse("Base plan should not have repair", basePlan.hasRepairTransition());
        assertTrue("New plan should have repair", planWithRepair.hasRepairTransition());
        assertEquals("Should have 2 transitions", 2, planWithRepair.numTransitions());
    }
}

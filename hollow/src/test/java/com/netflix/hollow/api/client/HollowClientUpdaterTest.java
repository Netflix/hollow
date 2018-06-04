/*
 *
 *  Copyright 2018 Netflix, Inc.
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

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.core.HollowConstants;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import org.junit.Before;
import org.junit.Test;

public class HollowClientUpdaterTest {
    private HollowConsumer.DoubleSnapshotConfig mockDoubleSnapshotConfig;

    private HollowClientUpdater hollowClientUpdater;

    @Before
    public void setUp() {
        mockDoubleSnapshotConfig = mock(HollowConsumer.DoubleSnapshotConfig.class);

        hollowClientUpdater = new HollowClientUpdater(null, null, null, mockDoubleSnapshotConfig,
                null, null, null, null, null);
    }

    @Test
    public void testUpdateTo_noVersions() throws Throwable {
        when(mockDoubleSnapshotConfig.allowDoubleSnapshot()).thenReturn(false);

        assertTrue(hollowClientUpdater.updateTo(HollowConstants.VERSION_NONE));
        HollowReadStateEngine readStateEngine = hollowClientUpdater.getStateEngine();
        assertTrue("Should have no types", readStateEngine.getAllTypes().isEmpty());
        assertTrue("Should create snapshot plan next, even if double snapshot config disallows it",
                hollowClientUpdater.shouldCreateSnapshotPlan());
        assertTrue(hollowClientUpdater.updateTo(HollowConstants.VERSION_NONE));
        assertTrue("Should still have no types", readStateEngine.getAllTypes().isEmpty());
    }
}

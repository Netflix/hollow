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
import com.netflix.hollow.api.metrics.HollowConsumerMetrics;
import com.netflix.hollow.core.memory.MemoryMode;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class HollowClientUpdaterChecksumTest {

    @Test
    public void testChecksumValidatorCanBePassedToConstructor() {
        // Arrange
        ChecksumValidator checksumValidator = mock(ChecksumValidator.class);

        // Act - Create updater with ChecksumValidator parameter
        HollowClientUpdater updater = new HollowClientUpdater(
            mock(HollowConsumer.BlobRetriever.class),
            Collections.emptyList(),
            mock(HollowAPIFactory.class),
            mock(HollowConsumer.DoubleSnapshotConfig.class),
            null,
            MemoryMode.ON_HEAP,
            null,
            null,
            mock(HollowConsumerMetrics.class),
            null,
            checksumValidator,
            false,  // repairEnabled parameter
            HollowConsumer.UpdatePlanBlobVerifier.DEFAULT_INSTANCE
        );

        // Assert - If we get here, the constructor accepted the parameter
        assertNotNull("Updater should be created", updater);
    }
}

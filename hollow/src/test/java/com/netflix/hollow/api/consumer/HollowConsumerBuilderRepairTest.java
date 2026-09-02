package com.netflix.hollow.api.consumer;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class HollowConsumerBuilderRepairTest {

    @Test
    public void testWithRepairEnabledConfiguration() {
        // Act
        HollowConsumer.Builder builder = HollowConsumer
            .withBlobRetriever(mock(HollowConsumer.BlobRetriever.class))
            .withRepairEnabled(true);

        HollowConsumer consumer = builder.build();

        // Assert
        assertTrue("Repair should be enabled", consumer.isRepairEnabled());
    }

    @Test
    public void testWithChecksumValidationConfiguration() {
        HollowConsumer.Builder builder = HollowConsumer
            .withBlobRetriever(mock(HollowConsumer.BlobRetriever.class))
            .withChecksumValidation(true);

        HollowConsumer consumer = builder.build();

        assertTrue("Checksum validation should be enabled",
            consumer.isChecksumValidationEnabled());
    }

    @Test
    public void testDefaultRepairDisabled() {
        HollowConsumer consumer = HollowConsumer
            .withBlobRetriever(mock(HollowConsumer.BlobRetriever.class))
            .build();

        assertFalse("Repair should be disabled by default", consumer.isRepairEnabled());
    }
}

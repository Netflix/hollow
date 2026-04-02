package com.netflix.hollow.api.consumer;

import com.netflix.hollow.api.consumer.HollowConsumer.Blob;
import com.netflix.hollow.api.consumer.HollowConsumer.Blob.BlobType;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.*;

public class HollowConsumerBlobTypeTest {

    @Test
    public void testRepairBlobType() {
        // Arrange & Act
        BlobType repairType = BlobType.REPAIR;

        // Assert
        assertNotNull("REPAIR blob type should exist", repairType);
        assertEquals("REPAIR type name should be 'repair'", "repair", repairType.getType());
    }

    @Test
    public void testBlobIsRepairTrue() {
        // Arrange: Create repair blob (fromVersion == toVersion, has snapshot)
        Blob blob = new TestBlob(100L, 100L, BlobType.REPAIR);

        // Act & Assert
        assertTrue("Blob should be identified as REPAIR", blob.isRepair());
        assertFalse("Repair blob should not be snapshot", blob.isSnapshot());
        assertFalse("Repair blob should not be delta", blob.isDelta());
        assertFalse("Repair blob should not be reverse delta", blob.isReverseDelta());
    }

    @Test
    public void testBlobIsRepairFalseForOtherTypes() {
        assertFalse(new TestBlob(-1L, 100L, BlobType.SNAPSHOT).isRepair());
        assertFalse(new TestBlob(100L, 101L, BlobType.DELTA).isRepair());
        assertFalse(new TestBlob(101L, 100L, BlobType.REVERSE_DELTA).isRepair());
    }

    // Test helper
    private static class TestBlob extends Blob {
        private final BlobType type;

        TestBlob(long fromVersion, long toVersion, BlobType type) {
            super(fromVersion, toVersion);
            this.type = type;
        }

        @Override
        public BlobType getBlobType() {
            return type;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return new ByteArrayInputStream(new byte[0]);
        }
    }
}

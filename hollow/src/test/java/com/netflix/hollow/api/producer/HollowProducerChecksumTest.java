package com.netflix.hollow.api.producer;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.tools.checksum.HollowChecksum;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class HollowProducerChecksumTest {
    private HollowProducer.Announcer mockAnnouncer;
    private HollowProducer producer;

    @Before
    public void setUp() {
        mockAnnouncer = mock(HollowProducer.Announcer.class);
        producer = HollowProducer.withPublisher(mock(HollowProducer.Publisher.class))
                .withAnnouncer(mockAnnouncer)
                .build();
    }

    @Test
    public void testChecksumPublishedInAnnouncement() {
        // Arrange: produce data
        producer.runCycle(state -> {
            // Write test data
            state.add(new TestRecord(1, "value1"));
        });

        // Act: capture announcement
        ArgumentCaptor<Long> versionCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Map> metadataCaptor = ArgumentCaptor.forClass(Map.class);
        verify(mockAnnouncer).announce(versionCaptor.capture(), metadataCaptor.capture());

        // Assert: checksum in metadata
        Map<String, String> metadata = metadataCaptor.getValue();
        assertNotNull("Metadata should not be null", metadata);
        assertTrue("Metadata should contain checksum", metadata.containsKey("hollow.checksum"));

        String checksumStr = metadata.get("hollow.checksum");
        assertNotNull("Checksum should not be null", checksumStr);
        assertFalse("Checksum should not be empty", checksumStr.isEmpty());
    }

    @Test
    public void testChecksumChangesWhenDataChanges() {
        // First cycle
        producer.runCycle(state -> {
            state.add(new TestRecord(1, "value1"));
        });

        ArgumentCaptor<Map> metadataCaptor = ArgumentCaptor.forClass(Map.class);
        verify(mockAnnouncer, times(1)).announce(anyLong(), metadataCaptor.capture());
        @SuppressWarnings("unchecked")
        Map<String, String> metadata1 = metadataCaptor.getValue();
        String checksum1 = metadata1.get("hollow.checksum");

        // Verify checksum is a valid integer
        assertNotNull("Checksum should not be null", checksum1);
        try {
            Integer.parseInt(checksum1);
        } catch (NumberFormatException e) {
            fail("Checksum should be a valid integer, but was: " + checksum1);
        }

        // Second cycle with more data
        producer.runCycle(state -> {
            state.add(new TestRecord(1, "value1"));
            state.add(new TestRecord(2, "value2"));
            state.add(new TestRecord(3, "value3"));
        });

        verify(mockAnnouncer, atLeast(2)).announce(anyLong(), metadataCaptor.capture());
        List<Map> allMetadata = metadataCaptor.getAllValues();
        @SuppressWarnings("unchecked")
        Map<String, String> metadata2 = (Map<String, String>) allMetadata.get(allMetadata.size() - 1);
        String checksum2 = metadata2.get("hollow.checksum");

        // Verify second checksum is also valid
        assertNotNull("Second checksum should not be null", checksum2);
        try {
            Integer.parseInt(checksum2);
        } catch (NumberFormatException e) {
            fail("Second checksum should be a valid integer, but was: " + checksum2);
        }

        // Verify checksums differ when data changes
        assertNotEquals("Checksums should differ when data changes", checksum1, checksum2);
    }

    // Test helper class
    @SuppressWarnings("unused")
    private static class TestRecord {
        int id;
        String value;

        TestRecord(int id, String value) {
            this.id = id;
            this.value = value;
        }
    }
}

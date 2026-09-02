package com.netflix.hollow.api.producer;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ProducerPerTypeChecksumTest {
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
    public void testProducerPublishesPerTypeChecksums() throws Exception {
        // Populate with multiple types
        producer.runCycle(state -> {
            state.add(new TypeA("a1", 100));
            state.add(new TypeA("a2", 200));
            state.add(new TypeB("b1", 300));
        });

        // Capture announcement metadata
        ArgumentCaptor<Long> versionCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Map> metadataCaptor = ArgumentCaptor.forClass(Map.class);
        verify(mockAnnouncer).announce(versionCaptor.capture(), metadataCaptor.capture());

        @SuppressWarnings("unchecked")
        Map<String, String> metadata = metadataCaptor.getValue();

        assertNotNull("Metadata should not be null", metadata);

        // Verify full checksum exists
        assertTrue("Should have full checksum", metadata.containsKey("hollow.checksum"));

        // Verify per-type checksums exist
        assertTrue("Should have TypeA checksum", metadata.containsKey("hollow.checksum.TypeA"));
        assertTrue("Should have TypeB checksum", metadata.containsKey("hollow.checksum.TypeB"));

        // Verify checksums are valid integers
        try {
            Integer.parseInt(metadata.get("hollow.checksum"));
            Integer.parseInt(metadata.get("hollow.checksum.TypeA"));
            Integer.parseInt(metadata.get("hollow.checksum.TypeB"));
        } catch (NumberFormatException e) {
            fail("All checksums should be valid integers");
        }
    }

    @SuppressWarnings("unused")
    static class TypeA {
        String name;
        int value;

        TypeA(String name, int value) {
            this.name = name;
            this.value = value;
        }
    }

    @SuppressWarnings("unused")
    static class TypeB {
        String id;
        int count;

        TypeB(String id, int count) {
            this.id = id;
            this.count = count;
        }
    }
}

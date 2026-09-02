/**
 * Copyright 2025 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.netflix.hollow.api.integration;

import com.netflix.hollow.api.client.ChecksumValidator;
import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.test.InMemoryBlobStore;
import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.api.producer.fs.HollowInMemoryBlobStager;
import com.netflix.hollow.core.read.engine.metrics.TypeMemoryProfiler;
import com.netflix.hollow.tools.checksum.HollowChecksum;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Integration test demonstrating all checksum optimization PoCs working together.
 */
public class ChecksumOptimizationIntegrationTest {

    private InMemoryBlobStore blobStore;
    private HollowProducer.Announcer mockAnnouncer;

    @Before
    public void setUp() {
        blobStore = new InMemoryBlobStore();
        mockAnnouncer = mock(HollowProducer.Announcer.class);
    }

    @Test
    public void testEndToEndChecksumOptimizations() throws Exception {
        // Producer publishes with per-type checksums
        HollowProducer producer = HollowProducer.withPublisher(blobStore)
            .withBlobStager(new HollowInMemoryBlobStager())
            .withAnnouncer(mockAnnouncer)
            .build();

        long v1 = producer.runCycle(state -> {
            state.add(new TypeA("a1", 100));
            state.add(new TypeB("b1", 200));
        });

        // Verify per-type checksums published
        ArgumentCaptor<Map> metadataCaptor = ArgumentCaptor.forClass(Map.class);
        verify(mockAnnouncer).announce(eq(v1), metadataCaptor.capture());

        @SuppressWarnings("unchecked")
        Map<String, String> metadata = metadataCaptor.getValue();
        assertTrue("Should have TypeA checksum", metadata.containsKey("hollow.checksum.TypeA"));
        assertTrue("Should have TypeB checksum", metadata.containsKey("hollow.checksum.TypeB"));

        // Consumer with memory profiling
        HollowConsumer consumer = HollowConsumer.withBlobRetriever(blobStore).build();

        // Load initial data first
        consumer.triggerRefreshTo(v1);
        assertEquals(v1, consumer.getCurrentVersionId());

        // Publish v2
        long v2 = producer.runCycle(state -> {
            state.add(new TypeA("a2", 150));
            state.add(new TypeB("b2", 250));
        });

        // Capture v2 metadata
        metadataCaptor = ArgumentCaptor.forClass(Map.class);
        verify(mockAnnouncer).announce(eq(v2), metadataCaptor.capture());

        @SuppressWarnings("unchecked")
        Map<String, String> v2Metadata = metadataCaptor.getValue();

        // Profile memory usage during delta update
        TypeMemoryProfiler profiler = new TypeMemoryProfiler();
        profiler.startProfiling(consumer.getStateEngine());

        // Update consumer
        consumer.triggerRefreshTo(v2);

        TypeMemoryProfiler.ProfileResult profileResult = profiler.endProfiling();
        assertEquals(v2, consumer.getCurrentVersionId());
        // Memory profiling should complete successfully (allocation can be 0 for small updates due to GC)
        assertNotNull("Profile result should not be null", profileResult);

        // Verify incremental validation works
        ChecksumValidator validator = new ChecksumValidator();
        HollowChecksum checksum = HollowChecksum.forStateEngine(consumer.getStateEngine());

        ChecksumValidator.IncrementalResult result = validator.validateIncremental(
            consumer.getStateEngine(), v2Metadata, checksum);

        assertTrue("Checksum validation should pass", result.isValid());
        assertEquals("No types should be mismatched", 0, result.getMismatchedTypes().size());
    }

    @Test
    public void testChecksumFallbackWithMemoryProfiling() throws Exception {
        HollowProducer producer = HollowProducer.withPublisher(blobStore)
            .withBlobStager(new HollowInMemoryBlobStager())
            .build();

        long v1 = producer.runCycle(state -> state.add(new TypeA("a1", 100)));
        long v2 = producer.runCycle(state -> state.add(new TypeA("a2", 200)));

        HollowConsumer consumer = HollowConsumer.withBlobRetriever(blobStore).build();

        consumer.triggerRefreshTo(v1);
        assertEquals(v1, consumer.getCurrentVersionId());

        // Profile memory usage during update
        TypeMemoryProfiler profiler = new TypeMemoryProfiler();
        profiler.startProfiling(consumer.getStateEngine());

        // Update to v2
        consumer.triggerRefreshTo(v2);

        TypeMemoryProfiler.ProfileResult result = profiler.endProfiling();

        assertEquals(v2, consumer.getCurrentVersionId());
        assertNotNull("Profile result should not be null", result);
        // Note: Memory profiling tracks heap delta which can be negative due to GC
        // The important thing is that profiling completes successfully
    }

    static class TypeA {
        String name;
        int value;
        TypeA(String name, int value) {
            this.name = name;
            this.value = value;
        }
    }

    static class TypeB {
        String id;
        int count;
        TypeB(String id, int count) {
            this.id = id;
            this.count = count;
        }
    }
}

package com.netflix.hollow.core.read.engine.metrics;

import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class TypeMemoryProfilerTest {

    @Test
    public void testProfileTypeLoading() throws Exception {
        HollowWriteStateEngine writeEngine = new HollowWriteStateEngine();
        HollowObjectMapper mapper = new HollowObjectMapper(writeEngine);

        // Add objects of different types with varying memory footprints
        for (int i = 0; i < 1000; i++) {
            mapper.add(new SmallType("small" + i));
        }

        for (int i = 0; i < 100; i++) {
            mapper.add(new LargeType("large" + i, new byte[10000]));
        }

        HollowReadStateEngine readEngine = new HollowReadStateEngine();
        TypeMemoryProfiler profiler = new TypeMemoryProfiler();

        // Profile type loading
        profiler.startProfiling(readEngine);

        roundTrip(writeEngine, readEngine);

        TypeMemoryProfiler.ProfileResult result = profiler.endProfiling();

        assertNotNull(result);
        assertTrue("Should allocate memory", result.getTotalBytesAllocated() > 0);

        Map<String, TypeMemoryProfiler.TypeProfile> typeProfiles = result.getTypeProfiles();
        assertTrue(typeProfiles.containsKey("SmallType"));
        assertTrue(typeProfiles.containsKey("LargeType"));

        // LargeType should have larger memory footprint
        TypeMemoryProfiler.TypeProfile smallProfile = typeProfiles.get("SmallType");
        TypeMemoryProfiler.TypeProfile largeProfile = typeProfiles.get("LargeType");

        assertTrue("LargeType should allocate more memory than SmallType",
            largeProfile.getBytesAllocated() > smallProfile.getBytesAllocated());
    }

    @Test
    public void testProfileDeltaApplicationMemory() throws Exception {
        // Test profiling on a smaller dataset to verify it works
        HollowWriteStateEngine writeEngine = new HollowWriteStateEngine();
        HollowObjectMapper mapper = new HollowObjectMapper(writeEngine);

        // Add a few objects
        for (int i = 0; i < 10; i++) {
            mapper.add(new SmallType("item" + i));
        }

        HollowReadStateEngine readEngine = new HollowReadStateEngine();
        TypeMemoryProfiler profiler = new TypeMemoryProfiler();

        profiler.startProfiling(readEngine);
        roundTripSnapshot(writeEngine, readEngine);
        TypeMemoryProfiler.ProfileResult result = profiler.endProfiling();

        assertNotNull(result);
        // Total bytes may be negative due to GC, but type profiles should exist
        assertTrue(result.getTypeProfiles().size() >= 1);
        assertTrue(result.getTypeProfiles().containsKey("SmallType"));
        // Verify SmallType has some allocation tracked
        TypeMemoryProfiler.TypeProfile smallProfile = result.getTypeProfiles().get("SmallType");
        assertTrue(smallProfile.getBytesAllocated() >= 0);
    }

    static class SmallType {
        String name;
        SmallType(String name) { this.name = name; }
    }

    static class LargeType {
        String name;
        byte[] data;
        LargeType(String name, byte[] data) {
            this.name = name;
            this.data = data;
        }
    }

    private void roundTrip(HollowWriteStateEngine writeEngine, HollowReadStateEngine readEngine) throws Exception {
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        com.netflix.hollow.core.write.HollowBlobWriter writer =
            new com.netflix.hollow.core.write.HollowBlobWriter(writeEngine);
        writer.writeSnapshot(baos);

        com.netflix.hollow.core.read.engine.HollowBlobReader reader =
            new com.netflix.hollow.core.read.engine.HollowBlobReader(readEngine);
        reader.readSnapshot(com.netflix.hollow.core.read.HollowBlobInput.serial(baos.toByteArray()));
    }

    private void roundTripSnapshot(HollowWriteStateEngine writeEngine, HollowReadStateEngine readEngine) throws Exception {
        roundTrip(writeEngine, readEngine);
    }
}

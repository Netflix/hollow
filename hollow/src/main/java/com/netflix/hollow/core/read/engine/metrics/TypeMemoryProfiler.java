package com.netflix.hollow.core.read.engine.metrics;

import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Profiles memory allocation during type-by-type loading in Hollow.
 * Tracks heap usage per type to identify memory hotspots.
 */
public class TypeMemoryProfiler {
    private static final Logger LOG = Logger.getLogger(TypeMemoryProfiler.class.getName());

    private final MemoryMXBean memoryBean;
    private HollowReadStateEngine stateEngine;
    private long startHeapUsed;
    private Map<String, Long> typeMemoryBefore;
    private boolean profiling;

    public TypeMemoryProfiler() {
        this.memoryBean = ManagementFactory.getMemoryMXBean();
        this.typeMemoryBefore = new HashMap<>();
        this.profiling = false;
    }

    /**
     * Start profiling memory allocations for the given state engine.
     */
    public void startProfiling(HollowReadStateEngine stateEngine) {
        this.stateEngine = stateEngine;
        this.profiling = true;

        // Force GC to get cleaner baseline
        System.gc();

        // Capture starting heap usage
        MemoryUsage heapUsage = memoryBean.getHeapMemoryUsage();
        this.startHeapUsed = heapUsage.getUsed();

        // Capture per-type starting memory (estimate based on type state size)
        for (HollowTypeReadState typeState : stateEngine.getTypeStates()) {
            long approxSize = estimateTypeStateMemory(typeState);
            typeMemoryBefore.put(typeState.getSchema().getName(), approxSize);
        }

        LOG.info("Started memory profiling at " + startHeapUsed + " bytes heap used");
    }

    /**
     * End profiling and return results.
     */
    public ProfileResult endProfiling() {
        if (!profiling) {
            throw new IllegalStateException("Profiling not started");
        }

        // Force GC before measurement
        System.gc();

        // Capture ending heap usage
        MemoryUsage heapUsage = memoryBean.getHeapMemoryUsage();
        long endHeapUsed = heapUsage.getUsed();

        long totalAllocated = endHeapUsed - startHeapUsed;

        // Calculate per-type allocations
        Map<String, TypeProfile> typeProfiles = new HashMap<>();

        for (HollowTypeReadState typeState : stateEngine.getTypeStates()) {
            String typeName = typeState.getSchema().getName();
            long afterSize = estimateTypeStateMemory(typeState);
            long beforeSize = typeMemoryBefore.getOrDefault(typeName, 0L);
            long allocated = Math.max(0, afterSize - beforeSize);

            typeProfiles.put(typeName, new TypeProfile(
                typeName,
                allocated,
                typeState.maxOrdinal() + 1
            ));
        }

        profiling = false;

        LOG.info(String.format("Memory profiling complete: %d bytes allocated across %d types",
            totalAllocated, typeProfiles.size()));

        return new ProfileResult(totalAllocated, typeProfiles);
    }

    /**
     * Estimate memory used by a type read state.
     */
    private long estimateTypeStateMemory(HollowTypeReadState typeState) {
        // Use the built-in heap footprint calculation
        return typeState.getApproximateHeapFootprintInBytes();
    }

    /**
     * Profile result containing memory allocation data.
     */
    public static class ProfileResult {
        private final long totalBytesAllocated;
        private final Map<String, TypeProfile> typeProfiles;

        public ProfileResult(long totalBytesAllocated, Map<String, TypeProfile> typeProfiles) {
            this.totalBytesAllocated = totalBytesAllocated;
            this.typeProfiles = typeProfiles;
        }

        public long getTotalBytesAllocated() {
            return totalBytesAllocated;
        }

        public Map<String, TypeProfile> getTypeProfiles() {
            return typeProfiles;
        }

        public void printSummary() {
            System.out.println("=== Type Memory Profile ===");
            System.out.printf("Total allocated: %d bytes (%.2f MB)%n",
                totalBytesAllocated, totalBytesAllocated / 1024.0 / 1024.0);
            System.out.println("\nPer-type breakdown:");

            typeProfiles.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue().bytesAllocated, a.getValue().bytesAllocated))
                .forEach(entry -> {
                    TypeProfile profile = entry.getValue();
                    System.out.printf("  %s: %d bytes (%.2f MB), %d ordinals%n",
                        profile.typeName,
                        profile.bytesAllocated,
                        profile.bytesAllocated / 1024.0 / 1024.0,
                        profile.ordinalCount);
                });
        }
    }

    /**
     * Profile for a single type.
     */
    public static class TypeProfile {
        private final String typeName;
        private final long bytesAllocated;
        private final long ordinalCount;

        public TypeProfile(String typeName, long bytesAllocated, long ordinalCount) {
            this.typeName = typeName;
            this.bytesAllocated = bytesAllocated;
            this.ordinalCount = ordinalCount;
        }

        public String getTypeName() {
            return typeName;
        }

        public long getBytesAllocated() {
            return bytesAllocated;
        }

        public long getOrdinalCount() {
            return ordinalCount;
        }
    }
}

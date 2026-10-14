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
package com.netflix.hollow.tools.checksum;

import com.netflix.hollow.core.memory.encoding.HashCodes;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.util.SimultaneousExecutor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Vector;

/**
 * Can be used to generate checksums for data contained in a {@link HollowReadStateEngine}.
 * <p>
 * Note that the checksums here incorporate the positions of data in sets and maps, which may vary based on hash collisions.
 */
public class HollowChecksum {

    private int currentChecksum = 0;
    private Vector<TypeChecksum> sortedTypeChecksums;

    public void setSortedTypeChecksums(Vector<TypeChecksum> sortedTypeChecksums) {
        this.sortedTypeChecksums = sortedTypeChecksums;
    }

    public Vector<TypeChecksum> getSortedTypeChecksums() {
        return sortedTypeChecksums;
    }

    public HollowChecksum() { }

    public void applyType(TypeChecksum typeChecksum) {
        if (this.sortedTypeChecksums == null) {
            this.sortedTypeChecksums = new Vector<>();
        }
        this.sortedTypeChecksums.addElement(typeChecksum);
        applyInt(typeChecksum.checksum);
    }

    public void applyInt(int value) {
        currentChecksum ^= HashCodes.hashInt(value);
        currentChecksum = HashCodes.hashInt(currentChecksum);
    }

    public void applyLong(long value) {
        currentChecksum ^= HashCodes.hashLong(value);
        currentChecksum = HashCodes.hashInt(currentChecksum);
    }

    public int intValue() {
        return currentChecksum;
    }

    @Override
    public boolean equals(Object other) {
        if(other instanceof HollowChecksum)
            return ((HollowChecksum) other).currentChecksum == currentChecksum;
        return false;
    }

    @Override
    public int hashCode() {
        return currentChecksum;
    }

    public String toString() {
        return Integer.toHexString(currentChecksum);
    }

    public static HollowChecksum forStateEngine(HollowReadStateEngine stateEngine) {
        return forStateEngineWithCommonSchemas(stateEngine, stateEngine);
    }

    public static HollowChecksum forStateEngine(HollowReadStateEngine stateEngine, boolean parallelPerShard) {
        return forStateEngineWithCommonSchemas(stateEngine, stateEngine, parallelPerShard);
    }

    public static HollowChecksum forStateEngineWithCommonSchemas(HollowReadStateEngine stateEngine, HollowReadStateEngine commonSchemasWithState) {
        return forStateEngineWithCommonSchemas(stateEngine, commonSchemasWithState, false);
    }

    /**
     * Overload that takes the per-shard mode explicitly. When {@code true}, the checksum fans out one task per
     * (type, shard) and combines the per-shard partials, so a type dominated by a single large shard parallelizes
     * across its shards.
     *
     * @param parallelPerShard {@code true} to fan out per (type, shard); {@code false} for the legacy per-type path
     */
    public static HollowChecksum forStateEngineWithCommonSchemas(HollowReadStateEngine stateEngine, HollowReadStateEngine commonSchemasWithState, boolean parallelPerShard) {
        if(parallelPerShard)
            return forStateEngineParallelPerShard(stateEngine, commonSchemasWithState);
        return forStateEngineParallelPerType(stateEngine, commonSchemasWithState);
    }

    private static HollowChecksum forStateEngineParallelPerType(HollowReadStateEngine stateEngine, HollowReadStateEngine commonSchemasWithState) {
        final Vector<TypeChecksum> typeChecksums = new Vector<TypeChecksum>();
        SimultaneousExecutor executor = new SimultaneousExecutor(HollowChecksum.class, "checksum-common-schemas");

        for(final HollowTypeReadState typeState : stateEngine.getTypeStates()) {
            HollowTypeReadState commonSchemasWithType = commonSchemasWithState.getTypeState(typeState.getSchema().getName());
            if(commonSchemasWithType != null) {
                final HollowSchema commonSchemasWith = commonSchemasWithType.getSchema();
                executor.execute(new Runnable() {
                    public void run() {
                        HollowChecksum cksum = typeState.getChecksum(commonSchemasWith);
                        typeChecksums.addElement(new TypeChecksum(typeState.getSchema().getName(), cksum));
                    }
                });
            }
        }

        try {
            executor.awaitSuccessfulCompletion();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Collections.sort(typeChecksums);

        HollowChecksum totalChecksum = new HollowChecksum();
        for(TypeChecksum cksum : typeChecksums) {
            totalChecksum.applyType(cksum);
        }

        return totalChecksum;
    }

    private static HollowChecksum forStateEngineParallelPerShard(HollowReadStateEngine stateEngine, HollowReadStateEngine commonSchemasWithState) {
        SimultaneousExecutor executor = new SimultaneousExecutor(HollowChecksum.class, "checksum-common-schemas");

        // Fan out one task per (type, shard) rather than per type. Types that are dominated by a single large
        // type-shard therefore parallelize across their shards instead of running the whole type on one thread.
        // Each shard's partial checksum is stored by shard index so it can be combined deterministically (in
        // ascending shard order) regardless of the order in which the tasks happen to complete.
        final List<TypeShardChecksums> allTypeChecksums = new ArrayList<TypeShardChecksums>();
        for(final HollowTypeReadState typeState : stateEngine.getTypeStates()) {
            HollowTypeReadState commonSchemasWithType = commonSchemasWithState.getTypeState(typeState.getSchema().getName());
            if(commonSchemasWithType != null) {
                final HollowSchema commonSchemasWith = commonSchemasWithType.getSchema();
                final int[] shardChecksums = new int[typeState.numShards()];
                allTypeChecksums.add(new TypeShardChecksums(typeState.getSchema().getName(), shardChecksums));
                for(int shard = 0; shard < shardChecksums.length; shard++) {
                    final int shardNumber = shard;
                    executor.execute(new Runnable() {
                        public void run() {
                            shardChecksums[shardNumber] = typeState.getShardChecksum(commonSchemasWith, shardNumber).intValue();
                        }
                    });
                }
            }
        }

        try {
            executor.awaitSuccessfulCompletion();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Combine each type's per-shard partials in ascending shard order into a per-type checksum, then combine
        // the per-type checksums in sorted type-name order into the total (as before).
        Vector<TypeChecksum> typeChecksums = new Vector<TypeChecksum>();
        for(TypeShardChecksums typeShardChecksums : allTypeChecksums) {
            HollowChecksum typeChecksum = new HollowChecksum();
            for(int shardChecksum : typeShardChecksums.shardChecksums)
                typeChecksum.applyInt(shardChecksum);
            typeChecksums.addElement(new TypeChecksum(typeShardChecksums.typeName, typeChecksum));
        }

        Collections.sort(typeChecksums);

        HollowChecksum totalChecksum = new HollowChecksum();
        for(TypeChecksum cksum : typeChecksums) {
            totalChecksum.applyType(cksum);
        }

        return totalChecksum;
    }

    private static final class TypeShardChecksums {
        private final String typeName;
        private final int[] shardChecksums;

        private TypeShardChecksums(String typeName, int[] shardChecksums) {
            this.typeName = typeName;
            this.shardChecksums = shardChecksums;
        }
    }


    public static class TypeChecksum implements Comparable<TypeChecksum>{
        private final String type;
        private final int checksum;

        public TypeChecksum(String type, HollowChecksum cksum) {
            this.type = type;
            this.checksum = cksum.intValue();
        }

        public int getChecksum() {
            return checksum;
        }

        @Override
        public int compareTo(TypeChecksum other) {
            return type.compareTo(other.type);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TypeChecksum that = (TypeChecksum) o;
            return checksum == that.checksum && Objects.equals(type, that.type);
        }

        @Override
        public int hashCode() {
            return Objects.hash(type, checksum);
        }

        @Override
        public String toString() {
            return "TypeChecksum{" +
                    "type='" + type + '\'' +
                    ", checksum=" + Integer.toHexString(checksum) +
                    '}';
        }
    }
}

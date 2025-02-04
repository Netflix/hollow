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
import java.util.Collections;
import java.util.Vector;

/**
 * Can be used to generate checksums for data contained in a {@link HollowReadStateEngine}.
 * <p>
 * Note that the checksums here incorporate the positions of data in sets and maps, which may vary based on hash collisions.
 */
public class HollowChecksum {

    private int currentChecksum = 0;

    public HollowChecksum() { }

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
    
    public static HollowChecksum forStateEngineWithCommonSchemas(HollowReadStateEngine stateEngine, HollowReadStateEngine commonSchemasWithState) {
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
            totalChecksum.applyInt(cksum.getChecksum());
        }

        return totalChecksum;
    }


    private static class TypeChecksum implements Comparable<TypeChecksum>{
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
    }
}

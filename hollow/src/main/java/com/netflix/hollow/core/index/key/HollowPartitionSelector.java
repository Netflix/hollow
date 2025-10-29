/*
 *  Copyright 2016-2021 Netflix, Inc.
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
package com.netflix.hollow.core.index.key;

import com.netflix.hollow.core.memory.encoding.HashCodes;
import com.netflix.hollow.core.read.HollowReadFieldUtils;

/**
 * Common utility for selecting which partition a record should be assigned to based on
 * consistent hashing of primary key values.
 * <p>
 * This utility provides a consistent hashing function that can be used both when writing
 * records (to select the partition) and when looking up records (to determine which
 * partition contains a given primary key).
 * <p>
 * Uses XOR-based hashing: hashCode = keyHashCode(keys[0], 0) ^ keyHashCode(keys[1], 1) ^ ...
 * <p>
 * This class is used by both write-side (HollowTypeWriteState) and read-side
 * (HollowObjectTypeReadState) to ensure partition assignments are consistent.
 */
public class HollowPartitionSelector {

    /**
     * Computes a hash code from primary key field values using field type information.
     *
     * @param primaryKey the primary key definition containing field types
     * @param primaryKeyFieldValues the values of the primary key fields in order
     * @return hash code based on primary key field values
     */
    public static int computePrimaryKeyHash(PrimaryKey primaryKey, Object... primaryKeyFieldValues) {
        if(primaryKeyFieldValues == null || primaryKeyFieldValues.length == 0) {
            return 0;
        }

        if(primaryKey == null || primaryKey.numFields() != primaryKeyFieldValues.length) {
            // If no primary key metadata, return 0
            return 0;
        }

        // XOR-based hash: hashCode ^= keyHashCode(keys[i], i)
        int hashCode = 0;
        for(int i = 0; i < primaryKeyFieldValues.length; i++) {
            hashCode ^= keyHashCode(primaryKeyFieldValues[i], i);
        }

        return hashCode;
    }

    /**
     * Computes the hash code for a single primary key field value based on its type.
     *
     * @param key the field value (may be actual value or pre-hashed Integer for primitives)
     * @param fieldIdx the index of this field in the primary key
     * @return the hash code for this field
     */
    private static int keyHashCode(Object key, int fieldIdx) {
        if(key == null) {
            return 0;
        }

        // Handle different types based on runtime type
        // Note: For primitives from write records, we may receive pre-hashed Integer values
        if(key instanceof Boolean) {
            return HashCodes.hashInt(HollowReadFieldUtils.booleanHashCode((Boolean)key));
        } else if(key instanceof Double) {
            return HashCodes.hashInt(HollowReadFieldUtils.doubleHashCode(((Double)key).doubleValue()));
        } else if(key instanceof Float) {
            return HashCodes.hashInt(HollowReadFieldUtils.floatHashCode(((Float)key).floatValue()));
        } else if(key instanceof Long) {
            return HashCodes.hashInt(HollowReadFieldUtils.longHashCode(((Long)key).longValue()));
        } else if(key instanceof byte[]) {
            return HashCodes.hashCode((byte[])key);
        } else if(key instanceof String) {
            return HashCodes.hashCode((String)key);
        } else if(key instanceof Integer) {
            // Could be an actual int value, a reference ordinal, or a pre-hashed value
            // For consistency, just hash the integer
            return HashCodes.hashInt(HollowReadFieldUtils.intHashCode(((Integer)key).intValue()));
        }

        // Unknown type - try to use its hashCode
        return key.hashCode();
    }

    /**
     * Selects which partition index a record with the given primary key should map to.
     *
     * @param numPartitions the total number of partitions
     * @param primaryKey the primary key definition
     * @param primaryKeyFieldValues the values of the primary key fields in order
     * @return the partition index (0 to numPartitions-1)
     */
    public static int selectPartition(int numPartitions, PrimaryKey primaryKey, Object... primaryKeyFieldValues) {
        if(numPartitions <= 1) {
            return 0;
        }

        int hash = computePrimaryKeyHash(primaryKey, primaryKeyFieldValues);

        // If hash is 0 (no primary key or all null values), use partition 0
        if(hash == 0) {
            return 0;
        }

        // Use modulo to map to partition index
        // Convert to positive value and modulo by numPartitions
        return (hash & Integer.MAX_VALUE) % numPartitions;
    }

    /**
     * Determines which partition a record with the given primary key would be assigned to.
     * This is an alias for selectPartition with a more descriptive name for lookup operations.
     *
     * @param numPartitions the total number of partitions
     * @param primaryKey the primary key definition
     * @param primaryKeyFieldValues the values of the primary key fields in order
     * @return the partition index (0 to numPartitions-1) where this record would be stored
     */
    public static int getPartitionForPrimaryKey(int numPartitions, PrimaryKey primaryKey, Object... primaryKeyFieldValues) {
        return selectPartition(numPartitions, primaryKey, primaryKeyFieldValues);
    }
}

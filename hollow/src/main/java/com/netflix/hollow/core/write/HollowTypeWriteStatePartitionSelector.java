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
package com.netflix.hollow.core.write;

/**
 * Utility class for selecting which partition a record should be assigned to based on
 * consistent hashing of primary key values.
 * <p>
 * This class provides a consistent hashing function that can be used both when writing
 * records (to select the partition) and when looking up records (to determine which
 * partition contains a given primary key).
 */
public class HollowTypeWriteStatePartitionSelector {

    /**
     * Computes a hash code from primary key field values.
     *
     * @param primaryKeyFieldValues the values of the primary key fields in order
     * @return hash code based on primary key field values
     */
    public static int computePrimaryKeyHash(Object... primaryKeyFieldValues) {
        if(primaryKeyFieldValues == null || primaryKeyFieldValues.length == 0) {
            return 0;
        }

        int hash = 0;
        for(Object value : primaryKeyFieldValues) {
            hash = hash * 31 + (value == null ? 0 : value.hashCode());
        }

        return hash;
    }

    /**
     * Selects which partition index a record with the given primary key should map to.
     *
     * @param numPartitions the total number of partitions
     * @param primaryKeyFieldValues the values of the primary key fields in order
     * @return the partition index (0 to numPartitions-1)
     */
    public static int selectPartition(int numPartitions, Object... primaryKeyFieldValues) {
        if(numPartitions <= 1) {
            return 0;
        }

        int hash = computePrimaryKeyHash(primaryKeyFieldValues);

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
     * This uses the same hashing logic as selectPartition.
     *
     * @param numPartitions the total number of partitions
     * @param primaryKeyFieldValues the values of the primary key fields in order
     * @return the partition index (0 to numPartitions-1) where this record would be stored
     */
    public static int getPartitionForPrimaryKey(int numPartitions, Object... primaryKeyFieldValues) {
        return selectPartition(numPartitions, primaryKeyFieldValues);
    }
}

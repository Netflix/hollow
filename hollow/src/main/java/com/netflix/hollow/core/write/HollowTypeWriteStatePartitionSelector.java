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

import com.netflix.hollow.core.index.key.HollowPartitionSelector;
import com.netflix.hollow.core.index.key.PrimaryKey;

/**
 * Utility class for selecting which partition a record should be assigned to based on
 * consistent hashing of primary key values.
 * <p>
 * This class delegates to {@link HollowPartitionSelector} for the actual implementation.
 *
 * @deprecated Use {@link HollowPartitionSelector} directly instead
 */
@Deprecated
public class HollowTypeWriteStatePartitionSelector {

    /**
     * Computes a hash code from primary key field values using field type information.
     *
     * @param primaryKey the primary key definition containing field types
     * @param primaryKeyFieldValues the values of the primary key fields in order
     * @return hash code based on primary key field values
     */
    public static int computePrimaryKeyHash(PrimaryKey primaryKey, Object... primaryKeyFieldValues) {
        return HollowPartitionSelector.computePrimaryKeyHash(primaryKey, primaryKeyFieldValues);
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
        return HollowPartitionSelector.selectPartition(numPartitions, primaryKey, primaryKeyFieldValues);
    }

    /**
     * Determines which partition a record with the given primary key would be assigned to.
     *
     * @param numPartitions the total number of partitions
     * @param primaryKey the primary key definition
     * @param primaryKeyFieldValues the values of the primary key fields in order
     * @return the partition index (0 to numPartitions-1) where this record would be stored
     */
    public static int getPartitionForPrimaryKey(int numPartitions, PrimaryKey primaryKey, Object... primaryKeyFieldValues) {
        return HollowPartitionSelector.getPartitionForPrimaryKey(numPartitions, primaryKey, primaryKeyFieldValues);
    }
}

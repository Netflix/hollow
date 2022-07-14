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
package com.netflix.hollow.core.read.dataaccess;

import com.netflix.hollow.core.read.engine.set.HollowSetTypeReadState;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;
import com.netflix.hollow.core.schema.HollowSetSchema;

/**
 * A handle for all of the records of a specific SET type in a Hollow dataset.  The most common type of {@link HollowSetTypeDataAccess}
 * is a {@link HollowSetTypeReadState}.
 * 
 * @see HollowSetSchema
 */
public interface HollowSetTypeDataAccess extends HollowCollectionTypeDataAccess {

    HollowSetSchema getSchema();

    /**
     * Note that this method will only reliably work on unhashed sets (sets without a defined hash key or custom defined hash code).
     * <p>
     * Generally, the method {@link #findElement(int, Object...)} may be more useful.
     *
     * @param ordinal the ordinal
     * @param value the value
     * @return whether or not the <b>unhashed</b> set at the specified ordinal contains the specified element ordinal.
     */
    boolean contains(int ordinal, int value);

    /**
     * Generally, the method {@link #findElement(int, Object...)} may be more useful.
     *
     * @param ordinal the ordinal
     * @param value the value
     * @param hashCode the hash code
     * @return whether or not the set at the specified ordinal contains the specified element ordinal with the specified hashCode.
     */
    boolean contains(int ordinal, int value, int hashCode);

    /**
     * Returns The matching ordinal of the element from the set at the specified ordinal which matches the provided hash key.
     *
     * @param ordinal the ordinal
     * @param hashKey the hash keys
     * @return the matching element's ordinal, or {@link com.netflix.hollow.core.HollowConstants#ORDINAL_NONE} if no such element exists.
     */
    int findElement(int ordinal, Object... hashKey);

    int relativeBucketValue(int ordinal, int bucketIndex);

    /**
     * @param ordinal the ordinal
     * @param hashCode the hash code
     * @return a {@link HollowOrdinalIterator} over any elements from the set at the specified which potentially match the specified hashCode.
     */
    HollowOrdinalIterator potentialMatchOrdinalIterator(int ordinal, int hashCode);

}

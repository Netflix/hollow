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

import com.netflix.hollow.core.read.engine.HollowCollectionTypeReadState;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;
import com.netflix.hollow.core.schema.HollowCollectionSchema;
import com.netflix.hollow.core.schema.HollowListSchema;
import com.netflix.hollow.core.schema.HollowSetSchema;

/**
 * A handle for all of the records of a specific LIST or SET type in a Hollow dataset.  The most common type of {@link HollowCollectionTypeDataAccess}
 * is a {@link HollowCollectionTypeReadState}.
 * 
 * @see HollowListSchema
 * @see HollowSetSchema
 */
public interface HollowCollectionTypeDataAccess extends HollowTypeDataAccess {

    /**
     * @param ordinal the ordinal
     * @return the number of elements contained in the set at the specified ordinal.
     */
    int size(int ordinal);

    /**
     * @param ordinal the ordinal
     * @return an iterator over all elements in the collection.
     */
    HollowOrdinalIterator ordinalIterator(int ordinal);

    HollowCollectionSchema getSchema();

}

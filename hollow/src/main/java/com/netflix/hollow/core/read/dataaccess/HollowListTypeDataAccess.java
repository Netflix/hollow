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

import com.netflix.hollow.core.read.engine.list.HollowListTypeReadState;
import com.netflix.hollow.core.schema.HollowListSchema;

/**
 * A handle for all of the records of a specific LIST type in a Hollow dataset.  The most common type of {@link HollowListTypeDataAccess}
 * is a {@link HollowListTypeReadState}.
 * 
 * @see HollowListSchema
 */
public interface HollowListTypeDataAccess extends HollowCollectionTypeDataAccess {

    HollowListSchema getSchema();

    /**
     * @param ordinal the oridinal
     * @param listIndex the list index
     * @return the element at the specified listIndex from the list record at the specified ordinal  
     */
    int getElementOrdinal(int ordinal, int listIndex);

}

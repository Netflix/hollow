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

import com.netflix.hollow.core.HollowDataset;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.missing.MissingDataHandler;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.util.HollowObjectHashCodeFinder;
import java.util.Collection;
import java.util.List;

/**
 * A {@link HollowDataAccess} is a consumer's root handle to a Hollow dataset.  
 * <p>
 * The most common type of {@link HollowDataAccess} is a {@link HollowReadStateEngine}.  
 *
 */
public interface HollowDataAccess extends HollowDataset {

    /**
     * @param typeName the type name
     * @return The handle to data for a specific type in this dataset.
     */
    HollowTypeDataAccess getTypeDataAccess(String typeName);

    /**
     * @param typeName The type name
     * @param ordinal optional parameter.  When known, may provide a more optimal data access implementation for traversal of historical data access.
     * @return The handle to data for a specific type in this dataset.
     */
    HollowTypeDataAccess getTypeDataAccess(String typeName, int ordinal);

    /**
     * @return The names of all types in this dataset
     */
    Collection<String> getAllTypes();

    @Override
    List<HollowSchema> getSchemas();

    @Override
    HollowSchema getSchema(String name);

    @Deprecated
    HollowObjectHashCodeFinder getHashCodeFinder();

    MissingDataHandler getMissingDataHandler();

    void resetSampling();

    boolean hasSampleResults();

}

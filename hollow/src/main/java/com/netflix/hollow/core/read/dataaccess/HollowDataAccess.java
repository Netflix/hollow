/*
 *
 *  Copyright 2016 Netflix, Inc.
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

import com.netflix.hollow.core.util.HollowObjectHashCodeFinder;

import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.HollowDataset;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.missing.MissingDataHandler;
import java.util.List;
import java.util.Collection;

/**
 * A {@link HollowDataAccess} is a consumer's root handle to a Hollow dataset.  
 * <p>
 * The most common type of {@link HollowDataAccess} is a {@link HollowReadStateEngine}.  
 *
 */
public interface HollowDataAccess extends HollowDataset {

    /**
     * @return The handle to data for a specific type in this dataset.
     */
    public HollowTypeDataAccess getTypeDataAccess(String typeName);

    /**
     * @param typeName The type name
     * @param ordinal optional parameter.  When known, may provide a more optimal data access implementation for traversal of historical data access.
     * @return The handle to data for a specific type in this dataset.
     */
    public HollowTypeDataAccess getTypeDataAccess(String typeName, int ordinal);

    /**
     * @return The names of all types in this dataset
     */
    public Collection<String> getAllTypes();
    
    @Override
    public List<HollowSchema> getSchemas();
    
    @Override
    public HollowSchema getSchema(String name);

    public HollowObjectHashCodeFinder getHashCodeFinder();

    public MissingDataHandler getMissingDataHandler();

    public void resetSampling();

    public boolean hasSampleResults();

}

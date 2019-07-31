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
package com.netflix.hollow.core;

import com.netflix.hollow.api.error.SchemaNotFoundException;
import com.netflix.hollow.core.read.dataaccess.HollowDataAccess;
import com.netflix.hollow.core.schema.HollowSchema;
import java.util.List;

/**
 * A Hollow dataset contains a set of strongly typed schemas.
 *
 * This is the superinterface for {@link HollowStateEngine} and {@link HollowDataAccess}
 *
 */
public interface HollowDataset {

    /**
     * @return the schemas for all types in this dataset.
     */
    List<HollowSchema> getSchemas();

    /**
     * @param typeName the type name
     * @return the schema for the specified type in this dataset.
     */
    HollowSchema getSchema(String typeName);

    /**
     * @param typeName the type name
     * @return the schema for the specified type in this dataset.
     * @throws SchemaNotFoundException if the schema is not found
     */
    HollowSchema getNonNullSchema(String typeName) throws SchemaNotFoundException;
    
    /**
     * @param other another HollowDataset
     * @return true iff the other HollowDataset has an identical set of schemas.
     */
    default boolean hasIdenticalSchemas(HollowDataset other) {
        List<HollowSchema> thisSchemas = getSchemas();
        List<HollowSchema> otherSchemas = other.getSchemas();
        
        if(thisSchemas.size() != otherSchemas.size())
            return false;
        
        for (HollowSchema thisSchema : thisSchemas) {
            HollowSchema otherSchema = other.getSchema(thisSchema.getName());
            
            if(otherSchema == null || !thisSchema.equals(otherSchema))
                return false;
        }
        return true;
    }

}

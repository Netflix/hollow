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
package com.netflix.hollow.core.schema;

import com.netflix.hollow.api.error.SchemaNotFoundException;
import com.netflix.hollow.core.HollowDataset;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A HollowDataset implementation which only describes a set of schemas comprising a dataset. 
 */
public class SimpleHollowDataset implements HollowDataset {

    private final Map<String, HollowSchema> schemas;

    public SimpleHollowDataset(Map<String, HollowSchema> schemas) {
        this.schemas = schemas;
    }

    public SimpleHollowDataset(List<HollowSchema> schemas) {
        Map<String, HollowSchema> schemaMap = new HashMap<>(schemas.size());

        for(HollowSchema schema : schemas) {
            schemaMap.put(schema.getName(), schema);
        }

        this.schemas = schemaMap;
    }

    @Override
    public List<HollowSchema> getSchemas() {
        return new ArrayList<>(schemas.values());
    }

    @Override
    public HollowSchema getSchema(String typeName) {
        return schemas.get(typeName);
    }

    @Override
    public HollowSchema getNonNullSchema(String typeName) throws SchemaNotFoundException {
        HollowSchema schema = getSchema(typeName);
        if(schema == null)
            throw new SchemaNotFoundException(typeName, schemas.keySet());
        return schema;
    }

    public static SimpleHollowDataset fromClassDefinitions(Class<?>... classes) {
        HollowWriteStateEngine stateEngine = new HollowWriteStateEngine();
        HollowObjectMapper mapper = new HollowObjectMapper(stateEngine);
        for(Class<?> clazz : classes) {
            mapper.initializeTypeState(clazz);
        }
        return new SimpleHollowDataset(stateEngine.getSchemas());
    }

}

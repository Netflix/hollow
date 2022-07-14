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
package com.netflix.hollow.jsonadapter.discover;

import com.netflix.hollow.core.schema.HollowMapSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.hollow.core.schema.HollowSchema;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SchemaSolidifier {

    public static Collection<HollowSchema> convertDiscoveredSchemas(Collection<HollowDiscoveredSchema> discoveredSchemas) {
        Map<String, HollowSchema> schemaMap = new HashMap<String, HollowSchema>();

        for(HollowDiscoveredSchema discoveredSchema : discoveredSchemas) {
            HollowSchema schema = discoveredSchema.toHollowSchema();
            schemaMap.put(schema.getName(), schema);

            if(schema instanceof HollowMapSchema) {
                String keyType = ((HollowMapSchema) schema).getKeyType();
                if(!schemaMap.containsKey(keyType))
                    schemaMap.put(keyType, getStringSchema(keyType));
            } else if(referencesGenericStringSchema(schema)) {
                if(!schemaMap.containsKey("String"))
                    schemaMap.put("String", getStringSchema("String"));
            }
        }

        return schemaMap.values();
    }

    private static boolean referencesGenericStringSchema(HollowSchema schema) {
        if(schema instanceof HollowObjectSchema) {
            HollowObjectSchema objSchema = (HollowObjectSchema) schema;
            for(int i = 0; i < objSchema.numFields(); i++) {
                if("String".equals(objSchema.getReferencedType(i))) {
                    return true;
                }
            }
        }
        return false;
    }

    private static HollowObjectSchema getStringSchema(String schemaName) {
        HollowObjectSchema schema = new HollowObjectSchema(schemaName, 1);
        schema.addField("value", FieldType.STRING);
        return schema;
    }

}

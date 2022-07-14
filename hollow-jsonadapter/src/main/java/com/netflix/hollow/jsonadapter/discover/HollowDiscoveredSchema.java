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

import com.netflix.hollow.core.schema.HollowListSchema;
import com.netflix.hollow.core.schema.HollowMapSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.hollow.core.schema.HollowSchema;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HollowDiscoveredSchema {
    final String schemaName;
    final DiscoveredSchemaType type;
    final String subType;
    final Map<String, HollowDiscoveredField> fields;

    public HollowDiscoveredSchema(String schemaName, DiscoveredSchemaType schemaType, String subType) {
        this.schemaName = schemaName;
        this.type = schemaType;
        this.subType = subType;
        this.fields = schemaType == DiscoveredSchemaType.OBJECT ? new ConcurrentHashMap<String, HollowDiscoveredField>() : Collections.<String, HollowDiscoveredField>emptyMap();
        // System.out.println(String.format("[new] HollowDiscoveredSchema: schemaName=%s, type=%s, subType=%s", schemaName, schemaType, subType));
    }

    public String getName() {
        return schemaName;
    }

    public Map<String, HollowDiscoveredField> getFields() {
        return fields;
    }

    public synchronized void addOrReplaceField(String fieldName, FieldType fieldType) {
        fields.put(fieldName, new HollowDiscoveredField(fieldType, null));
        //System.out.println(String.format("\t addOrReplaceField: schemaName=%s, fieldName=%s, filedType=%s", schemaName, fieldName, fieldType));
    }

    public void addField(String fieldName, FieldType fieldType) {
        addField(fieldName, fieldType, null);
    }

    public synchronized void addField(String fieldName, FieldType fieldType, String referencedType) {
        HollowDiscoveredField field = fields.get(fieldName);

        if(field == null) {
            fields.put(fieldName, new HollowDiscoveredField(fieldType, referencedType));
        } else {
            if(field.fieldType != fieldType) {
                field.fieldType = mostRelaxed(field.fieldType, fieldType);
            } else if(field.referencedType != referencedType) {
                throw new RuntimeException("Cannot reference more than one type of object for a given field");
            }
        }
        //System.out.println(String.format("\t addField: schemaName=%s, fieldName=%s, filedType=%s, referencedType=%s %s", schemaName, fieldName, fieldType, referencedType, this));
    }

    private static FieldType mostRelaxed(FieldType ft1, FieldType ft2) {
        if(ft1 == FieldType.STRING || ft2 == FieldType.STRING)
            return FieldType.STRING;
        if(ft1 == FieldType.DOUBLE || ft2 == FieldType.DOUBLE)
            return FieldType.DOUBLE;
        throw new RuntimeException("There is no compatible field type between " + ft1 + " and " + ft2);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("HollowDiscoveredSchema [schemaName=");
        builder.append(schemaName);
        builder.append(", type=");
        builder.append(type);
        builder.append(", subType=");
        builder.append(subType);
        builder.append(", fields=");
        builder.append(fields);
        builder.append("]");
        return builder.toString();
    }

    public HollowSchema toHollowSchema() {
        switch(type) {
            case LIST:
                return new HollowListSchema(schemaName, subType);
            case MAP:
                return new HollowMapSchema(schemaName, "MapKey", subType);
            case OBJECT:
                HollowObjectSchema schema = new HollowObjectSchema(schemaName, fields.size());

                for(Map.Entry<String, HollowDiscoveredField> entry : fields.entrySet()) {
                    if(entry.getValue().fieldType == FieldType.STRING) {
                        schema.addField(entry.getKey(), FieldType.REFERENCE, "String");
                    } else {
                        schema.addField(entry.getKey(), entry.getValue().fieldType, entry.getValue().referencedType);
                    }
                }

                return schema;
        }

        throw new IllegalStateException("HollowDiscoveredSchema type must be one of LIST,MAP,OBJECT.  Was " + type);
    }
}
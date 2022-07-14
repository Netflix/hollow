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
package com.netflix.hollow.explorer.ui.model;

import com.netflix.hollow.core.schema.HollowCollectionSchema;
import com.netflix.hollow.core.schema.HollowMapSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowSchema;
import java.util.ArrayList;
import java.util.List;

public class SchemaDisplay {

    private final HollowSchema schema;
    private final List<SchemaDisplayField> displayFields;
    private final String fieldPath;

    private boolean isExpanded;

    public SchemaDisplay(HollowSchema schema) {
        this(schema, "");
    }

    public SchemaDisplay(HollowSchema schema, String fieldPath) {
        this.schema = schema;
        this.fieldPath = fieldPath;
        this.displayFields = createDisplayFields();
        this.isExpanded = false;
    }

    private List<SchemaDisplayField> createDisplayFields() {
        List<SchemaDisplayField> displayFields = new ArrayList<SchemaDisplayField>();

        switch(schema.getSchemaType()) {
            case OBJECT:
                HollowObjectSchema objSchema = (HollowObjectSchema) schema;

                for(int i = 0; i < objSchema.numFields(); i++)
                    displayFields.add(new SchemaDisplayField(fieldPath + "." + objSchema.getFieldName(i), objSchema, i));

                return displayFields;

            case LIST:
            case SET:
                HollowCollectionSchema collSchema = (HollowCollectionSchema) schema;

                displayFields.add(new SchemaDisplayField(fieldPath + ".element", collSchema));

                return displayFields;

            case MAP:
                HollowMapSchema mapSchema = (HollowMapSchema) schema;

                displayFields.add(new SchemaDisplayField(fieldPath + ".key", mapSchema, 0));
                displayFields.add(new SchemaDisplayField(fieldPath + ".value", mapSchema, 1));

                return displayFields;
        }

        throw new IllegalArgumentException();
    }

    public String getTypeName() {
        return schema.getName();
    }

    public HollowSchema getSchema() {
        return schema;
    }

    public List<SchemaDisplayField> getFields() {
        return displayFields;
    }

    public void setExpanded(boolean isExpanded) {
        this.isExpanded = isExpanded;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

}

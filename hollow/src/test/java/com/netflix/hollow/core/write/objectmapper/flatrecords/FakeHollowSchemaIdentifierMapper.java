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
package com.netflix.hollow.core.write.objectmapper.flatrecords;

import com.netflix.hollow.core.HollowDataset;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.schema.HollowSchema.SchemaType;
import java.util.List;

public class FakeHollowSchemaIdentifierMapper implements HollowSchemaIdentifierMapper {

    private final HollowDataset dataset;
    private final List<HollowSchema> schemas;

    public FakeHollowSchemaIdentifierMapper(HollowDataset dataset) {
        this.schemas = dataset.getSchemas();
        this.dataset = dataset;
    }

    @Override
    public HollowSchema getSchema(int identifier) {
        return schemas.get(identifier);
    }

    @Override
    public FieldType[] getPrimaryKeyFieldTypes(int identifier) {
        HollowSchema schema = getSchema(identifier);
        if (schema.getSchemaType() == SchemaType.OBJECT) {
            PrimaryKey primaryKey = ((HollowObjectSchema) schema).getPrimaryKey();

            if (primaryKey != null) {
                FieldType fieldTypes[] = new FieldType[primaryKey.numFields()];

                for (int i = 0; i < fieldTypes.length; i++) {
                    fieldTypes[i] = primaryKey.getFieldType(dataset, i);
                }

                return fieldTypes;
            }
        }

        return null;
    }

    @Override
    public int getSchemaId(HollowSchema forSchema) {

        for (int i = 0; i < schemas.size(); i++) {
            HollowSchema datasetSchema = schemas.get(i);

            if (forSchema.equals(datasetSchema)) {
                return i;
            }
        }

        throw new IllegalArgumentException("Schema is unidentified: \n" + forSchema.toString());
    }

}

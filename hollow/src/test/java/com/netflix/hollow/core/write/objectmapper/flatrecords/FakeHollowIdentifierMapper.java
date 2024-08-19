package com.netflix.hollow.core.write.objectmapper.flatrecords;

import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.schema.SimpleHollowDataset;
import com.netflix.hollow.core.write.objectmapper.flatrecords.HollowSchemaIdentifierMapper;

import java.util.ArrayList;
import java.util.List;

public class FakeHollowIdentifierMapper implements HollowSchemaIdentifierMapper {
    private final List<HollowSchema> allSchemas = new ArrayList<>();

    @Override
    public HollowSchema getSchema(int identifier) {
        return allSchemas.get(identifier);
    }

    @Override
    public HollowObjectSchema.FieldType[] getPrimaryKeyFieldTypes(int identifier) {
        HollowSchema schema = getSchema(identifier);
        if (schema.getSchemaType() == HollowSchema.SchemaType.OBJECT) {
            PrimaryKey primaryKey = ((HollowObjectSchema) schema).getPrimaryKey();

            if (primaryKey != null) {
                HollowObjectSchema.FieldType[] fieldTypes = new HollowObjectSchema.FieldType[primaryKey.numFields()];

                for (int i = 0; i < fieldTypes.length; i++) {
                    fieldTypes[i] = primaryKey.getFieldType(new SimpleHollowDataset(allSchemas), i);
                }

                return fieldTypes;
            }
        }

        return null;
    }

    @Override
    public int getSchemaId(HollowSchema schema) {
        for(int i=0;i<allSchemas.size();i++)
            if(allSchemas.get(i).equals(schema))
                return i;
        allSchemas.add(schema);
        return allSchemas.size()-1;
    }
}
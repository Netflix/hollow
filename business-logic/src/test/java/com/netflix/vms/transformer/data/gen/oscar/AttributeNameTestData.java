package com.netflix.vms.transformer.data.gen.oscar;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

public class AttributeNameTestData extends HollowTestObjectRecord {

    AttributeNameTestData(AttributeNameField... fields){
        super(fields);
    }

    public static AttributeNameTestData AttributeName(AttributeNameField... fields) {
        return new AttributeNameTestData(fields);
    }

    public static AttributeNameTestData AttributeName(String val) {
        return AttributeName(AttributeNameField.value(val));
    }

    public AttributeNameTestData update(AttributeNameField... fields){
        super.addFields(fields);
        return this;
    }

    public String value() {
        Field f = super.getField("value");
        return f == null ? null : (String)f.value;
    }

    public static class AttributeNameField extends HollowTestObjectRecord.Field {

        private AttributeNameField(String name, Object val) { super(name, val); }

        public static AttributeNameField value(String val) {
            return new AttributeNameField("value", val);
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("AttributeName", 1);

    static {
        SCHEMA.addField("value", FieldType.STRING);
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}
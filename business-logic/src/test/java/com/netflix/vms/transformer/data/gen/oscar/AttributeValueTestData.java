package com.netflix.vms.transformer.data.gen.oscar;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

public class AttributeValueTestData extends HollowTestObjectRecord {

    AttributeValueTestData(AttributeValueField... fields){
        super(fields);
    }

    public static AttributeValueTestData AttributeValue(AttributeValueField... fields) {
        return new AttributeValueTestData(fields);
    }

    public static AttributeValueTestData AttributeValue(String val) {
        return AttributeValue(AttributeValueField.value(val));
    }

    public AttributeValueTestData update(AttributeValueField... fields){
        super.addFields(fields);
        return this;
    }

    public String value() {
        Field f = super.getField("value");
        return f == null ? null : (String)f.value;
    }

    public static class AttributeValueField extends HollowTestObjectRecord.Field {

        private AttributeValueField(String name, Object val) { super(name, val); }

        public static AttributeValueField value(String val) {
            return new AttributeValueField("value", val);
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("AttributeValue", 1);

    static {
        SCHEMA.addField("value", FieldType.STRING);
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}
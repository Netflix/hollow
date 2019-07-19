package com.netflix.vms.transformer.data.gen.oscar;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

public class InteractiveTypeTestData extends HollowTestObjectRecord {

    InteractiveTypeTestData(InteractiveTypeField... fields){
        super(fields);
    }

    public static InteractiveTypeTestData InteractiveType(InteractiveTypeField... fields) {
        return new InteractiveTypeTestData(fields);
    }

    public static InteractiveTypeTestData InteractiveType(String val) {
        return InteractiveType(InteractiveTypeField.value(val));
    }

    public InteractiveTypeTestData update(InteractiveTypeField... fields){
        super.addFields(fields);
        return this;
    }

    public String value() {
        Field f = super.getField("value");
        return f == null ? null : (String)f.value;
    }

    public static class InteractiveTypeField extends HollowTestObjectRecord.Field {

        private InteractiveTypeField(String name, Object val) { super(name, val); }

        public static InteractiveTypeField value(String val) {
            return new InteractiveTypeField("value", val);
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("InteractiveType", 1);

    static {
        SCHEMA.addField("value", FieldType.STRING);
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}
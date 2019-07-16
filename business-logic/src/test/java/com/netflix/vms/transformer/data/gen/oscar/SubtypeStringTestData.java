package com.netflix.vms.transformer.data.gen.oscar;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

public class SubtypeStringTestData extends HollowTestObjectRecord {

    SubtypeStringTestData(SubtypeStringField... fields){
        super(fields);
    }

    public static SubtypeStringTestData SubtypeString(SubtypeStringField... fields) {
        return new SubtypeStringTestData(fields);
    }

    public static SubtypeStringTestData SubtypeString(String val) {
        return SubtypeString(SubtypeStringField.value(val));
    }

    public SubtypeStringTestData update(SubtypeStringField... fields){
        super.addFields(fields);
        return this;
    }

    public String value() {
        Field f = super.getField("value");
        return f == null ? null : (String)f.value;
    }

    public static class SubtypeStringField extends HollowTestObjectRecord.Field {

        private SubtypeStringField(String name, Object val) { super(name, val); }

        public static SubtypeStringField value(String val) {
            return new SubtypeStringField("value", val);
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("SubtypeString", 1);

    static {
        SCHEMA.addField("value", FieldType.STRING);
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}
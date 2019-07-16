package com.netflix.vms.transformer.data.gen.oscar;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

public class SupplementalSubtypeTestData extends HollowTestObjectRecord {

    SupplementalSubtypeTestData(SupplementalSubtypeField... fields){
        super(fields);
    }

    public static SupplementalSubtypeTestData SupplementalSubtype(SupplementalSubtypeField... fields) {
        return new SupplementalSubtypeTestData(fields);
    }

    public static SupplementalSubtypeTestData SupplementalSubtype(String val) {
        return SupplementalSubtype(SupplementalSubtypeField.value(val));
    }

    public SupplementalSubtypeTestData update(SupplementalSubtypeField... fields){
        super.addFields(fields);
        return this;
    }

    public String value() {
        Field f = super.getField("value");
        return f == null ? null : (String)f.value;
    }

    public static class SupplementalSubtypeField extends HollowTestObjectRecord.Field {

        private SupplementalSubtypeField(String name, Object val) { super(name, val); }

        public static SupplementalSubtypeField value(String val) {
            return new SupplementalSubtypeField("value", val);
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("SupplementalSubtype", 1);

    static {
        SCHEMA.addField("value", FieldType.STRING);
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}
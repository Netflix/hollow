package com.netflix.vms.transformer.data.gen.oscar;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

public class OverrideEntityValueTestData extends HollowTestObjectRecord {

    OverrideEntityValueTestData(OverrideEntityValueField... fields){
        super(fields);
    }

    public static OverrideEntityValueTestData OverrideEntityValue(OverrideEntityValueField... fields) {
        return new OverrideEntityValueTestData(fields);
    }

    public static OverrideEntityValueTestData OverrideEntityValue(String val) {
        return OverrideEntityValue(OverrideEntityValueField.value(val));
    }

    public OverrideEntityValueTestData update(OverrideEntityValueField... fields){
        super.addFields(fields);
        return this;
    }

    public String value() {
        Field f = super.getField("value");
        return f == null ? null : (String)f.value;
    }

    public static class OverrideEntityValueField extends HollowTestObjectRecord.Field {

        private OverrideEntityValueField(String name, Object val) { super(name, val); }

        public static OverrideEntityValueField value(String val) {
            return new OverrideEntityValueField("value", val);
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("OverrideEntityValue", 1);

    static {
        SCHEMA.addField("value", FieldType.STRING);
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}
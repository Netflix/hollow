package com.netflix.vms.transformer.data.gen.oscar;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

public class ForceReasonTestData extends HollowTestObjectRecord {

    ForceReasonTestData(ForceReasonField... fields){
        super(fields);
    }

    public static ForceReasonTestData ForceReason(ForceReasonField... fields) {
        return new ForceReasonTestData(fields);
    }

    public static ForceReasonTestData ForceReason(String val) {
        return ForceReason(ForceReasonField.value(val));
    }

    public ForceReasonTestData update(ForceReasonField... fields){
        super.addFields(fields);
        return this;
    }

    public String value() {
        Field f = super.getField("value");
        return f == null ? null : (String)f.value;
    }

    public static class ForceReasonField extends HollowTestObjectRecord.Field {

        private ForceReasonField(String name, Object val) { super(name, val); }

        public static ForceReasonField value(String val) {
            return new ForceReasonField("value", val);
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("ForceReason", 1);

    static {
        SCHEMA.addField("value", FieldType.STRING);
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}
package com.netflix.vms.transformer.data.gen.oscar;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

public class PhaseNameTestData extends HollowTestObjectRecord {

    PhaseNameTestData(PhaseNameField... fields){
        super(fields);
    }

    public static PhaseNameTestData PhaseName(PhaseNameField... fields) {
        return new PhaseNameTestData(fields);
    }

    public static PhaseNameTestData PhaseName(String val) {
        return PhaseName(PhaseNameField.value(val));
    }

    public PhaseNameTestData update(PhaseNameField... fields){
        super.addFields(fields);
        return this;
    }

    public String value() {
        Field f = super.getField("value");
        return f == null ? null : (String)f.value;
    }

    public static class PhaseNameField extends HollowTestObjectRecord.Field {

        private PhaseNameField(String name, Object val) { super(name, val); }

        public static PhaseNameField value(String val) {
            return new PhaseNameField("value", val);
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("PhaseName", 1);

    static {
        SCHEMA.addField("value", FieldType.STRING);
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}
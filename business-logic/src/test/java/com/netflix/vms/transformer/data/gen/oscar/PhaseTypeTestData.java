package com.netflix.vms.transformer.data.gen.oscar;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

public class PhaseTypeTestData extends HollowTestObjectRecord {

    PhaseTypeTestData(PhaseTypeField... fields){
        super(fields);
    }

    public static PhaseTypeTestData PhaseType(PhaseTypeField... fields) {
        return new PhaseTypeTestData(fields);
    }

    public static PhaseTypeTestData PhaseType(String val) {
        return PhaseType(PhaseTypeField._name(val));
    }

    public PhaseTypeTestData update(PhaseTypeField... fields){
        super.addFields(fields);
        return this;
    }

    public String _name() {
        Field f = super.getField("_name");
        return f == null ? null : (String)f.value;
    }

    public static class PhaseTypeField extends HollowTestObjectRecord.Field {

        private PhaseTypeField(String name, Object val) { super(name, val); }

        public static PhaseTypeField _name(String val) {
            return new PhaseTypeField("_name", val);
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("PhaseType", 1);

    static {
        SCHEMA.addField("_name", FieldType.STRING);
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}
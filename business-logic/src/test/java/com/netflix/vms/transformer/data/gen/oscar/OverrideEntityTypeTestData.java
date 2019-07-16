package com.netflix.vms.transformer.data.gen.oscar;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

public class OverrideEntityTypeTestData extends HollowTestObjectRecord {

    OverrideEntityTypeTestData(OverrideEntityTypeField... fields){
        super(fields);
    }

    public static OverrideEntityTypeTestData OverrideEntityType(OverrideEntityTypeField... fields) {
        return new OverrideEntityTypeTestData(fields);
    }

    public static OverrideEntityTypeTestData OverrideEntityType(String val) {
        return OverrideEntityType(OverrideEntityTypeField._name(val));
    }

    public OverrideEntityTypeTestData update(OverrideEntityTypeField... fields){
        super.addFields(fields);
        return this;
    }

    public String _name() {
        Field f = super.getField("_name");
        return f == null ? null : (String)f.value;
    }

    public static class OverrideEntityTypeField extends HollowTestObjectRecord.Field {

        private OverrideEntityTypeField(String name, Object val) { super(name, val); }

        public static OverrideEntityTypeField _name(String val) {
            return new OverrideEntityTypeField("_name", val);
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("OverrideEntityType", 1);

    static {
        SCHEMA.addField("_name", FieldType.STRING);
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}
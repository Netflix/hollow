package com.netflix.vms.transformer.data.gen.oscar;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

public class RolloutTypeTestData extends HollowTestObjectRecord {

    RolloutTypeTestData(RolloutTypeField... fields){
        super(fields);
    }

    public static RolloutTypeTestData RolloutType(RolloutTypeField... fields) {
        return new RolloutTypeTestData(fields);
    }

    public static RolloutTypeTestData RolloutType(String val) {
        return RolloutType(RolloutTypeField._name(val));
    }

    public RolloutTypeTestData update(RolloutTypeField... fields){
        super.addFields(fields);
        return this;
    }

    public String _name() {
        Field f = super.getField("_name");
        return f == null ? null : (String)f.value;
    }

    public static class RolloutTypeField extends HollowTestObjectRecord.Field {

        private RolloutTypeField(String name, Object val) { super(name, val); }

        public static RolloutTypeField _name(String val) {
            return new RolloutTypeField("_name", val);
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("RolloutType", 1);

    static {
        SCHEMA.addField("_name", FieldType.STRING);
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}
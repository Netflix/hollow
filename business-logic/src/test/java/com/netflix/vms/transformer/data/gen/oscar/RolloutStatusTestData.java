package com.netflix.vms.transformer.data.gen.oscar;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

public class RolloutStatusTestData extends HollowTestObjectRecord {

    RolloutStatusTestData(RolloutStatusField... fields){
        super(fields);
    }

    public static RolloutStatusTestData RolloutStatus(RolloutStatusField... fields) {
        return new RolloutStatusTestData(fields);
    }

    public static RolloutStatusTestData RolloutStatus(String val) {
        return RolloutStatus(RolloutStatusField._name(val));
    }

    public RolloutStatusTestData update(RolloutStatusField... fields){
        super.addFields(fields);
        return this;
    }

    public String _name() {
        Field f = super.getField("_name");
        return f == null ? null : (String)f.value;
    }

    public static class RolloutStatusField extends HollowTestObjectRecord.Field {

        private RolloutStatusField(String name, Object val) { super(name, val); }

        public static RolloutStatusField _name(String val) {
            return new RolloutStatusField("_name", val);
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("RolloutStatus", 1);

    static {
        SCHEMA.addField("_name", FieldType.STRING);
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}
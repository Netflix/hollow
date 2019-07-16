package com.netflix.vms.transformer.data.gen.oscar;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

public class RolloutNameTestData extends HollowTestObjectRecord {

    RolloutNameTestData(RolloutNameField... fields){
        super(fields);
    }

    public static RolloutNameTestData RolloutName(RolloutNameField... fields) {
        return new RolloutNameTestData(fields);
    }

    public static RolloutNameTestData RolloutName(String val) {
        return RolloutName(RolloutNameField.value(val));
    }

    public RolloutNameTestData update(RolloutNameField... fields){
        super.addFields(fields);
        return this;
    }

    public String value() {
        Field f = super.getField("value");
        return f == null ? null : (String)f.value;
    }

    public static class RolloutNameField extends HollowTestObjectRecord.Field {

        private RolloutNameField(String name, Object val) { super(name, val); }

        public static RolloutNameField value(String val) {
            return new RolloutNameField("value", val);
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("RolloutName", 1);

    static {
        SCHEMA.addField("value", FieldType.STRING);
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}
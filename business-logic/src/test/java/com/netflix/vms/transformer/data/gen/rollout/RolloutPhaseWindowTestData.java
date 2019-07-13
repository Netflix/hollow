package com.netflix.vms.transformer.data.gen.rollout;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

public class RolloutPhaseWindowTestData extends HollowTestObjectRecord {

    RolloutPhaseWindowTestData(RolloutPhaseWindowField... fields){
        super(fields);
    }

    public static RolloutPhaseWindowTestData RolloutPhaseWindow(RolloutPhaseWindowField... fields) {
        return new RolloutPhaseWindowTestData(fields);
    }

    public RolloutPhaseWindowTestData update(RolloutPhaseWindowField... fields){
        super.addFields(fields);
        return this;
    }

    public long endDate() {
        Field f = super.getField("endDate");
        return f == null ? Long.MIN_VALUE : (Long)f.value;
    }

    public long startDate() {
        Field f = super.getField("startDate");
        return f == null ? Long.MIN_VALUE : (Long)f.value;
    }

    public static class RolloutPhaseWindowField extends Field {

        private RolloutPhaseWindowField(String name, Object val) { super(name, val); }

        public static RolloutPhaseWindowField endDate(long val) {
            return new RolloutPhaseWindowField("endDate", val);
        }

        public static RolloutPhaseWindowField startDate(long val) {
            return new RolloutPhaseWindowField("startDate", val);
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("RolloutPhaseWindow", 2);

    static {
        SCHEMA.addField("endDate", FieldType.LONG);
        SCHEMA.addField("startDate", FieldType.LONG);
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}
package com.netflix.vms.transformer.data.gen.gatekeeper2;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

public class ParentNodeIdTestData extends HollowTestObjectRecord {

    ParentNodeIdTestData(ParentNodeIdField... fields){
        super(fields);
    }

    public static ParentNodeIdTestData ParentNodeId(ParentNodeIdField... fields) {
        return new ParentNodeIdTestData(fields);
    }

    public static ParentNodeIdTestData ParentNodeId(long val) {
        return ParentNodeId(ParentNodeIdField.value(val));
    }

    public ParentNodeIdTestData update(ParentNodeIdField... fields){
        super.addFields(fields);
        return this;
    }

    public long value() {
        Field f = super.getField("value");
        return f == null ? Long.MIN_VALUE : (Long)f.value;
    }

    public static class ParentNodeIdField extends HollowTestObjectRecord.Field {

        private ParentNodeIdField(String name, Object val) { super(name, val); }

        public static ParentNodeIdField value(long val) {
            return new ParentNodeIdField("value", val);
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("ParentNodeId", 1);

    static {
        SCHEMA.addField("value", FieldType.LONG);
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}
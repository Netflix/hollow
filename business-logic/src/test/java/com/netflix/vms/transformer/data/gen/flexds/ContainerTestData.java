package com.netflix.vms.transformer.data.gen.flexds;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

public class ContainerTestData extends HollowTestObjectRecord {

    ContainerTestData(ContainerField... fields){
        super(fields);
    }

    public static ContainerTestData Container(ContainerField... fields) {
        return new ContainerTestData(fields);
    }

    public ContainerTestData update(ContainerField... fields){
        super.addFields(fields);
        return this;
    }

    public int sequenceNumber() {
        Field f = super.getField("sequenceNumber");
        return f == null ? Integer.MIN_VALUE : (Integer)f.value;
    }

    public long parentId() {
        Field f = super.getField("parentId");
        return f == null ? Long.MIN_VALUE : (Long)f.value;
    }

    public long dataId() {
        Field f = super.getField("dataId");
        return f == null ? Long.MIN_VALUE : (Long)f.value;
    }

    public static class ContainerField extends HollowTestObjectRecord.Field {

        private ContainerField(String name, Object val) { super(name, val); }

        public static ContainerField sequenceNumber(int val) {
            return new ContainerField("sequenceNumber", val);
        }

        public static ContainerField parentId(long val) {
            return new ContainerField("parentId", val);
        }

        public static ContainerField dataId(long val) {
            return new ContainerField("dataId", val);
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("Container", 3);

    static {
        SCHEMA.addField("sequenceNumber", FieldType.INT);
        SCHEMA.addField("parentId", FieldType.LONG);
        SCHEMA.addField("dataId", FieldType.LONG);
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}
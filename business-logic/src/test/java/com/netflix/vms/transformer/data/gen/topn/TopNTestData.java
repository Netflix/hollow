package com.netflix.vms.transformer.data.gen.topn;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

public class TopNTestData extends HollowTestObjectRecord {

    TopNTestData(TopNField... fields){
        super(fields);
    }

    public static TopNTestData TopN(TopNField... fields) {
        return new TopNTestData(fields);
    }

    public TopNTestData update(TopNField... fields){
        super.addFields(fields);
        return this;
    }

    public long videoId() {
        Field f = super.getField("videoId");
        return f == null ? Long.MIN_VALUE : (Long)f.value;
    }

    public SetOfTopNAttributeTestData attributes() {
        Field f = super.getField("attributes");
        return f == null ? null : (SetOfTopNAttributeTestData)f.value;
    }

    public static class TopNField extends HollowTestObjectRecord.Field {

        private TopNField(String name, Object val) { super(name, val); }

        public static TopNField videoId(long val) {
            return new TopNField("videoId", val);
        }

        public static TopNField attributes(SetOfTopNAttributeTestData val) {
            return new TopNField("attributes", val);
        }

        public static TopNField attributes(TopNAttributeTestData... elements) {
            return attributes(new SetOfTopNAttributeTestData(elements));
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("TopN", 2, new PrimaryKey("TopN", "videoId"));

    static {
        SCHEMA.addField("videoId", FieldType.LONG);
        SCHEMA.addField("attributes", FieldType.REFERENCE, "SetOfTopNAttribute");
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}
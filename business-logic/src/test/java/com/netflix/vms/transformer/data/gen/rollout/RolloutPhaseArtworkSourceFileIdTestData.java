package com.netflix.vms.transformer.data.gen.rollout;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.vms.transformer.data.gen.rollout.StringTestData.StringField;

public class RolloutPhaseArtworkSourceFileIdTestData extends HollowTestObjectRecord {

    RolloutPhaseArtworkSourceFileIdTestData(RolloutPhaseArtworkSourceFileIdField... fields){
        super(fields);
    }

    public static RolloutPhaseArtworkSourceFileIdTestData RolloutPhaseArtworkSourceFileId(RolloutPhaseArtworkSourceFileIdField... fields) {
        return new RolloutPhaseArtworkSourceFileIdTestData(fields);
    }

    public RolloutPhaseArtworkSourceFileIdTestData update(RolloutPhaseArtworkSourceFileIdField... fields){
        super.addFields(fields);
        return this;
    }

    public StringTestData valueRef() {
        Field f = super.getField("value");
        return f == null ? null : (StringTestData)f.value;
    }

    public String value() {
        Field f = super.getField("value");
        if(f == null) return null;
        StringTestData ref = (StringTestData)f.value;
        return ref.value();
    }

    public static class RolloutPhaseArtworkSourceFileIdField extends Field {

        private RolloutPhaseArtworkSourceFileIdField(String name, Object val) { super(name, val); }

        public static RolloutPhaseArtworkSourceFileIdField value(StringTestData val) {
            return new RolloutPhaseArtworkSourceFileIdField("value", val);
        }

        public static RolloutPhaseArtworkSourceFileIdField value(StringField... fields) {
            return value(new StringTestData(fields));
        }

        public static RolloutPhaseArtworkSourceFileIdField value(String val) {
            return value(StringField.value(val));
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("RolloutPhaseArtworkSourceFileId", 1);

    static {
        SCHEMA.addField("value", FieldType.REFERENCE, "String");
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}
package com.netflix.vms.transformer.data.gen.rollout;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.vms.transformer.data.gen.rollout.StringTestData.StringField;

public class RolloutTestData extends HollowTestObjectRecord {

    RolloutTestData(RolloutField... fields){
        super(fields);
    }

    public static RolloutTestData Rollout(RolloutField... fields) {
        return new RolloutTestData(fields);
    }

    public RolloutTestData update(RolloutField... fields){
        super.addFields(fields);
        return this;
    }

    public long rolloutId() {
        Field f = super.getField("rolloutId");
        return f == null ? Long.MIN_VALUE : (Long)f.value;
    }

    public long movieId() {
        Field f = super.getField("movieId");
        return f == null ? Long.MIN_VALUE : (Long)f.value;
    }

    public StringTestData rolloutNameRef() {
        Field f = super.getField("rolloutName");
        return f == null ? null : (StringTestData)f.value;
    }

    public String rolloutName() {
        Field f = super.getField("rolloutName");
        if(f == null) return null;
        StringTestData ref = (StringTestData)f.value;
        return ref.value();
    }

    public StringTestData rolloutTypeRef() {
        Field f = super.getField("rolloutType");
        return f == null ? null : (StringTestData)f.value;
    }

    public String rolloutType() {
        Field f = super.getField("rolloutType");
        if(f == null) return null;
        StringTestData ref = (StringTestData)f.value;
        return ref.value();
    }

    public RolloutPhaseListTestData phases() {
        Field f = super.getField("phases");
        return f == null ? null : (RolloutPhaseListTestData)f.value;
    }

    public static class RolloutField extends Field {

        private RolloutField(String name, Object val) { super(name, val); }

        public static RolloutField rolloutId(long val) {
            return new RolloutField("rolloutId", val);
        }

        public static RolloutField movieId(long val) {
            return new RolloutField("movieId", val);
        }

        public static RolloutField rolloutName(StringTestData val) {
            return new RolloutField("rolloutName", val);
        }

        public static RolloutField rolloutName(StringField... fields) {
            return rolloutName(new StringTestData(fields));
        }

        public static RolloutField rolloutName(String val) {
            return rolloutName(StringField.value(val));
        }

        public static RolloutField rolloutType(StringTestData val) {
            return new RolloutField("rolloutType", val);
        }

        public static RolloutField rolloutType(StringField... fields) {
            return rolloutType(new StringTestData(fields));
        }

        public static RolloutField rolloutType(String val) {
            return rolloutType(StringField.value(val));
        }

        public static RolloutField phases(RolloutPhaseListTestData val) {
            return new RolloutField("phases", val);
        }

        public static RolloutField phases(RolloutPhaseTestData... elements) {
            return phases(new RolloutPhaseListTestData(elements));
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("Rollout", 5, new PrimaryKey("Rollout", "rolloutId", "movieId"));

    static {
        SCHEMA.addField("rolloutId", FieldType.LONG);
        SCHEMA.addField("movieId", FieldType.LONG);
        SCHEMA.addField("rolloutName", FieldType.REFERENCE, "String");
        SCHEMA.addField("rolloutType", FieldType.REFERENCE, "String");
        SCHEMA.addField("phases", FieldType.REFERENCE, "RolloutPhaseList");
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}
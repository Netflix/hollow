package com.netflix.vms.transformer.data.gen.oscar;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

import com.netflix.vms.transformer.data.gen.oscar.DateTestData.DateField;
import com.netflix.vms.transformer.data.gen.oscar.MovieIdTestData.MovieIdField;
import com.netflix.vms.transformer.data.gen.oscar.RolloutNameTestData.RolloutNameField;
import com.netflix.vms.transformer.data.gen.oscar.RolloutStatusTestData.RolloutStatusField;
import com.netflix.vms.transformer.data.gen.oscar.RolloutTypeTestData.RolloutTypeField;
import com.netflix.vms.transformer.data.gen.oscar.StringTestData.StringField;

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

    public MovieIdTestData movieIdRef() {
        Field f = super.getField("movieId");
        return f == null ? null : (MovieIdTestData)f.value;
    }

    public long movieId() {
        Field f = super.getField("movieId");
        if(f == null) return Long.MIN_VALUE;
        MovieIdTestData ref = (MovieIdTestData)f.value;
        return ref.value();
    }

    public RolloutNameTestData rolloutNameRef() {
        Field f = super.getField("rolloutName");
        return f == null ? null : (RolloutNameTestData)f.value;
    }

    public String rolloutName() {
        Field f = super.getField("rolloutName");
        if(f == null) return null;
        RolloutNameTestData ref = (RolloutNameTestData)f.value;
        return ref.value();
    }

    public RolloutTypeTestData typeRef() {
        Field f = super.getField("type");
        return f == null ? null : (RolloutTypeTestData)f.value;
    }

    public String type() {
        Field f = super.getField("type");
        if(f == null) return null;
        RolloutTypeTestData ref = (RolloutTypeTestData)f.value;
        return ref._name();
    }

    public RolloutStatusTestData statusRef() {
        Field f = super.getField("status");
        return f == null ? null : (RolloutStatusTestData)f.value;
    }

    public String status() {
        Field f = super.getField("status");
        if(f == null) return null;
        RolloutStatusTestData ref = (RolloutStatusTestData)f.value;
        return ref._name();
    }

    public SetOfRolloutPhaseTestData phases() {
        Field f = super.getField("phases");
        return f == null ? null : (SetOfRolloutPhaseTestData)f.value;
    }

    public SetOfRolloutCountryTestData countries() {
        Field f = super.getField("countries");
        return f == null ? null : (SetOfRolloutCountryTestData)f.value;
    }

    public DateTestData dateCreatedRef() {
        Field f = super.getField("dateCreated");
        return f == null ? null : (DateTestData)f.value;
    }

    public long dateCreated() {
        Field f = super.getField("dateCreated");
        if(f == null) return Long.MIN_VALUE;
        DateTestData ref = (DateTestData)f.value;
        return ref.value();
    }

    public DateTestData lastUpdatedRef() {
        Field f = super.getField("lastUpdated");
        return f == null ? null : (DateTestData)f.value;
    }

    public long lastUpdated() {
        Field f = super.getField("lastUpdated");
        if(f == null) return Long.MIN_VALUE;
        DateTestData ref = (DateTestData)f.value;
        return ref.value();
    }

    public StringTestData createdByRef() {
        Field f = super.getField("createdBy");
        return f == null ? null : (StringTestData)f.value;
    }

    public String createdBy() {
        Field f = super.getField("createdBy");
        if(f == null) return null;
        StringTestData ref = (StringTestData)f.value;
        return ref.value();
    }

    public StringTestData updatedByRef() {
        Field f = super.getField("updatedBy");
        return f == null ? null : (StringTestData)f.value;
    }

    public String updatedBy() {
        Field f = super.getField("updatedBy");
        if(f == null) return null;
        StringTestData ref = (StringTestData)f.value;
        return ref.value();
    }

    public static class RolloutField extends HollowTestObjectRecord.Field {

        private RolloutField(String name, Object val) { super(name, val); }

        public static RolloutField rolloutId(long val) {
            return new RolloutField("rolloutId", val);
        }

        public static RolloutField movieId(MovieIdTestData val) {
            return new RolloutField("movieId", val);
        }

        public static RolloutField movieId(MovieIdField... fields) {
            return movieId(new MovieIdTestData(fields));
        }

        public static RolloutField movieId(long val) {
            return movieId(MovieIdField.value(val));
        }

        public static RolloutField rolloutName(RolloutNameTestData val) {
            return new RolloutField("rolloutName", val);
        }

        public static RolloutField rolloutName(RolloutNameField... fields) {
            return rolloutName(new RolloutNameTestData(fields));
        }

        public static RolloutField rolloutName(String val) {
            return rolloutName(RolloutNameField.value(val));
        }

        public static RolloutField type(RolloutTypeTestData val) {
            return new RolloutField("type", val);
        }

        public static RolloutField type(RolloutTypeField... fields) {
            return type(new RolloutTypeTestData(fields));
        }

        public static RolloutField type(String val) {
            return type(RolloutTypeField._name(val));
        }

        public static RolloutField status(RolloutStatusTestData val) {
            return new RolloutField("status", val);
        }

        public static RolloutField status(RolloutStatusField... fields) {
            return status(new RolloutStatusTestData(fields));
        }

        public static RolloutField status(String val) {
            return status(RolloutStatusField._name(val));
        }

        public static RolloutField phases(SetOfRolloutPhaseTestData val) {
            return new RolloutField("phases", val);
        }

        public static RolloutField phases(RolloutPhaseTestData... elements) {
            return phases(new SetOfRolloutPhaseTestData(elements));
        }

        public static RolloutField countries(SetOfRolloutCountryTestData val) {
            return new RolloutField("countries", val);
        }

        public static RolloutField countries(RolloutCountryTestData... elements) {
            return countries(new SetOfRolloutCountryTestData(elements));
        }

        public static RolloutField dateCreated(DateTestData val) {
            return new RolloutField("dateCreated", val);
        }

        public static RolloutField dateCreated(DateField... fields) {
            return dateCreated(new DateTestData(fields));
        }

        public static RolloutField dateCreated(long val) {
            return dateCreated(DateField.value(val));
        }

        public static RolloutField lastUpdated(DateTestData val) {
            return new RolloutField("lastUpdated", val);
        }

        public static RolloutField lastUpdated(DateField... fields) {
            return lastUpdated(new DateTestData(fields));
        }

        public static RolloutField lastUpdated(long val) {
            return lastUpdated(DateField.value(val));
        }

        public static RolloutField createdBy(StringTestData val) {
            return new RolloutField("createdBy", val);
        }

        public static RolloutField createdBy(StringField... fields) {
            return createdBy(new StringTestData(fields));
        }

        public static RolloutField createdBy(String val) {
            return createdBy(StringField.value(val));
        }

        public static RolloutField updatedBy(StringTestData val) {
            return new RolloutField("updatedBy", val);
        }

        public static RolloutField updatedBy(StringField... fields) {
            return updatedBy(new StringTestData(fields));
        }

        public static RolloutField updatedBy(String val) {
            return updatedBy(StringField.value(val));
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("Rollout", 11, new PrimaryKey("Rollout", "rolloutId"));

    static {
        SCHEMA.addField("rolloutId", FieldType.LONG);
        SCHEMA.addField("movieId", FieldType.REFERENCE, "MovieId");
        SCHEMA.addField("rolloutName", FieldType.REFERENCE, "RolloutName");
        SCHEMA.addField("type", FieldType.REFERENCE, "RolloutType");
        SCHEMA.addField("status", FieldType.REFERENCE, "RolloutStatus");
        SCHEMA.addField("phases", FieldType.REFERENCE, "SetOfRolloutPhase");
        SCHEMA.addField("countries", FieldType.REFERENCE, "SetOfRolloutCountry");
        SCHEMA.addField("dateCreated", FieldType.REFERENCE, "Date");
        SCHEMA.addField("lastUpdated", FieldType.REFERENCE, "Date");
        SCHEMA.addField("createdBy", FieldType.REFERENCE, "String");
        SCHEMA.addField("updatedBy", FieldType.REFERENCE, "String");
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}
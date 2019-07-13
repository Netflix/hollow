package com.netflix.vms.transformer.data.gen.rollout;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.vms.transformer.data.gen.rollout.RolloutPhaseElementsTestData.RolloutPhaseElementsField;
import com.netflix.vms.transformer.data.gen.rollout.StringTestData.StringField;

public class RolloutPhaseTestData extends HollowTestObjectRecord {

    RolloutPhaseTestData(RolloutPhaseField... fields){
        super(fields);
    }

    public static RolloutPhaseTestData RolloutPhase(RolloutPhaseField... fields) {
        return new RolloutPhaseTestData(fields);
    }

    public RolloutPhaseTestData update(RolloutPhaseField... fields){
        super.addFields(fields);
        return this;
    }

    public long seasonMovieId() {
        Field f = super.getField("seasonMovieId");
        return f == null ? Long.MIN_VALUE : (Long)f.value;
    }

    public RolloutPhaseElementsTestData elements() {
        Field f = super.getField("elements");
        return f == null ? null : (RolloutPhaseElementsTestData)f.value;
    }

    public StringTestData nameRef() {
        Field f = super.getField("name");
        return f == null ? null : (StringTestData)f.value;
    }

    public String name() {
        Field f = super.getField("name");
        if(f == null) return null;
        StringTestData ref = (StringTestData)f.value;
        return ref.value();
    }

    public Boolean showCoreMetadata() {
        Field f = super.getField("showCoreMetadata");
        return f == null ? null : (Boolean)f.value;
    }

    public RolloutPhaseWindowMapTestData windows() {
        Field f = super.getField("windows");
        return f == null ? null : (RolloutPhaseWindowMapTestData)f.value;
    }

    public StringTestData phaseTypeRef() {
        Field f = super.getField("phaseType");
        return f == null ? null : (StringTestData)f.value;
    }

    public String phaseType() {
        Field f = super.getField("phaseType");
        if(f == null) return null;
        StringTestData ref = (StringTestData)f.value;
        return ref.value();
    }

    public Boolean onHold() {
        Field f = super.getField("onHold");
        return f == null ? null : (Boolean)f.value;
    }

    public static class RolloutPhaseField extends Field {

        private RolloutPhaseField(String name, Object val) { super(name, val); }

        public static RolloutPhaseField seasonMovieId(long val) {
            return new RolloutPhaseField("seasonMovieId", val);
        }

        public static RolloutPhaseField elements(RolloutPhaseElementsTestData val) {
            return new RolloutPhaseField("elements", val);
        }

        public static RolloutPhaseField elements(RolloutPhaseElementsField... fields) {
            return elements(new RolloutPhaseElementsTestData(fields));
        }

        public static RolloutPhaseField name(StringTestData val) {
            return new RolloutPhaseField("name", val);
        }

        public static RolloutPhaseField name(StringField... fields) {
            return name(new StringTestData(fields));
        }

        public static RolloutPhaseField name(String val) {
            return name(StringField.value(val));
        }

        public static RolloutPhaseField showCoreMetadata(boolean val) {
            return new RolloutPhaseField("showCoreMetadata", val);
        }

        public static RolloutPhaseField windows(RolloutPhaseWindowMapTestData val) {
            return new RolloutPhaseField("windows", val);
        }

        public static RolloutPhaseField windows(
                ISOCountryTestData key, RolloutPhaseWindowTestData value) {
            return windows(RolloutPhaseWindowMapTestData.RolloutPhaseWindowMap(key, value));
        }

        public static RolloutPhaseField windows(
                ISOCountryTestData key1, RolloutPhaseWindowTestData value1,
                ISOCountryTestData key2, RolloutPhaseWindowTestData value2) {
            return windows(RolloutPhaseWindowMapTestData.RolloutPhaseWindowMap(key1, value1, key2, value2));
        }

        public static RolloutPhaseField windows(
                ISOCountryTestData key1, RolloutPhaseWindowTestData value1,
                ISOCountryTestData key2, RolloutPhaseWindowTestData value2,
                ISOCountryTestData key3, RolloutPhaseWindowTestData value3) {
            return windows(RolloutPhaseWindowMapTestData.RolloutPhaseWindowMap(key1, value1, key2, value2, key3, value3));
        }

        public static RolloutPhaseField windows(
                ISOCountryTestData key1, RolloutPhaseWindowTestData value1,
                ISOCountryTestData key2, RolloutPhaseWindowTestData value2,
                ISOCountryTestData key3, RolloutPhaseWindowTestData value3,
                ISOCountryTestData key4, RolloutPhaseWindowTestData value4) {
            return windows(RolloutPhaseWindowMapTestData.RolloutPhaseWindowMap(key1, value1, key2, value2, key3, value3, key4, value4));
        }

        public static RolloutPhaseField windows(
                ISOCountryTestData key1, RolloutPhaseWindowTestData value1,
                ISOCountryTestData key2, RolloutPhaseWindowTestData value2,
                ISOCountryTestData key3, RolloutPhaseWindowTestData value3,
                ISOCountryTestData key4, RolloutPhaseWindowTestData value4,
                ISOCountryTestData key5, RolloutPhaseWindowTestData value5) {
            return windows(RolloutPhaseWindowMapTestData.RolloutPhaseWindowMap(key1, value1, key2, value2, key3, value3, key4, value4, key5, value5));
        }

        public static RolloutPhaseField phaseType(StringTestData val) {
            return new RolloutPhaseField("phaseType", val);
        }

        public static RolloutPhaseField phaseType(StringField... fields) {
            return phaseType(new StringTestData(fields));
        }

        public static RolloutPhaseField phaseType(String val) {
            return phaseType(StringField.value(val));
        }

        public static RolloutPhaseField onHold(boolean val) {
            return new RolloutPhaseField("onHold", val);
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("RolloutPhase", 7);

    static {
        SCHEMA.addField("seasonMovieId", FieldType.LONG);
        SCHEMA.addField("elements", FieldType.REFERENCE, "RolloutPhaseElements");
        SCHEMA.addField("name", FieldType.REFERENCE, "String");
        SCHEMA.addField("showCoreMetadata", FieldType.BOOLEAN);
        SCHEMA.addField("windows", FieldType.REFERENCE, "RolloutPhaseWindowMap");
        SCHEMA.addField("phaseType", FieldType.REFERENCE, "String");
        SCHEMA.addField("onHold", FieldType.BOOLEAN);
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}
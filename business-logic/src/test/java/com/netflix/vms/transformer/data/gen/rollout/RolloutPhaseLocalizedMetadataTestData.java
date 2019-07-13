package com.netflix.vms.transformer.data.gen.rollout;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.vms.transformer.data.gen.rollout.StringTestData.StringField;

public class RolloutPhaseLocalizedMetadataTestData extends HollowTestObjectRecord {

    RolloutPhaseLocalizedMetadataTestData(RolloutPhaseLocalizedMetadataField... fields){
        super(fields);
    }

    public static RolloutPhaseLocalizedMetadataTestData RolloutPhaseLocalizedMetadata(RolloutPhaseLocalizedMetadataField... fields) {
        return new RolloutPhaseLocalizedMetadataTestData(fields);
    }

    public RolloutPhaseLocalizedMetadataTestData update(RolloutPhaseLocalizedMetadataField... fields){
        super.addFields(fields);
        return this;
    }

    public StringTestData SUPPLEMENTAL_MESSAGERef() {
        Field f = super.getField("SUPPLEMENTAL_MESSAGE");
        return f == null ? null : (StringTestData)f.value;
    }

    public String SUPPLEMENTAL_MESSAGE() {
        Field f = super.getField("SUPPLEMENTAL_MESSAGE");
        if(f == null) return null;
        StringTestData ref = (StringTestData)f.value;
        return ref.value();
    }

    public StringTestData MERCH_OVERRIDE_MESSAGERef() {
        Field f = super.getField("MERCH_OVERRIDE_MESSAGE");
        return f == null ? null : (StringTestData)f.value;
    }

    public String MERCH_OVERRIDE_MESSAGE() {
        Field f = super.getField("MERCH_OVERRIDE_MESSAGE");
        if(f == null) return null;
        StringTestData ref = (StringTestData)f.value;
        return ref.value();
    }

    public StringTestData POSTPLAY_OVERRIDE_MESSAGERef() {
        Field f = super.getField("POSTPLAY_OVERRIDE_MESSAGE");
        return f == null ? null : (StringTestData)f.value;
    }

    public String POSTPLAY_OVERRIDE_MESSAGE() {
        Field f = super.getField("POSTPLAY_OVERRIDE_MESSAGE");
        if(f == null) return null;
        StringTestData ref = (StringTestData)f.value;
        return ref.value();
    }

    public StringTestData ODP_OVERRIDE_MESSAGERef() {
        Field f = super.getField("ODP_OVERRIDE_MESSAGE");
        return f == null ? null : (StringTestData)f.value;
    }

    public String ODP_OVERRIDE_MESSAGE() {
        Field f = super.getField("ODP_OVERRIDE_MESSAGE");
        if(f == null) return null;
        StringTestData ref = (StringTestData)f.value;
        return ref.value();
    }

    public StringTestData POSTPLAY_ALTRef() {
        Field f = super.getField("POSTPLAY_ALT");
        return f == null ? null : (StringTestData)f.value;
    }

    public String POSTPLAY_ALT() {
        Field f = super.getField("POSTPLAY_ALT");
        if(f == null) return null;
        StringTestData ref = (StringTestData)f.value;
        return ref.value();
    }

    public StringTestData POSTPLAY_COMPLETIONRef() {
        Field f = super.getField("POSTPLAY_COMPLETION");
        return f == null ? null : (StringTestData)f.value;
    }

    public String POSTPLAY_COMPLETION() {
        Field f = super.getField("POSTPLAY_COMPLETION");
        if(f == null) return null;
        StringTestData ref = (StringTestData)f.value;
        return ref.value();
    }

    public StringTestData TAGLINERef() {
        Field f = super.getField("TAGLINE");
        return f == null ? null : (StringTestData)f.value;
    }

    public String TAGLINE() {
        Field f = super.getField("TAGLINE");
        if(f == null) return null;
        StringTestData ref = (StringTestData)f.value;
        return ref.value();
    }

    public static class RolloutPhaseLocalizedMetadataField extends Field {

        private RolloutPhaseLocalizedMetadataField(String name, Object val) { super(name, val); }

        public static RolloutPhaseLocalizedMetadataField SUPPLEMENTAL_MESSAGE(StringTestData val) {
            return new RolloutPhaseLocalizedMetadataField("SUPPLEMENTAL_MESSAGE", val);
        }

        public static RolloutPhaseLocalizedMetadataField SUPPLEMENTAL_MESSAGE(StringField... fields) {
            return SUPPLEMENTAL_MESSAGE(new StringTestData(fields));
        }

        public static RolloutPhaseLocalizedMetadataField SUPPLEMENTAL_MESSAGE(String val) {
            return SUPPLEMENTAL_MESSAGE(StringField.value(val));
        }

        public static RolloutPhaseLocalizedMetadataField MERCH_OVERRIDE_MESSAGE(StringTestData val) {
            return new RolloutPhaseLocalizedMetadataField("MERCH_OVERRIDE_MESSAGE", val);
        }

        public static RolloutPhaseLocalizedMetadataField MERCH_OVERRIDE_MESSAGE(StringField... fields) {
            return MERCH_OVERRIDE_MESSAGE(new StringTestData(fields));
        }

        public static RolloutPhaseLocalizedMetadataField MERCH_OVERRIDE_MESSAGE(String val) {
            return MERCH_OVERRIDE_MESSAGE(StringField.value(val));
        }

        public static RolloutPhaseLocalizedMetadataField POSTPLAY_OVERRIDE_MESSAGE(StringTestData val) {
            return new RolloutPhaseLocalizedMetadataField("POSTPLAY_OVERRIDE_MESSAGE", val);
        }

        public static RolloutPhaseLocalizedMetadataField POSTPLAY_OVERRIDE_MESSAGE(StringField... fields) {
            return POSTPLAY_OVERRIDE_MESSAGE(new StringTestData(fields));
        }

        public static RolloutPhaseLocalizedMetadataField POSTPLAY_OVERRIDE_MESSAGE(String val) {
            return POSTPLAY_OVERRIDE_MESSAGE(StringField.value(val));
        }

        public static RolloutPhaseLocalizedMetadataField ODP_OVERRIDE_MESSAGE(StringTestData val) {
            return new RolloutPhaseLocalizedMetadataField("ODP_OVERRIDE_MESSAGE", val);
        }

        public static RolloutPhaseLocalizedMetadataField ODP_OVERRIDE_MESSAGE(StringField... fields) {
            return ODP_OVERRIDE_MESSAGE(new StringTestData(fields));
        }

        public static RolloutPhaseLocalizedMetadataField ODP_OVERRIDE_MESSAGE(String val) {
            return ODP_OVERRIDE_MESSAGE(StringField.value(val));
        }

        public static RolloutPhaseLocalizedMetadataField POSTPLAY_ALT(StringTestData val) {
            return new RolloutPhaseLocalizedMetadataField("POSTPLAY_ALT", val);
        }

        public static RolloutPhaseLocalizedMetadataField POSTPLAY_ALT(StringField... fields) {
            return POSTPLAY_ALT(new StringTestData(fields));
        }

        public static RolloutPhaseLocalizedMetadataField POSTPLAY_ALT(String val) {
            return POSTPLAY_ALT(StringField.value(val));
        }

        public static RolloutPhaseLocalizedMetadataField POSTPLAY_COMPLETION(StringTestData val) {
            return new RolloutPhaseLocalizedMetadataField("POSTPLAY_COMPLETION", val);
        }

        public static RolloutPhaseLocalizedMetadataField POSTPLAY_COMPLETION(StringField... fields) {
            return POSTPLAY_COMPLETION(new StringTestData(fields));
        }

        public static RolloutPhaseLocalizedMetadataField POSTPLAY_COMPLETION(String val) {
            return POSTPLAY_COMPLETION(StringField.value(val));
        }

        public static RolloutPhaseLocalizedMetadataField TAGLINE(StringTestData val) {
            return new RolloutPhaseLocalizedMetadataField("TAGLINE", val);
        }

        public static RolloutPhaseLocalizedMetadataField TAGLINE(StringField... fields) {
            return TAGLINE(new StringTestData(fields));
        }

        public static RolloutPhaseLocalizedMetadataField TAGLINE(String val) {
            return TAGLINE(StringField.value(val));
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("RolloutPhaseLocalizedMetadata", 7);

    static {
        SCHEMA.addField("SUPPLEMENTAL_MESSAGE", FieldType.REFERENCE, "String");
        SCHEMA.addField("MERCH_OVERRIDE_MESSAGE", FieldType.REFERENCE, "String");
        SCHEMA.addField("POSTPLAY_OVERRIDE_MESSAGE", FieldType.REFERENCE, "String");
        SCHEMA.addField("ODP_OVERRIDE_MESSAGE", FieldType.REFERENCE, "String");
        SCHEMA.addField("POSTPLAY_ALT", FieldType.REFERENCE, "String");
        SCHEMA.addField("POSTPLAY_COMPLETION", FieldType.REFERENCE, "String");
        SCHEMA.addField("TAGLINE", FieldType.REFERENCE, "String");
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}
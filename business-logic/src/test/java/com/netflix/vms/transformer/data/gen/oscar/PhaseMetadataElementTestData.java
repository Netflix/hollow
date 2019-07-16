package com.netflix.vms.transformer.data.gen.oscar;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

import com.netflix.vms.transformer.data.gen.oscar.DateTestData.DateField;
import com.netflix.vms.transformer.data.gen.oscar.StringTestData.StringField;

public class PhaseMetadataElementTestData extends HollowTestObjectRecord {

    PhaseMetadataElementTestData(PhaseMetadataElementField... fields){
        super(fields);
    }

    public static PhaseMetadataElementTestData PhaseMetadataElement(PhaseMetadataElementField... fields) {
        return new PhaseMetadataElementTestData(fields);
    }

    public PhaseMetadataElementTestData update(PhaseMetadataElementField... fields){
        super.addFields(fields);
        return this;
    }

    public long attributeId() {
        Field f = super.getField("attributeId");
        return f == null ? Long.MIN_VALUE : (Long)f.value;
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

    public static class PhaseMetadataElementField extends HollowTestObjectRecord.Field {

        private PhaseMetadataElementField(String name, Object val) { super(name, val); }

        public static PhaseMetadataElementField attributeId(long val) {
            return new PhaseMetadataElementField("attributeId", val);
        }

        public static PhaseMetadataElementField dateCreated(DateTestData val) {
            return new PhaseMetadataElementField("dateCreated", val);
        }

        public static PhaseMetadataElementField dateCreated(DateField... fields) {
            return dateCreated(new DateTestData(fields));
        }

        public static PhaseMetadataElementField dateCreated(long val) {
            return dateCreated(DateField.value(val));
        }

        public static PhaseMetadataElementField lastUpdated(DateTestData val) {
            return new PhaseMetadataElementField("lastUpdated", val);
        }

        public static PhaseMetadataElementField lastUpdated(DateField... fields) {
            return lastUpdated(new DateTestData(fields));
        }

        public static PhaseMetadataElementField lastUpdated(long val) {
            return lastUpdated(DateField.value(val));
        }

        public static PhaseMetadataElementField createdBy(StringTestData val) {
            return new PhaseMetadataElementField("createdBy", val);
        }

        public static PhaseMetadataElementField createdBy(StringField... fields) {
            return createdBy(new StringTestData(fields));
        }

        public static PhaseMetadataElementField createdBy(String val) {
            return createdBy(StringField.value(val));
        }

        public static PhaseMetadataElementField updatedBy(StringTestData val) {
            return new PhaseMetadataElementField("updatedBy", val);
        }

        public static PhaseMetadataElementField updatedBy(StringField... fields) {
            return updatedBy(new StringTestData(fields));
        }

        public static PhaseMetadataElementField updatedBy(String val) {
            return updatedBy(StringField.value(val));
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("PhaseMetadataElement", 5);

    static {
        SCHEMA.addField("attributeId", FieldType.LONG);
        SCHEMA.addField("dateCreated", FieldType.REFERENCE, "Date");
        SCHEMA.addField("lastUpdated", FieldType.REFERENCE, "Date");
        SCHEMA.addField("createdBy", FieldType.REFERENCE, "String");
        SCHEMA.addField("updatedBy", FieldType.REFERENCE, "String");
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}
package com.netflix.vms.transformer.data.gen.oscar;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

import com.netflix.vms.transformer.data.gen.oscar.DateTestData.DateField;
import com.netflix.vms.transformer.data.gen.oscar.PersonIdTestData.PersonIdField;
import com.netflix.vms.transformer.data.gen.oscar.StringTestData.StringField;

public class PhaseCastMemberTestData extends HollowTestObjectRecord {

    PhaseCastMemberTestData(PhaseCastMemberField... fields){
        super(fields);
    }

    public static PhaseCastMemberTestData PhaseCastMember(PhaseCastMemberField... fields) {
        return new PhaseCastMemberTestData(fields);
    }

    public PhaseCastMemberTestData update(PhaseCastMemberField... fields){
        super.addFields(fields);
        return this;
    }

    public PersonIdTestData personIdRef() {
        Field f = super.getField("personId");
        return f == null ? null : (PersonIdTestData)f.value;
    }

    public long personId() {
        Field f = super.getField("personId");
        if(f == null) return Long.MIN_VALUE;
        PersonIdTestData ref = (PersonIdTestData)f.value;
        return ref.value();
    }

    public int sequenceNumber() {
        Field f = super.getField("sequenceNumber");
        return f == null ? Integer.MIN_VALUE : (Integer)f.value;
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

    public static class PhaseCastMemberField extends HollowTestObjectRecord.Field {

        private PhaseCastMemberField(String name, Object val) { super(name, val); }

        public static PhaseCastMemberField personId(PersonIdTestData val) {
            return new PhaseCastMemberField("personId", val);
        }

        public static PhaseCastMemberField personId(PersonIdField... fields) {
            return personId(new PersonIdTestData(fields));
        }

        public static PhaseCastMemberField personId(long val) {
            return personId(PersonIdField.value(val));
        }

        public static PhaseCastMemberField sequenceNumber(int val) {
            return new PhaseCastMemberField("sequenceNumber", val);
        }

        public static PhaseCastMemberField dateCreated(DateTestData val) {
            return new PhaseCastMemberField("dateCreated", val);
        }

        public static PhaseCastMemberField dateCreated(DateField... fields) {
            return dateCreated(new DateTestData(fields));
        }

        public static PhaseCastMemberField dateCreated(long val) {
            return dateCreated(DateField.value(val));
        }

        public static PhaseCastMemberField lastUpdated(DateTestData val) {
            return new PhaseCastMemberField("lastUpdated", val);
        }

        public static PhaseCastMemberField lastUpdated(DateField... fields) {
            return lastUpdated(new DateTestData(fields));
        }

        public static PhaseCastMemberField lastUpdated(long val) {
            return lastUpdated(DateField.value(val));
        }

        public static PhaseCastMemberField createdBy(StringTestData val) {
            return new PhaseCastMemberField("createdBy", val);
        }

        public static PhaseCastMemberField createdBy(StringField... fields) {
            return createdBy(new StringTestData(fields));
        }

        public static PhaseCastMemberField createdBy(String val) {
            return createdBy(StringField.value(val));
        }

        public static PhaseCastMemberField updatedBy(StringTestData val) {
            return new PhaseCastMemberField("updatedBy", val);
        }

        public static PhaseCastMemberField updatedBy(StringField... fields) {
            return updatedBy(new StringTestData(fields));
        }

        public static PhaseCastMemberField updatedBy(String val) {
            return updatedBy(StringField.value(val));
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("PhaseCastMember", 6);

    static {
        SCHEMA.addField("personId", FieldType.REFERENCE, "PersonId");
        SCHEMA.addField("sequenceNumber", FieldType.INT);
        SCHEMA.addField("dateCreated", FieldType.REFERENCE, "Date");
        SCHEMA.addField("lastUpdated", FieldType.REFERENCE, "Date");
        SCHEMA.addField("createdBy", FieldType.REFERENCE, "String");
        SCHEMA.addField("updatedBy", FieldType.REFERENCE, "String");
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}
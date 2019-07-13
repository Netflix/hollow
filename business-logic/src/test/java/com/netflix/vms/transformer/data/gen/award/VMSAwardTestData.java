package com.netflix.vms.transformer.data.gen.award;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.vms.transformer.data.gen.award.StringTestData.StringField;

public class VMSAwardTestData extends HollowTestObjectRecord {

    VMSAwardTestData(VMSAwardField... fields){
        super(fields);
    }

    public static VMSAwardTestData VMSAward(VMSAwardField... fields) {
        return new VMSAwardTestData(fields);
    }

    public VMSAwardTestData update(VMSAwardField... fields){
        super.addFields(fields);
        return this;
    }

    public long awardId() {
        Field f = super.getField("awardId");
        return f == null ? Long.MIN_VALUE : (Long)f.value;
    }

    public StringTestData countryCodeRef() {
        Field f = super.getField("countryCode");
        return f == null ? null : (StringTestData)f.value;
    }

    public String countryCode() {
        Field f = super.getField("countryCode");
        if(f == null) return null;
        StringTestData ref = (StringTestData)f.value;
        return ref.value();
    }

    public long sequenceNumber() {
        Field f = super.getField("sequenceNumber");
        return f == null ? Long.MIN_VALUE : (Long)f.value;
    }

    public Boolean isMovieAward() {
        Field f = super.getField("isMovieAward");
        return f == null ? null : (Boolean)f.value;
    }

    public long festivalId() {
        Field f = super.getField("festivalId");
        return f == null ? Long.MIN_VALUE : (Long)f.value;
    }

    public static class VMSAwardField extends Field {

        private VMSAwardField(String name, Object val) { super(name, val); }

        public static VMSAwardField awardId(long val) {
            return new VMSAwardField("awardId", val);
        }

        public static VMSAwardField countryCode(StringTestData val) {
            return new VMSAwardField("countryCode", val);
        }

        public static VMSAwardField countryCode(StringField... fields) {
            return countryCode(new StringTestData(fields));
        }

        public static VMSAwardField countryCode(String val) {
            return countryCode(StringField.value(val));
        }

        public static VMSAwardField sequenceNumber(long val) {
            return new VMSAwardField("sequenceNumber", val);
        }

        public static VMSAwardField isMovieAward(boolean val) {
            return new VMSAwardField("isMovieAward", val);
        }

        public static VMSAwardField festivalId(long val) {
            return new VMSAwardField("festivalId", val);
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("VMSAward", 5, new PrimaryKey("VMSAward", "awardId"));

    static {
        SCHEMA.addField("awardId", FieldType.LONG);
        SCHEMA.addField("countryCode", FieldType.REFERENCE, "String");
        SCHEMA.addField("sequenceNumber", FieldType.LONG);
        SCHEMA.addField("isMovieAward", FieldType.BOOLEAN);
        SCHEMA.addField("festivalId", FieldType.LONG);
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}
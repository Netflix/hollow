package com.netflix.vms.transformer.data.gen.cupToken;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.vms.transformer.data.gen.cupToken.LongTestData.LongField;
import com.netflix.vms.transformer.data.gen.cupToken.StringTestData.StringField;

public class CinderCupTokenRecordTestData extends HollowTestObjectRecord {

    CinderCupTokenRecordTestData(CinderCupTokenRecordField... fields){
        super(fields);
    }

    public static CinderCupTokenRecordTestData CinderCupTokenRecord(CinderCupTokenRecordField... fields) {
        return new CinderCupTokenRecordTestData(fields);
    }

    public CinderCupTokenRecordTestData update(CinderCupTokenRecordField... fields){
        super.addFields(fields);
        return this;
    }

    public LongTestData movieIdRef() {
        Field f = super.getField("movieId");
        return f == null ? null : (LongTestData)f.value;
    }

    public long movieId() {
        Field f = super.getField("movieId");
        if(f == null) return Long.MIN_VALUE;
        LongTestData ref = (LongTestData)f.value;
        return ref.value();
    }

    public LongTestData dealIdRef() {
        Field f = super.getField("dealId");
        return f == null ? null : (LongTestData)f.value;
    }

    public long dealId() {
        Field f = super.getField("dealId");
        if(f == null) return Long.MIN_VALUE;
        LongTestData ref = (LongTestData)f.value;
        return ref.value();
    }

    public StringTestData cupTokenIdRef() {
        Field f = super.getField("cupTokenId");
        return f == null ? null : (StringTestData)f.value;
    }

    public String cupTokenId() {
        Field f = super.getField("cupTokenId");
        if(f == null) return null;
        StringTestData ref = (StringTestData)f.value;
        return ref.value();
    }

    public static class CinderCupTokenRecordField extends Field {

        private CinderCupTokenRecordField(String name, Object val) { super(name, val); }

        public static CinderCupTokenRecordField movieId(LongTestData val) {
            return new CinderCupTokenRecordField("movieId", val);
        }

        public static CinderCupTokenRecordField movieId(LongField... fields) {
            return movieId(new LongTestData(fields));
        }

        public static CinderCupTokenRecordField movieId(long val) {
            return movieId(LongField.value(val));
        }

        public static CinderCupTokenRecordField dealId(LongTestData val) {
            return new CinderCupTokenRecordField("dealId", val);
        }

        public static CinderCupTokenRecordField dealId(LongField... fields) {
            return dealId(new LongTestData(fields));
        }

        public static CinderCupTokenRecordField dealId(long val) {
            return dealId(LongField.value(val));
        }

        public static CinderCupTokenRecordField cupTokenId(StringTestData val) {
            return new CinderCupTokenRecordField("cupTokenId", val);
        }

        public static CinderCupTokenRecordField cupTokenId(StringField... fields) {
            return cupTokenId(new StringTestData(fields));
        }

        public static CinderCupTokenRecordField cupTokenId(String val) {
            return cupTokenId(StringField.value(val));
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("CinderCupTokenRecord", 3, new PrimaryKey("CinderCupTokenRecord", "movieId", "dealId"));

    static {
        SCHEMA.addField("movieId", FieldType.REFERENCE, "Long");
        SCHEMA.addField("dealId", FieldType.REFERENCE, "Long");
        SCHEMA.addField("cupTokenId", FieldType.REFERENCE, "String");
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}
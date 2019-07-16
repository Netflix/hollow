package com.netflix.vms.transformer.data.gen.flexds;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

import com.netflix.vms.transformer.data.gen.flexds.StringTestData.StringField;

public class AuditGroupTestData extends HollowTestObjectRecord {

    AuditGroupTestData(AuditGroupField... fields){
        super(fields);
    }

    public static AuditGroupTestData AuditGroup(AuditGroupField... fields) {
        return new AuditGroupTestData(fields);
    }

    public AuditGroupTestData update(AuditGroupField... fields){
        super.addFields(fields);
        return this;
    }

    public StringTestData userRef() {
        Field f = super.getField("user");
        return f == null ? null : (StringTestData)f.value;
    }

    public String user() {
        Field f = super.getField("user");
        if(f == null) return null;
        StringTestData ref = (StringTestData)f.value;
        return ref.value();
    }

    public StringTestData appRef() {
        Field f = super.getField("app");
        return f == null ? null : (StringTestData)f.value;
    }

    public String app() {
        Field f = super.getField("app");
        if(f == null) return null;
        StringTestData ref = (StringTestData)f.value;
        return ref.value();
    }

    public long timestamp() {
        Field f = super.getField("timestamp");
        return f == null ? Long.MIN_VALUE : (Long)f.value;
    }

    public int revision() {
        Field f = super.getField("revision");
        return f == null ? Integer.MIN_VALUE : (Integer)f.value;
    }

    public static class AuditGroupField extends HollowTestObjectRecord.Field {

        private AuditGroupField(String name, Object val) { super(name, val); }

        public static AuditGroupField user(StringTestData val) {
            return new AuditGroupField("user", val);
        }

        public static AuditGroupField user(StringField... fields) {
            return user(new StringTestData(fields));
        }

        public static AuditGroupField user(String val) {
            return user(StringField.value(val));
        }

        public static AuditGroupField app(StringTestData val) {
            return new AuditGroupField("app", val);
        }

        public static AuditGroupField app(StringField... fields) {
            return app(new StringTestData(fields));
        }

        public static AuditGroupField app(String val) {
            return app(StringField.value(val));
        }

        public static AuditGroupField timestamp(long val) {
            return new AuditGroupField("timestamp", val);
        }

        public static AuditGroupField revision(int val) {
            return new AuditGroupField("revision", val);
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("AuditGroup", 4);

    static {
        SCHEMA.addField("user", FieldType.REFERENCE, "String");
        SCHEMA.addField("app", FieldType.REFERENCE, "String");
        SCHEMA.addField("timestamp", FieldType.LONG);
        SCHEMA.addField("revision", FieldType.INT);
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}
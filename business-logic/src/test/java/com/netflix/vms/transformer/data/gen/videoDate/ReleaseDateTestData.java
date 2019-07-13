package com.netflix.vms.transformer.data.gen.videoDate;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.vms.transformer.data.gen.videoDate.StringTestData.StringField;

public class ReleaseDateTestData extends HollowTestObjectRecord {

    ReleaseDateTestData(ReleaseDateField... fields){
        super(fields);
    }

    public static ReleaseDateTestData ReleaseDate(ReleaseDateField... fields) {
        return new ReleaseDateTestData(fields);
    }

    public ReleaseDateTestData update(ReleaseDateField... fields){
        super.addFields(fields);
        return this;
    }

    public StringTestData releaseDateTypeRef() {
        Field f = super.getField("releaseDateType");
        return f == null ? null : (StringTestData)f.value;
    }

    public String releaseDateType() {
        Field f = super.getField("releaseDateType");
        if(f == null) return null;
        StringTestData ref = (StringTestData)f.value;
        return ref.value();
    }

    public StringTestData distributorNameRef() {
        Field f = super.getField("distributorName");
        return f == null ? null : (StringTestData)f.value;
    }

    public String distributorName() {
        Field f = super.getField("distributorName");
        if(f == null) return null;
        StringTestData ref = (StringTestData)f.value;
        return ref.value();
    }

    public int month() {
        Field f = super.getField("month");
        return f == null ? Integer.MIN_VALUE : (Integer)f.value;
    }

    public int year() {
        Field f = super.getField("year");
        return f == null ? Integer.MIN_VALUE : (Integer)f.value;
    }

    public int day() {
        Field f = super.getField("day");
        return f == null ? Integer.MIN_VALUE : (Integer)f.value;
    }

    public StringTestData bcp47codeRef() {
        Field f = super.getField("bcp47code");
        return f == null ? null : (StringTestData)f.value;
    }

    public String bcp47code() {
        Field f = super.getField("bcp47code");
        if(f == null) return null;
        StringTestData ref = (StringTestData)f.value;
        return ref.value();
    }

    public static class ReleaseDateField extends Field {

        private ReleaseDateField(String name, Object val) { super(name, val); }

        public static ReleaseDateField releaseDateType(StringTestData val) {
            return new ReleaseDateField("releaseDateType", val);
        }

        public static ReleaseDateField releaseDateType(StringField... fields) {
            return releaseDateType(new StringTestData(fields));
        }

        public static ReleaseDateField releaseDateType(String val) {
            return releaseDateType(StringField.value(val));
        }

        public static ReleaseDateField distributorName(StringTestData val) {
            return new ReleaseDateField("distributorName", val);
        }

        public static ReleaseDateField distributorName(StringField... fields) {
            return distributorName(new StringTestData(fields));
        }

        public static ReleaseDateField distributorName(String val) {
            return distributorName(StringField.value(val));
        }

        public static ReleaseDateField month(int val) {
            return new ReleaseDateField("month", val);
        }

        public static ReleaseDateField year(int val) {
            return new ReleaseDateField("year", val);
        }

        public static ReleaseDateField day(int val) {
            return new ReleaseDateField("day", val);
        }

        public static ReleaseDateField bcp47code(StringTestData val) {
            return new ReleaseDateField("bcp47code", val);
        }

        public static ReleaseDateField bcp47code(StringField... fields) {
            return bcp47code(new StringTestData(fields));
        }

        public static ReleaseDateField bcp47code(String val) {
            return bcp47code(StringField.value(val));
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("ReleaseDate", 6);

    static {
        SCHEMA.addField("releaseDateType", FieldType.REFERENCE, "String");
        SCHEMA.addField("distributorName", FieldType.REFERENCE, "String");
        SCHEMA.addField("month", FieldType.INT);
        SCHEMA.addField("year", FieldType.INT);
        SCHEMA.addField("day", FieldType.INT);
        SCHEMA.addField("bcp47code", FieldType.REFERENCE, "String");
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}
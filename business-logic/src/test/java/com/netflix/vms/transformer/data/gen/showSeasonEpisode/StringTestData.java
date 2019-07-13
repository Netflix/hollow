package com.netflix.vms.transformer.data.gen.showSeasonEpisode;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

public class StringTestData extends HollowTestObjectRecord {

    StringTestData(StringField... fields){
        super(fields);
    }

    public static StringTestData String(StringField... fields) {
        return new StringTestData(fields);
    }

    public static StringTestData String(String val) {
        return String(StringField.value(val));
    }

    public StringTestData update(StringField... fields){
        super.addFields(fields);
        return this;
    }

    public String value() {
        Field f = super.getField("value");
        return f == null ? null : (String)f.value;
    }

    public static class StringField extends Field {

        private StringField(String name, Object val) { super(name, val); }

        public static StringField value(String val) {
            return new StringField("value", val);
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("String", 1);

    static {
        SCHEMA.addField("value", FieldType.STRING);
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}
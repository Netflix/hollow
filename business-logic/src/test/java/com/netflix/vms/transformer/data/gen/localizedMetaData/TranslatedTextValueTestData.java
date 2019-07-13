package com.netflix.vms.transformer.data.gen.localizedMetaData;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.vms.transformer.data.gen.localizedMetaData.StringTestData.StringField;

public class TranslatedTextValueTestData extends HollowTestObjectRecord {

    TranslatedTextValueTestData(TranslatedTextValueField... fields){
        super(fields);
    }

    public static TranslatedTextValueTestData TranslatedTextValue(TranslatedTextValueField... fields) {
        return new TranslatedTextValueTestData(fields);
    }

    public TranslatedTextValueTestData update(TranslatedTextValueField... fields){
        super.addFields(fields);
        return this;
    }

    public StringTestData valueRef() {
        Field f = super.getField("value");
        return f == null ? null : (StringTestData)f.value;
    }

    public String value() {
        Field f = super.getField("value");
        if(f == null) return null;
        StringTestData ref = (StringTestData)f.value;
        return ref.value();
    }

    public static class TranslatedTextValueField extends Field {

        private TranslatedTextValueField(String name, Object val) { super(name, val); }

        public static TranslatedTextValueField value(StringTestData val) {
            return new TranslatedTextValueField("value", val);
        }

        public static TranslatedTextValueField value(StringField... fields) {
            return value(new StringTestData(fields));
        }

        public static TranslatedTextValueField value(String val) {
            return value(StringField.value(val));
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("TranslatedTextValue", 1);

    static {
        SCHEMA.addField("value", FieldType.REFERENCE, "String");
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}
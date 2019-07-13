package com.netflix.vms.transformer.data.gen.localizedMetaData;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.vms.transformer.data.gen.localizedMetaData.StringTestData.StringField;

public class LocalizedMetaDataTestData extends HollowTestObjectRecord {

    LocalizedMetaDataTestData(LocalizedMetadataField... fields){
        super(fields);
    }

    public static LocalizedMetaDataTestData LocalizedMetadata(LocalizedMetadataField... fields) {
        return new LocalizedMetaDataTestData(fields);
    }

    public LocalizedMetaDataTestData update(LocalizedMetadataField... fields){
        super.addFields(fields);
        return this;
    }

    public long movieId() {
        Field f = super.getField("movieId");
        return f == null ? Long.MIN_VALUE : (Long)f.value;
    }

    public StringTestData attributeNameRef() {
        Field f = super.getField("attributeName");
        return f == null ? null : (StringTestData)f.value;
    }

    public String attributeName() {
        Field f = super.getField("attributeName");
        if(f == null) return null;
        StringTestData ref = (StringTestData)f.value;
        return ref.value();
    }

    public StringTestData labelRef() {
        Field f = super.getField("label");
        return f == null ? null : (StringTestData)f.value;
    }

    public String label() {
        Field f = super.getField("label");
        if(f == null) return null;
        StringTestData ref = (StringTestData)f.value;
        return ref.value();
    }

    public MapOfTranslatedTextTestData translatedTexts() {
        Field f = super.getField("translatedTexts");
        return f == null ? null : (MapOfTranslatedTextTestData)f.value;
    }

    public static class LocalizedMetadataField extends Field {

        private LocalizedMetadataField(String name, Object val) { super(name, val); }

        public static LocalizedMetadataField movieId(long val) {
            return new LocalizedMetadataField("movieId", val);
        }

        public static LocalizedMetadataField attributeName(StringTestData val) {
            return new LocalizedMetadataField("attributeName", val);
        }

        public static LocalizedMetadataField attributeName(StringField... fields) {
            return attributeName(new StringTestData(fields));
        }

        public static LocalizedMetadataField attributeName(String val) {
            return attributeName(StringField.value(val));
        }

        public static LocalizedMetadataField label(StringTestData val) {
            return new LocalizedMetadataField("label", val);
        }

        public static LocalizedMetadataField label(StringField... fields) {
            return label(new StringTestData(fields));
        }

        public static LocalizedMetadataField label(String val) {
            return label(StringField.value(val));
        }

        public static LocalizedMetadataField translatedTexts(MapOfTranslatedTextTestData val) {
            return new LocalizedMetadataField("translatedTexts", val);
        }

        public static LocalizedMetadataField translatedTexts(
                MapKeyTestData key, TranslatedTextValueTestData value) {
            return translatedTexts(MapOfTranslatedTextTestData.MapOfTranslatedText(key, value));
        }

        public static LocalizedMetadataField translatedTexts(
                MapKeyTestData key1, TranslatedTextValueTestData value1,
                MapKeyTestData key2, TranslatedTextValueTestData value2) {
            return translatedTexts(MapOfTranslatedTextTestData.MapOfTranslatedText(key1, value1, key2, value2));
        }

        public static LocalizedMetadataField translatedTexts(
                MapKeyTestData key1, TranslatedTextValueTestData value1,
                MapKeyTestData key2, TranslatedTextValueTestData value2,
                MapKeyTestData key3, TranslatedTextValueTestData value3) {
            return translatedTexts(MapOfTranslatedTextTestData.MapOfTranslatedText(key1, value1, key2, value2, key3, value3));
        }

        public static LocalizedMetadataField translatedTexts(
                MapKeyTestData key1, TranslatedTextValueTestData value1,
                MapKeyTestData key2, TranslatedTextValueTestData value2,
                MapKeyTestData key3, TranslatedTextValueTestData value3,
                MapKeyTestData key4, TranslatedTextValueTestData value4) {
            return translatedTexts(MapOfTranslatedTextTestData.MapOfTranslatedText(key1, value1, key2, value2, key3, value3, key4, value4));
        }

        public static LocalizedMetadataField translatedTexts(
                MapKeyTestData key1, TranslatedTextValueTestData value1,
                MapKeyTestData key2, TranslatedTextValueTestData value2,
                MapKeyTestData key3, TranslatedTextValueTestData value3,
                MapKeyTestData key4, TranslatedTextValueTestData value4,
                MapKeyTestData key5, TranslatedTextValueTestData value5) {
            return translatedTexts(MapOfTranslatedTextTestData.MapOfTranslatedText(key1, value1, key2, value2, key3, value3, key4, value4, key5, value5));
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("LocalizedMetadata", 4, new PrimaryKey("LocalizedMetadata", "movieId", "attributeName", "label"));

    static {
        SCHEMA.addField("movieId", FieldType.LONG);
        SCHEMA.addField("attributeName", FieldType.REFERENCE, "String");
        SCHEMA.addField("label", FieldType.REFERENCE, "String");
        SCHEMA.addField("translatedTexts", FieldType.REFERENCE, "MapOfTranslatedText");
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}
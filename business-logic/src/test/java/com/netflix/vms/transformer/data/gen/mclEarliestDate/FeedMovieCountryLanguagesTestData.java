package com.netflix.vms.transformer.data.gen.mclEarliestDate;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.vms.transformer.data.gen.mclEarliestDate.LongTestData.LongField;
import com.netflix.vms.transformer.data.gen.mclEarliestDate.StringTestData.StringField;

public class FeedMovieCountryLanguagesTestData extends HollowTestObjectRecord {

    FeedMovieCountryLanguagesTestData(FeedMovieCountryLanguagesField... fields){
        super(fields);
    }

    public static FeedMovieCountryLanguagesTestData FeedMovieCountryLanguages(FeedMovieCountryLanguagesField... fields) {
        return new FeedMovieCountryLanguagesTestData(fields);
    }

    public FeedMovieCountryLanguagesTestData update(FeedMovieCountryLanguagesField... fields){
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

    public MapOfStringToLongTestData languageToEarliestWindowStartDateMap() {
        Field f = super.getField("languageToEarliestWindowStartDateMap");
        return f == null ? null : (MapOfStringToLongTestData)f.value;
    }

    public static class FeedMovieCountryLanguagesField extends Field {

        private FeedMovieCountryLanguagesField(String name, Object val) { super(name, val); }

        public static FeedMovieCountryLanguagesField movieId(LongTestData val) {
            return new FeedMovieCountryLanguagesField("movieId", val);
        }

        public static FeedMovieCountryLanguagesField movieId(LongField... fields) {
            return movieId(new LongTestData(fields));
        }

        public static FeedMovieCountryLanguagesField movieId(long val) {
            return movieId(LongField.value(val));
        }

        public static FeedMovieCountryLanguagesField countryCode(StringTestData val) {
            return new FeedMovieCountryLanguagesField("countryCode", val);
        }

        public static FeedMovieCountryLanguagesField countryCode(StringField... fields) {
            return countryCode(new StringTestData(fields));
        }

        public static FeedMovieCountryLanguagesField countryCode(String val) {
            return countryCode(StringField.value(val));
        }

        public static FeedMovieCountryLanguagesField languageToEarliestWindowStartDateMap(MapOfStringToLongTestData val) {
            return new FeedMovieCountryLanguagesField("languageToEarliestWindowStartDateMap", val);
        }

        public static FeedMovieCountryLanguagesField languageToEarliestWindowStartDateMap(
                StringTestData key, LongTestData value) {
            return languageToEarliestWindowStartDateMap(MapOfStringToLongTestData.MapOfStringToLong(key, value));
        }

        public static FeedMovieCountryLanguagesField languageToEarliestWindowStartDateMap(
                StringTestData key1, LongTestData value1,
                StringTestData key2, LongTestData value2) {
            return languageToEarliestWindowStartDateMap(MapOfStringToLongTestData.MapOfStringToLong(key1, value1, key2, value2));
        }

        public static FeedMovieCountryLanguagesField languageToEarliestWindowStartDateMap(
                StringTestData key1, LongTestData value1,
                StringTestData key2, LongTestData value2,
                StringTestData key3, LongTestData value3) {
            return languageToEarliestWindowStartDateMap(MapOfStringToLongTestData.MapOfStringToLong(key1, value1, key2, value2, key3, value3));
        }

        public static FeedMovieCountryLanguagesField languageToEarliestWindowStartDateMap(
                StringTestData key1, LongTestData value1,
                StringTestData key2, LongTestData value2,
                StringTestData key3, LongTestData value3,
                StringTestData key4, LongTestData value4) {
            return languageToEarliestWindowStartDateMap(MapOfStringToLongTestData.MapOfStringToLong(key1, value1, key2, value2, key3, value3, key4, value4));
        }

        public static FeedMovieCountryLanguagesField languageToEarliestWindowStartDateMap(
                StringTestData key1, LongTestData value1,
                StringTestData key2, LongTestData value2,
                StringTestData key3, LongTestData value3,
                StringTestData key4, LongTestData value4,
                StringTestData key5, LongTestData value5) {
            return languageToEarliestWindowStartDateMap(MapOfStringToLongTestData.MapOfStringToLong(key1, value1, key2, value2, key3, value3, key4, value4, key5, value5));
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("FeedMovieCountryLanguages", 3, new PrimaryKey("FeedMovieCountryLanguages", "movieId", "countryCode"));

    static {
        SCHEMA.addField("movieId", FieldType.REFERENCE, "Long");
        SCHEMA.addField("countryCode", FieldType.REFERENCE, "String");
        SCHEMA.addField("languageToEarliestWindowStartDateMap", FieldType.REFERENCE, "MapOfStringToLong");
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}
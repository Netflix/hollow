package com.netflix.vms.transformer.data.gen.showSeasonEpisode;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.vms.transformer.data.gen.showSeasonEpisode.StringTestData.StringField;

public class ShowSeasonEpisodeTestData extends HollowTestObjectRecord {

    ShowSeasonEpisodeTestData(ShowSeasonEpisodeField... fields){
        super(fields);
    }

    public static ShowSeasonEpisodeTestData ShowSeasonEpisode(ShowSeasonEpisodeField... fields) {
        return new ShowSeasonEpisodeTestData(fields);
    }

    public ShowSeasonEpisodeTestData update(ShowSeasonEpisodeField... fields){
        super.addFields(fields);
        return this;
    }

    public long movieId() {
        Field f = super.getField("movieId");
        return f == null ? Long.MIN_VALUE : (Long)f.value;
    }

    public long displaySetId() {
        Field f = super.getField("displaySetId");
        return f == null ? Long.MIN_VALUE : (Long)f.value;
    }

    public ISOCountryListTestData countryCodes() {
        Field f = super.getField("countryCodes");
        return f == null ? null : (ISOCountryListTestData)f.value;
    }

    public SeasonListTestData seasons() {
        Field f = super.getField("seasons");
        return f == null ? null : (SeasonListTestData)f.value;
    }

    public Boolean hideSeasonNumbers() {
        Field f = super.getField("hideSeasonNumbers");
        return f == null ? null : (Boolean)f.value;
    }

    public Boolean episodicNewBadge() {
        Field f = super.getField("episodicNewBadge");
        return f == null ? null : (Boolean)f.value;
    }

    public StringTestData merchOrderRef() {
        Field f = super.getField("merchOrder");
        return f == null ? null : (StringTestData)f.value;
    }

    public String merchOrder() {
        Field f = super.getField("merchOrder");
        if(f == null) return null;
        StringTestData ref = (StringTestData)f.value;
        return ref.value();
    }

    public static class ShowSeasonEpisodeField extends Field {

        private ShowSeasonEpisodeField(String name, Object val) { super(name, val); }

        public static ShowSeasonEpisodeField movieId(long val) {
            return new ShowSeasonEpisodeField("movieId", val);
        }

        public static ShowSeasonEpisodeField displaySetId(long val) {
            return new ShowSeasonEpisodeField("displaySetId", val);
        }

        public static ShowSeasonEpisodeField countryCodes(ISOCountryListTestData val) {
            return new ShowSeasonEpisodeField("countryCodes", val);
        }

        public static ShowSeasonEpisodeField countryCodes(ISOCountryTestData... elements) {
            return countryCodes(new ISOCountryListTestData(elements));
        }

        public static ShowSeasonEpisodeField seasons(SeasonListTestData val) {
            return new ShowSeasonEpisodeField("seasons", val);
        }

        public static ShowSeasonEpisodeField seasons(SeasonTestData... elements) {
            return seasons(new SeasonListTestData(elements));
        }

        public static ShowSeasonEpisodeField hideSeasonNumbers(boolean val) {
            return new ShowSeasonEpisodeField("hideSeasonNumbers", val);
        }

        public static ShowSeasonEpisodeField episodicNewBadge(boolean val) {
            return new ShowSeasonEpisodeField("episodicNewBadge", val);
        }

        public static ShowSeasonEpisodeField merchOrder(StringTestData val) {
            return new ShowSeasonEpisodeField("merchOrder", val);
        }

        public static ShowSeasonEpisodeField merchOrder(StringField... fields) {
            return merchOrder(new StringTestData(fields));
        }

        public static ShowSeasonEpisodeField merchOrder(String val) {
            return merchOrder(StringField.value(val));
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("ShowSeasonEpisode", 7, new PrimaryKey("ShowSeasonEpisode", "movieId", "displaySetId"));

    static {
        SCHEMA.addField("movieId", FieldType.LONG);
        SCHEMA.addField("displaySetId", FieldType.LONG);
        SCHEMA.addField("countryCodes", FieldType.REFERENCE, "ISOCountryList");
        SCHEMA.addField("seasons", FieldType.REFERENCE, "SeasonList");
        SCHEMA.addField("hideSeasonNumbers", FieldType.BOOLEAN);
        SCHEMA.addField("episodicNewBadge", FieldType.BOOLEAN);
        SCHEMA.addField("merchOrder", FieldType.REFERENCE, "String");
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}
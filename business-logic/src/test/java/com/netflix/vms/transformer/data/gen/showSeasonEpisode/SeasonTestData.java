package com.netflix.vms.transformer.data.gen.showSeasonEpisode;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.vms.transformer.data.gen.showSeasonEpisode.StringTestData.StringField;

public class SeasonTestData extends HollowTestObjectRecord {

    SeasonTestData(SeasonField... fields){
        super(fields);
    }

    public static SeasonTestData Season(SeasonField... fields) {
        return new SeasonTestData(fields);
    }

    public SeasonTestData update(SeasonField... fields){
        super.addFields(fields);
        return this;
    }

    public int sequenceNumber() {
        Field f = super.getField("sequenceNumber");
        return f == null ? Integer.MIN_VALUE : (Integer)f.value;
    }

    public long movieId() {
        Field f = super.getField("movieId");
        return f == null ? Long.MIN_VALUE : (Long)f.value;
    }

    public EpisodeListTestData episodes() {
        Field f = super.getField("episodes");
        return f == null ? null : (EpisodeListTestData)f.value;
    }

    public Boolean hideEpisodeNumbers() {
        Field f = super.getField("hideEpisodeNumbers");
        return f == null ? null : (Boolean)f.value;
    }

    public Boolean episodicNewBadge() {
        Field f = super.getField("episodicNewBadge");
        return f == null ? null : (Boolean)f.value;
    }

    public int episodeSkipping() {
        Field f = super.getField("episodeSkipping");
        return f == null ? Integer.MIN_VALUE : (Integer)f.value;
    }

    public Boolean filterUnavailableEpisodes() {
        Field f = super.getField("filterUnavailableEpisodes");
        return f == null ? null : (Boolean)f.value;
    }

    public Boolean useLatestEpisodeAsDefault() {
        Field f = super.getField("useLatestEpisodeAsDefault");
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

    public static class SeasonField extends Field {

        private SeasonField(String name, Object val) { super(name, val); }

        public static SeasonField sequenceNumber(int val) {
            return new SeasonField("sequenceNumber", val);
        }

        public static SeasonField movieId(long val) {
            return new SeasonField("movieId", val);
        }

        public static SeasonField episodes(EpisodeListTestData val) {
            return new SeasonField("episodes", val);
        }

        public static SeasonField episodes(EpisodeTestData... elements) {
            return episodes(new EpisodeListTestData(elements));
        }

        public static SeasonField hideEpisodeNumbers(boolean val) {
            return new SeasonField("hideEpisodeNumbers", val);
        }

        public static SeasonField episodicNewBadge(boolean val) {
            return new SeasonField("episodicNewBadge", val);
        }

        public static SeasonField episodeSkipping(int val) {
            return new SeasonField("episodeSkipping", val);
        }

        public static SeasonField filterUnavailableEpisodes(boolean val) {
            return new SeasonField("filterUnavailableEpisodes", val);
        }

        public static SeasonField useLatestEpisodeAsDefault(boolean val) {
            return new SeasonField("useLatestEpisodeAsDefault", val);
        }

        public static SeasonField merchOrder(StringTestData val) {
            return new SeasonField("merchOrder", val);
        }

        public static SeasonField merchOrder(StringField... fields) {
            return merchOrder(new StringTestData(fields));
        }

        public static SeasonField merchOrder(String val) {
            return merchOrder(StringField.value(val));
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("Season", 9);

    static {
        SCHEMA.addField("sequenceNumber", FieldType.INT);
        SCHEMA.addField("movieId", FieldType.LONG);
        SCHEMA.addField("episodes", FieldType.REFERENCE, "EpisodeList");
        SCHEMA.addField("hideEpisodeNumbers", FieldType.BOOLEAN);
        SCHEMA.addField("episodicNewBadge", FieldType.BOOLEAN);
        SCHEMA.addField("episodeSkipping", FieldType.INT);
        SCHEMA.addField("filterUnavailableEpisodes", FieldType.BOOLEAN);
        SCHEMA.addField("useLatestEpisodeAsDefault", FieldType.BOOLEAN);
        SCHEMA.addField("merchOrder", FieldType.REFERENCE, "String");
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}
package com.netflix.vms.transformer.data.gen.showSeasonEpisode;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

public class EpisodeTestData extends HollowTestObjectRecord {

    EpisodeTestData(EpisodeField... fields){
        super(fields);
    }

    public static EpisodeTestData Episode(EpisodeField... fields) {
        return new EpisodeTestData(fields);
    }

    public EpisodeTestData update(EpisodeField... fields){
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

    public Boolean midSeason() {
        Field f = super.getField("midSeason");
        return f == null ? null : (Boolean)f.value;
    }

    public Boolean seasonFinale() {
        Field f = super.getField("seasonFinale");
        return f == null ? null : (Boolean)f.value;
    }

    public Boolean showFinale() {
        Field f = super.getField("showFinale");
        return f == null ? null : (Boolean)f.value;
    }

    public static class EpisodeField extends Field {

        private EpisodeField(String name, Object val) { super(name, val); }

        public static EpisodeField sequenceNumber(int val) {
            return new EpisodeField("sequenceNumber", val);
        }

        public static EpisodeField movieId(long val) {
            return new EpisodeField("movieId", val);
        }

        public static EpisodeField midSeason(boolean val) {
            return new EpisodeField("midSeason", val);
        }

        public static EpisodeField seasonFinale(boolean val) {
            return new EpisodeField("seasonFinale", val);
        }

        public static EpisodeField showFinale(boolean val) {
            return new EpisodeField("showFinale", val);
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("Episode", 5);

    static {
        SCHEMA.addField("sequenceNumber", FieldType.INT);
        SCHEMA.addField("movieId", FieldType.LONG);
        SCHEMA.addField("midSeason", FieldType.BOOLEAN);
        SCHEMA.addField("seasonFinale", FieldType.BOOLEAN);
        SCHEMA.addField("showFinale", FieldType.BOOLEAN);
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}
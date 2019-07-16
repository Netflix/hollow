package com.netflix.vms.transformer.data.gen.oscar;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

import com.netflix.vms.transformer.data.gen.oscar.MovieIdTestData.MovieIdField;
import com.netflix.vms.transformer.data.gen.oscar.MovieTitleTypeTestData.MovieTitleTypeField;

public class MovieTitleTestData extends HollowTestObjectRecord {

    MovieTitleTestData(MovieTitleField... fields){
        super(fields);
    }

    public static MovieTitleTestData MovieTitle(MovieTitleField... fields) {
        return new MovieTitleTestData(fields);
    }

    public MovieTitleTestData update(MovieTitleField... fields){
        super.addFields(fields);
        return this;
    }

    public MovieIdTestData movieIdRef() {
        Field f = super.getField("movieId");
        return f == null ? null : (MovieIdTestData)f.value;
    }

    public long movieId() {
        Field f = super.getField("movieId");
        if(f == null) return Long.MIN_VALUE;
        MovieIdTestData ref = (MovieIdTestData)f.value;
        return ref.value();
    }

    public MovieTitleTypeTestData typeRef() {
        Field f = super.getField("type");
        return f == null ? null : (MovieTitleTypeTestData)f.value;
    }

    public String type() {
        Field f = super.getField("type");
        if(f == null) return null;
        MovieTitleTypeTestData ref = (MovieTitleTypeTestData)f.value;
        return ref._name();
    }

    public static class MovieTitleField extends HollowTestObjectRecord.Field {

        private MovieTitleField(String name, Object val) { super(name, val); }

        public static MovieTitleField movieId(MovieIdTestData val) {
            return new MovieTitleField("movieId", val);
        }

        public static MovieTitleField movieId(MovieIdField... fields) {
            return movieId(new MovieIdTestData(fields));
        }

        public static MovieTitleField movieId(long val) {
            return movieId(MovieIdField.value(val));
        }

        public static MovieTitleField type(MovieTitleTypeTestData val) {
            return new MovieTitleField("type", val);
        }

        public static MovieTitleField type(MovieTitleTypeField... fields) {
            return type(new MovieTitleTypeTestData(fields));
        }

        public static MovieTitleField type(String val) {
            return type(MovieTitleTypeField._name(val));
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("MovieTitle", 2, new PrimaryKey("MovieTitle", "movieId", "type"));

    static {
        SCHEMA.addField("movieId", FieldType.REFERENCE, "MovieId");
        SCHEMA.addField("type", FieldType.REFERENCE, "MovieTitleType");
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}
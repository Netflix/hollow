package com.netflix.vms.transformer.data.gen.oscar;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

public class MovieReleaseTypeTestData extends HollowTestObjectRecord {

    MovieReleaseTypeTestData(MovieReleaseTypeField... fields){
        super(fields);
    }

    public static MovieReleaseTypeTestData MovieReleaseType(MovieReleaseTypeField... fields) {
        return new MovieReleaseTypeTestData(fields);
    }

    public static MovieReleaseTypeTestData MovieReleaseType(String val) {
        return MovieReleaseType(MovieReleaseTypeField._name(val));
    }

    public MovieReleaseTypeTestData update(MovieReleaseTypeField... fields){
        super.addFields(fields);
        return this;
    }

    public String _name() {
        Field f = super.getField("_name");
        return f == null ? null : (String)f.value;
    }

    public static class MovieReleaseTypeField extends HollowTestObjectRecord.Field {

        private MovieReleaseTypeField(String name, Object val) { super(name, val); }

        public static MovieReleaseTypeField _name(String val) {
            return new MovieReleaseTypeField("_name", val);
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("MovieReleaseType", 1);

    static {
        SCHEMA.addField("_name", FieldType.STRING);
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}
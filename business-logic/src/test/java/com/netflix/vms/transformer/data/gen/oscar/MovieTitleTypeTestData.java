package com.netflix.vms.transformer.data.gen.oscar;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

public class MovieTitleTypeTestData extends HollowTestObjectRecord {

    MovieTitleTypeTestData(MovieTitleTypeField... fields){
        super(fields);
    }

    public static MovieTitleTypeTestData MovieTitleType(MovieTitleTypeField... fields) {
        return new MovieTitleTypeTestData(fields);
    }

    public static MovieTitleTypeTestData MovieTitleType(String val) {
        return MovieTitleType(MovieTitleTypeField._name(val));
    }

    public MovieTitleTypeTestData update(MovieTitleTypeField... fields){
        super.addFields(fields);
        return this;
    }

    public String _name() {
        Field f = super.getField("_name");
        return f == null ? null : (String)f.value;
    }

    public static class MovieTitleTypeField extends HollowTestObjectRecord.Field {

        private MovieTitleTypeField(String name, Object val) { super(name, val); }

        public static MovieTitleTypeField _name(String val) {
            return new MovieTitleTypeField("_name", val);
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("MovieTitleType", 1);

    static {
        SCHEMA.addField("_name", FieldType.STRING);
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}
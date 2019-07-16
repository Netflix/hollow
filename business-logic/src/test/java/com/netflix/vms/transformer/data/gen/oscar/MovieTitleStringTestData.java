package com.netflix.vms.transformer.data.gen.oscar;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

public class MovieTitleStringTestData extends HollowTestObjectRecord {

    MovieTitleStringTestData(MovieTitleStringField... fields){
        super(fields);
    }

    public static MovieTitleStringTestData MovieTitleString(MovieTitleStringField... fields) {
        return new MovieTitleStringTestData(fields);
    }

    public static MovieTitleStringTestData MovieTitleString(String val) {
        return MovieTitleString(MovieTitleStringField.value(val));
    }

    public MovieTitleStringTestData update(MovieTitleStringField... fields){
        super.addFields(fields);
        return this;
    }

    public String value() {
        Field f = super.getField("value");
        return f == null ? null : (String)f.value;
    }

    public static class MovieTitleStringField extends HollowTestObjectRecord.Field {

        private MovieTitleStringField(String name, Object val) { super(name, val); }

        public static MovieTitleStringField value(String val) {
            return new MovieTitleStringField("value", val);
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("MovieTitleString", 1);

    static {
        SCHEMA.addField("value", FieldType.STRING);
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}
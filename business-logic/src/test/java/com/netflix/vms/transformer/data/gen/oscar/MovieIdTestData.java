package com.netflix.vms.transformer.data.gen.oscar;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

public class MovieIdTestData extends HollowTestObjectRecord {

    MovieIdTestData(MovieIdField... fields){
        super(fields);
    }

    public static MovieIdTestData MovieId(MovieIdField... fields) {
        return new MovieIdTestData(fields);
    }

    public static MovieIdTestData MovieId(long val) {
        return MovieId(MovieIdField.value(val));
    }

    public MovieIdTestData update(MovieIdField... fields){
        super.addFields(fields);
        return this;
    }

    public long value() {
        Field f = super.getField("value");
        return f == null ? Long.MIN_VALUE : (Long)f.value;
    }

    public static class MovieIdField extends HollowTestObjectRecord.Field {

        private MovieIdField(String name, Object val) { super(name, val); }

        public static MovieIdField value(long val) {
            return new MovieIdField("value", val);
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("MovieId", 1);

    static {
        SCHEMA.addField("value", FieldType.LONG);
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}
package com.netflix.vms.transformer.data.gen.oscar;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

public class MovieTypeTestData extends HollowTestObjectRecord {

    MovieTypeTestData(MovieTypeField... fields){
        super(fields);
    }

    public static MovieTypeTestData MovieType(MovieTypeField... fields) {
        return new MovieTypeTestData(fields);
    }

    public MovieTypeTestData update(MovieTypeField... fields){
        super.addFields(fields);
        return this;
    }

    public Boolean streamingType() {
        Field f = super.getField("streamingType");
        return f == null ? null : (Boolean)f.value;
    }

    public Boolean viewable() {
        Field f = super.getField("viewable");
        return f == null ? null : (Boolean)f.value;
    }

    public Boolean merchable() {
        Field f = super.getField("merchable");
        return f == null ? null : (Boolean)f.value;
    }

    public String _name() {
        Field f = super.getField("_name");
        return f == null ? null : (String)f.value;
    }

    public static class MovieTypeField extends HollowTestObjectRecord.Field {

        private MovieTypeField(String name, Object val) { super(name, val); }

        public static MovieTypeField streamingType(boolean val) {
            return new MovieTypeField("streamingType", val);
        }

        public static MovieTypeField viewable(boolean val) {
            return new MovieTypeField("viewable", val);
        }

        public static MovieTypeField merchable(boolean val) {
            return new MovieTypeField("merchable", val);
        }

        public static MovieTypeField _name(String val) {
            return new MovieTypeField("_name", val);
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("MovieType", 4);

    static {
        SCHEMA.addField("streamingType", FieldType.BOOLEAN);
        SCHEMA.addField("viewable", FieldType.BOOLEAN);
        SCHEMA.addField("merchable", FieldType.BOOLEAN);
        SCHEMA.addField("_name", FieldType.STRING);
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}
package com.netflix.vms.transformer.data.gen.oscar;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

import com.netflix.vms.transformer.data.gen.oscar.StringTestData.StringField;

public class MovieSetContentLabelTestData extends HollowTestObjectRecord {

    MovieSetContentLabelTestData(MovieSetContentLabelField... fields){
        super(fields);
    }

    public static MovieSetContentLabelTestData MovieSetContentLabel(MovieSetContentLabelField... fields) {
        return new MovieSetContentLabelTestData(fields);
    }

    public MovieSetContentLabelTestData update(MovieSetContentLabelField... fields){
        super.addFields(fields);
        return this;
    }

    public StringTestData descriptionRef() {
        Field f = super.getField("description");
        return f == null ? null : (StringTestData)f.value;
    }

    public String description() {
        Field f = super.getField("description");
        if(f == null) return null;
        StringTestData ref = (StringTestData)f.value;
        return ref.value();
    }

    public int id() {
        Field f = super.getField("id");
        return f == null ? Integer.MIN_VALUE : (Integer)f.value;
    }

    public String _name() {
        Field f = super.getField("_name");
        return f == null ? null : (String)f.value;
    }

    public static class MovieSetContentLabelField extends HollowTestObjectRecord.Field {

        private MovieSetContentLabelField(String name, Object val) { super(name, val); }

        public static MovieSetContentLabelField description(StringTestData val) {
            return new MovieSetContentLabelField("description", val);
        }

        public static MovieSetContentLabelField description(StringField... fields) {
            return description(new StringTestData(fields));
        }

        public static MovieSetContentLabelField description(String val) {
            return description(StringField.value(val));
        }

        public static MovieSetContentLabelField id(int val) {
            return new MovieSetContentLabelField("id", val);
        }

        public static MovieSetContentLabelField _name(String val) {
            return new MovieSetContentLabelField("_name", val);
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("MovieSetContentLabel", 3);

    static {
        SCHEMA.addField("description", FieldType.REFERENCE, "String");
        SCHEMA.addField("id", FieldType.INT);
        SCHEMA.addField("_name", FieldType.STRING);
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}
package com.netflix.vms.transformer.data.gen.personVideo;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

public class PersonVideoAliasIdTestData extends HollowTestObjectRecord {

    PersonVideoAliasIdTestData(PersonVideoAliasIdField... fields){
        super(fields);
    }

    public static PersonVideoAliasIdTestData PersonVideoAliasId(PersonVideoAliasIdField... fields) {
        return new PersonVideoAliasIdTestData(fields);
    }

    public static PersonVideoAliasIdTestData PersonVideoAliasId(int val) {
        return PersonVideoAliasId(PersonVideoAliasIdField.value(val));
    }

    public PersonVideoAliasIdTestData update(PersonVideoAliasIdField... fields){
        super.addFields(fields);
        return this;
    }

    public int value() {
        Field f = super.getField("value");
        return f == null ? Integer.MIN_VALUE : (Integer)f.value;
    }

    public static class PersonVideoAliasIdField extends Field {

        private PersonVideoAliasIdField(String name, Object val) { super(name, val); }

        public static PersonVideoAliasIdField value(int val) {
            return new PersonVideoAliasIdField("value", val);
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("PersonVideoAliasId", 1);

    static {
        SCHEMA.addField("value", FieldType.INT);
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}
package com.netflix.vms.transformer.data.gen.oscar;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

public class PersonIdTestData extends HollowTestObjectRecord {

    PersonIdTestData(PersonIdField... fields){
        super(fields);
    }

    public static PersonIdTestData PersonId(PersonIdField... fields) {
        return new PersonIdTestData(fields);
    }

    public static PersonIdTestData PersonId(long val) {
        return PersonId(PersonIdField.value(val));
    }

    public PersonIdTestData update(PersonIdField... fields){
        super.addFields(fields);
        return this;
    }

    public long value() {
        Field f = super.getField("value");
        return f == null ? Long.MIN_VALUE : (Long)f.value;
    }

    public static class PersonIdField extends HollowTestObjectRecord.Field {

        private PersonIdField(String name, Object val) { super(name, val); }

        public static PersonIdField value(long val) {
            return new PersonIdField("value", val);
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("PersonId", 1);

    static {
        SCHEMA.addField("value", FieldType.LONG);
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}
package com.netflix.vms.transformer.data.gen.oscar;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

public class PersonNameTestData extends HollowTestObjectRecord {

    PersonNameTestData(PersonNameField... fields){
        super(fields);
    }

    public static PersonNameTestData PersonName(PersonNameField... fields) {
        return new PersonNameTestData(fields);
    }

    public static PersonNameTestData PersonName(String val) {
        return PersonName(PersonNameField.value(val));
    }

    public PersonNameTestData update(PersonNameField... fields){
        super.addFields(fields);
        return this;
    }

    public String value() {
        Field f = super.getField("value");
        return f == null ? null : (String)f.value;
    }

    public static class PersonNameField extends HollowTestObjectRecord.Field {

        private PersonNameField(String name, Object val) { super(name, val); }

        public static PersonNameField value(String val) {
            return new PersonNameField("value", val);
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("PersonName", 1);

    static {
        SCHEMA.addField("value", FieldType.STRING);
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}
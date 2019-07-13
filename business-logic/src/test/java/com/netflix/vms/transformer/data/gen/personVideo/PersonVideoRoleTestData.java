package com.netflix.vms.transformer.data.gen.personVideo;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

public class PersonVideoRoleTestData extends HollowTestObjectRecord {

    PersonVideoRoleTestData(PersonVideoRoleField... fields){
        super(fields);
    }

    public static PersonVideoRoleTestData PersonVideoRole(PersonVideoRoleField... fields) {
        return new PersonVideoRoleTestData(fields);
    }

    public PersonVideoRoleTestData update(PersonVideoRoleField... fields){
        super.addFields(fields);
        return this;
    }

    public int sequenceNumber() {
        Field f = super.getField("sequenceNumber");
        return f == null ? Integer.MIN_VALUE : (Integer)f.value;
    }

    public int roleTypeId() {
        Field f = super.getField("roleTypeId");
        return f == null ? Integer.MIN_VALUE : (Integer)f.value;
    }

    public long videoId() {
        Field f = super.getField("videoId");
        return f == null ? Long.MIN_VALUE : (Long)f.value;
    }

    public static class PersonVideoRoleField extends Field {

        private PersonVideoRoleField(String name, Object val) { super(name, val); }

        public static PersonVideoRoleField sequenceNumber(int val) {
            return new PersonVideoRoleField("sequenceNumber", val);
        }

        public static PersonVideoRoleField roleTypeId(int val) {
            return new PersonVideoRoleField("roleTypeId", val);
        }

        public static PersonVideoRoleField videoId(long val) {
            return new PersonVideoRoleField("videoId", val);
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("PersonVideoRole", 3);

    static {
        SCHEMA.addField("sequenceNumber", FieldType.INT);
        SCHEMA.addField("roleTypeId", FieldType.INT);
        SCHEMA.addField("videoId", FieldType.LONG);
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}
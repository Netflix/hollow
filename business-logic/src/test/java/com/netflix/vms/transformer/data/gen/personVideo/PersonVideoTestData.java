package com.netflix.vms.transformer.data.gen.personVideo;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

public class PersonVideoTestData extends HollowTestObjectRecord {

    PersonVideoTestData(PersonVideoField... fields){
        super(fields);
    }

    public static PersonVideoTestData PersonVideo(PersonVideoField... fields) {
        return new PersonVideoTestData(fields);
    }

    public PersonVideoTestData update(PersonVideoField... fields){
        super.addFields(fields);
        return this;
    }

    public PersonVideoAliasIdsListTestData aliasIds() {
        Field f = super.getField("aliasIds");
        return f == null ? null : (PersonVideoAliasIdsListTestData)f.value;
    }

    public PersonVideoRolesListTestData roles() {
        Field f = super.getField("roles");
        return f == null ? null : (PersonVideoRolesListTestData)f.value;
    }

    public long personId() {
        Field f = super.getField("personId");
        return f == null ? Long.MIN_VALUE : (Long)f.value;
    }

    public static class PersonVideoField extends Field {

        private PersonVideoField(String name, Object val) { super(name, val); }

        public static PersonVideoField aliasIds(PersonVideoAliasIdsListTestData val) {
            return new PersonVideoField("aliasIds", val);
        }

        public static PersonVideoField aliasIds(PersonVideoAliasIdTestData... elements) {
            return aliasIds(new PersonVideoAliasIdsListTestData(elements));
        }

        public static PersonVideoField roles(PersonVideoRolesListTestData val) {
            return new PersonVideoField("roles", val);
        }

        public static PersonVideoField roles(PersonVideoRoleTestData... elements) {
            return roles(new PersonVideoRolesListTestData(elements));
        }

        public static PersonVideoField personId(long val) {
            return new PersonVideoField("personId", val);
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("PersonVideo", 3, new PrimaryKey("PersonVideo", "personId"));

    static {
        SCHEMA.addField("aliasIds", FieldType.REFERENCE, "PersonVideoAliasIdsList");
        SCHEMA.addField("roles", FieldType.REFERENCE, "PersonVideoRolesList");
        SCHEMA.addField("personId", FieldType.LONG);
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}
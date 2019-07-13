package com.netflix.vms.transformer.data.gen.showCountryLabel;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

public class ShowMemberTypeTestData extends HollowTestObjectRecord {

    ShowMemberTypeTestData(ShowMemberTypeField... fields){
        super(fields);
    }

    public static ShowMemberTypeTestData ShowMemberType(ShowMemberTypeField... fields) {
        return new ShowMemberTypeTestData(fields);
    }

    public ShowMemberTypeTestData update(ShowMemberTypeField... fields){
        super.addFields(fields);
        return this;
    }

    public ISOCountryListTestData countryCodes() {
        Field f = super.getField("countryCodes");
        return f == null ? null : (ISOCountryListTestData)f.value;
    }

    public long sequenceLabelId() {
        Field f = super.getField("sequenceLabelId");
        return f == null ? Long.MIN_VALUE : (Long)f.value;
    }

    public static class ShowMemberTypeField extends Field {

        private ShowMemberTypeField(String name, Object val) { super(name, val); }

        public static ShowMemberTypeField countryCodes(ISOCountryListTestData val) {
            return new ShowMemberTypeField("countryCodes", val);
        }

        public static ShowMemberTypeField countryCodes(ISOCountryTestData... elements) {
            return countryCodes(new ISOCountryListTestData(elements));
        }

        public static ShowMemberTypeField sequenceLabelId(long val) {
            return new ShowMemberTypeField("sequenceLabelId", val);
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("ShowMemberType", 2);

    static {
        SCHEMA.addField("countryCodes", FieldType.REFERENCE, "ISOCountryList");
        SCHEMA.addField("sequenceLabelId", FieldType.LONG);
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}
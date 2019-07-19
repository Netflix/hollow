package com.netflix.vms.transformer.data.gen.oscar;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

public class ShowCountryLabelTestData extends HollowTestObjectRecord {

    ShowCountryLabelTestData(ShowCountryLabelField... fields){
        super(fields);
    }

    public static ShowCountryLabelTestData ShowCountryLabel(ShowCountryLabelField... fields) {
        return new ShowCountryLabelTestData(fields);
    }

    public ShowCountryLabelTestData update(ShowCountryLabelField... fields){
        super.addFields(fields);
        return this;
    }

    public long videoId() {
        Field f = super.getField("videoId");
        return f == null ? Long.MIN_VALUE : (Long)f.value;
    }

    public ShowMemberTypeListTestData showMemberTypes() {
        Field f = super.getField("showMemberTypes");
        return f == null ? null : (ShowMemberTypeListTestData)f.value;
    }

    public static class ShowCountryLabelField extends HollowTestObjectRecord.Field {

        private ShowCountryLabelField(String name, Object val) { super(name, val); }

        public static ShowCountryLabelField videoId(long val) {
            return new ShowCountryLabelField("videoId", val);
        }

        public static ShowCountryLabelField showMemberTypes(ShowMemberTypeListTestData val) {
            return new ShowCountryLabelField("showMemberTypes", val);
        }

        public static ShowCountryLabelField showMemberTypes(ShowMemberTypeTestData... elements) {
            return showMemberTypes(new ShowMemberTypeListTestData(elements));
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("ShowCountryLabel", 2, new PrimaryKey("ShowCountryLabel", "videoId"));

    static {
        SCHEMA.addField("videoId", FieldType.LONG);
        SCHEMA.addField("showMemberTypes", FieldType.REFERENCE, "ShowMemberTypeList");
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}
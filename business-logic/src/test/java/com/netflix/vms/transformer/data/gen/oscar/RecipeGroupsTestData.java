package com.netflix.vms.transformer.data.gen.oscar;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

public class RecipeGroupsTestData extends HollowTestObjectRecord {

    RecipeGroupsTestData(RecipeGroupsField... fields){
        super(fields);
    }

    public static RecipeGroupsTestData RecipeGroups(RecipeGroupsField... fields) {
        return new RecipeGroupsTestData(fields);
    }

    public static RecipeGroupsTestData RecipeGroups(String val) {
        return RecipeGroups(RecipeGroupsField.value(val));
    }

    public RecipeGroupsTestData update(RecipeGroupsField... fields){
        super.addFields(fields);
        return this;
    }

    public String value() {
        Field f = super.getField("value");
        return f == null ? null : (String)f.value;
    }

    public static class RecipeGroupsField extends HollowTestObjectRecord.Field {

        private RecipeGroupsField(String name, Object val) { super(name, val); }

        public static RecipeGroupsField value(String val) {
            return new RecipeGroupsField("value", val);
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("RecipeGroups", 1);

    static {
        SCHEMA.addField("value", FieldType.STRING);
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}
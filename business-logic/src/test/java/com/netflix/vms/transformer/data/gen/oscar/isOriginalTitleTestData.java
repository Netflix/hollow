package com.netflix.vms.transformer.data.gen.oscar;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

public class isOriginalTitleTestData extends HollowTestObjectRecord {

    isOriginalTitleTestData(isOriginalTitleField... fields){
        super(fields);
    }

    public static isOriginalTitleTestData isOriginalTitle(isOriginalTitleField... fields) {
        return new isOriginalTitleTestData(fields);
    }

    public static isOriginalTitleTestData isOriginalTitle(boolean val) {
        return isOriginalTitle(isOriginalTitleField.value(val));
    }

    public isOriginalTitleTestData update(isOriginalTitleField... fields){
        super.addFields(fields);
        return this;
    }

    public Boolean value() {
        Field f = super.getField("value");
        return f == null ? null : (Boolean)f.value;
    }

    public static class isOriginalTitleField extends HollowTestObjectRecord.Field {

        private isOriginalTitleField(String name, Object val) { super(name, val); }

        public static isOriginalTitleField value(boolean val) {
            return new isOriginalTitleField("value", val);
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("isOriginalTitle", 1);

    static {
        SCHEMA.addField("value", FieldType.BOOLEAN);
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}
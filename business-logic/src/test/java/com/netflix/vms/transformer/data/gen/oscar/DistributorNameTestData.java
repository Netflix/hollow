package com.netflix.vms.transformer.data.gen.oscar;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

public class DistributorNameTestData extends HollowTestObjectRecord {

    DistributorNameTestData(DistributorNameField... fields){
        super(fields);
    }

    public static DistributorNameTestData DistributorName(DistributorNameField... fields) {
        return new DistributorNameTestData(fields);
    }

    public static DistributorNameTestData DistributorName(String val) {
        return DistributorName(DistributorNameField.value(val));
    }

    public DistributorNameTestData update(DistributorNameField... fields){
        super.addFields(fields);
        return this;
    }

    public String value() {
        Field f = super.getField("value");
        return f == null ? null : (String)f.value;
    }

    public static class DistributorNameField extends HollowTestObjectRecord.Field {

        private DistributorNameField(String name, Object val) { super(name, val); }

        public static DistributorNameField value(String val) {
            return new DistributorNameField("value", val);
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("DistributorName", 1);

    static {
        SCHEMA.addField("value", FieldType.STRING);
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}
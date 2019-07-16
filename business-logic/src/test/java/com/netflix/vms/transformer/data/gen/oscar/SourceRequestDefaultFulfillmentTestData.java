package com.netflix.vms.transformer.data.gen.oscar;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

public class SourceRequestDefaultFulfillmentTestData extends HollowTestObjectRecord {

    SourceRequestDefaultFulfillmentTestData(SourceRequestDefaultFulfillmentField... fields){
        super(fields);
    }

    public static SourceRequestDefaultFulfillmentTestData SourceRequestDefaultFulfillment(SourceRequestDefaultFulfillmentField... fields) {
        return new SourceRequestDefaultFulfillmentTestData(fields);
    }

    public static SourceRequestDefaultFulfillmentTestData SourceRequestDefaultFulfillment(String val) {
        return SourceRequestDefaultFulfillment(SourceRequestDefaultFulfillmentField._name(val));
    }

    public SourceRequestDefaultFulfillmentTestData update(SourceRequestDefaultFulfillmentField... fields){
        super.addFields(fields);
        return this;
    }

    public String _name() {
        Field f = super.getField("_name");
        return f == null ? null : (String)f.value;
    }

    public static class SourceRequestDefaultFulfillmentField extends HollowTestObjectRecord.Field {

        private SourceRequestDefaultFulfillmentField(String name, Object val) { super(name, val); }

        public static SourceRequestDefaultFulfillmentField _name(String val) {
            return new SourceRequestDefaultFulfillmentField("_name", val);
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("SourceRequestDefaultFulfillment", 1);

    static {
        SCHEMA.addField("_name", FieldType.STRING);
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}
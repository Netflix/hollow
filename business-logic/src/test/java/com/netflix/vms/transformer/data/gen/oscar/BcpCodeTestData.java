package com.netflix.vms.transformer.data.gen.oscar;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

public class BcpCodeTestData extends HollowTestObjectRecord {

    BcpCodeTestData(BcpCodeField... fields){
        super(fields);
    }

    public static BcpCodeTestData BcpCode(BcpCodeField... fields) {
        return new BcpCodeTestData(fields);
    }

    public static BcpCodeTestData BcpCode(String val) {
        return BcpCode(BcpCodeField.value(val));
    }

    public BcpCodeTestData update(BcpCodeField... fields){
        super.addFields(fields);
        return this;
    }

    public String value() {
        Field f = super.getField("value");
        return f == null ? null : (String)f.value;
    }

    public static class BcpCodeField extends HollowTestObjectRecord.Field {

        private BcpCodeField(String name, Object val) { super(name, val); }

        public static BcpCodeField value(String val) {
            return new BcpCodeField("value", val);
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("BcpCode", 1);

    static {
        SCHEMA.addField("value", FieldType.STRING);
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}
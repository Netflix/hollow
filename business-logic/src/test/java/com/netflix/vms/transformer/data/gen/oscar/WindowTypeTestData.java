package com.netflix.vms.transformer.data.gen.oscar;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

public class WindowTypeTestData extends HollowTestObjectRecord {

    WindowTypeTestData(WindowTypeField... fields){
        super(fields);
    }

    public static WindowTypeTestData WindowType(WindowTypeField... fields) {
        return new WindowTypeTestData(fields);
    }

    public static WindowTypeTestData WindowType(String val) {
        return WindowType(WindowTypeField._name(val));
    }

    public WindowTypeTestData update(WindowTypeField... fields){
        super.addFields(fields);
        return this;
    }

    public String _name() {
        Field f = super.getField("_name");
        return f == null ? null : (String)f.value;
    }

    public static class WindowTypeField extends HollowTestObjectRecord.Field {

        private WindowTypeField(String name, Object val) { super(name, val); }

        public static WindowTypeField _name(String val) {
            return new WindowTypeField("_name", val);
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("WindowType", 1);

    static {
        SCHEMA.addField("_name", FieldType.STRING);
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}